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
import org.mywms.model.StockUnit;
import org.mywms.model.User;

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

	private String positionNumber;

	private LOSGoodsReceipt goodsReceipt;

	private String orderReference;

	private BigDecimal amount;

	private String itemData;
	
	private int scale;

	private String lot;

	private String stockUnitStr;

	private StockUnit stockUnit;

	private String unitLoad;

	private LOSGoodsReceiptType receiptType;

	private String qaFault;
	
	private int qaLock = 0;
	
	private LOSAdvice relatedAdvice;
	
	private LOSGoodsReceiptState state=LOSGoodsReceiptState.RAW;
	
	private User operator;
	
	public LOSGoodsReceiptPosition(){
		
	}
	
	/**
	 * Unique identifier
	 * 
	 * @return
	 */
	@Column(unique = true)
	public String getPositionNumber() {
		return positionNumber;
	}

	public void setPositionNumber(String positionNumber) {
		this.positionNumber = positionNumber;
	}

	/**
	 * Containing {@link LOSGoodsReceipt}
	 * @return
	 */
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	public LOSGoodsReceipt getGoodsReceipt() {
		return goodsReceipt;
	}

	public void setGoodsReceipt(LOSGoodsReceipt goodsReceipt) {
		this.goodsReceipt = goodsReceipt;
	}

	/**
	 * References an external order number
	 * 
	 * @return
	 */
	public String getOrderReference() {
		return orderReference;
	}

	public void setOrderReference(String orderReference) {
		this.orderReference = orderReference;
	}

	/**
	 * The amount that has been entered during goods receipt process.
	 * 
	 * @return
	 */
	@Column(precision=17, scale=4)
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


	/**
	 * Returns {@link StockUnit} of this entity.
	 * 
	 * Might be null after parent {@link LOSGoodsReceipt} has been set to {@link LOSGoodsOutRequestState.FINISHED}!
	 * Information of StockUnit is therefore permanently stored in {@link LOSGoodsReceiptPosition.getStockUnitStr}
	 * 
	 * @return stock unit 
	 */
	@OneToOne
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

	/**
	 * Permanently holds string reference to the {@link StockUnit} that has been created 
	 * during goods receipt
	 * 
	 * @return
	 */
	public String getStockUnitStr() {
		return stockUnitStr;
	}

	void setStockUnitStr(String stockUnitName) {
		this.stockUnitStr = stockUnitName;
	}

	public void setReceiptType(LOSGoodsReceiptType receiptType) {
		this.receiptType = receiptType;
	}

	@Enumerated(EnumType.STRING)
	public LOSGoodsReceiptType getReceiptType() {
		return receiptType;
	}

	public void setQaFault(String qaFault) {
		this.qaFault = qaFault;
	}
	
	/**
	 * An optional description of quality faults seen during goods receipt process. 
	 * @return
	 */
	@Column(length=1024)
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

	/**
	 * Scale of item data
	 * @return
	 */
	public int getScale() {
		return scale;
	}

	@ManyToOne(optional=true)
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

	@ManyToOne(optional=true, fetch=FetchType.LAZY)
	public User getOperator() {
		return operator;
	}

	public void setOperator(User operator) {
		this.operator = operator;
	}

}
