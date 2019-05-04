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
import de.linogistix.los.inventory.query.dto.ItemDataTO;
import de.linogistix.los.query.BODTO;
import java.beans.IntrospectionException;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 * A {@link BOMasterDevice} for BasicEnity {@link Device}.
 *
 * @author trautm
 */
public class BOItemDataMasterNode extends BOMasterNode {

    ItemDataTO to;

    /** Creates a new instance of BODeviceNode */
    public BOItemDataMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (ItemDataTO) d;
    }


    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            BOMasterNodeProperty<String> clientNumber = new BOMasterNodeProperty<String>("clientNumber", String.class, to.getClientNumber(), InventoryBundleResolver.class);
            sheet.put(clientNumber);
            BOMasterNodeProperty<String> name = new BOMasterNodeProperty<String>("nameX", String.class, to.getNameX(), InventoryBundleResolver.class);
            sheet.put(name);
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
        BOMasterNodeProperty<String> clientNumber = new BOMasterNodeProperty<String>("clientNumber", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> name = new BOMasterNodeProperty<String>("nameX", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            clientNumber, name
        };

        return props;
    }
}
