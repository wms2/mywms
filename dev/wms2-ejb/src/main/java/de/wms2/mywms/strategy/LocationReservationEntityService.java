/* 
Copyright 2019 Matthias Krane
info@krane.engineer

This file is part of the Warehouse Management System mywms

mywms is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.
 
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/
package de.wms2.mywms.strategy;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.util.Wms2BundleResolver;

/**
 * @author krane
 * 
 */
@Stateless
public class LocationReservationEntityService {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private PersistenceManager manager;

	public LocationReservation create(StorageLocation storageLocation, UnitLoad unitLoad, String reserverType,
			Long reserverId, String reserverName) throws BusinessException {
		LocationReservation res = manager.createInstance(LocationReservation.class);
		res.setStorageLocation(storageLocation);
		res.setUnitLoad(unitLoad);
		res.setReserverType(reserverType);
		res.setReserverId(reserverId);
		res.setReserverName(reserverName);

		manager.persistValidated(res);

		return res;
	}

	/**
	 * Checks whether an entity exists, which is matching the given criteria. All
	 * parameters are optional.
	 * 
	 * @param storageLocation Optional
	 * @param unitLoad        Optional
	 */
	public boolean exists(StorageLocation storageLocation, UnitLoad unitLoad) {
		String jpql = "SELECT res.id FROM " + LocationReservation.class.getName() + " res where 1=1";
		if (storageLocation != null) {
			jpql += " and res.storageLocation=:storageLocation";
		}
		if (unitLoad != null) {
			jpql += " and res.unitLoad=:unitLoad";
		}
		Query query = manager.createQuery(jpql);
		if (storageLocation != null) {
			query.setParameter("storageLocation", storageLocation);
		}
		if (unitLoad != null) {
			query.setParameter("unitLoad", unitLoad);
		}
		query.setMaxResults(1);
		try {
			query.getSingleResult();
		} catch (NoResultException e) {
			return false;
		}
		return true;
	}

	public void remove(StorageLocation location, UnitLoad unitLoad) throws BusinessException {
		String logStr = "remove ";
		logger.log(Level.FINE, logStr + "remove reservation location=" + location + ", unitLoad=" + unitLoad);

		if (location == null && unitLoad == null) {
			logger.log(Level.WARNING, logStr + "Missing parameter location or unitLoad. Abort.");
			throw new BusinessException(Wms2BundleResolver.class,
					"LocationReserver.missingParameterLocationUnitLoad");
		}
		List<LocationReservation> reservations = readList(location, unitLoad);
		for (LocationReservation reservation : reservations) {
			manager.removeValidated(reservation);
		}
	}

	@SuppressWarnings("unchecked")
	public Collection<UnitLoad> readReservingUnitLoadsByLocation(StorageLocation storageLocation) {
		String jpql = "SELECT entity.unitLoad FROM " + LocationReservation.class.getName() + " entity";
		jpql += " where entity.storageLocation=:storageLocation";
		Query query = manager.createQuery(jpql);
		query.setParameter("storageLocation", storageLocation);
		return query.getResultList();
	}

	/**
	 * Select a list of entities matching the given criteria. All parameters are
	 * optional.
	 */
	@SuppressWarnings("unchecked")
	public List<LocationReservation> readList(StorageLocation storageLocation, UnitLoad unitLoad) {
		String jpql = "SELECT res FROM " + LocationReservation.class.getName() + " res where 1=1";
		if (storageLocation != null) {
			jpql += " and res.storageLocation=:storageLocation";
		}
		if (unitLoad != null) {
			jpql += " and res.unitLoad=:unitLoad";
		}
		jpql += " order by res.storageLocation.name, res.unitLoad.labelId, res.id ";
		Query query = manager.createQuery(jpql);
		if (storageLocation != null) {
			query.setParameter("storageLocation", storageLocation);
		}
		if (unitLoad != null) {
			query.setParameter("unitLoad", unitLoad);
		}
		return query.getResultList();
	}
}
