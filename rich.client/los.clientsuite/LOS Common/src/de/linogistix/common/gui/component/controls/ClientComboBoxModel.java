/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls;

import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.ClientQueryRemote;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import org.mywms.model.Client;
import org.openide.util.Lookup;

/**
 *
 * @author Jordan
 */
public class ClientComboBoxModel extends BOAutoFilteringComboBoxModel<Client>{

    private ClientQueryRemote clientQuery;
    
    public ClientComboBoxModel() throws Exception {
        super(Client.class);
        
        J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
        clientQuery = loc.getStateless(ClientQueryRemote.class);
    }
    
    @Override
    public LOSResultList<BODTO<Client>> getResults(String searchString, QueryDetail detail) {
        
        return clientQuery.autoCompletion(searchString, detail);
        
    }
}
