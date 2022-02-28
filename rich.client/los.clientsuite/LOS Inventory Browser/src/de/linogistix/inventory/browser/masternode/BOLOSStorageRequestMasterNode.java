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
import de.linogistix.los.inventory.query.dto.LOSStorageRequestTO;
import de.linogistix.los.query.BODTO;
import java.beans.IntrospectionException;
import java.math.BigDecimal;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 * A {@link BOMasterDevice} for BasicEnity {@link Device}.
 *
 * @author trautm
 */
public class BOLOSStorageRequestMasterNode extends BOMasterNode {

    LOSStorageRequestTO to;

    /** Creates a new instance of BODeviceNode */
    public BOLOSStorageRequestMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (LOSStorageRequestTO) d;
    }

    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            BOMasterNodeProperty<String> clientNumber = new BOMasterNodeProperty<String>("clientNumber", String.class, to.getClientNumber(), InventoryBundleResolver.class);
            sheet.put(clientNumber);
            String strOrderType = BundleResolve.resolve(new Class[]{bo.getBundleResolver(),CommonBundleResolver.class},"TransportOrder.orderType."+to.getOrderType(), new Object[0], false);
            if( strOrderType == null || strOrderType.length()==0 ) {
                strOrderType = String.valueOf(to.getOrderType());
            }
            BOMasterNodeProperty<String> orderType = new BOMasterNodeProperty<String>("orderType", String.class, strOrderType, null);
            sheet.put(orderType);
            BOMasterNodeProperty<String> unitLoadLabel = new BOMasterNodeProperty<String>("unitLoadLabel", String.class, to.getUnitLoadLabel(), CommonBundleResolver.class);
            sheet.put(unitLoadLabel);
            BOMasterNodeProperty<String> sourceLocationName = new BOMasterNodeProperty<String>("sourceLocationName", String.class, to.getSourceLocationName(), CommonBundleResolver.class);
            sheet.put(sourceLocationName);
            BOMasterNodeProperty<String> destinationLocationName = new BOMasterNodeProperty<String>("destinationLocationName", String.class, to.getDestinationLocationName(), CommonBundleResolver.class);
            sheet.put(destinationLocationName);
            BOMasterNodeProperty<String> itemNumber = new BOMasterNodeProperty<String>("itemNumber", String.class, to.getItemDataNumber(), CommonBundleResolver.class);
            sheet.put(itemNumber);
            BOMasterNodeProperty<String> itemName = new BOMasterNodeProperty<String>("itemName", String.class, to.getItemDataName(), CommonBundleResolver.class);
            sheet.put(itemName);

            String strState = BundleResolve.resolve(new Class[]{bo.getBundleResolver(),CommonBundleResolver.class},"state."+to.getState(), new Object[0], false);
            if( strState == null || strState.length()==0 ) {
                strState = String.valueOf(to.getState());
            }
            BOMasterNodeProperty<String> state = new BOMasterNodeProperty<String>("state", String.class, strState, null);
            sheet.put(state);
        }
        return new PropertySet[]{sheet};
    }

    public static Property[] boMasterNodeProperties() {

        BOMasterNodeProperty<String> clientNumber = new BOMasterNodeProperty<String>("clientNumber", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> orderType = new BOMasterNodeProperty<String>("orderType", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> unitLoadLabel = new BOMasterNodeProperty<String>("unitLoadLabel", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> sourceLocationName = new BOMasterNodeProperty<String>("sourceLocationName", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> destinationLocationName = new BOMasterNodeProperty<String>("destinationLocationName", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> itemNumber = new BOMasterNodeProperty<String>("itemNumber", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> itemName = new BOMasterNodeProperty<String>("itemName", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> state = new BOMasterNodeProperty<String>("state", String.class, "", CommonBundleResolver.class);
        return new Property[]{clientNumber, orderType, unitLoadLabel, sourceLocationName, destinationLocationName, itemNumber, itemName,  state};
    }
}
