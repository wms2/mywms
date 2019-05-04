/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.query.dto;

import de.linogistix.los.location.model.LOSWorkingAreaPosition;
import de.linogistix.los.query.BODTO;

/**
 * @author krane
 *
 */
public class LOSWorkingAreaPositionTO extends BODTO<LOSWorkingAreaPosition>{

	private static final long serialVersionUID = 1L;

	public String name;
	public String workingAreaName;
	public String clusterName;
	
	public LOSWorkingAreaPositionTO(LOSWorkingAreaPosition wap){
		super(wap.getId(), wap.getVersion(), wap.getId());
		this.workingAreaName = wap.getWorkingArea() == null ? "" : wap.getWorkingArea().getName();
		this.clusterName = wap.getCluster() == null ? "" : wap.getCluster().getName();
		this.name = workingAreaName+" \u21d4 "+clusterName;
	}
	
	public LOSWorkingAreaPositionTO(Long id, int version, String workingAreaName, String clusterName){
		super(id, version, id);
		this.workingAreaName = workingAreaName;
		this.clusterName = clusterName;
		this.name = workingAreaName+" \u21d4 "+clusterName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWorkingAreaName() {
		return workingAreaName;
	}

	public void setWorkingAreaName(String workingAreaName) {
		this.workingAreaName = workingAreaName;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	
}
