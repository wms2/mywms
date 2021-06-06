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
package de.wms2.mywms.inventory;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.ObserverException;
import javax.inject.Inject;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.mywms.model.BasicEntity;
import org.mywms.model.Client;
import org.mywms.model.User;

import de.wms2.mywms.client.ClientBusiness;
import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.location.StorageLocationEntityService;
import de.wms2.mywms.picking.PickingOrderLine;
import de.wms2.mywms.picking.PickingOrderLineEntityService;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.product.PackagingUnit;
import de.wms2.mywms.sequence.SequenceBusiness;
import de.wms2.mywms.strategy.FixAssignment;
import de.wms2.mywms.strategy.FixAssignmentEntityService;
import de.wms2.mywms.strategy.LocationReserver;
import de.wms2.mywms.strategy.OrderState;
import de.wms2.mywms.user.UserBusiness;
import de.wms2.mywms.util.Wms2BundleResolver;
import de.wms2.mywms.util.Wms2Constants;

/**
 * @author krane
 *
 */
@Stateless
public class InventoryBusiness {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private PersistenceManager manager;
	@Inject
	private UserBusiness userBusiness;
	@Inject
	private StorageLocationEntityService locationService;
	@Inject
	private FixAssignmentEntityService fixService;
	@Inject
	private UnitLoadEntityService unitLoadService;
	@Inject
	private LocationReserver locationReserver;
	@Inject
	private JournalHandler recordService;
	@Inject
	private StockUnitEntityService stockService;
	@Inject
	private SequenceBusiness sequenceBusiness;
	@Inject
	private Event<UnitLoadTrashEvent> unitLoadTrashEvent;
	@Inject
	private Event<StockUnitTrashEvent> stockUnitTrashEvent;
	@Inject
	private Event<StockUnitChangeAmountEvent> stockUnitChangeAmountEvent;
	@Inject
	private Event<StockUnitStateChangeEvent> stockUnitStateChangeEvent;
	@Inject
	private Event<UnitLoadTransferLocationEvent> unitLoadTransferLocationEvent;
	@Inject
	private Event<LockChangeEvent> lockChangeEvent;
	@Inject
	private Event<UnitLoadTransferCarrierEvent> transferCarrierEvent;
	@Inject
	private StockUnitEntityService stockUnitService;
	@Inject
	private TrashHandler trashHandler;
	@Inject
	private PickingOrderLineEntityService pickingOrderLineEntityService;
	@Inject
	private ClientBusiness clientBusiness;

	public void cleanupDeleted() throws BusinessException {
		List<UnitLoad> unitLoads = unitLoadService.readList(null, null, null, StockState.DELETABLE, null);
		for (UnitLoad unitLoad : unitLoads) {
			trashHandler.removeUnitLoad(unitLoad);
		}
	}

	// ***********************************************************************
	// UnitLoad handling
	// ***********************************************************************
	/**
	 * Change the state of a unit load and all it's stock units.
	 * <p>
	 * The state is just written to the entities. NO CHECK!<br>
	 * Be sure what you are doing!
	 */
	public void changeState(UnitLoad unitLoad, int state) throws BusinessException {
		unitLoad.setState(state);
		for (StockUnit stockUnit : stockUnitService.readByUnitLoad(unitLoad)) {
			int oldState = stockUnit.getState();
			stockUnit.setState(state);
			fireStockUnitStateChangeEvent(stockUnit, oldState);
		}
	}

	public UnitLoad createUnitLoad(Client client, String label, UnitLoadType unitLoadType, StorageLocation location,
			int state, String activityCode, User operator, String note) throws BusinessException {
		String logStr = "createUnitLoad ";
		logger.log(Level.FINE, logStr + "label=" + label);

		UnitLoad unitLoad = unitLoadService.readByLabel(label);
		if (unitLoad != null) {
			return unitLoad;
		}
		if (unitLoadType == null) {
			logger.log(Level.INFO, logStr + "Missing parameter unitLoadType. Abort");
			throw new BusinessException(Wms2BundleResolver.class, "Inventory.missingUnitLoadType");
		}
		if (location == null) {
			logger.log(Level.INFO, logStr + "Missing parameter storageLocation. Abort");
			throw new BusinessException(Wms2BundleResolver.class, "Inventory.missingStorageLocation");
		}

		if (operator == null) {
			operator = userBusiness.getCurrentUser();
		}
		if (StringUtils.isEmpty(label)) {
			label = sequenceBusiness.readNextValue(UnitLoad.class, "labelId");
		}

		unitLoad = unitLoadService.create(client, label, unitLoadType, location);
		unitLoad.setState(state);

		locationReserver.allocateLocation(location, unitLoad);

		recordService.recordCreation(unitLoad, activityCode, operator, note);

		fireUnitLoadTransferLocationEvent(unitLoad, null, location, activityCode, operator, note);

		return unitLoad;
	}

	/**
	 * Check whether unit load transfer is allowed. Use this before using
	 * transferUnitLoad(). transferUnitLoad() just transfers. It does not make
	 * further logical checks.
	 */
	public void checkTransferUnitLoad(UnitLoad unitLoad, StorageLocation destination, boolean checkLock)
			throws BusinessException {
		String logStr = "checkTransferUnitLoad ";

		// if the destination is permanent assigned to a special itemData
		// check if stocks on the unit load only contain that itemData
		List<ItemData> fixItemDatas = fixService.readItemDataListByLocation(destination);
		if (fixItemDatas.size() > 0) {
			boolean hasChilds = unitLoadService.hasChilds(unitLoad);

			if (hasChilds) {
				logger.log(Level.SEVERE,
						logStr + "Carrier unit loads are not allowed on fixed locations. unitLoad=" + unitLoad);
				throw new BusinessException(Wms2BundleResolver.class, "Inventory.carrierNotOnFixLocation");
			}

			List<StockUnit> stocksOnUnitLoad = stockUnitService.readByUnitLoad(unitLoad);
			for (StockUnit stockUnit : stocksOnUnitLoad) {
				if (!fixItemDatas.contains(stockUnit.getItemData())) {
					logger.log(Level.SEVERE, logStr + "Wrong itemData for fix location. itemData="
							+ stockUnit.getItemData() + ", location=" + destination);
					throw new BusinessException(Wms2BundleResolver.class, "Inventory.wrongItemOnFixAssignment");
				}
			}
		}

		locationReserver.checkAllocateLocation(destination, unitLoad.getClient(), unitLoad.getUnitLoadType(), checkLock,
				true);

	}

	public UnitLoad transferUnitLoad(UnitLoad unitLoad, StorageLocation destination, String activityCode, User operator,
			String note) throws BusinessException {
		String logStr = "transferUnitLoad ";
		logger.log(Level.FINE, logStr + "unitLoad=" + unitLoad + ", destination=" + destination);

		StorageLocation source = unitLoad.getStorageLocation();
		if (source.equals(destination)) {
			logger.log(Level.FINE, logStr + "Will not move. source and destination location are equal. unitload="
					+ unitLoad + ", location=" + destination);
			return unitLoad;
		}

		boolean deallocateLocation = true;

		UnitLoad carrier = unitLoad.getCarrierUnitLoad();
		if (carrier != null) {
			// Maybe the carrier will be no more carrier
			if (!unitLoadService.hasOtherChilds(carrier, unitLoad)) {
				carrier.setCarrier(false);
			}
			unitLoad.setCarrierUnitLoad(null);
			deallocateLocation = false;
		}

		if (deallocateLocation) {
			locationReserver.deallocateLocation(source, unitLoad, true);
		}
		locationReserver.allocateLocation(destination, unitLoad);

		if (operator == null) {
			operator = userBusiness.getCurrentUser();
		}

		transferToLocationRecursive(unitLoad, source, destination, activityCode, operator, note, 0);

		boolean areUnitLoadsOnLocation = unitLoadService.existsByLocation(source);
		if (!areUnitLoadsOnLocation) {
			locationReserver.deallocateLocation(source);
		}

		return unitLoad;
	}

	private void transferToLocationRecursive(UnitLoad unitLoad, StorageLocation source, StorageLocation dest,
			String activityCode, User operator, String note, int depth) throws BusinessException {
		String logStr = "transferToLocation ";

		if (depth > Wms2Constants.MAX_CARRIER_DEPTH) {
			logger.log(Level.SEVERE, logStr + "Cannot transfer unit load with more than "
					+ Wms2Constants.MAX_CARRIER_DEPTH + " carriers");
			throw new BusinessException(Wms2BundleResolver.class, "Inventory.carrierMaxDepthExceeded");
		}

		unitLoad.setStorageLocation(dest);

		if (operator == null) {
			operator = userBusiness.getCurrentUser();
		}

		recordService.recordTransferUnitLoad(unitLoad, unitLoad, source, dest, activityCode, operator, note);

		fireUnitLoadTransferLocationEvent(unitLoad, source, dest, activityCode, operator, note);

		List<UnitLoad> childs = unitLoadService.readChilds(unitLoad);
		for (UnitLoad child : childs) {
			if (child.equals(unitLoad)) {
				logger.log(Level.SEVERE, logStr + "Selfreference detected! A unitLoad is its onw carrier. label="
						+ unitLoad.getLabelId());
				continue;
			}
			transferToLocationRecursive(child, source, dest, activityCode, operator, note, depth + 1);
		}
	}

	/**
	 * Remove a complete unit load. All stocks will be killed.
	 */
	public void deleteUnitLoad(UnitLoad unitLoad, String activityCode, User operator, String note)
			throws BusinessException {
		String logStr = "deleteUnitLoad ";
		logger.log(Level.FINE, logStr + "unitLoad=" + unitLoad);

		if (unitLoad == null) {
			logger.log(Level.WARNING, logStr + "UnitLoad cannot be read. Seems to be already gone");
			return;
		}

		if (operator == null) {
			operator = userBusiness.getCurrentUser();
		}

		StorageLocation trash = locationService.getTrash();
		if (!unitLoad.getStorageLocation().equals(trash)) {
			unitLoad = transferUnitLoad(unitLoad, trash, activityCode, operator, note);
		}

		trashUnitLoadRecursive(unitLoad, activityCode, operator, note, 0);
	}

	private UnitLoad trashUnitLoadRecursive(UnitLoad unitLoad, String activityCode, User operator, String note,
			int depth) throws BusinessException {
		String logStr = "trashUnitLoadRecursive ";
		logger.log(Level.FINE, logStr + "unitLoad=" + unitLoad);

		if (depth > Wms2Constants.MAX_CARRIER_DEPTH) {
			logger.log(Level.SEVERE, logStr + "Cannot transfer unit load with so much carrier levels. "
					+ Wms2Constants.MAX_CARRIER_DEPTH);
			throw new BusinessException(Wms2BundleResolver.class, "Inventory.carrierMaxDepthExceeded");
		}

		List<StockUnit> stocksOnUnitLoad = stockUnitService.readByUnitLoad(unitLoad);
		for (StockUnit stock : stocksOnUnitLoad) {
			int stockState = stock.getState();
			if (stock.getState() < StockState.DELETABLE) {
				stock.setState(StockState.DELETABLE);
				fireStockUnitStateChangeEvent(stock, stockState);

				BigDecimal oldAmount = stock.getAmount();
				if (oldAmount.compareTo(BigDecimal.ZERO) > 0) {
					stock.setAmount(BigDecimal.ZERO);
					recordService.recordChangeAmount(stock.getClient(), stock, oldAmount.negate(), activityCode,
							operator, note);
				}
			}
		}

		String suffix = "-" + unitLoad.getId();
		if (!unitLoad.getLabelId().endsWith(suffix)) {
			String labelNew = unitLoad.getLabelId() + suffix;
			unitLoad.setLabelId(labelNew);
		}

		unitLoad.setState(StockState.DELETABLE);
		unitLoad.setCarrierUnitLoad(null);
		fireUnitLoadTrashEvent(unitLoad, activityCode, operator, note);

		List<UnitLoad> childs = unitLoadService.readChilds(unitLoad);
		for (UnitLoad child : childs) {
			if (child.equals(unitLoad)) {
				logger.log(Level.SEVERE,
						logStr + "Selfreference detected! A unitLoad is its onw carrier. unitLoad=" + unitLoad);
				continue;
			}
			trashUnitLoadRecursive(child, activityCode, operator, note, depth + 1);
		}

		return unitLoad;
	}

	/**
	 * Transfer a unit load to clearing location.
	 */
	public UnitLoad transferToClearing(UnitLoad unitLoad, String activityCode, User operator, String note)
			throws BusinessException {
		String logStr = "sendToClearing ";
		logger.log(Level.FINE, logStr + "unitLoad=" + unitLoad);

		if (operator == null) {
			operator = userBusiness.getCurrentUser();
		}

		StorageLocation location = locationService.getClearing();
		unitLoad = transferUnitLoad(unitLoad, location, activityCode, operator, note);

		unitLoad = addUnitLoadLockRecursive(unitLoad, LockType.GENERAL, operator, note, true, 0);

		return unitLoad;
	}

	/**
	 * Add a lock to the UnitLoad. The difference to direct usage of the
	 * LockBoundary is that child UnitLoads of carriers and StockUnits can be
	 * considered
	 * 
	 * @param processStock  The StockUnits will be processed too
	 * @param processChilds The child UnitLoads of a carrier will be processed too
	 */
	public UnitLoad addUnitLoadLock(UnitLoad unitLoad, int lock, User operator, String note, boolean processStock,
			boolean processChilds) throws BusinessException {
		String logStr = "addUnitLoadLock ";
		logger.log(Level.FINE, logStr + "unitLoad=" + unitLoad + ", lock=" + lock);

		if (operator == null) {
			operator = userBusiness.getCurrentUser();
		}

		if (processChilds) {
			return addUnitLoadLockRecursive(unitLoad, lock, operator, note, processStock, 0);
		}
		return addUnitLoadLock(unitLoad, lock, operator, note, processStock);
	}

	private UnitLoad addUnitLoadLockRecursive(UnitLoad unitLoad, int lock, User operator, String note,
			boolean processStock, int depth) throws BusinessException {
		String logStr = "addUnitLoadLockRecursive ";

		if (depth > Wms2Constants.MAX_CARRIER_DEPTH) {
			logger.log(Level.SEVERE, logStr + "Cannot transfer unit load with so much carrier levels. "
					+ Wms2Constants.MAX_CARRIER_DEPTH);
			throw new BusinessException(Wms2BundleResolver.class, "Inventory.carrierMaxDepthExceeded");
		}

		if (operator == null) {
			operator = userBusiness.getCurrentUser();
		}

		unitLoad = addUnitLoadLock(unitLoad, lock, operator, note, processStock);

		List<UnitLoad> childs = unitLoadService.readChilds(unitLoad);
		for (UnitLoad child : childs) {
			if (child.equals(unitLoad)) {
				logger.log(Level.SEVERE,
						logStr + "Selfreference detected! A unitLoad is its onw carrier. unitLoad=" + unitLoad);
				continue;
			}
			unitLoad = addUnitLoadLockRecursive(child, lock, operator, note, processStock, depth + 1);
		}
		return unitLoad;
	}

	private UnitLoad addUnitLoadLock(UnitLoad unitLoad, int lock, User operator, String note, boolean processStock)
			throws BusinessException {
		int oldUnitLoadLock = unitLoad.getLock();
		if (lock != oldUnitLoadLock) {
			unitLoad.setLock(lock);
			fireLockChangeEvent(unitLoad, oldUnitLoadLock);
		}
		if (processStock) {
			List<StockUnit> stocksOnUnitLoad = stockUnitService.readByUnitLoad(unitLoad);
			for (StockUnit stock : stocksOnUnitLoad) {
				addStockUnitLock(stock, lock);
			}
		}

		return unitLoad;
	}

	public StockUnit addStockUnitLock(StockUnit stockUnit, int lock) throws BusinessException {
		int oldStockLock = stockUnit.getLock();
		if (lock != oldStockLock) {
			stockUnit.setLock(lock);
			fireLockChangeEvent(stockUnit, oldStockLock);
		}

		return stockUnit;
	}

	/**
	 * Check and process empty UnitLoad handling. Not ManageEmpties UnitLoadTypes
	 * are send to trash. Others stay where they are.
	 */
	private void processEmpties(UnitLoad unitLoad, boolean manageEmpties, String activityCode, User operator,
			String note) throws BusinessException {
		String logStr = "processEmpties ";
		if (unitLoad == null) {
			return;
		}
		if (manageEmpties && unitLoad.getUnitLoadType().isManageEmpties()) {
			logger.log(Level.FINE, logStr + "Manage empties. Leave unitLoad=" + unitLoad + "on location="
					+ unitLoad.getStorageLocation());
			return;
		}

		boolean isStockOnUnitLoad = stockUnitService.existsByUnitLoad(unitLoad);
		if (isStockOnUnitLoad) {
			logger.log(Level.FINE, logStr + "UnitLoad is not empty. unitLoad=" + unitLoad);
			return;
		}
		boolean hasChilds = unitLoadService.hasChilds(unitLoad);
		if (hasChilds) {
			logger.log(Level.FINE, logStr + "UnitLoad has childs. unitLoad=" + unitLoad);
			return;
		}

		logger.log(Level.FINE, logStr + "UnitLoad is empty: " + unitLoad);

		UnitLoad carrier = unitLoad.getCarrierUnitLoad();

		if (operator == null) {
			operator = userBusiness.getCurrentUser();
		}

		deleteUnitLoad(unitLoad, activityCode, operator, note);

		if (carrier != null) {
			// there must be no other unit loads or stocks on the carrier
			boolean hasCarrierOtherChilds = unitLoadService.hasOtherChilds(carrier, unitLoad);
			if (!hasCarrierOtherChilds) {
				processEmpties(carrier, manageEmpties, activityCode, operator, note);
			}
		}
	}

	public void changeClient(UnitLoad unitLoad, Client client, String activityCode, User operator, String note)
			throws BusinessException {
		String logStr = "changeClient ";
		logger.log(Level.FINE, logStr + "unitLoad=" + unitLoad + ", client=" + client);

		if (unitLoad == null) {
			logger.log(Level.WARNING, logStr + "Missing parameter unitLoad");
			return;
		}
		if (client == null) {
			logger.log(Level.WARNING, logStr + "Missing parameter client");
			return;
		}

		if (operator == null) {
			operator = userBusiness.getCurrentUser();
		}

		List<StockUnit> stockUnits = stockUnitService.readByUnitLoad(unitLoad);
		for (StockUnit stockUnit : stockUnits) {
			Client oldClient = stockUnit.getClient();
			if (client.equals(oldClient)) {
				continue;
			}
			stockUnit.setClient(client);
			recordService.recordChangeClient(stockUnit, oldClient, client, stockUnit.getUnitLoad(),
					stockUnit.getUnitLoad().getStorageLocation(), activityCode, operator, note);
		}
		unitLoad.setClient(client);

	}

	public void checkChangeClient(UnitLoad unitLoad, Client newClient) throws BusinessException {
		String logStr = "checkChangeClient ";
		logger.log(Level.FINE, logStr + "unitLoad=" + unitLoad);

		if (unitLoad == null) {
			logger.log(Level.WARNING, logStr + "Missing parameter unitLoad");
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingUnitLoad");
		}

		List<StockUnit> stockUnits = stockUnitService.readByUnitLoad(unitLoad);
		for (StockUnit stockUnit : stockUnits) {
			ItemData itemData = stockUnit.getItemData();
			// For stocks of a not system clients itemData, only the owning
			// client and system client are valid
			if (!itemData.getClient().isSystemClient() && newClient != null && !newClient.isSystemClient()
					&& !newClient.equals(itemData.getClient())) {
				logger.log(Level.INFO,
						logStr + "Cannot change client owned itemData to different client. itemData client="
								+ itemData.getClient() + ", requested client=" + newClient);
				throw new BusinessException(Wms2BundleResolver.class, "Validator.notSystemClientsItemData");
			}
			if (stockUnit.getReservedAmount().compareTo(BigDecimal.ZERO) > 0) {
				// There must be no reservations on the UnitLoad
				logger.log(Level.INFO, logStr + "There are reservations on a StockUnit. unitLoad=" + unitLoad);
				throw new BusinessException(Wms2BundleResolver.class, "Validator.existsStockUnitReservation");
			}
			List<PickingOrderLine> pickList = pickingOrderLineEntityService.readList(stockUnit, null, null, null, null,
					null, OrderState.PICKED);
			if (!pickList.isEmpty()) {
				logger.log(Level.INFO, logStr + "There must be no picks on the stock unit. unitLoad=" + unitLoad);
				throw new BusinessException(Wms2BundleResolver.class, "Validator.existsStockUnitReservation");
			}
		}
	}

	public Collection<Client> readClientRestrictions(Collection<UnitLoad> unitLoads) throws BusinessException {
		String logStr = "readClientRestrictions ";
		logger.log(Level.FINE, logStr + "unitLoads=" + unitLoads);

		Client singleValidClient = null;
		boolean allClientsValid = true;

		List<StockUnit> stockUnits = new ArrayList<>();
		for (UnitLoad unitLoad : unitLoads) {
			stockUnits.addAll(stockUnitService.readByUnitLoad(unitLoad));
		}
		for (StockUnit stockUnit : stockUnits) {
			ItemData itemData = stockUnit.getItemData();
			// For stocks of a not system clients itemData, only the owning
			// client and system client are valid
			if (!itemData.getClient().isSystemClient()) {
				allClientsValid = false;
				if (singleValidClient != null && !singleValidClient.equals(itemData.getClient())) {
					// Different clients are the only valid one. => So all are
					// invalid
					singleValidClient = null;
					break;
				}
				singleValidClient = itemData.getClient();
			}
		}
		if (!allClientsValid) {
			List<Client> validClients = new ArrayList<>();

			validClients.add(clientBusiness.getSystemClient());
			if (singleValidClient != null) {
				validClients.add(singleValidClient);
			}
			return validClients;
		}

		return null;
	}

	// ***********************************************************************
	// Carrier handling
	// ***********************************************************************

	/**
	 * Move one UnitLoad to a carrier UnitLoad
	 */
	public UnitLoad transferToCarrier(UnitLoad fromUnitLoad, UnitLoad toCarrier, String activityCode, User operator,
			String note) throws BusinessException {
		String logStr = "transferToCarrier ";
		logger.log(Level.FINE, logStr + "fromUnitLoad=" + fromUnitLoad + ", toCarrier=" + toCarrier);

		if (fromUnitLoad.equals(toCarrier)) {
			logger.log(Level.WARNING, logStr
					+ "Source equals destination. Cannot place unitload on itself. fromUnitLoad=" + fromUnitLoad);
			throw new BusinessException(Wms2BundleResolver.class, "Inventory.carrierSelfReference");
		}

		if (unitLoadService.hasParent(toCarrier, fromUnitLoad)) {
			logger.log(Level.WARNING, logStr
					+ "Source has destination as child. Cannot place destination unitload on itself. fromUnitLoad="
					+ fromUnitLoad + ", toUnitLoad=" + toCarrier);
			throw new BusinessException(Wms2BundleResolver.class, "Inventory.carrierSelfReference");
		}

		UnitLoad fromCarrier = fromUnitLoad.getCarrierUnitLoad();
		if (fromCarrier == null) {
			locationReserver.deallocateLocation(fromUnitLoad.getStorageLocation(), fromUnitLoad, true);
		} else if (!unitLoadService.hasOtherChilds(fromCarrier, fromUnitLoad)) {
			fromCarrier.setCarrier(false);
		}

		if (operator == null) {
			operator = userBusiness.getCurrentUser();
		}

		fromUnitLoad.setCarrierUnitLoad(toCarrier);
		fromUnitLoad.setStorageLocation(toCarrier.getStorageLocation());
		toCarrier.setCarrier(true);

		transferToUnitLoadRecursive(fromUnitLoad, toCarrier, activityCode, operator, note, 0);

		return fromUnitLoad;
	}

	private void transferToUnitLoadRecursive(UnitLoad unitLoad, UnitLoad destination, String activityCode,
			User operator, String note, int depth) throws BusinessException {
		String logStr = "transferToUnitLoad ";

		if (depth > Wms2Constants.MAX_CARRIER_DEPTH) {
			logger.log(Level.SEVERE, logStr + "Cannot transfer unit load with more than "
					+ Wms2Constants.MAX_CARRIER_DEPTH + " carriers");
			throw new BusinessException(Wms2BundleResolver.class, "Inventory.carrierMaxDepthExceeded");
		}

		unitLoad.setStorageLocation(destination.getStorageLocation());

		recordService.recordTransferUnitLoad(unitLoad, destination, unitLoad.getStorageLocation(),
				destination.getStorageLocation(), activityCode, operator, note);

		fireUnitLoadTransferCarrierEvent(unitLoad, destination, activityCode, operator, note);

		List<UnitLoad> childs = unitLoadService.readChilds(unitLoad);
		for (UnitLoad child : childs) {
			if (child.equals(unitLoad)) {
				logger.log(Level.SEVERE, logStr + "Selfreference detected! A unitLoad is its onw carrier. label="
						+ unitLoad.getLabelId());
				continue;
			}
			transferToUnitLoadRecursive(child, destination, activityCode, operator, note, depth + 1);
		}
	}

	// ***********************************************************************
	// Stock handling
	// ***********************************************************************
	public void checkCreateStockUnit(ItemData itemData, BigDecimal amount, String lotNumber, Date bestBefore,
			String serialNumber) throws BusinessException {
		String logStr = "checkCreateStockUnit ";

		if (StringUtils.isBlank(lotNumber) && itemData.isLotMandatory()) {
			logger.log(Level.INFO, logStr + "Missing lot. product=" + itemData);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingLot");
		}
		if (bestBefore == null && itemData.isBestBeforeMandatory()) {
			logger.log(Level.INFO, logStr + "Missing best before. product=" + itemData);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingBestBefore");
		}

		if (bestBefore != null) {
			Integer shelflife = itemData.getShelflife();
			if (shelflife != null && shelflife.intValue() > 0) {
				Date min = DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DATE), shelflife);
				if (bestBefore.compareTo(min) < 0) {
					logger.log(Level.WARNING, logStr + "Not enough shelflife. itemData=" + itemData + ", shelflife="
							+ shelflife + ", min best-before=" + min + ", best-before=" + bestBefore);
					throw new BusinessException(Wms2BundleResolver.class, "Validator.invalidShelfLife",
							new Object[] { shelflife });
				}
			}
		}

		if (amount == null) {
			logger.log(Level.INFO, logStr + "no amount given");
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingAmount");
		}

		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			logger.log(Level.INFO, logStr + "zero or negative amount given");
			throw new BusinessException(Wms2BundleResolver.class, "Validator.invalidAmount");
		}

		if (!StringUtils.isBlank(serialNumber)) {
			if (stockService.existsBySerialNumber(itemData, serialNumber)) {
				logger.log(Level.INFO,
						logStr + "Serial-No already exists. product=" + itemData + ", serial=" + serialNumber);
				throw new BusinessException(Wms2BundleResolver.class, "Validator.notUniqueSerial");
			}
		}
	}

	public StockUnit createStock(UnitLoad unitLoad, ItemData itemData, BigDecimal amount, String lotNumber,
			Date bestBefore, String serialNumber, PackagingUnit packagingUnit, int state, String activityCode,
			User operator, String note, boolean sendNotify) throws BusinessException {
		String logStr = "createStock ";
		logger.log(Level.FINE,
				logStr + "itemData=" + itemData + ", amount=" + amount + ", unitLoad=" + unitLoad + ", lotNumber="
						+ lotNumber + ", bestBefore=" + bestBefore + ", serial=" + serialNumber + ", packagingUnit="
						+ packagingUnit);

		if (amount == null) {
			logger.log(Level.WARNING, logStr + "no amount given. abort");
			throw new BusinessException(Wms2BundleResolver.class, "Inventory.missingAmount");
		}
		if (unitLoad == null) {
			logger.log(Level.WARNING, logStr + "no unitload given. abort");
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingUnitLoad");
		}
		Client client = unitLoad.getClient();

		serialNumber = StringUtils.trimToNull(serialNumber);

		if (serialNumber != null) {
			if (stockService.existsBySerialNumber(itemData, serialNumber)) {
				logger.log(Level.WARNING,
						logStr + "Serial-No already exists. itemData=" + itemData + ", serial=" + serialNumber);
				throw new BusinessException(Wms2BundleResolver.class, "Validator.notUniqueSerial");
			}
		}

		StockUnit stockUnit = manager.createInstance(StockUnit.class);
		stockUnit.setClient(client);
		stockUnit.setItemData(itemData);
		stockUnit.setAmount(amount);
		stockUnit.setBestBefore(bestBefore);
		stockUnit.setLotNumber(lotNumber);
		stockUnit.setUnitLoad(unitLoad);
		stockUnit.setSerialNumber(serialNumber);
		stockUnit.setAdditionalContent(note);
		stockUnit.setState(state);
		stockUnit.setPackagingUnit(packagingUnit);
		unitLoad.setState(state);
		if (bestBefore != null) {
			stockUnit.setStrategyDate(bestBefore);
		}

		manager.persist(stockUnit);

		addWeight(unitLoad, itemData, amount);

		if (BigDecimal.ZERO.compareTo(amount) != 0) {
			if (operator == null) {
				operator = userBusiness.getCurrentUser();
			}
			recordService.recordCreation(stockUnit, stockUnit.getAmount(), activityCode, operator, note);

			fireStockUnitChangeAmountEvent(client, stockUnit, BigDecimal.ZERO, activityCode, operator, note,
					sendNotify);
		}

		return stockUnit;
	}

	/**
	 * Move a stock unit to another stock unit
	 * 
	 * @param fromStock
	 * @param toStock
	 * @param amount       (null means complete)
	 * @param activityCode
	 * @param operator
	 * @param note
	 * @throws BusinessException
	 */
	public StockUnit transferStock(StockUnit fromStock, StockUnit toStock, BigDecimal amount, String activityCode,
			User operator, String note) throws BusinessException {
		String logStr = "transferStock ";
		logger.log(Level.FINE, logStr + "fromStock=" + fromStock + ", toStock=" + toStock + ", amount=" + amount);

		BigDecimal fromAmount = fromStock.getAmount();
		BigDecimal toAmount = toStock.getAmount();

		if (amount == null) {
			amount = fromStock.getAmount();
		}

		if (fromStock.getState() != toStock.getState() && toAmount.compareTo(BigDecimal.ZERO) > 0) {
			logger.log(Level.WARNING, logStr + "Different state of from- and to-stock. cannot combine. from-state="
					+ fromStock.getState() + ", to-state=" + toStock.getState());
			throw new BusinessException(Wms2BundleResolver.class, "Inventory.transferNotAllowed");
		}

		if (!isSummableItem(fromStock, amount, toStock, toStock.getAmount())) {
			logger.log(Level.WARNING, logStr + "Transfer of stock unit is not allowed");
			throw new BusinessException(Wms2BundleResolver.class, "Inventory.transferNotAllowed");
		}

		// calculate the new packaging unit
		PackagingUnit newPackagingUnit = calculateNewPackaginUnit(fromStock, amount, toStock, toStock.getAmount());
		toStock.setPackagingUnit(newPackagingUnit);

		// transfer amount
		toStock.setAmount(toStock.getAmount().add(amount));
		fromStock.setAmount(fromStock.getAmount().subtract(amount));

		// use oldest strategy date
		if (fromStock.getStrategyDate().compareTo(toStock.getStrategyDate()) < 0) {
			toStock.setStrategyDate(fromStock.getStrategyDate());
		}

		if (fromStock.getUnitLoad() != null) {
			fromStock.getUnitLoad().setOpened(true);
			addWeight(fromStock.getUnitLoad(), fromStock.getItemData(), amount.negate());
		}

		if (toStock.getUnitLoad() != null) {
			toStock.getUnitLoad().setOpened(true);
			addWeight(toStock.getUnitLoad(), toStock.getItemData(), amount);
		}

		if (operator == null) {
			operator = userBusiness.getCurrentUser();
		}
		recordService.recordChangeAmount(fromStock.getClient(), fromStock, amount.negate(), activityCode, operator,
				note);

		recordService.recordCreation(toStock, amount, activityCode, operator, note);

		fireStockUnitChangeAmountEvent(fromStock.getClient(), fromStock, fromAmount, activityCode, operator, note,
				false);

		fireStockUnitChangeAmountEvent(toStock.getClient(), toStock, toAmount, activityCode, operator, note, false);

		if (fromStock.getAmount().compareTo(BigDecimal.ZERO) == 0) {
			logger.log(Level.FINE,
					logStr + "remove empty stock. id=" + fromStock.getId() + " ul=" + fromStock.getUnitLoad());
			deleteStockUnit(fromStock, activityCode, operator, note);
		}

		return toStock;
	}

	/**
	 * Checks whether transfer of stock to the other unitLoad is allowed
	 */
	public void checkTransferStock(StockUnit stock, UnitLoad unitLoad, BigDecimal amount) throws BusinessException {
		String logStr = "checkTransferStock ";

		if (unitLoad.isLocked()) {
			logger.log(Level.INFO, logStr + "UnitLoad is locked. unitLoad=" + unitLoad);
			throw new BusinessException(Wms2BundleResolver.class, "Inventory.transferNotAllowed");
		}

		StorageLocation location = unitLoad.getStorageLocation();
		List<FixAssignment> fixList = fixService.readByLocation(location);
		if (fixList.size() > 0) {
			boolean valid = false;
			for (FixAssignment fix : fixList) {
				if (fix.getItemData().equals(stock.getItemData())) {
					valid = true;
					break;
				}
			}
			if (!valid) {
				logger.log(Level.INFO,
						logStr + "Wrong fix assignment. itemData=" + stock.getItemData() + ", location=" + location);
				throw new BusinessException(Wms2BundleResolver.class, "Inventory.transferNotAllowed");
			}
		}

		if (amount != null && amount.compareTo(stock.getAmount()) > 0) {
			logger.log(Level.WARNING, logStr + "Cannot move more amount than available on stock. stock=" + stock
					+ ", amount-stock=" + stock.getAmount() + ", amount-to-move=" + amount);
			throw new BusinessException(Wms2BundleResolver.class, "Inventory.tooMuchAmount");
		}

	}

	/**
	 * Move stock unit to a new unit load.
	 * 
	 * @param stock
	 * @param toUnitLoad
	 * @param amount       optional. null means complete amount
	 * @param activityCode
	 * @param operator
	 * @param note
	 * @return
	 * @throws BusinessException
	 */
	public StockUnit transferStock(StockUnit stock, UnitLoad toUnitLoad, BigDecimal amount, int toState,
			String activityCode, User operator, String note) throws BusinessException {
		String logStr = "transferStock ";
		logger.log(Level.FINE, logStr + "stock=" + stock + ", fromUnitLoad=" + stock.getUnitLoad() + ", toUnitLoad="
				+ toUnitLoad + ", amount=" + amount + ", stock.amount=" + stock.getAmount());

		if (amount == null) {
			amount = stock.getAmount();
		}
		if (amount.compareTo(stock.getAmount()) > 0) {
			logger.log(Level.INFO, logStr + "Cannot move more amount than available on stock. Abort. stock=" + stock
					+ ", amount-stock=" + stock.getAmount() + ", amount-to-move=" + amount);
			throw new BusinessException(Wms2BundleResolver.class, "Inventory.amountNotAvailable");
		}
		if (amount.compareTo(BigDecimal.ZERO) < 0) {
			logger.log(Level.INFO, logStr + "Amount cannot be smaller than zero. amount=" + amount);
			throw new BusinessException(Wms2BundleResolver.class, "Inventory.onlyPositiveAmount");
		}

		if (operator == null) {
			operator = userBusiness.getCurrentUser();
		}

		if (toUnitLoad.getUnitLoadType().isAggregateStocks() && amount.compareTo(BigDecimal.ZERO) > 0) {
			List<StockUnit> addToStockList = stockService.readList(stock.getClient(), stock.getItemData(),
					stock.getLotNumber(), toUnitLoad, null, stock.getSerialNumber(), null, null);
			for (StockUnit addToStock : addToStockList) {
				if (isSummableItem(stock, amount, addToStock, addToStock.getAmount())) {
					int stockState = stock.getState();
					stock.setState(toState);
					transferStock(stock, addToStock, amount, activityCode, operator, note);
					stock.setState(stockState);
					return addToStock;
				}
			}
		}

		StockUnit stockNew = null;

		if (amount.compareTo(stock.getAmount()) == 0) {
			// Move complete stock unit

			UnitLoad unitLoadOld = stock.getUnitLoad();

			unitLoadOld.setOpened(true);

			StorageLocation locationOld = unitLoadOld.getStorageLocation();

			// add to destination
			stock.setUnitLoad(toUnitLoad);

			Client oldClient = stock.getClient();
			stock.setClient(toUnitLoad.getClient());
			if (stock.getState() < toState) {
				stock.setState(toState);
			}
			toUnitLoad.setState(stock.getState());

			toUnitLoad.setOpened(true);

			addWeight(unitLoadOld, stock.getItemData(), stock.getAmount().negate());
			addWeight(toUnitLoad, stock.getItemData(), stock.getAmount());

			if (!oldClient.equals(toUnitLoad.getClient())) {
				recordService.recordChangeClient(stock, oldClient, toUnitLoad.getClient(), unitLoadOld, locationOld,
						activityCode, operator, note);
			} else {
				recordService.recordTransferStock(stock, unitLoadOld, locationOld, activityCode, operator, note);
			}

			processEmpties(unitLoadOld, true, activityCode, operator, note);

			stockNew = stock;

		} else {
			// Move partial stock unit

			stockNew = createStock(toUnitLoad, stock.getItemData(), BigDecimal.ZERO, stock.getLotNumber(),
					stock.getBestBefore(), stock.getSerialNumber(), stock.getPackagingUnit(), toState, activityCode,
					operator, note, false);
			stockNew.setPackagingUnit(stock.getPackagingUnit());
			stockNew.setStrategyDate(stock.getStrategyDate());
			stockNew.setLock(stock.getLock());

			transferStock(stock, stockNew, amount, activityCode, operator, note);
		}

		return stockNew;
	}

	/**
	 * Change the amount of a stock unit
	 * 
	 * @param client Will be used for journal. If null the stock units client is
	 *               used.
	 */
	public StockUnit changeAmount(StockUnit stock, BigDecimal newAmount, Client client, String activityCode,
			User operator, String note, boolean sendNotify) throws BusinessException {
		String logStr = "changeAmount ";
		logger.log(Level.FINE, logStr + "stock=" + stock + ", amount=" + newAmount);

		if (newAmount == null) {
			logger.log(Level.WARNING, logStr + "Amount cannot be null");
			throw new BusinessException(Wms2BundleResolver.class, "Inventory.missingAmount");
		}

		if (operator == null) {
			operator = userBusiness.getCurrentUser();
		}
		if (client == null) {
			client = stock.getClient();
		}

		BigDecimal oldAmount = stock.getAmount();

		try {
			newAmount = newAmount.setScale(stock.getItemData().getScale(), RoundingMode.HALF_UP);
		} catch (Throwable t) {
		}

		if (newAmount.compareTo(BigDecimal.ZERO) < 0) {
			logger.log(Level.WARNING, logStr + "Amount must not be less than zero. amount=" + newAmount);
			throw new BusinessException(Wms2BundleResolver.class, "Inventory.onlyPositiveAmount");
		}

		BigDecimal diffAmount = newAmount.subtract(stock.getAmount());

		if (diffAmount.compareTo(BigDecimal.ZERO) != 0) {
			logger.log(Level.INFO,
					logStr + "Change. stock=" + stock + ", amount=" + stock.getAmount() + ", new amount=" + newAmount);

			if (newAmount.compareTo(stock.getReservedAmount()) < 0) {
				logger.log(Level.INFO, logStr + "release reservedAmount. stock=" + stock + ", amount=" + newAmount
						+ ", old reservedAmount=" + stock.getReservedAmount());
				stock.setReservedAmount(newAmount);
			}

			stock.setAmount(newAmount);
			addWeight(stock.getUnitLoad(), stock.getItemData(), diffAmount);

			recordService.recordChangeAmount(client, stock, diffAmount, activityCode, operator, note);

			fireStockUnitChangeAmountEvent(client, stock, oldAmount, activityCode, operator, note, sendNotify);
		}

		if (newAmount.compareTo(BigDecimal.ZERO) == 0) {
			deleteStockUnit(stock, activityCode, operator, note);
			processEmpties(stock.getUnitLoad(), true, activityCode, operator, note);
		}

		return stock;
	}

	/**
	 * Changes the packaging unit. <br>
	 * Only the packaging information is changed. The amount of base units will not
	 * be changed!
	 */
	public StockUnit changePackagingUnit(StockUnit stock, PackagingUnit packagingUnit) throws BusinessException {
		String logStr = "changePackagingUnit ";
		logger.log(Level.FINE, logStr + "stock=" + stock + ", packagingUnit=" + packagingUnit);

		if (packagingUnit != null) {
			if (!Objects.equals(packagingUnit.getItemData(), stock.getItemData())) {
				logger.log(Level.WARNING,
						logStr + "Different itemDatas in in StockUnit and PackagingUnit. stock.itemData="
								+ stock.getItemData() + ", unit.itemData=" + packagingUnit.getItemData());
				throw new BusinessException(Wms2BundleResolver.class, "Validator.inequalItemData");
			}
		}

		stock.setPackagingUnit(packagingUnit);

		return stock;
	}

	/**
	 * Send StockUnit to trash location. The default trash handling is used. Maybe
	 * direct deletion is done. If not directly deleted, the StockUnit will be
	 * locked.
	 * 
	 * @param stockUnit
	 * @param manageEmpties
	 * @param activityCode
	 * @param operator
	 * @param note
	 * @throws BusinessException
	 */
	public void deleteStockUnit(StockUnit stock, String activityCode, User operator, String note)
			throws BusinessException {
		String logStr = "deleteStockUnit ";
		logger.log(Level.FINE, logStr + "stock=" + stock);

		if (operator == null) {
			operator = userBusiness.getCurrentUser();
		}

		BigDecimal oldAmount = stock.getAmount();
		if (oldAmount.compareTo(BigDecimal.ZERO) > 0) {
			stock.setAmount(BigDecimal.ZERO);
			recordService.recordChangeAmount(stock.getClient(), stock, oldAmount.negate(), activityCode, operator,
					note);
		}

		UnitLoad trash = unitLoadService.getTrash();

		stock = transferStock(stock, trash, null, StockState.DELETABLE, activityCode, operator, note);

		fireStockUnitTrashEvent(stock, activityCode, operator, note);
	}

	/**
	 * Send StockUnit to clearing location. The StockUnit will be locked.
	 * 
	 * @param stockUnit
	 * @param activityCode
	 * @param operator
	 * @param note
	 * @throws BusinessException
	 */
	public StockUnit transferToClearing(StockUnit stock, String activityCode, User operator, String note)
			throws BusinessException {
		String logStr = "sendToClearing ";
		logger.log(Level.FINE, logStr + "stock=" + stock);

		if (operator == null) {
			operator = userBusiness.getCurrentUser();
		}

		UnitLoad clearing = unitLoadService.getClearing();
		stock = transferStock(stock, clearing, null, stock.getState(), activityCode, operator, note);

		int oldLock = stock.getLock();
		if (oldLock != LockType.GENERAL) {
			stock.setLock(LockType.GENERAL);
			fireLockChangeEvent(stock, oldLock);
		}

		return stock;
	}

	public boolean isSummableItem(StockUnit stock1, BigDecimal amount1, StockUnit stock2, BigDecimal amount2) {
		String logStr = "isSummableItem ";
		if (stock1 == null || stock2 == null || stock1.equals(stock2)) {
			return false;
		}
		if (!Objects.equals(stock1.getItemData(), stock2.getItemData())) {
			logger.log(Level.FINE,
					logStr + "itemData not equal. item1=" + stock1.getItemData() + ", item2=" + stock2.getItemData());
			return false;
		}
		if (!StringUtils.equals(stock1.getLotNumber(), stock2.getLotNumber())) {
			logger.log(Level.FINE,
					logStr + "Lot not equal. lot1=" + stock1.getLotNumber() + ", item2=" + stock2.getLotNumber());
			return false;
		}
		if (!Objects.equals(stock1.getBestBefore(), stock2.getBestBefore())) {
			logger.log(Level.FINE, logStr + "Best before not equal. bestbefore1=" + stock1.getBestBefore()
					+ ", bestbefore2=" + stock2.getBestBefore());
			return false;
		}
		if (!StringUtils.equals(stock1.getSerialNumber(), stock2.getSerialNumber())) {
			logger.log(Level.FINE, logStr + "Serial != null. ser1=" + stock1.getSerialNumber() + ", item2="
					+ stock2.getSerialNumber());
			return false;
		}

		PackagingUnit packagingUnit1 = stock1.getPackagingUnit();
		PackagingUnit packagingUnit2 = stock2.getPackagingUnit();
		if (!Objects.equals(packagingUnit1, packagingUnit2)) {
			if (packagingUnit1 != null) {
				if (amount1 == null) {
					amount1 = stock1.getAmount();
				}
				if (amount1.compareTo(packagingUnit1.getAmount()) < 0) {
					// less than one package. ignore
					packagingUnit1 = null;
				}
			}
			if (packagingUnit2 != null) {
				if (amount2 == null) {
					amount2 = stock2.getAmount();
				}
				if (amount2.compareTo(packagingUnit2.getAmount()) < 0) {
					// less than one package. ignore
					packagingUnit2 = null;
				}
			}
			if (packagingUnit1 != null && packagingUnit2 != null) {
				logger.log(Level.FINER,
						logStr + "Packaging units not equal. item1=" + packagingUnit1 + ", item2=" + packagingUnit2);
				return false;
			}
		}

		if (stock1.getLock() != stock2.getLock()) {
			logger.log(Level.FINER,
					logStr + "Locks not equal. item1=" + stock1.getLock() + ", item2=" + stock2.getLock());
			return false;
		}

		return true;
	}

	public void calculateWeight(UnitLoad unitLoad) {
		String logStr = "calculateWeight ";
		if (!unitLoad.getUnitLoadType().isCalculateWeight()) {
			return;
		}

		boolean isInvalid = false;

		double unitLoadWeight = 0.0;
		if (unitLoad.getUnitLoadType().getWeight() != null) {
			unitLoadWeight = unitLoad.getUnitLoadType().getWeight().doubleValue();
		}

		int numStock = 0;
		List<StockUnit> stocksOnUnitLoad = stockUnitService.readByUnitLoad(unitLoad);
		for (StockUnit stock : stocksOnUnitLoad) {

			double itemWeight = 0.0;

			if (stock.getItemData().getWeight() != null) {
				itemWeight = stock.getItemData().getWeight().doubleValue();
			}

			unitLoadWeight += (itemWeight * stock.getAmount().doubleValue());

			numStock++;
			if (numStock > 1000) {
				logger.log(Level.SEVERE, logStr
						+ "Calculation of weight of container unit loads is not supportet. Unit load has more than 1000 stocks. unitLoad="
						+ unitLoad);
				isInvalid = true;
				break;
			}
		}

		if (!isInvalid) {
			BigDecimal calculatedWeight = new BigDecimal(unitLoadWeight, new MathContext(3, RoundingMode.HALF_UP));
			unitLoad.setWeightCalculated(calculatedWeight);
			return;
		}

		unitLoad.setWeightCalculated(null);
		return;
	}

	public BigDecimal readAmount(ItemData itemData, StorageLocation storageLocation) {
		String hql = "SELECT sum(stock.amount) FROM ";
		hql += StockUnit.class.getName() + " stock ";
		hql += " WHERE 1=1";
		if (storageLocation != null) {
			hql += " and stock.unitLoad.storageLocation=:location ";
		}
		if (itemData != null) {
			hql += " and stock.itemData=:itemData";
		}
		Query query = manager.createQuery(hql);
		if (storageLocation != null) {
			query = query.setParameter("location", storageLocation);
		}
		if (itemData != null) {
			query = query.setParameter("itemData", itemData);
		}
		BigDecimal amount = (BigDecimal) query.getSingleResult();
		if (amount != null) {
			return amount;
		}
		return BigDecimal.ZERO;
	}

	// ***********************************************************************
	// PRIVATE
	// ***********************************************************************

	private void addWeight(UnitLoad unitLoad, ItemData itemData, BigDecimal amount) {
		if (!unitLoad.getUnitLoadType().isCalculateWeight()) {
			return;
		}
		BigDecimal unitLoadWeight = unitLoad.getWeightCalculated();
		if (unitLoadWeight == null || unitLoadWeight.compareTo(BigDecimal.ZERO) <= 0) {
			calculateWeight(unitLoad);
			return;
		}

		BigDecimal diffWeight = null;
		BigDecimal itemWeight = itemData.getWeight();
		if (itemWeight != null) {
			diffWeight = itemWeight.multiply(amount);
		}

		if (diffWeight != null) {
			unitLoadWeight = unitLoadWeight.add(diffWeight);
		}

		if (unitLoadWeight == null || unitLoadWeight.compareTo(BigDecimal.ZERO) <= 0) {
			calculateWeight(unitLoad);
			return;
		}

		unitLoad.setWeightCalculated(unitLoadWeight);
	}

	private PackagingUnit calculateNewPackaginUnit(StockUnit stock1, BigDecimal amount1, StockUnit stock2,
			BigDecimal amount2) {

		PackagingUnit packagingUnit1 = stock1.getPackagingUnit();
		PackagingUnit packagingUnit2 = stock2.getPackagingUnit();
		if (Objects.equals(packagingUnit1, packagingUnit2)) {
			return packagingUnit1;
		}

		if (packagingUnit1 != null) {
			if (amount1 == null) {
				amount1 = stock1.getAmount();
			}
			if (amount1.compareTo(packagingUnit1.getAmount()) < 0) {
				// less than one package. ignore
				packagingUnit1 = null;
			}
		}
		if (packagingUnit2 != null) {
			if (amount2 == null) {
				amount2 = stock2.getAmount();
			}
			if (amount2.compareTo(packagingUnit2.getAmount()) < 0) {
				// less than one package. ignore
				packagingUnit2 = null;
			}
		}
		if (Objects.equals(packagingUnit1, packagingUnit2)) {
			return packagingUnit1;
		}
		if (packagingUnit1 == null) {
			return packagingUnit2;
		}
		if (packagingUnit2 == null) {
			return packagingUnit1;
		}

		return packagingUnit2;
	}

	private void fireUnitLoadTrashEvent(UnitLoad unitLoad, String activityCode, User operator, String note)
			throws BusinessException {
		try {
			logger.fine("Fire UnitLoadTrashEvent. unitLoad=" + unitLoad);
			unitLoadTrashEvent.fire(new UnitLoadTrashEvent(unitLoad, activityCode, operator, note));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

	private void fireStockUnitTrashEvent(StockUnit stockUnit, String activityCode, User operator, String note)
			throws BusinessException {
		try {
			logger.fine("Fire StockUnitTrashEvent. stockUnit=" + stockUnit);
			stockUnitTrashEvent.fire(new StockUnitTrashEvent(stockUnit, activityCode, operator, note));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

	private void fireStockUnitChangeAmountEvent(Client client, StockUnit stockUnit, BigDecimal oldAmount,
			String activityCode, User operator, String note, boolean sendNotify) throws BusinessException {
		try {
			logger.fine("Fire StockUnitChangeAmountEvent. stockUnit=" + stockUnit + ", amount=" + stockUnit.getAmount()
					+ ", oldAmount=" + oldAmount);
			stockUnitChangeAmountEvent.fire(new StockUnitChangeAmountEvent(client, stockUnit, oldAmount,
					stockUnit.getAmount(), activityCode, operator, note, sendNotify));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

	private void fireStockUnitStateChangeEvent(StockUnit entity, int oldState) throws BusinessException {
		try {
			logger.fine("Fire StockUnitStateChangeEvent. entity=" + entity + ", state=" + entity.getState()
					+ ", oldState=" + oldState);
			stockUnitStateChangeEvent.fire(new StockUnitStateChangeEvent(entity, oldState, entity.getState()));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

	private void fireUnitLoadTransferLocationEvent(UnitLoad unitLoad, StorageLocation source,
			StorageLocation destination, String activityCode, User operator, String note) throws BusinessException {
		try {
			logger.fine("Fire UnitLoadTransferLocationEvent. unitLoad=" + unitLoad + ", source=" + source
					+ ", destination=" + destination);
			unitLoadTransferLocationEvent.fire(
					new UnitLoadTransferLocationEvent(unitLoad, source, destination, activityCode, operator, note));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

	private void fireLockChangeEvent(BasicEntity entity, int oldLock) throws BusinessException {
		try {
			logger.fine(
					"Fire LockChangeEvent. entity=" + entity + ", lock=" + entity.getLock() + ", oldLock=" + oldLock);
			lockChangeEvent.fire(new LockChangeEvent(entity, oldLock, entity.getLock()));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

	private void fireUnitLoadTransferCarrierEvent(UnitLoad unitLoad, UnitLoad destination, String activityCode,
			User operator, String note) throws BusinessException {
		try {
			logger.fine("Fire UnitLoadTransferCarrierEvent. unitLoad=" + unitLoad + ", destination=" + destination);
			transferCarrierEvent
					.fire(new UnitLoadTransferCarrierEvent(unitLoad, destination, activityCode, operator, note));
		} catch (ObserverException ex) {
			Throwable cause = ex.getCause();
			if (cause != null && cause instanceof BusinessException) {
				throw (BusinessException) cause;
			}
			throw ex;
		}
	}

}
