/* 
Copyright 2019-2021 Matthias Krane
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
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.mywms.model.Client;

import de.wms2.mywms.client.ClientBusiness;
import de.wms2.mywms.entity.GenericEntityService;
import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.inventory.StockState;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.inventory.UnitLoadTypeUsages;
import de.wms2.mywms.location.Area;
import de.wms2.mywms.location.AreaUsages;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.strategy.FixAssignment;
import de.wms2.mywms.strategy.FixAssignmentEntityService;
import de.wms2.mywms.strategy.OrderStrategy;
import de.wms2.mywms.strategy.OrderStrategyCompleteHandling;
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

	public static final BigDecimal MAX_SCALE_FACTOR = new BigDecimal("10000");

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

	public boolean isStrategyRequiresCompleteAmount(OrderStrategy strategy) {
		if (strategy.getCompleteHandling() == OrderStrategyCompleteHandling.AMOUNT_SMALLEST_DIFF) {
			return false;
		}
		return true;
	}

	/**
	 * Find the first source stock which can be used in a picking order.
	 */
	public StockUnit findFirstSourceStock(ItemData itemData, BigDecimal amount, Client client, String lotNumber,
			OrderStrategy strategy) throws BusinessException {
		String logStr = "findFirstSourceStock ";

		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
			logger.log(Level.WARNING, logStr + "No amount. No pick. itemData=" + itemData);
			return null;
		}

		if (strategy == null) {
			strategy = orderStrategyService.getDefault(client);
		}

		StockUnit stock = findFirstSourceStockIntern(itemData, client, lotNumber, amount, strategy);
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
			stock = findFirstSourceStockIntern(itemData, clientBusiness.getSystemClient(), lotNumber, amount, strategy);
			if (stock != null) {
				return stock;
			}
		}

		logger.log(Level.FINE,
				logStr + "No stock available for system client. search for all clients. requesting client=" + client
						+ ", itemData=" + itemData);
		stock = findFirstSourceStockIntern(itemData, null, lotNumber, amount, strategy);

		return stock;
	}

	private StockUnit findFirstSourceStockIntern(ItemData itemData, Client client, String lotNumber, BigDecimal amount,
			OrderStrategy strategy) throws BusinessException {
		String logStr = "findFirstSourceStockIntern ";

		logger.log(Level.FINE, logStr + "itemData=" + itemData + ", amount=" + amount + ",client=" + client
				+ ", lotNumber=" + lotNumber + ", strategy=" + strategy);

		List<PickingStockUnit> stockList = readSourceStockList(itemData, client, lotNumber, strategy);
		if (stockList.isEmpty()) {
			logger.log(Level.INFO, logStr + "No stock. client=" + client + ", itemData=" + itemData + ", lotNumber="
					+ lotNumber + ", strategy=" + strategy);
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

		// Find one complete unit load with the exact matching amount
		// Here explicit look for complete unit load handling and selected preferred
		// matching.
		// Flags: preferMatching and completeHandling!=NONE
		if (strategy.isPreferMatching() && strategy.getCompleteHandling() != OrderStrategyCompleteHandling.NONE) {
			for (PickingStockUnit stock : stockList) {
				if (stock.availableAmount.compareTo(amount) != 0) {
					continue;
				}
				if (!isValidForCompleteHandling(stock)) {
					continue;
				}

				logger.log(Level.FINE, logStr + "Use complete single unit load with matching amount. amount=" + amount
						+ ", stock=" + stock);
				return manager.find(StockUnit.class, stock.stockId);
			}
		}

		// Find one complete unit load with the exact matching amount
		// Flags: CompleteHandling.AMOUNT_FIRST_MATCH
		if (strategy.getCompleteHandling() == OrderStrategyCompleteHandling.AMOUNT_FIRST_MATCH) {
			for (PickingStockUnit stock : stockList) {
				if (stock.availableAmount.compareTo(amount) != 0) {
					continue;
				}
				if (!isValidForCompleteHandling(stock)) {
					continue;
				}

				logger.log(Level.FINE, logStr + "Use complete single unit load with matching amount. amount=" + amount
						+ ", stock=" + stock);
				return manager.find(StockUnit.class, stock.stockId);
			}
		}

		// Find complete unit loads with exact matching amount in sum.
		// Flags: CompleteHandling.AMOUNT_MATCH
		if (strategy.getCompleteHandling() == OrderStrategyCompleteHandling.AMOUNT_MATCH) {
			PickingStockUnit stock = combineFirstMatch(stockList, amount);
			if (stock != null) {
				logger.log(Level.FINE,
						logStr + "Use combinable complete matching unit load. amount=" + amount + ", stock=" + stock);
				return manager.find(StockUnit.class, stock.stockId);
			}
		}

		// Find complete unit load with at least the amount
		// Flags: CompleteHandling.AMOUNT_FIRST_PLUS
		if (strategy.getCompleteHandling() == OrderStrategyCompleteHandling.AMOUNT_FIRST_PLUS) {
			for (PickingStockUnit stock : stockList) {
				if (stock.availableAmount.compareTo(amount) < 0) {
					continue;
				}
				if (!isValidForCompleteHandling(stock)) {
					continue;
				}

				logger.log(Level.FINE, logStr + "Use complete first unit load with al least requested amount. amount="
						+ amount + ", stock=" + stock);
				return manager.find(StockUnit.class, stock.stockId);
			}
		}

		// Find complete unit loads with minimum difference
		// Flags: CompleteHandling.AMOUNT_SMALLEST_DIFF
		if (strategy.getCompleteHandling() == OrderStrategyCompleteHandling.AMOUNT_SMALLEST_DIFF) {
			PickingStockUnit stock = combineSmallestDiff(stockList, amount);
			if (stock != null) {
				logger.log(Level.FINE, logStr + "Use combinable complete unit loads with smallest diff. amount="
						+ amount + ", stock=" + stock);
				return manager.find(StockUnit.class, stock.stockId);
			}
		}

		// Find complete unit loads with minimum positive difference
		// Flags: CompleteHandling.AMOUNT_SMALLEST_PLUS
		if (strategy.getCompleteHandling() == OrderStrategyCompleteHandling.AMOUNT_SMALLEST_PLUS) {
			PickingStockUnit stock = combineSmallestPlus(stockList, amount);
			if (stock != null) {
				logger.log(Level.FINE, logStr + "Use combinable complete unit loads with smallest plus diff. amount="
						+ amount + ", stock=" + stock);
				return manager.find(StockUnit.class, stock.stockId);
			}
		}

		// No valid stock found to handle request with only complete unit loads
		// Flags: Not CompleteHandling.NONE
		if (strategy.getCompleteHandling() != OrderStrategyCompleteHandling.NONE) {
			logger.log(Level.INFO,
					logStr + "No usable stock found. Only complete unit loads are permitted. candidates="
							+ stockList.size() + ", itemData=" + itemData + ", amount=" + amount + ", client=" + client
							+ ", lotNumber=" + lotNumber);
			return null;
		}

		// Find complete unit load with exact matching amount.
		// Flags: preferComplete and preferMatching
		if (strategy.isPreferComplete() && strategy.isPreferMatching()) {
			for (PickingStockUnit stock : stockList) {
				if (stock.availableAmount.compareTo(amount) != 0) {
					continue;
				}
				if (!isValidForCompleteHandling(stock)) {
					continue;
				}

				logger.log(Level.FINE, logStr + "Use complete unit load. amount=" + amount + ", stock=" + stock);
				return manager.find(StockUnit.class, stock.stockId);
			}
		}

		// Find complete unit load with less than the requested amount.
		// An additional pick will be generated.
		// Flags: preferComplete
		if (strategy.isPreferComplete()) {
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

		// Find any usable stock for picking with exact matching amount
		// Flags: preferMatching
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

		// Find stock on fixed picking location
		// The maximum amount to take from fixed picking locations may be limited
		// Fixed picking location overrides FIFO
		for (PickingStockUnit stock : stockList) {
			if (!stock.onFixedLocation) {
				continue;
			}
			if (!stock.locationUseForPicking) {
				continue;
			}
			if (stock.maxFixPickAmount != null && stock.maxFixPickAmount.compareTo(BigDecimal.ZERO) > 0
					&& stock.maxFixPickAmount.compareTo(amount) < 0) {
				logger.log(Level.FINE,
						logStr + "Amount for fixed location exceeded. location=" + stock.locationName + ", itemData="
								+ itemData + ", amount=" + amount + ", maxFixPickAmount=" + stock.maxFixPickAmount);
				continue;
			}

			logger.log(Level.FINE,
					logStr + "Use stock on fixed picking location. amount=" + amount + ", stock=" + stock);
			return manager.find(StockUnit.class, stock.stockId);
		}

		// Find any usable stock for picking on not fixed picking location
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

			logger.log(Level.FINE,
					logStr + "Use stock on not fixed picking location. amount=" + amount + ", stock=" + stock);
			return manager.find(StockUnit.class, stock.stockId);
		}

		// Find any usable stock for picking. Including fixed picking locations.
		for (PickingStockUnit stock : stockList) {
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
				+ ", itemData=" + itemData + ", lotNumber=" + lotNumber + ", amount=" + amount);
		return null;
	}

	/**
	 * Optimizer score for minimal difference amount
	 * <p>
	 * The sore value maps to the amount difference.
	 */
	private static Long scoreDiffAmount(Collection<PickingStockUnit> values, PickingStockUnit target) {
		BigDecimal sum = BigDecimal.ZERO;
		for (PickingStockUnit value : values) {
			if (value == null) {
				return null;
			}
			sum = sum.add(value.amount);
		}
		return sum.subtract(target.amount).multiply(MAX_SCALE_FACTOR).longValue();
	}

	/**
	 * Optimizer score for minimal positive difference amount
	 * <p>
	 * The sore value maps to the amount difference. Negative differences get a
	 * normally unreachable negative score.
	 */
	private static Long scorePositiveDiffAmount(Collection<PickingStockUnit> values, PickingStockUnit target) {
		BigDecimal sum = BigDecimal.ZERO;
		for (PickingStockUnit value : values) {
			if (value == null) {
				return null;
			}
			sum = sum.add(value.amount);
		}
		if (sum.compareTo(target.amount) < 0) {
			// return a very big negative score
			return Long.MIN_VALUE + 1 + (sum.multiply(MAX_SCALE_FACTOR).longValue());
		}
		return sum.subtract(target.amount).multiply(MAX_SCALE_FACTOR).longValue();
	}

	private PickingStockUnit combineFirstMatch(List<PickingStockUnit> candidates, BigDecimal requestedAmount) {
		List<PickingStockUnit> usables = candidates.stream().filter(stock -> isValidForCompleteHandling(stock))
				.collect(Collectors.toList());
		PickingStockUnit optimalTarget = new PickingStockUnit(requestedAmount);
		List<PickingStockUnit> combinables = new Optimizer().findBestCombination(usables,
				PickingStockFinder::scoreDiffAmount, optimalTarget);
		if (combinables == null || combinables.isEmpty()) {
			return null;
		}
		if (scoreDiffAmount(combinables, optimalTarget) != 0) {
			return null;
		}
		return combinables.get(0);
	}

	private PickingStockUnit combineSmallestDiff(List<PickingStockUnit> candidates, BigDecimal requestedAmount) {
		List<PickingStockUnit> usables = candidates.stream().filter(stock -> isValidForCompleteHandling(stock))
				.collect(Collectors.toList());
		PickingStockUnit optimalTarget = new PickingStockUnit(requestedAmount);
		List<PickingStockUnit> combinables = new Optimizer().findBestCombination(usables,
				PickingStockFinder::scoreDiffAmount, optimalTarget);
		if (combinables == null || combinables.isEmpty()) {
			return null;
		}
		return combinables.get(0);
	}

	private PickingStockUnit combineSmallestPlus(List<PickingStockUnit> candidates, BigDecimal requestedAmount) {
		List<PickingStockUnit> usables = candidates.stream().filter(stock -> isValidForCompleteHandling(stock))
				.collect(Collectors.toList());
		PickingStockUnit optimalTarget = new PickingStockUnit(requestedAmount);
		List<PickingStockUnit> combinables = new Optimizer().findBestCombination(usables,
				PickingStockFinder::scorePositiveDiffAmount, optimalTarget);
		if (combinables == null || combinables.isEmpty()) {
			return null;
		}
		return combinables.get(0);
	}

	/**
	 * Find all usable source stocks which can be used in a picking order.
	 */
	public List<StockUnit> findSourceStockList(ItemData itemData, Client client, String lotNumber,
			OrderStrategy strategy) {
		if (strategy == null) {
			strategy = orderStrategyService.getDefault(client);
		}

		List<PickingStockUnit> stockList = readSourceStockList(itemData, client, lotNumber, strategy);
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
	private List<PickingStockUnit> readSourceStockList(ItemData itemData, Client client, String lotNumber,
			OrderStrategy strategy) {
		String logStr = "readSourceStockList ";
		logger.log(Level.FINE, logStr + " itemData=" + itemData + ", client=" + client + ", lotNumber=" + lotNumber
				+ ", strategy=" + strategy);

		String jpql = "";

		jpql += " select stock.id, stock.amount, stock.reservedAmount, ";
		jpql += " location.id, location.name, unitLoad.id, unitLoad.opened, ";
		jpql += " role.usages, unitLoadType.usages";
		jpql += " from " + StockUnit.class.getName() + " stock, ";
		jpql += UnitLoad.class.getName() + " unitLoad, ";
		jpql += UnitLoadType.class.getName() + " unitLoadType, ";
		jpql += StorageLocation.class.getName() + " location, ";
		jpql += Area.class.getName() + " role ";
		jpql += "where unitLoad=stock.unitLoad ";
		jpql += " and unitLoadType = unitLoad.unitLoadType ";
		jpql += " and location = unitLoad.storageLocation ";
		jpql += " and role=location.area ";

		jpql += " and (role.usages like '%" + AreaUsages.PICKING + "%'";
		jpql += "      or role.usages like '%" + AreaUsages.STORAGE + "%') ";
		jpql += " and stock.state=" + StockState.ON_STOCK;

		if (!strategy.isUseLockedStock()) {
			jpql += " and stock.lock=0 ";
			jpql += " and unitLoad.lock=0 ";
		}
		jpql += " and location.lock=0 ";

		jpql += " and stock.amount > 0 ";

		if (client != null) {
			jpql += " and stock.client =:client ";
		}

		jpql += " and stock.itemData =:itemData ";

		if (!StringUtils.isBlank(lotNumber)) {
			jpql += " and stock.lotNumber =:lotNumber ";
		}

		jpql += " order by ";
		jpql += "stock.strategyDate, stock.amount, stock.created ";

		logger.log(Level.FINER, logStr + "QUERY: " + jpql);

		Query query = manager.createQuery(jpql);

		if (client != null) {
			query.setParameter("client", client);
		}
		query.setParameter("itemData", itemData);

		if (!StringUtils.isBlank(lotNumber)) {
			query.setParameter("lotNumber", lotNumber);
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
			String areaUsages = (String) result[7];
			String unitLoadTypeUsages = (String) result[8];

			boolean onFixedLocation = false;
			BigDecimal maxFixPickAmount = null;
			for (FixAssignment fix : fixList) {
				if (fix.getStorageLocation().getId().longValue() == locationId) {
					onFixedLocation = true;
					maxFixPickAmount = fix.getMaxPickAmount();
				}
			}

			PickingStockUnit stock = new PickingStockUnit(stockId, amount, reservedAmount, locationName, unitLoadId,
					unitLoadOpened, areaUsages, unitLoadTypeUsages, onFixedLocation, maxFixPickAmount);

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
		if (!stock.unitLoadForComplete) {
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
		public boolean unitLoadForComplete = false;
		public BigDecimal availableAmount;
		public BigDecimal maxFixPickAmount;
		public Boolean mixed = null;

		public PickingStockUnit(BigDecimal amount) {
			this.amount = amount;
		}

		public PickingStockUnit(long stockId, BigDecimal amount, BigDecimal reservedAmount, String locationName,
				long unitLoadId, boolean opened, String areaUsages, String unitLoadTypUsages, boolean onFixedLocation,
				BigDecimal maxFixPickAmount) {
			this.stockId = stockId;
			this.locationName = locationName;
			this.amount = amount;
			this.reservedAmount = reservedAmount;
			this.availableAmount = (reservedAmount == null ? amount : amount.subtract(reservedAmount));
			this.unitLoadId = unitLoadId;
			this.unitLoadOpened = opened;
			this.onFixedLocation = onFixedLocation;
			this.maxFixPickAmount = maxFixPickAmount;
			List<String> usages = ListUtils.stringToList(areaUsages);
			if (usages.contains(AreaUsages.PICKING)) {
				this.locationUseForPicking = true;
			}
			if (usages.contains(AreaUsages.STORAGE)) {
				this.locationUseForStorage = true;
			}
			usages = ListUtils.stringToList(unitLoadTypUsages);
			if (usages.contains(UnitLoadTypeUsages.COMPLETE)) {
				this.unitLoadForComplete = true;
			}
		}

		public String toString() {
			return "location=" + locationName + ", amount=" + amount;
		}
	}

}
