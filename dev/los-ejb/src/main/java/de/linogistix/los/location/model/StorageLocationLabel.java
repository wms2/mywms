/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.mywms.model.Document;

/**
 *
 * @author trautm
 */
@Entity
@Table(name = "los_slLabel"
//	,uniqueConstraints = {
//    @UniqueConstraint(columnNames = {
//        "client_id","labelId"
//    })}
) 
public class StorageLocationLabel extends Document{

	private static final long serialVersionUID = 1L;
	
	private String labelId;

    public String getLabelId() {
        return labelId;
    }

    public void setLabelId(String labelId) {
        this.labelId = labelId;
    }
    
}
