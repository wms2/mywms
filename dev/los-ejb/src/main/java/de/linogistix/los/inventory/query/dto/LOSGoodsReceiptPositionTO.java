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

import de.linogistix.los.query.BODTO;
import de.wms2.mywms.goodsreceipt.GoodsReceiptLine;

public class LOSGoodsReceiptPositionTO extends BODTO<GoodsReceiptLine> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BigDecimal amount;
	public String itemData;
	public String lot;
	public String operatorName;
	public int qaLock;
	public String unitLoad;
	public Date created;

	public LOSGoodsReceiptPositionTO(GoodsReceiptLine x) {
		this(x.getId(), x.getVersion(), x.getLineNumber(), x.getAmount(),
				x.getItemData() == null ? null : x.getItemData().getNumber(), x.getLotNumber(),
				x.getOperator() == null ? null : x.getOperator().getName(), x.getLockType(), x.getUnitLoadLabel(),
				x.getCreated());
	}

	public LOSGoodsReceiptPositionTO(Long id, int version, String positionNumber, BigDecimal amount, String itemData,
			String lot, String operatorName, int qaLock, String unitLoad, Date created) {
		super(id, version, positionNumber);
		this.amount = amount;
		this.itemData = itemData;
		this.lot = lot;
		this.operatorName = operatorName;
		this.qaLock = qaLock;
		this.unitLoad = unitLoad;
		this.created = created;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
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

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public int getQaLock() {
		return qaLock;
	}

	public void setQaLock(int qaLock) {
		this.qaLock = qaLock;
	}

	public String getUnitLoad() {
		return unitLoad;
	}

	public void setUnitLoad(String unitLoad) {
		this.unitLoad = unitLoad;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
}
