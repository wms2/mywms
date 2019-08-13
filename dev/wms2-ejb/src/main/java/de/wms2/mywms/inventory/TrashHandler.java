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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.FlushModeType;

import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.picking.PickingUnitLoad;
import de.wms2.mywms.picking.PickingUnitLoadEntityService;
import de.wms2.mywms.strategy.LocationReservationEntityService;

/**
 * @author krane
 *
 */
@Stateless
public class TrashHandler {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private PersistenceManager manager;
	@Inject
	private StockUnitEntityService stockUnitService;
	@Inject
	private PickingUnitLoadEntityService pickingUnitLoadService;
	@Inject
	private LocationReservationEntityService reservationService;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean removeStockUnit(StockUnit entity) {
		String logStr = "removeStockUnit ";
		logger.log(Level.FINE, logStr + "StockUnit=" + entity);

		try {
			manager.setFlushMode(FlushModeType.COMMIT);
			entity = manager.find(StockUnit.class, entity.getId());
			if (entity == null) {
				logger.log(Level.WARNING, logStr + "Entity is already deleted. Cannot remove.");
				return false;
			}
			manager.remove(entity);
			manager.flush();

			return true;
		} catch (Throwable t) {
			logger.log(Level.SEVERE,
					logStr + "Cannot remove item. " + t.getClass().getName() + ", " + t.getMessage());
		}
		return false;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean removeUnitLoad(UnitLoad unitLoad) {
		String logStr = "removeUnitLoad ";
		logger.log(Level.FINE, logStr + "unitLoad=" + unitLoad);
		try {
			manager.setFlushMode(FlushModeType.COMMIT);
			try {
				unitLoad = manager.find(UnitLoad.class, unitLoad.getId());
			} catch (Throwable t) {
				logger.log(Level.WARNING,
						logStr + "Cannot read UnitLoad. Exception=" + t.getClass().getName() + ", " + t.getMessage());
			}
			if (unitLoad == null) {
				logger.log(Level.WARNING, logStr + "UnitLoad is already deleted. Cannot remove.");
				return false;
			}

			List<StockUnit> stocksOnUnitLoad = stockUnitService.readByUnitLoad(unitLoad);
			for (StockUnit stock : stocksOnUnitLoad) {
				manager.remove(stock);
			}

			List<PickingUnitLoad> pickingUnitLoads = pickingUnitLoadService.readList(unitLoad, null, null);
			for (PickingUnitLoad pickingUnitLoad : pickingUnitLoads) {
				manager.remove(pickingUnitLoad);
			}

			reservationService.remove(null, unitLoad);

			manager.remove(unitLoad);
			manager.flush();

			return true;
		} catch (Throwable t) {
			logger.log(Level.SEVERE,
					logStr + "Cannot remove item. " + t.getClass().getName() + ", " + t.getMessage());
		}

		return false;
	}


}
