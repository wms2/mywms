/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.inventory.gui.component.controls;

import de.linogistix.common.gui.component.controls.BOAutoFilteringComboBoxModel;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.query.LOSCustomerOrderQueryRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import org.openide.util.Lookup;

/**
 *
 * @author Jordan
 */
public class CustomerOrderComboBoxModel extends BOAutoFilteringComboBoxModel<LOSCustomerOrder>{

    private LOSCustomerOrderQueryRemote orderQuery;
    
    private int orderState;
    
    public CustomerOrderComboBoxModel() throws Exception{
        super(LOSCustomerOrder.class);
        
        J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
        orderQuery = loc.getStateless(LOSCustomerOrderQueryRemote.class);
    }
    
    @Override
    public LOSResultList<BODTO<LOSCustomerOrder>> getResults(String searchString, QueryDetail detail) {
        
        LOSResultList<BODTO<LOSCustomerOrder>> resList;
        resList = orderQuery.autoCompletionOpenOrders(searchString, null, detail);
        return resList;
    }

    @Override
    public void clear() {
        super.clear();
        orderState = 0;
    }
    
     public int getOrderState() {
        return orderState;
    }

    public void setOrderState(int orderState) {
        this.orderState = orderState;
    }
    
}
