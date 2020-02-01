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

import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.mywms.model.Client;

import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.location.StorageLocation;

/**
 * This service is to handle the history and journal entries of the core module.
 * 
 * 
 * @author krane
 *
 */
public class JournalHandler {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private PersistenceManager manager;

	public InventoryJournal recordCreation(UnitLoad unitLoad, String activityCode, String operator, String note)
			throws BusinessException {
		InventoryJournal rec = manager.createInstance(InventoryJournal.class);
		rec.setClient(unitLoad.getClient());

		rec.setOperator(operator);

		rec.setFromUnitLoad(unitLoad.getLabelId());
		rec.setFromStorageLocation(unitLoad.getStorageLocation().getName());

		rec.setToUnitLoad(unitLoad.getLabelId());
		rec.setToStorageLocation(unitLoad.getStorageLocation().getName());

		rec.setRecordType(InventoryJournalRecordType.CREATED);
		rec.setActivityCode(activityCode);
		rec.setAdditionalContent(note);

		rec.setUnitLoadType(unitLoad.getUnitLoadType().getName());

		manager.persist(rec);

		return rec;
	}

	public InventoryJournal recordCreation(StockUnit stock, BigDecimal amount, String activityCode, String operator,
			String note) {
		if (BigDecimal.ZERO.compareTo(amount) == 0) {
			logger.log(Level.FINER, "Do not record zero amount creation");
			return null;
		}
		InventoryJournal rec = manager.createInstance(InventoryJournal.class);
		rec.setClient(stock.getClient());

		rec.setOperator(operator);
		rec.setAmount(amount);
		rec.setStockUnitAmount(stock.getAmount());

		String label = null;
		String locationName = null;
		label = stock.getUnitLoad().getLabelId();
		locationName = stock.getUnitLoad().getStorageLocation().getName();

		rec.setFromUnitLoad(label);
		rec.setFromStorageLocation(locationName);

		rec.setToUnitLoad(label);
		rec.setToStorageLocation(locationName);

		rec.setProductNumber(stock.getItemData().getNumber());
		rec.setProductName(stock.getItemData().getName());
		rec.setScale(stock.getItemData().getScale());
		rec.setUnitName(stock.getItemData().getItemUnit().getName());
		rec.setLotNumber(stock.getLotNumber());
		rec.setBestBefore(stock.getBestBefore());
		rec.setRecordType(InventoryJournalRecordType.CREATED);
		rec.setActivityCode(activityCode);
		rec.setAdditionalContent(note);

		rec.setSerialNumber(stock.getSerialNumber());

		rec.setUnitLoadType(stock.getUnitLoad() == null ? null : stock.getUnitLoad().getUnitLoadType().getName());

		manager.persist(rec);

		return rec;
	}

	public InventoryJournal recordChangeAmount(Client client, StockUnit stock, BigDecimal amount, String activityCode,
			String operator, String note) {
		if (client == null) {
			client = stock.getClient();
		}
		InventoryJournal rec = manager.createInstance(InventoryJournal.class);
		rec.setClient(client);

		rec.setOperator(operator);
		rec.setAmount(amount);
		rec.setStockUnitAmount(stock.getAmount());

		String label = null;
		String locationName = null;
		label = stock.getUnitLoad().getLabelId();
		locationName = stock.getUnitLoad().getStorageLocation().getName();

		rec.setFromUnitLoad(label);
		rec.setFromStorageLocation(locationName);

		rec.setToUnitLoad(label);
		rec.setToStorageLocation(locationName);

		rec.setProductNumber(stock.getItemData().getNumber());
		rec.setProductName(stock.getItemData().getName());
		rec.setScale(stock.getItemData().getScale());
		rec.setUnitName(stock.getItemData().getItemUnit().getName());
		rec.setLotNumber(stock.getLotNumber());
		rec.setBestBefore(stock.getBestBefore());
		rec.setRecordType(InventoryJournalRecordType.CHANGED);
		rec.setActivityCode(activityCode);
		rec.setAdditionalContent(note);

		rec.setSerialNumber(stock.getSerialNumber());

		rec.setUnitLoadType(stock.getUnitLoad() == null ? null : stock.getUnitLoad().getUnitLoadType().getName());

		manager.persist(rec);

		return rec;
	}

	public InventoryJournal recordTransferStock(StockUnit stock, UnitLoad unitLoadOld, StorageLocation locationOld,
			String activityCode, String operator, String note) {
		if (BigDecimal.ZERO.compareTo(stock.getAmount()) == 0) {
			logger.log(Level.FINER, "Do not record zero amount transfer");
			return null;
		}
		UnitLoad unitLoadNew = stock.getUnitLoad();
		StorageLocation locationNew = (unitLoadNew == null ? null : unitLoadNew.getStorageLocation());

		InventoryJournal rec = manager.createInstance(InventoryJournal.class);
		rec.setClient(stock.getClient());

		rec.setOperator(operator);
		rec.setAmount(null);
		rec.setStockUnitAmount(stock.getAmount());

		rec.setFromUnitLoad(unitLoadOld == null ? null : unitLoadOld.getLabelId());
		rec.setToUnitLoad(unitLoadNew == null ? null : unitLoadNew.getLabelId());

		rec.setFromStorageLocation(locationOld == null ? null : locationOld.getName());
		rec.setToStorageLocation(locationNew == null ? null : locationNew.getName());

		rec.setProductNumber(stock.getItemData().getNumber());
		rec.setProductName(stock.getItemData().getName());
		rec.setScale(stock.getItemData().getScale());
		rec.setUnitName(stock.getItemData().getItemUnit().getName());
		rec.setLotNumber(stock.getLotNumber());
		rec.setBestBefore(stock.getBestBefore());
		rec.setRecordType(InventoryJournalRecordType.TRANSFERED);
		rec.setActivityCode(activityCode);
		rec.setAdditionalContent(note);

		rec.setSerialNumber(stock.getSerialNumber());

		rec.setUnitLoadType(null);
		if (unitLoadOld != null) {
			rec.setUnitLoadType(unitLoadOld.getUnitLoadType().getName());
		} else if (unitLoadNew != null) {
			rec.setUnitLoadType(unitLoadNew.getUnitLoadType().getName());
		}

		manager.persist(rec);

		return rec;
	}

	public void recordChangeClient(StockUnit stock, Client clientOld, Client clientNew, UnitLoad unitLoadOld,
			StorageLocation locationOld, String activityCode, String operator, String note) {
		if (BigDecimal.ZERO.compareTo(stock.getAmount()) == 0) {
			logger.log(Level.FINER, "Do not record zero amount transfer");
			return;
		}

		// removal from old client
		InventoryJournal rec = manager.createInstance(InventoryJournal.class);
		rec.setClient(clientOld);

		rec.setOperator(operator);
		rec.setAmount(stock.getAmount().negate());
		rec.setStockUnitAmount(BigDecimal.ZERO);

		rec.setFromUnitLoad(unitLoadOld.getLabelId());
		rec.setToUnitLoad(unitLoadOld.getLabelId());
		rec.setFromStorageLocation(locationOld.getName());
		rec.setToStorageLocation(locationOld.getName());

		rec.setProductNumber(stock.getItemData().getNumber());
		rec.setProductName(stock.getItemData().getName());
		rec.setScale(stock.getItemData().getScale());
		rec.setUnitName(stock.getItemData().getItemUnit().getName());
		rec.setLotNumber(stock.getLotNumber());
		rec.setBestBefore(stock.getBestBefore());
		rec.setRecordType(InventoryJournalRecordType.REMOVED);
		rec.setActivityCode(activityCode);
		rec.setAdditionalContent(note);

		rec.setSerialNumber(stock.getSerialNumber());

		rec.setUnitLoadType(stock.getUnitLoad().getUnitLoadType().getName());

		manager.persist(rec);

		// addition to new client
		rec = manager.createInstance(InventoryJournal.class);
		rec.setClient(clientNew);

		rec.setOperator(operator);
		rec.setAmount(stock.getAmount());
		rec.setStockUnitAmount(stock.getAmount());

		rec.setFromUnitLoad(stock.getUnitLoad().getLabelId());
		rec.setToUnitLoad(stock.getUnitLoad().getLabelId());
		rec.setFromStorageLocation(stock.getUnitLoad().getStorageLocation().getName());
		rec.setToStorageLocation(stock.getUnitLoad().getStorageLocation().getName());

		rec.setProductNumber(stock.getItemData().getNumber());
		rec.setProductName(stock.getItemData().getName());
		rec.setScale(stock.getItemData().getScale());
		rec.setUnitName(stock.getItemData().getItemUnit().getName());
		rec.setLotNumber(stock.getLotNumber());
		rec.setBestBefore(stock.getBestBefore());
		rec.setRecordType(InventoryJournalRecordType.CREATED);
		rec.setActivityCode(activityCode);
		rec.setAdditionalContent(note);

		rec.setSerialNumber(stock.getSerialNumber());

		rec.setUnitLoadType(stock.getUnitLoad().getUnitLoadType().getName());

		manager.persist(rec);

	}

	public InventoryJournal recordTransferUnitLoad(UnitLoad fromUnitLoad, UnitLoad toUnitLoad,
			StorageLocation fromLocation, StorageLocation toLocation, String activityCode, String operator,
			String note) {
		InventoryJournal rec = manager.createInstance(InventoryJournal.class);
		rec.setClient(fromUnitLoad.getClient());

		rec.setOperator(operator);

		rec.setFromUnitLoad(fromUnitLoad.getLabelId());
		rec.setFromStorageLocation(fromLocation.getName());

		rec.setToUnitLoad(toUnitLoad.getLabelId());
		rec.setToStorageLocation(toLocation.getName());

		rec.setRecordType(InventoryJournalRecordType.TRANSFERED);
		rec.setActivityCode(activityCode);
		rec.setAdditionalContent(note);

		rec.setUnitLoadType(fromUnitLoad.getUnitLoadType().getName());

		manager.persist(rec);

		return rec;
	}

	public InventoryJournal recordCounting(StockUnit stock, UnitLoad unitLoad, StorageLocation location,
			String activityCode, String operator, String note) {
		InventoryJournal rec = manager.createInstance(InventoryJournal.class);

		if (stock != null) {
			rec.setClient(stock.getClient());
		} else if (unitLoad != null) {
			rec.setClient(unitLoad.getClient());
		} else if (location != null) {
			rec.setClient(location.getClient());
		} else {
			logger.log(Level.SEVERE, "Cannot record counting, No parameters given to record");
			return null;
		}

		rec.setOperator(operator);
		rec.setRecordType(InventoryJournalRecordType.COUNTED);
		rec.setActivityCode(activityCode);
		rec.setAdditionalContent(note);
		rec.setScale(0);
		rec.setFromStorageLocation("-");
		rec.setToStorageLocation("-");

		if (stock != null) {
			String label = null;
			String locationName = null;
			label = stock.getUnitLoad().getLabelId();
			locationName = stock.getUnitLoad().getStorageLocation().getName();
			rec.setFromUnitLoad(label);
			rec.setFromStorageLocation(locationName);
			rec.setToUnitLoad(label);
			rec.setToStorageLocation(locationName);
			rec.setStockUnitAmount(stock.getAmount());

			rec.setProductNumber(stock.getItemData().getNumber());
			rec.setProductName(stock.getItemData().getName());
			rec.setScale(stock.getItemData().getScale());
			rec.setUnitName(stock.getItemData().getItemUnit().getName());
			rec.setLotNumber(stock.getLotNumber());
			rec.setBestBefore(stock.getBestBefore());
			rec.setSerialNumber(stock.getSerialNumber());
			rec.setUnitLoadType(stock.getUnitLoad() == null ? null : stock.getUnitLoad().getUnitLoadType().getName());
		}
		if (unitLoad != null) {
			rec.setFromUnitLoad(unitLoad.getLabelId());
			rec.setFromStorageLocation(unitLoad.getStorageLocation().getName());
			rec.setToUnitLoad(unitLoad.getLabelId());
			rec.setToStorageLocation(unitLoad.getStorageLocation().getName());
			rec.setUnitLoadType(unitLoad.getUnitLoadType().getName());
		}
		if (location != null) {
			rec.setFromStorageLocation(location.getName());
			rec.setToStorageLocation(location.getName());
		}

		manager.persist(rec);

		return rec;
	}

}
