/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.treat_order;

import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.los.inventory.query.dto.LOSOrderStockUnitTO;
import de.linogistix.los.query.BODTO;
import java.math.BigDecimal;
import org.openide.nodes.Sheet;

/**
 *
 * @author Jordan
 */
public class TreatOrderStockUnitMasterNode extends BOMasterNode {

    private LOSOrderStockUnitTO stockTO;
    
    public TreatOrderStockUnitMasterNode(BODTO to, BO bo){
        super(to, bo);
        
        stockTO = (LOSOrderStockUnitTO) to;
    }
    
    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            sheet.setName(getName());
            sheet.setDisplayName(getDisplayName());
            BOMasterNodeProperty<String> lot = new BOMasterNodeProperty<String>("lot", String.class, stockTO.lot, CommonBundleResolver.class);
            sheet.put(lot);
            BOMasterNodeProperty<String> unitLoad = new BOMasterNodeProperty<String>("unitLoad", String.class, stockTO.unitLoad, CommonBundleResolver.class);
            sheet.put(unitLoad);
            BOMasterNodeProperty<String> storageLocation = new BOMasterNodeProperty<String>("storageLocation", String.class, stockTO.storageLocation, CommonBundleResolver.class);
            sheet.put(storageLocation);
            BOMasterNodeProperty<BigDecimal> availableAmount = new BOMasterNodeProperty<BigDecimal>("amount", BigDecimal.class, stockTO.availableAmount, CommonBundleResolver.class);
            sheet.put(availableAmount);
        }
        return new PropertySet[]{sheet};
    }
    
    public static Property[] boMasterNodeProperties() {
        
        BOMasterNodeProperty<String> lot = new BOMasterNodeProperty<String>("lot", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> unitLoad = new BOMasterNodeProperty<String>("unitLoad", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> storageLocation = new BOMasterNodeProperty<String>("storageLocation", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<BigDecimal> availableAmount = new BOMasterNodeProperty<BigDecimal>("amount", BigDecimal.class, new BigDecimal(0), CommonBundleResolver.class);
        
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{lot, unitLoad, storageLocation, availableAmount};
        
        return props;
    }

}
