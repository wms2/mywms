/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package de.linogistix.los.location.entityservice;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.Client;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.ClientService;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.util.entityservice.LOSSystemPropertyService;
import de.wms2.mywms.location.Area;
import de.wms2.mywms.location.AreaUsages;

/**
 *  
 * @author Markus Jordan
 * @version $Revision: 339 $ provided by $Author: trautmann $
 */
@Stateless 
public class LOSAreaServiceBean extends BasicServiceBean<Area> implements LOSAreaService, LOSAreaServiceRemote {	
	
	
	public final static String PROPERTY_KEY_AREA_DEFAULT = "AREA_DEFAULT";

	
	@EJB
	private LOSSystemPropertyService propertyService;
	@EJB
	private ClientService clientService;
	@EJB
	private EntityGenerator entityGenerator;
	
    public Area createLOSArea(Client c, String name) {
        Area a = entityGenerator.generateEntity(Area.class);
        a.setName(name);
                
        manager.persist(a);
        manager.flush();
        
        return a;
    }

	public Area getByName(Client client, String name) throws EntityNotFoundException {
		String queryStr = "SELECT o FROM " + Area.class.getSimpleName() + " o " + "WHERE o.name=:name";
		Query query = manager.createQuery(queryStr);

		query.setParameter("name", name);

		try {
			Area area = (Area) query.getSingleResult();
			return area;
		} catch (NoResultException ex) {
			throw new EntityNotFoundException(ServiceExceptionKey.NO_AREA_WITH_NAME);
		}
	}
	
    @SuppressWarnings("unchecked")
	public List<Area> getForGoodsIn( ) {
		String queryStr = 
				"SELECT o FROM " + Area.class.getSimpleName() + " o " +
                "WHERE o.usages like '%"+AreaUsages.GOODS_IN + "%'" +
				"ORDER BY o.id ";
		Query query = manager.createQuery(queryStr);
		
		return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<Area> getForGoodsOut( ) {
		String queryStr = 
				"SELECT o FROM " + Area.class.getSimpleName() + " o " +
                "WHERE o.usages like '%"+AreaUsages.GOODS_OUT + "%'" +
				"ORDER BY o.id ";
		Query query = manager.createQuery(queryStr);
		
		return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Area> getForStorage( ) {
		String queryStr = 
				"SELECT o FROM " + Area.class.getSimpleName() + " o " +
                "WHERE o.usages like '%"+AreaUsages.STORAGE + "%'" +
				"ORDER BY o.id ";
		Query query = manager.createQuery(queryStr);
		
		return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Area> getForPicking( ) {
		String queryStr = 
				"SELECT o FROM " + Area.class.getSimpleName() + " o " +
                "WHERE o.usages like '%"+AreaUsages.PICKING + "%'" +
				"ORDER BY o.id ";
		Query query = manager.createQuery(queryStr);
		
		return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Area> getForTransfer() {
		String queryStr = 
				"SELECT o FROM " + Area.class.getSimpleName() + " o " +
				"WHERE o.usages like '%" + AreaUsages.TRANSFER + "%'" +
				"ORDER BY o.id ";
		Query query = manager.createQuery(queryStr);
		
		return query.getResultList();
    }

    public Area getDefault() {
    	String name = propertyService.getStringDefault(PROPERTY_KEY_AREA_DEFAULT, "Default");
    	
    	Client client = clientService.getSystemClient();
    	Area area = null;
		try {
			area = getByName(client, name);
		} catch (EntityNotFoundException e) {}
		
    	if( area == null ) {
    	    area = createLOSArea(client, name);
    	    area.setUseFor(AreaUsages.PICKING, true);
    	    area.setUseFor(AreaUsages.STORAGE, true);
    	}
		
    	return area;
    }
    
}
