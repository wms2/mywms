/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import de.linogistix.los.inventory.model.LOSStorageRequest;
import de.linogistix.los.inventory.model.LOSStorageRequestState;
import de.linogistix.los.query.BODTO;

public class LOSStorageRequestTO extends BODTO<LOSStorageRequest> {

	private static final long serialVersionUID = 1L;

    private String unitLoadLabel;
    private LOSStorageRequestState requestState = LOSStorageRequestState.RAW;
    private String requestStateName;
    private String destinationName;


	public LOSStorageRequestTO() {
	}

	public LOSStorageRequestTO(Long id, int version, String name) {
		super(id, version, name);
	}
	
	public LOSStorageRequestTO(Long id, int version, String name, 
			String unitLoadLabel, LOSStorageRequestState requestState, String destinationName) {
		super(id, version, name);
		this.unitLoadLabel = unitLoadLabel;
		this.requestState = requestState;
		this.requestStateName = requestState.name();
		this.destinationName = destinationName;
	}

	
	public String getUnitLoadLabel() {
		return unitLoadLabel;
	}

	public void setUnitLoadLabel(String unitLoadLabel) {
		this.unitLoadLabel = unitLoadLabel;
	}

	public LOSStorageRequestState getRequestState() {
		return requestState;
	}

	public void setRequestState(LOSStorageRequestState requestState) {
		this.requestState = requestState;
	}

	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

	public String getRequestStateName() {
		return requestStateName;
	}
	public void setRequestStateName(String requestStateName) {
		this.requestStateName = requestStateName;
	}

}
