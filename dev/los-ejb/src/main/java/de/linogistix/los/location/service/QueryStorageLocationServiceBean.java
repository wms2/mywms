/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.location.StorageLocationEntityService;

@Stateless
public class QueryStorageLocationServiceBean implements QueryStorageLocationServiceRemote {

	@Inject
	private StorageLocationEntityService locationService;

	public StorageLocation getByName(String name) throws UnAuthorizedException {
		return locationService.read(name);
	}

}
