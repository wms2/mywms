/* 
Copyright 2020 Matthias Krane
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
package de.wms2.mywms.picking;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.TypedQuery;

import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.StockUnitEntityService;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.strategy.OrderState;
import de.wms2.mywms.strategy.OrderStrategy;
import de.wms2.mywms.strategy.OrderStrategyEntityService;

/**
 * @author krane
 *
 */
@Stateless
public class ExtinguishOrderGenerator {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private StockUnitEntityService stockUnitService;
	@Inject
	private OrderStrategyEntityService orderStrategyEntityService;
	@Inject
	private PickingOrderLineGenerator pickingOrderLineGenerator;
	@Inject
	private PickingOrderGenerator pickingOrderGenerator;
	@Inject
	private PersistenceManager manager;

	public List<PickingOrder> generateStockUnitExtinguishOrders(Collection<StockUnit> stockUnits)
			throws BusinessException {
		List<PickingOrderLine> picks = new ArrayList<>();

		for (StockUnit stockUnit : stockUnits) {
			if (stockUnit.getAvailableAmount().compareTo(BigDecimal.ZERO) > 0) {
				OrderStrategy strategy = orderStrategyEntityService.getExtinguish(stockUnit.getClient());
				picks.add(pickingOrderLineGenerator.generatePick(null, strategy, stockUnit,
						stockUnit.getAvailableAmount(), stockUnit.getClient()));
			}
		}

		List<PickingOrder> pickingOrders = generatePickingOrders(picks);

		logger.info("Generated extinguish orders. num picks=" + picks.size() + ", num orders=" + pickingOrders.size());
		return pickingOrders;
	}

	public List<PickingOrder> generateUnitLoadExtinguishOrders(Collection<UnitLoad> unitLoads)
			throws BusinessException {
		List<PickingOrderLine> picks = new ArrayList<>();

		for (UnitLoad unitLoad : unitLoads) {
			List<StockUnit> stockUnits = stockUnitService.readByUnitLoad(unitLoad);
			OrderStrategy strategy = orderStrategyEntityService.getExtinguish(unitLoad.getClient());
			for (StockUnit stockUnit : stockUnits) {
				if (stockUnit.getAvailableAmount().compareTo(BigDecimal.ZERO) > 0) {
					picks.add(pickingOrderLineGenerator.generatePick(null, strategy, stockUnit,
							stockUnit.getAvailableAmount(), unitLoad.getClient()));
				}
			}
		}

		List<PickingOrder> pickingOrders = generatePickingOrders(picks);

		logger.info("Generated extinguish orders. num picks=" + picks.size() + ", num orders=" + pickingOrders.size());
		return pickingOrders;
	}

	private List<PickingOrder> generatePickingOrders(Collection<PickingOrderLine> picks) throws BusinessException {

		Set<OrderStrategy> strategies = new HashSet<>();

		for (PickingOrderLine pick : picks) {
			strategies.add(pick.getOrderStrategy());
		}

		List<PickingOrder> existingPickingOrders = readOpenPickingOrder(strategies);

		for (PickingOrder existingPickingOrder : existingPickingOrders) {
			picks = pickingOrderGenerator.addPicksToOrder(existingPickingOrder, picks);
			if (picks.size() == 0) {
				break;
			}
		}
		if (picks.size() > 0) {
			return pickingOrderGenerator.generatePickingOrders(picks);
		}

		// Only return new generated orders
		return new ArrayList<>();
	}

	public List<PickingOrder> readOpenPickingOrder(Collection<OrderStrategy> orderStrategies) {
		String jpql = "SELECT entity FROM ";
		jpql += PickingOrder.class.getName() + " entity ";
		jpql += "WHERE entity.state<=:maxState ";
		jpql += " and entity.orderStrategy in(:orderStrategies) ";
		jpql += " order by entity.orderNumber ";
		TypedQuery<PickingOrder> query = manager.createQuery(jpql, PickingOrder.class);
		query.setParameter("maxState", OrderState.PROCESSABLE);
		query.setParameter("orderStrategies", orderStrategies);
		return query.getResultList();
	}

}
