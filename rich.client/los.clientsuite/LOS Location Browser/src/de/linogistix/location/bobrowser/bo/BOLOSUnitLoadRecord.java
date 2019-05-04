/*
 * BOLOSUnitLoadRecord.java
 *
 * Copyright (c) 2006-2013 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.location.bobrowser.bo;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.location.bobrowser.masternode.BOLOSUnitLoadRecordMasterNode;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.location.res.LocationBundleResolver;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.location.model.LOSUnitLoadRecord;
import de.linogistix.los.location.query.LOSUnitLoadRecordQueryRemote;
import java.util.Vector;
import java.util.logging.Logger;
import javax.swing.Action;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.util.Lookup;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOLOSUnitLoadRecord extends BO {
    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {};
    }
 
    private static final Logger log = Logger.getLogger(BOLOSUnitLoadRecord.class.getName());
    Vector<Action> actions;

    protected String initName() {
        return "LOS_UNITLOAD_RECORD";
    }

    @Override
    protected String initIconBaseWithExtension() {

        return "de/linogistix/location/res/icon/UnitLoadRecord.png";
    }

    protected BusinessObjectQueryRemote initQueryService() {

        BusinessObjectQueryRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (BusinessObjectQueryRemote) loc.getStateless(LOSUnitLoadRecordQueryRemote.class);

        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
        }
        return ret;
    }

    protected BasicEntity initEntityTemplate() {
        LOSUnitLoadRecord o;

        o = new LOSUnitLoadRecord();
        o.setLabel("--Change--");
        return o;

    }

    protected BusinessObjectCRUDRemote initCRUDService() {
        return null;
    }

    @Override
    protected Class initBundleResolver() {
        return LocationBundleResolver.class;
    }

    @Override
    protected String[] initIdentifiableProperties() {
        return new String[]{"activityCode"};
    }

    @Override
    protected Property[] initBoMasterNodeProperties() {
        return BOLOSUnitLoadRecordMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOLOSUnitLoadRecordMasterNode.class;
    }
    
}
