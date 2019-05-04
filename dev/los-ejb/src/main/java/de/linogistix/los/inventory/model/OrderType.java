/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.model;

import de.linogistix.los.location.model.LOSStorageLocation;

public enum OrderType {
	
	/** Goods going into a production process*/
	TO_PRODUCTION,
	
	/** Goods for internal use, some locks won't take effect */
	INTERNAL,
	
	/** Goods going to a customer */
	TO_CUSTOMER,
	
	/** Goods for replenishing a {@link LOSStorageLocation} */
	TO_REPLENISH,
	
	/** Goods should be extinguished */
	TO_EXTINGUISH,
	
	/** Goods should be brought from one site to another */
	TO_OTHER_SITE
	
}
