/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.model.LOSAdvice;

import java.math.BigDecimal;
import java.util.Date;
import javax.ejb.Local;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;

/**
 * Services for the goods advice process, i.e. goods where adviced from the supplier.
 *
 * @author trautm
 */
@Local
public interface LOSAdviceBusiness {

    /**
	 * Creates a new {@link LOSAdvice}
	 * @param c
	 * @param lot
	 * @param amount
	 * @param expireLot
	 * @param expectedDelivery
	 * @param requestId unique id
	 * @return requestID of created LOSAdvice
     * @throws InventoryException 
	 */
	public abstract LOSAdvice goodsAdvise(Client c, ItemData item, Lot lot, BigDecimal amount, boolean expireLot, Date expectedDelivery, String requestId) throws InventoryException;
    
    public abstract void removeAdvise(Client c, LOSAdvice adv) throws InventoryException;
    
    
}
