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

import de.linogistix.los.inventory.query.dto.StockUnitLabelTO;
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
public class BOStockUnitLabelMasterNode extends BOMasterNode {

    StockUnitLabelTO to;

    /** Creates a new instance of BODeviceNode */
    public BOStockUnitLabelMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (StockUnitLabelTO) d;
    }

    @Override
    public String getHtmlDisplayName() {
        String ret = getDisplayName();
        return ret;
    }

    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            BOMasterNodeProperty<String> itemdataRef = new BOMasterNodeProperty<String>("itemdataRef", String.class, to.itemdataRef, CommonBundleResolver.class);
            sheet.put(itemdataRef);
            BOMasterNodeProperty<String> lotRef = new BOMasterNodeProperty<String>("lotRef", String.class, to.lotRef, CommonBundleResolver.class);
            sheet.put(lotRef);
            BOMasterNodeProperty<String> date = new BOMasterNodeProperty<String>("expectedDelivery",String.class, to.date, CommonBundleResolver.class);
            sheet.put(date);
            BOMasterNodeProperty<BigDecimal> amount = new BOMasterNodeProperty<BigDecimal>("amount", BigDecimal.class, to.itemMeasure.getValue(), CommonBundleResolver.class);
            sheet.put(amount);
            BOMasterNodeProperty<String> itemUnit = new BOMasterNodeProperty<String>("itemUnit", String.class, to.itemMeasure.getItemUnit(), CommonBundleResolver.class);
            sheet.put(itemUnit);
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
            BOMasterNodeProperty<String> itemdataRef = new BOMasterNodeProperty<String>("itemdataRef", String.class, "", CommonBundleResolver.class);
            BOMasterNodeProperty<String> lotRef = new BOMasterNodeProperty<String>("lotRef", String.class, "", CommonBundleResolver.class);
            BOMasterNodeProperty<String> date = new BOMasterNodeProperty<String>("expectedDelivery",String.class, new Date().toString(), CommonBundleResolver.class);
            BOMasterNodeProperty<BigDecimal> amount = new BOMasterNodeProperty<BigDecimal>("amount", BigDecimal.class, new BigDecimal(0), CommonBundleResolver.class);
            BOMasterNodeProperty<String> itemUnit = new BOMasterNodeProperty<String>("itemUnit", String.class, "", CommonBundleResolver.class);

        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            itemdataRef, lotRef, date, amount, itemUnit
        };

        return props;
    }
}
