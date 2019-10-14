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
package de.wms2.mywms.delivery;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.product.ItemData;

public class DeliveryReportDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private String lineNumber;
	private String type;
	private String label;
	private ItemData itemData;
	private BigDecimal amount;
	private BigDecimal unitPrice;
	private String lotNumber;
	private Date bestBefore;
	private String serialNumber;
	private String pickingHint;
	private String packingHint;
	private String shippingHint;
	private UnitLoad unitLoad;

	private String externalId;

	public DeliveryReportDto(String type, ItemData itemData, BigDecimal amount) {
		this.type = type;
		this.itemData = itemData;
		this.amount = amount;
	}

	public void addAmount(BigDecimal amount) {
		if (this.amount == null) {
			this.amount = amount;
			return;
		}
		if (amount == null) {
			return;
		}
		this.amount = this.amount.add(amount);
	}

	public String getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public ItemData getItemData() {
		return itemData;
	}

	public void setItemData(ItemData itemData) {
		this.itemData = itemData;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getLotNumber() {
		return lotNumber;
	}

	public void setLotNumber(String lotNumber) {
		this.lotNumber = lotNumber;
	}

	public Date getBestBefore() {
		return bestBefore;
	}

	public void setBestBefore(Date bestBefore) {
		this.bestBefore = bestBefore;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getPickingHint() {
		return pickingHint;
	}

	public void setPickingHint(String pickingHint) {
		this.pickingHint = pickingHint;
	}

	public String getPackingHint() {
		return packingHint;
	}

	public void setPackingHint(String packingHint) {
		this.packingHint = packingHint;
	}

	public String getShippingHint() {
		return shippingHint;
	}

	public void setShippingHint(String shippingHint) {
		this.shippingHint = shippingHint;
	}

	public UnitLoad getUnitLoad() {
		return unitLoad;
	}

	public void setUnitLoad(UnitLoad unitLoad) {
		this.unitLoad = unitLoad;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

}
