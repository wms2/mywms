/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import java.util.Date;

import de.linogistix.los.query.BODTO;
import de.wms2.mywms.goodsreceipt.GoodsReceipt;
import de.wms2.mywms.location.StorageLocation;

public class LOSGoodsReceiptTO extends BODTO<GoodsReceipt> {

	private static final long serialVersionUID = 1L;
	
	private String deliveryNoteNumber;
	private Date receiptDate;
	private String clientNumber;
	private String locationName;
	private int state;
	
	public LOSGoodsReceiptTO(GoodsReceipt x) {
		this(x.getId(), x.getVersion(), x.getOrderNumber(), x.getDeliveryNoteNumber(), x.getReceiptDate(), x.getState(), x.getClient().getNumber(), x.getStorageLocation());
	}
	
	public LOSGoodsReceiptTO(Long id, int version, 
			String goodsReceiptNumber, String deliveryNoteNumber,
			Date receiptDate, int state, String clientNumber, StorageLocation location) {
		super(id, version, goodsReceiptNumber);
		this.deliveryNoteNumber = deliveryNoteNumber;
		this.receiptDate = receiptDate;
		this.state = state;
		this.clientNumber = clientNumber;
		this.locationName = location == null ? null : location.getName();
	}

	public String getDeliveryNoteNumber() {
		return deliveryNoteNumber;
	}

	public void setDeliveryNoteNumber(String deliveryNoteNumber) {
		this.deliveryNoteNumber = deliveryNoteNumber;
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

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Date getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
	}


}
