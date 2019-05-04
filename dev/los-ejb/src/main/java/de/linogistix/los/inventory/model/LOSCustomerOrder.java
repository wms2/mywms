/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */

package de.linogistix.los.inventory.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mywms.model.BasicClientAssignedEntity;

import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.model.State;

/**
 * Represents an order of goods/items that should be retrieved from the warehouse.
 * 
 * 
 * @author krane
 */
@Entity
@Table(name = "los_customerorder") 
@NamedQueries({
	@NamedQuery(name="LOSCustomerOrder.queryByNumber", query="FROM LOSCustomerOrder order WHERE order.number=:number"),
	@NamedQuery(name="LOSCustomerOrder.idByNumber", query="SELECT order.id FROM LOSCustomerOrder order WHERE number=:number")
})
public class LOSCustomerOrder extends BasicClientAssignedEntity {
	private static final long serialVersionUID = 1L;
	
	public static final int PRIO_DEFAULT = 50;
	
    private String number;
    
    private String externalNumber;
	private String externalId;

	private List<LOSCustomerOrderPosition> positions;

	private LOSOrderStrategy strategy;
	
    private int state = State.RAW;

    private Date delivery;
    
    private LOSStorageLocation destination;
    
    private String documentUrl;
    
    private String labelUrl;    
    
    private String customerNumber;
    private String customerName;

    private int prio = PRIO_DEFAULT;
    
    private String dtype;
    
    @Column(insertable = false, updatable = false) 
    public String getDtype() {
		return dtype;
	}
	public void setDtype(String dtype) {
		this.dtype = dtype;
	}
	
	
	@Column(unique=true)
    public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	
	public String getExternalId() {
		return externalId;
	}
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	
    @OneToMany(mappedBy="order")
    @OrderBy("index ASC")
    public List<LOSCustomerOrderPosition> getPositions() {
        return positions;
    }
    public void setPositions(List<LOSCustomerOrderPosition> positions) {
        this.positions = positions;
    }

	@Column(nullable = false)
    public int getState() {
        return state;
    }
    public void setState(int state) {
        this.state = state;
    }

    @Temporal(TemporalType.DATE)
    public Date getDelivery() {
        return delivery;
    }
    public void setDelivery(Date delivery) {
        this.delivery = delivery;
    }

    @ManyToOne(optional=true)
    public LOSStorageLocation getDestination() {
        return destination;
    }
    public void setDestination(LOSStorageLocation destination) {
        this.destination = destination;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }
    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public String getLabelUrl() {
        return labelUrl;
    }
    public void setLabelUrl(String labelUrl) {
        this.labelUrl = labelUrl;
    }
    
	public String getCustomerNumber() {
		return customerNumber;
	}
	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}   
	
	public String getExternalNumber() {
		return externalNumber;
	}
	public void setExternalNumber(String externalNumber) {
		this.externalNumber = externalNumber;
	}

	@Column(nullable = false)
	public int getPrio() {
		return prio;
	}
	public void setPrio(int prio) {
		this.prio = prio;
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
        return getNumber();
    }

}
