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
import org.mywms.model.User;

import de.linogistix.los.model.State;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;

/**
 * @author krane
 *
 */
@Entity
@Table(name = "los_replenishorder") 
public class LOSReplenishOrder extends BasicClientAssignedEntity {
	private static final long serialVersionUID = 1L;
	
	@Column(nullable = false, unique = true)
    private String number;
    
    @ManyToOne(optional=false)
    private ItemData itemData;

	@ManyToOne(optional=true, fetch=FetchType.LAZY)
    private Lot lot;
	
    private int state = State.RAW;

	@ManyToOne(optional=true, fetch=FetchType.LAZY)
    private StorageLocation destination;
    
    private String requestedRack;

	@ManyToOne(optional=true, fetch=FetchType.LAZY)
    private StorageLocation requestedLocation;
    
	@ManyToOne(optional=true, fetch=FetchType.LAZY)
    private StockUnit stockUnit;
    private String sourceLocationName;

    private int prio = 50;

	@Column(nullable = true, precision=17, scale=4)
    private BigDecimal requestedAmount = null;
    
	@ManyToOne(optional=true, fetch=FetchType.LAZY)
    private User operator;

    public ItemData getItemData() {
		return itemData;
	}
	public void setItemData(ItemData itemData) {
		this.itemData = itemData;
	}

	public Lot getLot() {
		return lot;
	}
	public void setLot(Lot lot) {
		this.lot = lot;
	}

	
    public BigDecimal getRequestedAmount() {
		if( getItemData() != null && requestedAmount != null ) {
			return requestedAmount.setScale(getItemData().getScale());
		}
		return requestedAmount;
	}
	public void setRequestedAmount(BigDecimal requestedAmount) {
		this.requestedAmount = requestedAmount;
	}
	
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

    public StorageLocation getDestination() {
        return destination;
    }
    public void setDestination(StorageLocation destination) {
        this.destination = destination;
    }

	public int getPrio() {
		return prio;
	}
	public void setPrio(int prio) {
		this.prio = prio;
	}

	public StockUnit getStockUnit() {
		return stockUnit;
	}
	public void setStockUnit(StockUnit stockUnit) {
		this.stockUnit = stockUnit;
	}
	
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
	
	public String getRequestedRack() {
		return requestedRack;
	}
	public void setRequestedRack(String requestedRack) {
		this.requestedRack = requestedRack;
	}

	public StorageLocation getRequestedLocation() {
		return requestedLocation;
	}
	public void setRequestedLocation(StorageLocation requestedLocation) {
		this.requestedLocation = requestedLocation;
	}
	
	@PrePersist
	@PreUpdate
	public void setRedundantValues() {
		sourceLocationName = stockUnit == null ? null : stockUnit.getUnitLoad().getStorageLocation().getName();
	}
	
	@Override
    public String toUniqueString() {
        return getNumber();
    }

}
