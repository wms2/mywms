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

import de.wms2.mywms.location.LocationCluster;

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


	@ManyToOne(optional=false)
	private LOSWorkingArea workingArea;

	@ManyToOne(optional=false)
	private LocationCluster cluster;

	public LOSWorkingArea getWorkingArea() {
		return workingArea;
	}
	public void setWorkingArea(LOSWorkingArea workingArea) {
		this.workingArea = workingArea;
	}
	
	public LocationCluster getCluster() {
		return cluster;
	}
	public void setCluster(LocationCluster cluster) {
		this.cluster = cluster;
	}
	
	@Override
	public String toUniqueString() {
		return cluster == null ? ""+getId() : cluster.getName();
	}

}
