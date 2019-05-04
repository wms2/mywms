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
public enum LOSAdviceState {
    /**A new Advice in the system has been created*/
	RAW,
	/** */
	PROCESSING,
    /** More amount has been advised than has come up to now. The system expects more goods to come.*/
    GOODS_TO_COME,
    /** Less amount has been advised than come actually. The system doesn't expect more goods to come*/
    OVERLOAD,
    /** The Advice is finished, i.e.all goods have come that were advised. */
    FINISHED;
    
    public boolean isFinished(){
        return this == FINISHED;
    }

}
