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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Logger;

import de.wms2.mywms.document.Document;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.product.PackagingUnit;

public class StockUnitReportDto implements Serializable {
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	private static final long serialVersionUID = 1L;

	private Document image;
	private StockUnit stockUnit;
	private UnitLoad unitLoad;
	private UnitLoadType unitLoadType;
	private StorageLocation storageLocation;
	private ItemData itemData;
	private String lotNumber;
	private BigDecimal packagingUnitAmount;
	private String packagingUnitName;
	private BigDecimal baseUnitAmount;
	private String baseUnitName;

	public StockUnitReportDto(StockUnit stockUnit) {
		if (stockUnit == null) {
			return;
		}
		this.stockUnit = stockUnit;
		this.unitLoad = stockUnit.getUnitLoad();
		this.unitLoadType = unitLoad.getUnitLoadType();
		this.storageLocation = unitLoad.getStorageLocation();
		this.itemData = stockUnit.getItemData();
		this.lotNumber = stockUnit.getLotNumber();
		this.baseUnitAmount = stockUnit.getAmount();
		this.baseUnitName = stockUnit.getItemUnit().getName();
		this.packagingUnitAmount = stockUnit.getAmount();
		this.packagingUnitName = stockUnit.getItemUnit().getName();
		PackagingUnit packagingUnit = stockUnit.getPackagingUnit();
		if (packagingUnit != null) {
			try {
				this.packagingUnitAmount = stockUnit.getAmount().divide(packagingUnit.getAmount(),
						RoundingMode.UNNECESSARY);
				this.packagingUnitName = packagingUnit.getName();
			} catch (Exception e) {
				logger.warning(
						"Cannot calculate packaging amount. Not possible to divide base amount with packaging amount. base amount="
								+ stockUnit.getAmount() + ", packaging amount=" + packagingUnit.getAmount()
								+ ", itemData=" + itemData);
				// do not set packaging unit if it the amount can not be divided
			}
		}
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

	public StorageLocation getStorageLocation() {
		return storageLocation;
	}

	public void setStorageLocation(StorageLocation storageLocation) {
		this.storageLocation = storageLocation;
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

	public BigDecimal getPackagingUnitAmount() {
		return packagingUnitAmount;
	}

	public void setPackagingUnitAmount(BigDecimal packagingUnitAmount) {
		this.packagingUnitAmount = packagingUnitAmount;
	}

	public String getPackagingUnitName() {
		return packagingUnitName;
	}

	public void setPackagingUnitName(String packagingUnitName) {
		this.packagingUnitName = packagingUnitName;
	}

	public BigDecimal getBaseUnitAmount() {
		return baseUnitAmount;
	}

	public void setBaseUnitAmount(BigDecimal baseUnitAmount) {
		this.baseUnitAmount = baseUnitAmount;
	}

	public String getBaseUnitName() {
		return baseUnitName;
	}

	public void setBaseUnitName(String baseUnitName) {
		this.baseUnitName = baseUnitName;
	}

	public Logger getLogger() {
		return logger;
	}

}
