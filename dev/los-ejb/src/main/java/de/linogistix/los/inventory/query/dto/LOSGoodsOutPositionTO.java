/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import de.linogistix.los.inventory.model.LOSGoodsOutRequestPosition;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPositionState;
import de.linogistix.los.query.BODTO;

public class LOSGoodsOutPositionTO extends BODTO<LOSGoodsOutRequestPosition> {

	private static final long serialVersionUID = 1L;
	
	private String unitLoadLabel;
	private String locationName;
	private String outState;
	private String goodsOutNumber;
	
	
	public LOSGoodsOutPositionTO(LOSGoodsOutRequestPosition x) {
		this(x.getId(), x.getVersion(), x.getOutState(), x.getSource().getLabelId(), x.getSource().getStorageLocation().getName(), x.getGoodsOutRequest().getNumber());
	}
	
	public LOSGoodsOutPositionTO(Long id, int version,
				LOSGoodsOutRequestPositionState outState,
				String unitLoadLabel,
				String locationName,
				String goodsOutNumber) {
		super(id, version, unitLoadLabel);
		this.unitLoadLabel = unitLoadLabel;
		this.locationName = locationName;
		this.outState = outState.name();
		this.goodsOutNumber = goodsOutNumber;
	}

	public String getUnitLoadLabel() {
		return unitLoadLabel;
	}

	public void setUnitLoadLabel(String unitLoadLabel) {
		this.unitLoadLabel = unitLoadLabel;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getOutState() {
		return outState;
	}

	public void setOutState(String outState) {
		this.outState = outState;
	}

	public String getGoodsOutNumber() {
		return goodsOutNumber;
	}

	public void setGoodsOutNumber(String goodsOutNumber) {
		this.goodsOutNumber = goodsOutNumber;
	}

	

	
}
