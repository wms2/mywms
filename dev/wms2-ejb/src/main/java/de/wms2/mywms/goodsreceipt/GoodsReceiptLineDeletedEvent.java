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
package de.wms2.mywms.goodsreceipt;

import java.math.BigDecimal;

import de.wms2.mywms.advice.AdviceLine;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.product.ItemData;

/**
 * This event fired when a goods receipt line is deleted
 * 
 * @author krane
 *
 */
public class GoodsReceiptLineDeletedEvent {
	private GoodsReceiptLine goodsReceiptLine;
	private AdviceLine adviceLine;
	private ItemData itemData;
	private UnitLoadType unitLoadType;
	private BigDecimal amount;

	public GoodsReceiptLineDeletedEvent(GoodsReceiptLine goodsReceiptLine, AdviceLine adviceLine, ItemData itemData,
			BigDecimal amount, UnitLoadType unitLoadType) {
		this.goodsReceiptLine = goodsReceiptLine;
		this.adviceLine = adviceLine;
		this.itemData = itemData;
		this.unitLoadType = unitLoadType;
		this.amount = amount;

	}

	public GoodsReceiptLine getGoodsReceiptLine() {
		return goodsReceiptLine;
	}

	public AdviceLine getAdviceLine() {
		return adviceLine;
	}

	public ItemData getItemData() {
		return itemData;
	}

	public UnitLoadType getUnitLoadType() {
		return unitLoadType;
	}

	public BigDecimal getAmount() {
		return amount;
	}
}
