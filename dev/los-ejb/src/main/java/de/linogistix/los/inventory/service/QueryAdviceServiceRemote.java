/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.List;

import javax.ejb.Remote;

import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.wms2.mywms.advice.AdviceLine;
import de.wms2.mywms.goodsreceipt.GoodsReceipt;


@Remote
public interface QueryAdviceServiceRemote {
	
	/**
	 * Get an advice by its database identifier.
	 * 
	 * @param id identifier to search for.
	 * @return Advice with specified id.
	 * @throws UnAuthorizedException if caller is not assigned to system client, but to another client than the goods receipt is.
	 * @throws EntityNotFoundException if there is no advice for the specified id.
	 */
	public AdviceLine getById(long id) throws UnAuthorizedException, EntityNotFoundException;
    
	public List<AdviceLine> getListByGoodsReceipCode( GoodsReceipt gr, String code, boolean selectFinished );

}
