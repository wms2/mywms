/*
 * Copyright (c) 2010 - 2012 LinogistiX GmbH
 *
 *  www.linogistix.com
 *
 *  Project myWMS-3PL
 */
package de.linogistix.inventory.browser.masternode;

import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.los.inventory.query.dto.LOSStorageStrategyTO;


import de.linogistix.los.query.BODTO;
import java.beans.IntrospectionException;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 *
 *  @author krane
 */
public class BOLOSStorageStrategyMasterNode extends BOMasterNode {

    LOSStorageStrategyTO to;

    /** Creates a new instance of BODeviceNode */
    public BOLOSStorageStrategyMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (LOSStorageStrategyTO) d;
    }


    @Override
    public PropertySet[] getPropertySets() {
        if (sheet == null) {
            sheet = new Sheet.Set();
            
//            BOMasterNodeProperty<String> orderDirection;
//            orderDirection = new BOMasterNodeProperty<String>("orderDirection", String.class, "orderDirection."+to.getOrderDirection(), L8xBundleResolver.class, true);
//            sheet.put(orderDirection);

        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
//    public static Property[] boMasterNodeProperties() {
//        BOMasterNodeProperty<String> orderDirection;
//        orderDirection = new BOMasterNodeProperty<String>("orderDirection", String.class, "", L8xBundleResolver.class);
//        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
//            orderDirection
//        };
//
//        return props;
//    }
}
