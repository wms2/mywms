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
import de.linogistix.los.inventory.service.dto.GoodsReceiptTO;
import de.wms2.mywms.goodsreceipt.GoodsReceipt;

@Remote
public interface QueryGoodsReceiptServiceRemote {

	
	/**
	 * Get a goods receipt by its database identifier with associated collections initialized.
	 * 
	 * @param id identifier to search for.
	 * @return Goods receipt with specified id.
	 * @throws UnAuthorizedException if caller is not assigned to system client, but to another client than the goods receipt is.
	 * @throws EntityNotFoundException if there is no goods receipt for the specified id.
	 */
	public GoodsReceipt fetchEager(long id) throws UnAuthorizedException, EntityNotFoundException;
	
	/**
	 * Search for all goods receipts that have one of the specified states.
	 * For security reasons result will be limited according to the callers client <br> 
	 * - callers who belong to the system client will get all goods receipts <br>
	 * - callers of a certain client will get only those goods receipts that are also assigned to that client.
	 * 
	 * @param states the states to search for.
	 * @return List of goods receipts. May be empty, if there are none.
	 */
	public List<GoodsReceiptTO> getDtoListByStates(int minState, int maxState);
	
	public List<GoodsReceiptTO> getOpenDtoListByCode(String code, boolean limitAmountToNotified, int minState, int maxState);
}
