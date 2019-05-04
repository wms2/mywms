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
import de.linogistix.los.inventory.model.LOSAdviceState;
import de.linogistix.los.inventory.query.dto.LOSAdviceTO;
import de.linogistix.los.query.BODTO;
import java.beans.IntrospectionException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 * A {@link BOMasterDevice} for BasicEnity {@link Device}.
 *
 * @author trautm
 */
public class BOLOSAdviceMasterNode extends BOMasterNode {

    LOSAdviceTO to;

    /** Creates a new instance of BODeviceNode */
    public BOLOSAdviceMasterNode(BODTO d) throws IntrospectionException {
        super(d);
        to = (LOSAdviceTO) d;
    }
    
    /** Creates a new instance of BODeviceNode */
    public BOLOSAdviceMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (LOSAdviceTO) d;
    }

    @Override
    public String getHtmlDisplayName() {
        String ret = getDisplayName();
        GregorianCalendar now = new GregorianCalendar();
        GregorianCalendar today = new GregorianCalendar(now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH));
        if(to.state != null){    
            if (to.state.equals(LOSAdviceState.FINISHED.toString())) {
                ret = "<font color=\"#C0C0C0\"><s>" + ret + "</s></font>";
            } 
            else if (to.state.equals(LOSAdviceState.OVERLOAD.toString())) {
                ret = "<font color=\"#000000\"><s>" + ret + "</s></font>";
            } 
            else if (to.expectedDelivery != null && to.expectedDelivery.before(today.getTime())) {
                ret = "<font color=\"#FF0000\">" + ret + "</font>";
            }
        }
        return ret;
    }

    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            BOMasterNodeProperty<String> client;
            client = new BOMasterNodeProperty<String>("client", String.class, to.client, InventoryBundleResolver.class);
            sheet.put(client);
            BOMasterNodeProperty<String> itemData;
            itemData = new BOMasterNodeProperty<String>("itemData", String.class, to.itemData, InventoryBundleResolver.class);
            sheet.put(itemData);
            BOMasterNodeProperty<String> itemDataName;
            itemDataName = new BOMasterNodeProperty<String>("itemDataName", String.class, to.itemDataName, InventoryBundleResolver.class);
            sheet.put(itemDataName);
            BOMasterNodeProperty<String> lot;
            lot = new BOMasterNodeProperty<String>("lot", String.class, to.lot, InventoryBundleResolver.class);
            sheet.put(lot);
            BOMasterNodeProperty<BigDecimal> receiptAmount;
            receiptAmount = new BOMasterNodeProperty<BigDecimal>("receiptAmount", BigDecimal.class, to.receiptAmount, InventoryBundleResolver.class);
            sheet.put(receiptAmount);
            BOMasterNodeProperty<BigDecimal> notifiedAmount;
            notifiedAmount = new BOMasterNodeProperty<BigDecimal>("notifiedAmount", BigDecimal.class, to.notifiedAmount, InventoryBundleResolver.class);
            sheet.put(notifiedAmount);
            BOMasterNodeProperty<Date> expDelivery;
            expDelivery = new BOMasterNodeProperty<Date>("expectedDelivery",Date.class, to.expectedDelivery, InventoryBundleResolver.class);
            sheet.put(expDelivery);
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
        BOMasterNodeProperty<String> client;
        client = new BOMasterNodeProperty<String>("client", String.class, "", InventoryBundleResolver.class);

        BOMasterNodeProperty<String> itemData;
        itemData = new BOMasterNodeProperty<String>("itemData", String.class, "", InventoryBundleResolver.class);

        BOMasterNodeProperty<String> itemDataName;
        itemDataName = new BOMasterNodeProperty<String>("itemDataName", String.class, "", InventoryBundleResolver.class);

        BOMasterNodeProperty<String> lot;
        lot = new BOMasterNodeProperty<String>("lot", String.class, "", InventoryBundleResolver.class);

        BOMasterNodeProperty<BigDecimal> receiptAmount;
        receiptAmount = new BOMasterNodeProperty<BigDecimal>("receiptAmount", BigDecimal.class, new BigDecimal(0), InventoryBundleResolver.class);

        BOMasterNodeProperty<BigDecimal> notifiedAmount;
        notifiedAmount = new BOMasterNodeProperty<BigDecimal>("notifiedAmount", BigDecimal.class, new BigDecimal(0), InventoryBundleResolver.class);

        BOMasterNodeProperty<Date> expDelivery;
        expDelivery = new BOMasterNodeProperty<Date>("expectedDelivery", Date.class, new Date(), InventoryBundleResolver.class);

        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            client, itemData, itemDataName, lot, notifiedAmount, receiptAmount, expDelivery
        };

        return props;
    }
}
