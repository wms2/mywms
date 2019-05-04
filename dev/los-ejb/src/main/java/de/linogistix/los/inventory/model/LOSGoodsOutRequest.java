/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mywms.model.BasicClientAssignedEntity;
import org.mywms.model.User;

import de.linogistix.los.location.model.LOSStorageLocation;

@Entity
@Table(name = "los_outreq")
public class LOSGoodsOutRequest extends BasicClientAssignedEntity{
	
	private static final long serialVersionUID = 1L;

	private String number;
	private String externalNumber;

	private List<LOSGoodsOutRequestPosition> positions = new ArrayList<LOSGoodsOutRequestPosition>();
	
	private LOSGoodsOutRequestState outState = LOSGoodsOutRequestState.RAW;

	private User operator;
	
	private LOSCustomerOrder customerOrder;

	private Date shippingDate;
	
	private String courier;
	
	private String groupName;

	private LOSStorageLocation outLocation;

	
    @Column(nullable=false, unique=true)
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}

	public String getExternalNumber() {
		return externalNumber;
	}
	public void setExternalNumber(String externalNumber) {
		this.externalNumber = externalNumber;
	}
	
	public void setPositions(List<LOSGoodsOutRequestPosition> positions) {
		this.positions = positions;
	}
	
	@OneToMany(mappedBy="goodsOutRequest")
	public List<LOSGoodsOutRequestPosition> getPositions() {
		return positions;
	}
	
	public void setOutState(LOSGoodsOutRequestState outState) {
		this.outState = outState;
	}
	
	@Enumerated(EnumType.STRING)
	public LOSGoodsOutRequestState getOutState() {
		return outState;
	}

	public void setOperator(User operator) {
		this.operator = operator;
	}

    @ManyToOne(optional=true, fetch=FetchType.LAZY)
	public User getOperator() {
		return operator;
	}

	@ManyToOne(optional=true, fetch=FetchType.LAZY)
	public LOSCustomerOrder getCustomerOrder() {
		return customerOrder;
	}
	public void setCustomerOrder(LOSCustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getShippingDate() {
		return shippingDate;
	}

	public void setShippingDate(Date shippingDate) {
		this.shippingDate = shippingDate;
	}

	public String getCourier() {
		return courier;
	}

	public void setCourier(String courier) {
		this.courier = courier;
	}

	@ManyToOne(optional=true, fetch=FetchType.LAZY)
	public LOSStorageLocation getOutLocation() {
		return outLocation;
	}

	public void setOutLocation(LOSStorageLocation outLocation) {
		this.outLocation = outLocation;
	}

	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
}
