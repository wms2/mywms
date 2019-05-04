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

import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.linogistix.los.query.BODTO;

public class LOSGoodsReceiptPositionTO extends BODTO<LOSGoodsReceiptPosition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String orderReference;
	public BigDecimal amount;
	public String itemData;
	public String lot;
	public String operatorName;
	public int qaLock;
	public String unitLoad;
	public Date created;
	
	public LOSGoodsReceiptPositionTO(LOSGoodsReceiptPosition x)	{
		this(x.getId(), x.getVersion(), x.getPositionNumber(), x.getOrderReference(), x.getAmount(), x.getItemData(), x.getLot(), x.getScale(), x.getOperator()==null?null:x.getOperator().getName(), x.getQaLock(), x.getUnitLoad(), x.getCreated());
	}
	
	public LOSGoodsReceiptPositionTO(Long id, int version, 
					String positionNumber,
					String orderReference,
					BigDecimal amount,
					String itemData, 
					String lot,
					int scale,
					String operatorName,
					int qaLock,
					String unitLoad,
					Date created) 
	{
		super(id, version, positionNumber);
		this.orderReference = orderReference;
		this.amount = amount;
		this.itemData = itemData;
		this.lot = lot;
		try {
			this.amount = amount.setScale(scale);
		}
		catch( Throwable t ) {
			this.amount = amount;
		}
		this.operatorName = operatorName;
		this.qaLock = qaLock;
		this.unitLoad = unitLoad;
		this.created = created;
	}

	public String getOrderReference() {
		return orderReference;
	}

	public void setOrderReference(String orderReference) {
		this.orderReference = orderReference;
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
