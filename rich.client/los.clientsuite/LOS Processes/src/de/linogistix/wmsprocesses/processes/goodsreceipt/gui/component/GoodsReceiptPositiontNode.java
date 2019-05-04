/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.goodsreceipt.gui.component;

import de.linogistix.common.bobrowser.bo.BOMasterNode.BOMasterNodeProperty;
import de.linogistix.common.bobrowser.bo.editor.PropertyRW;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import java.math.BigDecimal;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node.Property;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;

/**
 *
 * @author trautm
 */
class GoodsReceiptPositiontNode extends AbstractNode {

    static final String LOT = NbBundle.getMessage(WMSProcessesBundleResolver.class, "lot");
    static final String ITEM = NbBundle.getMessage(WMSProcessesBundleResolver.class, "itemData");
    static final String AMOUNT = NbBundle.getMessage(WMSProcessesBundleResolver.class, "amount");
    static final String UNITLOAD = NbBundle.getMessage(WMSProcessesBundleResolver.class, "unitLoad");
    static final String STATE = NbBundle.getMessage(WMSProcessesBundleResolver.class, "state");
    
    Sheet.Set sheet = null;
    BOMasterNodeProperty unitLoad;
    BOMasterNodeProperty lot;
    BOMasterNodeProperty itemData;
    BOMasterNodeProperty<String> state;
    BOMasterNodeProperty<BigDecimal> amount;
    LOSGoodsReceiptPosition pos;

   
    public GoodsReceiptPositiontNode(LOSGoodsReceiptPosition pos, LOSAdvice advice) {
        super(Children.LEAF);
        this.pos = pos;
        this.setName(pos.getPositionNumber());
    }

    @Override
    public PropertySet[] getPropertySets() {
        if (sheet == null) {
            sheet = new Sheet.Set();

            unitLoad = new BOMasterNodeProperty<String>("unitLoad", String.class, UNITLOAD, "",pos.getUnitLoad());
            lot = new BOMasterNodeProperty<String>("lot", String.class, LOT, "", pos.getLot());
            itemData = new BOMasterNodeProperty<String>("itemData", String.class, ITEM, "",  pos.getItemData());
//            amount = new PropertyRW<BigDecimal>("amount", BigDecimal.class, AMOUNT, pos.getAmount());
            amount = new BOMasterNodeProperty<BigDecimal>("amount", BigDecimal.class, AMOUNT, "", pos.getAmount());
            if(pos.getState() != null){
                state = new BOMasterNodeProperty<String>("state", String.class, STATE, "", NbBundle.getMessage(CommonBundleResolver.class,"LOSGoodsReceiptState."+pos.getState().name()));
            }
            else{
                state = new BOMasterNodeProperty<String>("state", String.class, STATE, "", "");
            }
            
            sheet.put(unitLoad);
            sheet.put(lot);
            sheet.put(itemData);
            sheet.put(amount);
            sheet.put(state);

        }
        return new PropertySet[]{sheet};
    }

    @SuppressWarnings("unchecked")
    public static Property[] templateProperties() {

        Property[] ret = new Property[]{
            new BOMasterNodeProperty<String>("unitLoad", String.class, UNITLOAD, "",null),
            new BOMasterNodeProperty<String>("lot", String.class, LOT, "",null),
            new BOMasterNodeProperty<String>("itemData", String.class, ITEM, "", null),
            new PropertyRW<BigDecimal>("amount", BigDecimal.class, AMOUNT, new BigDecimal(0)),
            new BOMasterNodeProperty<String>("state", String.class, STATE, "",null)
        };
        return ret;

    }

    @Override
    public boolean equals(Object obj) {
        GoodsReceiptPositiontNode n;
        if (obj != null) {
            return false;
        }
        if (obj instanceof GoodsReceiptPositiontNode) {
            n = (GoodsReceiptPositiontNode) obj;
            return pos.equals(n.pos);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return pos.hashCode();
    }

   

    
}

