/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.query.dto;

import java.io.Serializable;

public class LOSRackTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer locationIndexMin = null;
	private Integer locationIndexMax = null;
	private Integer numLocation = null;
	
	public Integer getLocationIndexMin() {
		return locationIndexMin;
	}
	public void setLocationIndexMin(Integer locationIndexMin) {
		this.locationIndexMin = locationIndexMin;
	}
	public Integer getLocationIndexMax() {
		return locationIndexMax;
	}
	public void setLocationIndexMax(Integer locationIndexMax) {
		this.locationIndexMax = locationIndexMax;
	}
	public Integer getNumLocation() {
		return numLocation;
	}
	public void setNumLocation(Integer numLocation) {
		this.numLocation = numLocation;
	}
	
	
	
}
