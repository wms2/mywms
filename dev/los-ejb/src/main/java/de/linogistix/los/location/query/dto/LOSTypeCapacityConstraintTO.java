/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.query.dto;

import java.math.BigDecimal;

import de.linogistix.los.query.BODTO;
import de.wms2.mywms.strategy.TypeCapacityConstraint;

/**
 * @author krane
 *
 */
public class LOSTypeCapacityConstraintTO extends BODTO<TypeCapacityConstraint>{

	private static final long serialVersionUID = 1L;

	private String name;
	private String locationTypeName;
	private String unitLoadTypeName;
    private BigDecimal allocation = BigDecimal.valueOf(100);

	public LOSTypeCapacityConstraintTO(TypeCapacityConstraint constraint){
		super(constraint.getId(), constraint.getVersion(), constraint.getId());
		this.locationTypeName = constraint.getLocationType() == null ? "" : constraint.getLocationType().getName();
		this.unitLoadTypeName = constraint.getUnitLoadType() == null ? "" : constraint.getUnitLoadType().getName();
		this.allocation = constraint.getAllocation();
		this.name = locationTypeName+" \u21d4 "+unitLoadTypeName;
	}
	
	public LOSTypeCapacityConstraintTO(Long id, int version, String locationTypeName, String unitLoadTypeName, BigDecimal allocation){
		super(id, version, id);
		this.locationTypeName = locationTypeName;
		this.unitLoadTypeName = unitLoadTypeName;
		this.allocation = allocation;
		this.name = locationTypeName+" \u21d4 "+unitLoadTypeName;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getLocationTypeName() {
		return locationTypeName;
	}

	public void setLocationTypeName(String locationTypeName) {
		this.locationTypeName = locationTypeName;
	}

	public String getUnitLoadTypeName() {
		return unitLoadTypeName;
	}

	public void setUnitLoadTypeName(String unitLoadTypeName) {
		this.unitLoadTypeName = unitLoadTypeName;
	}

	public BigDecimal getAllocation() {
		return allocation;
	}

	public void setAllocation(BigDecimal allocation) {
		this.allocation = allocation;
	}

	
}
