/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.stocktaking.model;

public enum LOSStocktakingState {

	/**
	 * The order is only created. It cannot be used for counting.
	 */
	CREATED,
	
	/**
	 * The order is created and can be used for counting.
	 */
	FREE,
	
	/**
	 * The counting has been started.  
	 */
	STARTED,
	
	/**
	 * The counting is finished but the result as not been booked.
	 */
	COUNTED,
	
	/**
	 * Everything is finished.
	 */
	FINISHED,
	
	/**
	 * Everything is canceled. 
	 */
	CANCELLED
	
	
}
