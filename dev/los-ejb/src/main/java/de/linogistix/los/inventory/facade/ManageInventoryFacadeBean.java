/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jws.WebParam;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.service.ClientService;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.inventory.businessservice.LOSAdviceBusiness;
import de.linogistix.los.inventory.businessservice.LOSInventoryComponent;
import de.linogistix.los.inventory.customization.ManageItemDataService;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.exception.InventoryTransactionException;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.inventory.service.ItemDataService;
import de.linogistix.los.inventory.service.ItemUnitService;
import de.linogistix.los.inventory.service.LOSLotService;
import de.linogistix.los.inventory.service.QueryItemDataService;
import de.linogistix.los.location.businessservice.LOSStorage;
import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote;
import de.linogistix.los.location.service.QueryFixedAssignmentService;
import de.linogistix.los.location.service.QueryUnitLoadTypeService;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.util.businessservice.ContextService;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.inventory.InventoryBusiness;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.product.ItemUnit;
import de.wms2.mywms.product.PackagingUnit;
import de.wms2.mywms.product.PackagingUnitEntityService;

/**
 * A Webservice for managing ItemData/articles in the wms.
 * 
 * @see ManageInventoryFacade
 * 
 * @author trautm
 *
 */
@Stateless 
@PermitAll
public class ManageInventoryFacadeBean implements ManageInventoryFacade {

	Logger log = Logger.getLogger(ManageInventoryFacadeBean.class);

	@EJB
	private LOSStorage storage;
	
	@EJB
	private ManageItemDataService itemDataService;

	@EJB
	private ItemDataService itemDataServiceMyWMSWithoutProject;
	
	@EJB
	private QueryItemDataService itemDataQuery;
	
	@EJB
	private ClientService clientService;
	
    @EJB 
    private LOSLotService lotService;
	
    @EJB
    private LOSAdviceBusiness goodsAdvice;
	
    @EJB
    private LOSInventoryComponent inventoryComponent;
    
    @EJB
    private ContextService contextService;
    
    @EJB
    private InventoryGeneratorService genService;
    
    @EJB
    private LOSStorageLocationQueryRemote slQueryRemote;
    
	@EJB
	private QueryFixedAssignmentService queryFixService;

	@EJB
	private QueryUnitLoadTypeService queryUltService;

	@EJB
	private ItemUnitService itemUnitService;
	
	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	
	@Inject
	private PackagingUnitEntityService packagingUnitService;
	@Inject
	private InventoryBusiness inventoryBusiness;
	@Inject
	private PackagingUnitEntityService packagingUnitEntityService;

	/* 
	 * @see ManageInventoryRemote#createItemData(java.lang.String, java.lang.String)
	 */
	public boolean createItemData (
			@WebParam( name="clientRef") String clientRef, 
			@WebParam( name="articleRef") String articleRef)throws InventoryException {
		
		Client c;
		ItemData itemData;
		
        if (clientRef == null){
            throw new InventoryException(InventoryExceptionKey.CLIENT_NULL, new Object[0]);
        }
        
        if (articleRef == null){
            throw new InventoryException(InventoryExceptionKey.ARTICLE_NULL, new Object[0]);
        }
        
        c = clientService.getByNumber(clientRef);
		
        if(c == null){
			log.info("Create a new Client: " + clientRef);
			c = clientService.create(clientRef, clientRef, clientRef);
		}
		try{
			ItemUnit hu = itemUnitService.getDefault();
			itemData = itemDataService.createItemData(c, articleRef, articleRef, hu);
            itemData.setName(articleRef);
			log.info("Create a new ItemData: <" + articleRef + "> for client: <" + c.getNumber() + ">");
			return true;
		} catch (InventoryTransactionException ex){
			log.error(ex.getMessage(), ex);
			throw new InventoryException(InventoryExceptionKey.ITEMDATA_EXISTS, new String[]{articleRef});
		}
	}

	/* 
	 * @see ManageInventoryRemote#deleteItemData(java.lang.String, java.lang.String)
	 */
	public boolean deleteItemData(
			@WebParam( name="clientRef") String clientRef, 
			@WebParam( name="articleRef") String articleRef) {
		
		Client c = clientService.getByNumber(clientRef);
		
		if(c == null){
			log.error("--- !!! NO SUCH CLIENT "+clientRef+" !!! ---");
			return false;
		}
		
		ItemData iDat = itemDataQuery.getByItemNumber(c, articleRef);
		
		if(iDat == null){
			log.error("--- !!! NO SUCH ITEM DATA "+clientRef+ " / "+articleRef+" !!! ---");
			return false;
		}
		
		try{
			itemDataService.deleteItemData(iDat);
			return true;
		
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}

	/* 
	 * @see ManageInventoryRemote#updateItemReference(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean updateItemReference(
			@WebParam( name="clientRef") String clientRef, 
			@WebParam( name="existingRef") String existingRef,
			@WebParam( name="newRef") String newRef) {
		
		Client c = clientService.getByNumber(clientRef);
		
		if(c == null){
			log.error("--- !!! NO SUCH CLIENT "+clientRef+" !!! ---");
			return false;
		}
		
		ItemData iDat = itemDataQuery.getByItemNumber(c, existingRef);
		
		if(iDat == null){
			log.error("--- !!! NO SUCH ITEM DATA "+clientRef+ " / "+existingRef+" !!! ---");
			return false;
		}
		
		iDat.setNumber(newRef);
		
		return true;
	}

	public boolean createAvis(
			@WebParam( name="clientRef") String clientRef,
			@WebParam( name="articleRef") String articleRef,
			@WebParam( name="batchRef") String batchRef,
			@WebParam(name="amount") BigDecimal amount,
			@WebParam( name="expectedDelivery") Date expectedDelivery,
			@WebParam( name="bestBeforeEnd") Date bestBeforeEnd,
			@WebParam( name="useNotBefore") Date useNotBefore,
			@WebParam(name="expireBatch") boolean expireBatch)
			{
		
        Lot batch = null;
		
		try {
			
			Client c = clientService.getByNumber(clientRef);
			
			if(c == null){
				log.error("--- !!! NO SUCH CLIENT "+clientRef+" !!! ---");
				return false;
			}
			
			ItemData itemData = itemDataQuery.getByItemNumber(c, articleRef);
			
			if(itemData == null){
				log.error("--- !!! NO SUCH ITEM DATA "+clientRef+ " / " +articleRef+" !!! ---");
				return false;
			}
			
			if(batchRef != null){
				try{
					batch = lotService.getByNameAndItemData(c, batchRef, itemData.getNumber());
				} catch (EntityNotFoundException ex){
					log.warn("No Iventory Found: CREATED. - " + ex.getMessage());
					batch = lotService.create(c, itemData, batchRef, new Date(), useNotBefore, bestBeforeEnd);
				}
				
				inventoryComponent.processLotDates(batch, bestBeforeEnd, useNotBefore);
			}
			
			goodsAdvice.goodsAdvise(c, itemData, batch, amount, expireBatch, expectedDelivery, "");
			
			return true;
		
		} catch (Throwable t){
			log.error(t.getMessage(), t);
			return false;
		}
		
	}
	
	public boolean createAvis(String clientRef, String articleRef,
			String batchRef, BigDecimal amount, Date expectedDelivery,
			Date bestBeforeEnd, Date useNotBefore, boolean expireBatch,
			String requestID) {
		return createAvis(clientRef, articleRef,
				batchRef, amount, expectedDelivery,
				bestBeforeEnd, useNotBefore, expireBatch,
				requestID, "");
	}
	
	public boolean createAvis(String clientRef, String articleRef,
			String batchRef, BigDecimal amount, Date expectedDelivery,
			Date bestBeforeEnd, Date useNotBefore, boolean expireBatch,
			String requestID, String comment) {
		
        Lot batch = null;
		
		try {
			
			Client c = clientService.getByNumber(clientRef);
			
			if(c == null){
				log.error("--- !!! NO SUCH CLIENT "+clientRef+" !!! ---");
				return false;
			}
			
			ItemData itemData = itemDataQuery.getByItemNumber(c, articleRef);
			
			if(itemData == null){
				log.error("--- !!! NO SUCH ITEM DATA "+clientRef+ " / "+articleRef+" !!! ---");
				return false;
			}
			
			if(batchRef != null){
				try{
					batch = lotService.getByNameAndItemData(c, batchRef, itemData.getNumber());
				} catch (EntityNotFoundException ex){
					log.warn("No Iventory Found: CREATED. - " + ex.getMessage());
					batch = lotService.create(c, itemData, batchRef, new Date(), useNotBefore, bestBeforeEnd);
				}
				
				inventoryComponent.processLotDates(batch, bestBeforeEnd, useNotBefore);
			}
			
			LOSAdvice adv = goodsAdvice.goodsAdvise(c, itemData, batch, amount, expireBatch, expectedDelivery, requestID);
			adv.setAdditionalContent(comment);
			
			return true;
			
		} catch (Throwable t){
			log.error(t.getMessage(), t);
			return false;
		}
	}

    public void createStockUnitOnStorageLocation(
            
            @WebParam(name = "clientRef") String clientRef,
            
            @WebParam(name = "slName") String slName,
            
            @WebParam(name = "articleRef") String articleRef,
            
            @WebParam(name = "lotRef") String lotRef,
            
            @WebParam(name = "amount") BigDecimal amount,
            
            @WebParam(name = "unitLoadRef") String unitLoadRef) throws InventoryException, FacadeException, EntityNotFoundException 
    {    
  
        inventoryComponent.createStockUnitOnStorageLocation(clientRef, slName, articleRef, lotRef, amount, unitLoadRef,genService.generateManageInventoryNumber(), null );
    }

    public void deleteStockUnitsFromStorageLocations(List<BODTO<StorageLocation>> locations) throws FacadeException{
    	
    	if (locations == null){
    		return;
    	}
    
    	for (BODTO<StorageLocation> loc : locations){
    		StorageLocation sl = manager.find(StorageLocation.class, loc.getId());
    		if (sl != null){
    			inventoryComponent.deleteStockUnitsFromStorageLocation(sl, genService.generateManageInventoryNumber());
    		}
    	}
    }

    public void sendStockUnitsToNirwanaFromSl(List<BODTO<StorageLocation>> locations) throws FacadeException{
    	
    	if (locations == null){
    		return;
    	}
    	
    	for (BODTO<StorageLocation> loc : locations){
    		StorageLocation sl = manager.find(StorageLocation.class, loc.getId());
    		if (sl != null){
    			inventoryComponent.sendStockUnitsToNirwana(sl, genService.generateManageInventoryNumber());
    		}
    	}
    }
    
    public void sendStockUnitsToNirwana(List<BODTO<StockUnit>> sus) throws FacadeException{
    	
    	if (sus == null){
    		return;
    	}
    	
    	for (BODTO<StockUnit> loc : sus){
    		StockUnit su = manager.find(StockUnit.class, loc.getId());
    		if (su != null){
    				// do not change amount to zero. This kills reservation check!  
    				// inventoryComponent.changeAmount(su, new BigDecimal(0), true, genService.generateManageInventoryNumber(), null, null, true);
                    inventoryComponent.sendStockUnitsToNirwana(su, genService.generateManageInventoryNumber());
    		}
    	}
    }
    
    public void sendStockUnitsToNirwanaFromUl(List<BODTO<UnitLoad>> unitLoads) throws FacadeException{
    	
    	if (unitLoads == null){
    		return;
    	}
    	
    	
    	for (BODTO<UnitLoad> loc : unitLoads){
    		UnitLoad ul = manager.find(UnitLoad.class, loc.getId());
    		if (ul != null){
    			inventoryComponent.sendStockUnitsToNirwana(ul, genService.generateManageInventoryNumber());
    		}
    	}
    }
	
    public void sendStockUnitToNirwana(String location) throws FacadeException{
    	
    	if (location == null){
    		return;
    	}
  
        StorageLocation sl = slQueryRemote.queryByIdentity(location);
        sl = manager.find(StorageLocation.class, sl.getId());
        if (sl != null) {
            inventoryComponent.sendStockUnitsToNirwana(sl, genService.generateManageInventoryNumber());
        }
    }

	public void transferStockUnit(BODTO<StockUnit> suTO,
			BODTO<UnitLoad> ulTO) throws FacadeException {
		
		transferStockUnit(suTO, ulTO, false, false, null);
		
	}
	
    public void transferStockUnit(BODTO<StockUnit> suTO, BODTO<UnitLoad> ulTO, String info) throws FacadeException {
		transferStockUnit(suTO, ulTO, false, false, info);
    }

	public void transferStockUnit(BODTO<StockUnit> suTO,
			BODTO<UnitLoad> ulTO, 
			boolean removeSuReservation,
			boolean removeSuLock) throws FacadeException {
		transferStockUnit(suTO, ulTO, removeSuReservation, removeSuLock, null);
	}
	
	public void transferStockUnit(BODTO<StockUnit> suTO,
			BODTO<UnitLoad> ulTO, 
			boolean removeSuReservation,
			boolean removeSuLock,
			String comment) throws FacadeException {
		
		StockUnit su = manager.find(StockUnit.class, suTO.getId());
		UnitLoad ul = manager.find(UnitLoad.class, ulTO.getId());
		
		String s = genService.generateManageInventoryNumber();
		if (removeSuLock){
			su.setLock(0);
		}
		
		if (removeSuReservation){
			su.setReservedAmount(new BigDecimal(0));
		}
		
		inventoryComponent.transferStockUnit(su, ul, s, comment);
		
	}
  
	public boolean testSuitable(BODTO<StockUnit> su, BODTO<UnitLoad> ul) throws FacadeException{
		UnitLoad unitLoad = manager.find(UnitLoad.class, ul.getId());
		StockUnit stockUnit = manager.find(StockUnit.class, su.getId());
		return inventoryComponent.testSuiable(stockUnit, unitLoad);
	}

	public void changeAmount(BODTO<StockUnit> su, BigDecimal amount, BigDecimal reserved, String packaging, String comment)
			throws FacadeException {
		
		StockUnit stockUnit = manager.find(StockUnit.class, su.getId());
		String s = genService.generateManageInventoryNumber();

		PackagingUnit packagingUnit = null;
		if (!StringUtils.isEmpty(packaging)) {
			packagingUnit = packagingUnitService.readIgnoreCase(packaging, stockUnit.getItemData());
		}

		inventoryComponent.changeAmount(stockUnit, amount, true, s, comment, null, true);
		try {
			inventoryBusiness.changePackagingUnit(stockUnit, packagingUnit);
		} catch (BusinessException e) {
			// TODO krane Factory bauen und testen
			throw new FacadeException(e.getMessage(), e.getMessage(), new Object[] {}, e.getBundleResolver());
		}

		inventoryComponent.changeReservedAmount(stockUnit, reserved, s);
	}
	
	public void transferStock(BODTO<StockUnit> from, BODTO<StockUnit> to,
			BigDecimal amount, boolean makeReservation) throws FacadeException {
		
		StockUnit fromStockUnit = manager.find(StockUnit.class, from.getId());
		StockUnit toStockUnit = manager.find(StockUnit.class, to.getId());
		
		String s = genService.generateManageInventoryNumber();

		if (makeReservation){
			fromStockUnit.setReservedAmount(amount);
		}
		inventoryComponent.transferStockFromReserved(fromStockUnit, toStockUnit, amount, s);		
	}
	
	public void transferStock(BODTO<UnitLoad> from, BODTO<UnitLoad> to
			, boolean relesereservation) throws FacadeException {
		
		UnitLoad fromUl = manager.find(UnitLoad.class, from.getId());
		UnitLoad toUl = manager.find(UnitLoad.class, to.getId());
		String s = genService.generateManageInventoryNumber();

		inventoryComponent.transferStock(fromUl, toUl, s, false);		
	}

	public BODTO<Lot> createLot(BODTO<Client> client, 
								BODTO<ItemData> item,
								String lotNumber, 
								Date useNotBefore, 
								Date bestBeforeEnd,
								boolean expireBatch) 
		throws InventoryException 
	{
		Client cl;
		try {
			cl = clientService.get(client.getId());
		} catch (EntityNotFoundException e) {
			throw new InventoryException(
					InventoryExceptionKey.NO_SUCH_CLIENT, 
					new Object[]{client.getName()});
		}
		
		ItemData it;
		try {
			it = itemDataServiceMyWMSWithoutProject.get(item.getId());
		} catch (EntityNotFoundException e) {
			throw new InventoryException(
					InventoryExceptionKey.NO_SUCH_ITEMDATA, 
					new Object[]{item.getName()});
		}
				
		try {
			lotService.getByNameAndItemData(cl, lotNumber, it.getNumber());
			throw new InventoryException(InventoryExceptionKey.LOT_ALREADY_EXIST, new Object[]{lotNumber});
			
		} catch (EntityNotFoundException e) {
			// o.k. the number still does not exist
		}
		
		Lot lot = lotService.create(cl, it, lotNumber, new Date(), useNotBefore, bestBeforeEnd);
		
		inventoryComponent.processLotDates(lot, bestBeforeEnd, useNotBefore);
		
		return new BODTO<Lot>(lot.getId(), lot.getVersion(), lot.getName());
	}

		
	public void reserveStock(BODTO<StockUnit> stockTO, BigDecimal amountToReserve) throws InventoryException{
		
		if(stockTO == null){
			throw new InventoryException(InventoryExceptionKey.ARGUMENT_NULL, 
										 new Object[0]);
		}
		
		StockUnit stock = manager.find(StockUnit.class, stockTO.getId());
		
		if(stock == null){
			throw new InventoryException(InventoryExceptionKey.NO_STOCKUNIT, 
										 new Object[]{stockTO.getName()});
		}
		
		if(stock.getLock()>0){
			throw new InventoryException(InventoryExceptionKey.STOCKUNIT_IS_LOCKED, 
										 new Object[]{stock.getLock()});
		}
		
		if(stock.getAvailableAmount().compareTo(amountToReserve) < 0){
			throw new InventoryException(InventoryExceptionKey.UNSUFFICIENT_AMOUNT, 
										 new Object[]{stock.getAvailableAmount(), stock.getId()});
		}
		
		
		stock.addReservedAmount(amountToReserve);
	}
	
	public void releaseReservation(BODTO<StockUnit> stockTO, BigDecimal amountToRelease) throws InventoryException{
		
		if(stockTO == null){
			throw new InventoryException(InventoryExceptionKey.ARGUMENT_NULL, 
										 new Object[0]);
		}
		
		StockUnit stock = manager.find(StockUnit.class, stockTO.getId());
		
		if(stock == null){
			throw new InventoryException(InventoryExceptionKey.NO_STOCKUNIT, 
										 new Object[]{stockTO.getName()});
		}
		
		if(stock.getLock()>0){
			throw new InventoryException(InventoryExceptionKey.STOCKUNIT_IS_LOCKED, 
										 new Object[]{stock.getLock()});
		}
		
		if(stock.getReservedAmount().compareTo(amountToRelease) < 0){
			throw new InventoryException(InventoryExceptionKey.UNSUFFICIENT_RESERVED_AMOUNT, 
										 new Object[]{stock.getReservedAmount()});
		}
		
		
		stock.releaseReservedAmount(amountToRelease);
		
	}

//	public void createStockUnit(String clientRef, String suName,
//			String articleRef, String lotRef, int amount, String unitLoadRef)
//			throws InventoryException, FacadeException, EntityNotFoundException {
//		Client c = clientService.getByNumber(clientRef);  
//        User u = contextService.getCallersUser();
//    	manageInventory.createStockUnit(clientRef, suName, articleRef, lotRef, amount, unitLoadRef,genService.generateManageInventoryNumber(u) );
//		
//	}

	public void removeStockUnit(BODTO<StockUnit> stockUnit, String activityCode)
		throws FacadeException 
	{

		StockUnit su = manager.find(StockUnit.class, stockUnit.getId());
		
		inventoryComponent.removeStockUnit(su, activityCode, true);

	}
	
	public void transferUnitLoad(BODTO<StorageLocation> target,
			BODTO<UnitLoad> ul, int index, boolean ignoreSlLock, String info) throws FacadeException {
		StorageLocation targetLocation = manager.find(StorageLocation.class, target.getId());
		if (targetLocation == null)
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_STORAGELOCATION, new String[]{target.getName()});
		
		UnitLoad unitLoad = manager.find(UnitLoad.class, ul.getId());
		if (unitLoad == null)
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_UNITLOAD, new String[]{ul.getName()});
		
		// Handle fixed locations
		ItemData itemData = null;
		boolean isItemDataUnique = true;
		for( StockUnit su : unitLoad.getStockUnitList() ) {
			if( itemData == null ) {
				itemData = su.getItemData();
				continue;
			}
			if( itemData.equals( su.getItemData() ) ) {
				continue;
			}
			isItemDataUnique = false;
		}
		
		LOSFixedLocationAssignment fix = queryFixService.getByLocation(targetLocation);
		if( fix != null ) {
			if( !isItemDataUnique ) {
				log.error("Cannot store mixed unit load on fixed location for item data="+fix.getItemData().getNumber());
				throw new InventoryException(InventoryExceptionKey.WRONG_ITEMDATA, new Object[]{""});
			}
			if( itemData == null ) {
				log.error("Cannot store none item data on fixed location for item data="+fix.getItemData().getNumber());
				throw new InventoryException(InventoryExceptionKey.WRONG_ITEMDATA, new Object[]{""});
			}
			if( ! fix.getItemData().equals(itemData) ) {
				log.error("Cannot store item data="+ itemData.getNumber()+" on fixed location for item data="+fix.getItemData().getNumber());
				throw new InventoryException(InventoryExceptionKey.WRONG_ITEMDATA, new Object[]{itemData.getNumber(),fix.getItemData().getNumber()});
			}
			
			if (targetLocation.getUnitLoads() != null && targetLocation.getUnitLoads().size() > 0) {
				// There is aready a unit load on the destination. => Add stock
				
				UnitLoad onDestination = targetLocation.getUnitLoads().get(0);
				
				inventoryComponent.transferStock(unitLoad, onDestination, "", false);
				storage.sendToNirwana( contextService.getCallerUserName(), unitLoad);
				log.info("Transferred Stock to virtual UnitLoadType: "+onDestination.toShortString());
			} else {
				UnitLoadType virtual = queryUltService.getPickLocationUnitLoadType();

				unitLoad.setUnitLoadType(virtual);
				unitLoad.setLabelId(targetLocation.getName());
				storage.transferUnitLoad( contextService.getCallerUserName(), targetLocation, unitLoad, -1, false, null, null);

			}

			return;
		}
		

		storage.transferUnitLoad(contextService.getCallerUserName(), targetLocation, unitLoad, -1, ignoreSlLock, info, "");
		
	}

	@Override
	public List<String> readPackagingNames(ItemData itemData) {
		return packagingUnitEntityService.readNamesByItemData(itemData);
	}

}
