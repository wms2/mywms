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
import javax.persistence.Query;

import org.mywms.model.Client;
import org.mywms.service.AreaService;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.ClientService;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.location.model.LOSArea;
import de.linogistix.los.util.entityservice.LOSSystemPropertyService;

/**
 * @see de.linogistix.los.location.entityservice.LOSRackService
 *  
 * @author Markus Jordan
 * @version $Revision: 339 $ provided by $Author: trautmann $
 */
@Stateless 
public class LOSAreaServiceBean extends BasicServiceBean<LOSArea> implements LOSAreaService, LOSAreaServiceRemote {	
	
	
	public final static String PROPERTY_KEY_AREA_DEFAULT = "AREA_DEFAULT";

	
	@EJB
	private LOSSystemPropertyService propertyService;
	@EJB
	private ClientService clientService;
	@EJB
	private EntityGenerator entityGenerator;
	@EJB
	AreaService delegate;
	
    public LOSArea createLOSArea(Client c, String name) {
        LOSArea a = entityGenerator.generateEntity(LOSArea.class);
        a.setClient(c);
        a.setName(name);
                
        manager.persist(a);
        manager.flush();
        
        return a;
    }

	public LOSArea getByName(Client c, String name) throws EntityNotFoundException {
		return (LOSArea) delegate.getByName(c, name);
	}
	
	
    @SuppressWarnings("unchecked")
	public List<LOSArea> getForGoodsIn( ) {
		String queryStr = 
				"SELECT o FROM " + LOSArea.class.getSimpleName() + " o " +
				"WHERE o.useForGoodsIn=true " +
				"ORDER BY o.id ";
		Query query = manager.createQuery(queryStr);
		
		return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<LOSArea> getForGoodsOut( ) {
		String queryStr = 
				"SELECT o FROM " + LOSArea.class.getSimpleName() + " o " +
				"WHERE o.useForGoodsIn=true " +
				"ORDER BY o.id ";
		Query query = manager.createQuery(queryStr);
		
		return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<LOSArea> getForStorage( ) {
		String queryStr = 
				"SELECT o FROM " + LOSArea.class.getSimpleName() + " o " +
				"WHERE o.useForStorage=true " +
				"ORDER BY o.id ";
		Query query = manager.createQuery(queryStr);
		
		return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<LOSArea> getForPicking( ) {
		String queryStr = 
				"SELECT o FROM " + LOSArea.class.getSimpleName() + " o " +
				"WHERE o.useForPicking=true " +
				"ORDER BY o.id ";
		Query query = manager.createQuery(queryStr);
		
		return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<LOSArea> getForTransfer() {
		String queryStr = 
				"SELECT o FROM " + LOSArea.class.getSimpleName() + " o " +
				"WHERE o.useForTransfer=true " +
				"ORDER BY o.id ";
		Query query = manager.createQuery(queryStr);
		
		return query.getResultList();
    }

    public LOSArea getDefault() {
    	String name = propertyService.getStringDefault(PROPERTY_KEY_AREA_DEFAULT, "Default");
    	
    	Client client = clientService.getSystemClient();
    	LOSArea area = null;
		try {
			area = getByName(client, name);
		} catch (EntityNotFoundException e) {}
		
    	if( area == null ) {
    	    area = createLOSArea(client, name);
    	    area.setUseForPicking(true);
    	    area.setUseForStorage(true);
    	}
		
    	return area;
    }
    
}
