/*
 * StorageLocationQueryRemote.java
 *
 * Created on 14. September 2006, 06:59
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.los.query;
import javax.ejb.Remote;
import org.mywms.model.Client;

import de.linogistix.los.common.exception.UnAuthorizedException;

@Remote
public interface ClientQueryRemote extends BusinessObjectQueryRemote<Client> {
    
    Client getSystemClient();
    
    public Client getByNumberIgnoreCase( String clientNumber ) throws UnAuthorizedException ;    
}
