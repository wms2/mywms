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
package de.wms2.mywms.strategy;

import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.mywms.model.Client;

import de.wms2.mywms.client.ClientBusiness;
import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.location.StorageLocationEntityService;
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
public class OrderStrategyEntityService {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private PersistenceManager manager;
	@Inject
	private UserBusiness userBusiness;
	@Inject
	private ClientBusiness clientBusiness;
	@Inject
	private SystemPropertyBusiness propertyBusiness;
	@Inject
	private StorageLocationEntityService locationService;

	public OrderStrategy create(String name) throws BusinessException {
		OrderStrategy strategy = manager.createInstance(OrderStrategy.class);
		strategy.setName(name);

		manager.persistValidated(strategy);

		return strategy;
	}

	public OrderStrategy read(String name) {
		String jpql = "SELECT entity FROM " + OrderStrategy.class.getName() + " entity ";
		jpql += "WHERE entity.name=:name ";
		Query query = manager.createQuery(jpql);
		query.setParameter("name", name);

		try {
			return (OrderStrategy) query.getSingleResult();
		} catch (Throwable ne) {
		}

		return null;
	}

	public OrderStrategy getDefault(Client client) {
		String logStr = "getDefault ";
		if (client == null) {
			client = clientBusiness.getSystemClient();
		}

		String name = propertyBusiness.getString(Wms2Properties.KEY_ORDERSTRATEGY_DEFAULT, client, null, null);
		if (StringUtils.isEmpty(name)) {
			name = propertyBusiness.getString(Wms2Properties.KEY_ORDERSTRATEGY_DEFAULT, null, null, null);
		}
		if (StringUtils.isEmpty(name)) {
			Locale locale = userBusiness.getCurrentUsersLocale();
			name = Translator.getString(Wms2BundleResolver.class, "BasicData", "orderStrategyDefault", "name", locale);
			String desc = Translator.getString(Wms2BundleResolver.class, "BasicData", "orderStrategyDefault", "desc",
					"", locale);

			propertyBusiness.createOrUpdate(Wms2Properties.KEY_ORDERSTRATEGY_DEFAULT, name, Wms2Properties.GROUP_WMS,
					desc);
		}

		OrderStrategy strategy = read(name);
		if (strategy != null) {
			return strategy;
		}

		logger.log(Level.WARNING, logStr + "create system default order strategy");

		strategy = manager.createInstance(OrderStrategy.class);

		Locale locale = userBusiness.getCurrentUsersLocale();
		String desc = Translator.getString(Wms2BundleResolver.class, "BasicData", "orderStrategyDefault", "desc", "",
				locale);

		strategy.setName(name);
		strategy.setAdditionalContent(desc);

		List<StorageLocation> locations = locationService.getForGoodsOut(client);
		if (locations.size() >= 1) {
			strategy.setDefaultDestination(locations.get(0));
		}

		manager.persist(strategy);

		return strategy;
	}

	public void setDefault(OrderStrategy orderStrategy) throws BusinessException {
		propertyBusiness.setValue(Wms2Properties.KEY_ORDERSTRATEGY_DEFAULT, orderStrategy.getName());
	}

	public OrderStrategy getExtinguish(Client client) {
		if (client == null) {
			client = clientBusiness.getSystemClient();
		}

		String name = propertyBusiness.getString(Wms2Properties.KEY_ORDERSTRATEGY_EXTINGUISH, client, null, null);
		if (StringUtils.isEmpty(name)) {
			name = propertyBusiness.getString(Wms2Properties.KEY_ORDERSTRATEGY_EXTINGUISH, null, null, null);
		}
		if (StringUtils.isEmpty(name)) {
			Locale locale = userBusiness.getCurrentUsersLocale();
			name = Translator.getString(Wms2BundleResolver.class, "BasicData", "orderStrategyExtinguish", "name",
					locale);
			String desc = Translator.getString(Wms2BundleResolver.class, "BasicData", "orderStrategyExtinguiesh",
					"desc", "", locale);

			propertyBusiness.createOrUpdate(Wms2Properties.KEY_ORDERSTRATEGY_EXTINGUISH, name, Wms2Properties.GROUP_WMS,
					desc);
		}

		OrderStrategy strategy = read(name);
		if (strategy != null) {
			return strategy;
		}

		logger.log(Level.WARNING, "create system extinghuis order strategy");

		strategy = manager.createInstance(OrderStrategy.class);

		Locale locale = userBusiness.getCurrentUsersLocale();
		String desc = Translator.getString(Wms2BundleResolver.class, "BasicData", "orderStrategyExtinguish", "desc", "",
				locale);

		strategy.setName(name);
		strategy.setAdditionalContent(desc);
		strategy.setDefaultDestination(locationService.getClearing());
		strategy.setManualCreationIndex(0);
		strategy.setCreateFollowUpPicks(false);
		strategy.setSendToPacking(false);
		strategy.setSendToShipping(false);
		strategy.setCreateShippingOrder(false);
		strategy.setManualCreationIndex(-1);
		strategy.setPreferComplete(false);
		strategy.setUseLockedStock(true);
		strategy.setUseLockedLot(true);

		manager.persist(strategy);

		return strategy;
	}

}
