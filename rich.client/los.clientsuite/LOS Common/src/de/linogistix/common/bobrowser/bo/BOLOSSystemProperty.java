/*
 * BOLOSSystemProperty.java
 *
 *
 * Copyright (c) 2009-2013 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.bobrowser.bo;

import de.linogistix.common.bobrowser.masternode.BOLOSSystemPropertyMasterNode;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.LOSSystemPropertyQueryRemote;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.crud.LOSSystemPropertyCRUDRemote;
import de.linogistix.los.model.LOSSystemProperty;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.util.Lookup;

/**
 *
 * @author krane
 */
public class BOLOSSystemProperty extends BO {

    private static String[] allowedLOSServicePropertys = new String[]{Role.ADMIN_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};

    protected String initName() {
        return "LOSSystemProperties";
    }

    @Override
    protected String initIconBaseWithExtension() {
        return "de/linogistix/common/res/icon/Edit.png";
    }

    protected BusinessObjectQueryRemote initQueryService() {

        BusinessObjectQueryRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (LOSSystemPropertyQueryRemote) loc.getStateless(LOSSystemPropertyQueryRemote.class);

        } catch (Throwable t) {
            //
        }
        return ret;
    }

    protected BasicEntity initEntityTemplate() {
        LOSSystemProperty c;

        c = new LOSSystemProperty();
        c.setKey("Template");
        c.setValue("Template");

        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
        c.setClient( login.getUsersClient() );
        
        return c;

    }

    protected BusinessObjectCRUDRemote initCRUDService() {
        LOSSystemPropertyCRUDRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (LOSSystemPropertyCRUDRemote) loc.getStateless(LOSSystemPropertyCRUDRemote.class);

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
        return new String[]{"key"};
    }

    @Override
    protected Property[] initBoMasterNodeProperties() {
        return BOLOSSystemPropertyMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOLOSSystemPropertyMasterNode.class;
    }

    @Override
    public String[] getAllowedRoles() {
        return allowedLOSServicePropertys;
    }
}
