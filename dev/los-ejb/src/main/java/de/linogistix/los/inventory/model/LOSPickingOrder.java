/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.mywms.model.BasicClientAssignedEntity;
import org.mywms.model.User;

import de.linogistix.los.model.State;
import de.wms2.mywms.location.StorageLocation;

/**
 * 
 * @author krane
 */
@Entity
@Table(name = "los_pickingorder")
@NamedQueries({
	@NamedQuery(name="LOSPickingOrder.queryByCustomerOrder", query="SELECT distinct po FROM LOSPickingOrder po, LOSPickingPosition pp, LOSCustomerOrderPosition cp WHERE pp.pickingOrder=po AND pp.customerOrderPosition=cp AND cp.order=:customerOrder")
})
public class LOSPickingOrder extends BasicClientAssignedEntity {
	private static final long serialVersionUID = 1L;

	@Column(nullable = false, unique = true)
	private String number;

	private String customerOrderNumber;

	@Column(nullable = false)
	private int state = State.RAW;

	@Column(nullable = false)
	private int prio = 50;

	@ManyToOne(optional = true)
	private User operator;

	@Column(nullable = false)
	private boolean manualCreation;

	@OneToMany(mappedBy = "pickingOrder")
	@OrderBy("id")
	private List<LOSPickingPosition> positions;

	@OneToMany(mappedBy = "pickingOrder")
	@OrderBy("id")
	private List<LOSPickingUnitLoad> unitLoads;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private StorageLocation destination;

	@ManyToOne(optional = false)
	private LOSOrderStrategy strategy;

	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}

	
	public String getCustomerOrderNumber() {
		return customerOrderNumber;
	}
	public void setCustomerOrderNumber(String customerOrderNumber) {
		this.customerOrderNumber = customerOrderNumber;
	}
	
	
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	
	public User getOperator() {
		return operator;
	}
	public void setOperator(User operator) {
		this.operator = operator;
	}

	public List<LOSPickingPosition> getPositions() {
		return positions;
	}
	public void setPositions(List<LOSPickingPosition> positions) {
		this.positions = positions;
	}
	
	public List<LOSPickingUnitLoad> getUnitLoads() {
		return unitLoads;
	}
	public void setUnitLoads(List<LOSPickingUnitLoad> unitLoads) {
		this.unitLoads = unitLoads;
	}
	
	public int getPrio() {
		return prio;
	}
	public void setPrio(int prio) {
		this.prio = prio;
	}
	
	public boolean isManualCreation() {
		return manualCreation;
	}
	public void setManualCreation(boolean manualCreation) {
		this.manualCreation = manualCreation;
	}
	
	public StorageLocation getDestination() {
		return destination;
	}
	public void setDestination(StorageLocation destination) {
		this.destination = destination;
	}
	
	public LOSOrderStrategy getStrategy() {
		return strategy;
	}
	public void setStrategy(LOSOrderStrategy strategy) {
		this.strategy = strategy;
	}
	
	
	@Override
	public String toUniqueString() {
		return number;
	}

}
