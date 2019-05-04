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

import de.linogistix.los.inventory.model.LOSStockUnitRecord;
import de.linogistix.los.inventory.model.LOSStockUnitRecordType;
import de.linogistix.los.query.BODTO;

public class LOSStockUnitRecordTO extends BODTO<LOSStockUnitRecord> {

	private static final long serialVersionUID = 1L;

	public String itemData;
	
	public String lot;
	
	public String fromSu;
	public String fromSl;
	public String fromUl;
	
	public String toSu;
	public String toSl;
	public String toUl;
	
	public BigDecimal amount;
	public BigDecimal amountStock;
	
	public String activityCode;
	public String type;
	private String unitLoadType;
	private Date recordDate;

	public LOSStockUnitRecordTO(LOSStockUnitRecord rec) {
		this(rec.getId(), rec.getVersion(), rec.getId(), rec.getItemData(), rec.getLot(), rec.getAmount(), rec.getAmountStock(), rec.getFromStockUnitIdentity(), rec.getFromStorageLocation(), rec.getFromUnitLoad(), rec.getToStockUnitIdentity(), rec.getToStorageLocation(), rec.getToUnitLoad(), rec.getActivityCode(), rec.getType(), rec.getCreated(), rec.getUnitLoadType());
	}
	
	public LOSStockUnitRecordTO(Long id, int version, Long name, String itemData, String lot,BigDecimal amount, BigDecimal amountStock, String fromSu, String fromSl, String fromUl, String toSu, String toSl, String toUl, String activityCode, LOSStockUnitRecordType type){
		super(id, version, name);
		this.itemData = itemData;
		this.lot = lot;
		this.fromSu = fromSu;
		this.fromSl = fromSl;
		this.fromUl = fromUl;
		this.toSu = toSu;
		this.toSl = toSl;
		this.toUl = toUl;
		this.amount = amount;
		this.amountStock = amountStock;
		this.activityCode = activityCode;
		this.type = type.name();
		setClassName(LOSStockUnitRecord.class.getName());
	}
	
	public LOSStockUnitRecordTO(Long id, int version, Long name, String itemData, String lot,BigDecimal amount, BigDecimal amountStock, String fromSu, String fromSl, String fromUl, String toSu, String toSl, String toUl, String activityCode, LOSStockUnitRecordType type, Date recordDate, String unitLoadType){
		super(id, version, name);
		this.itemData = itemData;
		this.lot = lot;
		this.fromSu = fromSu;
		this.fromSl = fromSl;
		this.fromUl = fromUl;
		this.toSu = toSu;
		this.toSl = toSl;
		this.toUl = toUl;
		this.amount = amount;
		this.amountStock = amountStock;
		this.activityCode = activityCode;
		this.type = type.name();
		this.unitLoadType = unitLoadType;
		this.recordDate=recordDate;
		setClassName(LOSStockUnitRecord.class.getName());
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

	
	public String getFromSu() {
		return fromSu;
	}

	public void setFromSu(String fromSu) {
		this.fromSu = fromSu;
	}

	public String getFromSl() {
		return fromSl;
	}

	public void setFromSl(String fromSl) {
		this.fromSl = fromSl;
	}

	public String getFromUl() {
		return fromUl;
	}

	public void setFromUl(String fromUl) {
		this.fromUl = fromUl;
	}

	public String getToSu() {
		return toSu;
	}

	public void setToSu(String toSu) {
		this.toSu = toSu;
	}

	public String getToSl() {
		return toSl;
	}

	public void setToSl(String toSl) {
		this.toSl = toSl;
	}

	public String getToUl() {
		return toUl;
	}

	public void setToUl(String toUl) {
		this.toUl = toUl;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getAmountStock() {
		return amountStock;
	}

	public void setAmountStock(BigDecimal amountStock) {
		this.amountStock = amountStock;
	}

	public String getActivityCode() {
		return activityCode;
	}

	public void setActivityCode(String activityCode) {
		this.activityCode = activityCode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUnitLoadType() {
		return unitLoadType;
	}

	public void setUnitLoadType(String unitLoadType) {
		this.unitLoadType = unitLoadType;
	}

	public Date getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(Date recordDate) {
		this.recordDate = recordDate;
	}


	
}
