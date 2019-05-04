/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.los.query;

import java.io.Serializable;
import org.mywms.model.BasicEntity;

/**
 * Prototype for transfer objects for BasicEntities.
 *
 * @author trautm
 */
public class BODTO<T extends BasicEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    /**database id for the referenced Entity*/
    private Long id;
    /**A unique Name*/
    private String name;
    /**entity version in database */
    private int version;
    /**entity classname*/
    private String className;
    
    public BODTO(){
    	//
    }
    
    public BODTO(T entity) {
        this.id = entity.getId();
        this.version = entity.getVersion();
        this.name = entity.toUniqueString();
    }
    
    public BODTO(Long id, int version, String name) {
        this.id = id;
        this.version = version;
        this.name = name;
    }

    public BODTO(Long id, int version, Long uniqueId) {
        this.id = id;
        this.version = version;
        this.name = Long.toString(uniqueId);
    }

    /**database id for the referenced Entity
     */
    public Long getId() {
        return id;
    }

    /**A unique Name*/
    public String getName() {
        return name;
    }

    /**entity version in database */
    public int getVersion() {
        return version;
    }

    /**entity classname*/
    public String getClassName(){
        return className;
    }
    
    /**entity classname*/
    public void setClassName(String className){
        this.className = className;
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public boolean equals(Object obj) {
    	BODTO<BasicEntity> to;
    	
    	if (obj == null) return false;
    	
    	if (obj == this) return true;
    	
    	if (obj instanceof BODTO ) 
    		to = (BODTO<BasicEntity>) obj;
    	else 
    		return false;
    	
    	//return to.id == this.id; 
//    	return this.id.equals(to.id) && this.version == to.version;
    	
    	// 08.12.2012, krane. Avoid NullPointerExceptions
    	if( this.id==null || to.id==null ) {
    		return false;
    	}
    	return this.id.equals(to.id) && this.version == to.version;
    	
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : Long.valueOf(id+version).hashCode();
    }

}
