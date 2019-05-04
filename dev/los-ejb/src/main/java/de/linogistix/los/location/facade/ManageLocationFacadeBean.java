/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.facade;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;

import de.linogistix.los.location.businessservice.LOSStorage;
import de.linogistix.los.location.businessservice.LocationReserver;
import de.linogistix.los.location.customization.CustomLocationService;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.exception.LOSLocationExceptionKey;
import de.linogistix.los.location.model.LOSRack;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSTypeCapacityConstraint;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.query.LOSUnitLoadQueryRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.util.businessservice.ContextService;

@Stateless
@PermitAll
public class ManageLocationFacadeBean implements ManageLocationFacade {

	private static final Logger log = Logger
			.getLogger(ManageLocationFacadeBean.class);

	@EJB
	private LOSStorage storage;

	@EJB
	private LOSStorageLocationService slService;

	@EJB
	private ContextService contextService;

	@EJB
	private LOSUnitLoadQueryRemote uLoadQueryRemote;

	@EJB
	private LocationReserver locationReserver;
	@EJB
	private CustomLocationService customLocationService;

	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;

	public void releaseReservations(List<BODTO<LOSStorageLocation>> locations)
			throws FacadeException {

		if (locations == null) {
			return;
		}

		for (BODTO<LOSStorageLocation> storLoc : locations) {
			LOSStorageLocation sl = manager.find(LOSStorageLocation.class,
					storLoc.getId());
			if (sl == null) {
				log.warn("Not found: " + storLoc.getName());
				continue;
			}
			locationReserver.deallocateLocationComplete(sl);
		}
	}

	public void sendUnitLoadToNirwana(String labelId) throws FacadeException {

		LOSUnitLoad u = uLoadQueryRemote.queryByIdentity(labelId);
		manager.find(LOSUnitLoad.class, u.getId());
		LOSStorageLocation nirwana = slService.getNirwana();
		storage.transferUnitLoad(contextService.getCallerUserName(), nirwana, u);

	}

	public void sendUnitLoadToNirwana(List<BODTO<LOSUnitLoad>> list)
			throws FacadeException {
		if (list == null) {
			return;
		}

		LOSStorageLocation nirwana = slService.getNirwana();
		for (BODTO<LOSUnitLoad> ul : list) {
			LOSUnitLoad u = manager.find(LOSUnitLoad.class, ul.getId());
			storage.transferUnitLoad(contextService.getCallerUserName(), nirwana, u);
		}
	}


	public LOSTypeCapacityConstraint checkUnitLoadSuitable(
			BODTO<LOSStorageLocation> dest, BODTO<LOSUnitLoad> ul, boolean ignoreLock)
			throws LOSLocationException {
		
		LOSStorageLocation storageLocation = manager.find(LOSStorageLocation.class, dest.getId());
		if (storageLocation == null)
			throw new LOSLocationException(LOSLocationExceptionKey.NO_SUCH_LOCATION, new String[]{dest.getName()});
		
		LOSUnitLoad unitLoad = manager.find(LOSUnitLoad.class, ul.getId());
		if (unitLoad == null)
			throw new LOSLocationException(LOSLocationExceptionKey.NO_SUCH_UNITLOAD, new String[]{dest.getName()});

		return locationReserver.checkAllocateLocation(storageLocation, unitLoad, ignoreLock);
		
	}

	public void transferUnitLoad(BODTO<LOSStorageLocation> dest,
			BODTO<LOSUnitLoad> ul, int index, boolean ignoreSlLock, String info) throws FacadeException {
		LOSStorageLocation storageLocation = manager.find(LOSStorageLocation.class, dest.getId());
		if (storageLocation == null)
			throw new LOSLocationException(LOSLocationExceptionKey.NO_SUCH_LOCATION, new String[]{dest.getName()});
		
		LOSUnitLoad unitLoad = manager.find(LOSUnitLoad.class, ul.getId());
		if (unitLoad == null)
			throw new LOSLocationException(LOSLocationExceptionKey.NO_SUCH_UNITLOAD, new String[]{dest.getName()});
		
		storage.transferUnitLoad(contextService.getCallerUserName(), storageLocation, unitLoad, -1, ignoreSlLock, info, "");
		
	}

	public void transferToCarrier(BODTO<LOSUnitLoad> sourceTo, BODTO<LOSUnitLoad> destinationTo, String info) throws FacadeException {
		
		LOSUnitLoad source = manager.find(LOSUnitLoad.class, sourceTo.getId());
		if (source == null)
			throw new LOSLocationException(LOSLocationExceptionKey.NO_SUCH_UNITLOAD, new String[]{sourceTo.getName()});
		
		LOSUnitLoad destination = manager.find(LOSUnitLoad.class, destinationTo.getId());
		if (destination == null)
			throw new LOSLocationException(LOSLocationExceptionKey.NO_SUCH_UNITLOAD, new String[]{destinationTo.getName()});
		
		storage.transferToCarrier(contextService.getCallerUserName(), source, destination, info, "");
		
	}

	@Override
	public int setLocationOrderIndex(BODTO<LOSRack> rackTo, int startValue, int diffValue) throws FacadeException {
		if (rackTo == null) {
			throw new LOSLocationException(LOSLocationExceptionKey.NO_SUCH_LOCATION, new String[]{"NULL"});
		}
		LOSRack rack = manager.find(LOSRack.class, rackTo.getId());
		if (rack == null) {
			throw new LOSLocationException(LOSLocationExceptionKey.NO_SUCH_LOCATION, new String[]{rackTo.getName()});
		}
		
		return customLocationService.setLocationOrderIndex(rack, startValue, diffValue);
	}

}
