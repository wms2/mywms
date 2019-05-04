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
import de.linogistix.los.inventory.query.dto.LOSReplenishOrderTO;
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
public class BOLOSReplenishOrderMasterNode extends BOMasterNode {

    LOSReplenishOrderTO to;

    /** Creates a new instance of BODeviceNode */
    public BOLOSReplenishOrderMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (LOSReplenishOrderTO) d;
    }

    @Override
    public String getHtmlDisplayName() {
        String ret = getDisplayName();
        
        if(to.getState() >= State.FINISHED ) {
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
            BOMasterNodeProperty<String> clientNumber = new BOMasterNodeProperty<String>("clientNumber", String.class, to.getClientNumber(), InventoryBundleResolver.class);
            sheet.put(clientNumber);
            BOMasterNodeProperty<String> itemNumber = new BOMasterNodeProperty<String>("itemNumber", String.class, to.getItemDataNumber(), CommonBundleResolver.class);
            sheet.put(itemNumber);
            BOMasterNodeProperty<String> itemName = new BOMasterNodeProperty<String>("itemName", String.class, to.getItemDataName(), CommonBundleResolver.class);
            sheet.put(itemName);
            BOMasterNodeProperty<String> sourceLocationName = new BOMasterNodeProperty<String>("sourceLocationName", String.class, to.getSourceLocationName(), CommonBundleResolver.class);
            sheet.put(sourceLocationName);
            BOMasterNodeProperty<String> destinationLocationName = new BOMasterNodeProperty<String>("destinationLocationName", String.class, to.getDestinationLocationName(), CommonBundleResolver.class);
            sheet.put(destinationLocationName);
            BOMasterNodeProperty<BigDecimal> amount = new BOMasterNodeProperty<BigDecimal>("amount", BigDecimal.class, to.getAmount(), InventoryBundleResolver.class);
            sheet.put(amount);

            String strState = BundleResolve.resolve(new Class[]{bo.getBundleResolver(),CommonBundleResolver.class},"state."+to.getState(), new Object[0], false);
            if( strState == null || strState.length()==0 ) {
                strState = String.valueOf(to.getState());
            }
            BOMasterNodeProperty<String> state = new BOMasterNodeProperty<String>("state", String.class, strState, null);
            sheet.put(state);

                        String strPrio = BundleResolve.resolve(new Class[]{bo.getBundleResolver(),CommonBundleResolver.class},"prio."+to.getPrio(), new Object[0], false);
            if( strPrio == null || strPrio.length()==0 ) {
                strPrio = String.valueOf(to.getPrio());
            }
            BOMasterNodeProperty<String> prio = new BOMasterNodeProperty<String>("prio", String.class, strPrio, CommonBundleResolver.class);
            sheet.put(prio);

        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {

        BOMasterNodeProperty<String> clientNumber = new BOMasterNodeProperty<String>("clientNumber", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> itemNumber = new BOMasterNodeProperty<String>("itemNumber", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> itemName = new BOMasterNodeProperty<String>("itemName", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> sourceLocationName = new BOMasterNodeProperty<String>("sourceLocationName", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> destinationLocationName = new BOMasterNodeProperty<String>("destinationLocationName", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<BigDecimal> amount = new BOMasterNodeProperty<BigDecimal>("amount", BigDecimal.class, BigDecimal.ZERO, InventoryBundleResolver.class);
        BOMasterNodeProperty<String> state = new BOMasterNodeProperty<String>("state", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> prio = new BOMasterNodeProperty<String>("prio", String.class, "", CommonBundleResolver.class);
        return new Property[]{clientNumber, itemNumber, itemName, amount, sourceLocationName, destinationLocationName, state, prio};
    }
}
