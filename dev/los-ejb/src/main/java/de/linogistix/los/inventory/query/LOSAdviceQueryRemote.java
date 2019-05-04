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

import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSAdviceState;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
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
public interface LOSAdviceQueryRemote extends BusinessObjectQueryRemote<LOSAdvice>{ 
  
	/**
	 * Returns DTO of those {@link LOSAdvice} that are not finished yet, i.e. in {@link LOSAdviceState#FINISHED}
	 * @return
	 * @throws BusinessObjectNotFoundException
	 * @throws BusinessObjectQueryException
	 */
	public List<BODTO<LOSAdvice>>  queryGoodsToCome(QueryDetail qd) 
		throws BusinessObjectNotFoundException, BusinessObjectQueryException;
	
	public LOSResultList<BODTO<LOSAdvice>> autoCompletionByClientLotItemdata(
                                                            String exp,
                                                            BODTO<Client> client, 
                                                            BODTO<ItemData> item,
                                                            BODTO<Lot> lot,
                                                            QueryDetail detail);
	/**
	 * Retuns list of BODTO<LOSAdvice> that are assigned to given {@link LOSGoodsReceipt}.
	 * 
	 * @param gr
	 * @param detail
	 * @return list of BODTO<LOSAdvice> that are assigned to given {@link LOSGoodsReceipt}.
	 */
//	public LOSResultList<BODTO<LOSAdvice>> queryByAssingigGoodsReceipt(BODTO<LOSGoodsReceipt> gr, QueryDetail detail);
	
	public boolean hasSingleClient();
}