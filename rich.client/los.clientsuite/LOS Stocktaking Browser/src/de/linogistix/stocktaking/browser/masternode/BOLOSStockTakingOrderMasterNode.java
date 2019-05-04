/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.stocktaking.browser.masternode;

import de.linogistix.common.bobrowser.bo.*;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.stocktaking.model.LOSStocktakingState;
import de.linogistix.los.stocktaking.query.dto.StockTakingOrderTO;
import de.linogistix.stocktaking.res.StocktakingBundleResolver;
import java.beans.IntrospectionException;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;

/**
 * A {@link BOMasterDevice} for BasicEnity {@link Device}.
 *
 * @author trautm
 */
public class BOLOSStockTakingOrderMasterNode extends BOMasterNode {

    StockTakingOrderTO to;

    /** Creates a new instance of BODeviceNode */
    public BOLOSStockTakingOrderMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (StockTakingOrderTO) d;
    }
    
    @Override
    public String getHtmlDisplayName() {
        String ret = getDisplayName();
        
        if ( LOSStocktakingState.COUNTED.name().equals(to.getState()) ) {
            ret = "<font color=\"#E3170D\"><b>" + ret + "</b></font>";
        }
        
        if ( LOSStocktakingState.FINISHED.name().equals(to.getState()) ) {
            ret = "<font color=\"#4CBB17\"><b>" + ret + "</b></font>";
        }
        
        return ret;
    }
    
    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            BOMasterNodeProperty<String> storageLocation = new BOMasterNodeProperty<String>("locationName", String.class, to.locationName, StocktakingBundleResolver.class);
            sheet.put(storageLocation);
            BOMasterNodeProperty<String> unitLoadLabel = new BOMasterNodeProperty<String>("unitLoadLabel", String.class, to.unitLoadLabel, StocktakingBundleResolver.class);
            sheet.put(unitLoadLabel);
            BOMasterNodeProperty<String> operator = new BOMasterNodeProperty<String>("operator", String.class, to.operator, StocktakingBundleResolver.class);
            sheet.put(operator);
            BOMasterNodeProperty<String> state = new BOMasterNodeProperty<String>("state", String.class, NbBundle.getMessage(StocktakingBundleResolver.class, LOSStocktakingState.class.getSimpleName() + "." + to.state), StocktakingBundleResolver.class);
            sheet.put(state);
            
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {

        
        BOMasterNodeProperty<String> storageLocation = new BOMasterNodeProperty<String>("locationName", String.class, "", StocktakingBundleResolver.class);
            
        BOMasterNodeProperty<String> unitLoadLabel = new BOMasterNodeProperty<String>("unitLoadLabel", String.class, "", StocktakingBundleResolver.class);
            
        BOMasterNodeProperty<String> operator = new BOMasterNodeProperty<String>("operator", String.class, "", StocktakingBundleResolver.class);
            
        BOMasterNodeProperty<String> state = new BOMasterNodeProperty<String>("state", String.class, "", StocktakingBundleResolver.class);
        
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            storageLocation, unitLoadLabel, operator, state
        };

        return props;
    }
}
