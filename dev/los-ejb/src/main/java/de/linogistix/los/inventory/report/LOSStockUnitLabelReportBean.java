/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.globals.DocumentTypes;
import org.mywms.service.ConstraintViolatedException;

import de.linogistix.los.common.businessservice.LOSJasperReportGenerator;
import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.inventory.model.StockUnitLabel;
import de.linogistix.los.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.service.StockUnitLabelService;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;

/**
 *
 * @author trautm
 */
@Stateless
public class LOSStockUnitLabelReportBean implements LOSStockUnitLabelReport {

    private static final Logger log = Logger.getLogger(LOSStockUnitLabelReportBean.class);
    @EJB
	private LOSJasperReportGenerator reportGenerator;
    @EJB
    private StockUnitLabelService labelService;
	@EJB
	private EntityGenerator entityGenerator;

    @PersistenceContext(unitName = "myWMS")
	private EntityManager manager;
    
    
    
	/**
	 * Generation of a goods receipt stock unit label
	 * The object and report is generated. 
	 * It is not persisted.
	 * 
	 * @param unitLoad
	 * @return
	 * @throws FacadeException
	 */
	public StockUnitLabel generateStockUnitLabel(UnitLoad unitLoad) throws FacadeException {
		String logStr = "generateStockUnitLabel ";
		StockUnitLabel label = null;
		
		log.info(logStr+"Generate stock unit label for unitLoad="+unitLoad.getLabelId());
		
		label = entityGenerator.generateEntity( StockUnitLabel.class );
		label.setName( "LOS-"+unitLoad.getLabelId() );
		label.setClient( unitLoad.getClient() );
		label.setClientRef( unitLoad.getClient().getNumber() );
		label.setLabelID( unitLoad.getLabelId() );
		label.setType(DocumentTypes.APPLICATION_PDF.toString());

		SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy");
		label.setDateRef( sd.format(new Date()) );
		

		
		
		List<LOSStockUnitReportTO> valueMap = new ArrayList<LOSStockUnitReportTO>();

		for( StockUnit stock : unitLoad.getStockUnitList() ) {
			LOSStockUnitReportTO labelPos;
			labelPos = new LOSStockUnitReportTO();
			labelPos.itemNumber = stock.getItemData().getNumber();
			labelPos.itemName = stock.getItemData().getName();
			labelPos.itemUnit = stock.getItemData().getItemUnit().getName();
			labelPos.itemScale = stock.getItemData().getScale();
			labelPos.lotName = stock.getLot() == null ? "" : stock.getLot().getName();
			labelPos.amount = stock.getAmount();
			labelPos.serialNumber = stock.getSerialNumber();
			
			valueMap.add(labelPos);

			
			label.setAmount(stock.getAmount());
			label.setItemdataRef(stock.getItemData().getNumber());
			label.setItemNameRef(stock.getItemData().getName());
			label.setItemUnit( stock.getItemData().getItemUnit().getName() );
			label.setLotRef(stock.getLot() == null ? "" : stock.getLot().getName());
			label.setScale(stock.getItemData().getScale());
			
		}

		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("labelId", unitLoad.getLabelId() );
		parameters.put("formattedDate", sd.format(new Date()) );
		parameters.put("printDate", new Date() );
		parameters.put("clientNumber", unitLoad.getClient().getNumber() );
		parameters.put("clientName", unitLoad.getClient().getName() );
		parameters.put("clientCode", unitLoad.getClient().getCode() );

		byte[] bytes = reportGenerator.createPdf(unitLoad.getClient(), "StockUnitLabel", InventoryBundleResolver.class, valueMap, parameters);
		label.setDocument(bytes);


		return label;

	}
	
	/**
	 * Persist a stock unit label
	 * If it is already existing, it will be replaced.
	 * 
	 * @param label
	 * @return
	 * @throws FacadeException
	 */
	public StockUnitLabel storeStockUnitLabel(StockUnitLabel label) throws FacadeException {
		String logStr = "storeStockUnitLabel ";

		StockUnitLabel labelOld = labelService.getByLabelId(label.getLabelID());
		if( labelOld != null ) {
			try {
				log.debug(logStr+"Remove old label. name="+labelOld.getName());
				labelService.delete(labelOld);
				manager.flush();
			} catch (ConstraintViolatedException e) {
				log.error(logStr+"Cannot remove old receipt! Cannot build new!");
				return null;
			}
		}
		
		manager.persist(label);
		
		return label;

	}

}
