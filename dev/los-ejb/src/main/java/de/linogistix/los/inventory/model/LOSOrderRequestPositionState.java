/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.model;

public enum LOSOrderRequestPositionState {
	/** The order has been created */
	RAW,
	/** The Order has been calculated (i.e. picking requests have been created)*/
	PROCESSING,
	/** The order has finished (i.e. is PICKED and the goods out process has finished as well)*/
	FINISHED,
	/** There has been not enough amount to fulfill the order but the order should be finished despite the missing amount */ 
	PICKED_PARTIAL,
	/** All positions have been picked */ 
	PICKED,
	/** The Order will be reprocessed later because now there is not enough amount */
	PENDING,
	/** The order has failed*/
	FAILED
}
