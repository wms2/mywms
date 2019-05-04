/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.mywms.model.BasicEntity;

import de.linogistix.los.location.model.LOSLocationCluster;

/**
 * @author krane
 *
 */
@Entity
@Table(name="los_workingareapos",
uniqueConstraints={
     @UniqueConstraint(columnNames={"workingArea_id","cluster_id"})
}
)
public class LOSWorkingAreaPosition extends BasicEntity {
	private static final long serialVersionUID = 1L;


	private LOSWorkingArea workingArea;
	private LOSLocationCluster cluster;
	

	@ManyToOne(optional=false)
	public LOSWorkingArea getWorkingArea() {
		return workingArea;
	}
	public void setWorkingArea(LOSWorkingArea workingArea) {
		this.workingArea = workingArea;
	}
	
	@ManyToOne(optional=false)
	public LOSLocationCluster getCluster() {
		return cluster;
	}
	public void setCluster(LOSLocationCluster cluster) {
		this.cluster = cluster;
	}
	
	@Override
	public String toUniqueString() {
		return cluster == null ? ""+getId() : cluster.getName();
	}

}
