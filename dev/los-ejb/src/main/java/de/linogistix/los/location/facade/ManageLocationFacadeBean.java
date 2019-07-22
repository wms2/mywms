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
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;

import de.linogistix.los.location.businessservice.LOSStorage;
import de.linogistix.los.location.businessservice.LocationReserver;
import de.linogistix.los.location.customization.CustomLocationService;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.exception.LOSLocationExceptionKey;
import de.linogistix.los.location.query.LOSUnitLoadQueryRemote;
import de.linogistix.los.location.query.dto.LOSRackTO;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.util.businessservice.ContextService;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.location.StorageLocationEntityService;
import de.wms2.mywms.strategy.TypeCapacityConstraint;

@Stateless
@PermitAll
public class ManageLocationFacadeBean implements ManageLocationFacade {

	private static final Logger log = Logger
			.getLogger(ManageLocationFacadeBean.class);

	@EJB
	private LOSStorage storage;

	@EJB
	private ContextService contextService;

	@EJB
	private LOSUnitLoadQueryRemote uLoadQueryRemote;

	@EJB
	private LocationReserver locationReserver;
	@EJB
	private CustomLocationService customLocationService;
	@Inject
	private StorageLocationEntityService locationServcie;

	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;

	public void releaseReservations(List<BODTO<StorageLocation>> locations)
			throws FacadeException {

		if (locations == null) {
			return;
		}

		for (BODTO<StorageLocation> storLoc : locations) {
			StorageLocation sl = manager.find(StorageLocation.class,
					storLoc.getId());
			if (sl == null) {
				log.warn("Not found: " + storLoc.getName());
				continue;
			}
			locationReserver.deallocateLocationComplete(sl);
		}
	}

	public void sendUnitLoadToNirwana(String labelId) throws FacadeException {

		UnitLoad u = uLoadQueryRemote.queryByIdentity(labelId);
		manager.find(UnitLoad.class, u.getId());
		StorageLocation nirwana = locationServcie.getTrash();
		storage.transferUnitLoad(contextService.getCallerUserName(), nirwana, u);

	}

	public void sendUnitLoadToNirwana(List<BODTO<UnitLoad>> list)
			throws FacadeException {
		if (list == null) {
			return;
		}

		StorageLocation nirwana = locationServcie.getTrash();
		for (BODTO<UnitLoad> ul : list) {
			UnitLoad u = manager.find(UnitLoad.class, ul.getId());
			storage.transferUnitLoad(contextService.getCallerUserName(), nirwana, u);
		}
	}


	public TypeCapacityConstraint checkUnitLoadSuitable(
			BODTO<StorageLocation> dest, BODTO<UnitLoad> ul, boolean ignoreLock)
			throws LOSLocationException {
		
		StorageLocation storageLocation = manager.find(StorageLocation.class, dest.getId());
		if (storageLocation == null)
			throw new LOSLocationException(LOSLocationExceptionKey.NO_SUCH_LOCATION, new String[]{dest.getName()});
		
		UnitLoad unitLoad = manager.find(UnitLoad.class, ul.getId());
		if (unitLoad == null)
			throw new LOSLocationException(LOSLocationExceptionKey.NO_SUCH_UNITLOAD, new String[]{dest.getName()});

		return locationReserver.checkAllocateLocation(storageLocation, unitLoad, ignoreLock);
		
	}

	public void transferUnitLoad(BODTO<StorageLocation> dest,
			BODTO<UnitLoad> ul, int index, boolean ignoreSlLock, String info) throws FacadeException {
		StorageLocation storageLocation = manager.find(StorageLocation.class, dest.getId());
		if (storageLocation == null)
			throw new LOSLocationException(LOSLocationExceptionKey.NO_SUCH_LOCATION, new String[]{dest.getName()});
		
		UnitLoad unitLoad = manager.find(UnitLoad.class, ul.getId());
		if (unitLoad == null)
			throw new LOSLocationException(LOSLocationExceptionKey.NO_SUCH_UNITLOAD, new String[]{dest.getName()});
		
		storage.transferUnitLoad(contextService.getCallerUserName(), storageLocation, unitLoad, -1, ignoreSlLock, info, "");
		
	}

	public void transferToCarrier(BODTO<UnitLoad> sourceTo, BODTO<UnitLoad> destinationTo, String info) throws FacadeException {
		
		UnitLoad source = manager.find(UnitLoad.class, sourceTo.getId());
		if (source == null)
			throw new LOSLocationException(LOSLocationExceptionKey.NO_SUCH_UNITLOAD, new String[]{sourceTo.getName()});
		
		UnitLoad destination = manager.find(UnitLoad.class, destinationTo.getId());
		if (destination == null)
			throw new LOSLocationException(LOSLocationExceptionKey.NO_SUCH_UNITLOAD, new String[]{destinationTo.getName()});
		
		storage.transferToCarrier(contextService.getCallerUserName(), source, destination, info, "");
		
	}

	@Override
	public int setLocationOrderIndex(String rackTo, int startValue, int diffValue) throws FacadeException {
		if (StringUtils.isBlank(rackTo)) {
			throw new LOSLocationException(LOSLocationExceptionKey.NO_SUCH_LOCATION, new String[]{"NULL"});
		}
		return locationServcie.writeStorageLocationOrderIndex(rackTo, startValue, diffValue);
	}

	@Override
	public LOSRackTO readRackInfo(String rack) {
		LOSRackTO rackTO = new LOSRackTO();

		String jpql = "select min(location.orderIndex), max(location.orderIndex), count(*) from ";
		jpql += StorageLocation.class.getName() + " location ";
		jpql += " where location.rack=:rack";
		Query query = manager.createQuery(jpql);
		query.setParameter("rack", rack);

		Object result = query.getSingleResult();
		Object[] results = (Object[]) result;

		rackTO.setLocationIndexMin((Integer) results[0]);
		rackTO.setLocationIndexMax((Integer) results[1]);
		rackTO.setNumLocation(((Long) results[2]).intValue());

		return rackTO;
	}

}
