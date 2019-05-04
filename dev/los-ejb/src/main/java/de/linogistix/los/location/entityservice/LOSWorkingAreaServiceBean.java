/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.entityservice;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mywms.service.BasicServiceBean;

import de.linogistix.los.location.model.LOSLocationCluster;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSWorkingArea;
import de.linogistix.los.location.model.LOSWorkingAreaPosition;


/**
 * @author krane
 *
 */
@Stateless 
public class LOSWorkingAreaServiceBean extends BasicServiceBean<LOSWorkingArea> implements LOSWorkingAreaService {	
	
	
	public LOSWorkingArea getByName(String name) {
		String queryStr = "SELECT o FROM "+LOSWorkingArea.class.getSimpleName() + " o WHERE o.name=:name"; 

        Query query = manager.createQuery(queryStr);

        query.setParameter("name", name);

        try {
        	LOSWorkingArea request = (LOSWorkingArea)query.getSingleResult();
            return request;
        }
        catch (Exception ex) { }
        
 		return null;
 	}
	
	public boolean containsLocation(LOSWorkingArea area, LOSStorageLocation location) {
        LOSLocationCluster cluster = location.getCluster();
        if( cluster == null ) {
        	return false;
        }
        
		String queryStr = "SELECT o.id FROM "+LOSWorkingAreaPosition.class.getSimpleName() + " o WHERE o.workingArea=:area and o.cluster=:cluster"; 
        Query query = manager.createQuery(queryStr);

        query.setParameter("area", area);
        query.setParameter("cluster", cluster);
        query.setMaxResults(1);
        try {
            query.getSingleResult();
        } catch (NoResultException nre) {
            return false;
        }
        return true;
	}

    
}
