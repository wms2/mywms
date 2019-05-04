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
import de.linogistix.common.bobrowser.action.BODocumentOpenAction;
import de.linogistix.inventory.browser.masternode.BOPickReceiptMasterNode;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.inventory.pick.crud.PickReceiptCRUDRemote;
import de.linogistix.los.inventory.pick.query.PickReceiptQueryRemote;
import de.linogistix.los.inventory.pick.model.PickReceipt;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import java.util.ArrayList;
import java.util.List;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOPickReceipt extends BO {
    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR};
    }
 
    protected String initName() {
        return "PickingReceipts";
    }

    protected String initIconBaseWithExtension() {
        return "de/linogistix/bobrowser/res/icon/PickingReceipt.png";
    }

    protected BusinessObjectQueryRemote initQueryService() {

        BusinessObjectQueryRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (PickReceiptQueryRemote) loc.getStateless(PickReceiptQueryRemote.class);

        } catch (Throwable t) {
            //
        }
        return ret;
    }

    protected BasicEntity initEntityTemplate() {
        PickReceipt c;

        c = new PickReceipt();
        c.setName("");
        return c;

    }

    protected BusinessObjectCRUDRemote initCRUDService() {
        PickReceiptCRUDRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = loc.getStateless(PickReceiptCRUDRemote.class);

        } catch (Throwable t) {
            //ExceptionAnnotator.annotate(t);
        }
        return ret;
    }

    protected Class initBundleResolver() {
        return InventoryBundleResolver.class;
    }

    protected String[] initIdentifiableProperties() {
        return new String[]{"number"};
    }

    @Override
    protected List<SystemAction> initMasterActions() {
        List<SystemAction> actions = new ArrayList<SystemAction>();
        SystemAction action;

        action = SystemAction.get(BODocumentOpenAction.class);
        action.setEnabled(true);
        actions.add(action);

        return actions;
    }

    @Override
    protected Property[] initBoMasterNodeProperties() {
        return BOPickReceiptMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOPickReceiptMasterNode.class;
    }
    
    
    
    
}
