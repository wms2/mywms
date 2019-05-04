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
import de.linogistix.common.util.BundleResolve;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.query.dto.LOSPickingPositionTO;

import de.linogistix.los.model.State;
import de.linogistix.los.query.BODTO;
import java.awt.Image;
import java.beans.IntrospectionException;
import java.math.BigDecimal;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 * A {@link BOMasterDevice} for BasicEnity {@link Device}.
 *
 * @author trautm
 */
public class BOPickingPositionMasterNode extends BOMasterNode {

    LOSPickingPositionTO to;

    /** Creates a new instance of BODeviceNode */
    public BOPickingPositionMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (LOSPickingPositionTO) d;
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
            BOMasterNodeProperty<String> pickingOrderNumber = new BOMasterNodeProperty<String>("pickingOrderNumber", String.class, to.getPickingOrderNumber(), InventoryBundleResolver.class);
            sheet.put(pickingOrderNumber);
            BOMasterNodeProperty<String> itemDataNumber = new BOMasterNodeProperty<String>("itemDataNumber", String.class, to.getItemDataNumber(), InventoryBundleResolver.class);
            sheet.put(itemDataNumber);
            BOMasterNodeProperty<String> itemDataName = new BOMasterNodeProperty<String>("itemDataName", String.class, to.getItemDataName(), InventoryBundleResolver.class);
            sheet.put(itemDataName);
            BOMasterNodeProperty<BigDecimal> amount = new BOMasterNodeProperty<BigDecimal>("amount", BigDecimal.class, to.getAmount(), InventoryBundleResolver.class);
            sheet.put(amount);
//            BOMasterNodeProperty<BigDecimal> amountPicked = new BOMasterNodeProperty<BigDecimal>("amountPicked", BigDecimal.class, to.getAmountPicked(), InventoryBundleResolver.class);
//            sheet.put(amountPicked);
            BOMasterNodeProperty<String> pickFromLocationName = new BOMasterNodeProperty<String>("pickFromLocationName", String.class, to.getPickFromLocationName(), InventoryBundleResolver.class);
            sheet.put(pickFromLocationName);
//            BOMasterNodeProperty<String> pickFromUnitLoadLabel = new BOMasterNodeProperty<String>("pickFromUnitLoadLabel", String.class, to.getPickFromUnitLoadLabel(), InventoryBundleResolver.class);
//            sheet.put(pickFromUnitLoadLabel);
//
//            String strType = BundleResolve.resolve(new Class[]{bo.getBundleResolver(),CommonBundleResolver.class},"pickingType."+to.getPickingType(), new Object[0], false);
//            if( strType == null || strType.length()==0 ) {
//                strType = ""+to.getPickingType();
//            }
//            BOMasterNodeProperty<String> type = new BOMasterNodeProperty<String>("type", String.class, strType, CommonBundleResolver.class);
//            sheet.put(type);

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

//            BOMasterNodeProperty<String> clientNumber = new BOMasterNodeProperty<String>("clientNumber", String.class, "", InventoryBundleResolver.class);
            BOMasterNodeProperty<String> pickingOrderNumber = new BOMasterNodeProperty<String>("pickingOrderNumber", String.class, "", InventoryBundleResolver.class);
            BOMasterNodeProperty<String> itemDataNumber = new BOMasterNodeProperty<String>("itemDataNumber", String.class, "", InventoryBundleResolver.class);
            BOMasterNodeProperty<String> itemDataName = new BOMasterNodeProperty<String>("itemDataName", String.class, "", InventoryBundleResolver.class);
            BOMasterNodeProperty<BigDecimal> amount = new BOMasterNodeProperty<BigDecimal>("amount", BigDecimal.class, new BigDecimal(0), InventoryBundleResolver.class);
//            BOMasterNodeProperty<BigDecimal> amountPicked = new BOMasterNodeProperty<BigDecimal>("amountPicked", BigDecimal.class, new BigDecimal(0), InventoryBundleResolver.class);
            BOMasterNodeProperty<String> pickFromLocationName = new BOMasterNodeProperty<String>("pickFromLocationName", String.class, "", InventoryBundleResolver.class);
//            BOMasterNodeProperty<String> pickFromUnitLoadLabel = new BOMasterNodeProperty<String>("pickFromUnitLoadLabel", String.class, "", InventoryBundleResolver.class);
//            BOMasterNodeProperty<String> type = new BOMasterNodeProperty<String>("type", String.class, "", CommonBundleResolver.class);
            BOMasterNodeProperty<String> state = new BOMasterNodeProperty<String>("state", String.class, "", CommonBundleResolver.class);
            
        return new Property[]{pickingOrderNumber, itemDataNumber, itemDataName, amount, pickFromLocationName, state};
    }
}
