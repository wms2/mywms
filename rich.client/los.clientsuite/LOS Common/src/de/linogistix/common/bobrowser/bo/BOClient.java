/*
 * BOClient.java
 *
 * Created on 1. Dezember 2006, 01:17
 *
 * Copyright (c) 2006-2013 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.bobrowser.bo;

import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.ClientQueryRemote;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.crud.ClientCRUDRemote;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.mywms.model.Client;
import org.openide.util.Lookup;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOClient extends BO {

    private static String[] allowedRoles = new String[]{Role.ADMIN_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};

    protected String initName() {
        return "Clients";
    }

    @Override
    protected String initIconBaseWithExtension() {
        return "de/linogistix/common/res/icon/Client.png";
    }

    protected BusinessObjectQueryRemote initQueryService() {

        BusinessObjectQueryRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (BusinessObjectQueryRemote) loc.getStateless(ClientQueryRemote.class);

        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
        }
        return ret;
    }

    protected BasicEntity initEntityTemplate() {
        Client c;

        c = new Client();
        c.setName("Template");
        c.setNumber("Template");
        c.setCode("Template");

        return c;

    }

    protected BusinessObjectCRUDRemote initCRUDService() {
        ClientCRUDRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (ClientCRUDRemote) loc.getStateless(ClientCRUDRemote.class);

        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
        }
        return ret;
    }

    @Override
    protected Class initBundleResolver() {
        return CommonBundleResolver.class;
    }

    @Override
    protected String[] initIdentifiableProperties() {
        return new String[]{"name"};
    }

    @Override
    public String[] getAllowedRoles() {
        return allowedRoles;
    }
}
