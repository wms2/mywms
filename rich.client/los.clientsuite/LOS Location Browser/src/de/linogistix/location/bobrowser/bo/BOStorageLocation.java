/*
 * BONodeUser.java
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
import de.linogistix.common.services.J2EEServiceLocatorException;
import de.linogistix.location.bobrowser.masternode.BOLOStorageLocationMasterNode;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.location.res.icon.LocationIconResolver;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.location.crud.LOSStorageLocationCRUDRemote;
import de.linogistix.los.location.entityservice.LOSAreaServiceRemote;
import de.linogistix.los.location.entityservice.LOSLocationClusterServiceRemote;
import de.linogistix.los.location.entityservice.LOSStorageLocationTypeServiceRemote;
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote;
import de.wms2.mywms.location.StorageLocation;
import java.util.ArrayList;
import java.util.List;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOStorageLocation extends BO {

    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR,Role.FOREMAN_STR};
    }


    protected String initName() {
        return "StorageLocations";
    }

    @Override
    protected String initIconBaseWithExtension() {

        return "de/linogistix/location/res/icon/StorageLocation.png";
    }

    @Override
    public Class getIconResolver() {
        return LocationIconResolver.class;
    }
    
    protected BusinessObjectQueryRemote initQueryService() {

        BusinessObjectQueryRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (BusinessObjectQueryRemote) loc.getStateless(LOSStorageLocationQueryRemote.class);

        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
        }
        return ret;
    }

    protected BasicEntity initEntityTemplate() {
        StorageLocation o;

        o = new StorageLocation();
        o.setName("Template");

        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
        o.setClient( login.getUsersClient() );

        J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
        try {
            LOSLocationClusterServiceRemote clusterService = loc.getStateless(LOSLocationClusterServiceRemote.class);
            o.setCluster(clusterService.getDefault());
            LOSAreaServiceRemote areaService =  loc.getStateless(LOSAreaServiceRemote.class);
            o.setArea(areaService.getDefault());
            LOSStorageLocationTypeServiceRemote locationTypeService =  loc.getStateless(LOSStorageLocationTypeServiceRemote.class);
            o.setType(locationTypeService.getDefaultStorageLocationType());


        } catch (J2EEServiceLocatorException ex) {
            Exceptions.printStackTrace(ex);
        }
        return o;

    }

    protected BusinessObjectCRUDRemote initCRUDService() {
        BusinessObjectCRUDRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (LOSStorageLocationCRUDRemote) loc.getStateless(LOSStorageLocationCRUDRemote.class);

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

//    @Override
//    public List<SystemAction> initMasterActions() {
//        List<SystemAction> actions = new ArrayList<SystemAction>();
//
//        SystemAction action;
//
//        action = SystemAction.get(BOStorageLocationReleaseAction.class);
//        action.setEnabled(true);
//        actions.add(action);
//
//        return actions;
//    }

    @Override
    protected Property[] initBoMasterNodeProperties() {
        return BOLOStorageLocationMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOLOStorageLocationMasterNode.class;
    }

        @Override
    public List<Object> getValueList(String fieldName) {
        if( "lock".equals(fieldName) ) {
            List<Object> entryList = new ArrayList<Object>();
            entryList.add(0);
            entryList.add(1);
            entryList.add(7);

            return entryList;
        }
        else if( fieldName.equals("allocationState")) {
            List<Object> entryList = new ArrayList<Object>();
            entryList.add(0);
            entryList.add(1);
            return entryList;
        }

        return super.getValueList(fieldName);
    }
}
