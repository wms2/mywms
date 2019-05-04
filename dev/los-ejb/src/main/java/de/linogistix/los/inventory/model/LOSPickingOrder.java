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

import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.model.State;

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
	
    private String number;
    private String customerOrderNumber;
	private int state = State.RAW;
	private int prio = 50;
    private User operator;
    private boolean manualCreation;
    private List<LOSPickingPosition> positions;
    private List<LOSPickingUnitLoad> unitLoads;
    private LOSStorageLocation destination;
	private LOSOrderStrategy strategy;

    
    @Column(nullable = false, unique = true)
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
	
	
    @Column(nullable = false)
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	
	@ManyToOne(optional=true)
	public User getOperator() {
		return operator;
	}
	public void setOperator(User operator) {
		this.operator = operator;
	}

	@OneToMany(mappedBy="pickingOrder")
	@OrderBy("id")
	public List<LOSPickingPosition> getPositions() {
		return positions;
	}
	public void setPositions(List<LOSPickingPosition> positions) {
		this.positions = positions;
	}
	
	@OneToMany(mappedBy="pickingOrder")
	@OrderBy("id")
	public List<LOSPickingUnitLoad> getUnitLoads() {
		return unitLoads;
	}
	public void setUnitLoads(List<LOSPickingUnitLoad> unitLoads) {
		this.unitLoads = unitLoads;
	}
	
	@Column(nullable = false)
	public int getPrio() {
		return prio;
	}
	public void setPrio(int prio) {
		this.prio = prio;
	}
	
	@Column(nullable = false)
	public boolean isManualCreation() {
		return manualCreation;
	}
	public void setManualCreation(boolean manualCreation) {
		this.manualCreation = manualCreation;
	}
	
	@ManyToOne(optional=true, fetch=FetchType.LAZY)
	public LOSStorageLocation getDestination() {
		return destination;
	}
	public void setDestination(LOSStorageLocation destination) {
		this.destination = destination;
	}
	
	@ManyToOne(optional=false)
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
