/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.User;

import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.inventory.customization.ManageStorageService;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSStorageRequest;
import de.linogistix.los.inventory.model.LOSStorageRequestState;
import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.inventory.service.StockUnitService;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.query.ClientQueryRemote;
import de.linogistix.los.util.businessservice.ContextService;
import de.wms2.mywms.inventory.InventoryBusiness;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.StockUnitEntityService;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.inventory.UnitLoadTypeEntityService;
import de.wms2.mywms.location.Area;
import de.wms2.mywms.location.AreaUsages;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.strategy.FixAssignment;
import de.wms2.mywms.strategy.FixAssignmentEntityService;
import de.wms2.mywms.strategy.LocationFinder;
import de.wms2.mywms.strategy.LocationReserver;
import de.wms2.mywms.strategy.StorageStrategy;

/**
 * 
 * @author trautm
 */
@Stateless
public class StorageBusinessBean implements StorageBusiness {

	private static final Logger log = Logger
			.getLogger(StorageBusinessBean.class);
	@EJB
	private InventoryGeneratorService genService;
	@EJB
	private ClientQueryRemote clQuery;
	@EJB
	private StockUnitService suService;
	@EJB
	private FixAssignmentEntityService fixService;
	@EJB
	private EntityGenerator entityGenerator;
	@EJB
	private ManageStorageService manageStorageService;
	@EJB
	private ContextService contextService;

	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;
	
	@EJB
	private LocationFinder locationFinder;
	@Inject
	private UnitLoadTypeEntityService unitLoadTypeService;
	@Inject
	private LocationReserver locationReserver;
	@Inject
	private InventoryBusiness inventoryBusiness;
	@Inject
	private StockUnitEntityService stockUnitEntityService;

	public LOSStorageRequest getOrCreateStorageRequest(Client c, UnitLoad ul) throws FacadeException {
		return getOrCreateStorageRequest(c, ul, false, null, null);
	}
	public LOSStorageRequest getOrCreateStorageRequest(Client c, UnitLoad ul, boolean startProcessing) throws FacadeException {
		return getOrCreateStorageRequest(c, ul, startProcessing, null, null);
	}
	/**
	 * 
	 * The created request contains no destination information yet
	 * 
	 * @param c
	 * @param ul
	 * @param location, if null a location will be searched
	 * @param strategy, if null the default will be used
	 * @return
	 * @throws de.linogistix.los.inventory.exception.InventoryException
	 * @throws org.mywms.service.EntityNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public LOSStorageRequest getOrCreateStorageRequest(Client c, UnitLoad ul, boolean startProcessing, StorageLocation location, StorageStrategy strategy)
			throws FacadeException {
		LOSStorageRequest ret;

		Query query = manager.createQuery("SELECT req FROM "
				+ LOSStorageRequest.class.getSimpleName() + " req "
				+ " WHERE req.unitLoad.labelId=:label "
				+ " AND req.requestState in (:rstate, :processing) ");

		query.setParameter("label", ul.getLabelId());
		query.setParameter("rstate", LOSStorageRequestState.RAW);
		query.setParameter("processing", LOSStorageRequestState.PROCESSING);

		List<LOSStorageRequest> list = query.getResultList();
		if (list == null || list.size() < 1) {
			ret = entityGenerator.generateEntity(LOSStorageRequest.class);
			ret.setUnitLoad(ul);
			ret.setRequestState(startProcessing?LOSStorageRequestState.PROCESSING:LOSStorageRequestState.RAW);
			ret.setNumber(genService.generateStorageRequestNumber(c));
			
			if( location == null ) {
				location = locationFinder.findStorageLocation(ul, strategy);
			}
			if( location == null ) {
				throw new InventoryException(
						InventoryExceptionKey.STORAGE_NO_DESTINATION_FOUND,
						new Object[] { ul.getLabelId() });
			}
			ret.setDestination(location);
			locationReserver.allocateLocation(location, ul);
			
			ret.setClient(c);
			manager.persist(ret);
			
			manageStorageService.onStorageRequestStateChange(ret, null);
			
			log.info("CREATED LOSStorageRequest for " + ul.getLabelId());
			return ret;
		} else {
			log.warn("FOUND existing LOSStorageRequest for " + ul.getLabelId());
			return list.get(0);
		}

	}

	@SuppressWarnings("unchecked")
	public LOSStorageRequest getOpenStorageRequest( String unitLoadLabel ) {

		Query query = manager.createQuery("SELECT req FROM "
				+ LOSStorageRequest.class.getSimpleName() + " req "
				+ " WHERE req.unitLoad.labelId=:label "
				+ " AND req.requestState in (:raw,:processing) ");

		query.setParameter("label", unitLoadLabel);
		query.setParameter("raw", LOSStorageRequestState.RAW);
		query.setParameter("processing", LOSStorageRequestState.PROCESSING);

		List<LOSStorageRequest> list = query.getResultList();
		if( list.size() > 0 ) {
			return list.get(0);
		}
		return null;
	}


	public void finishStorageRequest(LOSStorageRequest req,
			StorageLocation sl, boolean force) throws FacadeException,
			LOSLocationException {
		// transfer UnitLoad
		UnitLoad ul;
		StorageLocation assigned;
		boolean partialProcessed = false;
		
		ul = manager.find(UnitLoad.class, req.getUnitLoad().getId());
		sl = manager.find(StorageLocation.class, sl.getId());
		req = manager.find(LOSStorageRequest.class, req.getId());
		
		LOSStorageRequestState stateOld = req.getRequestState();
		
		assigned = manager.find(StorageLocation.class, req.getDestination().getId());
		if (!(sl.equals(assigned))) {
			locationReserver.checkAllocateLocation(sl, req.getUnitLoad(), force, true);
				
			// Do not ask questions in front-end.
			// Do not place front-end logic into back-end
			force = true;
			
			if (force){
				// Just to disable locationReservers repair function on empty locations
				req.setRequestState(LOSStorageRequestState.TERMINATED);

				if( !ul.getStorageLocation().equals(sl) ) {
					transferUnitLoad( req, ul, sl );
				}
				locationReserver.deallocateLocation(assigned, ul);
				locationReserver.allocateLocation(sl, ul);
			} else{
				throw new InventoryException(
						InventoryExceptionKey.STORAGE_WRONG_LOCATION_BUT_ALLOWED,
						sl.getName());
			}

			Area area = sl.getArea();
			if( area != null && area.isUseFor(AreaUsages.TRANSFER) ) {
				// Do not finish the storage order on a transfer location
				req.setRequestState(LOSStorageRequestState.PROCESSING);
				partialProcessed = true;
			} 
			else {
				req.setRequestState(LOSStorageRequestState.TERMINATED);
				req.setDestination(sl);
			}

		} else {
			req.setRequestState(LOSStorageRequestState.TERMINATED);
			if( !ul.getStorageLocation().equals(assigned) ) {
				transferUnitLoad( req, ul, assigned );
			}
		}

		if( stateOld != req.getRequestState() ) {
			manageStorageService.onStorageRequestStateChange(req, stateOld);
		}
		
		if( partialProcessed ) {
			manageStorageService.onStorageRequestPartialProcessed(req);
		}

	}

	private void transferUnitLoad(LOSStorageRequest req, UnitLoad unitload, StorageLocation targetLocation ) throws FacadeException {
		
		FixAssignment fix = fixService.readFirstByLocation(targetLocation);
		User operator = contextService.getCallersUser();

		if( fix != null ) {
			for( StockUnit su : unitload.getStockUnitList() ) {
				if( !su.getItemData().equals(fix.getItemData()) ) {
					throw new InventoryException(InventoryExceptionKey.STORAGE_WRONG_LOCATION_NOT_ALLOWED, targetLocation.getName());
				}
			}
			
			if (targetLocation.getUnitLoads() != null && targetLocation.getUnitLoads().size() > 0) {
				// There is already a unit load on the destination. => Add stock
				UnitLoad targetUl = null;
				for( UnitLoad ul : targetLocation.getUnitLoads() ) {
					if( ul.getLabelId().equals(targetLocation.getName() ) ) {
						targetUl = ul;
						break;
					}
				}
				if( targetUl == null ) {
					targetUl = targetLocation.getUnitLoads().get(0);
				}

				for(StockUnit stock:unitload.getStockUnitList()) {
					inventoryBusiness.transferStock(stock, targetUl, null, stock.getState(), req.getNumber(), operator, null);
				}

				log.info("Transferred Stock to virtual UnitLoadType: "+unitload.toShortString());
			} else {
				UnitLoadType virtual = unitLoadTypeService.getVirtual();
				unitload.setType(virtual);
				unitload.setLabelId(targetLocation.getName());
				inventoryBusiness.transferUnitLoad(unitload, targetLocation, req.getNumber(), operator, null);
			}
		} 
		else {
			inventoryBusiness.transferUnitLoad(unitload, targetLocation, req.getNumber(), operator, null);
		}
	}
	
	
	public void finishStorageRequest(LOSStorageRequest req,
			UnitLoad destination) throws FacadeException {
		
		// zuschuetten
		UnitLoad from = manager.find(UnitLoad.class, req.getUnitLoad().getId());
		destination = manager.find(UnitLoad.class, destination.getId()); 
		req = manager.find(LOSStorageRequest.class, req.getId());
		
		LOSStorageRequestState stateOld = req.getRequestState();

		req.setRequestState(LOSStorageRequestState.TERMINATED);

		for(StockUnit stock:from.getStockUnitList()) {
			inventoryBusiness.transferStock(stock, destination, null, stock.getState(), req.getNumber(), null, null);
		}

		StorageLocation orig = manager.find(StorageLocation.class, req.getDestination().getId());
		locationReserver.deallocateLocation(orig, from);
		
		// if UnitLoad empty, send to nirwana
		if(!stockUnitEntityService.existsByUnitLoad(req.getUnitLoad())){
			UnitLoad u = manager.find(UnitLoad.class, req.getUnitLoad().getId());
			inventoryBusiness.deleteUnitLoad(u, req.getNumber(), null, null);
		}
		
		if( stateOld != req.getRequestState() ) {
			manageStorageService.onStorageRequestStateChange(req, stateOld);
		}
		
	}

	// ------------------------------------------------------------------------


	public Client determineClient(UnitLoad ul) {
		Client systemCl;

		systemCl = clQuery.getSystemClient();

		Client ret = systemCl;

		if (!ul.getClient().equals(systemCl)) {
			ret = ul.getClient();
			return ret;
		} else {
			ret = null;
			for (StockUnit u : suService.getListByUnitLoad(ul)) {
				if (ret == null || ret.equals(u.getClient())) {
					ret = u.getClient();
				} else {
					// StockUnits of different clients - take system client
					ret = systemCl;
					return ret;
				}
			}
		}

		if (ret == null) {
			ret = systemCl;
		}

		return ret;
	}


	public void cancelStorageRequest(LOSStorageRequest req) throws FacadeException {

		LOSStorageRequestState stateOld = req.getRequestState();

		req.setRequestState(LOSStorageRequestState.CANCELED);

		if( req.getDestination() != null && req.getUnitLoad() != null ) {
			locationReserver.deallocateLocation(req.getDestination(), req.getUnitLoad());
		}
		else {
			log.warn("cancelStorageRequest: cannot release reservation of location. destination="+req.getDestination()+", unitLoad="+req.getUnitLoad());
		}
		
		if( stateOld != req.getRequestState() ) {
			manageStorageService.onStorageRequestStateChange(req, stateOld);
		}
		
	}

	public void removeStorageRequest(LOSStorageRequest req) throws FacadeException {

		LOSStorageRequestState stateOld = req.getRequestState();

		req.setRequestState(LOSStorageRequestState.CANCELED);

		if( req.getDestination() != null && (stateOld == LOSStorageRequestState.RAW || stateOld == LOSStorageRequestState.PROCESSING) ) {
			locationReserver.deallocateLocation( req.getDestination(), req.getUnitLoad() );
		}
		else {
			log.warn("removeStorageRequest: cannot release reservation of location. destination="+req.getDestination()+", unitLoad="+req.getUnitLoad()+", state="+stateOld);
		}
		
		if( stateOld != req.getRequestState() ) {
			manageStorageService.onStorageRequestStateChange(req, stateOld);
		}
		
		manager.remove(req);
	}

}
