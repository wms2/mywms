/* 
Copyright 2019 Matthias Krane

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
package de.wms2.mywms.location;

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
import de.wms2.mywms.user.UserBusiness;
import de.wms2.mywms.util.Translator;
import de.wms2.mywms.util.Wms2BundleResolver;

/**
 * @author krane
 *
 */
@Stateless
public class StorageLocationEntityService {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private ClientBusiness clientBusiness;

	@Inject
	private PersistenceManager manager;

	@Inject
	private UserBusiness userBusiness;
	@Inject
	private LocationTypeEntityService locationTypeService;
	@Inject
	private LocationClusterEntityService locationClusterService;
	@Inject
	private AreaEntityService areaService;

	public StorageLocation create(Client client, String name, LocationType type, Area role, LocationCluster cluster)
			throws BusinessException {
		if (client == null) {
			client = clientBusiness.getSystemClient();
		}
		if (type == null) {
			type = locationTypeService.getDefault();
		}
		if (role == null) {
			role = areaService.getDefault();
		}
		if (cluster == null) {
			cluster = locationClusterService.getDefault();
		}

		StorageLocation location = manager.createInstance(StorageLocation.class);
		location.setClient(client);
		location.setName(name);
		location.setLocationType(type);
		location.setArea(role);
		location.setLocationCluster(cluster);

		manager.persistValidated(location);

		return location;
	}

	public StorageLocation read(String name) {
		String jpql = "SELECT entity FROM " + StorageLocation.class.getName() + " entity ";
		jpql += " WHERE entity.name=:name";
		Query query = manager.createQuery(jpql);
		query.setParameter("name", name);
		try {
			return (StorageLocation) query.getSingleResult();
		} catch (NoResultException e) {
		}
		return null;
	}

	public StorageLocation readByBarcode(String code) {
		String jpql = "SELECT entity FROM " + StorageLocation.class.getName() + " entity ";
		jpql += " WHERE entity.barcode=:code";
		jpql += " ORDER BY entity.name, entity.id";
		Query query = manager.createQuery(jpql);
		query.setParameter("code", code);
		query.setMaxResults(1);
		try {
			StorageLocation location = (StorageLocation) query.getSingleResult();
			return location;
		} catch (NoResultException e) {
		}

		return null;
	}

	// -----------------------------------------------------------------------
	/**
	 * Select a list of entities matching the given criteria. All parameters are
	 * optional.
	 * 
	 * @param client Optional
	 * @param area   Optional
	 * @param aisle  Optional
	 * @param offset Optional
	 * @param limit  Optional
	 */
	@SuppressWarnings("unchecked")
	public List<StorageLocation> readList(Client client, Area area, String aisle, Integer offset, Integer limit) {
		String jpql = "SELECT entity FROM " + StorageLocation.class.getName() + " entity WHERE 1=1 ";

		if (client != null) {
			jpql += " AND entity.client = :client ";
		}
		if (area != null) {
			jpql += " AND entity.area = :area ";
		}
		if (!StringUtils.isBlank(aisle)) {
			jpql += " AND entity.aisle = :aisle ";
		}

		jpql += " ORDER BY entity.name";

		Query query = manager.createQuery(jpql);
		if (offset != null) {
			query.setFirstResult(offset);
		}
		if (limit != null) {
			query.setMaxResults(limit);
		}
		if (client != null) {
			query.setParameter("client", client);
		}
		if (area != null) {
			query.setParameter("area", area);
		}
		if (!StringUtils.isBlank(aisle)) {
			query.setParameter("aisle", aisle);
		}
		return (List<StorageLocation>) query.getResultList();
	}

	/**
	 * Get all StorageLocations next to each other in the given field. The locations
	 * are ordered by location.positionX. Field criteria are location.field,
	 * location.positionY, location.positionZ, location.locationCluster
	 */
	@SuppressWarnings("unchecked")
	public List<StorageLocation> readLocationsOfField(StorageLocation location) {
		String jpql = "SELECT entity FROM " + StorageLocation.class.getName() + " entity ";
		jpql += " WHERE entity.field=:field and entity.positionY=:positionY and entity.positionZ=:positionZ ";
		jpql += " and entity.locationCluster=:locationCluster";
		jpql += " ORDER BY entity.positionX, entity.id";

		Query query = manager.createQuery(jpql);

		query.setParameter("field", location.getField());
		query.setParameter("positionY", location.getYPos());
		query.setParameter("positionZ", location.getZPos());
		query.setParameter("locationCluster", location.getLocationCluster());

		return (List<StorageLocation>) query.getResultList();
	}

	/**
	 * Get all StorageLocations with a area for goods in
	 */
	@SuppressWarnings("unchecked")
	public List<StorageLocation> getForGoodsIn(Client client) {
		String jpql = "SELECT entity FROM " + StorageLocation.class.getName() + " entity ";
		jpql += " WHERE entity.area.usages like '%" + AreaUsages.GOODS_IN + "%'";

		if (client != null) {
			jpql += " AND entity.client = :client ";
		}

		jpql += " ORDER BY entity.name";

		Query query = manager.createQuery(jpql);

		if (client != null) {
			query.setParameter("client", client);
		}

		return (List<StorageLocation>) query.getResultList();
	}

	/**
	 * Get all StorageLocations with a area for goods out
	 */
	@SuppressWarnings("unchecked")
	public List<StorageLocation> getForGoodsOut(Client client) {
		String jpql = "SELECT entity FROM " + StorageLocation.class.getName() + " entity ";
		jpql += " WHERE entity.area.usages like '%" + AreaUsages.GOODS_OUT + "%'";

		if (client != null) {
			jpql += " AND entity.client = :client ";
		}

		jpql += " ORDER BY entity.name";

		Query query = manager.createQuery(jpql);

		if (client != null) {
			query.setParameter("client", client);
		}

		return (List<StorageLocation>) query.getResultList();
	}

	/**
	 * Read the system location used as trash
	 */
	public StorageLocation getTrash() {
		StorageLocation trash;
		trash = manager.find(StorageLocation.class, 0L);
		if (trash != null) {
			return trash;
		}

		trash = createSystemStorageLocation(0, "locationTrash");

		Area clearingRole = areaService.getSystem();
		trash.setArea(clearingRole);

		return trash;
	}

	/**
	 * Read the system location used for clearings
	 */
	public StorageLocation getClearing() {
		StorageLocation clearing;

		clearing = manager.find(StorageLocation.class, 1L);
		if (clearing != null) {
			return clearing;
		}

		clearing = createSystemStorageLocation(1, "locationClearing");

		Area area = areaService.getSystem();
		clearing.setArea(area);

		return clearing;
	}

	private StorageLocation createSystemStorageLocation(long id, String nameKey) {
		String logStr = "createSystemStorageLocation ";

		Locale locale = userBusiness.getCurrentUsersLocale();
		String name = Translator.getString(Wms2BundleResolver.class, "BasicData", nameKey, "name", locale);
		String note = Translator.getString(Wms2BundleResolver.class, "BasicData", "systemEntity", "note", locale);

		logger.log(Level.WARNING, logStr + "create new system location. id=" + id + ", name=" + name);

		StorageLocation location = read(name);
		if (location == null) {
			location = manager.createInstance(StorageLocation.class);
			location.setName(name);
			location.setClient(clientBusiness.getSystemClient());
			location.setLocationType(locationTypeService.getSystem());
			location.setArea(areaService.getSystem());
			location.setLocationCluster(locationClusterService.getSystem());
			location.setAdditionalContent(note);

			manager.persist(location);
		}

		manager.flush();

		String queryStr = "UPDATE " + StorageLocation.class.getName() + " SET id=:idNew WHERE id=:idOld";
		Query query = manager.createQuery(queryStr);
		query.setParameter("idNew", id);
		query.setParameter("idOld", location.getId());
		query.executeUpdate();

		manager.flush();

		location = manager.find(StorageLocation.class, id);
		return location;
	}

	/**
	 * Read the system StorageLocation of the current logged in user. Every User
	 * gets one location. Identified by his name.
	 */
	public StorageLocation getCurrentUsersLocation() {
		String userName = userBusiness.getCurrentUsersName();
		StorageLocation location = read(userName);
		if (location == null) {
			Locale locale = userBusiness.getCurrentUsersLocale();
			String note = Translator.getString(Wms2BundleResolver.class, "BasicData", "usersLocation", "note", locale);
			location = manager.createInstance(StorageLocation.class);
			location.setName(userName);
			location.setClient(clientBusiness.getSystemClient());
			location.setLocationType(locationTypeService.getSystem());
			location.setArea(areaService.getSystem());
			location.setLocationCluster(locationClusterService.getSystem());
			location.setAdditionalContent(note);

			manager.persist(location);

			location.setAdditionalContent(note);
		}
		return location;
	}

	@SuppressWarnings("unchecked")
	public int writeStorageLocationOrderIndex(String rack, int startValue, int diffValue) {
		String logStr = "writeStorageLocationOrderIndex ";
		logger.log(Level.FINE, logStr + "rack=" + rack + ", startValue=" + startValue + ", diffValue=" + diffValue);

		String jpql = "SELECT location FROM " + StorageLocation.class.getName() + " location ";
		jpql += " WHERE rack=:rack";
		jpql += " ORDER BY location.XPos, location.YPos, location.ZPos, location.name ";

		Query query = manager.createQuery(jpql);
		query.setParameter("rack", rack);
		List<StorageLocation> locations = query.getResultList();

		int orderIndex = startValue;
		for (StorageLocation location : locations) {
			location.setOrderIndex(orderIndex);
			orderIndex += diffValue;
		}

		return locations.size();
	}
}
