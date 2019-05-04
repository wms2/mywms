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
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.inventory.query.LotQueryRemote;
import de.linogistix.los.query.QueryDetail;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Jordan
 */
public class LotComboBoxModel extends BOAutoFilteringComboBoxModel<Lot>{

    private LotQueryRemote lotQuery;
    
    private BODTO<Client> clientTO = null;
    
    private BODTO<ItemData> itemDataTO = null;
    
    public LotComboBoxModel() throws Exception {
        super(Lot.class);
        
        J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
        lotQuery = loc.getStateless(LotQueryRemote.class);
    }
    
    @Override
    public LOSResultList<BODTO<Lot>> getResults(String searchString, QueryDetail detail) {
        if( isSingleResult() ) {
            try {
                return getResults();
            } catch (FacadeException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        
        return lotQuery.autoCompletionByClientAndItemData(searchString, clientTO, itemDataTO);
    }
    
     @Override
    public void clear() {
         super.clear();
        clientTO = null;
        itemDataTO = null;
    }
    
    public BODTO<Client> getClientTO() {
        return clientTO;
    }
     
    public void setClientTO(BODTO<Client> clientTO) {
        this.clientTO = clientTO;
    }

    public BODTO<ItemData> getItemDataTO() {
        return itemDataTO;
    }
    
    public void setItemDataTO(BODTO<ItemData> itemTO) {
        this.itemDataTO = itemTO;
    }

}
