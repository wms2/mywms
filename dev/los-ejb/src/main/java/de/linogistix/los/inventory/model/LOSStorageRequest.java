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

import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.StorageLocation;

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

    @Column(nullable=false, unique=true)
	private String number;

    @ManyToOne(optional=true, fetch=FetchType.LAZY)
	private StorageLocation destination;
    
    @Enumerated(EnumType.STRING)
    private LOSStorageRequestState requestState;
    
    @ManyToOne(optional=false)
    private UnitLoad unitLoad;

	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	
    public StorageLocation getDestination() {
        return destination;
    }

    public void setDestination(StorageLocation destination) {
        this.destination = destination;
    }

    public LOSStorageRequestState getRequestState() {
        return requestState;
    }

    public void setRequestState(LOSStorageRequestState requestState) {
        this.requestState = requestState;
    }

    public UnitLoad getUnitLoad() {
        return unitLoad;
    }

    public void setUnitLoad(UnitLoad unitLoad) {
        this.unitLoad = unitLoad;
    }

    @Override
    public String toUniqueString() {
        return super.toUniqueString();
    }
    
    
   
}
