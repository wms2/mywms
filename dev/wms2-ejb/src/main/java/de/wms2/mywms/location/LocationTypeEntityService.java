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
public class LocationTypeEntityService {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private SystemPropertyBusiness propertyBusiness;
	@Inject
	private PersistenceManager manager;
	@Inject
	private UserBusiness userBusiness;

	public LocationType create(String name) throws BusinessException {
		LocationType type = manager.createInstance(LocationType.class);
		type.setName(name);

		manager.persistValidated(type);

		return type;
	}

	public LocationType read(String name) {
		String jpql = "SELECT entity FROM " + LocationType.class.getName() + " entity ";
		jpql += " where entity.name=:name";
		Query query = manager.createQuery(jpql);
		query.setParameter("name", name);
		try {
			LocationType entity = (LocationType) query.getSingleResult();
			return entity;
		} catch (NoResultException e) {
		}
		return null;
	}

	/**
	 * Read system default LocationType
	 */
	public LocationType getDefault() {
		String bundleKey = "locationTypeDefault";
		String name = propertyBusiness.getString(Wms2Properties.KEY_LOCATIONTYPE_DEFAULT, null, null, null);
		if (StringUtils.isEmpty(name)) {
			Locale locale = userBusiness.getCurrentUsersLocale();
			name = Translator.getString(Wms2BundleResolver.class, "BasicData", bundleKey, "name", locale);
			String desc = Translator.getString(Wms2BundleResolver.class, "BasicData", bundleKey, "desc", "", locale);

			propertyBusiness.createOrUpdate(Wms2Properties.KEY_LOCATIONTYPE_DEFAULT, name, Wms2Properties.GROUP_WMS,
					desc);
		}

		LocationType type = read(name);
		if (type != null) {
			return type;
		}

		logger.log(Level.WARNING, "Default location type undefined. create default location type with name=" + name);

		type = manager.createInstance(LocationType.class);
		type.setName(name);
		manager.persist(type);

		return type;
	}

	public void setDefault(LocationType type) {
		propertyBusiness.setValue(Wms2Properties.KEY_LOCATIONTYPE_DEFAULT, type.getName());
	}

	/**
	 * Read system LocationType used for system-locations. There should be no
	 * restrictions on this LocationType
	 */
	public LocationType getSystem() {
		long systemId = 1L;
		LocationType type = manager.find(LocationType.class, systemId);
		if (type != null) {
			return type;
		}
		String bundleKey = "locationTypeSystem";
		Locale locale = userBusiness.getCurrentUsersLocale();
		String name = Translator.getString(Wms2BundleResolver.class, "BasicData", bundleKey, "name", locale);
		String note1 = Translator.getString(Wms2BundleResolver.class, "BasicData", bundleKey, "desc", "", locale);
		String note2 = Translator.getString(Wms2BundleResolver.class, "BasicData", "systemEntity", "note", locale);
		String note = note1 + '\n' + note2;

		logger.log(Level.WARNING, "create new system location type. name=" + name);

		String checkedName = name;
		int i = 1;
		while (true) {
			type = read(checkedName);
			if (type == null) {
				break;
			}
			checkedName = name + "-" + i++;
		}

		type = manager.createInstance(LocationType.class);
		type.setName(checkedName);
		type.setAdditionalContent(note);
		manager.persist(type);

		manager.flush();

		String jpql = "UPDATE " + LocationType.class.getName() + " SET id=:idNew WHERE id=:idOld";
		Query query = manager.createQuery(jpql);
		query.setParameter("idNew", systemId);
		query.setParameter("idOld", type.getId());
		query.executeUpdate();

		manager.flush();

		type = manager.find(LocationType.class, systemId);
		return type;
	}

}
