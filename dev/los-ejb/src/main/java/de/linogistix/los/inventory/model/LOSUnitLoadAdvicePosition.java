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

import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.product.ItemData;

@Entity
@Table(name="los_uladvicepos")
public class LOSUnitLoadAdvicePosition extends BasicClientAssignedEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne(optional=false)
	private LOSUnitLoadAdvice unitLoadAdvice;
	
	@Column(nullable=false)
	private String positionNumber;
	
	@ManyToOne(optional=true)
	private Lot lot;
	
	@ManyToOne(optional=false)
	private ItemData itemData;
	
	@Column(nullable=false, precision=17, scale=4)
	private BigDecimal notifiedAmount;

	public LOSUnitLoadAdvice getUnitLoadAdvice() {
		return unitLoadAdvice;
	}

	public void setUnitLoadAdvice(LOSUnitLoadAdvice unitLoadAdvice) {
		this.unitLoadAdvice = unitLoadAdvice;
	}

	public String getPositionNumber() {
		return positionNumber;
	}

	public void setPositionNumber(String positionNumber) {
		this.positionNumber = positionNumber;
	}

	public Lot getLot() {
		return lot;
	}

	public void setLot(Lot lot) {
		this.lot = lot;
	}

	public ItemData getItemData() {
		return itemData;
	}

	public void setItemData(ItemData itemData) {
		this.itemData = itemData;
	}

	public BigDecimal getNotifiedAmount() {
		return notifiedAmount;
	}

	public void setNotifiedAmount(BigDecimal notifiedAmount) {
		this.notifiedAmount = notifiedAmount;
	}
	
}
