/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.mywms.model.BasicClientAssignedEntity;

import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;

/**
 *
 * @author trautm
 */

@Entity
@Table(name="los_storagereq")
@NamedQueries({
	@NamedQuery(name="LOSStorageRequest.queryActiveByDestination", query="FROM LOSStorageRequest req WHERE req.destination=:destination AND req.requestState in (:stateRaw,:stateProcessing)")
})
public class LOSStorageRequest extends BasicClientAssignedEntity{

	private static final long serialVersionUID = 1L;

	private String number;

	private LOSStorageLocation destination;
    
    private LOSStorageRequestState requestState;
    
    private LOSUnitLoad unitLoad;

    @Column(nullable=false, unique=true)
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	
    @ManyToOne(optional=true, fetch=FetchType.LAZY)
    public LOSStorageLocation getDestination() {
        return destination;
    }

    public void setDestination(LOSStorageLocation destination) {
        this.destination = destination;
    }

    @Enumerated(EnumType.STRING)
    public LOSStorageRequestState getRequestState() {
        return requestState;
    }

    public void setRequestState(LOSStorageRequestState requestState) {
        this.requestState = requestState;
    }

    @ManyToOne(optional=false)
    public LOSUnitLoad getUnitLoad() {
        return unitLoad;
    }

    public void setUnitLoad(LOSUnitLoad unitLoad) {
        this.unitLoad = unitLoad;
    }

    @Override
    public String toUniqueString() {
        return super.toUniqueString();
    }
    
    
   
}
