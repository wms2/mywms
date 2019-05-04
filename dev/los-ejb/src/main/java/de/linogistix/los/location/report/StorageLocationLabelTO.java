/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.report;

import java.io.Serializable;

/**
 *
 * @author trautm
 */
public class StorageLocationLabelTO implements Serializable{
    
	private static final long serialVersionUID = 1L;

	private int offset;
    
    private String name;
    
    public StorageLocationLabelTO(String name, int offset){
        this.name = name;
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
