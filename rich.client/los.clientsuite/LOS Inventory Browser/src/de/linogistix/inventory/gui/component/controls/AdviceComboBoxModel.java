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
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.query.LOSAdviceQueryRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.product.ItemData;
import org.mywms.model.Client;
import org.openide.util.Lookup;

/**
 *
 * @author Jordan
 */
public class AdviceComboBoxModel extends BOAutoFilteringComboBoxModel<LOSAdvice>{

    private LOSAdviceQueryRemote adviceQuery; 
    
    private BODTO<Client> clientTO = null;
    
    private BODTO<ItemData> itemDataTO = null;
    
    private BODTO<Lot> lotTO = null;
    
    public AdviceComboBoxModel() throws Exception {
        super(LOSAdvice.class);
        
        J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
        adviceQuery = loc.getStateless(LOSAdviceQueryRemote.class);
    }
    
    @Override
    public LOSResultList<BODTO<LOSAdvice>> getResults(String searchString, QueryDetail detail) {
        
        return adviceQuery.autoCompletionByClientLotItemdata(searchString, clientTO, itemDataTO, lotTO, detail);
        
    }

    @Override
    public void clear() {
        super.clear();
        clientTO = null;
        itemDataTO = null;
        lotTO = null;
    }
        
    public void setClientTO(BODTO<Client> clientTO) {
        this.clientTO = clientTO;
    }

    public void setItemDataTO(BODTO<ItemData> itemTO) {
        this.itemDataTO = itemTO;
    }
    
    public void setLotTO(BODTO<Lot> lotTO) {
        this.lotTO = lotTO;
    }

}
