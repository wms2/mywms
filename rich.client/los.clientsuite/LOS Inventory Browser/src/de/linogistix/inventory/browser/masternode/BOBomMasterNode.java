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
import de.linogistix.los.inventory.query.dto.LOSBomTO;
import de.linogistix.los.query.BODTO;
import java.beans.IntrospectionException;
import java.math.BigDecimal;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 *
 * @author krane
 */
public class BOBomMasterNode extends BOMasterNode {

    LOSBomTO to;

    /** Creates a new instance of BODeviceNode */
    public BOBomMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (LOSBomTO) d;
    }


    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            BOMasterNodeProperty<String> parent = new BOMasterNodeProperty<String>("parentNumber", String.class, to.getParentNumber(), InventoryBundleResolver.class);
            sheet.put(parent);
            BOMasterNodeProperty<String> parentName = new BOMasterNodeProperty<String>("parentName", String.class, to.getParentName(), InventoryBundleResolver.class);
            sheet.put(parentName);
            BOMasterNodeProperty<String> child = new BOMasterNodeProperty<String>("childNumber", String.class, to.getChildNumber(), InventoryBundleResolver.class);
            sheet.put(child);
            BOMasterNodeProperty<String> childName = new BOMasterNodeProperty<String>("childName", String.class, to.getChildName(), InventoryBundleResolver.class);
            sheet.put(childName);
            BOMasterNodeProperty<BigDecimal> amount = new BOMasterNodeProperty<BigDecimal>("amount", BigDecimal.class, to.getAmount(), InventoryBundleResolver.class);
            sheet.put(amount);
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
        BOMasterNodeProperty<String> parent = new BOMasterNodeProperty<String>("parentNumber", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> parentName = new BOMasterNodeProperty<String>("parentName", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> child = new BOMasterNodeProperty<String>("childNumber", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> childName = new BOMasterNodeProperty<String>("childName", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<BigDecimal> amount = new BOMasterNodeProperty<BigDecimal>("amount", BigDecimal.class, BigDecimal.ZERO, InventoryBundleResolver.class);
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            parent, parentName, child, childName, amount
        };

        return props;
    }
}
