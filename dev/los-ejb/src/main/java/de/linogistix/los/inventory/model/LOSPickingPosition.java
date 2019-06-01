/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */

package de.linogistix.los.inventory.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.mywms.model.BasicClientAssignedEntity;

import de.linogistix.los.model.State;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.product.ItemData;

/**
 *
 * @author krane
 */
@Entity
@Table(name = "los_pickingpos")
@NamedQueries({
@NamedQuery(name="LOSPickingPosition.queryByCustomerOrder", query="FROM LOSPickingPosition pos WHERE pos.customerOrderPosition.order=:customerOrder"),
@NamedQuery(name="LOSPickingPosition.queryByCustomerOrderPos", query="FROM LOSPickingPosition pos WHERE pos.customerOrderPosition=:customerOrderPos")
})
public class LOSPickingPosition extends BasicClientAssignedEntity{
	private static final long serialVersionUID = 1L;
  
	public final static int PICKING_TYPE_DEFAULT = 0;
	public final static int PICKING_TYPE_PICK = 1;
	public final static int PICKING_TYPE_COMPLETE = 2;

	@ManyToOne(optional = true)
	private LOSPickingOrder pickingOrder;
	private String pickingOrderNumber;

	@ManyToOne(optional = true, fetch=FetchType.EAGER)
	private LOSCustomerOrderPosition customerOrderPosition;
	
	@Column(nullable = false, precision = 17, scale = 4)
	private BigDecimal amount = BigDecimal.ZERO;

	@Column(nullable = false, precision = 17, scale = 4)
	private BigDecimal amountPicked = BigDecimal.ZERO;

	@ManyToOne(optional = true, fetch=FetchType.EAGER)
	private StockUnit pickFromStockUnit;
	private String pickFromLocationName;
	private String pickFromUnitLoadLabel;
	
	@ManyToOne(optional = false)
	private ItemData itemData;

	@ManyToOne(optional=true, fetch=FetchType.LAZY)
	private LOSPickingUnitLoad pickToUnitLoad;

	@Column(nullable = false)
	private int state = State.RAW;
	
	@Column(nullable = false)
	private int pickingType = PICKING_TYPE_DEFAULT;
	
	@ManyToOne(optional=false)
	private LOSOrderStrategy strategy;

	@ManyToOne(optional = true, fetch=FetchType.EAGER)
	private Lot lotPicked;

	public Lot getLotPicked() {
		return lotPicked;
	}
	public void setLotPicked(Lot lotPicked) {
		this.lotPicked = lotPicked;
	}
	
	public LOSCustomerOrderPosition getCustomerOrderPosition() {
		return customerOrderPosition;
	}
	public void setCustomerOrderPosition(LOSCustomerOrderPosition customerOrderPosition) {
		this.customerOrderPosition = customerOrderPosition;
	}
	
	public BigDecimal getAmount() {
		if( getItemData() != null ) {
			return amount.setScale(getItemData().getScale());
		}
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getAmountPicked() {
		if( getItemData() != null ) {
			return amountPicked.setScale(getItemData().getScale());
		}
		return amountPicked;
	}
	public void setAmountPicked(BigDecimal amountPicked) {
		this.amountPicked = amountPicked;
	}

	public LOSPickingOrder getPickingOrder() {
		return pickingOrder;
	}
	public void setPickingOrder(LOSPickingOrder pickingOrder) {
		this.pickingOrder = pickingOrder;
	}
	
	public String getPickingOrderNumber() {
		return pickingOrderNumber;
	}
	// For hibernate only
	// prepersist writes the number of the current picking order
	@SuppressWarnings("unused")
	private void setPickingOrderNumber(String pickingOrderNumber) {
		this.pickingOrderNumber = pickingOrderNumber;
	}
	
	public StockUnit getPickFromStockUnit() {
		return pickFromStockUnit;
	}
	public void setPickFromStockUnit(StockUnit pickFromStockUnit) {
		this.pickFromStockUnit = pickFromStockUnit;
	}

	public String getPickFromLocationName() {
		return pickFromLocationName;
	}
	public void setPickFromLocationName(String pickFromLocationName) {
		this.pickFromLocationName = pickFromLocationName;
	}
	
	public String getPickFromUnitLoadLabel() {
		return pickFromUnitLoadLabel;
	}
	public void setPickFromUnitLoadLabel(String pickFromUnitLoadLabel) {
		this.pickFromUnitLoadLabel = pickFromUnitLoadLabel;
	}
	
	public ItemData getItemData() {
		return itemData;
	}
	public void setItemData(ItemData itemData) {
		this.itemData = itemData;
	}
	
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	
	public int getPickingType() {
		return pickingType;
	}
	public void setPickingType(int pickingType) {
		this.pickingType = pickingType;
	}
	
	public LOSOrderStrategy getStrategy() {
		return strategy;
	}
	public void setStrategy(LOSOrderStrategy strategy) {
		this.strategy = strategy;
	}
	
	
	public LOSPickingUnitLoad getPickToUnitLoad() {
		return pickToUnitLoad;
	}
	public void setPickToUnitLoad(LOSPickingUnitLoad pickToUnitLoad) {
		this.pickToUnitLoad = pickToUnitLoad;
	}
	
	@Transient
	public String getUnit() {
		return itemData == null ? null : itemData.getItemUnit().getName();
	}

	@SuppressWarnings("unused")
	@PrePersist
	@PreUpdate
	// For hibernate only. By annotation it is called before saving
	private void setRedundantValues() {
		if( pickingOrder == null ) {
			pickingOrderNumber = null;
		}
		else {
			pickingOrderNumber = pickingOrder.getNumber();
		}
	}

}
