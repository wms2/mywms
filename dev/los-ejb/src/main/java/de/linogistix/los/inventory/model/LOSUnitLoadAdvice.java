/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.mywms.model.BasicClientAssignedEntity;
import org.mywms.model.UnitLoadType;

@Entity
@Table(name="los_uladvice")
public class LOSUnitLoadAdvice extends BasicClientAssignedEntity {

	private static final long serialVersionUID = 1L;

	private String number;
	
	private String externalNumber;
	
	private LOSAdvice relatedAdvice;
	
	private LOSAdviceType adviceType;
	
	private String labelId;
	
	private UnitLoadType unitLoadType;
	
	private String reasonForReturn;
	
	private LOSUnitLoadAdviceState adviceState;
	
	private String switchStateInfo;
	
	private List<LOSUnitLoadAdvicePosition> positionList = new ArrayList<LOSUnitLoadAdvicePosition>();

	@Column(unique=true, nullable=false)
	public String getNumber() {
		return number;
	}

	public void setNumber(String adviceNumber) {
		this.number = adviceNumber;
	}

	public String getExternalNumber() {
		return externalNumber;
	}

	public void setExternalNumber(String externalAdviceNumber) {
		this.externalNumber = externalAdviceNumber;
	}

	@ManyToOne(optional=true)
	public LOSAdvice getRelatedAdvice() {
		return relatedAdvice;
	}

	public void setRelatedAdvice(LOSAdvice relatedAdvice) {
		this.relatedAdvice = relatedAdvice;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	public LOSAdviceType getAdviceType() {
		return adviceType;
	}

	public void setAdviceType(LOSAdviceType adviceType) {
		this.adviceType = adviceType;
	}

	@Column(unique=true, nullable=false)
	public String getLabelId() {
		return labelId;
	}

	public void setLabelId(String labelId) {
		this.labelId = labelId;
	}

	@ManyToOne(optional=true, fetch=FetchType.LAZY)
	public UnitLoadType getUnitLoadType() {
		return unitLoadType;
	}

	public void setUnitLoadType(UnitLoadType unitLoadType) {
		this.unitLoadType = unitLoadType;
	}

	public String getReasonForReturn() {
		return reasonForReturn;
	}

	public void setReasonForReturn(String reasonForReturn) {
		this.reasonForReturn = reasonForReturn;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	public LOSUnitLoadAdviceState getAdviceState() {
		return adviceState;
	}

	public void setAdviceState(LOSUnitLoadAdviceState adviceState) {
		this.adviceState = adviceState;
	}

	public String getSwitchStateInfo() {
		return switchStateInfo;
	}

	public void setSwitchStateInfo(String switchStateInfo) {
		this.switchStateInfo = switchStateInfo;
	}

	@OneToMany(mappedBy="unitLoadAdvice")
	@OrderBy("positionNumber")
	public List<LOSUnitLoadAdvicePosition> getPositionList() {
		return positionList;
	}

	public void setPositionList(List<LOSUnitLoadAdvicePosition> positionList) {
		this.positionList = positionList;
	}
}
