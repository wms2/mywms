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
import de.linogistix.los.stocktaking.query.dto.StockTakingRecordTO;
import de.linogistix.stocktaking.res.StocktakingBundleResolver;
import java.beans.IntrospectionException;
import java.math.BigDecimal;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;

/**
 * A {@link BOMasterDevice} for BasicEnity {@link Device}.
 *
 * @author trautm
 */
public class BOLOSStockTakingRecordMasterNode extends BOMasterNode {

    StockTakingRecordTO to;

    /** Creates a new instance of BODeviceNode */
    public BOLOSStockTakingRecordMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (StockTakingRecordTO) d;
    }
     
    @Override
    public String getHtmlDisplayName() {
        String ret = getDisplayName();
        
        if (LOSStocktakingState.COUNTED.name().equals(to.getState()) ) {
            ret = "<font color=\"#E3170D\"><b>" + ret + "</b></font>";
        }
        
        if (LOSStocktakingState.FINISHED.name().equals(to.getState()) ) {
            ret = "<font color=\"#4CBB17\"><b>" + ret + "</b></font>";
        }
        
        return ret;
    }
    
    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            BOMasterNodeProperty<String> clientNo = new BOMasterNodeProperty<String>("clientNo", String.class, to.clientNo, StocktakingBundleResolver.class); 
            sheet.put(clientNo);
            BOMasterNodeProperty<String> unitLoad = new BOMasterNodeProperty<String>("unitLoad", String.class, to.unitLoad, StocktakingBundleResolver.class);
            sheet.put(unitLoad);
            BOMasterNodeProperty<String> itemNo = new BOMasterNodeProperty<String>("itemNo", String.class, to.itemNo, StocktakingBundleResolver.class);
            sheet.put(itemNo);
            BOMasterNodeProperty<String> lotNo = new BOMasterNodeProperty<String>("lotNo", String.class, to.lotNo, StocktakingBundleResolver.class);
            sheet.put(lotNo);
            BOMasterNodeProperty<BigDecimal> plannedQuantity = new BOMasterNodeProperty<BigDecimal>("plannedQuantity", BigDecimal.class, to.plannedQuantity, StocktakingBundleResolver.class);
            sheet.put(plannedQuantity);
            BOMasterNodeProperty<BigDecimal> countedQuantity = new BOMasterNodeProperty<BigDecimal>("countedQuantity", BigDecimal.class, to.countedQuantity, StocktakingBundleResolver.class);
            sheet.put(countedQuantity);
            BOMasterNodeProperty<String> state = new BOMasterNodeProperty<String>("state", String.class, NbBundle.getMessage(StocktakingBundleResolver.class, LOSStocktakingState.class.getSimpleName() + "." + to.state), StocktakingBundleResolver.class);
            sheet.put(state);
            
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
        BOMasterNodeProperty<String> clientNo = new BOMasterNodeProperty<String>("clientNo", String.class, "", StocktakingBundleResolver.class);
        
        BOMasterNodeProperty<String> unitLoadLabel = new BOMasterNodeProperty<String>("unitLoad", String.class, "", StocktakingBundleResolver.class);
            
        BOMasterNodeProperty<String> itemNo = new BOMasterNodeProperty<String>("itemNo", String.class, "", StocktakingBundleResolver.class);

        BOMasterNodeProperty<String> lotNo = new BOMasterNodeProperty<String>("lotNo", String.class, "", StocktakingBundleResolver.class);

        BOMasterNodeProperty<BigDecimal> plannedQuantity = new BOMasterNodeProperty<BigDecimal>("plannedQuantity", BigDecimal.class, BigDecimal.ZERO, StocktakingBundleResolver.class);

        BOMasterNodeProperty<BigDecimal> countedQuantity = new BOMasterNodeProperty<BigDecimal>("countedQuantity", BigDecimal.class, BigDecimal.ZERO, StocktakingBundleResolver.class);

        BOMasterNodeProperty<String> state = new BOMasterNodeProperty<String>("state", String.class, "", StocktakingBundleResolver.class);
        
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            clientNo, unitLoadLabel, itemNo, lotNo, plannedQuantity, countedQuantity, state
        };

        return props;
    }
}
