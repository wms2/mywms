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
import de.linogistix.los.inventory.query.dto.LotTO;
import de.linogistix.los.query.BODTO;
import java.beans.IntrospectionException;
import java.util.Date;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 * A {@link BOMasterDevice} for BasicEnity {@link Device}.
 *
 * @author trautm
 */
public class BOLotMasterNode extends BOMasterNode {

    LotTO to;

    /** Creates a new instance of BODeviceNode */
    public BOLotMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (LotTO) d;
    }


    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            BOMasterNodeProperty<String> itemData = new BOMasterNodeProperty<String>("itemData", String.class, to.getItemData(), InventoryBundleResolver.class);
            sheet.put(itemData);
            BOMasterNodeProperty<String> itemDataName = new BOMasterNodeProperty<String>("itemDataName", String.class, to.getItemDataName(), InventoryBundleResolver.class);
            sheet.put(itemDataName);
            BOMasterNodeProperty<Date> useNotBefore = new BOMasterNodeProperty<Date>("useNotBefore", Date.class, to.getUseNotBefore(), InventoryBundleResolver.class);
            sheet.put(useNotBefore);
            BOMasterNodeProperty<Date> bestBeforeEnd = new BOMasterNodeProperty<Date>("bestBeforeEnd", Date.class, to.getBestBeforeEnd(), InventoryBundleResolver.class);
            sheet.put(bestBeforeEnd);
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
        BOMasterNodeProperty<String> itemData = new BOMasterNodeProperty<String>("itemData", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> itemDataName = new BOMasterNodeProperty<String>("itemDataName", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<Date> useNotBefore = new BOMasterNodeProperty<Date>("useNotBefore", Date.class, new Date(), InventoryBundleResolver.class);
        BOMasterNodeProperty<Date> bestBeforeEnd = new BOMasterNodeProperty<Date>("bestBeforeEnd", Date.class, new Date(), InventoryBundleResolver.class);
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            itemData, itemDataName, useNotBefore, bestBeforeEnd
        };

        return props;
    }
}
