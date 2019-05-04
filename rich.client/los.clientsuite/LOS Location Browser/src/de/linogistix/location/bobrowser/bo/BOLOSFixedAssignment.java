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
package de.linogistix.location.bobrowser.bo;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.location.bobrowser.masternode.BOFixedAssignmentMasterNode;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.location.crud.LOSFixedLocationAssignmentCRUDRemote;
import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.query.LOSFixedLocationAssignmentQueryRemote;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.openide.nodes.Node.Property;
import org.openide.util.Lookup;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOLOSFixedAssignment extends BO {

    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR,Role.FOREMAN_STR};
    }


    public String initName() {
        return "LOSFixedLocationAssignments";
    }

    @Override
    public String initIconBaseWithExtension() {
        return "de/linogistix/location/res/icon/LOSFixedLocationAssignment.png";
    }

    public BusinessObjectQueryRemote initQueryService() {

        BusinessObjectQueryRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (LOSFixedLocationAssignmentQueryRemote) loc.getStateless(LOSFixedLocationAssignmentQueryRemote.class);

        } catch (Throwable t) {
            //
        }
        return ret;
    }

    public BasicEntity initEntityTemplate() {
        LOSFixedLocationAssignment c;

        c = new LOSFixedLocationAssignment();
        return c;

    }

    public BusinessObjectCRUDRemote initCRUDService() {
        LOSFixedLocationAssignmentCRUDRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (LOSFixedLocationAssignmentCRUDRemote) loc.getStateless(LOSFixedLocationAssignmentCRUDRemote.class);

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
        return new String[]{"requestId"};
    }

    @Override
    protected Property[] initBoMasterNodeProperties() {
        return BOFixedAssignmentMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<BOFixedAssignmentMasterNode> initBoMasterNodeType() {
        return BOFixedAssignmentMasterNode.class;
    }
}
