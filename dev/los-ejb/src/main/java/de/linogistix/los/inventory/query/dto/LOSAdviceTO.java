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

import org.mywms.model.Client;

import de.linogistix.los.query.BODTO;
import de.wms2.mywms.advice.AdviceLine;

public class LOSAdviceTO extends BODTO<AdviceLine> {

	private static final long serialVersionUID = 1L;

	public int state;

	public BigDecimal receiptAmount;

	public BigDecimal notifiedAmount;

	public String itemData;

	public String itemDataName;

	public String lot = "";

	public String client;

	public Date expectedDelivery;

	public LOSAdviceTO() {
	}

	public LOSAdviceTO(Long id, int version, String name, 
			int state, BigDecimal amount, BigDecimal confirmedAmount,
			String itemNumber, String itemName, int scale, String lot, String client, Date expectedDelivery) {
		super(id, version, name);
		this.state = state;
		this.notifiedAmount = amount.setScale(scale);
		this.receiptAmount = confirmedAmount.setScale(scale);
		this.itemData = itemNumber;
		this.itemDataName = itemName;
		this.lot = lot;
		this.client = client;
		this.expectedDelivery = expectedDelivery;
		setClassName(AdviceLine.class.getName());
	}

	public LOSAdviceTO(AdviceLine adv) {
		super(adv.getId(), adv.getVersion(), adv.getLineNumber() );
		this.state = adv.getState();
		this.receiptAmount = adv.getAmount().setScale(adv.getItemData().getScale());
		this.notifiedAmount = adv.getConfirmedAmount().setScale(adv.getItemData().getScale());
		this.itemData = adv.getItemData().getNumber();;
		this.itemDataName = adv.getItemData().getName();;

		this.lot = adv.getLotNumber();
		this.client = adv.getAdvice().getClient().getNumber();
		this.expectedDelivery = adv.getAdvice().getDeliveryDate();
		setClassName(AdviceLine.class.getName());
	}

	public LOSAdviceTO(Long id, int version, String name) {
		super(id, version, name);
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
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
