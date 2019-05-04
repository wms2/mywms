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

import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.query.dto.LOSGoodsOutPositionTO;
import de.linogistix.los.query.BODTO;
import java.awt.Image;
import java.beans.IntrospectionException;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 * A {@link BOMasterDevice} for BasicEnity {@link Device}.
 *
 * @author trautm
 */
public class BOLOSGoodsOutPositionMasterNode extends BOMasterNode {

    LOSGoodsOutPositionTO to;

    /** Creates a new instance of BODeviceNode */
    public BOLOSGoodsOutPositionMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (LOSGoodsOutPositionTO) d;
    }

    
    @Override
    public Image getIcon(int arg0) {
        return super.getIcon(arg0);
    }

    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            BOMasterNodeProperty<String> goodsOutNumber = new BOMasterNodeProperty<String>("goodsOutNumber", String.class, to.getGoodsOutNumber(), InventoryBundleResolver.class);
            sheet.put(goodsOutNumber);
            BOMasterNodeProperty<String> locationName = new BOMasterNodeProperty<String>("locationName", String.class, to.getLocationName(), CommonBundleResolver.class);
            sheet.put(locationName);
            BOMasterNodeProperty<String> outState = new BOMasterNodeProperty<String>("outState", String.class, "LOSGoodsOutRequestPositionState." + to.getOutState(), CommonBundleResolver.class, true);
            sheet.put(outState);
            
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {

        BOMasterNodeProperty<String> goodsOutNumber = new BOMasterNodeProperty<String>("goodsOutNumber", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> locationName = new BOMasterNodeProperty<String>("locationName", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> outState = new BOMasterNodeProperty<String>("outState", String.class, "", CommonBundleResolver.class);
        return new Property[]{goodsOutNumber, locationName, outState};
    }
}
