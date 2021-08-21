/* 
Copyright 2019-2021 Matthias Krane
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
package de.wms2.mywms.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.mywms.model.Client;

import de.wms2.mywms.client.ClientBusiness;
import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.location.StorageLocationEntityService;
import de.wms2.mywms.user.UserBusiness;
import de.wms2.mywms.util.Translator;
import de.wms2.mywms.util.Wms2BundleResolver;
import de.wms2.mywms.util.Wms2Constants;

/**
 * @author krane
 *
 */
@Stateless
public class UnitLoadEntityService {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private PersistenceManager manager;
	@Inject
	private UserBusiness userBusiness;
	@Inject
	private ClientBusiness clientBusiness;
	@Inject
	private StorageLocationEntityService locationService;
	@Inject
	private UnitLoadTypeEntityService unitLoadTypeService;

	public UnitLoad create(Client client, String label, UnitLoadType type, StorageLocation storageLocation) {
		UnitLoad unitLoad = manager.createInstance(UnitLoad.class);
		unitLoad.setClient(client);
		unitLoad.setLabelId(label);
		unitLoad.setUnitLoadType(type);
		unitLoad.setStorageLocation(storageLocation);
		unitLoad.setWeightCalculated(type.getWeight());

		manager.persist(unitLoad);
		manager.flush();

		return unitLoad;
	}

	public UnitLoad readByLabel(String label) {
		String jpql = "SELECT entity FROM " + UnitLoad.class.getName() + " entity ";
		jpql += " WHERE entity.labelId=:label";
		Query query = manager.createQuery(jpql);
		query.setParameter("label", label);
		try {
			return (UnitLoad) query.getSingleResult();
		} catch (NoResultException e) {
		}
		return null;
	}

	public List<UnitLoad> readByLocation(StorageLocation location) {
		return readList(null, location, null, null, null, null, null);
	}

	public List<UnitLoad> readByField(String field) {
		if (StringUtils.isBlank(field)) {
			return new ArrayList<>();
		}
		return readList(null, null, field, null, null, null, null);
	}

	public List<UnitLoad> readBySection(String section) {
		if (StringUtils.isBlank(section)) {
			return new ArrayList<>();
		}
		return readList(null, null, null, section, null, null, null);
	}

	public boolean existsByLabel(String label) {
		String jpql = "SELECT entity.id FROM " + UnitLoad.class.getName() + " entity";
		jpql += " WHERE entity.labelId=:label";
		Query query = manager.createQuery(jpql);
		query.setParameter("label", label);
		query.setMaxResults(1);
		try {
			query.getSingleResult();
			return true;
		} catch (NoResultException e) {
		}
		return false;
	}

	public boolean existsByLocation(StorageLocation location) {
		String jpql = "SELECT entity.id FROM " + UnitLoad.class.getName() + " entity";
		jpql += " WHERE entity.storageLocation=:location";
		Query query = manager.createQuery(jpql);
		query.setParameter("location", location);
		query.setMaxResults(1);
		try {
			query.getSingleResult();
			return true;
		} catch (NoResultException e) {
		}
		return false;
	}

	/**
	 * Select a list of entities matching the given criteria. All parameters are
	 * optional.
	 */
	@SuppressWarnings("unchecked")
	public List<UnitLoad> readList(Client client, StorageLocation location, String field, String section,
			Boolean emptyUnitLoad, Integer minState, Integer maxState) {
		String hql = "SELECT entity FROM " + UnitLoad.class.getName() + " entity WHERE 1=1";
		if (client != null) {
			hql += " and entity.client=:client";
		}
		if (location != null) {
			hql += " and entity.storageLocation=:location";
		}
		if (!StringUtils.isBlank(field)) {
			hql += " and entity.storageLocation.field=:field";
		}
		if (!StringUtils.isBlank(section)) {
			hql += " and entity.storageLocation.section=:section";
		}
		if (emptyUnitLoad != null && emptyUnitLoad.booleanValue()) {
			hql += " and not exists(select 1 from " + StockUnit.class.getSimpleName()
					+ " stock where stock.unitLoad=entity) ";
		}
		if (emptyUnitLoad != null && !emptyUnitLoad.booleanValue()) {
			hql += " and exists(select 1 from " + StockUnit.class.getSimpleName()
					+ " stock where stock.unitLoad=entity) ";
		}
		if (minState != null) {
			hql += " and entity.state>=:minState ";
		}
		if (maxState != null) {
			hql += " and entity.state<=:maxState ";
		}
		hql += " order by entity.labelId";

		Query query = manager.createQuery(hql);
		if (client != null) {
			query.setParameter("client", client);
		}
		if (location != null) {
			query.setParameter("location", location);
		}
		if (!StringUtils.isBlank(field)) {
			query.setParameter("field", field);
		}
		if (!StringUtils.isBlank(section)) {
			query.setParameter("section", section);
		}
		if (minState != null) {
			query.setParameter("minState", minState);
		}
		if (maxState != null) {
			query.setParameter("maxState", maxState);
		}
		return query.getResultList();
	}

	/**
	 * Select the number of entities matching the given criteria. All parameters are
	 * optional.
	 * 
	 * @param location    Optional
	 * @param reservation Optional
	 */
	public int count(StorageLocation location) {
		String hql = "SELECT count(*) FROM " + UnitLoad.class.getName() + " entity";
		hql += " where entity.storageLocation=:location";
		Query query = manager.createQuery(hql);
		query.setParameter("location", location);
		Long num = (Long) query.getSingleResult();
		return num.intValue();
	}

	/**
	 * Checks whether a UnitLoad has children (carrier)
	 */
	public boolean hasChilds(UnitLoad unitLoad) {
		if (unitLoad == null) {
			return false;
		}
		String hql = "SELECT entity.id FROM " + UnitLoad.class.getName() + " entity";
		hql += " WHERE entity.carrierUnitLoad = :unitLoad ";
		Query query = manager.createQuery(hql);
		query.setParameter("unitLoad", unitLoad);
		query.setMaxResults(1);
		try {
			query.getSingleResult();
			return true;
		} catch (NoResultException e) {
		}
		return false;
	}

	/**
	 * Read all children of a UnitLoad (carrier)
	 */
	@SuppressWarnings("unchecked")
	public List<UnitLoad> readChilds(UnitLoad unitLoad) {
		String jpql = "SELECT entity FROM " + UnitLoad.class.getName() + " entity";
		jpql += " WHERE entity.carrierUnitLoad = :carrier";
		Query query = manager.createQuery(jpql);
		query.setParameter("carrier", unitLoad);

		return query.getResultList();
	}

	/**
	 * Read all children, including the children of the child.
	 * 
	 * @param unitLoad
	 */
	public List<UnitLoad> readChildsRecursive(UnitLoad unitLoad) throws BusinessException {
		return readChildsRecursive(unitLoad, 1);
	}

	private List<UnitLoad> readChildsRecursive(UnitLoad unitLoad, int depth) {
		String logStr = "getChildsRecursive ";

		if (depth > Wms2Constants.MAX_CARRIER_DEPTH) {
			logger.log(Level.SEVERE, logStr + "Cannot transfer unit load with more than "
					+ Wms2Constants.MAX_CARRIER_DEPTH + " carriers");
			return new ArrayList<>();
		}

		List<UnitLoad> childs = readChilds(unitLoad);
		List<UnitLoad> newChilds = new ArrayList<>();
		for (UnitLoad child : childs) {
			if (hasChilds(child)) {
				newChilds.addAll(readChildsRecursive(child, depth++));
			}
		}
		childs.addAll(newChilds);
		return childs;
	}

	/**
	 * Checks whether a UnitLoad is a parent UnitLoad (carrier) of a given UnitLoad.
	 * The check is done over all valid levels of stacked carriers
	 */
	public boolean hasParent(UnitLoad unitLoad, UnitLoad parentToCheck) {
		return hasParent(unitLoad, parentToCheck, 0);
	}

	private boolean hasParent(UnitLoad unitLoad, UnitLoad parentToCheck, int depth) {
		String logStr = "hasParent ";

		if (unitLoad.equals(parentToCheck)) {
			logger.log(Level.FINE, logStr + "unitLoad is equal. label=" + unitLoad.getLabelId());
			return true;
		}

		if (unitLoad.getCarrierUnitLoad() == null) {
			return false;
		}

		if (unitLoad.getCarrierUnitLoad().equals(parentToCheck)) {
			logger.log(Level.FINE, logStr + "carrier match of unit load. unitload=" + unitLoad + ", carrier="
					+ unitLoad.getCarrierUnitLoad());
			return true;
		}

		if (depth > Wms2Constants.MAX_CARRIER_DEPTH) {
			logger.log(Level.SEVERE, logStr + "Cannot transfer unit load with more than "
					+ Wms2Constants.MAX_CARRIER_DEPTH + " carriers");
			return false;
		}

		return hasParent(unitLoad.getCarrierUnitLoad(), parentToCheck, depth + 1);
	}

	/**
	 * Checks whether a UnitLoad has other children than the given one (carrier)
	 */
	public boolean hasOtherChilds(UnitLoad unitLoad, UnitLoad notOther) {
		if (unitLoad == null) {
			return false;
		}
		String hql = "SELECT entity.id FROM " + UnitLoad.class.getName() + " entity";
		hql += " WHERE entity.carrierUnitLoad = :unitLoad AND entity!=:notOther";
		Query query = manager.createQuery(hql);
		query.setParameter("unitLoad", unitLoad);
		query.setParameter("notOther", notOther);
		query.setMaxResults(1);
		try {
			query.getSingleResult();
			return true;
		} catch (NoResultException e) {
		}
		return false;
	}

	/**
	 * Read the system unit load used as trash
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public UnitLoad getTrash() {
		UnitLoad trash;
		trash = manager.find(UnitLoad.class, 0L);
		if (trash != null) {
			return trash;
		}

		StorageLocation trashLocation = locationService.getTrash();

		trash = createSystemUnitLoad(0, "unitLoadTrash", trashLocation);

		return trash;
	}

	/**
	 * Read the system unit load used for clearing
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public UnitLoad getClearing() {
		UnitLoad clearing;
		clearing = manager.find(UnitLoad.class, 1L);
		if (clearing != null) {
			return clearing;
		}

		StorageLocation clearingLocation = locationService.getClearing();

		clearing = createSystemUnitLoad(1, "unitLoadClearing", clearingLocation);

		return clearing;
	}

	private UnitLoad createSystemUnitLoad(long id, String nameKey, StorageLocation location) {
		String logStr = "createSystemUnitLoad ";

		Locale locale = userBusiness.getCurrentUsersLocale();
		String name = Translator.getString(Wms2BundleResolver.class, "BasicData", nameKey, "name", locale);
		String note = Translator.getString(Wms2BundleResolver.class, "BasicData", "systemEntity", "note", locale);

		logger.log(Level.WARNING, logStr + "create new system UnitLoad. id=" + id + ", name=" + name);

		UnitLoad unitLoad = readByLabel(name);
		if (unitLoad == null) {

			Client systemClient = clientBusiness.getSystemClient();
			UnitLoadType systemType = unitLoadTypeService.getSystem();
			unitLoad = manager.createInstance(UnitLoad.class);
			unitLoad.setClient(systemClient);
			unitLoad.setLabelId(name);
			unitLoad.setUnitLoadType(systemType);
			unitLoad.setStorageLocation(location);
			unitLoad.setAdditionalContent(note);
			manager.persist(unitLoad);

		}

		manager.flush();

		String queryStr = "UPDATE " + UnitLoad.class.getName() + " SET id=:idNew WHERE id=:idOld";
		Query query = manager.createQuery(queryStr);
		query.setParameter("idNew", id);
		query.setParameter("idOld", unitLoad.getId());
		query.executeUpdate();

		manager.flush();

		unitLoad = manager.find(UnitLoad.class, id);

		return unitLoad;
	}
}
