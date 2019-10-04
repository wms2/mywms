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
package de.wms2.mywms.inventory;

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
public class UnitLoadTypeEntityService {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private PersistenceManager manager;
	@Inject
	private SystemPropertyBusiness propertyBusiness;
	@Inject
	private UserBusiness userBusiness;

	public UnitLoadType create(String name) throws BusinessException {
		UnitLoadType type = manager.createInstance(UnitLoadType.class);
		type.setName(name);

		manager.persistValidated(type);

		return type;
	}

	public UnitLoadType readByName(String name) {
		String jpql = "SELECT entity FROM " + UnitLoadType.class.getName() + " entity ";
		jpql += " where entity.name=:name";
		Query query = manager.createQuery(jpql);
		query.setParameter("name", name);
		try {
			UnitLoadType item = (UnitLoadType) query.getSingleResult();
			return item;
		} catch (NoResultException e) {
		}
		return null;
	}

	/**
	 * Read list of Areas used for the given usage
	 */
	@SuppressWarnings("unchecked")
	public List<UnitLoadType> getForUsage(String usage) {
		String jpql = "SELECT entity FROM " + UnitLoadType.class.getName() + " entity ";
		jpql += " WHERE entity.usages like :usage";
		jpql += " ORDER BY entity.name ";
		Query query = manager.createQuery(jpql);
		query.setParameter("usage", "%" + usage + "%");
		return query.getResultList();
	}

	/**
	 * Read the systems default UnitLoadType
	 */
	public UnitLoadType getDefault() {
		String bundleKey = "unitLoadTypeDefault";

		String name = propertyBusiness.getString(Wms2Properties.KEY_UNITLOADTYPE_DEFAULT, null, null, null);
		if (StringUtils.isEmpty(name)) {
			Locale locale = userBusiness.getCurrentUsersLocale();
			name = Translator.getString(Wms2BundleResolver.class, "BasicData", bundleKey, "name", locale);
			String desc = Translator.getString(Wms2BundleResolver.class, "BasicData", bundleKey, "desc", "", locale);

			propertyBusiness.createOrUpdate(Wms2Properties.KEY_UNITLOADTYPE_DEFAULT, name, Wms2Properties.GROUP_WMS,
					desc);
		}

		UnitLoadType type = readByName(name);
		if (type != null) {
			return type;
		}

		logger.log(Level.WARNING, "Default unit load type undefined. create default unit load type with name=" + name);

		type = manager.createInstance(UnitLoadType.class);
		type.setName(name);

		manager.persist(type);

		type.setUsages(UnitLoadTypeUsages.FORKLIFT + "," + UnitLoadTypeUsages.PICKING + ","
				+ UnitLoadTypeUsages.SHIPPING + "," + UnitLoadTypeUsages.STORAGE);

		return type;
	}

	/**
	 * Read the systems default UnitLoadType
	 */
	public UnitLoadType getPicking() {
		String bundleKey = "unitLoadTypePicking";

		String name = propertyBusiness.getString(Wms2Properties.KEY_UNITLOADTYPE_PICKING, null, null, null);
		if (StringUtils.isEmpty(name)) {
			Locale locale = userBusiness.getCurrentUsersLocale();
			name = Translator.getString(Wms2BundleResolver.class, "BasicData", bundleKey, "name", locale);
			String desc = Translator.getString(Wms2BundleResolver.class, "BasicData", bundleKey, "desc", "", locale);

			propertyBusiness.createOrUpdate(Wms2Properties.KEY_UNITLOADTYPE_PICKING, name, Wms2Properties.GROUP_WMS,
					desc);
		}

		UnitLoadType type = readByName(name);
		if (type != null) {
			return type;
		}

		logger.log(Level.WARNING, "Picking unit load type undefined. create unit load type with name=" + name);

		type = manager.createInstance(UnitLoadType.class);
		type.setName(name);
		type.setAggregateStocks(false);

		manager.persist(type);

		type.setUsages(UnitLoadTypeUsages.FORKLIFT + "," + UnitLoadTypeUsages.PICKING + ","
				+ UnitLoadTypeUsages.SHIPPING);

		return type;
	}

	/**
	 * Sets the systems default UnitLoadType
	 */
	public void setDefault(UnitLoadType unitLoadType) {
		propertyBusiness.setValue(Wms2Properties.KEY_UNITLOADTYPE_DEFAULT, unitLoadType.getName());
	}

	/**
	 * Read the system UnitLoadType
	 */
	public UnitLoadType getSystem() {
		UnitLoadType type = manager.find(UnitLoadType.class, 0L);
		if (type != null) {
			return type;
		}
		type = createSystem(0, "unitLoadTypeSystem");

		type.setAggregateStocks(false);
		type.setCalculateWeight(false);
		type.setManageEmpties(false);

		return type;
	}

	/**
	 * Read the systems virtual UnitLoadType
	 */
	public UnitLoadType getVirtual() {
		UnitLoadType type = manager.find(UnitLoadType.class, 1L);
		if (type != null) {
			return type;
		}
		type = createSystem(1, "unitLoadTypeVirtual");

		type.setAggregateStocks(true);
		type.setCalculateWeight(false);
		type.setManageEmpties(false);

		return type;
	}

	private UnitLoadType createSystem(long id, String nameKey) {
		Locale locale = userBusiness.getCurrentUsersLocale();
		String name = Translator.getString(Wms2BundleResolver.class, "BasicData", nameKey, "name", locale);
		String desc = Translator.getString(Wms2BundleResolver.class, "BasicData", nameKey, "desc", locale);
		String note = Translator.getString(Wms2BundleResolver.class, "BasicData", "systemEntity", "note", locale);
		note = desc + '\n' + note;
		logger.log(Level.WARNING, "create new system unit load type. id=" + id + ", name=" + name);

		UnitLoadType unitLoadType = readByName(name);
		if (unitLoadType == null) {
			unitLoadType = manager.createInstance(UnitLoadType.class);
			unitLoadType.setName(name);
			unitLoadType.setAdditionalContent(note);
			manager.persist(unitLoadType);
		}

		manager.flush();

		String queryStr = "UPDATE " + UnitLoadType.class.getName() + " SET id=:idNew WHERE id=:idOld";
		Query query = manager.createQuery(queryStr);
		query.setParameter("idNew", id);
		query.setParameter("idOld", unitLoadType.getId());
		query.executeUpdate();

		manager.flush();

		unitLoadType = manager.find(UnitLoadType.class, id);
		return unitLoadType;
	}

}
