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
package de.linogistix.los.inventory.query.dto;

import java.math.BigDecimal;
import java.util.Date;

import de.linogistix.los.query.BODTO;
import de.wms2.mywms.inventory.InventoryJournal;

public class InventoryJournalTO extends BODTO<InventoryJournal> {

	private static final long serialVersionUID = 1L;

	public String itemData;

	public String lot;

	public String fromSl;
	public String fromUl;

	public String toSl;
	public String toUl;

	public BigDecimal amount;
	public BigDecimal amountStock;

	public String activityCode;
	public String type;
	private String unitLoadType;
	private Date recordDate;

	public InventoryJournalTO(InventoryJournal rec) {
		this(rec.getId(), rec.getVersion(), rec.getId(), rec.getProductNumber(), rec.getLotNumber(), rec.getAmount(),
				rec.getStockUnitAmount(), rec.getFromStorageLocation(), rec.getFromUnitLoad(),
				rec.getToStorageLocation(), rec.getToUnitLoad(), rec.getActivityCode(), rec.getRecordType(),
				rec.getCreated(), rec.getUnitLoadType());
	}

	public InventoryJournalTO(Long id, int version, Long name, String itemData, String lot, BigDecimal amount,
			BigDecimal amountStock, String fromSl, String fromUl, String toSl, String toUl, String activityCode,
			String type, Date recordDate, String unitLoadType) {
		super(id, version, name);
		this.itemData = itemData;
		this.lot = lot;
		this.fromSl = fromSl;
		this.fromUl = fromUl;
		this.toSl = toSl;
		this.toUl = toUl;
		this.amount = amount;
		this.amountStock = amountStock;
		this.activityCode = activityCode;
		this.type = type;
		this.unitLoadType = unitLoadType;
		this.recordDate = recordDate;
		setClassName(InventoryJournal.class.getName());
	}

	public String getItemData() {
		return itemData;
	}

	public void setItemData(String itemData) {
		this.itemData = itemData;
	}

	public String getLot() {
		return lot;
	}

	public void setLot(String lot) {
		this.lot = lot;
	}

	public String getFromSl() {
		return fromSl;
	}

	public void setFromSl(String fromSl) {
		this.fromSl = fromSl;
	}

	public String getFromUl() {
		return fromUl;
	}

	public void setFromUl(String fromUl) {
		this.fromUl = fromUl;
	}

	public String getToSl() {
		return toSl;
	}

	public void setToSl(String toSl) {
		this.toSl = toSl;
	}

	public String getToUl() {
		return toUl;
	}

	public void setToUl(String toUl) {
		this.toUl = toUl;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getAmountStock() {
		return amountStock;
	}

	public void setAmountStock(BigDecimal amountStock) {
		this.amountStock = amountStock;
	}

	public String getActivityCode() {
		return activityCode;
	}

	public void setActivityCode(String activityCode) {
		this.activityCode = activityCode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUnitLoadType() {
		return unitLoadType;
	}

	public void setUnitLoadType(String unitLoadType) {
		this.unitLoadType = unitLoadType;
	}

	public Date getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(Date recordDate) {
		this.recordDate = recordDate;
	}

}
