/*
 * Copyright (c) 2006 - 2011 LinogistiX GmbH
 *
 *  www.linogistix.com
 *
 *  Project myWMS-LOS
 */
package de.linogistix.inventory.browser.masternode;

import de.linogistix.common.bobrowser.bo.BOMasterNode;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.util.BundleResolve;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.query.dto.LOSPickingOrderTO;
import de.linogistix.los.model.State;

import de.linogistix.los.query.BODTO;
import java.awt.Image;
import java.beans.IntrospectionException;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 *
 * @author krane
 */
public class BOPickingOrderMasterNode extends BOMasterNode {

    LOSPickingOrderTO to;

    /** Creates a new instance of BODeviceNode */
    public BOPickingOrderMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (LOSPickingOrderTO) d;
    }

    @Override
    public String getHtmlDisplayName() {
        String ret = getDisplayName();

        if (to.getState() >= State.FINISHED) {
             ret = "<font color=\"#C0C0C0\">" + ret + "</font>";
        }
        return ret;
    }

    @Override
    public Image getIcon(int arg0) {
        return super.getIcon(arg0);
    }

    @Override
    public PropertySet[] getPropertySets() {
        if (sheet == null) {
            sheet = new Sheet.Set();
            BOMasterNodeProperty<String> client = new BOMasterNodeProperty<String>("clientNumber", String.class, to.getClientNumber(), InventoryBundleResolver.class);
            sheet.put(client);
            BOMasterNodeProperty<String> customerOrderNumber = new BOMasterNodeProperty<String>("customerOrderNumber", String.class, to.getCustomerOrderNumber(), InventoryBundleResolver.class);
            sheet.put(customerOrderNumber);
//            BOMasterNodeProperty<String> userName = new BOMasterNodeProperty<String>("userName", String.class, to.getUserName(), InventoryBundleResolver.class);
//            sheet.put(userName);
//            BOMasterNodeProperty<String> destinationName = new BOMasterNodeProperty<String>("destinationName", String.class, to.getDestinationName(), InventoryBundleResolver.class);
//            sheet.put(destinationName);
//            BOMasterNodeProperty<Integer> numPos = new BOMasterNodeProperty<Integer>("numPos", Integer.class, to.getNumPos(), InventoryBundleResolver.class);
//            sheet.put(numPos);

            String strState = BundleResolve.resolve(new Class[]{bo.getBundleResolver(),CommonBundleResolver.class},"state."+to.getState(), new Object[0], false);
            if( strState == null || strState.length()==0 ) {
                strState = String.valueOf(to.getState());
            }
            BOMasterNodeProperty<String> state = new BOMasterNodeProperty<String>("state", String.class, strState, CommonBundleResolver.class);
            sheet.put(state);

//            String strPrio = BundleResolve.resolve(new Class[]{bo.getBundleResolver(),CommonBundleResolver.class},"prio."+to.getPrio(), new Object[0], false);
//            if( strPrio == null || strPrio.length()==0 ) {
//                strPrio = String.valueOf(to.getPrio());
//            }
//            BOMasterNodeProperty<String> prio = new BOMasterNodeProperty<String>("prio", String.class, strPrio, CommonBundleResolver.class);
//            sheet.put(prio);
        }


        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
            BOMasterNodeProperty<String> client = new BOMasterNodeProperty<String>("clientNumber", String.class, "", InventoryBundleResolver.class);
            BOMasterNodeProperty<String> customerOrderNumber = new BOMasterNodeProperty<String>("customerOrderNumber", String.class, "", InventoryBundleResolver.class);
//            BOMasterNodeProperty<String> userName = new BOMasterNodeProperty<String>("userName", String.class, "", InventoryBundleResolver.class);
//            BOMasterNodeProperty<String> destinationName = new BOMasterNodeProperty<String>("destinationName", String.class, "", InventoryBundleResolver.class);
//            BOMasterNodeProperty<Integer> numPos = new BOMasterNodeProperty<Integer>("numPos", Integer.class, 0, InventoryBundleResolver.class);
            BOMasterNodeProperty<String> state = new BOMasterNodeProperty<String>("state", String.class, "", CommonBundleResolver.class);
//            BOMasterNodeProperty<String> prio = new BOMasterNodeProperty<String>("prio", String.class, "", CommonBundleResolver.class);

        return new Property[]{client, customerOrderNumber, state};
    }
}
