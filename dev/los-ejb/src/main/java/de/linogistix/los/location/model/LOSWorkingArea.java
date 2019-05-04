/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.mywms.model.BasicEntity;

/**
 * @author krane
 *
 */
@Entity
@Table(name="los_workingarea")
public class LOSWorkingArea extends BasicEntity {
	private static final long serialVersionUID = 1L;


	private String name;
	private List<LOSWorkingAreaPosition> positionList;

	@Column(unique=true, nullable=false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
    @OneToMany(mappedBy="workingArea")
	public List<LOSWorkingAreaPosition> getPositionList() {
		return positionList;
	}
	public void setPositionList(List<LOSWorkingAreaPosition> positionList) {
		this.positionList = positionList;
	}

	@Override
	public String toUniqueString() {
		return name;
	}

}
