/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mywms.model.BasicEntity;

/**
 *
 * @author krane
 */
@Entity
@Table(name="los_locationcluster")
public class LOSLocationCluster extends BasicEntity {
    
	private static final long serialVersionUID = 1L;
	
	private String name;
	private Integer level = 0;
	
	@Column(unique=true, nullable=false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}

	@Override
    public String toUniqueString() {
    	return getName();
    }
    
    
}
