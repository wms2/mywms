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

import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.wms2.mywms.location.StorageLocation;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Remote
public interface LOSStorageLocationQueryRemote 
        extends BusinessObjectQueryRemote<StorageLocation>
{ 
	
	public LOSResultList<BODTO<StorageLocation>> autoCompletionClientAndAreaType(String searchString, 
																		       		BODTO<Client> clientTO, 
																		       		QueryDetail detail);
	
	public StorageLocation getClearing();
	
	public StorageLocation getNirwana();
	
	public String getNirwanaName();
	
}
