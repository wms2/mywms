/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.stocktaking.process.gui;

import de.linogistix.los.stocktaking.model.LOSStocktakingRecord;
import de.linogistix.los.stocktaking.query.dto.StockTakingRecordTO;
import de.linogistix.stocktaking.browser.masternode.BOLOSStockTakingRecordMasterNode;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author trautm
 */
public class LOSStockTakingRecordNode extends AbstractNode{
    
    private static final Logger log = Logger.getLogger(LOSStockTakingRecordNode.class.getName());
    
    LOSStockTakingRecordNode(List<LOSStocktakingRecord> records){
        super(new LOSStockTakingRecordChildren(records));
    }
    
    static class LOSStockTakingRecordChildren extends Children.Keys<LOSStocktakingRecord>{

        List<LOSStocktakingRecord> records;
        
        LOSStockTakingRecordChildren(List<LOSStocktakingRecord> records){
            this.records = records;
        }
        
        @Override
        protected Node[] createNodes(LOSStocktakingRecord rec) {
            BOLOSStockTakingRecordMasterNode m;
            StockTakingRecordTO to = new StockTakingRecordTO(
                    rec.getId(), rec.getVersion(), rec.getId(),
                        rec.getClientNo(),
                        rec.getUnitLoadLabel(),
                        rec.getItemNo(), 
                        rec.getLotNo(),
                        rec.getPlannedQuantity(),
                        rec.getCountedQuantity(),
                        rec.getState());
            try {
                m = new BOLOSStockTakingRecordMasterNode(to, null);
                return new Node[]{m};
            } catch (IntrospectionException ex) {
                log.log(Level.SEVERE, ex.getMessage(), ex);
                return new Node[0]; 
            }
        }

        @Override
        protected void addNotify() {
            if (records.size() > 0){
                setKeys(records);
            } else{
                setKeys(new ArrayList<LOSStocktakingRecord>());
            }
        }   
        
    }
}
