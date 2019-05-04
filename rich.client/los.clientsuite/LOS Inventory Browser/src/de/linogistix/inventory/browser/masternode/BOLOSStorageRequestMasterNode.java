/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.inventory.browser.masternode;

import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.bobrowser.bo.BO;

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
            BOMasterNodeProperty<String> requestState = new BOMasterNodeProperty<String>("requestStateName", String.class, "LOSStorageRequestState." + to.getRequestState().name(), InventoryBundleResolver.class, true);
            sheet.put(requestState);
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
        BOMasterNodeProperty<String> unitLoadLabel = new BOMasterNodeProperty<String>("unitLoadLabel", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> destinationName = new BOMasterNodeProperty<String>("destinationName", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> requestState = new BOMasterNodeProperty<String>("requestStateName", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            unitLoadLabel, destinationName, requestState
        };

        return props;
    }
}
