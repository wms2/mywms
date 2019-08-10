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
import javax.ejb.EJBAccessException;
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
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.util.businessservice.ContextService;
import de.wms2.mywms.inventory.InventoryBusiness;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.inventory.StockState;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadEntityService;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.inventory.UnitLoadTypeEntityService;
import de.wms2.mywms.location.LocationType;
import de.wms2.mywms.location.LocationTypeEntityService;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.location.StorageLocationEntityService;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.product.ItemUnit;
import de.wms2.mywms.product.PackagingUnit;
import de.wms2.mywms.product.PackagingUnitEntityService;
import de.wms2.mywms.strategy.FixAssignment;
import de.wms2.mywms.strategy.FixAssignmentEntityService;

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
    private ContextService contextService;
    
    @EJB
    private InventoryGeneratorService genService;
    
    @EJB
    private LOSStorageLocationQueryRemote slQueryRemote;
    
	@EJB
	private FixAssignmentEntityService fixService;

	@EJB
	private ItemUnitService itemUnitService;
	@EJB
	private LocationTypeEntityService slTypeService;
	@EJB
	private ItemDataService itemDataService1;

	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	
	@Inject
	private PackagingUnitEntityService packagingUnitService;
	@Inject
	private InventoryBusiness inventoryBusiness;
	@Inject
	private PackagingUnitEntityService packagingUnitEntityService;
	@Inject
	private UnitLoadTypeEntityService unitLoadTypeService;
	@Inject
	private StorageLocationEntityService locationService;
	@Inject
	private UnitLoadEntityService unitLoadService;

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
				
				lotService.processLotDates(batch, bestBeforeEnd, useNotBefore);
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
				
				lotService.processLotDates(batch, bestBeforeEnd, useNotBefore);
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

		Client c;
		StorageLocation sl;
		Lot lot = null;
		UnitLoad ul;

		try {
			c = clientService.getByNumber(clientRef);
			Client callerClient = contextService.getCallersClient();
			if ((!callerClient.equals(c)) && (!callerClient.isSystemClient())) {
				throw new EJBAccessException();
			}

			sl = locationService.read(slName);

			if (sl == null) {
				log.warn("NOT FOUND. Going to CREATE StorageLocation " + slName);

				LocationType type;
				try {
					type = slTypeService.getDefault();
					if (type == null)
						throw new NullPointerException("No default location type found.");
				} catch (Throwable e) {
					log.error(e.getMessage(), e);
					throw new RuntimeException(e.getMessage());
				}
				sl = locationService.create(slName, c, type, null, null);
			}

			ItemData idat = itemDataService1.getByItemNumber(c, articleRef);

			if (idat == null) {
				log.error("--- !!! NO ITEM WITH NUMBER " + articleRef + " !!! ---");
				throw new InventoryException(InventoryExceptionKey.NO_SUCH_ITEMDATA, articleRef);
			}

			if ((lotRef != null && lotRef.length() > 0) || idat.isLotMandatory()) {
				try {
					lot = getOrCreateLot(c, lotRef, idat);
				} catch (Throwable ex) {
					log.error(ex.getMessage(), ex);
					throw new InventoryException(InventoryExceptionKey.CREATE_STOCKUNIT_ON_STORAGELOCATION_FAILED,
							slName);
				}
			}
			try {
				ul = getOrCreateUnitLoad(c, idat, sl, unitLoadRef, StockState.ON_STOCK);
			} catch (Throwable ex) {
				log.error(ex.getMessage(), ex);
				throw new InventoryException(InventoryExceptionKey.CREATE_STOCKUNIT_ON_STORAGELOCATION_FAILED, slName);
			}

			// Fucking hibernate is not able to handle the stock unit list without accessing
			// it in advance
			ul.getStockUnitList().size();

			inventoryBusiness.createStock(ul, idat, amount, lot, null, null, ul.getState(), null, null, null, true);

		} catch (FacadeException ex) {
			throw ex;
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new InventoryException(InventoryExceptionKey.CREATE_STOCKUNIT_ONSTOCK, "");
		}
	}

	private Lot getOrCreateLot(Client c, String lotRef, ItemData idat) {
		Lot lot;
		if (lotRef != null && lotRef.length() != 0) {
			try {
				lot = lotService.getByNameAndItemData(c, lotRef, idat.getNumber());
				if (!lot.getItemData().equals(idat)) {
					throw new RuntimeException("ItemData does not match Lot");
				}
			} catch (EntityNotFoundException ex) {
				log.warn("CREATE Lot: " + ex.getMessage());
				lot = lotService.create(c, idat, lotRef, new Date(), null, null);
			}
		} else {
			throw new IllegalArgumentException("Missing orderRef");
		}
		return lot;
	}

	private UnitLoad getOrCreateUnitLoad(Client c, ItemData idat, StorageLocation sl, String ref, int state)
			throws FacadeException {
		UnitLoad ul;
		UnitLoadType type;

		if (c == null)
			throw new NullPointerException("Client must not be null");
		if (idat == null)
			throw new NullPointerException("Article must not be null");
		if (sl == null)
			throw new NullPointerException("StorageLocation must not be null");
		if (ref == null)
			throw new NullPointerException("Reference must not be null");

		if (ref != null && ref.length() != 0) {
			ul = unitLoadService.read(ref);
			if (ul == null) {
				log.warn("Unit load does not exist. create new. labelId=" + ref);
				type = idat.getDefaultUnitLoadType();
				if (type == null) {
					type = unitLoadTypeService.getDefault();
				}
				if (type == null) {
					throw new RuntimeException("Cannot retrieve default UnitLoadType");
				}
				ul = inventoryBusiness.createUnitLoad(c, ref, type, sl, StockState.ON_STOCK, null, null, null);
			}
		} else {
			throw new IllegalArgumentException("Missing labelId");
		}
		return ul;
	}

    public void sendStockUnitsToNirwanaFromSl(List<BODTO<StorageLocation>> locations) throws FacadeException{
    	
    	if (locations == null){
    		return;
    	}
    	
    	for (BODTO<StorageLocation> loc : locations){
    		StorageLocation sl = manager.find(StorageLocation.class, loc.getId());
    		if (sl != null){
    			for (UnitLoad ul : sl.getUnitLoads()) {
        			inventoryBusiness.deleteUnitLoad(ul, null, null, null);
    			}
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
    			inventoryBusiness.deleteStockUnit(su, null, null, null);
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
    			inventoryBusiness.deleteUnitLoad(ul, null, null, null);
    		}
    	}
    }

    public void transferStockUnit(BODTO<StockUnit> suTO, BODTO<UnitLoad> ulTO, String info) throws FacadeException {
		transferStockUnit(suTO, ulTO, false, false, info);
    }

	public void transferStockUnit(BODTO<StockUnit> suTO,
			BODTO<UnitLoad> ulTO, 
			boolean removeSuReservation,
			boolean removeSuLock,
			String comment) throws FacadeException {
		
		StockUnit su = manager.find(StockUnit.class, suTO.getId());
		UnitLoad ul = manager.find(UnitLoad.class, ulTO.getId());
		
		if (removeSuLock){
			su.setLock(0);
		}
		
		if (removeSuReservation){
			su.setReservedAmount(new BigDecimal(0));
		}
		
		inventoryBusiness.checkTransferStock(su, ul, null);
		inventoryBusiness.transferStock(su, ul, null, su.getState(), null, null, comment);
	}

	public void testSuitable(BODTO<StockUnit> su, BODTO<UnitLoad> ul) throws FacadeException{
		UnitLoad unitLoad = manager.find(UnitLoad.class, ul.getId());
		StockUnit stockUnit = manager.find(StockUnit.class, su.getId());
		inventoryBusiness.checkTransferStock(stockUnit, unitLoad, null);
	}

	public void changeAmount(BODTO<StockUnit> su, BigDecimal amount, BigDecimal reserved, String packaging, String comment)
			throws FacadeException {
		
		StockUnit stockUnit = manager.find(StockUnit.class, su.getId());

		PackagingUnit packagingUnit = null;
		if (!StringUtils.isEmpty(packaging)) {
			packagingUnit = packagingUnitService.readIgnoreCase(packaging, stockUnit.getItemData());
		}

		inventoryBusiness.changeAmount(stockUnit, amount, null, null, null, comment, true);
		inventoryBusiness.changePackagingUnit(stockUnit, packagingUnit);
		
		if (reserved.compareTo(new BigDecimal(0)) < 0) {
			throw new IllegalArgumentException("Amount cannot be negative");
		}

		if (reserved.compareTo(amount) > 0) {
			throw new InventoryException(InventoryExceptionKey.CANNOT_RESERVE_MORE_THAN_AVAILABLE, "" + stockUnit.getAmount());
		}

		stockUnit.setReservedAmount(reserved);
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
		
		lotService.processLotDates(lot, bestBeforeEnd, useNotBefore);
		
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
		
		FixAssignment fix = fixService.readFirst(null, targetLocation);
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
				for (StockUnit stock : unitLoad.getStockUnitList()) {
					inventoryBusiness.transferStock(stock, onDestination, null, StockState.ON_STOCK, null, null, info);
				}
				
				log.info("Transferred Stock to virtual UnitLoadType: "+onDestination.toShortString());
			} else {
				UnitLoadType virtual = unitLoadTypeService.getVirtual();

				unitLoad.setUnitLoadType(virtual);
				unitLoad.setLabelId(targetLocation.getName());
				inventoryBusiness.checkTransferUnitLoad(unitLoad, targetLocation, false);
				inventoryBusiness.transferUnitLoad(unitLoad, targetLocation, null, null, info);
			}

			return;
		}
		
		inventoryBusiness.checkTransferUnitLoad(unitLoad, targetLocation, false);
		inventoryBusiness.transferUnitLoad(unitLoad, targetLocation, null, null, info);
	}

	@Override
	public List<String> readPackagingNames(ItemData itemData) {
		return packagingUnitEntityService.readNamesByItemData(itemData);
	}

}
