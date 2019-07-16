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

import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.location.StorageLocation;

@Entity
@Table(name = "los_outreq")
public class LOSGoodsOutRequest extends BasicClientAssignedEntity{
	
	private static final long serialVersionUID = 1L;

	@Column(nullable = false, unique = true)
	private String number;

	private String externalNumber;

	@OneToMany(mappedBy = "goodsOutRequest")
	private List<LOSGoodsOutRequestPosition> positions = new ArrayList<LOSGoodsOutRequestPosition>();

	@Enumerated(EnumType.STRING)
	private LOSGoodsOutRequestState outState = LOSGoodsOutRequestState.RAW;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private User operator;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private DeliveryOrder customerOrder;

	@Temporal(TemporalType.TIMESTAMP)
	private Date shippingDate;

	private String courier;

	private String groupName;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private StorageLocation outLocation;

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
	
	public List<LOSGoodsOutRequestPosition> getPositions() {
		return positions;
	}
	
	public void setOutState(LOSGoodsOutRequestState outState) {
		this.outState = outState;
	}
	
	public LOSGoodsOutRequestState getOutState() {
		return outState;
	}

	public void setOperator(User operator) {
		this.operator = operator;
	}

	public User getOperator() {
		return operator;
	}

	public DeliveryOrder getCustomerOrder() {
		return customerOrder;
	}
	public void setCustomerOrder(DeliveryOrder customerOrder) {
		this.customerOrder = customerOrder;
	}
	
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

	public StorageLocation getOutLocation() {
		return outLocation;
	}

	public void setOutLocation(StorageLocation outLocation) {
		this.outLocation = outLocation;
	}

	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
}
