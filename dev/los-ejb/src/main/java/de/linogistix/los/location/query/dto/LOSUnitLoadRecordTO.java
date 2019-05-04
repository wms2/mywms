/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.query.dto;

import java.util.Date;

import de.linogistix.los.location.model.LOSUnitLoadRecord;
import de.linogistix.los.location.model.LOSUnitLoadRecordType;
import de.linogistix.los.query.BODTO;

public class LOSUnitLoadRecordTO extends BODTO<LOSUnitLoadRecord> {

	private static final long serialVersionUID = 1L;
	
	public String label;
	
	public String fromLocation;
	
	public String toLocation;
	
	public Date recordDate;
	
	public String activityCode;

	public String type;
	
	private String unitLoadType;

	public LOSUnitLoadRecordTO(LOSUnitLoadRecord x){
		this(x.getId(), x.getVersion(), x.getLabel(), x.getFromLocation(), x.getToLocation(), x.getCreated(), x.getActivityCode(), x.getRecordType(), x.getUnitLoadType());
	}
	
	public LOSUnitLoadRecordTO(Long id, int version, String label, String fromLocation, String toLocation, Date recordDate, String activityCode, LOSUnitLoadRecordType type){
		super(id, version, label);
		this.fromLocation = fromLocation;
		this.toLocation = toLocation;
		this.recordDate = recordDate;
		this.type = type.name();
	}
	
	public LOSUnitLoadRecordTO(Long id, int version, String label, String fromLocation, String toLocation, Date recordDate, String activityCode, LOSUnitLoadRecordType type, String unitLoadType){
		super(id, version, label);
		this.fromLocation = fromLocation;
		this.toLocation = toLocation;
		this.recordDate = recordDate;
		this.type = type.name();
		this.unitLoadType = unitLoadType;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getFromLocation() {
		return fromLocation;
	}

	public void setFromLocation(String fromLocation) {
		this.fromLocation = fromLocation;
	}

	public String getToLocation() {
		return toLocation;
	}

	public void setToLocation(String toLocation) {
		this.toLocation = toLocation;
	}

	public Date getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(Date recordDate) {
		this.recordDate = recordDate;
	}

	public String getActivityCode() {
		return activityCode;
	}

	public void setActivityCode(String activityCode) {
		this.activityCode = activityCode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUnitLoadType() {
		return unitLoadType;
	}

	public void setUnitLoadType(String unitLoadType) {
		this.unitLoadType = unitLoadType;
	}

}
