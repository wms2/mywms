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
import de.linogistix.los.inventory.query.dto.LOSUnitLoadAdvicePositionTO;
import de.linogistix.los.query.BODTO;
import java.beans.IntrospectionException;
import java.math.BigDecimal;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 * A {@link BOMasterDevice} for BasicEnity {@link Device}.
 *
 * @author trautm
 */
public class BOLOSUnitLoadAdvicePositionMasterNode extends BOMasterNode {

    LOSUnitLoadAdvicePositionTO to;

    /** Creates a new instance of BODeviceNode */
    public BOLOSUnitLoadAdvicePositionMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (LOSUnitLoadAdvicePositionTO) d;
    }


    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            BOMasterNodeProperty<String> itemData = new BOMasterNodeProperty("itemData", String.class, to.getItemData(), InventoryBundleResolver.class);
            sheet.put(itemData);
            BOMasterNodeProperty<BigDecimal> notifiedAmount = new BOMasterNodeProperty("notifiedAmount", BigDecimal.class, to.getNotifiedAmount(), InventoryBundleResolver.class);
            sheet.put(notifiedAmount);
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
            
            BOMasterNodeProperty<String> itemData = new BOMasterNodeProperty<String>("itemData", String.class,"", InventoryBundleResolver.class);            
            BOMasterNodeProperty<BigDecimal> notifiedAmount = new BOMasterNodeProperty<BigDecimal>("notifiedAmount", BigDecimal.class, BigDecimal.ZERO, InventoryBundleResolver.class);            

        return new Property[]{itemData, notifiedAmount};
    }
}
