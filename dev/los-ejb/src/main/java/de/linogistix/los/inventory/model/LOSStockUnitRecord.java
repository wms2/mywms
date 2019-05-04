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
	
	private String toStockUnitIdentity;
	
	private String toUnitLoad;
	
	private String toStorageLocation;
	
	private String fromStockUnitIdentity;
	
	private String fromUnitLoad;
	
	private String fromStorageLocation;
	
	private String activityCode;
	
	private String operator;
	
	private LOSStockUnitRecordType type;
	
	private String itemData;
	
	private int scale;
	
	private String lot;
    
	/**Amount that has been transferred */ 
	private BigDecimal amount;
	
	/**The amount of the stock unit after the post*/
	private BigDecimal amountStock;
	
	private String serialNumber;
	
	private String unitLoadType;

	public LOSStockUnitRecord(){
		
	}
	
	@Column(updatable=false)
	public String getToStockUnitIdentity() {
		return toStockUnitIdentity;
	}

	public void setToStockUnitIdentity(String toStockUnitIdentity) {
		this.toStockUnitIdentity = toStockUnitIdentity;
	}

	@Column(updatable=false)
	public String getFromStockUnitIdentity() {
		return fromStockUnitIdentity;
	}
	
	public void setFromStockUnitIdentity(String fromStockUnitIdentiy) {
		this.fromStockUnitIdentity = fromStockUnitIdentiy;
	}

	@Column(nullable=false, updatable=false)
	public String getActivityCode() {
		return activityCode;
	}

	public void setActivityCode(String activityCode) {
		this.activityCode = activityCode;
	}

	@Column(nullable=false, updatable=false)
	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	@Enumerated(EnumType.STRING)
	public LOSStockUnitRecordType getType() {
		return type;
	}

	public void setType(LOSStockUnitRecordType type) {
		this.type = type;
	}

	@Column(updatable=false)
	public String getItemData() {
		return itemData;
	}

	public void setItemData(String itemData) {
		this.itemData = itemData;
	}

	@Column(updatable=false)
	public String getLot() {
		return lot;
	}

	public void setLot(String lot) {
		this.lot = lot;
	}

	@Column(updatable=false, precision=17, scale=4)
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(updatable=false, precision=17, scale=4)
	public BigDecimal getAmountStock() {
		return amountStock;
	}

	public void setAmountStock(BigDecimal amountStock) {
		this.amountStock = amountStock;
	}

	@Column(updatable=false)
	public String getToUnitLoad() {
		return toUnitLoad;
	}

	public void setToUnitLoad(String toUnitLoad) {
		this.toUnitLoad = toUnitLoad;
	}

	@Column(nullable=false, updatable=false)
	public String getToStorageLocation() {
		return toStorageLocation;
	}

	public void setToStorageLocation(String toStorageLocation) {
		this.toStorageLocation = toStorageLocation;
	}

	@Column(updatable=false)
	public String getFromUnitLoad() {
		return fromUnitLoad;
	}

	public void setFromUnitLoad(String fromUnitLoad) {
		this.fromUnitLoad = fromUnitLoad;
	}

	@Column(nullable=false, updatable=false)
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
