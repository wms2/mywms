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

package de.linogistix.los.location.query;


import javax.ejb.Remote;

import org.mywms.model.Client;

import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Remote
public interface LOSStorageLocationQueryRemote 
        extends BusinessObjectQueryRemote<LOSStorageLocation>
{ 
	
	public LOSResultList<BODTO<LOSStorageLocation>> autoCompletionClientAndAreaType(String searchString, 
																		       		BODTO<Client> clientTO, 
																		       		QueryDetail detail);
	
	public LOSStorageLocation getClearing();
	
	public LOSStorageLocation getNirwana();
	
	public String getNirwanaName();
	
}
