/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.stocktaking.query.dto;

import de.linogistix.los.query.BODTO;
import de.linogistix.los.stocktaking.model.LOSStocktakingOrder;
import de.linogistix.los.stocktaking.model.LOSStocktakingState;

public class StockTakingOrderTO extends BODTO<LOSStocktakingOrder> {

	private static final long serialVersionUID = 1L;

	public String locationName;
	
	public String unitLoadLabel;
	
	public String operator;
	
	public String state;

	public StockTakingOrderTO(LOSStocktakingOrder x) {
		this(x.getId(), x.getVersion(), x.getId(), x.getLocationName(), x.getUnitLoadLabel(), x.getOperator(), x.getState());
	}
	
	public StockTakingOrderTO(Long id, int version, Long uniqueId, String location, String unitLoad, String operator, LOSStocktakingState state) {
		super(id, version, uniqueId);
		
		this.locationName = location;
		this.unitLoadLabel = unitLoad;
		this.operator = operator;
		this.state = state.name();
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getUnitLoadLabel() {
		return unitLoadLabel;
	}

	public void setUnitLoadLabel(String unitLoadLabel) {
		this.unitLoadLabel = unitLoadLabel;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	
	
}
