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

import de.linogistix.los.inventory.query.dto.LOSGoodsOutRequestTO;
import de.linogistix.los.query.BODTO;
import java.awt.Image;
import java.beans.IntrospectionException;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 * A {@link BOMasterDevice} for BasicEnity {@link Device}.
 *
 * @author trautm
 */
public class BOLOSGoodsOutRequestMasterNode extends BOMasterNode {

    LOSGoodsOutRequestTO to;

    /** Creates a new instance of BODeviceNode */
    public BOLOSGoodsOutRequestMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (LOSGoodsOutRequestTO) d;
    }

    
    @Override
    public Image getIcon(int arg0) {
        return super.getIcon(arg0);
    }

    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            BOMasterNodeProperty<String> client = new BOMasterNodeProperty<String>("clientNumber", String.class, to.getClientNumber(), InventoryBundleResolver.class);
            sheet.put(client);
            BOMasterNodeProperty<String> customerOrderNumber = new BOMasterNodeProperty<String>("customerOrderNumber", String.class, to.getCustomerOrderNumber(), InventoryBundleResolver.class);
            sheet.put(customerOrderNumber);
//            BOMasterNodeProperty<Integer> positionCount = new BOMasterNodeProperty<Integer>("numPos", Integer.class, to.getNumPos(), InventoryBundleResolver.class, true);
//            sheet.put(positionCount);
            String strState = BundleResolve.resolve(new Class[]{bo.getBundleResolver(),CommonBundleResolver.class},"state."+to.getState(), new Object[0], false);
            if( strState == null || strState.length()==0 ) {
                strState = String.valueOf(to.getState());
            }
            BOMasterNodeProperty<String> outState = new BOMasterNodeProperty<String>("state", String.class, strState, CommonBundleResolver.class);
            sheet.put(outState);
            
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {

        BOMasterNodeProperty<String> client = new BOMasterNodeProperty<String>("clientNumber", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> customerOrderNumber = new BOMasterNodeProperty<String>("customerOrderNumber", String.class, "", InventoryBundleResolver.class);
//        BOMasterNodeProperty<Integer> positionCount = new BOMasterNodeProperty<Integer>("numPos", Integer.class, 0, InventoryBundleResolver.class);
        BOMasterNodeProperty<String> outState = new BOMasterNodeProperty<String>("state", String.class, "", CommonBundleResolver.class);
        return new Property[]{client, customerOrderNumber, outState};
        
    }
}
