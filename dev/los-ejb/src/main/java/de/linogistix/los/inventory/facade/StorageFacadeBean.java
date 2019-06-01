/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;

import de.linogistix.los.inventory.businessservice.StorageBusiness;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSStorageRequest;
import de.linogistix.los.inventory.service.LOSStorageRequestService;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.exception.LOSLocationExceptionKey;
import de.linogistix.los.location.query.UnitLoadQueryRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.StorageLocation;

/**
 *
 * @author trautm
 */
@Stateless
public class StorageFacadeBean implements StorageFacade {

    private static final Logger log = Logger.getLogger(StorageFacadeBean.class);
    @EJB
    private StorageBusiness storage;
//    @EJB
//    private LOSStorageLocationQueryRemote locQuery;
//    @EJB
//    private LOSStorageRequestQueryRemote reqQuery;
    @EJB
    private LOSStorageLocationService locationService;
    @EJB
    private LOSStorageRequestService storageService;
    
    @EJB
    private UnitLoadQueryRemote ulQuery;
	
	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;

    public LOSStorageRequest getStorageRequest(String labelId, boolean startProcessing) throws FacadeException {
        try {
            LOSStorageRequest req;
            UnitLoad ul = ulQuery.queryByIdentity(labelId);
            ul = manager.find(UnitLoad.class, ul.getId());
            Client c = storage.determineClient(ul);
            req = storage.getOrCreateStorageRequest(c, ul, startProcessing);
            try {
                req = storageService.get(req.getId());
            } catch (Throwable t) {
                log.error(t,t);
            }
            
            return req;
        } catch (BusinessObjectNotFoundException ex) {
            log.error(ex.getMessage(), ex);
            throw new InventoryException(InventoryExceptionKey.STORAGE_FAILED, labelId);
        }
    }

    public void finishStorageRequest(String srcLabel, String destination, boolean addToExisting, boolean overwrite) throws InventoryException, LOSLocationException, FacadeException {
    	String logStr = "finishStorageRequest ";
    	log.debug(logStr+" label="+srcLabel+", destination="+destination);
    	
        LOSStorageRequest r;
        StorageLocation sl = null;
        UnitLoad ul;
                 
        r = getStorageRequest(srcLabel, false);
       
        if (addToExisting){
	        //try unit load first
        	try {
	            ul = ulQuery.queryByIdentity(destination);
	            
	            try{
//	            	sl = locQuery.queryByIdentity(destination);
	            	sl = locationService.getByName(destination);
	            	if( sl == null ) {
	            		throw new BusinessObjectNotFoundException();
	            	}
	            	if ( ! ul.getStorageLocation().equals(sl)){
	            		log.error("Ambigous scan of id " + destination);
	            		throw new InventoryException(InventoryExceptionKey.AMBIGUOUS_SCAN, destination);
	            	} else{
	            		log.info("A UnitLoad and StorageLocation of same name have been detected (But UnitLoad is on that StorageLocation): " + ul.toDescriptiveString());
	            	}
	            } catch (BusinessObjectNotFoundException ex) {
	            	//ok
	            }
	            
	            if( r.getUnitLoad() != null ) {
	            	if( r.getUnitLoad().equals(ul) ) {
	            		log.info("The target unit load cannot be the source unit load: " + srcLabel);
	            		throw new InventoryException(InventoryExceptionKey.UNIT_LOAD_EXISTS, "");
	            	}
	            }
	            storage.finishStorageRequest(r, ul);
	        } catch (BusinessObjectNotFoundException ex) {
	        	try { 
//	            	sl = locQuery.queryByIdentity(destination);
	            	sl = locationService.getByName(destination);
	            	if( sl == null ) {
	            		throw new BusinessObjectNotFoundException();
	            	}
	            	if (sl.getUnitLoads().size() == 1){
	            		ul = sl.getUnitLoads().get(0);
	            		storage.finishStorageRequest(r, ul);
	            	} else if (sl.getUnitLoads().size() == 0){
	            		storage.finishStorageRequest(r, sl, overwrite);
	            	} else{
	            		throw new InventoryException(InventoryExceptionKey.MUST_SCAN_STOCKUNIT, "");
	            	}
	            	
	            } catch (BusinessObjectNotFoundException bex) {
	                throw new LOSLocationException(LOSLocationExceptionKey.NO_SUCH_LOCATION, new Object[]{destination});
	            } 
//	        	throw new InventoryException(InventoryExceptionKey.STORAGE_WRONG_LOCATION_NOT_ALLOWED, srcLabel);
	        }
        }
        else{    
        	// try storage location first
	        try {
//	        	sl = locQuery.queryByIdentity(destination);
	        	sl = locationService.getByName(destination);
            	if( sl == null ) {
            		throw new BusinessObjectNotFoundException();
            	}

	        	try{
	        		ul = ulQuery.queryByIdentity(destination);
	        		if ( ! ul.getStorageLocation().equals(sl)){
	            		log.error("Ambigous scan of id " + destination);
	            		throw new InventoryException(InventoryExceptionKey.AMBIGUOUS_SCAN, destination);
	            	} else{
	            		log.info("A UnitLoad and StorageLocation of same name have been detected (But UnitLoad is on that StorageLocation): " + ul.toDescriptiveString());
	            	}
	            } catch (BusinessObjectNotFoundException ex) {
	            	//ok
	            }
	        	
	        } catch (BusinessObjectNotFoundException ex) {
//	            TODO > handle Exception
	        }
	        if (sl != null) {
	            storage.finishStorageRequest(r, sl, overwrite);
	        } else {
	            log.warn("No StorageLocation found: " + destination + ". Test for UnitLoad");
	            try {
	                ul = ulQuery.queryByIdentity(destination);
	            } catch (BusinessObjectNotFoundException ex) {
	                throw new InventoryException(InventoryExceptionKey.STORAGE_WRONG_LOCATION_NOT_ALLOWED, srcLabel);
	            }
	            if (addToExisting) {
	                storage.finishStorageRequest(r, ul);
	            } else {
	                throw new InventoryException(InventoryExceptionKey.STORAGE_ADD_TO_EXISTING, destination);
	            }
	        }
        }
    }
    
    
    public void cancelStorageRequest(String unitLoadLabel) throws FacadeException {
		
		LOSStorageRequest req = storage.getOpenStorageRequest( unitLoadLabel );
		if( req == null ) {
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_UNITLOAD, unitLoadLabel);
		}

		storage.cancelStorageRequest(req);
    }

    public void cancelStorageRequest(BODTO<LOSStorageRequest> r) throws FacadeException {		
		if (r == null) {
			return;
		}

		LOSStorageRequest req = manager.find(LOSStorageRequest.class, r.getId());
		if (req == null) {
			return;
		}
		
		storage.cancelStorageRequest(req);
    }
    
    public void removeStorageRequest(BODTO<LOSStorageRequest> r) throws FacadeException {		
		if (r == null) {
			return;
		}

		LOSStorageRequest req = manager.find(LOSStorageRequest.class, r.getId());
		if (req == null) {
			return;
		}
		
		storage.removeStorageRequest(req);
    }

}
