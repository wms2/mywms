/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.mywms.model.Client;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.common.service.QueryClientService;
import de.linogistix.los.util.StringTools;
import de.linogistix.los.util.businessservice.ContextService;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadEntityService;
import de.wms2.mywms.location.StorageLocation;

@Stateless
public class QueryUnitLoadServiceBean 
		implements QueryUnitLoadServiceRemote 
{
	@Inject
	private UnitLoadEntityService unitLoadService;

	@EJB
	private QueryClientService queryClientService;
	
	@EJB
	private ContextService ctxService;
	
	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	
	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.location.service.QueryUnitLoadService#getByLabelId(java.lang.String)
	 */
	public UnitLoad getByLabelId(String label) throws UnAuthorizedException {
		if (StringTools.isEmpty(ctxService.getCallerUserName())) {
			throw new UnAuthorizedException();
		}
		UnitLoad ul = unitLoadService.read(label);
		if (ul == null) {
			return null;
		}
		if (!ctxService.getCallersClient().equals(queryClientService.getSystemClient())
				&& !ctxService.getCallersClient().equals(ul.getClient())) {
			throw new UnAuthorizedException();
		}
		return ul;
	}

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.location.service.QueryUnitLoadService#getListByLocation(de.linogistix.los.location.model.LOSStorageLocation)
	 */
	public List<UnitLoad> getListByLocation(StorageLocation sl) {
		Client queryClient = ctxService.getCallersClient();
        if (!queryClient.isSystemClient()) {
        	queryClient = null;
        }

		return unitLoadService.readList(queryClient, sl, null, null, null, null, null);
	}
}
