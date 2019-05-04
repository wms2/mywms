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

import de.linogistix.los.inventory.pick.query.dto.PickReceiptTO;
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
public class BOPickReceiptMasterNode extends BOMasterNode {

    PickReceiptTO to;

    /** Creates a new instance of BODeviceNode */
    public BOPickReceiptMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (PickReceiptTO) d;
    }

    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            BOMasterNodeProperty<String> orderNumber = new BOMasterNodeProperty<String>("orderNumber", String.class, to.orderNumber, CommonBundleResolver.class);
            sheet.put(orderNumber);
            BOMasterNodeProperty<String> pickNumber = new BOMasterNodeProperty<String>("pickNumber", String.class, to.pickNumber, CommonBundleResolver.class);
            sheet.put(pickNumber);
            BOMasterNodeProperty<String> labelID = new BOMasterNodeProperty<String>("labelID", String.class, to.labelID, CommonBundleResolver.class);
            sheet.put(labelID);
            BOMasterNodeProperty<Date> date = new BOMasterNodeProperty<Date>("expectedDelivery", Date.class, to.date, CommonBundleResolver.class);
            sheet.put(date);
//            BOMasterNodeProperty<String> state = new BOMasterNodeProperty<String>("state", String.class, "PickingRequestState." + to.state, CommonBundleResolver.class, true);
//            sheet.put(state);
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
        BOMasterNodeProperty<String> orderNumber = new BOMasterNodeProperty<String>("orderNumber", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> pickNumber = new BOMasterNodeProperty<String>("pickNumber", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> labelID = new BOMasterNodeProperty<String>("labelID", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<Date> date = new BOMasterNodeProperty<Date>("expectedDelivery", Date.class, new Date(), CommonBundleResolver.class);
//        BOMasterNodeProperty<String> state = new BOMasterNodeProperty<String>("state", String.class, "PickingRequestState.FINISHED", CommonBundleResolver.class);
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            orderNumber, pickNumber, labelID, date
//                    , state
        };

        return props;
    }
}
