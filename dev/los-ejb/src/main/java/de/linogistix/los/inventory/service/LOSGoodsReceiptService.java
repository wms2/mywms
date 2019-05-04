/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.List;

import javax.ejb.Local;

import org.mywms.model.Client;
import org.mywms.service.BasicService;

import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;

@Local
public interface LOSGoodsReceiptService extends BasicService<LOSGoodsReceipt> {
	
	public LOSGoodsReceipt createGoodsReceipt(Client client, String number);

	public LOSGoodsReceipt getByGoodsReceiptNumber(String number);
	
	public List<LOSGoodsReceipt> getByAdvice( LOSAdvice adv );

	public LOSGoodsReceipt getOpenByDeliveryNoteNumber(String number);
	
	public List<LOSGoodsReceipt> getByDeliveryNoteNumber(String number);

}
