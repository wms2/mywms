/*
 * Copyright (c) 2006 - 2011 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.stocktaking.process;

import de.linogistix.common.bobrowser.bo.BONode;
import de.linogistix.common.bobrowser.query.BOQueryTopComponent;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.los.model.LOSCommonPropertyKey;
import de.linogistix.stocktaking.process.gui.StockTakingRecordPanel;
import de.linogistix.stocktaking.res.StocktakingBundleResolver;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 *
 * @author trautm
 */
public class StockTakingQueryTopComponent extends BOQueryTopComponent{
    StockTakingRecordPanel stockTakingProcessPanel;
    StockTakingProcessController controller;
    
    StockTakingQueryTopComponent(BONode node, boolean editableDetail){
        super(node, editableDetail);
    }

    @Override
    public int getPersistenceType() {
        J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
        if( loc.getPropertyBool(LOSCommonPropertyKey.NBCLIENT_RESTORE_TABS, true) ) {
            return TopComponent.PERSISTENCE_ONLY_OPENED;
        }
        else {
            return TopComponent.PERSISTENCE_NEVER;
        }
    }

    public StockTakingProcessController getController() {
        return controller;
    }

    @Override
    protected void postInit() {
        if (!hasBeenInitialized()){
            this.controller = new StockTakingProcessController(this);
            createTabbedPane();

            stockTakingProcessPanel = new StockTakingRecordPanel(this);
            addTab(NbBundle.getMessage(StocktakingBundleResolver.class, "Records"),getStockTakingProcessPanel());

            super.postInit();
        }
    }


    public StockTakingRecordPanel getStockTakingProcessPanel() {
        return stockTakingProcessPanel;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return  new HelpCtx("de.linogistix.stocktaking.dialog");
    }

    
}
