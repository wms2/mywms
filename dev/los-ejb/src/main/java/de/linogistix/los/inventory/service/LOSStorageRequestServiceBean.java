/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package de.linogistix.los.inventory.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.model.Client;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.ClientService;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.model.LOSStorageRequest;
import de.linogistix.los.inventory.model.LOSStorageRequestState;
import de.linogistix.los.location.service.UnitLoadService;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.StorageLocation;

/**
 *  
 * @author Taieb El Fakiri
 * @version $Revision: 276 $ provided by $Author: trautmann $
 */
@Stateless
public class LOSStorageRequestServiceBean
        extends BasicServiceBean<LOSStorageRequest>
        implements LOSStorageRequestService {

    private static final Logger log = Logger.getLogger(LOSStorageRequestServiceBean.class);
    @EJB
    UnitLoadService ulService;
    @EJB
    ClientService clService;
	@EJB
	private EntityGenerator entityGenerator;
    @SuppressWarnings("unchecked")
	public LOSStorageRequest getRawOrCreateByLabel(Client c, String label) throws InventoryException, EntityNotFoundException {
        LOSStorageRequest ret;
        UnitLoad ul;
                
        ul = ulService.getByLabelId(clService.getSystemClient(), label);
        
        Query query =
                manager.createQuery("SELECT req FROM " 
                + LOSStorageRequest.class.getSimpleName() 
                + " req " 
                + " WHERE req.unitLoad.labelId=:label "
                +" AND req.requestState = :rstate "
                );

        query.setParameter("label", label);
        query.setParameter("rstate", LOSStorageRequestState.RAW);
        
        List<LOSStorageRequest> list = query.getResultList();
        if (list == null || list.size() < 1){   
            ret = entityGenerator.generateEntity(LOSStorageRequest.class);
            ret.setUnitLoad(ul);
            ret.setNumber("");
            ret.setClient(ul.getClient());
            manager.persist(ret);
            log.info("CREATED LOSStorageRequest for " + label);
            return ret;
        } else{
            log.warn("FOUND existing LOSStorageRequest for " + label);
            return list.get(0);
        }
    }

	@SuppressWarnings("unchecked")
	public List<LOSStorageRequest> getListByLabelId(String label) {
		
		Query query = manager.createQuery("SELECT req FROM "
				+ LOSStorageRequest.class.getSimpleName() + " req "
				+ " WHERE req.unitLoad.labelId=:label ");

		query.setParameter("label", label);
		
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<LOSStorageRequest> getListByUnitLoad(UnitLoad unitLoad) {
		
		Query query = manager.createQuery("SELECT req FROM "
				+ LOSStorageRequest.class.getSimpleName() + " req "
				+ " WHERE req.unitLoad=:unitLoad ");

		query.setParameter("unitLoad", unitLoad);
		
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<LOSStorageRequest> getActiveListByDestination(StorageLocation destination) {
		Query query = manager.createNamedQuery("LOSStorageRequest.queryActiveByDestination");
		query.setParameter("destination", destination);
		query.setParameter("stateRaw", LOSStorageRequestState.RAW);
		query.setParameter("stateProcessing", LOSStorageRequestState.PROCESSING);
		
		return query.getResultList();
	}
}
