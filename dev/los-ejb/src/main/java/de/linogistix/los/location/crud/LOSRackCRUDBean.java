/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.crud;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.location.entityservice.LOSRackService;
import de.linogistix.los.location.model.LOSRack;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.service.BasicService;

/**
 *
 * @author Jordan
 */
@Stateless
public class LOSRackCRUDBean extends BusinessObjectCRUDBean<LOSRack> 
                             implements LOSRackCRUDRemote {

    @EJB
    LOSRackService service;
    
    @Override
    protected BasicService<LOSRack> getBasicService() {
        return service;
    }
}
