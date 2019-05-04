/*
 * BONodeClient.java
 *
 * Created on 1. Dezember 2006, 01:17
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.inventory.browser.bo;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.inventory.browser.masternode.BOLOSUnitLoadAdviceMasterNode;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.inventory.crud.LOSUnitLoadAdviceCRUDRemote;
import de.linogistix.los.inventory.model.LOSUnitLoadAdvice;
import de.linogistix.los.inventory.query.LOSUnitLoadAdviceQueryRemote;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.util.Lookup;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOLOSUnitLoadAdvice extends BO {
    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR,Role.INVENTORY_STR};
    }

    public String initName() {
        return "LOSUnitLoadAdvices";
    }

    @Override
    public String initIconBaseWithExtension() {
        return "de/linogistix/bobrowser/res/icon/LOSAdvice.png";
    }

    public BusinessObjectQueryRemote initQueryService() {

        BusinessObjectQueryRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (LOSUnitLoadAdviceQueryRemote) loc.getStateless(LOSUnitLoadAdviceQueryRemote.class);

        } catch (Throwable t) {
            //
        }
        return ret;
    }

    public BasicEntity initEntityTemplate() {
        LOSUnitLoadAdvice c;

        c = new LOSUnitLoadAdvice();

        return c;

    }

    public BusinessObjectCRUDRemote initCRUDService() {
        LOSUnitLoadAdviceCRUDRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (LOSUnitLoadAdviceCRUDRemote) loc.getStateless(LOSUnitLoadAdviceCRUDRemote.class);

        } catch (Throwable t) {
            //ExceptionAnnotator.annotate(t);
        }
        return ret;
    }

    @Override
    public Class<InventoryBundleResolver> initBundleResolver() {
        return InventoryBundleResolver.class;
    }

    @Override
    protected String[] initIdentifiableProperties() {
        return new String[]{"number"};
    }

    @Override
    protected Property[] initBoMasterNodeProperties() {
        return BOLOSUnitLoadAdviceMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOLOSUnitLoadAdviceMasterNode.class;
    }

}
