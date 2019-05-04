/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */

package de.linogistix.los.inventory.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.mywms.model.BasicClientAssignedEntity;

import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.model.State;

/**
 *
 * @author krane
 */
@Entity
@Table(name = "los_pickingunitload")
@NamedQueries({
	@NamedQuery(name="LOSPickingUnitLoad.queryByLabel", query="FROM LOSPickingUnitLoad ul WHERE ul.unitLoad.labelId=:label"),
	@NamedQuery(name="LOSPickingUnitLoad.queryByUnitLoad", query="FROM LOSPickingUnitLoad ul WHERE ul.unitLoad=:unitLoad"),
	@NamedQuery(name="LOSPickingUnitLoad.queryByCustomerOrderNumber", query="FROM LOSPickingUnitLoad ul WHERE ul.customerOrderNumber=:customerOrderNumber"),
	@NamedQuery(name="LOSPickingUnitLoad.queryByPickingOrder", query="FROM LOSPickingUnitLoad ul WHERE ul.pickingOrder=:pickingOrder")
})
public class LOSPickingUnitLoad extends BasicClientAssignedEntity{
	private static final long serialVersionUID = 1L;
  
	private LOSPickingOrder pickingOrder;
	private String customerOrderNumber;

	private LOSUnitLoad unitLoad;
	private int positionIndex;
	private int state = State.RAW;


	@ManyToOne(optional = false)
	public LOSPickingOrder getPickingOrder() {
		return pickingOrder;
	}
	public void setPickingOrder(LOSPickingOrder pickingOrder) {
		this.pickingOrder = pickingOrder;
	}
	
	@ManyToOne(optional=false, fetch=FetchType.EAGER)
	public LOSUnitLoad getUnitLoad() {
		return unitLoad;
	}
	public void setUnitLoad(LOSUnitLoad unitLoad) {
		this.unitLoad = unitLoad;
	}
	
	@Column(nullable = false)
	public int getPositionIndex() {
		return positionIndex;
	}
	public void setPositionIndex(int positionIndex) {
		this.positionIndex = positionIndex;
	}
	
	@Column(nullable = false)
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}

	public String getCustomerOrderNumber() {
		return customerOrderNumber;
	}
	public void setCustomerOrderNumber(String customerOrderNumber) {
		this.customerOrderNumber = customerOrderNumber;
	}

	
	
	@Override
	public String toUniqueString() {
		return unitLoad == null ? "ID-"+getId() : unitLoad.getLabelId();
	}

}
