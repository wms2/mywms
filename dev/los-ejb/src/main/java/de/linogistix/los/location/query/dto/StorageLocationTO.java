/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.query.dto;

import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.query.BODTO;

public class StorageLocationTO  extends BODTO<LOSStorageLocation>{

	private static final long serialVersionUID = 1L;

	private String name;
	private String clientNumber;
	private String type;
	private String area;
	private String zone;
	private int lock;
	
	public StorageLocationTO(LOSStorageLocation sl){
		this(sl.getId(), sl.getVersion(), sl.getName(), sl.getClient().getNumber(), sl.getType().getName(), sl.getArea()==null?null:sl.getArea().getName(), sl.getZone()==null?null:sl.getZone().getName(), sl.getLock());
	}
	
	public StorageLocationTO(Long id, int version, String name, String client, String type, String areaName, String zoneName, int lock){
		super(id, version, name);
		this.name = name;
		this.clientNumber = client;
		this.type = type;
		this.area = areaName;
		this.zone = zoneName;
		this.lock = lock;
	}

	public int getLock() {
		return lock;
	}
	public void setLock(int lock) {
		this.lock = lock;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}

	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getClientNumber() {
		return clientNumber;
	}
	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}
	
	
	
}
