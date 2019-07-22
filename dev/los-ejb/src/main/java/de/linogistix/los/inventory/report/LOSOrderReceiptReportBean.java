/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
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
import de.linogistix.los.inventory.model.OrderReceipt;
import de.linogistix.los.inventory.model.OrderReceiptPosition;
import de.linogistix.los.inventory.model.OrderType;
import de.linogistix.los.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.service.OrderReceiptService;
import de.linogistix.los.util.StringTools;
import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.picking.PickingUnitLoad;
import de.wms2.mywms.picking.PickingUnitLoadEntityService;


/**
 * @author krane
 *
 */
@Stateless
public class LOSOrderReceiptReportBean implements LOSOrderReceiptReport {

	private static final Logger log = Logger.getLogger(LOSOrderReceiptReportBean.class);
	
	@EJB
	private LOSJasperReportGenerator reportGenerator;
	@EJB
	private EntityGenerator entityGenerator;
	@EJB
	private OrderReceiptService receiptService;
	
    @PersistenceContext(unitName = "myWMS")
	private EntityManager manager;
    
	@Inject
	private PickingUnitLoadEntityService pickingUnitLoadService;
    
    
	public OrderReceipt generateOrderReceipt(DeliveryOrder order) throws FacadeException {
		String logStr = "generateOrderReceipt ";
		OrderReceipt receipt = null;
		
		log.info(logStr+"Generate receipt for order="+order.getOrderNumber());
		receipt = entityGenerator.generateEntity( OrderReceipt.class );
		receipt.setName("LOS-"+order.getOrderNumber());
		receipt.setClient(order.getClient());
		receipt.setOrderNumber(order.getOrderNumber());
		receipt.setDate(new Date());
		receipt.setType(DocumentTypes.APPLICATION_PDF.toString());
		receipt.setDestination(order.getDestination()==null?null:order.getDestination().getName());
		receipt.setOrderReference(order.getExternalNumber());
		receipt.setOrderType(OrderType.INTERNAL);
		receipt.setUser("");
		receipt.setPositions(new ArrayList<OrderReceiptPosition>());

		List<PickingUnitLoad> pulList = pickingUnitLoadService.getByDeliveryOrder(order);

		Map<String, LOSStockUnitReportTO> valueMap = new HashMap<String, LOSStockUnitReportTO>();

		for( PickingUnitLoad pul : pulList ) {
			UnitLoad unitLoad = pul.getUnitLoad();
			for( StockUnit stock : unitLoad.getStockUnitList() ) {
				String key = stock.getItemData().getNumber();
				if( stock.getLot() != null ) {
					key+="-LOT-"+stock.getLot().getName();
				}
				LOSStockUnitReportTO receiptPos = valueMap.get(key);
				if( receiptPos == null ) {
					receiptPos = new LOSStockUnitReportTO();
					receiptPos.itemNumber = stock.getItemData().getNumber();
					receiptPos.itemName = stock.getItemData().getName();
					receiptPos.itemUnit = stock.getItemData().getItemUnit().getName();
					receiptPos.itemScale = stock.getItemData().getScale();
					receiptPos.lotName = stock.getLot() == null ? "" : stock.getLot().getName();
					receiptPos.amount = stock.getAmount();
					
					valueMap.put(key, receiptPos);
				}
				else {
					receiptPos.amount = receiptPos.amount.add(stock.getAmount());
				}
				
				if( !StringTools.isEmpty(stock.getSerialNumber()) ) {
					receiptPos = new LOSStockUnitReportTO();
					key = key + "-SERIAL-" + stock.getSerialNumber();
					receiptPos.itemNumber = stock.getItemData().getNumber();
					receiptPos.serialNumber = stock.getSerialNumber();
					valueMap.put(key, receiptPos);
				}
			}
		}			

		List<LOSStockUnitReportTO> valueList = new ArrayList<LOSStockUnitReportTO>();
		valueList.addAll(valueMap.values());
		Collections.sort(valueList, new ReceiptPositionComparator());

		List<LOSStockUnitReportTO> valueList2 = new ArrayList<LOSStockUnitReportTO>();
		String itemNumber = null;
		int i = 1;
		
		for( LOSStockUnitReportTO value : valueList ) {

			
			OrderReceiptPosition receiptPos = entityGenerator.generateEntity(OrderReceiptPosition.class);
			receiptPos.setAmount(value.amount);
			receiptPos.setArticleDescr(value.itemName);
			receiptPos.setArticleRef(value.itemNumber);
			receiptPos.setArticleScale(value.itemScale);
			receiptPos.setClient(order.getClient());
			receiptPos.setLotRef(value.lotName);
			int p = -1; 
			try {
				p = Integer.valueOf(value.pos);
			}
			catch( Throwable t ) {}
			receiptPos.setPos( p );
			receiptPos.setReceipt(receipt);
			if( receipt.getPositions() == null ) {
				receipt.setPositions(new ArrayList<OrderReceiptPosition>());
			}
			receipt.getPositions().add(receiptPos);
			
			
			
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
		parameters.put("formattedOrderNumber", StringTools.isEmpty(order.getExternalNumber()) ? order.getOrderNumber() : order.getExternalNumber() );
		SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy");
		parameters.put("formattedDate", sd.format(new Date()) );
		parameters.put("printDate", new Date() );
		parameters.put("orderDate", order.getDeliveryDate() );
		parameters.put("orderNumber", order.getOrderNumber() );
		parameters.put("externalOrderNumber", order.getExternalNumber() );
		parameters.put("clientNumber", order.getClient().getNumber() );
		parameters.put("clientName", order.getClient().getName() );
		parameters.put("clientCode", order.getClient().getCode() );
		parameters.put("targetLocationName", order.getDestination() == null ? "" : order.getDestination().getName() );
		parameters.put("orderStrategyName", order.getOrderStrategy() == null ? "" : order.getOrderStrategy().getName() );
		parameters.put("prio", order.getPrio() );
		
		byte[] bytes = reportGenerator.createPdf(order.getClient(), "OrderReceipt", InventoryBundleResolver.class, valueList2, parameters);
		receipt.setDocument(bytes);


		return receipt;
	}

	public OrderReceipt storeOrderReceipt(OrderReceipt receipt) throws FacadeException {
		String logStr = "storeOrderReceipt ";

		OrderReceipt receiptOld = receiptService.getByOrderNumber(receipt.getOrderNumber());
		if( receiptOld != null ) {
			log.debug(logStr+"Remove old receipt. name="+receiptOld.getName());
			for( OrderReceiptPosition pos : receiptOld.getPositions() ) {
				manager.remove(pos);
			}
			manager.remove(receiptOld);
			manager.flush();
		}
		
		manager.persist(receipt);
		for( OrderReceiptPosition pos : receipt.getPositions() ) {
			manager.persist(pos);
		}
		
		return receipt;
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
