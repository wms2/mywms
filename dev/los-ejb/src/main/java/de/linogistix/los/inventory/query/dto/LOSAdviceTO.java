/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import java.math.BigDecimal;
import java.util.Date;

import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSAdviceState;
import de.linogistix.los.query.BODTO;

public class LOSAdviceTO extends BODTO<LOSAdvice> {

	private static final long serialVersionUID = 1L;

	public String state;

	public BigDecimal receiptAmount;

	public BigDecimal notifiedAmount;

	public String itemData;

	public String itemDataName;

	public String lot = "";

	public String client;

	public Date expectedDelivery;

	public LOSAdviceTO() {
	}

	public LOSAdviceTO(Long id, int version, String name, String client,
			LOSAdviceState state, BigDecimal amount, BigDecimal notifiedAmount,
			String itemNumber, String itemName, int scale, String lot, Date date) {
		super(id, version, name);
		this.state = state.name();
		this.receiptAmount = amount.setScale(scale);
		this.notifiedAmount = notifiedAmount.setScale(scale);
		this.itemData = itemNumber;
		this.itemDataName = itemName;
		this.lot = lot;
		this.client = client;
		this.expectedDelivery = date;
		setClassName(LOSAdvice.class.getName());
	}

	public LOSAdviceTO(LOSAdvice adv) {
		super(adv.getId(), adv.getVersion(), adv.getAdviceNumber() );
		this.state = adv.getAdviceState().toString();
		this.receiptAmount = adv.getReceiptAmount().setScale(adv.getItemData().getScale());
		this.notifiedAmount = adv.getNotifiedAmount().setScale(adv.getItemData().getScale());
		this.itemData = adv.getItemData().getNumber();;
		this.itemDataName = adv.getItemData().getName();;

		if (adv.getLot() != null) {
			this.lot = adv.getLot().getName();
		}
		this.client = adv.getClient().getNumber();
		this.expectedDelivery = adv.getExpectedDelivery();
		setClassName(LOSAdvice.class.getName());
	}

	public LOSAdviceTO(Long id, int version, String name) {
		super(id, version, name);
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public BigDecimal getReceiptAmount() {
		return receiptAmount;
	}

	public void setReceiptAmount(BigDecimal amount) {
		this.receiptAmount = amount;
	}

	public BigDecimal getNotifiedAmount() {
		return notifiedAmount;
	}

	public void setNotifiedAmount(BigDecimal notifiedAmount) {
		this.notifiedAmount = notifiedAmount;
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

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public Date getExpectedDelivery() {
		return expectedDelivery;
	}

	public void setExpectedDelivery(Date expectedDelivery) {
		this.expectedDelivery = expectedDelivery;
	}

	public String getItemDataName() {
		return itemDataName;
	}
	public void setItemDataName(String itemDataName) {
		this.itemDataName = itemDataName;
	}

	
}
