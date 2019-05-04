/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.stocktaking.process;

import de.linogistix.common.bobrowser.bo.BONode;
import de.linogistix.common.bobrowser.query.BOQueryTopComponent;
import de.linogistix.common.bobrowser.query.OpenBOQueryTopComponentAction;
import de.linogistix.stocktaking.res.StocktakingBundleResolver;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.ErrorManager;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Action which shows GoodsReceipt component.
 */
public class OpenStockTakingAction extends OpenBOQueryTopComponentAction {

    private static final Logger log = Logger.getLogger(OpenStockTakingAction.class.getName());
//    public OpenStockTakingAction() {
//        super(NbBundle.getMessage(StocktakingBundleResolver.class, "CTL_OpenStockTakingAction"));
////        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(GoodsReceiptTopComponent.ICON_PATH, true)));
//    }

//    public void actionPerformed(ActionEvent evt) {
//        TopComponent win = StockTakingTopComponent.findInstance();
//        
//        Mode mode = WindowManager.getDefault().findMode("editor");
//            
//        if(mode != null){
//            mode.dockInto(win);
//        }
//        
//        win.open();
//        win.requestActive();
//    }
    protected void performAction(Node[] node) {
        BONode n;
        if (node.length == 1) {
            try {
                n = (BONode) node[0];
            } catch (ClassCastException ex) {
                throw new RuntimeException("Unexpected node: " + node[0]);
            }
        } else {
            throw new RuntimeException("Expected 1 key but got " + node.length);
        }

        try {

            log.log(Level.FINE, "Looking for " + n.getName());
            TopComponent win = WindowManager.getDefault().findTopComponent(n.getName());
            if (win == null) {
                StockTakingQueryTopComponent c = new StockTakingQueryTopComponent(n, true);
                c.setPreferredId(WindowManager.getDefault().findTopComponentID(c));
                win = c;
            }
            
            if (win instanceof BOQueryTopComponent) {
                // OK
            } else{
                 ErrorManager.getDefault().log(ErrorManager.WARNING, "There seem to be multiple components with the '" + n.getName() + "' ID. That is a potential source of errors and unexpected behavior.");
                 win = new BOQueryTopComponent(n, true);
            }

            Mode mode = WindowManager.getDefault().findMode("editor");

            if (mode != null) {
                mode.dockInto(win);
            }

            win.open();
            win.requestActive();

        } catch (Throwable t) {
            ErrorManager em = ErrorManager.getDefault();
            //em.annotate(t, t.getMessage());
            em.notify(t);
        }

    }
    
    public String getName() {
        return NbBundle.getMessage(StocktakingBundleResolver.class, "CTL_OpenStockTakingAction");
    }
}
