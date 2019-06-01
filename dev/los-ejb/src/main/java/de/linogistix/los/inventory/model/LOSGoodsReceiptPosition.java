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
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.mywms.model.BasicClientAssignedEntity;
import org.mywms.model.User;

import de.wms2.mywms.inventory.StockUnit;

/**
 * A Position within {@link LOSGoodsReceipt}. 
 * 
 * Normally each {@link LOSGoodsReceiptPosition}  represents a {@link StockUnit} 
 * that has been acknowledged during goods receipt (goods in) process.
 * 
 * @author trautm
 *
 */
@Entity
@Table(name = "los_grrposition")
@NamedQueries({
@NamedQuery(name="LOSGoodsReceiptPosition.queryByNumber", query="FROM LOSGoodsReceiptPosition pos WHERE pos.positionNumber=:number"),
@NamedQuery(name="LOSGoodsReceiptPosition.existsByNumber", query="SELECT pos.id FROM LOSGoodsReceiptPosition pos WHERE pos.positionNumber=:number")
})
public class LOSGoodsReceiptPosition extends BasicClientAssignedEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * Unique identifier
	 */
	@Column(unique = true)
	private String positionNumber;

	/**
	 * Containing {@link LOSGoodsReceipt}
	 */
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	private LOSGoodsReceipt goodsReceipt;

	/**
	 * References an external order number
	 */
	private String orderReference;

	/**
	 * The amount that has been entered during goods receipt process.
	 */
	@Column(precision=17, scale=4)
	private BigDecimal amount;

	private String itemData;
	
	/**
	 * Scale of item data
	 */
	private int scale;

	private String lot;

	/**
	 * Permanently holds string reference to the {@link StockUnit} that has been created 
	 * during goods receipt
	 */
	private String stockUnitStr;

	/**
	 * {@link StockUnit} of this entity.
	 * 
	 * Might be null after parent {@link LOSGoodsReceipt} has been set to {@link LOSGoodsOutRequestState.FINISHED}!
	 * Information of StockUnit is therefore permanently stored in {@link LOSGoodsReceiptPosition.getStockUnitStr}
	 */
	@OneToOne
	private StockUnit stockUnit;

	private String unitLoad;

	@Enumerated(EnumType.STRING)
	private LOSGoodsReceiptType receiptType;

	/**
	 * An optional description of quality faults seen during goods receipt process. 
	 */
	@Column(length=1024)
	private String qaFault;
	
	private int qaLock = 0;
	
	@ManyToOne(optional=true)
	private LOSAdvice relatedAdvice;
	
	private LOSGoodsReceiptState state=LOSGoodsReceiptState.RAW;
	
	@ManyToOne(optional=true, fetch=FetchType.LAZY)
	private User operator;
	
	public LOSGoodsReceiptPosition(){
		
	}
	
	public String getPositionNumber() {
		return positionNumber;
	}

	public void setPositionNumber(String positionNumber) {
		this.positionNumber = positionNumber;
	}

	public LOSGoodsReceipt getGoodsReceipt() {
		return goodsReceipt;
	}

	public void setGoodsReceipt(LOSGoodsReceipt goodsReceipt) {
		this.goodsReceipt = goodsReceipt;
	}

	public String getOrderReference() {
		return orderReference;
	}

	public void setOrderReference(String orderReference) {
		this.orderReference = orderReference;
	}

	public BigDecimal getAmount() {
		try {
			return amount.setScale(scale);
		}
		catch( Throwable t ) {}
		
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Override
	public String toUniqueString() {
		return positionNumber;
	}


	public StockUnit getStockUnit() {
		return stockUnit;
	}

	public void setStockUnit(StockUnit stockUnit) {
		this.stockUnit = stockUnit;

		if (stockUnit != null && stockUnit.getItemData() != null) {
			setItemData(stockUnit.getItemData().getNumber());
			setScale(stockUnit.getItemData().getScale());
			if (stockUnit.getLot() != null) {
				setLot(stockUnit.getLot().getName());
			}

			// 11.10.2012, krane, do not change receipt data, when stock changes
			if( stockUnitStr==null ) {
				setStockUnitStr(stockUnit.toUniqueString());
			}
			if( unitLoad==null ) {
				setUnitLoad(stockUnit.getUnitLoad().toUniqueString());
			}
		}
	}

	public String getItemData() {
		return itemData;
	}

	void setItemData(String itemData) {
		this.itemData = itemData;
	}

	public String getLot() {
		return lot;
	}

	void setLot(String lot) {
		this.lot = lot;
	}

	public String getStockUnitStr() {
		return stockUnitStr;
	}

	void setStockUnitStr(String stockUnitName) {
		this.stockUnitStr = stockUnitName;
	}

	public void setReceiptType(LOSGoodsReceiptType receiptType) {
		this.receiptType = receiptType;
	}

	public LOSGoodsReceiptType getReceiptType() {
		return receiptType;
	}

	public void setQaFault(String qaFault) {
		this.qaFault = qaFault;
	}
	
	public String getQaFault() {
		return this.qaFault;
	}

	public int getQaLock() {
		return qaLock;
	}

	public void setQaLock(int qaLock) {
		this.qaLock = qaLock;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public int getScale() {
		return scale;
	}

	public LOSAdvice getRelatedAdvice() {
		return relatedAdvice;
	}

	public void setRelatedAdvice(LOSAdvice relatedAdvice) {
		this.relatedAdvice = relatedAdvice;
	}

	public void setUnitLoad(String unitLoad) {
		this.unitLoad = unitLoad;
	}

	public String getUnitLoad() {
		return unitLoad;
	}

	public LOSGoodsReceiptState getState() {
		return state;
	}

	public void setState(LOSGoodsReceiptState state) {
		this.state = state;
	}

	public User getOperator() {
		return operator;
	}

	public void setOperator(User operator) {
		this.operator = operator;
	}

}
