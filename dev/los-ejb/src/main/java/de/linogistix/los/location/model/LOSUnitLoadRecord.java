/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.mywms.model.BasicClientAssignedEntity;

@Entity
@Table(name="los_ul_record")
public class LOSUnitLoadRecord extends BasicClientAssignedEntity {

	private static final long serialVersionUID = 1L;

	@Column(nullable=false, updatable=false)
	private String label;
	
	@Column(nullable=false, updatable=false)
	private String fromLocation;
	
	@Column(nullable=false, updatable=false)
	private String toLocation;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=false, updatable=false)
	private LOSUnitLoadRecordType recordType;
	
	private String operator;
	
	private String activityCode;

	private String unitLoadType;
	
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

	public LOSUnitLoadRecordType getRecordType() {
		return recordType;
	}

	public void setRecordType(LOSUnitLoadRecordType recordType) {
		this.recordType = recordType;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getActivityCode() {
		return activityCode;
	}

	public void setActivityCode(String activityCode) {
		this.activityCode = activityCode;
	}

	public String getUnitLoadType() {
		return unitLoadType;
	}
	public void setUnitLoadType(String unitLoadType) {
		this.unitLoadType = unitLoadType;
	}
	
	
}
