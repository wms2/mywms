/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import java.util.Date;

import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptState;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.query.BODTO;

public class LOSGoodsReceiptTO extends BODTO<LOSGoodsReceipt> {

	private static final long serialVersionUID = 1L;
	
	private String deliveryNoteNumber;
	private Date receiptDate;
	private String receiptState;
	private String clientNumber;
	private String locationName;
	
	public LOSGoodsReceiptTO(LOSGoodsReceipt x) {
		this(x.getId(), x.getVersion(), x.getGoodsReceiptNumber(), x.getDeliveryNoteNumber(), x.getReceiptDate(), x.getReceiptState(), x.getClient().getNumber(), x.getGoodsInLocation());
	}
	
	public LOSGoodsReceiptTO(Long id, int version, 
			String goodsReceiptNumber, String deliveryNoteNumber,
			Date receiptDate, LOSGoodsReceiptState receiptState, String clientNumber, LOSStorageLocation location) {
		super(id, version, goodsReceiptNumber);
		this.deliveryNoteNumber = deliveryNoteNumber;
		this.receiptDate = receiptDate;
		this.receiptState = receiptState.name();
		this.clientNumber = clientNumber;
		this.locationName = location == null ? null : location.getName();
	}

	public String getDeliveryNoteNumber() {
		return deliveryNoteNumber;
	}

	public void setDeliveryNoteNumber(String deliveryNoteNumber) {
		this.deliveryNoteNumber = deliveryNoteNumber;
	}

	public Date getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
	}

	public String getReceiptState() {
		return receiptState;
	}

	public void setReceiptState(String receiptState) {
		this.receiptState = receiptState;
	}

	public String getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	public String getLocationName() {
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

}
