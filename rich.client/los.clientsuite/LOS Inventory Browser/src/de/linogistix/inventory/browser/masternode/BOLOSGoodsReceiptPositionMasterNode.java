/*
 * Copyright (c) 2006-2012 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.inventory.browser.masternode;

import de.linogistix.common.bobrowser.bo.BOMasterNode;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.res.CommonBundleResolver;

import de.linogistix.los.inventory.query.dto.LOSGoodsReceiptPositionTO;
import de.linogistix.los.query.BODTO;
import java.beans.IntrospectionException;
import java.math.BigDecimal;
import java.util.Date;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 * A {@link BOMasterDevice} for BasicEnity {@link Device}.
 *
 * @author trautm
 */
public class BOLOSGoodsReceiptPositionMasterNode extends BOMasterNode {

    LOSGoodsReceiptPositionTO to;

    /** Creates a new instance of BODeviceNode */
    public BOLOSGoodsReceiptPositionMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (LOSGoodsReceiptPositionTO) d;
    }

    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
//            BOMasterNodeProperty<String> orderReference = new BOMasterNodeProperty<String>("orderReference", String.class, to.orderReference, CommonBundleResolver.class);
//            sheet.put(orderReference);
            BOMasterNodeProperty<String> item = new BOMasterNodeProperty<String>("itemData",String.class, to.itemData, CommonBundleResolver.class);
            sheet.put(item);
            BOMasterNodeProperty<String> lot = new BOMasterNodeProperty<String>("lot", String.class, to.lot, CommonBundleResolver.class, true);
            sheet.put(lot);
            BOMasterNodeProperty<BigDecimal> amount = new BOMasterNodeProperty<BigDecimal>("amount", BigDecimal.class, to.amount, CommonBundleResolver.class);
            sheet.put(amount);
            BOMasterNodeProperty<Integer> qaLock = new BOMasterNodeProperty<Integer>("qaLock", Integer.class, to.qaLock, CommonBundleResolver.class);
            sheet.put(qaLock);
            BOMasterNodeProperty<String> unitload = new BOMasterNodeProperty<String>("unitLoad", String.class, to.unitLoad, CommonBundleResolver.class);
            sheet.put(unitload);
//            BOMasterNodeProperty<String> operatorName = new BOMasterNodeProperty<String>("operatorName", String.class, to.operatorName, CommonBundleResolver.class);
//            sheet.put(operatorName);
//            BOMasterNodeProperty<Date> created = new BOMasterNodeProperty<Date>("created", Date.class, to.created, CommonBundleResolver.class);
//            sheet.put(created);
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
//            BOMasterNodeProperty<String> orderReference = new BOMasterNodeProperty<String>("orderReference", String.class, "", CommonBundleResolver.class);
            
            BOMasterNodeProperty<String> item = new BOMasterNodeProperty<String>("itemData",String.class, "", CommonBundleResolver.class);
            
            BOMasterNodeProperty<String> lot = new BOMasterNodeProperty<String>("lot", String.class, "", CommonBundleResolver.class, true);
            
            BOMasterNodeProperty<BigDecimal> amount = new BOMasterNodeProperty<BigDecimal>("amount", BigDecimal.class, new BigDecimal(0), CommonBundleResolver.class);

            BOMasterNodeProperty<Integer> qaLock = new BOMasterNodeProperty<Integer>("qaLock", Integer.class, 0, CommonBundleResolver.class);

            BOMasterNodeProperty<String> unitload = new BOMasterNodeProperty<String>("unitLoad", String.class, "", CommonBundleResolver.class);

//            BOMasterNodeProperty<String> operatorName = new BOMasterNodeProperty<String>("operatorName", String.class, "", CommonBundleResolver.class);
//
//            BOMasterNodeProperty<Date> created = new BOMasterNodeProperty<Date>("created", Date.class, new Date(), CommonBundleResolver.class);

            BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
                item, lot, amount, qaLock, unitload
            };

        return props;
    }
}
