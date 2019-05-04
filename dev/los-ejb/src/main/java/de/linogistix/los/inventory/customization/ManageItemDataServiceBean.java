/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.customization;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.globals.SerialNoRecordType;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.ItemUnit;
import org.mywms.service.ItemDataService;
import org.mywms.service.StockUnitService;

import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.exception.InventoryTransactionException;
import de.linogistix.los.inventory.exception.StockExistException;

public class ManageItemDataServiceBean implements ManageItemDataService {

	@EJB
	private ItemDataService itemService;
	
	@EJB
	private StockUnitService stockService;
	@EJB
	private EntityGenerator entityGenerator;

	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	
	private static final Logger log = Logger.getLogger(ManageItemDataServiceBean.class);
	
	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.customization.ManageItemDataService#createItemData(org.mywms.model.Client, java.lang.String, java.lang.String, org.mywms.model.ItemUnit)
	 */
	public ItemData createItemData(Client cl, String number, String name, ItemUnit hu) 
		throws InventoryTransactionException 
	{
		if(cl == null){
			log.error(" --- CLIENT NULL ---");
			throw new NullPointerException(" --- CLIENT NULL ---");
		}
		
		if(number == null){
			log.error(" --- NUMBER NULL ---");
			throw new NullPointerException(" --- NUMBER NULL ---");
		}
		
		if(name == null){
			log.error(" --- NAME NULL ---");
			throw new NullPointerException(" --- NAME NULL ---");
		}
		
		if(hu == null){
			log.error(" --- ITEMUNIT NULL ---");
			throw new NullPointerException(" --- ITEMUNIT NULL ---");
		}
		
		ItemData item = itemService.getByItemNumber(cl, number);
		
		// if an item with number already exists
		if(item != null){
			log.error("--- NOT UNIQUE : CL "+cl.getNumber()+" , IT "+number+" ---");
			
			throw new InventoryTransactionException(InventoryExceptionKey.ITEMDATA_EXISTS);
		}
		
		return getManagedInstance(cl, number, name, hu);
	}

	//---------------------------------------------------------------------------------------
	private ItemData getManagedInstance(Client cl, String number, String name, ItemUnit hu){
		
		ItemData item = entityGenerator.generateEntity( ItemData.class );
		item.setClient(cl);
		item.setNumber(number);
		item.setName(name);
		item.setHandlingUnit(hu);
		
		manager.persist(item);
		
		return item;
		
	}

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.customization.ManageItemDataService#updateItemUnit(org.mywms.model.ItemData, org.mywms.model.ItemUnit)
	 */
	public ItemData updateItemUnit(ItemData item, ItemUnit newValue)
			throws StockExistException 
	{
		if(stockService.getCount(item)>0){
			throw new StockExistException();
		}
		
		item.setHandlingUnit(newValue);
		
		return manager.merge(item);
	}

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.customization.ManageItemDataService#updateLotMandatory(org.mywms.model.ItemData, boolean)
	 */
	public ItemData updateLotMandatory(ItemData item, boolean newValue) throws StockExistException {
		
		if(stockService.getCount(item)>0){
			throw new StockExistException();
		}
		
		item.setLotMandatory(newValue);
		
		return manager.merge(item);
	}

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.customization.ManageItemDataService#updateScale(org.mywms.model.ItemData, int)
	 */
	public ItemData updateScale(ItemData item, int newValue) throws StockExistException {
		
		if(stockService.getCount(item)>0){
			throw new StockExistException();
		}
		
		item.setScale(newValue);
		
		return manager.merge(item);
	}

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.customization.ManageItemDataService#updateSerialNoRecordType(org.mywms.model.ItemData, org.mywms.globals.SerialNoRecordType)
	 */
	public ItemData updateSerialNoRecordType(ItemData item, SerialNoRecordType newValue) 
		throws StockExistException 
	{
		
		if(stockService.getCount(item)>0){
			throw new StockExistException();
		}
		
		item.setSerialNoRecordType(newValue);
		
		return manager.merge(item);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.customization.ManageItemDataService#updateAdviceMandatory(org.mywms.model.ItemData, boolean)
	 */
	public ItemData updateAdviceMandatory(ItemData item, boolean newValue) {
		
		if(item.isAdviceMandatory() != newValue){
			
			item.setAdviceMandatory(newValue);
			
			log.info("[UPDATED]-[ITEMDATA]-Cl "+item.getClient().getNumber()
					+"-No "+item.getNumber()+"-[NEW] AdviceMandatory "+newValue);
			
			return manager.merge(item);
		}
		else{
			return item;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.customization.ManageItemDataService#updateDescription(org.mywms.model.ItemData, java.lang.String)
	 */
	public ItemData updateDescription(ItemData item, String newValue) {
				
		if((item.getDescription() == null && newValue != null)
			|| !item.getDescription().equals(newValue)){
			
			item.setDescription(newValue);
			
			log.info("[UPDATED]-[ITEMDATA]-Cl "+item.getClient().getNumber()
					+"-No "+item.getNumber()+"-[NEW] Description "+newValue);
			
			return manager.merge(item);
		}
		else{
			return item;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.customization.ManageItemDataService#updateName(org.mywms.model.ItemData, java.lang.String)
	 */
	public ItemData updateName(ItemData item, String newValue) {
		
		if(!item.getName().equals(newValue)){
			
			item.setName(newValue);
			
			log.info("[UPDATED]-[ITEMDATA]-Cl "+item.getClient().getNumber()
					+"-No "+item.getNumber()+"-[NEW] Name "+newValue);
			
			return manager.merge(item);
		}
		else{
			return item;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.customization.ManageItemDataService#updateSaftyStock(org.mywms.model.ItemData, int)
	 */
	public ItemData updateSaftyStock(ItemData item, int newValue) {
		
		if(item.getSafetyStock() != newValue){
			
			item.setSafetyStock(newValue);
			
			log.info("[UPDATED]-[ITEMDATA]-Cl "+item.getClient().getNumber()
					+"-No "+item.getNumber()+"-[NEW] SafetyStock "+newValue);
			
			return manager.merge(item);
		}
		else{
			return item;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.customization.ManageItemDataService#deleteItemData(org.mywms.model.ItemData)
	 */
	public void deleteItemData(ItemData item) throws StockExistException {
		
		if(stockService.getCount(item)>0){
			throw new StockExistException();
		}
		
		manager.remove(item);
		
		return;
	}
	
}
