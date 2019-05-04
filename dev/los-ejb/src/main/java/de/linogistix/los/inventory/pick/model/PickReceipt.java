/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.pick.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mywms.model.Document;

/**
 *
 * @author trautm
 */
@Entity
@Table(name = "los_pickreceipt"
//	, uniqueConstraints = {
//	    @UniqueConstraint(columnNames = {
//	            "client_id","labelID"
//	        })}
) 
public class PickReceipt extends Document{

	private static final long serialVersionUID = 1L;

	private String orderNumber;
	
	private String pickNumber;
	
	private Date date;
	
	private String labelID;
	
	private String state;

	private List<PickReceiptPosition> positions;
	
    @Override
    public String toUniqueString() {
        return getLabelID();
    }

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setPickNumber(String pickNumber) {
		this.pickNumber = pickNumber;
	}

	public String getPickNumber() {
		return pickNumber;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Temporal(TemporalType.DATE)
	public Date getDate() {
		return date;
	}

	public void setLabelID(String labelID) {
		this.labelID = labelID;
	}

	public String getLabelID() {
		return labelID;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}

	public void setPositions(List<PickReceiptPosition> positions) {
		this.positions = positions;
	}

	@OneToMany(mappedBy="receipt")
	public List<PickReceiptPosition> getPositions() {
		return positions;
	}
    
}
