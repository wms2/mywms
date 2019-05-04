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
import de.linogistix.inventory.browser.masternode.BOLOSUnitLoadAdvicePositionMasterNode;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.inventory.crud.LOSUnitLoadAdvicePositionCRUDRemote;
import de.linogistix.los.inventory.model.LOSUnitLoadAdvicePosition;
import de.linogistix.los.inventory.query.LOSUnitLoadAdvicePositionQueryRemote;
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
public class BOLOSUnitLoadAdvicePosition extends BO {
    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR,Role.INVENTORY_STR};
    }
 
    public String initName() {
        return "LOSUnitLoadAdvicePositions";
    }

    @Override
    public String initIconBaseWithExtension() {
        return "de/linogistix/bobrowser/res/icon/LOSAdvice.png";
    }

    public BusinessObjectQueryRemote initQueryService() {

        BusinessObjectQueryRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (LOSUnitLoadAdvicePositionQueryRemote) loc.getStateless(LOSUnitLoadAdvicePositionQueryRemote.class);

        } catch (Throwable t) {
            //
        }
        return ret;
    }

    public BasicEntity initEntityTemplate() {
        LOSUnitLoadAdvicePosition c;

        c = new LOSUnitLoadAdvicePosition();

        return c;

    }

    public BusinessObjectCRUDRemote initCRUDService() {
        LOSUnitLoadAdvicePositionCRUDRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (LOSUnitLoadAdvicePositionCRUDRemote) loc.getStateless(LOSUnitLoadAdvicePositionCRUDRemote.class);

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
        return new String[]{"positionNumber"};
    }
    
    @Override
    protected Property[] initBoMasterNodeProperties() {
        return BOLOSUnitLoadAdvicePositionMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOLOSUnitLoadAdvicePositionMasterNode.class;
    }

}
