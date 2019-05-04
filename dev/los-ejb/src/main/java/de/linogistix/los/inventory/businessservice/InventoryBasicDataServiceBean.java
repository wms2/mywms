/*
 * Copyright (c) 2012-2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.ItemUnit;
import org.mywms.service.ClientService;
import org.mywms.service.ItemUnitService;

import de.linogistix.los.inventory.model.LOSInventoryPropertyKey;
import de.linogistix.los.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.inventory.service.LOSOrderStrategyService;
import de.linogistix.los.inventory.service.LOSStorageStrategyService;
import de.linogistix.los.model.LOSCommonPropertyKey;
import de.linogistix.los.util.entityservice.LOSSystemPropertyService;


/**
 * @author krane
 *
 */
@Stateless
public class InventoryBasicDataServiceBean implements InventoryBasicDataService {

	private static final Logger log = Logger.getLogger(InventoryBasicDataServiceBean.class);

	@EJB
	private ClientService clientService;
	@EJB
	private LOSOrderStrategyService orderStrategyService;
	@EJB
	private LOSStorageStrategyService storageStrategyService;
	@EJB
	private LOSSystemPropertyService propertyService;
	@EJB
	private ItemUnitService unitService;
	@EJB
	private InventoryGeneratorService genService;
	public void createBasicData(Locale locale) throws FacadeException {

		log.info("Create Inventory Basic Data...");

		Client sys = clientService.getSystemClient();

		log.info("Create Sequences...");
		genService.generateGoodsReceiptNumber(sys);
		genService.generateAdviceNumber(sys);
		genService.generateUnitLoadLabelId(sys, null);
		genService.generatePickOrderNumber(sys, null);
		genService.generateOrderNumber(sys);
		genService.generateStorageRequestNumber(sys);
		genService.generateGoodsOutNumber(sys) ;
		genService.generateReplenishNumber(sys);
		genService.generateManageInventoryNumber();
		
		
		log.info("Create Strategies...");
		
		orderStrategyService.getDefault(sys);
		orderStrategyService.getExtinguish(sys);
		
		storageStrategyService.getDefault();
		
		log.info("Create Properties...");
		propertyService.createSystemProperty(sys, null, LOSInventoryPropertyKey.PRINT_GOODS_RECEIPT_LABEL, "false", LOSCommonPropertyKey.PROPERTY_GROUP_SERVER, resolve("PropertyDescPRINT_GOODS_RECEIPT_LABEL", locale), false, false);
		propertyService.createSystemProperty(sys, null, LOSInventoryPropertyKey.STORE_GOODS_RECEIPT_LABEL, "false", LOSCommonPropertyKey.PROPERTY_GROUP_SERVER, resolve("PropertyDescSTORE_GOODS_RECEIPT_LABEL", locale), false, false);
		propertyService.createSystemProperty(sys, null, LOSInventoryPropertyKey.GOODS_IN_DEFAULT_LOCK, "0", LOSCommonPropertyKey.PROPERTY_GROUP_SERVER, resolve("PropertyDescGOODS_IN_DEFAULT_LOCK", locale), false, false);
		propertyService.createSystemProperty(sys, null, LOSInventoryPropertyKey.DEFAULT_GOODS_RECEIPT_LOCATION_NAME, null, LOSCommonPropertyKey.PROPERTY_GROUP_CLIENT, resolve("PropertyDescDEFAULT_GOODS_RECEIPT_LOCATION_NAME", locale), false, false);
//		propertyService.createSystemProperty(sys, null, LOSInventoryPropertyKey.DEFAULT_GOODS_OUT_LOCATION_NAME, null, LOSCommonPropertyKey.PROPERTY_GROUP_CLIENT, resolve("PropertyDescPDEFAULT_GOODS_OUT_LOCATION_NAME", locale), false, false);
		propertyService.createSystemProperty(sys, null, LOSInventoryPropertyKey.GOODS_RECEIPT_PRINTER, null, LOSCommonPropertyKey.PROPERTY_GROUP_CLIENT, resolve("PropertyDescGOODS_RECEIPT_PRINTER", locale), false, false);
		propertyService.createSystemProperty(sys, null, LOSGoodsReceiptComponent.GR_LIMIT_AMOUNT_TO_NOTIFIED, "false", LOSCommonPropertyKey.PROPERTY_GROUP_SERVER, resolve("PropertyDescGR_LIMIT_AMOUNT_TO_NOTIFIED", locale), false, false);
		propertyService.createSystemProperty(sys, null, LOSInventoryPropertyKey.SHIPPING_RENAME_UNITLOAD, "false", LOSCommonPropertyKey.PROPERTY_GROUP_SERVER, resolve("PropertyDescSHIPPING_RENAME_UNITLOAD", locale), false, false);
		propertyService.createSystemProperty(sys, null, LOSInventoryPropertyKey.SHIPPING_LOCATION, null, LOSCommonPropertyKey.PROPERTY_GROUP_SERVER, resolve("PropertyDescSHIPPING_LOCATION", locale), false, false);

		ItemUnit pce = unitService.getDefault();
		pce.setUnitName( resolve("BasicDataItemUnitPcs", locale) );
		
		log.info("Create Inventory Basic Data. done.");
	}
	
	
	private final String resolve( String key, Locale locale ) {
        if (key == null) {
            return "";
        }
        
        ResourceBundle bundle;
        try {
            bundle = ResourceBundle.getBundle("de.linogistix.los.inventory.res.Bundle", locale, InventoryBundleResolver.class.getClassLoader());
            String s = bundle.getString(key);
            return s;
        }
        catch (MissingResourceException ex) {
        	log.error("Exception: "+ex.getMessage());
            return key;
        }
        catch (IllegalFormatException ife){
        	log.error("Exception: "+ife.getMessage());
        	return key;
        }
    }
}
