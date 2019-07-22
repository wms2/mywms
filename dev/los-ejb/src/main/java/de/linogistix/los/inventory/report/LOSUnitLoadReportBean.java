/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.report;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.globals.DocumentTypes;

import de.linogistix.los.common.businessservice.LOSJasperReportGenerator;
import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.inventory.pick.model.PickReceipt;
import de.linogistix.los.inventory.pick.model.PickReceiptPosition;
import de.linogistix.los.inventory.pick.service.PickReceiptService;
import de.linogistix.los.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.service.LOSCustomerOrderService;
import de.linogistix.los.util.StringTools;
import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.picking.PickingOrder;
import de.wms2.mywms.picking.PickingUnitLoad;
import de.wms2.mywms.picking.PickingUnitLoadEntityService;


/**
 * @author krane
 *
 */
@Stateless
public class LOSUnitLoadReportBean implements LOSUnitLoadReport {

	private static final Logger log = Logger.getLogger(LOSUnitLoadReportBean.class);
	
	@EJB
	private LOSJasperReportGenerator reportGenerator;
	@EJB
	private EntityGenerator entityGenerator;
	@EJB
	private PickReceiptService labelService;
	@EJB
	private LOSCustomerOrderService customerOrderService;

    @PersistenceContext(unitName = "myWMS")
	private EntityManager manager;
    
	@Inject
	private PickingUnitLoadEntityService pickingUnitLoadService;
    
    
	public PickReceipt generateUnitLoadReport(UnitLoad unitLoad) throws FacadeException {
		String logStr = "generateUnitLoadReport ";
		PickReceipt label = null;
		
		log.info(logStr+"Generate report for unitLoad="+unitLoad.getLabelId());
		
		PickingUnitLoad pul = pickingUnitLoadService.getByLabel(unitLoad.getLabelId());
		
		label = entityGenerator.generateEntity( PickReceipt.class );
		label.setName( "LOS-"+unitLoad.getLabelId() );
		label.setClient( unitLoad.getClient() );
		label.setLabelID( unitLoad.getLabelId() );
		label.setDate(new Date());
		label.setType(DocumentTypes.APPLICATION_PDF.toString());

		label.setPositions( new ArrayList<PickReceiptPosition>() );
		
		
		DeliveryOrder deliveryOrder = null;
		PickingOrder pickingOrder = null;
		if( pul != null ) {
			deliveryOrder = pul.getDeliveryOrder();
			if( deliveryOrder != null ) {
				label.setOrderNumber( deliveryOrder.getOrderNumber() );
			}
			pickingOrder = pul.getPickingOrder();
			if( pickingOrder != null ) {
				label.setPickNumber( pul.getPickingOrder().getOrderNumber() );
			}
		}
		
		Map<String, LOSStockUnitReportTO> valueMap = new HashMap<String, LOSStockUnitReportTO>();

		for( StockUnit stock : unitLoad.getStockUnitList() ) {
			
			
			PickReceiptPosition pickReceiptPos = entityGenerator.generateEntity( PickReceiptPosition.class );
			pickReceiptPos.setAmount(stock.getAmount());
			pickReceiptPos.setArticleDescr(stock.getItemData().getName());
			pickReceiptPos.setArticleRef(stock.getItemData().getNumber());
			pickReceiptPos.setLotRef( stock.getLot() == null ? "" : stock.getLot().getName() );
			pickReceiptPos.setReceipt(label);
			label.getPositions().add(pickReceiptPos);
			
			
			String key = stock.getItemData().getNumber();
			if( stock.getLot() != null ) {
				key+="-LOT-"+stock.getLot().getName();
			}
			LOSStockUnitReportTO labelPos = valueMap.get(key);
			if( labelPos == null ) {
				labelPos = new LOSStockUnitReportTO();
				labelPos.itemNumber = stock.getItemData().getNumber();
				labelPos.itemName = stock.getItemData().getName();
				labelPos.itemUnit = stock.getItemData().getItemUnit().getName();
				labelPos.itemScale = stock.getItemData().getScale();
				labelPos.lotName = stock.getLot() == null ? "" : stock.getLot().getName();
				labelPos.amount = stock.getAmount();
//				labelPos.serialNumber = stock.getSerialNumber();
				labelPos.serialNumber = "";
				
				valueMap.put(key, labelPos);
			}
			else {
				labelPos.amount = labelPos.amount.add(stock.getAmount());
			}
			
			if( !StringTools.isEmpty(stock.getSerialNumber()) ) {
				labelPos = new LOSStockUnitReportTO();
				key = key + "-SERIAL-" + stock.getSerialNumber();
				labelPos.itemNumber = stock.getItemData().getNumber();
				labelPos.serialNumber = stock.getSerialNumber();
				valueMap.put(key, labelPos);
			}
		}

		List<LOSStockUnitReportTO> valueList = new ArrayList<LOSStockUnitReportTO>();
		valueList.addAll(valueMap.values());
		Collections.sort(valueList, new ReceiptPositionComparator());

		List<LOSStockUnitReportTO> valueList2 = new ArrayList<LOSStockUnitReportTO>();
		String itemNumber = null;
		int i = 1;
		
		
		for( LOSStockUnitReportTO value : valueList ) {
			if( itemNumber != null && !itemNumber.equals(value.itemNumber) ) {
				valueList2.add( new LOSStockUnitReportTO() );
			}
			itemNumber = value.itemNumber;

			if( StringTools.isEmpty(value.serialNumber) ) {
				value.pos = ""+i;
				valueList2.add( value );
				i++;
			}
			else {
				value.pos = "";
				value.itemNumber = "";
				value.itemName = "  SN: "+value.serialNumber;
				value.amount = BigDecimal.ZERO;
				value.itemUnit = "";
				valueList2.add( value );
			}

		}
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("formattedOrderNumber", deliveryOrder == null ? "" : StringTools.isEmpty(deliveryOrder.getExternalNumber()) ? deliveryOrder.getOrderNumber() : deliveryOrder.getExternalNumber() );
		SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy");
		parameters.put("formattedDate", sd.format(new Date()) );
		parameters.put("labelId", label.getLabelID() );
		parameters.put("pickingOrderNumber", label.getPickNumber());
		parameters.put("printDate", new Date() );
		parameters.put("orderDate", deliveryOrder == null ? null : deliveryOrder.getDeliveryDate() );
		parameters.put("orderNumber", deliveryOrder == null ? "" : deliveryOrder.getOrderNumber() );
		parameters.put("externalOrderNumber", deliveryOrder == null ? "" : deliveryOrder.getExternalNumber() );
		parameters.put("clientNumber", unitLoad.getClient().getNumber() );
		parameters.put("clientName", unitLoad.getClient().getName() );
		parameters.put("clientCode", unitLoad.getClient().getCode() );
		parameters.put("targetLocationName", deliveryOrder == null ? "" : deliveryOrder.getDestination() == null ? "" : deliveryOrder.getDestination().getName() );
		parameters.put("orderStrategyName", deliveryOrder == null ? "" : deliveryOrder.getOrderStrategy() == null ? "" : deliveryOrder.getOrderStrategy().getName() );
		parameters.put("prio", deliveryOrder == null ? 0 : deliveryOrder.getPrio() );
		
		byte[] bytes = reportGenerator.createPdf(unitLoad.getClient(), "UnitLoadLabel", InventoryBundleResolver.class, valueList2, parameters);
		label.setDocument(bytes);


		return label;
	}

	public PickReceipt storeUnitLoadReport(PickReceipt label) throws FacadeException {
		String logStr = "storeUnitLoadReport ";

		PickReceipt labelOld = labelService.getByLabelId(label.getLabelID());
		if( labelOld != null ) {
			log.debug(logStr+"Remove old label. name="+labelOld.getName());
			for( PickReceiptPosition pos : labelOld.getPositions() ) {
				manager.remove(pos);
			}
			manager.remove(labelOld);
			manager.flush();
		}
		
		manager.persist(label);
		for( PickReceiptPosition pos : label.getPositions() ) {
			manager.persist(pos);
		}
		
		return label;
	}

	

	
	class ReceiptPositionComparator implements Comparator<LOSStockUnitReportTO>{
		public int compare(LOSStockUnitReportTO o1, LOSStockUnitReportTO o2) {
			int x = o1.itemNumber.compareTo(o2.itemNumber);
			if( x != 0 ) {
				return x;
			}
			
			if( o1.serialNumber != null && o2.serialNumber != null ) {
				x = o1.serialNumber.compareTo(o2.serialNumber);
				if( x != 0 ) {
					return x;
				}
			}
			
			return o1.amount.compareTo(o2.amount); 
		}
	}
}	
