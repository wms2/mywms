/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import java.math.BigDecimal;

import de.linogistix.los.inventory.model.LOSUnitLoadAdvicePosition;
import de.linogistix.los.query.BODTO;

public class LOSUnitLoadAdvicePositionTO extends BODTO<LOSUnitLoadAdvicePosition>{
	
	private static final long serialVersionUID = 1L;
	
	private String itemData;
	private BigDecimal notifiedAmount;
	
	
	public LOSUnitLoadAdvicePositionTO(LOSUnitLoadAdvicePosition x) {
		this(x.getId(), x.getVersion(), x.getPositionNumber(), x.getItemData().getNumber(), x.getNotifiedAmount());
	}
	
	public LOSUnitLoadAdvicePositionTO(Long id, int version, String posNo, String itemData, BigDecimal notifiedAmount){
		super(id, version, posNo);
		this.itemData = itemData;
		this.notifiedAmount = notifiedAmount;
	}


	public String getItemData() {
		return itemData;
	}


	public void setItemData(String itemData) {
		this.itemData = itemData;
	}


	public BigDecimal getNotifiedAmount() {
		return notifiedAmount;
	}


	public void setNotifiedAmount(BigDecimal notifiedAmount) {
		this.notifiedAmount = notifiedAmount;
	}



}
