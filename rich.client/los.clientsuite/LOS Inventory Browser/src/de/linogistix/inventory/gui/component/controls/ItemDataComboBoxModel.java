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
import de.wms2.mywms.product.ItemData;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Jordan
 */
public class ItemDataComboBoxModel extends BOAutoFilteringComboBoxModel<ItemData>{

    private ItemDataQueryRemote itemQuery;
    
    private BODTO<Client> clientTO = null;
    
    
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
        
        return itemQuery.autoCompletionClient(searchString, clientTO, detail);
    }

    @Override
    public void clear() {
        super.clear();
        clientTO = null;
    }
        
    public void setClientTO(BODTO<Client> clientTO) {
        this.clientTO = clientTO;
    }


}
