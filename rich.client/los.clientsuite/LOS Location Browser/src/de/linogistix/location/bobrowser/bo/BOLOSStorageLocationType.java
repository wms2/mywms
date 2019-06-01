/*
 * BOLOSStorageLocationType.java
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
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.location.crud.LOSStorageLocationTypeCRUDRemote;
import de.linogistix.los.location.query.LOSStorageLocationTypeQueryRemote;
import de.wms2.mywms.location.LocationType;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.openide.util.Lookup;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOLOSStorageLocationType extends BO {

    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR,Role.FOREMAN_STR};
    }


    protected String initName() {
        return "LOSStorageLocationTypes";
    }

    @Override
    protected String initIconBaseWithExtension() {
        return "de/linogistix/location/res/icon/Document.png";
    }

    protected BusinessObjectQueryRemote initQueryService() {

        BusinessObjectQueryRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (BusinessObjectQueryRemote) loc.getStateless(LOSStorageLocationTypeQueryRemote.class);

        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
        }
        return ret;
    }

    protected BasicEntity initEntityTemplate() {
        LocationType c;

        c = new LocationType();
        
        c.setName("Template");
        
        return c;
    }

    protected BusinessObjectCRUDRemote initCRUDService() {
        LOSStorageLocationTypeCRUDRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (LOSStorageLocationTypeCRUDRemote) loc.getStateless(LOSStorageLocationTypeCRUDRemote.class);

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

}
