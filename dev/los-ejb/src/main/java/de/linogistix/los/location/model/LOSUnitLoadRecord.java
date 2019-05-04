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

	private String label;
	
	private String fromLocation;
	
	private String toLocation;
	
	private LOSUnitLoadRecordType recordType;
	
	private String operator;
	
	private String activityCode;

	private String unitLoadType;
	
	@Column(nullable=false, updatable=false)
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Column(nullable=false, updatable=false)
	public String getFromLocation() {
		return fromLocation;
	}

	public void setFromLocation(String fromLocation) {
		this.fromLocation = fromLocation;
	}

	@Column(nullable=false, updatable=false)
	public String getToLocation() {
		return toLocation;
	}

	public void setToLocation(String toLocation) {
		this.toLocation = toLocation;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable=false, updatable=false)
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
