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
import de.linogistix.location.bobrowser.masternode.BOLOSRackMasterNode;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.entityservice.BusinessObjectLock;
import de.linogistix.los.location.constants.LOSStorageLocationLockState;
import de.linogistix.los.location.crud.LOSRackCRUDRemote;
import de.linogistix.los.location.model.LOSRack;
import de.linogistix.los.location.query.RackQueryRemote;
import java.util.Arrays;
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
public class BORack extends BO {

    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR,Role.FOREMAN_STR};
    }


    protected String initName() {
        return "Racks";
    }

    @Override
    protected String initIconBaseWithExtension() {
        return "de/linogistix/location/res/icon/Rack.png";
    }

    protected BusinessObjectQueryRemote initQueryService() {

        BusinessObjectQueryRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (RackQueryRemote) loc.getStateless(RackQueryRemote.class);

        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
        }
        return ret;
    }

    protected BasicEntity initEntityTemplate() {
        LOSRack c;

        c = new LOSRack();
        c.setName("Template");

        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
        c.setClient( login.getUsersClient() );

        return c;

    }

    protected BusinessObjectCRUDRemote initCRUDService() {
        LOSRackCRUDRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (LOSRackCRUDRemote) loc.getStateless(LOSRackCRUDRemote.class);

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
    protected Property[] initBoMasterNodeProperties() {
        return BOLOSRackMasterNode.boMasterNodeProperties();
    }
    
    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOLOSRackMasterNode.class;
    }

    @Override
    public List<BusinessObjectLock> getLockStates() {
        
        LOSStorageLocationLockState[] slLocks = LOSStorageLocationLockState.values();
           
        return Arrays.asList((BusinessObjectLock[]) slLocks);         
    }

}
