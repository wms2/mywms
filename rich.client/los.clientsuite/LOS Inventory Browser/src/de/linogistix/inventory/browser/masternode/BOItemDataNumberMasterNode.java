/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.inventory.browser.masternode;

import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.bobrowser.bo.BO;

import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.query.dto.ItemDataNumberTO;
import de.linogistix.los.query.BODTO;
import java.beans.IntrospectionException;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 *
 * @author krane
 */
public class BOItemDataNumberMasterNode extends BOMasterNode {

    ItemDataNumberTO to;

    /** Creates a new instance of BODeviceNode */
    public BOItemDataNumberMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (ItemDataNumberTO) d;
    }


    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            BOMasterNodeProperty<String> number = new BOMasterNodeProperty<String>("itemDataNumber", String.class, to.getItemDataNumber(), InventoryBundleResolver.class);
            sheet.put(number);
            BOMasterNodeProperty<String> itemDataName = new BOMasterNodeProperty<String>("itemDataName", String.class, to.getItemDataName(), InventoryBundleResolver.class);
            sheet.put(itemDataName);
            BOMasterNodeProperty<Integer> index = new BOMasterNodeProperty<Integer>("index", Integer.class, to.getIndex(), InventoryBundleResolver.class);
            sheet.put(index);
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
        BOMasterNodeProperty<String> number = new BOMasterNodeProperty<String>("itemDataNumber", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> itemDataName = new BOMasterNodeProperty<String>("itemDataName", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<Integer> index = new BOMasterNodeProperty<Integer>("index", Integer.class, 0, InventoryBundleResolver.class);
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            number, itemDataName, index
        };

        return props;
    }
}
