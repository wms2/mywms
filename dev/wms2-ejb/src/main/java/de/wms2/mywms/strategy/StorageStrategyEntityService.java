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
public class StorageStrategyEntityService {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private PersistenceManager manager;
	@Inject
	private SystemPropertyBusiness propertyBusiness;
	@Inject
	private UserBusiness userBusiness;

	public StorageStrategy create(String name) throws BusinessException {
		StorageStrategy strategy = manager.createInstance(StorageStrategy.class);
		strategy.setName(name);

		manager.persistValidated(strategy);

		return strategy;
	}

	public StorageStrategy read(String name) {
		String jpql = "SELECT entity FROM " + StorageStrategy.class.getName() + " entity ";
		jpql += " where entity.name=:name";
		Query query = manager.createQuery(jpql);
		query.setParameter("name", name);
		try {
			StorageStrategy entity = (StorageStrategy) query.getSingleResult();
			return entity;
		} catch (NoResultException t) {
		}
		return null;
	}

	public StorageStrategy getDefault() {
		String logStr = "getDefault ";

		String name = propertyBusiness.getString(Wms2Properties.KEY_STORAGESTRATEGY_DEFAULT, null, null, null);
		if (StringUtils.isEmpty(name)) {
			Locale locale = userBusiness.getCurrentUsersLocale();
			name = Translator.getString(Wms2BundleResolver.class, "BasicData", "storageStrategyDefault", "name",
					locale);
			String desc = Translator.getString(Wms2BundleResolver.class, "BasicData", "storageStrategyDefault", "desc",
					"", locale);

			propertyBusiness.createOrUpdate(Wms2Properties.KEY_ORDERSTRATEGY_DEFAULT, name, Wms2Properties.GROUP_WMS,
					desc);
		}

		StorageStrategy strategy = read(name);
		if (strategy != null) {
			return strategy;
		}

		logger.log(Level.WARNING, logStr + "create system default storage strategy");

		strategy = manager.createInstance(StorageStrategy.class);

		Locale locale = userBusiness.getCurrentUsersLocale();
		String desc = Translator.getString(Wms2BundleResolver.class, "BasicData", "storageStrategyDefault", "desc", "",
				locale);

		strategy.setName(name);
		strategy.setAdditionalContent(desc);
		strategy.setSorts(StorageStrategySortType.ZONE + "," + StorageStrategySortType.POSITION_X + ","
				+ StorageStrategySortType.POSITION_Y);

		manager.persist(strategy);

		return strategy;
	}

	public void setDefault(StorageStrategy orderStrategy) throws BusinessException {
		propertyBusiness.setValue(Wms2Properties.KEY_STORAGESTRATEGY_DEFAULT, orderStrategy.getName());
	}
}
