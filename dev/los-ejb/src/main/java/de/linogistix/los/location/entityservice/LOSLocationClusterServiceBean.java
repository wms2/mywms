/*
 * LOSLocationCluster
 *
 * Created on 2009
 *
 * Copyright (c) 2009 - 2012 LinogistiX GmbH. All rights reserved.
 *
 * <a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.los.location.entityservice;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mywms.globals.ServiceExceptionKey;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.location.model.LOSLocationCluster;
import de.linogistix.los.util.entityservice.LOSSystemPropertyService;

/**
 * @see de.linogistix.los.location.entityservice.LOSLocationClusterService
 *  
 * @author krane
 */
@Stateless 
public class LOSLocationClusterServiceBean 
	extends BasicServiceBean<LOSLocationCluster> 
	implements LOSLocationClusterService , LOSLocationClusterServiceRemote
{	
	public static final String PROPERTY_KEY_CLUSTER_DEFAULT = "CLUSTER_DEFAULT";
	
	@EJB
	private EntityGenerator entityGenerator;
	@EJB
	private LOSSystemPropertyService propertyService;
	
	public LOSLocationCluster createLocationCluster(String name) {
		LOSLocationCluster cluster = entityGenerator.generateEntity(LOSLocationCluster.class);
        cluster.setName(name);
        
        manager.persist(cluster);
        manager.flush();
        
        return cluster;
	}

	public LOSLocationCluster getByName(String name) throws EntityNotFoundException {
		String sql = "SELECT cl FROM " + LOSLocationCluster.class.getSimpleName() + " cl " +
        	"WHERE cl.name=:name";
		Query query = manager.createQuery(sql);

		query.setParameter("name", name);
		
		try {
			LOSLocationCluster cluster = (LOSLocationCluster) query.getSingleResult();
		    return cluster;
		}
		catch (NoResultException ex) {
		    throw new EntityNotFoundException(
		            ServiceExceptionKey.NO_ENTITY_WITH_NAME);
		}
	}
    
    public LOSLocationCluster getDefault() {
    	String name = propertyService.getStringDefault(PROPERTY_KEY_CLUSTER_DEFAULT, "Default");
    	
    	LOSLocationCluster cluster = null;
		try {
			cluster = getByName(name);
		} catch (EntityNotFoundException e) {}
		
    	if( cluster == null ) {
    	    cluster = createLocationCluster(name);
    	}
		
    	return cluster;
    }
}
