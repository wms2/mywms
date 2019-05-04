/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
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
import javax.persistence.Table;

import org.mywms.model.BasicClientAssignedEntity;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;

@Entity
@Table(name="los_uladvicepos")
public class LOSUnitLoadAdvicePosition extends BasicClientAssignedEntity {

	private static final long serialVersionUID = 1L;

	private LOSUnitLoadAdvice unitLoadAdvice;
	
	private String positionNumber;
	
	private Lot lot;
	
	private ItemData itemData;
	
	private BigDecimal notifiedAmount;

	@ManyToOne(optional=false)
	public LOSUnitLoadAdvice getUnitLoadAdvice() {
		return unitLoadAdvice;
	}

	public void setUnitLoadAdvice(LOSUnitLoadAdvice unitLoadAdvice) {
		this.unitLoadAdvice = unitLoadAdvice;
	}

	@Column(nullable=false)
	public String getPositionNumber() {
		return positionNumber;
	}

	public void setPositionNumber(String positionNumber) {
		this.positionNumber = positionNumber;
	}

	@ManyToOne(optional=true)
	public Lot getLot() {
		return lot;
	}

	public void setLot(Lot lot) {
		this.lot = lot;
	}

	@ManyToOne(optional=false)
	public ItemData getItemData() {
		return itemData;
	}

	public void setItemData(ItemData itemData) {
		this.itemData = itemData;
	}

	@Column(nullable=false, precision=17, scale=4)
	public BigDecimal getNotifiedAmount() {
		return notifiedAmount;
	}

	public void setNotifiedAmount(BigDecimal notifiedAmount) {
		this.notifiedAmount = notifiedAmount;
	}
	
}
