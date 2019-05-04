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

import org.mywms.model.Client;
import org.mywms.service.BasicService;
import org.mywms.service.ClientService;

/**
 * @author trautm
 *
 */
@Stateless
public class ClientCRUDBean extends BusinessObjectCRUDBean<Client> implements ClientCRUDRemote {

    @EJB
    ClientService service;

    @Override
    protected BasicService<Client> getBasicService() {
        return service;
    }

}
