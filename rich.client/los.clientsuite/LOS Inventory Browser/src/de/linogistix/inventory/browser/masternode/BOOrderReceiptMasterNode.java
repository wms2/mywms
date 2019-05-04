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

import de.linogistix.los.inventory.model.LOSOrderRequestState;
import de.linogistix.los.inventory.query.dto.OrderReceiptTO;
import de.linogistix.los.query.BODTO;
import java.beans.IntrospectionException;
import java.util.Date;
import java.util.GregorianCalendar;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 * A {@link BOMasterDevice} for BasicEnity {@link Device}.
 *
 * @author trautm
 */
public class BOOrderReceiptMasterNode extends BOMasterNode {

    OrderReceiptTO to;

    /** Creates a new instance of BODeviceNode */
    public BOOrderReceiptMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (OrderReceiptTO) d;
    }


    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
//            BOMasterNodeProperty<String> orderNumber = new BOMasterNodeProperty<String>("orderNumber", String.class, to.orderNumber, CommonBundleResolver.class);
//            sheet.put(orderNumber);
            BOMasterNodeProperty<String> orderReference = new BOMasterNodeProperty<String>("orderReference", String.class, to.orderReference, CommonBundleResolver.class);
            sheet.put(orderReference);
            BOMasterNodeProperty<Date> date = new BOMasterNodeProperty<Date>("date",Date.class, to.date, CommonBundleResolver.class);
            sheet.put(date);
//            BOMasterNodeProperty<String> state = new BOMasterNodeProperty<String>("state", String.class, "LOSOrderRequestState." + to.state, CommonBundleResolver.class, true);
//            sheet.put(state);
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
//        BOMasterNodeProperty<String> orderNumber = new BOMasterNodeProperty<String>("orderNumber", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> orderReference = new BOMasterNodeProperty<String>("orderReference", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<Date> date = new BOMasterNodeProperty<Date>("date", Date.class,new Date(), CommonBundleResolver.class);
//        BOMasterNodeProperty<String> state = new BOMasterNodeProperty<String>("state", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
//            orderNumber,
            orderReference, date
//                    , state
        };

        return props;
    }
}
