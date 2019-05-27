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

package de.linogistix.los.inventory.query;

import javax.ejb.Remote;

import org.mywms.facade.FacadeException;
import org.mywms.model.Client;

import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.product.ItemData;

/**
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Remote
public interface LotQueryRemote extends BusinessObjectQueryRemote<Lot> {

	public LOSResultList<BODTO<Lot>> autoCompletionByClientAndItemData(String lotExp,
			BODTO<Client> client, BODTO<ItemData> idat);

	public LOSResultList<BODTO<Lot>> getNotToUse(QueryDetail q) throws BusinessObjectNotFoundException, FacadeException;

	public LOSResultList<BODTO<Lot>> getToUseFromNow(QueryDetail q) throws BusinessObjectNotFoundException, FacadeException;

	public LOSResultList<BODTO<Lot>>getTooOld(QueryDetail q) throws BusinessObjectNotFoundException, FacadeException;
	
	public Lot queryByNameAndItemData(Client c, String lotName, ItemData idat) throws BusinessObjectNotFoundException;

}
