/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.query.dto;

import de.linogistix.los.query.BODTO;
import de.wms2.mywms.inventory.UnitLoad;

public class UnitLoadTO extends BODTO<UnitLoad> {

	private static final long serialVersionUID = 1L;
	
	private String storageLocation;
	private int lock;
	private String clientNumber;
	private Long numChildUnitLoads = null;
	private boolean carrier;
	private String typeName;
	
	public UnitLoadTO(UnitLoad ul){
		this(ul.getId(), ul.getVersion(), ul.getLabelId(), ul.getClient().getNumber(), ul.getLock(), ul.getStorageLocation().getName(), ul.isCarrier(), ul.getType().getName());
	}

	public UnitLoadTO(Long id, int version, String name, String clientNumber, int lock, String storageLocation, boolean carrier, String typeName){
		super(id, version, name);
		this.storageLocation = storageLocation;
		this.lock = lock;
		this.carrier = carrier;
		this.clientNumber = clientNumber;
		this.typeName=typeName;
	}

	public String getStorageLocation() {
		return storageLocation;
	}
	public void setStorageLocation(String storageLocation) {
		this.storageLocation = storageLocation;
	}

	public int getLock() {
		return lock;
	}
	public void setLock(int lock) {
		this.lock = lock;
	}

	public String getClientNumber() {
		return clientNumber;
	}
	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	public boolean isCarrier() {
		return carrier;
	}

	public void setCarrier(boolean carrier) {
		this.carrier = carrier;
	}

	public Long getNumChildUnitLoads() {
		return numChildUnitLoads;
	}
	public void setNumChildUnitLoads(Long numChildUnitLoads) {
		this.numChildUnitLoads = numChildUnitLoads;
	}

	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
}
