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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.mywms.model.BasicClientAssignedEntity;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;
import org.mywms.model.User;

import de.linogistix.los.location.model.LOSRack;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.model.State;

/**
 * @author krane
 *
 */
@Entity
@Table(name = "los_replenishorder") 
public class LOSReplenishOrder extends BasicClientAssignedEntity {
	private static final long serialVersionUID = 1L;
	
    private String number;
    
    private ItemData itemData;
    private Lot lot;
	
    private int state = State.RAW;

    private LOSStorageLocation destination;
    
    private LOSRack requestedRack;
    private LOSStorageLocation requestedLocation;
    
    private StockUnit stockUnit;
    private String sourceLocationName;

    private int prio = 50;

    private BigDecimal requestedAmount = null;
    
    private User operator;

    @ManyToOne(optional=false)
    public ItemData getItemData() {
		return itemData;
	}
	public void setItemData(ItemData itemData) {
		this.itemData = itemData;
	}

	@ManyToOne(optional=true, fetch=FetchType.LAZY)
	public Lot getLot() {
		return lot;
	}
	public void setLot(Lot lot) {
		this.lot = lot;
	}

	
	@Column(nullable = true, precision=17, scale=4)
    public BigDecimal getRequestedAmount() {
		if( getItemData() != null && requestedAmount != null ) {
			return requestedAmount.setScale(getItemData().getScale());
		}
		return requestedAmount;
	}
	public void setRequestedAmount(BigDecimal requestedAmount) {
		this.requestedAmount = requestedAmount;
	}
	
	@Column(nullable = false, unique = true)
    public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	
    public int getState() {
        return state;
    }
    public void setState(int state) {
        this.state = state;
    }

	@ManyToOne(optional=true, fetch=FetchType.LAZY)
    public LOSStorageLocation getDestination() {
        return destination;
    }
    public void setDestination(LOSStorageLocation destination) {
        this.destination = destination;
    }

	public int getPrio() {
		return prio;
	}
	public void setPrio(int prio) {
		this.prio = prio;
	}

	@ManyToOne(optional=true, fetch=FetchType.LAZY)
	public StockUnit getStockUnit() {
		return stockUnit;
	}
	public void setStockUnit(StockUnit stockUnit) {
		this.stockUnit = stockUnit;
	}
	
	@ManyToOne(optional=true, fetch=FetchType.LAZY)
	public User getOperator() {
		return operator;
	}
	public void setOperator(User operator) {
		this.operator = operator;
	}
	
	
	public String getSourceLocationName() {
		return sourceLocationName;
	}
	// hibernate only, the values are written in PrePersist, PreUpdate
	@SuppressWarnings("unused")
	private void setSourceLocationName(String sourceLocationName) {
		this.sourceLocationName = sourceLocationName;
	}
	
	@ManyToOne(optional=true, fetch=FetchType.LAZY)
	public LOSRack getRequestedRack() {
		return requestedRack;
	}
	public void setRequestedRack(LOSRack requestedRack) {
		this.requestedRack = requestedRack;
	}

	@ManyToOne(optional=true, fetch=FetchType.LAZY)
	public LOSStorageLocation getRequestedLocation() {
		return requestedLocation;
	}
	public void setRequestedLocation(LOSStorageLocation requestedLocation) {
		this.requestedLocation = requestedLocation;
	}
	
	@PrePersist
	@PreUpdate
	public void setRedundantValues() {
		sourceLocationName = stockUnit == null ? null : ((LOSUnitLoad)stockUnit.getUnitLoad()).getStorageLocation().getName();
	}
	
	@Override
    public String toUniqueString() {
        return getNumber();
    }

}
