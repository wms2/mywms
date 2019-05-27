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

import de.linogistix.los.model.State;
import de.wms2.mywms.inventory.UnitLoad;

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
  
	@ManyToOne(optional = false)
	private LOSPickingOrder pickingOrder;
	private String customerOrderNumber;

	@ManyToOne(optional=false, fetch=FetchType.EAGER)
	private UnitLoad unitLoad;
	@Column(nullable = false)
	private int positionIndex;
	@Column(nullable = false)
	private int state = State.RAW;


	public LOSPickingOrder getPickingOrder() {
		return pickingOrder;
	}
	public void setPickingOrder(LOSPickingOrder pickingOrder) {
		this.pickingOrder = pickingOrder;
	}
	
	public UnitLoad getUnitLoad() {
		return unitLoad;
	}
	public void setUnitLoad(UnitLoad unitLoad) {
		this.unitLoad = unitLoad;
	}
	
	public int getPositionIndex() {
		return positionIndex;
	}
	public void setPositionIndex(int positionIndex) {
		this.positionIndex = positionIndex;
	}
	
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
