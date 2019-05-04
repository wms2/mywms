/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.location.bobrowser.masternode;

import de.linogistix.common.bobrowser.bo.BOMasterNode;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.los.query.BODTO;

import de.linogistix.los.location.query.dto.LOSFixedLocationAssignmentTO;
import java.beans.IntrospectionException;
import java.math.BigDecimal;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 * A {@link BOMasterDevice} for BasicEnity {@link Device}.
 *
 * @author trautm
 */
public class BOFixedAssignmentMasterNode extends BOMasterNode {

    LOSFixedLocationAssignmentTO to;

    /** Creates a new instance of BODeviceNode */
    public BOFixedAssignmentMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (LOSFixedLocationAssignmentTO) d;
    }

//    @Override
//    public String getHtmlDisplayName() {
//        String ret = getDisplayName();
//        return ret;
//    }
  

    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            BOMasterNodeProperty<String> itemData = new BOMasterNodeProperty<String>("itemData", String.class, to.getItemData(), CommonBundleResolver.class);
            sheet.put(itemData);
            BOMasterNodeProperty<String> itemDataName = new BOMasterNodeProperty<String>("itemDataName", String.class, to.getItemDataName(), CommonBundleResolver.class);
            sheet.put(itemDataName);
            BOMasterNodeProperty<String> storageLocation = new BOMasterNodeProperty<String>("storageLocation", String.class, to.getStorageLocation(), CommonBundleResolver.class);
            sheet.put(storageLocation);
            BOMasterNodeProperty<BigDecimal> amount = new BOMasterNodeProperty<BigDecimal>("amount", BigDecimal.class, to.getAmount(), CommonBundleResolver.class);
            sheet.put(amount);
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
        BOMasterNodeProperty<String> itemData = new BOMasterNodeProperty<String>("itemData", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> itemDataName = new BOMasterNodeProperty<String>("itemDataName", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> storageLocation = new BOMasterNodeProperty<String>("storageLocation", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<BigDecimal> amount = new BOMasterNodeProperty<BigDecimal>("amount", BigDecimal.class, null, CommonBundleResolver.class);

        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            itemData, itemDataName, storageLocation, amount
        };

        return props;
    }
}
