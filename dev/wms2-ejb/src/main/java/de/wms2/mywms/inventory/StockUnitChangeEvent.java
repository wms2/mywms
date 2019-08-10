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

import org.mywms.model.User;

/**
 * This event fired when parameters of the stock unit are changed. See old*
 * variables.
 * 
 * @author krane
 *
 */
public class StockUnitChangeEvent {
	private StockUnit stock;
	private String lotNumberOld;
	private boolean sendNotify;
	private String activityCode;
	private User operator;
	private String note;

	public StockUnitChangeEvent(StockUnit stock, BigDecimal oldAmount, String oldLotNumber, String activityCode,
			User operator, String note, boolean sendNotify) {
		this.stock = stock;
		this.lotNumberOld = oldLotNumber;
		this.sendNotify = sendNotify;
		this.activityCode = activityCode;
		this.operator = operator;
		this.note = note;
	}

	public StockUnit getStock() {
		return stock;
	}

	public boolean isSendNotify() {
		return sendNotify;
	}

	public String getActivityCode() {
		return activityCode;
	}

	public User getOperator() {
		return operator;
	}

	public String getNote() {
		return note;
	}

	public String getLotNumberOld() {
		return lotNumberOld;
	}
}
