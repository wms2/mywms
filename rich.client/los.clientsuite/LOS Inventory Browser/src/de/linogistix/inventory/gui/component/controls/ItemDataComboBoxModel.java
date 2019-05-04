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
import de.linogistix.los.inventory.query.ItemDataQueryRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
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
public class ItemDataComboBoxModel extends BOAutoFilteringComboBoxModel<ItemData>{

    private ItemDataQueryRemote itemQuery;
    
    private BODTO<Client> clientTO = null;
    
    private BODTO<Lot> lotTO = null;
    
    public ItemDataComboBoxModel() throws Exception {
        super(ItemData.class);
        
        J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
        itemQuery = loc.getStateless(ItemDataQueryRemote.class);
    }
    
    @Override
    public LOSResultList<BODTO<ItemData>> getResults(String searchString, QueryDetail detail) {
        if( isSingleResult() ) {
            try {
                return getResults();
            } catch (FacadeException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        
        return itemQuery.autoCompletionClientAndLot(searchString, clientTO, null, detail);
    }

    @Override
    public void clear() {
        super.clear();
        clientTO = null;
        lotTO = null;
    }
        
    public void setClientTO(BODTO<Client> clientTO) {
        this.clientTO = clientTO;
    }

    public void setLotTO(BODTO<Lot> lotTO) {
        this.lotTO = lotTO;
    }

}
