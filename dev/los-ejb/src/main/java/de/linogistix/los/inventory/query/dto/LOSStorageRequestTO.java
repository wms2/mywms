/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import de.linogistix.los.query.BODTO;
import de.wms2.mywms.transport.TransportOrder;

public class LOSStorageRequestTO extends BODTO<TransportOrder> {

	private static final long serialVersionUID = 1L;

	private String unitLoadLabel;
	private int state;
	private String destinationName;

	public LOSStorageRequestTO() {
	}

	public LOSStorageRequestTO(Long id, int version, String name) {
		super(id, version, name);
	}

	public LOSStorageRequestTO(Long id, int version, String name, String unitLoadLabel, int state,
			String destinationName) {
		super(id, version, name);
		this.unitLoadLabel = unitLoadLabel;
		this.state = state;
		this.destinationName = destinationName;
	}

	public String getUnitLoadLabel() {
		return unitLoadLabel;
	}

	public void setUnitLoadLabel(String unitLoadLabel) {
		this.unitLoadLabel = unitLoadLabel;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}
}
