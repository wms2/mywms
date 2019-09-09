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
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.service.LOSStorageRequestService;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.exception.LOSLocationExceptionKey;
import de.linogistix.los.location.query.UnitLoadQueryRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadEntityService;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.location.StorageLocationEntityService;
import de.wms2.mywms.strategy.OrderState;
import de.wms2.mywms.transport.TransportBusiness;
import de.wms2.mywms.transport.TransportOrder;
import de.wms2.mywms.transport.TransportOrderEntityService;
import de.wms2.mywms.transport.TransportOrderType;

/**
 *
 * @author trautm
 */
@Stateless
public class StorageFacadeBean implements StorageFacade {

    private static final Logger log = Logger.getLogger(StorageFacadeBean.class);
    @EJB
    private LOSStorageRequestService storageService;
    
    @EJB
    private UnitLoadQueryRemote ulQuery;
	
	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;

	@Inject
	private StorageLocationEntityService locationService;
	@Inject
	private TransportOrderEntityService transportOrderService;
	@Inject
	private TransportBusiness transportBusiness;
	@Inject
	private UnitLoadEntityService unitLoadService;


    public TransportOrder getStorageRequest(String labelId, boolean startProcessing) throws FacadeException {
		UnitLoad unitLoad = unitLoadService.readByLabel(labelId);
		if (unitLoad == null) {
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_UNITLOAD, labelId);
		}

		TransportOrder transport = transportOrderService.readFirstOpen(unitLoad);
		if (transport == null) {
			transport = transportBusiness.createOrder(unitLoad, null, TransportOrderType.INBOUND, null);
		}
		if (startProcessing && transport.getState() < OrderState.STARTED) {
			transportBusiness.startOperation(transport, null);
		}

		return transport;
    }

    public void finishStorageRequest(String srcLabel, String destination, boolean addToExisting, boolean overwrite) throws InventoryException, LOSLocationException, FacadeException {
    	String logStr = "finishStorageRequest ";
    	log.debug(logStr+" label="+srcLabel+", destination="+destination);
    	
        StorageLocation sl = null;
        UnitLoad ul;
                 
        TransportOrder request = getStorageRequest(srcLabel, false);
       
        if (addToExisting){
	        //try unit load first
        	try {
	            ul = ulQuery.queryByIdentity(destination);
	            
	            // Read into hibernate context. The remote query gives a detached entity
	            ul = manager.find(UnitLoad.class, ul.getId());
	            
	            try{
//	            	sl = locQuery.queryByIdentity(destination);
	            	sl = locationService.read(destination);
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
	            
	            if( request.getUnitLoad() != null ) {
	            	if( request.getUnitLoad().equals(ul) ) {
	            		log.info("The target unit load cannot be the source unit load: " + srcLabel);
	            		throw new InventoryException(InventoryExceptionKey.UNIT_LOAD_EXISTS, "");
	            	}
	            }
	            transportBusiness.confirmOrder(request, ul);
	        } catch (BusinessObjectNotFoundException ex) {
	        	try { 
//	            	sl = locQuery.queryByIdentity(destination);
	            	sl = locationService.read(destination);
	            	if( sl == null ) {
	            		throw new BusinessObjectNotFoundException();
	            	}
	            	if (sl.getUnitLoads().size() == 1){
	            		ul = sl.getUnitLoads().get(0);
	            		transportBusiness.confirmOrder(request, ul);
	            	} else if (sl.getUnitLoads().size() == 0){
	            		transportBusiness.confirmOrder(request, sl);
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
	        	sl = locationService.read(destination);
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
	        	transportBusiness.confirmOrder(request, sl);
	        } else {
	            log.warn("No StorageLocation found: " + destination + ". Test for UnitLoad");
	            try {
	                ul = ulQuery.queryByIdentity(destination);
	            } catch (BusinessObjectNotFoundException ex) {
	                throw new InventoryException(InventoryExceptionKey.STORAGE_WRONG_LOCATION_NOT_ALLOWED, srcLabel);
	            }
	            if (addToExisting) {
		            // Read into hibernate context. The remote query gives a detached entity
		            ul = manager.find(UnitLoad.class, ul.getId());

	            	transportBusiness.confirmOrder(request, ul);
	            } else {
	                throw new InventoryException(InventoryExceptionKey.STORAGE_ADD_TO_EXISTING, destination);
	            }
	        }
        }
    }
    
    
    public void cancelStorageRequest(String unitLoadLabel) throws FacadeException {
		UnitLoad unitLoad = unitLoadService.readByLabel(unitLoadLabel);
		if (unitLoad == null) {
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_UNITLOAD, unitLoadLabel);
		}

		TransportOrder transport = transportOrderService.readFirstOpen(unitLoad);
		if (transport == null) {
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_UNITLOAD, unitLoadLabel);
		}

		transportBusiness.cancelOrder(transport);
    }

    public void cancelStorageRequest(BODTO<TransportOrder> r) throws FacadeException {		
		if (r == null) {
			return;
		}

		TransportOrder req = manager.find(TransportOrder.class, r.getId());
		if (req == null) {
			return;
		}
		
		transportBusiness.cancelOrder(req);
    }
    
    public void removeStorageRequest(BODTO<TransportOrder> r) throws FacadeException {		
		if (r == null) {
			return;
		}

		TransportOrder req = manager.find(TransportOrder.class, r.getId());
		if (req == null) {
			return;
		}
		
		transportBusiness.removeOrder(req);
    }

}
