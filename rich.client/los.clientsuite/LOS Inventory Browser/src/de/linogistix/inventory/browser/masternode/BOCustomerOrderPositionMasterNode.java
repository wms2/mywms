/*
 * Copyright (c) 2006 - 2011 LinogistiX GmbH
 *
 *  www.linogistix.com
 *
 *  Project myWMS-LOS
 */
package de.linogistix.inventory.browser.masternode;

import de.linogistix.common.bobrowser.bo.BOMasterNode;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.util.BundleResolve;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.query.dto.LOSCustomerOrderPositionTO;

import de.linogistix.los.model.State;
import de.linogistix.los.query.BODTO;
import java.awt.Image;
import java.beans.IntrospectionException;
import java.math.BigDecimal;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 *
 * @author krane
 */
public class BOCustomerOrderPositionMasterNode extends BOMasterNode {

    LOSCustomerOrderPositionTO to;

    /** Creates a new instance of BODeviceNode */
    public BOCustomerOrderPositionMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (LOSCustomerOrderPositionTO) d;
    }

    @Override
    public String getHtmlDisplayName() {
        String ret = getDisplayName();
        
        if (to.getState() >= State.FINISHED) {
             ret = "<font color=\"#C0C0C0\">" + ret + "</font>";
        }
        return ret;
    }
    
    @Override
    public Image getIcon(int arg0) {
        return super.getIcon(arg0);
    }

    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
//            BOMasterNodeProperty<String> clientNumber = new BOMasterNodeProperty<String>("clientNumber", String.class, to.getClientNumber(), InventoryBundleResolver.class);
//            sheet.put(clientNumber);
//            BOMasterNodeProperty<String> orderNumber = new BOMasterNodeProperty<String>("orderNumber", String.class, to.getOrderNumber(), InventoryBundleResolver.class);
//            sheet.put(orderNumber);
            BOMasterNodeProperty<String> item = new BOMasterNodeProperty<String>("itemData", String.class, to.getItemData(), InventoryBundleResolver.class);
            sheet.put(item);
            BOMasterNodeProperty<String> itemDataName = new BOMasterNodeProperty<String>("itemDataName", String.class, to.getItemDataName(), InventoryBundleResolver.class);
            sheet.put(itemDataName);
            BOMasterNodeProperty<String> lot = new BOMasterNodeProperty<String>("lot", String.class, to.getLot(), InventoryBundleResolver.class);
            sheet.put(lot);
            BOMasterNodeProperty<BigDecimal> amount = new BOMasterNodeProperty<BigDecimal>("amount", BigDecimal.class, to.getAmount(), InventoryBundleResolver.class);
            sheet.put(amount);
//            BOMasterNodeProperty<BigDecimal> amountPicked = new BOMasterNodeProperty<BigDecimal>("amountPicked", BigDecimal.class, to.getAmountPicked(), InventoryBundleResolver.class);
//            sheet.put(amountPicked);

            String strState = BundleResolve.resolve(new Class[]{bo.getBundleResolver(),CommonBundleResolver.class},"state."+to.getState(), new Object[0], false);
            if( strState == null || strState.length()==0 ) {
                strState = String.valueOf(to.getState());
            }
            BOMasterNodeProperty<String> state = new BOMasterNodeProperty<String>("state", String.class, strState, null);
            sheet.put(state);

        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {

//        BOMasterNodeProperty<String> clientNumber = new BOMasterNodeProperty<String>("clientNumber", String.class, "", InventoryBundleResolver.class);
//        BOMasterNodeProperty<String> orderNumber = new BOMasterNodeProperty<String>("orderNumber", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> item = new BOMasterNodeProperty<String>("itemData", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> itemDataName = new BOMasterNodeProperty<String>("itemDataName", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> lot = new BOMasterNodeProperty<String>("lot", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<BigDecimal> amount = new BOMasterNodeProperty<BigDecimal>("amount", BigDecimal.class, new BigDecimal(0), InventoryBundleResolver.class);
//        BOMasterNodeProperty<BigDecimal> amountPicked = new BOMasterNodeProperty<BigDecimal>("amountPicked", BigDecimal.class, new BigDecimal(0), InventoryBundleResolver.class);
        BOMasterNodeProperty<String> state = new BOMasterNodeProperty<String>("state", String.class, "", CommonBundleResolver.class);
            
        return new Property[]{item, itemDataName, lot, amount, state};
    }
}
