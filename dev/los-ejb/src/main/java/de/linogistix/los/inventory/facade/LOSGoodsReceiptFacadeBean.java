/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.globals.SerialNoRecordType;
import org.mywms.model.Client;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.common.businessservice.LOSPrintService;
import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.common.service.QueryClientService;
import de.linogistix.los.entityservice.BusinessObjectLockState;
import de.linogistix.los.inventory.businessservice.LOSGoodsReceiptComponent;
import de.linogistix.los.inventory.businessservice.LOSInventoryComponent;
import de.linogistix.los.inventory.crud.StockUnitCRUDRemote;
import de.linogistix.los.inventory.customization.ManageReceiptService;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.exception.InventoryTransferException;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.linogistix.los.inventory.model.LOSGoodsReceiptState;
import de.linogistix.los.inventory.model.LOSGoodsReceiptType;
import de.linogistix.los.inventory.model.LOSInventoryPropertyKey;
import de.linogistix.los.inventory.model.StockUnitLabel;
import de.linogistix.los.inventory.query.ItemDataQueryRemote;
import de.linogistix.los.inventory.query.LOSGoodsReceiptQueryRemote;
import de.linogistix.los.inventory.query.LotQueryRemote;
import de.linogistix.los.inventory.report.LOSStockUnitLabelReport;
import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.inventory.service.LOSLotService;
import de.linogistix.los.inventory.service.QueryItemDataService;
import de.linogistix.los.inventory.service.QueryLotService;
import de.linogistix.los.inventory.service.StockUnitService;
import de.linogistix.los.location.businessservice.LOSStorage;
import de.linogistix.los.location.entityservice.LOSUnitLoadService;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.service.QueryFixedAssignmentService;
import de.linogistix.los.location.service.QueryStorageLocationService;
import de.linogistix.los.location.service.QueryUnitLoadTypeService;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.ClientQueryRemote;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.query.exception.BusinessObjectQueryException;
import de.linogistix.los.report.ReportException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;
import de.linogistix.los.util.BusinessObjectHelper;
import de.linogistix.los.util.StringTools;
import de.linogistix.los.util.businessservice.ContextService;
import de.linogistix.los.util.entityservice.LOSSystemPropertyService;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;

@Stateless
public class LOSGoodsReceiptFacadeBean implements LOSGoodsReceiptFacade {

    private final Logger logger = Logger.getLogger(LOSGoodsReceiptFacadeBean.class);
    
    @EJB
    private LOSGoodsReceiptComponent receiptComponent;
    @EJB
    private ClientQueryRemote clientQuery;
    @EJB
    private LotQueryRemote lotQuery;
    @EJB
    private ItemDataQueryRemote itemQuery;
    @EJB
    private LOSUnitLoadService losUlService; 
    @EJB
    private StockUnitCRUDRemote suCrud;
    @EJB
    private InventoryGeneratorService genService;
    @EJB
    private LOSGoodsReceiptQueryRemote qrQuery;
    @EJB
    private LOSLotService lotService;
    @EJB
    private QueryLotService queryLotService;
    @EJB
    private ManageInventoryFacade manageInventoryFacade;
    @EJB
    private LOSSystemPropertyService propertyService;
    @EJB
    private QueryStorageLocationService locService;
    @EJB
    private LOSStorage storageService;
	@EJB
	private ContextService contextService;
	@EJB
	private StockUnitService stockUnitService;
	@EJB
	private QueryUnitLoadTypeService queryUltService;
	@EJB
	private LOSInventoryComponent inventoryComponent;
	@EJB
	private QueryFixedAssignmentService queryFixService;
	@EJB
	private QueryItemDataService queryItemDataService;
	@EJB
	private LOSStockUnitLabelReport suLabelReport;
	@EJB
	private QueryClientService queryClientService;
	@EJB
	private LOSPrintService printService;
	@EJB
	private ManageReceiptService manageGrService;
	
    @PersistenceContext(unitName = "myWMS")
    protected EntityManager manager;

    //-----------------------------------------------------------------------
    // Query data for Process input to choose from
    //----------------------------------------------------------------------
    public List<BODTO<Client>> getAllowedClients() throws BusinessObjectQueryException {
        return clientQuery.queryAllHandles(new QueryDetail(0, Integer.MAX_VALUE, "name", true));
    }

    public List<BODTO<StorageLocation>> getGoodsReceiptLocations() throws LOSLocationException {
        List<StorageLocation> sls = receiptComponent.getGoodsReceiptLocations();
        List<BODTO<StorageLocation>> ret = new ArrayList<BODTO<StorageLocation>>();
        for (StorageLocation sl : sls) {
            ret.add(new BODTO<StorageLocation>(sl.getId(), sl.getVersion(), sl.getName()));
        }

        return ret;
    }

    public List<BODTO<Lot>> getAllowedLots(String lotExp, BODTO<Client> client, BODTO<ItemData> idat) {
        try {

            Client c;
            
            if (client == null) {
                throw new NullPointerException("Client must not be null");
            }
            c = manager.find(Client.class, client.getId());
            if (idat != null) {
            	BODTO<Client> cTO = new BODTO<Client>(c.getId(), c.getVersion(), c.getNumber());
                return lotQuery.autoCompletionByClientAndItemData(lotExp, cTO, idat);
            } else {
                return lotQuery.autoCompletion(lotExp, c);
            }

        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
            return new ArrayList<BODTO<Lot>>();
        }

    }

    public List<BODTO<ItemData>> getAllowedItemData(String exp, BODTO<Client> client, BODTO<Lot> lot) {
        try {

            Client c;
            Lot l;
            List<BODTO<ItemData>> ret;

            c = manager.find(Client.class, client.getId());

            if (lot != null) {
                l = manager.find(Lot.class, lot.getId());
                ret = new ArrayList<BODTO<ItemData>>();
                ret.add(new BODTO<ItemData>(l.getItemData().getId(), l.getItemData().getVersion(), l.getItemData().getNumber()));
                return ret;
            } else {
                return itemQuery.autoCompletion(exp, c);
            }

        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
            return new ArrayList<BODTO<ItemData>>();
        }

    }

    public List<LOSAdvice> getAllowedAdvices(String exp, BODTO<Client> client, BODTO<Lot> lot, BODTO<ItemData> idat) throws InventoryException {
        Lot l = null; 
        ItemData i = null;
        Client c;
        if (lot != null){
            l = manager.find(Lot.class, lot.getId());
        }
        if (idat != null){
            i = manager.find(ItemData.class, idat.getId());
        }
        
        c = manager.find(Client.class, client.getId());
        
        return receiptComponent.getSuitableLOSAdvice(exp, c, l, i);

    }

    //------------------------------------------------------------------------
    // Receive Entity data from database
    //------------------------------------------------------------------------
    /**
     * eagerly fetches pos.
     * 
     * @param number
     * @return
     */
    public LOSGoodsReceipt getGoodsReceipt(LOSGoodsReceipt lazy) {
        try {
            LOSGoodsReceipt gr = qrQuery.queryById(lazy.getId());
            List<LOSGoodsReceiptPosition> l = gr.getPositionList();
            for (LOSGoodsReceiptPosition p : l) {
            	BusinessObjectHelper.eagerRead(p);
            }
            return gr;
        } catch (BusinessObjectNotFoundException ex) {
            logger.error(ex, ex);
            return lazy;
        } catch (BusinessObjectSecurityException ex) {
            logger.error(ex, ex);
            return lazy;
        }


    }

    //------------------------------------------------------------------------
    // Process
    //-----------------------------------------------------------------------
    public LOSGoodsReceipt createGoodsReceipt(BODTO<Client> client, String licencePlate, String driverName, String forwarder, String deliveryNoteNumber, Date receiptDate, BODTO<StorageLocation> goodsInLocation, String additionalContent) {
        LOSGoodsReceipt r;
        StorageLocation sl = manager.find(StorageLocation.class, goodsInLocation.getId());
        Client c = manager.find(Client.class, client.getId());

        r = receiptComponent.createGoodsReceipt(c, licencePlate, driverName, forwarder, deliveryNoteNumber, receiptDate);
        r.setAdditionalContent(additionalContent);
        r.setGoodsInLocation(sl);

        return r;
    }
    
    

    public LOSGoodsReceiptPosition createGoodsReceiptPosition(
            BODTO<Client> client,
            LOSGoodsReceipt gr,
            BODTO<Lot> batch,
            BODTO<ItemData> item,
            String unitLoadLabel,
            BODTO<UnitLoadType> unitLoadType,
            BigDecimal amount,
            BODTO<LOSAdvice> advice) throws FacadeException, ReportException {
    	return createGoodsReceiptPosition(client, 
    			gr, batch, item, unitLoadLabel, unitLoadType, amount,
    			advice,
    			LOSGoodsReceiptType.INTAKE,
    			BusinessObjectLockState.NOT_LOCKED.getLock(), "");
    }
    	
    public LOSGoodsReceiptPosition createGoodsReceiptPosition(
            BODTO<Client> client,
            LOSGoodsReceipt gr,
            BODTO<Lot> batch,
            BODTO<ItemData> item,
            String unitLoadLabel,
            BODTO<UnitLoadType> unitLoadType, 
            BigDecimal amount,
            BODTO<LOSAdvice> advice,
            LOSGoodsReceiptType receiptType,
            int lock,
            String lockCause) throws FacadeException, ReportException {
        return createGoodsReceiptPosition(
                client, gr, batch,item, unitLoadLabel, unitLoadType, amount, advice,
                receiptType, lock, lockCause, null, null, null, null);

    }

    public LOSGoodsReceiptPosition createGoodsReceiptPosition(
            BODTO<Client> client,
            LOSGoodsReceipt gr,
            BODTO<Lot> batch,
            BODTO<ItemData> item,
            String unitLoadLabel,
            BODTO<UnitLoadType> unitLoadType, 
            BigDecimal amount,
            BODTO<LOSAdvice> advice,
            LOSGoodsReceiptType receiptType,
            int lock,
            String lockCause, String serialNumber, String targetLocationName, String targetUnitLoadName, String printer) throws FacadeException, ReportException {

        LOSGoodsReceiptPosition pos;
        UnitLoad ul;
        
        Client c = manager.find(Client.class, client.getId());
        LOSGoodsReceipt goodsReceipt = manager.find(LOSGoodsReceipt.class, gr.getId());
        Lot lot;
        ItemData idat = manager.find(ItemData.class, item.getId());
        StorageLocation sl = manager.find(StorageLocation.class, gr.getGoodsInLocation().getId());
        
        UnitLoadType ulType;
        
        if (unitLoadType != null){
        	ulType = manager.find(UnitLoadType.class, unitLoadType.getId());
        } else{
        	ulType = queryUltService.getDefaultUnitLoadType();
        }
                
        if (idat == null){
        	throw new NullPointerException("" + item.getName());
        }
        
        if (sl == null){
        	throw new NullPointerException("" + gr.getGoodsInLocation().getName());
        }
        
        if (ulType == null){
        	throw new NullPointerException("" + unitLoadType.getName());
        }
        
        if (batch != null){
        	lot = manager.find(Lot.class, batch.getId());
        	if (lot == null){
        		throw new NullPointerException("" + batch.getName());
            }
        } else if (idat.isLotMandatory()){
        	throw new InventoryException(InventoryExceptionKey.LOT_MANDATORY, idat.getNumber());
        } else{
        	lot = null;
        }
        
        boolean printLabel = true;
        boolean isFixedLocation = false;
        StorageLocation targetLocation = null;
        UnitLoad targetUnitLoad = null;
        if( targetLocationName != null && targetLocationName.length()>0 ) {
        	try {
				targetLocation = locService.getByName(targetLocationName);
			} catch (Exception e) {
			}
			if( targetLocation == null ) {
				throw new InventoryException(InventoryExceptionKey.NO_SUCH_STORAGELOCATION, targetLocationName);
			}
			
			// Check, whether the target location is a fixed assigned location.
			// On this locations a special handling is needed, which the posting methods do not know??
			LOSFixedLocationAssignment fix = queryFixService.getByLocation(targetLocation);
			if( fix != null ) {
				if( ! fix.getItemData().equals(idat) ) {
					logger.error("Cannot store item data="+ idat.getNumber()+" on fixed location for item data="+fix.getItemData().getNumber());
					throw new InventoryException(InventoryExceptionKey.WRONG_ITEMDATA, new Object[]{idat.getNumber(),fix.getItemData().getNumber()});
				}
				printLabel = false;
				isFixedLocation = true;
			}
        }
        if( targetUnitLoadName != null && targetUnitLoadName.length()>0 ) {
        	try {
				targetUnitLoad = losUlService.getByLabelId(c, targetUnitLoadName);
 			} catch (Exception e) {
			}
			if( targetUnitLoad == null ) {
				throw new InventoryException(InventoryExceptionKey.NO_SUCH_UNITLOAD, targetUnitLoadName);
			}
        }
        
        
        BODTO<StorageLocation> sldto = new BODTO<StorageLocation>(sl.getId(), sl.getVersion(), sl.toUniqueString());
        BODTO<UnitLoadType> ultTO = new BODTO<UnitLoadType>(ulType.getId(), ulType.getVersion(), ulType.getName());
        
        
        boolean printLabelAutomatically = propertyService.getBooleanDefault(c, null, LOSInventoryPropertyKey.PRINT_GOODS_RECEIPT_LABEL, false);
        if( ! printLabelAutomatically ) {
        	printLabel = false;
        }
        
        try {
	        if (unitLoadLabel != null && unitLoadLabel.length() > 0) {
	        	
	        	printLabel = false;
	        	
	        	try{
	        		ul = losUlService.getByLabelId(c, unitLoadLabel);
	        		
	        		throw new InventoryException(InventoryExceptionKey.UNIT_LOAD_EXISTS, 
	        									 new Object[]{unitLoadLabel}); 
	        		
	        	}catch(EntityNotFoundException enf){ 
	        		BODTO<UnitLoad> unitLoad = getOrCreateUnitLoad(client, sldto, ultTO, unitLoadLabel);
	                ul = manager.find(UnitLoad.class, unitLoad.getId()); 
	        	}
	        } 
	        else {
		        if( targetUnitLoad != null ) {
		        	printLabel = false;
		        }
		        
	        	// do not use the label twice
	        	String label = null;
	        	int i = 0;
	        	while( i++ < 100 ) {
	        		label = genService.generateUnitLoadLabelId(c, ulType);
	        		try {
						losUlService.getByLabelId(c, label);
					} catch (EntityNotFoundException e) {
						break;
					}
	        		logger.info("UnitLoadLabel " + label + " already exists. Try the next");
	        	}

            	BODTO<UnitLoad> unitLoad = getOrCreateUnitLoad(client, sldto, ultTO, label);
                ul = manager.find(UnitLoad.class, unitLoad.getId());
	
	        }     
	        
        } catch (InventoryException ex){
        	throw ex;
        } catch (Throwable ex) {
            logger.error(ex, ex);
            throw new InventoryException(InventoryExceptionKey.CREATE_UNITLOAD, new Object[0]);
        }
        
        pos = receiptComponent.createGoodsReceiptPosition(c, goodsReceipt, "", amount, receiptType, lockCause);
        StockUnit su = receiptComponent.receiveStock(pos, lot, idat, amount, null, ul, serialNumber);
        
        if (!pos.getItemData().equals(idat.getNumber())){
        	throw new IllegalArgumentException("Item Data has not been set");
        }
        
        su = manager.find(su.getClass(), su.getId());
        
        if(lock > 0){
        
	        try {
				suCrud.lock(su, lock, lockCause);
			} catch (BusinessObjectSecurityException e1) {
				logger.error(e1.getMessage(), e1);
				throw new InventoryException(InventoryExceptionKey.STOCKUNIT_CONSTRAINT_VIOLATED, su.toUniqueString());
			}
	        
	        pos.setQaLock(lock);
        }
        
        manager.flush();
        manager.merge(pos);
        
        if (advice != null){
        	LOSAdvice a = manager.find(LOSAdvice.class, advice.getId());
        	receiptComponent.assignAdvice(a, pos);
        } else if (idat.isAdviceMandatory()){
        	throw new InventoryException(InventoryExceptionKey.ADVICE_MANDATORY, idat.getNumber());
        } else{
        	//ok
        }
        
        if (gr.getReceiptState() == LOSGoodsReceiptState.RAW){
			receiptComponent.acceptGoodsReceipt(gr);
		}
        
        if( targetLocation != null ) {
    		try {
	    		if( isFixedLocation ) {
	    			putOnFixedAssigned( contextService.getCallerUserName(), gr.getGoodsReceiptNumber(), ul, targetLocation);
	    		}
	    		else {
					storageService.transferUnitLoad( contextService.getCallerUserName(), targetLocation, ul, -1, false, null, null);
	    		}
			} catch (FacadeException e) {
				logger.error("Error in Transfer to target="+targetLocation.getName());
				throw e;
			}
        }
        else if( targetUnitLoad != null ) {
        	try {
				inventoryComponent.transferStock(ul, targetUnitLoad, gr.getGoodsReceiptNumber(), false);
				storageService.sendToNirwana( contextService.getCallerUserName(), ul);
				ul = targetUnitLoad;
			} catch (FacadeException e) {
				logger.error("Error in Transfer to target="+targetUnitLoadName);
				throw e;
			}
        }
        
        if(printLabel){
        	if( printer == null ) {
	            printer = propertyService.getString(c, null, LOSInventoryPropertyKey.GOODS_RECEIPT_PRINTER);
        	}
        	
            StockUnitLabel label = suLabelReport.generateStockUnitLabel(ul);
            boolean storeLabel = propertyService.getBooleanDefault(LOSInventoryPropertyKey.STORE_GOODS_RECEIPT_LABEL, false);
            if( storeLabel ) {
            	suLabelReport.storeStockUnitLabel(label);
            }
        	printService.print(printer, label.getDocument(), label.getType());
        }
	        
		manageGrService.onGoodsReceiptPositionCollected(pos);

        return pos;
    }

    public LOSGoodsReceiptPosition createGoodsReceiptPositionAndLot(
            BODTO<Client> client,
            LOSGoodsReceipt gr,
            String lotName,
            Date validFrom,
            Date validTo,
            boolean expireLot,
            BODTO<ItemData> item,
            String unitLoadLabel,
            BODTO<UnitLoadType> unitLoadType,
            BigDecimal amount,
            BODTO<LOSAdvice> advice,
            LOSGoodsReceiptType receiptType,
            int lock,
            String lockCause) throws FacadeException, ReportException {
    	
    	Lot l;
    	BODTO<Lot> lot;
    	Client c;
    	ItemData idat;
    	
    	c = manager.find(Client.class, client.getId());
    	idat = manager.find(ItemData.class, item.getId());
    	
    	
    	try {
			l = lotService.getByNameAndItemData(c, lotName, idat.getNumber());
		} catch (EntityNotFoundException e) {
			l = null;
		}

    	if (l != null){
    		logger.error("Lot already exists. Will tak eexisting " + l.toDescriptiveString());
    		lot = new BODTO<Lot>(l.getId(), l.getVersion(), l.toUniqueString());
    	} else{
    		lot = manageInventoryFacade.createLot(client, item, lotName, validFrom, validTo, expireLot);
    	}
    	
    	return createGoodsReceiptPosition(client, 
    			gr, lot, item, unitLoadLabel, unitLoadType, amount,
    			advice, receiptType, lock, lockCause
    			);
    }
    
    /**
     * 
     * @param c
     * @param sl
     * @param type
     * @param ref if null, take a generated labelId
     * @return
     * @throws de.linogistix.los.location.exception.LOSLocationException
     */
    public BODTO<UnitLoad> getOrCreateUnitLoad(BODTO<Client> c,
            BODTO<StorageLocation> sl, BODTO<UnitLoadType> type, String ref) throws FacadeException {

        UnitLoad ul;
        Client cl;
        if (c != null){
            cl = manager.find(Client.class, c.getId());
        } else{
          
            cl = clientQuery.getSystemClient();
           
        }

        if (cl == null) {
            throw new NullPointerException("Client must not be null");
        }
        if (sl == null) {
            throw new NullPointerException("StorageLocation must not be null");
        }
        if (type == null) {
            throw new NullPointerException("UnitLoadType must not be null");
        }
        UnitLoadType ulType = manager.find(UnitLoadType.class, type.getId());
        if (ref == null) {
        	ref = genService.generateUnitLoadLabelId(cl, ulType);
        }

        StorageLocation storLoc = manager.find(StorageLocation.class, sl.getId());

        ul = receiptComponent.getOrCreateUnitLoad(cl, storLoc, ulType, ref);
        
        return new BODTO<UnitLoad>(ul.getId(), ul.getVersion(), ul.getLabelId());
    }

    public BODTO<UnitLoadType> getUnitLoadType(BODTO<StorageLocation> sl) {
        StorageLocation storLoc = manager.find(StorageLocation.class, sl.getId());
        UnitLoadType type;
        if (storLoc.getCurrentTypeCapacityConstraint() != null){
        	type = storLoc.getCurrentTypeCapacityConstraint().getUnitLoadType();
        } else{
        	
			type = queryUltService.getDefaultUnitLoadType();
			if (type == null ) throw new RuntimeException("Default unit load type not found ");
			
        }
        return new BODTO<UnitLoadType>(type.getId(), type.getVersion(), type.getName());
    }

    public void cancelGoodsReceipt(LOSGoodsReceipt r) throws FacadeException {
        r = manager.find(LOSGoodsReceipt.class, r.getId());
        receiptComponent.cancel(r);
    }

    public void acceptGoodsReceipt(LOSGoodsReceipt r) throws InventoryException {
    	LOSGoodsReceipt foundGr = manager.find(r.getClass(), r.getId());

        if (foundGr == null) {
            throw new InventoryException(InventoryExceptionKey.CREATE_GOODSRECEIPT, r.getGoodsReceiptNumber());
        }

        receiptComponent.acceptGoodsReceipt(foundGr);

    }

    public void finishGoodsReceipt(LOSGoodsReceipt r) throws InventoryException, InventoryTransferException {
    	LOSGoodsReceipt foundGr = manager.find(r.getClass(), r.getId());

        if (foundGr == null) {
            throw new InventoryException(InventoryExceptionKey.CREATE_GOODSRECEIPT, r.getGoodsReceiptNumber());
        }

        receiptComponent.finishGoodsReceipt(foundGr);

    }
    
    public void assignLOSAdvice(LOSAdvice adv, LOSGoodsReceiptPosition pos) throws FacadeException {

        if (adv == null) {
            throw new InventoryException(InventoryExceptionKey.POSITION_NO_ADVICE, new Object[]{pos.getPositionNumber()});
        }

        LOSAdvice advice = manager.find(LOSAdvice.class, adv.getId());
        LOSGoodsReceiptPosition position = manager.find(LOSGoodsReceiptPosition.class, pos.getId());
        receiptComponent.assignAdvice(advice, position);
    }

    public void removeGoodsReceiptPosition(LOSGoodsReceipt r, LOSGoodsReceiptPosition pos) throws InventoryException, FacadeException {
        pos = manager.find(LOSGoodsReceiptPosition.class, pos.getId());
        r = manager.find(LOSGoodsReceipt.class, r.getId());
        receiptComponent.remove(r, pos);

    }

    /** assignes an advice {@link LOSAdvice} to  the {@link LOSGoodsReceipt}*/
	public void assignLOSAdvice(BODTO<LOSAdvice> to, LOSGoodsReceipt r)
			throws InventoryException {
		LOSAdvice adv = manager.find(LOSAdvice.class, to.getId());
		r = manager.find(LOSGoodsReceipt.class, r.getId());
		receiptComponent.assignAdvice(adv, r);
		if (r.getReceiptState() == LOSGoodsReceiptState.RAW){
			receiptComponent.acceptGoodsReceipt(r);
		}
	}

	/** removes an advice {@link LOSAdvice} from  the {@link LOSGoodsReceipt}*/
	public void removeAssigendLOSAdvice(BODTO<LOSAdvice> to, LOSGoodsReceipt r)
			throws InventoryException {
		LOSAdvice adv = manager.find(LOSAdvice.class, to.getId());
		receiptComponent.removeAssignedAdvice(adv, r);
		
	}

	public Lot createLot(Client client, String lotName, ItemData item, Date validTo) 
	{
		client = manager.merge(client);
		item = manager.merge(item);
		
		Lot lot = queryLotService.getByNameAndItemData(lotName, item);
		
		if(lot != null){
			return lot;
		}
		else{
			lot = lotService.create(client, item, lotName, new Date(), null, validTo);
			return lot;
		}
	}

	public void createStockUnitLabel(LOSGoodsReceipt rec, String printer) 	throws FacadeException {
		rec = manager.find(LOSGoodsReceipt.class, rec.getId());
		for( LOSGoodsReceiptPosition pos : rec.getPositionList() ) {
			createStockUnitLabel(pos, printer);
		}
	}
	
	public StockUnitLabel createStockUnitLabel(LOSGoodsReceiptPosition pos, String printer) 	throws FacadeException {
		String logStr = "createStockUnitLabel ";

		pos = manager.find(LOSGoodsReceiptPosition.class, pos.getId());

		StockUnit stock = pos.getStockUnit();

		if( stock == null ) {
			String s = pos.getStockUnitStr();
			if( !StringTools.isEmpty(s) ) {
				try {
					Long l = Long.valueOf(s);
					stock = manager.find(StockUnit.class, l);
				}
				catch( Throwable t ) {}
			}
		}
		if( stock == null ) {
			logger.info(logStr+"Position has no stock unit. => Cannot print label");
			throw new InventoryException(InventoryExceptionKey.GRPOS_HAS_NO_STOCK, "");
		}
		
		UnitLoad unitLoad = stock.getUnitLoad();
		
    	StockUnitLabel label = suLabelReport.generateStockUnitLabel(unitLoad);
        boolean storeLabel = propertyService.getBooleanDefault(LOSInventoryPropertyKey.STORE_GOODS_RECEIPT_LABEL, false);
        if( storeLabel ) {
        	suLabelReport.storeStockUnitLabel(label);
        }
    	printService.print(printer, label.getDocument(), label.getType());
    	
		return label;
		
	}

	public boolean checkSerialNumber(String serialNo, ItemData itemData) {
		if( itemData.getSerialNoRecordType() != SerialNoRecordType.ALWAYS_RECORD ) {
			return true;
		}
		if( serialNo == null || serialNo.length()==0 ) {
			return false;
		}
		
		List<StockUnit> list = stockUnitService.getBySerialNumber(itemData, serialNo);
		for (StockUnit su : list) {
			if (BigDecimal.ZERO.compareTo(su.getAmount()) < 0) {
				return false;
			}
		}

		return true;
	}

	
	
	protected void putOnFixedAssigned( String userName, String activityCode, UnitLoad unitload, StorageLocation targetLocation) throws InventoryException,
			FacadeException {
		
		// To picking? (implicitly if virtual ul type)
		UnitLoadType virtual = queryUltService.getPickLocationUnitLoadType();
		
		if (targetLocation.getUnitLoads() != null && targetLocation.getUnitLoads().size() > 0) {
			// There is aready a unit load on the destination. => Add stock
			
			UnitLoad onDestination = targetLocation.getUnitLoads().get(0);
			
			inventoryComponent.transferStock(unitload, onDestination, activityCode, false);
			storageService.sendToNirwana( contextService.getCallerUserName(), unitload);
			logger.info("Transferred Stock to virtual UnitLoadType: "+unitload.toShortString());
		} else {
			unitload.setType(virtual);
			unitload.setLabelId(targetLocation.getName());
			storageService.transferUnitLoad( userName, targetLocation, unitload, -1, false, null, null);

		}
	}

	public void createStock(
		    String clientNumber,
		    String lotName,
		    String itemDataNumber,
		    String unitLoadLabel,
		    String unitLoadTypeName, 
		    BigDecimal amount,
		    int lock, String lockCause, 
		    String serialNumber, 
		    String targetLocationName, 
		    String targetUnitLoadName) throws FacadeException {

        UnitLoad ul;
        
        Client client = null;
		try {
			client = queryClientService.getByNumber(clientNumber);
		} catch (UnAuthorizedException e2) {
		}
        if (client == null){
        	throw new InventoryException(InventoryExceptionKey.NO_SUCH_CLIENT, clientNumber);
        }
        
        
        ItemData idat = queryItemDataService.getByItemNumber(client, itemDataNumber);
        if (idat == null){
        	throw new InventoryException(InventoryExceptionKey.ITEMDATA_NOT_FOUND, itemDataNumber);
        }
        
        UnitLoadType ulType;
        
        if (unitLoadTypeName != null){
        	ulType = queryUltService.getByName(unitLoadTypeName);
        } else{
        	ulType = queryUltService.getDefaultUnitLoadType();
        }
                
        List<StorageLocation> sls = receiptComponent.getGoodsReceiptLocations();
        StorageLocation sl = null;
        if( sls.size()<1 ) {
        	throw new InventoryException(InventoryExceptionKey.NO_GOODS_RECEIPT_LOCATION, "");
        }
    	sl = sls.get(0);
        
        
        Lot lot = null;
        if (lotName != null){
        	try {
				lot = lotService.getByNameAndItemData(client, lotName, itemDataNumber);
			} catch (EntityNotFoundException e) {
			}
        	if (lot == null){
            	throw new InventoryException(InventoryExceptionKey.NO_LOT_WITH_NAME, lotName);
            }
        } else if (idat.isLotMandatory()){
        	throw new InventoryException(InventoryExceptionKey.LOT_MANDATORY, idat.getNumber());
        }
        
        boolean printLabel = true;
        boolean isFixedLocation = false;
        StorageLocation targetLocation = null;
        UnitLoad targetUnitLoad = null;
        if( targetLocationName != null && targetLocationName.length()>0 ) {
        	try {
				targetLocation = locService.getByName(targetLocationName);
			} catch (Exception e) {
			}
			if( targetLocation == null ) {
				throw new InventoryException(InventoryExceptionKey.NO_SUCH_STORAGELOCATION, targetLocationName);
			}
			
			// Check, whether the target location is a fixed assigned location.
			// On this locations a special handling is needed, which the posting methods do not know??
			LOSFixedLocationAssignment fix = queryFixService.getByLocation(targetLocation);
			if( fix != null ) {
				if( ! fix.getItemData().equals(idat) ) {
					logger.error("Cannot store item data="+ idat.getNumber()+" on fixed location for item data="+fix.getItemData().getNumber());
					throw new InventoryException(InventoryExceptionKey.WRONG_ITEMDATA, new Object[]{idat.getNumber(),fix.getItemData().getNumber()});
				}
				printLabel = false;
				isFixedLocation = true;
			}
        }
        if( targetUnitLoadName != null && targetUnitLoadName.length()>0 ) {
        	try {
				targetUnitLoad = losUlService.getByLabelId(client, targetUnitLoadName);
 			} catch (Exception e) {
			}
			if( targetUnitLoad == null ) {
				throw new InventoryException(InventoryExceptionKey.NO_SUCH_UNITLOAD, targetUnitLoadName);
			}
        }
        
        
        boolean printLabelAutomatically = propertyService.getBooleanDefault(LOSInventoryPropertyKey.PRINT_GOODS_RECEIPT_LABEL, false);
        if( ! printLabelAutomatically ) {
        	printLabel = false;
        }
        
        try {
	        if (unitLoadLabel != null && unitLoadLabel.length() > 0) {
	        	
	        	printLabel = false;
	        	
	        	try{
	        		ul = losUlService.getByLabelId(client, unitLoadLabel);
	        		
	        		throw new InventoryException(InventoryExceptionKey.UNIT_LOAD_EXISTS, 
	        									 new Object[]{unitLoadLabel}); 
	        		
	        	}catch(EntityNotFoundException enf){ 
	                ul = receiptComponent.getOrCreateUnitLoad(client, sl, ulType, unitLoadLabel);
	        	}
	        } 
	        else {
		        if( targetUnitLoad != null ) {
		        	printLabel = false;
		        }
		        
	        	// do not use the label twice
	        	String label = null;
	        	int i = 0;
	        	while( i++ < 100 ) {
	        		label = genService.generateUnitLoadLabelId(client, ulType);
	        		try {
						losUlService.getByLabelId(client, label);
					} catch (EntityNotFoundException e) {
						break;
					}
	        		logger.info("UnitLoadLabel " + label + " already exists. Try the next");
	        	}

                ul = receiptComponent.getOrCreateUnitLoad(client, sl, ulType, unitLoadLabel);
	        }     
	        
        } catch (InventoryException ex){
        	throw ex;
        } catch (Throwable ex) {
            logger.error(ex, ex);
            throw new InventoryException(InventoryExceptionKey.CREATE_UNITLOAD, new Object[0]);
        }
        
		StockUnit su = inventoryComponent.createStock(client, lot, idat,
				amount, null, ul, "IMAN", serialNumber, null, true);

        if(lock > 0){
	        try {
				suCrud.lock(su, lock, lockCause);
			} catch (BusinessObjectSecurityException e1) {
				logger.error(e1.getMessage(), e1);
				throw new InventoryException(InventoryExceptionKey.STOCKUNIT_CONSTRAINT_VIOLATED, su.toUniqueString());
			}
        }
        
        manager.flush();
        
        if( targetLocation != null ) {
    		try {
	    		if( isFixedLocation ) {
	    			putOnFixedAssigned( contextService.getCallerUserName(), "IMAN", ul, targetLocation);
	    		}
	    		else {
					storageService.transferUnitLoad( contextService.getCallerUserName(), targetLocation, ul, -1, false, null, null);
	    		}
			} catch (FacadeException e) {
				logger.error("Error in Transfer to target="+targetLocation.getName());
				throw e;
			}
        }
        else if( targetUnitLoad != null ) {
        	try {
				inventoryComponent.transferStock(ul, targetUnitLoad, "IMAN", false);
				storageService.sendToNirwana( contextService.getCallerUserName(), ul);
			} catch (FacadeException e) {
				logger.error("Error in Transfer to target="+targetUnitLoadName);
				throw e;
			}
        }
        
        if(printLabel){
        	StockUnitLabel label = suLabelReport.generateStockUnitLabel(ul);
            boolean storeLabel = propertyService.getBooleanDefault(LOSInventoryPropertyKey.STORE_GOODS_RECEIPT_LABEL, false);
            if( storeLabel ) {
            	suLabelReport.storeStockUnitLabel(label);
            }
        	printService.print(null, label.getDocument(), label.getType());
        }

	}

	public String getOldestLocationToAdd( ItemData itemData, Lot lot ) throws FacadeException {
		Client callersClient = contextService.getCallersClient();

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT loc.name, su.id, su.amount, su.created ");
		if( lot != null ) {
			sb.append(", lot.bestBeforeEnd ");
		}
		sb.append("FROM ");
		sb.append(StockUnit.class.getSimpleName()+" su ");
		sb.append(" JOIN su.unitLoad ul  ");
		sb.append(" JOIN su.unitLoad.storageLocation loc  ");
		if( lot != null ) {
			sb.append("JOIN su.lot lot ");
		}
		sb.append(" WHERE su.lock=0 ");
		sb.append(" and su.itemData=:itemData ");
		sb.append(" and su.amount>0 ");
        if (!callersClient.isSystemClient()) {
            sb.append("AND su.client = :cl ");
        }
		sb.append(" and loc.lock=0 ");
		sb.append(" and ul.lock=0 ");
		if( lot != null ) {
			sb.append(" and lot=:lot ");
			sb.append(" and lot.lock=0 ");
		}

		sb.append(" ORDER BY su.amount, su.created ");

		
		logger.debug("Search add location: "+sb.toString());
		
		Query query = manager.createQuery(sb.toString());

		query.setParameter("itemData", itemData);
		
        if (!callersClient.isSystemClient()) {
        	query.setParameter("cl", callersClient);
        }
		if( lot != null ) {
        	query.setParameter("lot", lot);
        }

        query.setMaxResults(1);
        Object o = null;
        try {
        	o = query.getSingleResult();
        }
        catch(NoResultException e){}
        if( o == null ) {
        	return null;
        }
        Object[] oa = (Object[])o;
        
        return (String)oa[0];
	}

}
