/*
 * Copyright (c) 2006 - 2011 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.stocktaking.process;

import de.linogistix.common.bobrowser.bo.BOEntityNode;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.services.J2EEServiceLocatorException;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;
import de.linogistix.los.stocktaking.model.LOSStocktakingOrder;
import de.linogistix.los.stocktaking.query.LOSStocktakingOrderQueryRemote;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.mywms.globals.Role;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author trautm
 */
public class StockTakingProcessController {

    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.INVENTORY_STR};
    }
    
    J2EEServiceLocator loc;
    private StockTakingQueryTopComponent topComponent;
    LOSStocktakingOrderQueryRemote orderQuery;
    
    StockTakingProcessController(StockTakingQueryTopComponent pTopComponent) {
        this.topComponent = pTopComponent;
        loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
        
        topComponent.getBOQueryPanel().getMasterDetailView().getDetailManager().
                addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                    Node[] nodes = (Node[]) evt.getNewValue();
                    if (nodes != null && nodes.length > 0) {
                        onSelectionChanged((LOSStocktakingOrder) ((BOEntityNode) nodes[0]).getBo());
                    }
                    else {
                       topComponent.stockTakingProcessPanel.onSelectionCleared(); 
                    }
                }
            }
        });
        
        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);

    }
    
    void onSelectionChanged(final LOSStocktakingOrder order) {
        topComponent.stockTakingProcessPanel.onSelectionChanged(order);
    }
    

    public LOSStocktakingOrder getOrder(Long id) {
        LOSStocktakingOrder order = null;
        try {
            if (orderQuery == null) {
                orderQuery = loc.getStateless(LOSStocktakingOrderQueryRemote.class);
            }
            order = orderQuery.queryById(id);
        } catch (J2EEServiceLocatorException ex) {
            ExceptionAnnotator.annotate(ex);
        } catch (BusinessObjectNotFoundException ex) {
            ExceptionAnnotator.annotate(ex);
        } catch (BusinessObjectSecurityException ex) {
            ExceptionAnnotator.annotate(ex);
        }
        return order;
    }
    
    public StockTakingQueryTopComponent getTopComponent() {
        return topComponent;
    }
}
