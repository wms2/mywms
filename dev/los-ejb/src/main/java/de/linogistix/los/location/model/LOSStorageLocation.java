/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mywms.model.BasicClientAssignedEntity;
import org.mywms.model.Zone;

/**
 *
 * @author Jordan
 */
@Entity
@Table(name="los_storloc")
@NamedQueries({
	@NamedQuery(name="LOSStorageLocation.queryByName", query="FROM LOSStorageLocation sl WHERE sl.name=:name")
})
public class LOSStorageLocation extends BasicClientAssignedEntity{

	private static final long serialVersionUID = 1L;

	private String name;
    
    private LOSStorageLocationType type;

    private List<LOSUnitLoad> unitLoads = new ArrayList<LOSUnitLoad>();
    
    private LOSTypeCapacityConstraint currentTypeCapacityConstraint;
    
    private LOSArea area;

    private LOSLocationCluster cluster;
    
    private Date stockTakingDate;
    
    private Zone zone = null;
    
    private BigDecimal allocation = BigDecimal.ZERO;
    
    private LOSRack rack;
    
    private int XPos;
    
    private int YPos;
    
    private int ZPos;

    private String field;
    private int fieldIndex;
    
    private String scanCode;
    private String plcCode;
    private int allocationState = 0;

    private int orderIndex = 0;

    @Column(nullable=false, unique=true)
	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(optional=false)
    public LOSStorageLocationType getType() {
        return type;
    }

    public void setType(LOSStorageLocationType type) {
        this.type = type;
    }
    
    @OneToMany(mappedBy="storageLocation")
    public List<LOSUnitLoad> getUnitLoads() {
        return unitLoads;
    }

    public void setUnitLoads(List<LOSUnitLoad> unitLoads) {
        this.unitLoads = unitLoads;
    }

    @ManyToOne(optional=true)
	@JoinColumn(name="currentTCC")
	public LOSTypeCapacityConstraint getCurrentTypeCapacityConstraint() {
		return currentTypeCapacityConstraint;
	}

	public void setCurrentTypeCapacityConstraint(
			LOSTypeCapacityConstraint currentTypeCapacityConstraint) {
		this.currentTypeCapacityConstraint = currentTypeCapacityConstraint;
	}

    @ManyToOne(optional=false)
	public LOSArea getArea() {
		return area;
	}

	public void setArea(LOSArea area) {
		this.area = area;
	}

    @Override
    public String toUniqueString() {
        return getName();
    }

	@ManyToOne(optional=true)
	public LOSLocationCluster getCluster() {
		return cluster;
	}

	public void setCluster(LOSLocationCluster cluster) {
		this.cluster = cluster;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getStockTakingDate() {
		return stockTakingDate;
	}

	public void setStockTakingDate(Date stockTakingDate) {
		this.stockTakingDate = stockTakingDate;
	}

    @ManyToOne(optional=true)
	public Zone getZone() {
		return zone;
	}
	public void setZone(Zone zone) {
		this.zone = zone;
	}
    @Column(nullable = false, precision=15, scale=2)
    public BigDecimal getAllocation() {
		return allocation;
	}
	public void setAllocation(BigDecimal allocation) {
		this.allocation = allocation;
	}

	@ManyToOne(optional=true)
    public LOSRack getRack() {
        return rack;
    }
    public void setRack(LOSRack rack) {
        this.rack = rack;
    }
    
    @Column(nullable=false)
    public int getXPos() {
        return XPos;
    }
    public void setXPos(int xPos) {
        this.XPos = xPos;
    }

    @Column(nullable=false)
    public int getYPos() {
        return YPos;
    }
    public void setYPos(int yPos) {
        this.YPos = yPos;
    }

    @Column(nullable=false)
	public int getZPos() {
		return ZPos;
	}
	public void setZPos(int pos) {
		ZPos = pos;
	}
	
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}

    @Column(nullable=false)
	public int getFieldIndex() {
		return fieldIndex;
	}
	public void setFieldIndex(int fieldIndex) {
		this.fieldIndex = fieldIndex;
	}

    @Column(nullable=false)
	public String getScanCode() {
		return scanCode;
	}
	public void setScanCode(String scanCode) {
		this.scanCode = scanCode;
	}

	public String getPlcCode() {
		return plcCode;
	}
	public void setPlcCode(String plcCode) {
		this.plcCode = plcCode;
	}

    @Column(nullable=false)
	public int getAllocationState() {
		return allocationState;
	}
	public void setAllocationState(int allocationState) {
		this.allocationState = allocationState;
	}

    @Column(nullable=false)
	public int getOrderIndex() {
		return orderIndex;
	}
	public void setOrderIndex(int orderIndex) {
		this.orderIndex = orderIndex;
	}

	@Override
	public String toShortString() {
		return super.toShortString() + "[name=" + name + "]";
	}

	@PrePersist
	@PreUpdate
	public void checkValues() {
		if( scanCode == null ) {
			scanCode = name;
		}
	}
}
