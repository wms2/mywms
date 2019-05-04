/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.exception;

import javax.ejb.ApplicationException;

import org.mywms.service.ServiceException;

import de.linogistix.los.inventory.res.InventoryBundleResolver;

/**
 * This Exception will not cause a rollback of the transaction.
 * 
 * @author Jordan
 *
 */
@ApplicationException(rollback=false)
public class StockExistException extends ServiceException {

	private static final long serialVersionUID = 1L;
	
	public StockExistException(){
		super("Stock Exists", "STOCK_EXISTS", new Object[0]);
		setBundleResolver(InventoryBundleResolver.class);
	}
}
