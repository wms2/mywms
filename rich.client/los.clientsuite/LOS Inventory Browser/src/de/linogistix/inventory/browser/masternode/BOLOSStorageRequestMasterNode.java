/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.inventory.browser.masternode;

import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.util.BundleResolve;

import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.query.dto.LOSStorageRequestTO;
import de.linogistix.los.query.BODTO;
import java.beans.IntrospectionException;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 * A {@link BOMasterDevice} for BasicEnity {@link Device}.
 *
 * @author trautm
 */
public class BOLOSStorageRequestMasterNode extends BOMasterNode {

    LOSStorageRequestTO to;

    /** Creates a new instance of BODeviceNode */
    public BOLOSStorageRequestMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (LOSStorageRequestTO) d;
    }


    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            BOMasterNodeProperty<String> unitLoadLabel = new BOMasterNodeProperty<String>("unitLoadLabel", String.class, to.getUnitLoadLabel(), InventoryBundleResolver.class);
            sheet.put(unitLoadLabel);
            BOMasterNodeProperty<String> destinationName = new BOMasterNodeProperty<String>("destinationName", String.class, to.getDestinationName(), InventoryBundleResolver.class);
            sheet.put(destinationName);
            String strState = BundleResolve.resolve(new Class[]{bo.getBundleResolver(),CommonBundleResolver.class},"state."+to.getState(), new Object[0], false);
            if( strState == null || strState.length()==0 ) {
                strState = String.valueOf(to.getState());
            }
            BOMasterNodeProperty<String> state = new BOMasterNodeProperty<String>("state", String.class, strState, null);
            sheet.put(state);
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
        BOMasterNodeProperty<String> unitLoadLabel = new BOMasterNodeProperty<String>("unitLoadLabel", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> destinationName = new BOMasterNodeProperty<String>("destinationName", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> state = new BOMasterNodeProperty<String>("state", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            unitLoadLabel, destinationName, state
        };

        return props;
    }
}
