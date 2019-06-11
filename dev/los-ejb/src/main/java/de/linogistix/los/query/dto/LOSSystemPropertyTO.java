/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.query.dto;

import de.linogistix.los.query.BODTO;
import de.wms2.mywms.property.SystemProperty;

public class LOSSystemPropertyTO extends BODTO<SystemProperty> {

	private static final long serialVersionUID = 1L;
    
	private String key;
	private String clientNumber;
	private String groupName;
	private String workstation;
	private String value;
	

	public LOSSystemPropertyTO(SystemProperty x){
		this(x.getId(), x.getVersion(), x.getPropertyGroup(), x.getPropertyKey(), x.getClient().getNumber(), x.getPropertyContext(), x.getPropertyValue());
	}
	
	public LOSSystemPropertyTO(Long id, int version, String groupName, String key, String clientNumber, String workstation, String value){
		super(id, version, key);
		this.groupName = groupName;
		this.key = key;
		this.clientNumber = clientNumber;
		this.workstation = workstation;
		this.value = value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getWorkstation() {
		return workstation;
	}

	public void setWorkstation(String workstation) {
		this.workstation = workstation;
	}

	
}
