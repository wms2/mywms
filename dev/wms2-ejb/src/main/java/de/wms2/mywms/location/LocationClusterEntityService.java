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

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.property.SystemPropertyBusiness;
import de.wms2.mywms.user.UserBusiness;
import de.wms2.mywms.util.Translator;
import de.wms2.mywms.util.Wms2BundleResolver;
import de.wms2.mywms.util.Wms2Properties;

/**
 * @author krane
 * 
 */
@Stateless
public class LocationClusterEntityService {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private PersistenceManager manager;
	@Inject
	private SystemPropertyBusiness propertyBusiness;
	@Inject
	private UserBusiness userBusiness;

	public LocationCluster create(String name) throws BusinessException {
		LocationCluster cluster = manager.createInstance(LocationCluster.class);
		cluster.setName(name);

		manager.persistValidated(cluster);

		return cluster;
	}

	public LocationCluster read(String name) {
		String jpql = "SELECT entity FROM " + LocationCluster.class.getName() + " entity ";
		jpql += " where entity.name=:name";
		Query query = manager.createQuery(jpql);
		query.setParameter("name", name);
		try {
			LocationCluster entity = (LocationCluster) query.getSingleResult();
			return entity;
		} catch (NoResultException e) {
		}
		return null;
	}

	/**
	 * Read the default LocationCluster. If not existing, it will be created.
	 */
	public LocationCluster getDefault() {
		String bundleKey = "locationClusterDefault";

		String name = propertyBusiness.getString(Wms2Properties.KEY_LOCATIONCLUSTER_DEFAULT, null, null, null);
		if (StringUtils.isEmpty(name)) {
			Locale locale = userBusiness.getCurrentUsersLocale();
			name = Translator.getString(Wms2BundleResolver.class, "BasicData", bundleKey, "name", locale);
			String desc = Translator.getString(Wms2BundleResolver.class, "BasicData", bundleKey, "desc", "", locale);

			propertyBusiness.createOrUpdate(Wms2Properties.KEY_LOCATIONCLUSTER_DEFAULT, name, Wms2Properties.GROUP_WMS,
					desc);
		}

		LocationCluster cluster = read(name);
		if (cluster != null) {
			return cluster;
		}

		logger.log(Level.WARNING,
				"Default location cluster undefined. create default location cluster with name=" + name);

		cluster = manager.createInstance(LocationCluster.class);
		cluster.setName(name);
		manager.persist(cluster);

		return cluster;
	}

	/**
	 * Sets the given LocationCluster as the Default-Cluster
	 */
	public void setDefault(LocationCluster cluster) {
		propertyBusiness.setValue(Wms2Properties.KEY_LOCATIONCLUSTER_DEFAULT, cluster.getName());
	}

	/**
	 * Read system LocationCluster
	 */
	public LocationCluster getSystem() {
		LocationCluster cluster = manager.find(LocationCluster.class, 0L);
		if (cluster != null) {
			return cluster;
		}

		String bundleKey = "locationClusterSystem";

		Locale locale = userBusiness.getCurrentUsersLocale();
		String name = Translator.getString(Wms2BundleResolver.class, "BasicData", bundleKey, "name", locale);
		String note1 = Translator.getString(Wms2BundleResolver.class, "BasicData", bundleKey, "desc", "", locale);
		String note2 = Translator.getString(Wms2BundleResolver.class, "BasicData", "systemEntity", "note", locale);
		String note = note1 + '\n' + note2;

		logger.log(Level.WARNING, "create new system location cluster. id=" + 0 + ", name=" + name);

		String checkedName = name;
		int i = 1;
		while (true) {
			cluster = read(checkedName);
			if (cluster == null) {
				break;
			}
			checkedName = name + "-" + i++;
		}

		cluster = manager.createInstance(LocationCluster.class);
		cluster.setName(checkedName);
		cluster.setAdditionalContent(note);
		manager.persist(cluster);

		manager.flush();
		manager.detach(cluster);

		String jpql = "UPDATE " + LocationCluster.class.getName() + " SET id=:idNew WHERE id=:idOld";
		Query query = manager.createQuery(jpql);
		query.setParameter("idNew", 0L);
		query.setParameter("idOld", cluster.getId());
		query.executeUpdate();

		manager.flush();

		cluster = manager.find(LocationCluster.class, 0L);
		return cluster;
	}

}
