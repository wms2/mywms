/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import javax.ejb.Local;

import org.mywms.service.BasicService;

import de.linogistix.los.inventory.model.OrderReceipt;
@Local
public interface OrderReceiptService extends BasicService<OrderReceipt>{
	public OrderReceipt getByOrderNumber( String orderNumber );
}
