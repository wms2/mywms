/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.query.dto;

import java.util.Random;

import de.linogistix.los.location.constants.LOSStorageLocationLockState;
import de.linogistix.los.location.model.LOSRack;
import de.linogistix.los.query.BODTO;

public class LOSRackTO extends BODTO<LOSRack> {

	private static final long serialVersionUID = 1L;
	
	public LOSStorageLocationLockState lock;
	private Integer locationIndexMin = null;
	private Integer locationIndexMax = null;
	private Integer numLocation = null;
	private String aisle = null;
	
	public LOSRackTO(LOSRack rack){
		this(rack.getId(), rack.getVersion(), rack.getName(), rack.getLock(), rack.getAisle());
	}
	
	public LOSRackTO(Long id, int version, String name, int lock, String aisle){
		super(id, new Random().nextInt(7000000), name);
//		this.lock = lock;
                switch(lock){
                    case 0: this.lock = LOSStorageLocationLockState.NOT_LOCKED;
                                        break;
                    case 1: this.lock = LOSStorageLocationLockState.GENERAL;
                                        break;
                    case 2: this.lock = LOSStorageLocationLockState.GOING_TO_DELETE;
                                        break;
                    case 300: this.lock = LOSStorageLocationLockState.STORAGE;
                                        break;
                    case 301: this.lock = LOSStorageLocationLockState.RETRIEVAL;
                                        break;
                    case 302: this.lock = LOSStorageLocationLockState.CLEARING;
                                        break;
                    default: this.lock = LOSStorageLocationLockState.GENERAL;
                }
        this.aisle = aisle;
	}

	public LOSStorageLocationLockState getLock() {
		return lock;
	}

	public void setLock(LOSStorageLocationLockState lock) {
		this.lock = lock;
	}

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

	public String getAisle() {
		return aisle;
	}
	public void setAisle(String aisle) {
		this.aisle = aisle;
	}
	
	
}
