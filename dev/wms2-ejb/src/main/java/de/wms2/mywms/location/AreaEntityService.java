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
public class AreaEntityService {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private PersistenceManager manager;
	@Inject
	private SystemPropertyBusiness propertyBusiness;
	@Inject
	private UserBusiness userBusiness;

	public Area create(String name) throws BusinessException {
		Area area = manager.createInstance(Area.class);
		area.setName(name);

		manager.persistValidated(area);

		return area;
	}

	public Area readByName(String name) {
		String jpql = "SELECT entity FROM " + Area.class.getName() + " entity ";
		jpql += " where entity.name=:name";
		Query query = manager.createQuery(jpql);
		query.setParameter("name", name);
		try {
			Area entity = (Area) query.getSingleResult();
			return entity;
		} catch (NoResultException e) {
		}
		return null;
	}

	/**
	 * Read list of Areas used for the given usage
	 */
	@SuppressWarnings("unchecked")
	public List<Area> getForUsage(String usage) {
		String jpql = "SELECT entity FROM " + Area.class.getName() + " entity ";
		jpql += " WHERE entity.usages like :usage";
		jpql += " ORDER BY entity.name ";
		Query query = manager.createQuery(jpql);
		query.setParameter("usage", "%" + usage + "%");
		return query.getResultList();
	}

	/**
	 * Read system default Area
	 */
	public Area getDefault() {
		String bundleKey = "areaDefault";

		String name = propertyBusiness.getString(Wms2Properties.KEY_AREA_DEFAULT, null, null, null);
		if (StringUtils.isEmpty(name)) {
			Locale locale = userBusiness.getCurrentUsersLocale();
			name = Translator.getString(Wms2BundleResolver.class, "BasicData", bundleKey, "name", locale);
			String desc = Translator.getString(Wms2BundleResolver.class, "BasicData", bundleKey, "desc", "", locale);

			propertyBusiness.createOrUpdate(Wms2Properties.KEY_AREA_DEFAULT, name, Wms2Properties.GROUP_WMS, desc);
		}

		Area area = readByName(name);
		if (area != null) {
			return area;
		}

		logger.log(Level.WARNING, "Default area undefined. Create default area with name=" + name);

		area = manager.createInstance(Area.class);
		area.setName(name);

		manager.persist(area);

		area.setUsages(AreaUsages.GOODS_IN + "," + AreaUsages.GOODS_OUT + "," + AreaUsages.PICKING + ","
				+ AreaUsages.STORAGE + "," + AreaUsages.REPLENISH);

		return area;
	}

	/**
	 * Sets the given Area as the Default-Role
	 */
	public void setDefault(Area area) {
		propertyBusiness.setValue(Wms2Properties.KEY_AREA_DEFAULT, area.getName());
	}

	/**
	 * Read system Area
	 */
	public Area getSystem() {
		Area area = manager.find(Area.class, 0L);
		if (area != null) {
			return area;
		}

		String bundleKey = "areaSystem";

		Locale locale = userBusiness.getCurrentUsersLocale();
		String name = Translator.getString(Wms2BundleResolver.class, "BasicData", bundleKey, "name", locale);
		String note1 = Translator.getString(Wms2BundleResolver.class, "BasicData", bundleKey, "desc", "", locale);
		String note2 = Translator.getString(Wms2BundleResolver.class, "BasicData", "systemEntity", "note", locale);
		String note = note1 + '\n' + note2;

		logger.log(Level.WARNING, "Create new system area. id=" + 0 + ", name=" + name);

		String checkedName = name;
		int i = 1;
		while (true) {
			area = readByName(checkedName);
			if (area == null) {
				break;
			}
			checkedName = name + "-" + i++;
		}

		area = manager.createInstance(Area.class);
		area.setName(checkedName);
		area.setAdditionalContent(note);
		manager.persist(area);

		manager.flush();
		manager.detach(area);

		String jpql = "UPDATE " + Area.class.getName() + " SET id=:idNew WHERE id=:idOld";
		Query query = manager.createQuery(jpql);
		query.setParameter("idNew", 0L);
		query.setParameter("idOld", area.getId());
		query.executeUpdate();

		manager.flush();

		area = manager.find(Area.class, 0L);
		return area;
	}

}
