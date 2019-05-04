/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.ws;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)             
@XmlType(
		name = "GoodsReceiptPositionTO",
		namespace="http://com.linogistix/connector/wms/inventory" 
)
public class GoodsReceiptPositionTO implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String itemDataNumber;
	
	private String lotName;
	
	private BigDecimal amount;
	
	private String unitLoadType;
	
	private String unitLoadLabelId;
	
	private int lock = 0;
	
	private String advice;

	public String getAdvice() {
		return advice;
	}

	public void setAdvice(String advice) {
		this.advice = advice;
	}

	public String getItemDataNumber() {
		return itemDataNumber;
	}

	public void setItemDataNumber(String itemDataNumber) {
		this.itemDataNumber = itemDataNumber;
	}

	public String getLotName() {
		return lotName;
	}

	public void setLotName(String lotName) {
		this.lotName = lotName;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getUnitLoadType() {
		return unitLoadType;
	}

	public void setUnitLoadType(String unitLoadType) {
		this.unitLoadType = unitLoadType;
	}

	public String getUnitLoadLabelId() {
		return unitLoadLabelId;
	}

	public void setUnitLoadLabelId(String unitLoadLabelId) {
		this.unitLoadLabelId = unitLoadLabelId;
	}

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}
	
	
	
}
