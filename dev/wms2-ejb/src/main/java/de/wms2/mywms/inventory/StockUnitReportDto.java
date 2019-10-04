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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import de.wms2.mywms.document.Document;
import de.wms2.mywms.product.ItemData;

public class StockUnitReportDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private String lineNumber;
	private String type;
	private String label;
	private String productNumber;
	private String productName;
	private String productUnit;
	private BigDecimal amount;
	private String lotNumber;
	private Date bestBefore;
	private String serialNumber;
	private Document image;

	public StockUnitReportDto() {
	}

	public StockUnitReportDto(StockUnit stockUnit) {
		type = "S";
		if (stockUnit == null) {
			return;
		}
		UnitLoad unitLoad = stockUnit.getUnitLoad();
		label = unitLoad.getLabelId();
		ItemData itemData = stockUnit.getItemData();
		productNumber = itemData.getNumber();
		productName = itemData.getName();
		productUnit = itemData.getItemUnit().getName();
		amount = stockUnit.getAmount();
		Lot lot = stockUnit.getLot();
		if (lot != null) {
			lotNumber = lot.getName();
			bestBefore = lot.getBestBeforeEnd();
		}
		serialNumber = stockUnit.getSerialNumber();
	}

	public StockUnitReportDto(String type, ItemData itemData, BigDecimal amount) {
		this.type = type;
		if (itemData != null) {
			this.productNumber = itemData.getNumber();
			this.productName = itemData.getName();
			this.productUnit = itemData.getItemUnit().getName();
		}
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

	public String getProductNumber() {
		return productNumber;
	}

	public void setProductNumber(String productNumber) {
		this.productNumber = productNumber;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductUnit() {
		return productUnit;
	}

	public void setProductUnit(String productUnit) {
		this.productUnit = productUnit;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
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

	public Document getImage() {
		return image;
	}

	public void setImage(Document image) {
		this.image = image;
	}

}
