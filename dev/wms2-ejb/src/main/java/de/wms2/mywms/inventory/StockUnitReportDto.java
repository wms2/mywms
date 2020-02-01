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

import de.wms2.mywms.document.Document;
import de.wms2.mywms.product.ItemData;

public class StockUnitReportDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private Document image;
	private StockUnit stockUnit;
	private UnitLoad unitLoad;
	private UnitLoadType unitLoadType;
	private ItemData itemData;
	private String lotNumber;

	public StockUnitReportDto(StockUnit stockUnit) {
		if (stockUnit == null) {
			return;
		}
		this.stockUnit = stockUnit;
		this.unitLoad = stockUnit.getUnitLoad();
		this.unitLoadType = unitLoad.getUnitLoadType();
		this.itemData = stockUnit.getItemData();
		this.lotNumber = stockUnit.getLotNumber();
	}

	public Document getImage() {
		return image;
	}

	public void setImage(Document image) {
		this.image = image;
	}

	public StockUnit getStockUnit() {
		return stockUnit;
	}

	public void setStockUnit(StockUnit stockUnit) {
		this.stockUnit = stockUnit;
	}

	public UnitLoad getUnitLoad() {
		return unitLoad;
	}

	public void setUnitLoad(UnitLoad unitLoad) {
		this.unitLoad = unitLoad;
	}

	public UnitLoadType getUnitLoadType() {
		return unitLoadType;
	}

	public void setUnitLoadType(UnitLoadType unitLoadType) {
		this.unitLoadType = unitLoadType;
	}

	public ItemData getItemData() {
		return itemData;
	}

	public void setItemData(ItemData itemData) {
		this.itemData = itemData;
	}

	public String getLotNumber() {
		return lotNumber;
	}

	public void setLotNumber(String lotNumber) {
		this.lotNumber = lotNumber;
	}

}
