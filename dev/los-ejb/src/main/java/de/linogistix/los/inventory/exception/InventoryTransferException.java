/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.exception;

import java.util.Arrays;

import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.res.InventoryBundleResolver;

public class InventoryTransferException extends FacadeException {

	private static final long serialVersionUID = 1L;

	private static final String resourceBundle = "de.linogistix.los.inventory.res.Bundle";
	
	private InventoryExceptionKey invKey;
	
	public InventoryTransferException(InventoryExceptionKey key, Object[] parameters){
		
		super(key.name() + ": " + Arrays.toString(parameters), key.name(), parameters, resourceBundle);
        invKey = key;
        setBundleResolver(InventoryBundleResolver.class);
	}
    
    public InventoryTransferException(InventoryExceptionKey key, String param1){
		
		super(key.name() + ":" +param1, key.name(), new Object[]{param1}, resourceBundle);
        invKey = key;
        setBundleResolver(InventoryBundleResolver.class);
	}

	public InventoryExceptionKey getInventoryExceptionKey() {
		return invKey;
	}
	
}
