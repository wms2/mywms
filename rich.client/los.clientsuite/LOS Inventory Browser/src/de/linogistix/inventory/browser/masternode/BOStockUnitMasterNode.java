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

import de.linogistix.los.inventory.query.dto.StockUnitTO;
import de.linogistix.los.inventory.service.StockUnitLockState;
import de.linogistix.los.query.BODTO;
import java.beans.IntrospectionException;
import java.math.BigDecimal;
import java.util.GregorianCalendar;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 * A {@link BOMasterDevice} for BasicEnity {@link Device}.
 *
 * @author trautm
 */
public class BOStockUnitMasterNode extends BOMasterNode {

    StockUnitTO to;

    /** Creates a new instance of BODeviceNode */
    public BOStockUnitMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (StockUnitTO) d;
    }

    @Override
    public String getHtmlDisplayName() {
        String ret = getDisplayName();
        GregorianCalendar today = new GregorianCalendar();
        if (to.lock == StockUnitLockState.LOT_EXPIRED.getLock()) {
            ret = "<font color=\"#FF0000\"><s>" + ret + "</s></font>";
        }

        return ret;
    }

    //    @Override
//    public Image getIcon(int arg0) {
//        if (to.lock > 0){
//            ImageIcon img = new ImageIcon(super.getIcon(arg0));
//            LockedIcon decorated = new LockedIcon(img);
//            return decorated.getImage();
//        } else {
//            return super.getIcon(arg0);
//        }
//    }
    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            BOMasterNodeProperty<String> itemData = new BOMasterNodeProperty<String>("itemData", String.class, to.itemData, CommonBundleResolver.class);
            sheet.put(itemData);
            BOMasterNodeProperty<String> itemDataName = new BOMasterNodeProperty<String>("itemDataName", String.class, to.itemDataName, CommonBundleResolver.class);
            sheet.put(itemDataName);
            BOMasterNodeProperty<String> lot = new BOMasterNodeProperty<String>("lot", String.class, to.lot, CommonBundleResolver.class);
            sheet.put(lot);
            BOMasterNodeProperty<String> unitLoad = new BOMasterNodeProperty<String>("unitLoad", String.class, to.unitLoad, CommonBundleResolver.class);
            sheet.put(unitLoad);
            BOMasterNodeProperty<String> storageLocation = new BOMasterNodeProperty<String>("storageLocation", String.class, to.storageLocation, CommonBundleResolver.class);
            sheet.put(storageLocation);
            BOMasterNodeProperty<BigDecimal> amount = new BOMasterNodeProperty<BigDecimal>("amount", BigDecimal.class, to.amount, CommonBundleResolver.class);
            sheet.put(amount);
            BOMasterNodeProperty<BigDecimal> reservedAmount = new BOMasterNodeProperty<BigDecimal>("reservedAmount", BigDecimal.class, to.reservedAmount, CommonBundleResolver.class);
            sheet.put(reservedAmount);
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
        BOMasterNodeProperty<String> itemData = new BOMasterNodeProperty<String>("itemData", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> itemDataName = new BOMasterNodeProperty<String>("itemDataName", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> lot = new BOMasterNodeProperty<String>("lot", String.class,"", CommonBundleResolver.class);

        BOMasterNodeProperty<String> unitLoad = new BOMasterNodeProperty<String>("unitLoad", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> storageLocation = new BOMasterNodeProperty<String>("storageLocation", String.class, "", CommonBundleResolver.class);
        
        BOMasterNodeProperty<BigDecimal> amount = new BOMasterNodeProperty<BigDecimal>("amount", BigDecimal.class,  new BigDecimal(0), CommonBundleResolver.class);
        BOMasterNodeProperty<BigDecimal> reservedAmount = new BOMasterNodeProperty<BigDecimal>("reservedAmount", BigDecimal.class,  new BigDecimal(0), CommonBundleResolver.class);

        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            itemData, itemDataName, lot, unitLoad, storageLocation, amount, reservedAmount
        };

        return props;
    }
    
    public static Property[] boMasterNodePropertiesAll() {
        BOMasterNodeProperty<String> itemData = new BOMasterNodeProperty<String>("itemData", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> itemDataName = new BOMasterNodeProperty<String>("itemDataName", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> lot = new BOMasterNodeProperty<String>("lot", String.class,"", CommonBundleResolver.class);

        BOMasterNodeProperty<String> unitLoad = new BOMasterNodeProperty<String>("unitLoad", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> storageLocation = new BOMasterNodeProperty<String>("storageLocation", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<Integer> amount = new BOMasterNodeProperty<Integer>("amount", Integer.class, 0, CommonBundleResolver.class);
        BOMasterNodeProperty<Integer> reservedAmount = new BOMasterNodeProperty<Integer>("reservedAmount", Integer.class, 0, CommonBundleResolver.class);

        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            itemData, itemDataName, lot, unitLoad, storageLocation, amount, reservedAmount
        };

        return props;
    }
}
