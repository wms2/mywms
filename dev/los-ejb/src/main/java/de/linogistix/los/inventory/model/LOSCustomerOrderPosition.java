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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.mywms.model.BasicClientAssignedEntity;

import de.linogistix.los.model.State;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.product.ItemData;

/**
 * A position of a customer order
 * 
 * @author krane
 */
@Entity
@Table(name = "los_customerpos", uniqueConstraints = { 
		@UniqueConstraint(columnNames = {"client_id","number" }) 
})
@NamedQueries({
	@NamedQuery(name="LOSCustomerOrderPosition.queryByNumber", query="FROM LOSCustomerOrderPosition pos WHERE pos.number=:number"),
	@NamedQuery(name="LOSCustomerOrderPosition.idByNumber", query="SELECT pos.id FROM LOSCustomerOrderPosition pos WHERE pos.number=:number")
})
public class LOSCustomerOrderPosition extends BasicClientAssignedEntity{
	private static final long serialVersionUID = 1L;

	private String number;

	private String externalId;

	@Column(nullable = false)
	private int index;

	@ManyToOne(optional = false)
	private LOSCustomerOrder order;

	@ManyToOne(optional = false)
	private ItemData itemData;

	@Column(precision = 17, scale = 4, nullable = false)
	private BigDecimal amount = BigDecimal.ZERO;

	@ManyToOne(optional = true)
	private Lot lot;

	@Column(nullable = false)
	private boolean partitionAllowed;

	@Column(nullable = false)
	private int state = State.RAW;

	@Column(precision = 17, scale = 4, nullable = false)
	private BigDecimal amountPicked = BigDecimal.ZERO;

	private String serialNumber;

    public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}

	public String getExternalId() {
		return externalId;
	}
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	
    public ItemData getItemData() {
        return itemData;
    }
    public void setItemData(ItemData itemData) {
        this.itemData = itemData;
    }

    public BigDecimal getAmount() {
    	if( itemData != null ) {
			try{
				return amount.setScale(getItemData().getScale());
			}catch(ArithmeticException ae){
				System.out.println("LOSCustomerOrderPosition "+getId()+" : expected scale = "+itemData.getScale()+" but was "+amount);
			}
    	}
    	return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Lot getLot() {
        return lot;
    }
    public void setLot(Lot lot) {
        this.lot = lot;
    }

    public boolean isPartitionAllowed() {
        return partitionAllowed;
    }
    public void setPartitionAllowed(boolean partitionAllowed) {
        this.partitionAllowed = partitionAllowed;
    }

	public void setState(int state) {
		this.state = state;
	}
	public int getState() {
		return state;
	}

	public BigDecimal getAmountPicked() {
    	if( itemData != null ) {
			try{
				return amountPicked.setScale(getItemData().getScale());
			}catch(ArithmeticException ae){
				System.out.println("LOSCustomerOrderPosition "+getId()+" : expected scale = "+itemData.getScale()+" but was "+amount);
			}
    	}
    	return amountPicked;
	}
	public void setAmountPicked(BigDecimal amountPicked) {
		this.amountPicked = amountPicked;
	}

	public LOSCustomerOrder getOrder() {
		return order;
	}
	public void setOrder(LOSCustomerOrder order) {
		this.order = order;
	}
	
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}



	@Override
    public String toUniqueString() {
        return getNumber();
    }

	
}
