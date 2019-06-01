/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.location.gui.component.controls;

import de.linogistix.common.gui.component.controls.BOAutoFilteringComboBoxModel;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote;
import de.wms2.mywms.location.StorageLocation;
import org.mywms.model.Client;
import org.openide.util.Lookup;

/**
 *
 * @author Jordan
 */
public class LOSStorageLocationComboBoxModel extends BOAutoFilteringComboBoxModel<StorageLocation>{

    private LOSStorageLocationQueryRemote slQuery;
    
    private BODTO<Client> clientTO = null;
    
    public LOSStorageLocationComboBoxModel() throws Exception {
        super(StorageLocation.class);
        
        J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
        slQuery = loc.getStateless(LOSStorageLocationQueryRemote.class);
    }
    
    @Override
    public LOSResultList<BODTO<StorageLocation>> getResults(String searchString, QueryDetail detail) {
        
        return slQuery.autoCompletionClientAndAreaType(searchString, null, detail);
        
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
