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
package de.wms2.mywms.picking;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import org.mywms.model.Client;

import de.wms2.mywms.client.ClientBusiness;
import de.wms2.mywms.entity.GenericEntityService;
import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.inventory.StockState;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.Area;
import de.wms2.mywms.location.AreaUsages;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.strategy.FixAssignment;
import de.wms2.mywms.strategy.FixAssignmentEntityService;
import de.wms2.mywms.strategy.OrderStrategy;
import de.wms2.mywms.strategy.OrderStrategyEntityService;
import de.wms2.mywms.util.ListUtils;

/**
 * Search suitable stock for picking
 * 
 * @author krane
 *
 */
@Stateless
public class PickingStockFinder {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private PersistenceManager manager;
	@Inject
	private FixAssignmentEntityService fixService;
	@Inject
	private OrderStrategyEntityService orderStrategyService;
	@Inject
	private ClientBusiness clientBusiness;
	@Inject
	private GenericEntityService entityService;

	/**
	 * Find the first source stock which can be used in a picking order.
	 */
	public StockUnit findFirstSourceStock(ItemData itemData, BigDecimal amount, Client client, Lot lot,
			OrderStrategy strategy) throws BusinessException {
		String logStr = "findFirstSourceStock ";

		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
			logger.log(Level.WARNING, logStr + "No amount. No pick. itemData=" + itemData);
			return null;
		}

		if (strategy == null) {
			strategy = orderStrategyService.getDefault(client);
		}

		StockUnit stock = findFirstSourceStockIntern(itemData, client, lot, amount, strategy);
		if (stock != null) {
			return stock;
		}

		if (client == null) {
			logger.log(Level.INFO, logStr + "No client. No consi. itemData=" + itemData);
			return null;
		}

		if (!itemData.getClient().isSystemClient()) {
			logger.log(Level.INFO, logStr + "No system clients itemData. No consi. itemData=" + itemData);
			return null;
		}

		if (!client.isSystemClient()) {
			logger.log(Level.FINE,
					logStr + "No stock available for client. search for system client. requesting client=" + client
							+ ", itemData=" + itemData);
			stock = findFirstSourceStockIntern(itemData, clientBusiness.getSystemClient(), lot, amount, strategy);
			if (stock != null) {
				return stock;
			}
		}

		logger.log(Level.FINE,
				logStr + "No stock available for system client. search for all clients. requesting client=" + client
						+ ", itemData=" + itemData);
		stock = findFirstSourceStockIntern(itemData, null, lot, amount, strategy);

		return stock;
	}

	private StockUnit findFirstSourceStockIntern(ItemData itemData, Client client, Lot lot, BigDecimal amount,
			OrderStrategy strategy) throws BusinessException {
		String logStr = "findFirstSourceStockIntern ";

		logger.log(Level.FINE, logStr + "itemData=" + itemData + ", amount=" + amount + ",client=" + client + ", lot="
				+ lot + ", strategy=" + strategy);

		List<PickingStockUnit> stockList = readSourceStockList(itemData, client, lot, strategy);
		if (stockList.isEmpty()) {
			logger.log(Level.INFO, logStr + "No stock. client=" + client + ", itemData=" + itemData + ", lot=" + lot
					+ ", strategy=" + strategy);
			return null;
		}

		// Do not use reserved material
		List<PickingStockUnit> notUsables = new ArrayList<>();
		for (PickingStockUnit stock : stockList) {
			if (stock.availableAmount.compareTo(BigDecimal.ZERO) <= 0) {
				notUsables.add(stock);
			}
		}
		stockList.removeAll(notUsables);

		// Find complete unit load with exact matching amount
		// Only in preferred or exclusive complete mode
		if (strategy.isPreferComplete() || strategy.isCompleteOnly()) {
			for (PickingStockUnit stock : stockList) {
				if (stock.availableAmount.compareTo(amount) != 0) {
					continue;
				}
				if (!isValidForCompleteHandling(stock)) {
					continue;
				}

				logger.log(Level.FINE,
						logStr + "Use complete matching unit load. amount=" + amount + ", stock=" + stock);
				return manager.find(StockUnit.class, stock.stockId);
			}
		}

		// Find complete unit load which has at least the requested amount.
		// In this mode more than the required amount will be used
		if (strategy.isCompleteOnly()) {
			for (PickingStockUnit stock : stockList) {
				if (stock.availableAmount.compareTo(amount) < 0) {
					continue;
				}
				if (!isValidForCompleteHandling(stock)) {
					continue;
				}

				logger.log(Level.FINE, logStr + "Use complete unit load with amount difference. amount=" + amount
						+ ", stock=" + stock);
				return manager.find(StockUnit.class, stock.stockId);
			}
		}

		// Find complete unit load with less than the requires amount. An additional
		// pick will be generated.
		if (strategy.isPreferComplete() || strategy.isCompleteOnly()) {
			for (PickingStockUnit stock : stockList) {
				if (stock.availableAmount.compareTo(amount) > 0) {
					continue;
				}
				if (!isValidForCompleteHandling(stock)) {
					continue;
				}

				logger.log(Level.FINE, logStr + "Use complete unit load. amount=" + amount + ", stock=" + stock);
				return manager.find(StockUnit.class, stock.stockId);
			}
		}

		// Only complete handling, but no complete has been found
		if (strategy.isCompleteOnly()) {
			logger.log(Level.INFO, logStr
					+ "No usable stock found. Only complete handling is allowed but no valid complete unit load has been found. candidates="
					+ stockList.size() + ", itemData=" + itemData + ", amount=" + amount + ", client=" + client
					+ ", lot=" + lot
					+ ", (unitLoadOpened=false, reservedAmount=0, locationUseForStorage=true, onFixedLocation=false, mixed=false)");
			return null;
		}

		// Find stock on fixed picking location
		for (PickingStockUnit stock : stockList) {
			if (!stock.onFixedLocation) {
				continue;
			}
			if (!stock.locationUseForPicking) {
				continue;
			}
			if (stock.maxFixPickAmount != null && stock.maxFixPickAmount.compareTo(BigDecimal.ZERO) > 0
					&& stock.maxFixPickAmount.compareTo(amount) < 0) {
				logger.log(Level.FINER, logStr + "Amount for fixed location exceeded. location=" + stock.locationName
						+ ", amount=" + amount + ", maxFixPickAmount=" + stock.maxFixPickAmount);
				continue;
			}

			logger.log(Level.FINE, logStr + "Use stock on fixed location. amount=" + amount + ", stock=" + stock);
			return manager.find(StockUnit.class, stock.stockId);
		}

		// Find any usable stock for picking with exact matching amount
		if (strategy.isPreferMatching()) {
			for (PickingStockUnit stock : stockList) {
				if (stock.availableAmount.compareTo(amount) != 0) {
					continue;
				}
				if (stock.onFixedLocation) {
					continue;
				}
				if (!stock.locationUseForPicking) {
					// A complete pick type would be valid. The amount is matching.
					if (!isValidForCompleteHandling(stock)) {
						continue;
					}
				}

				logger.log(Level.FINE, logStr + "Use matching stock. amount=" + amount + ", stock=" + stock);
				return manager.find(StockUnit.class, stock.stockId);
			}
		}

		// Find any usable stock for picking
		for (PickingStockUnit stock : stockList) {
			if (stock.onFixedLocation) {
				continue;
			}
			if (!stock.locationUseForPicking) {
				if (stock.availableAmount.compareTo(amount) > 0) {
					continue;
				}
				if (!isValidForCompleteHandling(stock)) {
					continue;
				}
			}

			logger.log(Level.FINE, logStr + "Use stock. amount=" + amount + ", stock=" + stock);
			return manager.find(StockUnit.class, stock.stockId);
		}

		logger.log(Level.INFO, logStr + "No usable stock found. cadidates=" + stockList.size() + ", client=" + client
				+ ", itemData=" + itemData + ", lot=" + lot + ", amount=" + amount);
		return null;
	}

	/**
	 * Find all usable source stocks which can be used in a picking order.
	 */
	public List<StockUnit> findSourceStockList(ItemData itemData, Client client, Lot lot, OrderStrategy strategy) {
		if (strategy == null) {
			strategy = orderStrategyService.getDefault(client);
		}

		List<PickingStockUnit> stockList = readSourceStockList(itemData, client, lot, strategy);
		List<StockUnit> entityList = new ArrayList<StockUnit>(stockList.size());
		for (PickingStockUnit to : stockList) {
			StockUnit stockUnit = manager.find(StockUnit.class, to.stockId);
			if (stockUnit != null) {
				entityList.add(stockUnit);
			}
		}

		return entityList;
	}

	@SuppressWarnings("unchecked")
	private List<PickingStockUnit> readSourceStockList(ItemData itemData, Client client, Lot lot,
			OrderStrategy strategy) {
		String logStr = "readSourceStockList ";
		logger.log(Level.FINE,
				logStr + " itemData=" + itemData + ", client=" + client + ", lot=" + lot + ", strategy=" + strategy);

		String jpql = "";

		jpql += " select stock.id, stock.amount, stock.reservedAmount, ";
		jpql += " location.id, location.name, unitLoad.id, unitLoad.opened, role.usages";
		jpql += " from " + StockUnit.class.getName() + " stock, ";
		jpql += UnitLoad.class.getName() + " unitLoad, ";
		jpql += StorageLocation.class.getName() + " location, ";
		jpql += Area.class.getName() + " role ";
		jpql += "where unitLoad=stock.unitLoad ";
		jpql += " and location = unitLoad.storageLocation ";
		jpql += " and role=location.area ";

		jpql += " and (role.usages like '%" + AreaUsages.PICKING + "%'";
		jpql += "      or role.usages like '%" + AreaUsages.STORAGE + "%') ";
		jpql += " and stock.state=" + StockState.ON_STOCK;

		if (!strategy.isUseLockedStock()) {
			jpql += " and stock.lock=0 ";
		}
		if (!strategy.isUseLockedLot()) {
			jpql += " and not exists( select 1 FROM " + Lot.class.getSimpleName()
					+ " lot where stock.lot=lot and lot.lock!=0) ";
		}

		jpql += " and stock.amount > 0 ";

		if (client != null) {
			jpql += " and stock.client =:client ";
		}

		jpql += " and stock.itemData =:itemData ";

		if (lot != null) {
			jpql += " and stock.lot =:lot ";
		}

		jpql += " order by ";
		jpql += "stock.strategyDate, stock.amount, stock.created ";

		logger.log(Level.FINER, logStr + "QUERY: " + jpql);

		Query query = manager.createQuery(jpql);

		if (client != null) {
			query.setParameter("client", client);
		}
		query.setParameter("itemData", itemData);

		if (lot != null) {
			query.setParameter("lot", lot);
		}

		List<Object[]> results = query.getResultList();
		List<FixAssignment> fixList = fixService.readList(itemData, null, true);
		List<PickingStockUnit> stockList = new ArrayList<>(results.size());
		for (Object[] result : results) {
			long stockId = (long) result[0];
			BigDecimal amount = (BigDecimal) result[1];
			BigDecimal reservedAmount = (BigDecimal) result[2];
			long locationId = (long) result[3];
			String locationName = (String) result[4];
			long unitLoadId = (long) result[5];
			boolean unitLoadOpened = (boolean) result[6];
			String usages = (String) result[7];

			boolean onFixedLocation = false;
			BigDecimal maxFixPickAmount = null;
			for (FixAssignment fix : fixList) {
				if (fix.getStorageLocation().getId().longValue() == locationId) {
					onFixedLocation = true;
					maxFixPickAmount = fix.getMaxPickAmount();
				}
			}

			PickingStockUnit stock = new PickingStockUnit(stockId, amount, reservedAmount, locationName, unitLoadId,
					unitLoadOpened, usages, onFixedLocation, maxFixPickAmount);

			stockList.add(stock);
		}

		logger.log(Level.FINER, logStr + "found stocks: " + stockList.size());
		return stockList;
	}

	private boolean isValidForCompleteHandling(PickingStockUnit stock) {
		if (stock.unitLoadOpened) {
			return false;
		}
		if (stock.reservedAmount.compareTo(BigDecimal.ZERO) > 0) {
			return false;
		}
		if (!stock.locationUseForStorage) {
			return false;
		}
		if (stock.onFixedLocation) {
			return false;
		}
		if (stock.mixed == null) {
			stock.mixed = entityService.exists(StockUnit.class, "unitLoad.id", stock.unitLoadId, stock.stockId);
		}
		if (stock.mixed) {
			return false;
		}

		return true;
	}

	private static class PickingStockUnit implements Serializable {
		private static final long serialVersionUID = 1L;

		public long stockId;
		public BigDecimal amount;
		public BigDecimal reservedAmount;
		public String locationName;
		public long unitLoadId;
		public boolean unitLoadOpened = true;
		public boolean onFixedLocation = false;
		public boolean locationUseForPicking = false;
		public boolean locationUseForStorage = false;
		public BigDecimal availableAmount;
		public BigDecimal maxFixPickAmount;
		public Boolean mixed = null;

		public PickingStockUnit(long stockId, BigDecimal amount, BigDecimal reservedAmount, String locationName,
				long unitLoadId, boolean opened, String usages, boolean onFixedLocation, BigDecimal maxFixPickAmount) {
			this.stockId = stockId;
			this.locationName = locationName;
			this.amount = amount;
			this.reservedAmount = reservedAmount;
			this.availableAmount = (reservedAmount == null ? amount : amount.subtract(reservedAmount));
			this.unitLoadId = unitLoadId;
			this.unitLoadOpened = opened;
			this.onFixedLocation = onFixedLocation;
			this.maxFixPickAmount = maxFixPickAmount;
			List<String> usageList = ListUtils.stringToList(usages);
			if (usageList.contains(AreaUsages.PICKING)) {
				this.locationUseForPicking = true;
			}
			if (usageList.contains(AreaUsages.STORAGE)) {
				this.locationUseForStorage = true;
			}
		}

		public String toString() {
			return "location=" + locationName + ", amount=" + amount;
		}
	}

}
