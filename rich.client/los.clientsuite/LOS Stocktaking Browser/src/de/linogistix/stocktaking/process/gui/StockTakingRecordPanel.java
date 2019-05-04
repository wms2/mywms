/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.stocktaking.process.gui;


import de.linogistix.los.stocktaking.model.LOSStocktakingOrder;
import de.linogistix.los.stocktaking.model.LOSStocktakingRecord;
import de.linogistix.stocktaking.browser.masternode.BOLOSStockTakingRecordMasterNode;
import de.linogistix.stocktaking.process.StockTakingQueryTopComponent;
import java.awt.BorderLayout;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.TreeTableView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;

/**
 *
 * @author artur
 */
public class StockTakingRecordPanel extends StockTakingRecordAbstractPanel {

    private Logger log = Logger.getLogger(StockTakingRecordPanel.class.getName());
        
    ExplorerManager manager;
    
    LOSStocktakingOrder selectedOrder;
    
    AbstractNode root;
    
    StockTakingQueryTopComponent topComponent;
            
    public StockTakingRecordPanel(StockTakingQueryTopComponent topComponent) {
        this.topComponent = topComponent;
        postInit();
    }
    //-----------------------------------------------------------------------
    
    protected void postInit() {
        
        initBeanView();
    }
 
    public void onSelectionCleared(){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                selectedOrder = null;
                LOSStockTakingRecordNode node = new LOSStockTakingRecordNode( new ArrayList<LOSStocktakingRecord>() );
                getExplorerManager().setRootContext(node);
                try {
                    getExplorerManager().setSelectedNodes(new Node[]{node});
                } catch (PropertyVetoException ex) {
                    log.log(Level.SEVERE,ex.getMessage(), ex);
                }
            }
        });       
    }
    
    public void onSelectionChanged(final LOSStocktakingOrder order){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                selectedOrder = topComponent.getController().getOrder(order.getId());
                LOSStockTakingRecordNode node = new LOSStockTakingRecordNode(order.getRecords());
                getExplorerManager().setRootContext(node);
                try {
                    getExplorerManager().setSelectedNodes(new Node[]{node});
                } catch (PropertyVetoException ex) {
                    log.log(Level.SEVERE,ex.getMessage(), ex);
                }
            }
        });       
    }
    
    protected  void initBeanView(){
        beanView = new TreeTableView();
        beanView.setProperties(BOLOSStockTakingRecordMasterNode.boMasterNodeProperties());
        beanView.setRootVisible(false);
        stockRecordViewPanel.add(beanView, BorderLayout.CENTER);
           
    }
    

    public ExplorerManager getExplorerManager() {
        if (this.manager == null) {
            this.manager = new ExplorerManager();
        }
        return this.manager;
    }

}
