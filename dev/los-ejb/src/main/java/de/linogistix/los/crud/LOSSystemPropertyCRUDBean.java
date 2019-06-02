/*
 * UserCRUDBean.java
 *
 * Created on 20.02.2007, 18:37:29
 *
 * Copyright (c) 2006/2007 LinogistiX GmbH. All rights reserved.
 *
 * <a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.los.crud;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.service.BasicService;

import de.linogistix.los.util.entityservice.LOSSystemPropertyService;
import de.wms2.mywms.property.SystemProperty;

/**
 * @author trautm
 *
 */
@Stateless
public class LOSSystemPropertyCRUDBean extends BusinessObjectCRUDBean<SystemProperty> implements LOSSystemPropertyCRUDRemote {

    @EJB
    LOSSystemPropertyService service;

    @Override
    protected BasicService<SystemProperty> getBasicService() {
        return service;
    }

}
