/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.mywms.model.BasicEntity;
import org.mywms.model.UnitLoadType;

/**
 * A LOSStorageLocationType may be restricted to certain UnitLoadTypes.
 * If a LOSStorageLocationType has no LOSTypeCapacityConstraint, 
 * it can store every kind of UnitLoad with an unbounded capacity.
 * Different kind of rules can be handled with different types:
 * - UNIT_LOAD_TYPE: store identically unit load type on one locations. allocation = percentage one unit load needs on the location
 * - PERCENTAGE: store different unit load types on one location. allocation = percentage one unit load needs on the location
 * 	The allocation of the location is calculated with the portion that is required.
 * 
 * @author Jordan
 */
@Entity
@Table(name="los_typecapacityconstraint",
       uniqueConstraints={
            @UniqueConstraint(columnNames={"storagelocationtype_id","unitloadtype_id"})
       }
)
@NamedQueries({
	@NamedQuery(name="LOSTypeCapacityConstraint.queryBySLTypeAndULType", query="FROM LOSTypeCapacityConstraint tcc WHERE tcc.storageLocationType=:SLType AND tcc.unitLoadType=:ULType")
})
public class LOSTypeCapacityConstraint extends BasicEntity{
	private static final long serialVersionUID = 1L;

	public final static int ALLOCATE_UNIT_LOAD_TYPE = 1;
	public final static int ALLOCATE_PERCENTAGE = 2;

    private LOSStorageLocationType storageLocationType;
    
    private UnitLoadType unitLoadType = null;
    
    private BigDecimal allocation = BigDecimal.valueOf(100);
    
    private int allocationType = ALLOCATE_UNIT_LOAD_TYPE;

    private int orderIndex = 0;

    @ManyToOne(optional=false)
    public LOSStorageLocationType getStorageLocationType() {
        return storageLocationType;
    }

    public void setStorageLocationType(LOSStorageLocationType storageLocationType) {
        this.storageLocationType = storageLocationType;
    }

    @ManyToOne(optional=false)
    public UnitLoadType getUnitLoadType() {
        return unitLoadType;
    }

    public void setUnitLoadType(UnitLoadType unitLoadType) {
        this.unitLoadType = unitLoadType;
    }

    @Column(nullable = false, precision=15, scale=2)
	public BigDecimal getAllocation() {
		return allocation;
	}

	public void setAllocation(BigDecimal allocation) {
		this.allocation = allocation;
	}

	@Column(nullable=false)
	public int getAllocationType() {
		return allocationType;
	}
	public void setAllocationType(int allocationType) {
		this.allocationType = allocationType;
	}

	@Column(nullable=false)
    public int getOrderIndex() {
		return orderIndex;
	}
	public void setOrderIndex(int orderIndex) {
		this.orderIndex = orderIndex;
	}

	@Override
    public String toUniqueString() {
        return unitLoadType == null ? ""+getId() : unitLoadType.getName();
    }
}
