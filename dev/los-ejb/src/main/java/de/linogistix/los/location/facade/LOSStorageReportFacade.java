/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.facade;

import java.util.List;

import javax.ejb.Remote;

import de.linogistix.los.location.exception.LOSLocationException;


@Remote
public interface LOSStorageReportFacade {

	public List<String> autocompleteClientName(String namepart);
	
	public List<String> autocompleteItemNumber(String clientName, String numberpart) throws LOSLocationException;
	
	public List<LOSStockListItem> getStockList(String clientName, String itemNumber) throws LOSLocationException;
}
