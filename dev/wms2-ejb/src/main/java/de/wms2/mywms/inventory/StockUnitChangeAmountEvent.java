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

import org.mywms.model.Client;
import org.mywms.model.User;

/**
 * This event fired when the amount of a stock unit changes
 * 
 * @author krane
 *
 */
public class StockUnitChangeAmountEvent {
	private StockUnit stock;
	private BigDecimal oldAmount;
	private BigDecimal newAmount;
	private boolean sendNotify;
	private String activityCode;
	private User operator;
	private String note;
	private Client client;

	public StockUnitChangeAmountEvent(Client client, StockUnit stock, BigDecimal oldAmount, BigDecimal newAmount,
			String activityCode, User operator, String note, boolean sendNotify) {
		this.client = client;
		this.stock = stock;
		this.oldAmount = oldAmount;
		this.newAmount = newAmount;
		this.sendNotify = sendNotify;
		this.activityCode = activityCode;
		this.operator = operator;
		this.note = note;
	}

	public StockUnit getStock() {
		return stock;
	}

	public BigDecimal getOldAmount() {
		return oldAmount;
	}

	public BigDecimal getNewAmount() {
		return newAmount;
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

	public Client getClient() {
		return client;
	}
}
