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


import java.util.List;

import javax.ejb.Remote;

import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;

import de.linogistix.los.inventory.query.dto.StockUnitTO;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.query.exception.BusinessObjectQueryException;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Remote
public interface StockUnitQueryRemote extends BusinessObjectQueryRemote<StockUnit>{ 
  
	public List<StockUnitTO> queryByStorageLocation(
			BODTO<LOSStorageLocation> sl,
			QueryDetail detail) throws BusinessObjectQueryException;
	
	public List<StockUnitTO> queryByItemData(
			BODTO<ItemData> idat,
			QueryDetail detail) throws BusinessObjectQueryException;
	
	public List<StockUnitTO> queryByLot(
			BODTO<Lot> lot,
			QueryDetail detail) throws BusinessObjectQueryException;
	
	public LOSResultList<StockUnitTO> queryByDefault(
			BODTO<Client> client, 
			BODTO<Lot> lot,
			BODTO<ItemData> itemData,
			BODTO<LOSStorageLocation> storageLocation,
			QueryDetail detail) throws BusinessObjectNotFoundException, BusinessObjectQueryException;
	
	//dgrys portierung wildfly 8.2
	//public LOSResultList<StockUnit> queryByLabelId(QueryDetail d, String suId) throws BusinessObjectNotFoundException, BusinessObjectQueryException;
}
