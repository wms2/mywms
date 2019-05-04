/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.stocktaking.exception;

import javax.ejb.ApplicationException;

import org.mywms.facade.FacadeException;

@ApplicationException(rollback=true)
public class LOSStockTakingException extends FacadeException {

	private static final long serialVersionUID = 1L;

	private static String resourceBundle = "de.linogistix.los.stocktaking.exception.LOSStockTakingExceptionBundle";
	
	private LOSStockTakingExceptionKey stockTakingExceptionKey;
	
	public LOSStockTakingException(LOSStockTakingExceptionKey key, Object[] parameters){
		super("", key.name(), parameters, resourceBundle);
		setBundleResolver(BundleResolver.class);
        stockTakingExceptionKey = key;
	}

	public LOSStockTakingExceptionKey getStockTakingExceptionKey() { 
		return stockTakingExceptionKey;
	}
}
