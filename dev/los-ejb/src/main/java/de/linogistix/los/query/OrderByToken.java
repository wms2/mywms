/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.query;

import java.io.Serializable;

public class OrderByToken implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String attribute;
    
    private boolean ascending;

    public OrderByToken(){
    	//
    }
    		
    public OrderByToken(String attribute, boolean ascending) {
        this.setAttribute(attribute);
        this.setAscending(ascending);

    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }
}
