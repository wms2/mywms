/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.model;


/**
 *
 * @author trautm
 */
public enum LOSOrderRequestState {
    /**Created but no {@link LOSPickRequest} has been created yet */
	RAW,
    
	/** Order is assembled*/
	ASSEMBLED,
    
	/**{@link LOSPickRequest} has been created */
    PROCESSING,
    
    /** Not all {@link LOSOrderRequestPosition}s could be solved*/
    PICKED_PARTIAL,
    
    /** {@link LOSOrderRequest} is picked */
    PICKED,
    
    /**{@link LOSOrderRequest} has failed at least */
    FAILED,
    
    /** {@link LOSOrderRequest} has been picked partial, not enough articles left */
    PENDING,
    
    /** {@link LOSOrderRequest} is finished and could be deleted */
    FINISHED
    
}
