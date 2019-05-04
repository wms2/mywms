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
import de.linogistix.los.inventory.query.dto.LOSPickingUnitLoadTO;
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
public class BOPickingUnitLoadMasterNode extends BOMasterNode {

    LOSPickingUnitLoadTO to;

    /** Creates a new instance of BODeviceNode */
    public BOPickingUnitLoadMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (LOSPickingUnitLoadTO) d;
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
            BOMasterNodeProperty<String> pickingOrderNumber = new BOMasterNodeProperty<String>("pickingOrderNumber", String.class, to.getPickingOrderNumber(), InventoryBundleResolver.class);
            sheet.put(pickingOrderNumber);
            BOMasterNodeProperty<String> customerOrderNumber = new BOMasterNodeProperty<String>("customerOrderNumber", String.class, to.getCustomerOrderNumber(), InventoryBundleResolver.class);
            sheet.put(customerOrderNumber);
            BOMasterNodeProperty<String> locationName = new BOMasterNodeProperty<String>("locationName", String.class, to.getLocationName(), CommonBundleResolver.class);
            sheet.put(locationName);

            String strState = BundleResolve.resolve(new Class[]{bo.getBundleResolver(),CommonBundleResolver.class},"state."+to.getState(), new Object[0], false);
            if( strState == null || strState.length()==0 ) {
                strState = String.valueOf(to.getState());
            }
            BOMasterNodeProperty<String> state = new BOMasterNodeProperty<String>("state", String.class, strState, CommonBundleResolver.class);
            sheet.put(state);

        }


        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
            BOMasterNodeProperty<String> client = new BOMasterNodeProperty<String>("clientNumber", String.class, "", InventoryBundleResolver.class);
            BOMasterNodeProperty<String> pickingOrderNumber = new BOMasterNodeProperty<String>("pickingOrderNumber", String.class, "", InventoryBundleResolver.class);
            BOMasterNodeProperty<String> customerOrderNumber = new BOMasterNodeProperty<String>("customerOrderNumber", String.class, "", InventoryBundleResolver.class);
            BOMasterNodeProperty<String> locationName = new BOMasterNodeProperty<String>("locationName", String.class, "", CommonBundleResolver.class);
            BOMasterNodeProperty<String> state = new BOMasterNodeProperty<String>("state", String.class, "", CommonBundleResolver.class);

        return new Property[]{client, pickingOrderNumber, customerOrderNumber, locationName, state};
    }
}
