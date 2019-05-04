/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service.dto;

import java.io.Serializable;

public class GoodsReceiptTO implements Serializable{

	private static final long serialVersionUID = 1L;

	private long id;
	
	private String goodsReceiptNo;
	
	private String suplier;
	
	private String deliveryNoteNo;
	
	public GoodsReceiptTO(long id, String goodsReceiptNo, String suplier, String deliveryNoteNo) 
	{
		super();
		this.id = id;
		this.goodsReceiptNo = goodsReceiptNo;
		this.suplier = suplier;
		this.deliveryNoteNo = deliveryNoteNo;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getGoodsReceiptNo() {
		return goodsReceiptNo;
	}

	public void setGoodsReceiptNo(String goodsReceiptNo) {
		this.goodsReceiptNo = goodsReceiptNo;
	}

	public String getSuplier() {
		return suplier;
	}

	public void setSuplier(String suplier) {
		this.suplier = suplier;
	}

	public String getDeliveryNoteNo() {
		return deliveryNoteNo;
	}

	public void setDeliveryNoteNo(String deliveryNoteNo) {
		this.deliveryNoteNo = deliveryNoteNo;
	}
	
	
}
