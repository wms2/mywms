/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.entityservice;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.model.Client;
import org.mywms.service.BasicServiceBean;

import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.util.businessservice.ContextService;
import de.wms2.mywms.location.Area;
import de.wms2.mywms.location.AreaEntityService;
import de.wms2.mywms.location.LocationType;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.location.StorageLocationEntityService;

/**
 *
 * @author Jordan
 */
@Stateless
public class LOSStorageLocationServiceBean 
    //###TODO: Role Concept, Client check
        
        extends BasicServiceBean<StorageLocation>
        implements LOSStorageLocationService{
    
	private static final Logger log = Logger.getLogger(LOSStorageLocationServiceBean.class);
	@EJB
	private ContextService ctxService;
	@EJB
	private EntityGenerator entityGenerator;
	@Inject
	private AreaEntityService areaService;
	@Inject
	private StorageLocationEntityService locationService;
	
    public StorageLocation createStorageLocation(Client client, String name, LocationType type) {
    	return createStorageLocation(client, name, type, null);
    }

	public StorageLocation createStorageLocation(
                                Client client,
                                String name, 
                                LocationType type,
                                Area area)
    {
        
        if ((client == null || name == null) || type == null) {
            log.info("Cannot create location. Parameter == null");
            return null;
        }

        if( area == null ) {
        	area = areaService.getDefault();
        }

        client = manager.merge(client);
        type = manager.merge(type);

        StorageLocation sl = entityGenerator.generateEntity( StorageLocation.class );
        sl.setClient(client);
        sl.setName(name);
        sl.setType(type);
        sl.setArea(area);
        
        manager.persist(sl);
        log.info("CREATED LOSStorageLocation: " + sl.toShortString());
//        manager.flush();
        
        return sl;
        
    }


    public StorageLocation getByName(String name) {
        
        try{
    		Query query = manager.createNamedQuery("LOSStorageLocation.queryByName");
    		query.setParameter("name", name);

            StorageLocation sl = (StorageLocation) query.getSingleResult();
            return sl;
        }
        catch(NoResultException nre){
        }
            
        try{
            Query query = manager.createQuery("SELECT sl FROM "
                            + StorageLocation.class.getSimpleName()+" sl "
                            + "WHERE LOWER(sl.name)=:name ");

            query.setParameter("name", name);

            StorageLocation sl = (StorageLocation) query.getSingleResult();
            return sl;
        }
        catch(NoResultException nre){
        }

        try{
        	Query query = manager.createQuery("SELECT sl FROM "
                    + StorageLocation.class.getSimpleName()+" sl "
                    + "WHERE sl.scanCode=:name ");

            query.setParameter("name", name);

            StorageLocation sl = (StorageLocation) query.getSingleResult();
            return sl;
        }
        catch(NoResultException nre){
        }
        
        return null;
    }
    
  //-----------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public List<StorageLocation> getListByArea(Client client, Area area) {
		
		StringBuffer qstr = new StringBuffer();
        qstr.append("SELECT sl FROM "
                	+ StorageLocation.class.getSimpleName()+ " sl "
                	+ "WHERE sl.area=:area ");
		
        if (!client.isSystemClient()) {
            qstr.append("AND sl.client = :cl ");
        }
        
        qstr.append("ORDER BY sl.name");
        
		Query query = manager.createQuery(qstr.toString());

        query.setParameter("area", area);
        
        if (!client.isSystemClient()) {
        	query.setParameter("cl", client);
        }

        return (List<StorageLocation>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<StorageLocation> getListByRack(String rack) {
		
		StringBuffer qstr = new StringBuffer();
        qstr.append("SELECT sl FROM "
                	+ StorageLocation.class.getSimpleName()+ " sl "
                	+ "WHERE sl.rack=:rack ");
		
        qstr.append("ORDER BY sl.name");
        
		Query query = manager.createQuery(qstr.toString());

        query.setParameter("rack", rack);
        
        return (List<StorageLocation>) query.getResultList();
	}
	
	public StorageLocation getNirwana() {
		return locationService.getTrash();
	}

	public StorageLocation getClearing() {
		return locationService.getClearing();
	}
	
	public StorageLocation getCurrentUsersLocation() {
		return locationService.getCurrentUsersLocation();
	}
}
