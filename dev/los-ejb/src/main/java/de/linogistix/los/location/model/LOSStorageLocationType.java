/*
 * StorageLocationType.java
 *
 * Created on November 19, 2007, 10:52 AM
 *
 * Copyright (c) 2007-2012 LinogistiX GmbH. All rights reserved.
 *
 * <a href="http://www.linogistix.com/">browse for licence information</a>
 */

package de.linogistix.los.location.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.mywms.model.BasicEntity;

/**
 * @author Jordan
 */
@Entity
@Table(name="los_storagelocationtype",
       uniqueConstraints={@UniqueConstraint(columnNames={"sltname"})})
public class LOSStorageLocationType extends BasicEntity{
    
	private static final long serialVersionUID = 1L;
	
    private String name;
    
    private BigDecimal height;
    private BigDecimal width;
    private BigDecimal depth;
    private BigDecimal volume;
    private BigDecimal liftingCapacity;
    
    private int handlingFlag = 0;

    
    @Column(name="sltname", nullable=false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
    @Column(nullable = true, precision=15, scale=2)
    public BigDecimal getHeight() {
		return height;
	}
	public void setHeight(BigDecimal height) {
		this.height = height;
	}

    @Column(nullable = true, precision=15, scale=2)
	public BigDecimal getWidth() {
		return width;
	}
	public void setWidth(BigDecimal width) {
		this.width = width;
	}

    @Column(nullable = true, precision=15, scale=2)
	public BigDecimal getDepth() {
		return depth;
	}
	public void setDepth(BigDecimal depth) {
		this.depth = depth;
	}

    @Column(nullable = true, precision=19, scale=6)
    public BigDecimal getVolume() {
		return volume;
	}
	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	@Column(nullable = true, precision=16, scale=3)
	public BigDecimal getLiftingCapacity() {
		return liftingCapacity;
	}
	public void setLiftingCapacity(BigDecimal liftingCapacity) {
		this.liftingCapacity = liftingCapacity;
	}

	@Column(nullable = false)
	public int getHandlingFlag() {
		return handlingFlag;
	}
	public void setHandlingFlag(int handlingFlag) {
		this.handlingFlag = handlingFlag;
	}

	@Override
    public String toUniqueString() {
    	return getName();
    }
    
}
