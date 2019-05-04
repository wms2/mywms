/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.entityservice;

import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.model.Client;
import org.mywms.model.User;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.ClientService;

import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.location.model.LOSArea;
import de.linogistix.los.location.model.LOSRack;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSStorageLocationType;
import de.linogistix.los.location.res.BundleResolver;
import de.linogistix.los.util.BundleHelper;
import de.linogistix.los.util.businessservice.ContextService;

/**
 *
 * @author Jordan
 */
@Stateless
public class LOSStorageLocationServiceBean 
    //###TODO: Role Concept, Client check
        
        extends BasicServiceBean<LOSStorageLocation>
        implements LOSStorageLocationService{
    
	private static final Logger log = Logger.getLogger(LOSStorageLocationServiceBean.class);
	@EJB
	private ClientService clService;
	@EJB
	private LOSStorageLocationTypeService slTypeService;
	@EJB
	private ContextService ctxService;
	@EJB
	private EntityGenerator entityGenerator;
	@EJB
	private LOSAreaService areaService;
	
    public LOSStorageLocation createStorageLocation(Client client, String name, LOSStorageLocationType type) {
    	return createStorageLocation(client, name, type, null);
    }

	public LOSStorageLocation createStorageLocation(
                                Client client,
                                String name, 
                                LOSStorageLocationType type,
                                LOSArea area)
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

        LOSStorageLocation sl = entityGenerator.generateEntity( LOSStorageLocation.class );
        sl.setClient(client);
        sl.setName(name);
        sl.setType(type);
        sl.setArea(area);
        
        manager.persist(sl);
        log.info("CREATED LOSStorageLocation: " + sl.toShortString());
//        manager.flush();
        
        return sl;
        
    }


    public LOSStorageLocation getByName(String name) {
        
        try{
    		Query query = manager.createNamedQuery("LOSStorageLocation.queryByName");
    		query.setParameter("name", name);

            LOSStorageLocation sl = (LOSStorageLocation) query.getSingleResult();
            return sl;
        }
        catch(NoResultException nre){
        }
            
        try{
            Query query = manager.createQuery("SELECT sl FROM "
                            + LOSStorageLocation.class.getSimpleName()+" sl "
                            + "WHERE LOWER(sl.name)=:name ");

            query.setParameter("name", name);

            LOSStorageLocation sl = (LOSStorageLocation) query.getSingleResult();
            return sl;
        }
        catch(NoResultException nre){
        }

        try{
        	Query query = manager.createQuery("SELECT sl FROM "
                    + LOSStorageLocation.class.getSimpleName()+" sl "
                    + "WHERE sl.scanCode=:name ");

            query.setParameter("name", name);

            LOSStorageLocation sl = (LOSStorageLocation) query.getSingleResult();
            return sl;
        }
        catch(NoResultException nre){
        }
        
        return null;
    }
    
  //-----------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public List<LOSStorageLocation> getListByArea(Client client, LOSArea area) {
		
		StringBuffer qstr = new StringBuffer();
        qstr.append("SELECT sl FROM "
                	+ LOSStorageLocation.class.getSimpleName()+ " sl "
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

        return (List<LOSStorageLocation>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<LOSStorageLocation> getListByRack(LOSRack rack) {
		
		StringBuffer qstr = new StringBuffer();
        qstr.append("SELECT sl FROM "
                	+ LOSStorageLocation.class.getSimpleName()+ " sl "
                	+ "WHERE sl.rack=:rack ");
		
        qstr.append("ORDER BY sl.name");
        
		Query query = manager.createQuery(qstr.toString());

        query.setParameter("rack", rack);
        
        return (List<LOSStorageLocation>) query.getResultList();
	}
	
	public LOSStorageLocation getNirwana() {
		LOSStorageLocation nirwana;
		nirwana = manager.find(LOSStorageLocation.class, 0L);
		if (nirwana != null){
			return nirwana;
		}
		
		createSystemStorageLocation(0, "SYSTEM_DATA_NIRWANA_LOCATION");

		nirwana = manager.find(LOSStorageLocation.class, 0L);
		if (nirwana != null){
			return nirwana;
		} else{
			throw new RuntimeException("Nirwana not found");
		}
		
	}

	public LOSStorageLocation getClearing() {
		LOSStorageLocation clearing;		
			 
		clearing = manager.find(LOSStorageLocation.class, 1L);
		if (clearing != null){
			return clearing;
		} 
		
		createSystemStorageLocation(1, "SYSTEM_DATA_CLEARING_LOCATION");
		
		clearing = manager.find(LOSStorageLocation.class, 1L);
		if (clearing != null){
			return clearing;
		} 
		else{
			throw new RuntimeException("Clearing not found");
		}
	}
	
	private void createSystemStorageLocation(long id, String nameKey) {
		log.warn("Try to create system Location");
		
		User user = ctxService.getCallersUser();
		Locale locale = null;
		if( user != null ) {
			try {
				locale = new Locale(user.getLocale());
			}
			catch( Throwable x ) {
				// egal
			}
		}
		if( locale == null ) {
			locale = Locale.getDefault();
		}
		
		
		String name = BundleHelper.resolve(BundleResolver.class, nameKey, locale );
		
		LOSStorageLocation sl;
		sl = getByName(name);
		if(sl == null) {
			sl = createStorageLocation(clService.getSystemClient(), name, slTypeService.getNoRestrictionType(), null);
			if( sl == null ) {
				log.error("Cannot create system Location");
				return;
			}
			String comment = BundleHelper.resolve(BundleResolver.class, "SYSTEM_DATA_COMMENT", locale );
			sl.setAdditionalContent(comment);
		}
		manager.flush();
		
		String queryStr = "UPDATE " + LOSStorageLocation.class.getSimpleName() + " SET id=:idNew WHERE id=:idOld";
		Query query = manager.createQuery(queryStr);
		query.setParameter("idNew", id);
		query.setParameter("idOld", sl.getId());
		query.executeUpdate();
		manager.flush();
		sl = null;
	}
	
	public LOSStorageLocation getCurrentUsersLocation() {
		String callersName = ctxService.getCallerUserName();
		LOSStorageLocation loc = getByName(callersName);
		if( loc == null ) {
			LOSStorageLocationType locType = slTypeService.getNoRestrictionType(); 
			loc = createStorageLocation(ctxService.getCallersClient(), callersName, locType, null);
		}
		return loc;
	}
}
