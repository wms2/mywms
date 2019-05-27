/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.mywms.model.BasicClientAssignedEntity;

//@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)             
//@XmlType(
//		name = "LOSStockUnitRecord",
//		namespace="http://com.linogistix/inventory" )
@Entity
@Table(name="los_stockrecord")
public class LOSStockUnitRecord extends BasicClientAssignedEntity {

	private static final long serialVersionUID = 1L;
	
	@Column(updatable=false)
	private String toStockUnitIdentity;
	
	@Column(updatable=false)
	private String toUnitLoad;
	
	@Column(nullable=false, updatable=false)
	private String toStorageLocation;
	
	@Column(updatable=false)
	private String fromStockUnitIdentity;
	
	@Column(updatable=false)
	private String fromUnitLoad;
	
	@Column(nullable=false, updatable=false)
	private String fromStorageLocation;
	
	@Column(nullable=false, updatable=false)
	private String activityCode;
	
	@Column(nullable=false, updatable=false)
	private String operator;
	
	@Enumerated(EnumType.STRING)
	private LOSStockUnitRecordType type;
	
	@Column(updatable=false)
	private String itemData;
	
	private int scale;
	
	@Column(updatable=false)
	private String lot;
    
	/**Amount that has been transferred */ 
	@Column(updatable=false, precision=17, scale=4)
	private BigDecimal amount;
	
	/**The amount of the stock unit after the post*/
	@Column(updatable=false, precision=17, scale=4)
	private BigDecimal amountStock;
	
	private String serialNumber;
	
	private String unitLoadType;

	public LOSStockUnitRecord(){
		
	}
	
	public String getToStockUnitIdentity() {
		return toStockUnitIdentity;
	}

	public void setToStockUnitIdentity(String toStockUnitIdentity) {
		this.toStockUnitIdentity = toStockUnitIdentity;
	}

	public String getFromStockUnitIdentity() {
		return fromStockUnitIdentity;
	}
	
	public void setFromStockUnitIdentity(String fromStockUnitIdentiy) {
		this.fromStockUnitIdentity = fromStockUnitIdentiy;
	}

	public String getActivityCode() {
		return activityCode;
	}

	public void setActivityCode(String activityCode) {
		this.activityCode = activityCode;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public LOSStockUnitRecordType getType() {
		return type;
	}

	public void setType(LOSStockUnitRecordType type) {
		this.type = type;
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

	public String getToUnitLoad() {
		return toUnitLoad;
	}

	public void setToUnitLoad(String toUnitLoad) {
		this.toUnitLoad = toUnitLoad;
	}

	public String getToStorageLocation() {
		return toStorageLocation;
	}

	public void setToStorageLocation(String toStorageLocation) {
		this.toStorageLocation = toStorageLocation;
	}

	public String getFromUnitLoad() {
		return fromUnitLoad;
	}

	public void setFromUnitLoad(String fromUnitLoad) {
		this.fromUnitLoad = fromUnitLoad;
	}

	public String getFromStorageLocation() {
		return fromStorageLocation;
	}

	public void setFromStorageLocation(String fromStorageLocation) {
		this.fromStorageLocation = fromStorageLocation;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getUnitLoadType() {
		return unitLoadType;
	}

	public void setUnitLoadType(String unitLoadType) {
		this.unitLoadType = unitLoadType;
	}



}
