/*
 * BOUser.java
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
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.user.crud.UserCRUDRemote;
import de.linogistix.los.user.query.UserQueryRemote;
import org.mywms.model.BasicEntity;
import org.mywms.model.User;
import org.mywms.globals.Role;
import org.openide.util.Lookup;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOUser extends BO {

    private static String[] allowedRoles = new String[]{
        Role.ADMIN_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR
    };

    @Override
    public String[] getAllowedRoles() {
        return allowedRoles;
    }

    protected String initName() {
        return "Users";
    }

    @Override
    protected String initIconBaseWithExtension() {

        return "de/linogistix/common/res/icon/User.png";
    }

    protected BusinessObjectQueryRemote initQueryService() {

        BusinessObjectQueryRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (BusinessObjectQueryRemote) loc.getStateless(UserQueryRemote.class);

        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
        }
        return ret;
    }

    protected BasicEntity initEntityTemplate() {
        User o;
        o = new User();
        o.setName("Template");
        o.setFirstname("Template");
        o.setLastname("Template");
        
        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
        o.setClient( login.getUsersClient() );

        return o;

    }

    protected BusinessObjectCRUDRemote initCRUDService() {
        UserCRUDRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (UserCRUDRemote) loc.getStateless(UserCRUDRemote.class);

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
}
