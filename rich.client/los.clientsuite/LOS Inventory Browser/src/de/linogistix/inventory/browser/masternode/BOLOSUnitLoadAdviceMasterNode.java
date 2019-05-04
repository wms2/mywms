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
import de.linogistix.los.inventory.model.LOSAdviceType;
import de.linogistix.los.inventory.query.dto.LOSUnitLoadAdviceTO;
import de.linogistix.los.query.BODTO;
import java.beans.IntrospectionException;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;

/**
 * A {@link BOMasterDevice} for BasicEnity {@link Device}.
 *
 * @author trautm
 */
public class BOLOSUnitLoadAdviceMasterNode extends BOMasterNode {

    LOSUnitLoadAdviceTO to;

    /** Creates a new instance of BODeviceNode */
    public BOLOSUnitLoadAdviceMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (LOSUnitLoadAdviceTO) d;
    }


    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            BOMasterNodeProperty<String> labelId = new BOMasterNodeProperty("ulLabelId", String.class, to.getUlLabelId(), InventoryBundleResolver.class);
            sheet.put(labelId);
            String st = NbBundle.getMessage(InventoryBundleResolver.class, LOSAdviceType.class.getSimpleName() + "." + to.getAdviceType());
            BOMasterNodeProperty<String> type = new BOMasterNodeProperty("adviceType", String.class, st, InventoryBundleResolver.class);
            sheet.put(type);
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
            
            BOMasterNodeProperty<String> labelId = new BOMasterNodeProperty<String>("ulLabelId", String.class,"", InventoryBundleResolver.class);            
            BOMasterNodeProperty<String> type = new BOMasterNodeProperty<String>("adviceType", String.class,"", InventoryBundleResolver.class);            

        return new Property[]{labelId, type};
    }
}
