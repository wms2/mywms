/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.stocktaking.query.dto;

import java.math.BigDecimal;

import de.linogistix.los.query.BODTO;
import de.linogistix.los.stocktaking.model.LOSStocktakingRecord;
import de.linogistix.los.stocktaking.model.LOSStocktakingState;

public class StockTakingRecordTO extends BODTO<LOSStocktakingRecord> {

	private static final long serialVersionUID = 1L;
	
	public String clientNo;
		
	public String unitLoad;
	
	public String itemNo;
	
	public String lotNo;
	
	public BigDecimal plannedQuantity;
	
	public BigDecimal countedQuantity;
		
	public String state;

	public StockTakingRecordTO(LOSStocktakingRecord r){
		this(r.getId(), r.getVersion(), r.getId(), r.getClientNo(), r.getUnitLoadLabel(), r.getItemNo(),
				r.getLotNo(), r.getPlannedQuantity(), r.getCountedQuantity(), r.getState());
	}
	
	public StockTakingRecordTO(Long id, int version, long ident, 
							   String clientNo, String unitload, 
							   String itemNo, String lotNo,
							   BigDecimal plannedQuantity,
							   BigDecimal countedQuantity, LOSStocktakingState state) 
	{
		super(id, version, ident);
		this.clientNo = clientNo;
		this.unitLoad = unitload;
		
		this.itemNo = itemNo;
		this.lotNo = lotNo;
		
		this.plannedQuantity = plannedQuantity;
		this.countedQuantity = countedQuantity;
		this.state = state.name();
	}

	public String getClientNo() {
		return clientNo;
	}

	public void setClientNo(String clientNo) {
		this.clientNo = clientNo;
	}

	public String getItemNo() {
		return itemNo;
	}

	public void setItemNo(String itemNo) {
		this.itemNo = itemNo;
	}

	public String getLotNo() {
		return lotNo;
	}

	public void setLotNo(String lotNo) {
		this.lotNo = lotNo;
	}

	public String getUnitLoad() {
		return unitLoad;
	}

	public void setUnitLoad(String serialNo) {
		this.unitLoad = serialNo;
	}

	public BigDecimal getPlannedQuantity() {
		return plannedQuantity;
	}

	public void setPlannedQuantity(BigDecimal plannedQuantity) {
		this.plannedQuantity = plannedQuantity;
	}

	public BigDecimal getCountedQuantity() {
		return countedQuantity;
	}

	public void setCountedQuantity(BigDecimal countedQuantity) {
		this.countedQuantity = countedQuantity;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	
}
