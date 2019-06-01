/*
 * BOLOSTypeCapacityConstraint.java
 *
 * Created on 1. Dezember 2006, 01:17
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.location.bobrowser.bo;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.location.bobrowser.masternode.BOLOSTypeCapacityConstraintMasterNode;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.location.crud.LOSTypeCapacityConstraintCRUDRemote;
import de.linogistix.los.location.query.LOSTypeCapacityConstraintQueryRemote;
import de.wms2.mywms.strategy.TypeCapacityConstraint;
import java.util.ArrayList;
import java.util.List;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.util.Lookup;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOLOSTypeCapacityConstraint extends BO {

    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR,Role.FOREMAN_STR};
    }


    protected String initName() {
        return "LOSTypeCapacityConstraints";
    }

    @Override
    protected String initIconBaseWithExtension() {
        return "de/linogistix/location/res/icon/Document.png";
    }

    protected BusinessObjectQueryRemote initQueryService() {

        BusinessObjectQueryRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (BusinessObjectQueryRemote) loc.getStateless(LOSTypeCapacityConstraintQueryRemote.class);

        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
        }
        return ret;
    }

    protected BasicEntity initEntityTemplate() {
        TypeCapacityConstraint c;

        c = new TypeCapacityConstraint();
        
        return c;

    }

    protected BusinessObjectCRUDRemote initCRUDService() {
        LOSTypeCapacityConstraintCRUDRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (LOSTypeCapacityConstraintCRUDRemote) loc.getStateless(LOSTypeCapacityConstraintCRUDRemote.class);

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
        return new String[]{"id"};
    }

    @Override
    public List<Object> getValueList(String fieldName) {
        if( fieldName.equals("allocationType")) {
            List<Object> entryList = new ArrayList<Object>();
            entryList.add(1);
            entryList.add(2);
            return entryList;
        }
        return super.getValueList(fieldName);
    }

    @Override
    protected Property[] initBoMasterNodeProperties() {
        return BOLOSTypeCapacityConstraintMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOLOSTypeCapacityConstraintMasterNode.class;
    }

}
