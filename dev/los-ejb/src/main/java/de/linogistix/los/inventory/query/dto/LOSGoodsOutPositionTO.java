/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import de.linogistix.los.query.BODTO;
import de.wms2.mywms.shipping.ShippingOrderLine;

public class LOSGoodsOutPositionTO extends BODTO<ShippingOrderLine> {

	private static final long serialVersionUID = 1L;
	
	private String unitLoadLabel;
	private String locationName;
	private int state;
	private String goodsOutNumber;
	
	
	public LOSGoodsOutPositionTO(ShippingOrderLine x) {
		this(x.getId(), x.getVersion(), x.getState(), x.getPacket().getUnitLoad().getLabelId(), x.getPacket().getUnitLoad().getStorageLocation().getName(), x.getShippingOrder().getOrderNumber());
	}
	
	public LOSGoodsOutPositionTO(Long id, int version,
				int state,
				String unitLoadLabel,
				String locationName,
				String goodsOutNumber) {
		super(id, version, unitLoadLabel);
		this.unitLoadLabel = unitLoadLabel;
		this.locationName = locationName;
		this.state = state;
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

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getGoodsOutNumber() {
		return goodsOutNumber;
	}

	public void setGoodsOutNumber(String goodsOutNumber) {
		this.goodsOutNumber = goodsOutNumber;
	}

	

	
}
