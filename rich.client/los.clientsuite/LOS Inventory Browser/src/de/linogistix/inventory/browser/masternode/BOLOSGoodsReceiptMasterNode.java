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
import de.linogistix.los.inventory.query.dto.LOSGoodsReceiptTO;
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
public class BOLOSGoodsReceiptMasterNode extends BOMasterNode {

    LOSGoodsReceiptTO to;

    /** Creates a new instance of BODeviceNode */
    public BOLOSGoodsReceiptMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (LOSGoodsReceiptTO) d;
    }

    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            BOMasterNodeProperty<String> clientNumber = new BOMasterNodeProperty<String>("clientNumber", String.class, to.getClientNumber(), InventoryBundleResolver.class);
            sheet.put(clientNumber);
            BOMasterNodeProperty<String> deliveryNoteNumber = new BOMasterNodeProperty<String>("deliveryNoteNumber", String.class, to.getDeliveryNoteNumber(), CommonBundleResolver.class);
            sheet.put(deliveryNoteNumber);
            BOMasterNodeProperty<Date> receiptDate = new BOMasterNodeProperty<Date>("receiptDate", Date.class, to.getReceiptDate(), CommonBundleResolver.class);
            sheet.put(receiptDate);
            BOMasterNodeProperty<String> receiptState = new BOMasterNodeProperty<String>("receiptState",String.class, "LOSGoodsReceiptState." + to.getReceiptState(), CommonBundleResolver.class, true);
            sheet.put(receiptState);
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
            BOMasterNodeProperty<String> clientNumber = new BOMasterNodeProperty<String>("clientNumber", String.class, "", InventoryBundleResolver.class);

            BOMasterNodeProperty<String> deliveryNoteNumber = new BOMasterNodeProperty<String>("deliveryNoteNumber", String.class, "", CommonBundleResolver.class);
            
            BOMasterNodeProperty<Date> receiptDate = new BOMasterNodeProperty<Date>("receiptDate", Date.class, new Date(), CommonBundleResolver.class);
            
            BOMasterNodeProperty<String> receiptState = new BOMasterNodeProperty<String>("receiptState",String.class, "", CommonBundleResolver.class);
            
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            clientNumber, deliveryNoteNumber, receiptDate, receiptState
        };

        return props;
    }
}
