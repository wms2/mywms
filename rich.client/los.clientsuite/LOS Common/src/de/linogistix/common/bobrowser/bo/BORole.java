/*
 * BORole.java
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
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.user.crud.RoleCRUDRemote;
import de.linogistix.los.user.query.RoleQueryRemote;
import org.mywms.model.BasicEntity;
import org.mywms.model.Role;
import org.openide.util.Lookup;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BORole extends BO {

    private static String[] allowedRoles = new String[]{org.mywms.globals.Role.ADMIN.toString(),org.mywms.globals.Role.CLEARING_STR,org.mywms.globals.Role.INVENTORY_STR,org.mywms.globals.Role.FOREMAN_STR};

    protected String initName() {
        return "Roles";
    }

    @Override
    protected String initIconBaseWithExtension() {
        return "de/linogistix/common/res/icon/Role.png";
    }

    protected BusinessObjectQueryRemote initQueryService() {

        BusinessObjectQueryRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (RoleQueryRemote) loc.getStateless(RoleQueryRemote.class);

        } catch (Throwable t) {
            //
        }
        return ret;
    }

    protected BasicEntity initEntityTemplate() {
        Role c;

        c = new Role();
        c.setName("Template");

        return c;

    }

    protected BusinessObjectCRUDRemote initCRUDService() {
        RoleCRUDRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (RoleCRUDRemote) loc.getStateless(RoleCRUDRemote.class);

        } catch (Throwable t) {
            //ExceptionAnnotator.annotate(t);
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
