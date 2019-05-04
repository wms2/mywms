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
import de.linogistix.los.inventory.model.LOSCustomerOrderPosition;
import de.linogistix.los.inventory.query.LOSCustomerOrderPositionQueryRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import org.openide.util.Lookup;

/**
 *
 * @author Jordan
 */
public class CustomerOrderPositionComboBoxModel extends BOAutoFilteringComboBoxModel<LOSCustomerOrderPosition>{

    private LOSCustomerOrderPositionQueryRemote positionQuery;
    
    private BODTO<LOSCustomerOrder> orderTO;
    
    public CustomerOrderPositionComboBoxModel() throws Exception{
        super(LOSCustomerOrderPosition.class);
        
        J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
        positionQuery = loc.getStateless(LOSCustomerOrderPositionQueryRemote.class);
    }
    
    @Override
    public LOSResultList<BODTO<LOSCustomerOrderPosition>> getResults(String searchString, QueryDetail detail) {
        LOSResultList<BODTO<LOSCustomerOrderPosition>> resList;
        resList = positionQuery.autoCompletionByOrderRequest(searchString, orderTO, detail);
        return resList;
    }

    public BODTO<LOSCustomerOrder> getOrderTO() {
        return orderTO;
    }
    
    public void setOrderTO(BODTO<LOSCustomerOrder> orderTO) {
        this.orderTO = orderTO;
    }

    @Override
    public void clear() {
        super.clear();
        orderTO = null;
    }
    
}
