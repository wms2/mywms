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

import de.linogistix.common.bobrowser.action.BODocumentOpenAction;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.inventory.browser.masternode.BOOrderReceiptMasterNode;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.inventory.crud.OrderReceiptCRUDRemote;
import de.linogistix.los.inventory.model.OrderReceipt;
import de.linogistix.los.inventory.query.OrderReceiptQueryRemote;
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
public class BOOrderReceipt extends BO {
    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR};
    }
 

    protected String initName() {
        return "OrderReceipts";
    }

    protected String initIconBaseWithExtension() {
        return "de/linogistix/bobrowser/res/icon/Document.gif";
    }

    protected BusinessObjectQueryRemote initQueryService() {

        BusinessObjectQueryRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (OrderReceiptQueryRemote) loc.getStateless(OrderReceiptQueryRemote.class);

        } catch (Throwable t) {
            //
        }
        return ret;
    }

    protected BasicEntity initEntityTemplate() {
        OrderReceipt c;

        c = new OrderReceipt();

        return c;

    }

    protected BusinessObjectCRUDRemote initCRUDService() {
        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            return loc.getStateless(OrderReceiptCRUDRemote.class);
        } catch (Throwable t) {}
        return null;
    }

    protected Class initBundleResolver() {
        return CommonBundleResolver.class;
    }

    protected String[] initIdentifiableProperties() {
        return new String[]{"orderNumber"};
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
        return BOOrderReceiptMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOOrderReceiptMasterNode.class;
    }
    
    
    
    
}
