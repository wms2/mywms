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
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.query.dto.LOSOrderStrategyTO;


import de.linogistix.los.query.BODTO;
import java.beans.IntrospectionException;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 *
 *  @author krane
 */
public class BOOrderStrategyMasterNode extends BOMasterNode {

    LOSOrderStrategyTO to;

    /** Creates a new instance of BODeviceNode */
    public BOOrderStrategyMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (LOSOrderStrategyTO) d;
    }


    @Override
    public PropertySet[] getPropertySets() {
        if (sheet == null) {
            sheet = new Sheet.Set();
            
            BOMasterNodeProperty<String> clientNumber;
            clientNumber = new BOMasterNodeProperty<String>("clientNumber", String.class, to.getClientNumber(), InventoryBundleResolver.class);
            sheet.put(clientNumber);

        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
        BOMasterNodeProperty<String> clientNumber;
        clientNumber = new BOMasterNodeProperty<String>("clientNumber", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            clientNumber
        };

        return props;
    }
}
