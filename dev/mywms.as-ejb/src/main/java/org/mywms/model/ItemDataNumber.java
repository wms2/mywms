/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package org.mywms.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.mywms.facade.FacadeException;

/**
 * The ItemDataNumber stores a list of numbers (e.g. EAN-codes) for one ItemData
 * 
 * @author krane
 */
@Entity
@Table(name = "los_itemdata_number", uniqueConstraints = { 
		@UniqueConstraint(columnNames = {
				"number","itemdata_id" }) })
public class ItemDataNumber extends BasicClientAssignedEntity
{
    private static final long serialVersionUID = 1L;

    private String number = null;
    private String manufacturerName = null;
    private ItemData itemData;
    private int index = 0;
    
    @Column(nullable = false)
    public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getManufacturerName() {
		return manufacturerName;
	}

	public void setManufacturerName(String manufacturerName) {
		this.manufacturerName = manufacturerName;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@ManyToOne(optional = false)
    public ItemData getItemData() {
		return itemData;
	}

	public void setItemData(ItemData itemData) {
		this.itemData = itemData;
	}

	/**
	 * Checks, if some constraints are kept during the previous operations.
	 * 
	 * @throws FacadeException
	 */
	@PreUpdate
	@PrePersist
	public void sanityCheck() throws FacadeException {

		if( number != null ) {
			number = number.trim();
		}
		if( number != null && number.length()==0 ) {
			number = null;
		}
		
		if( itemData != null && !itemData.getClient().equals(getClient())) {
			setClient(itemData.getClient());
		}

	}

	@Override
    public String toUniqueString() {
		return "" + (itemData == null ? "/ " : itemData.getNumber() + " / ") + number;
    }
    
    
}
