/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import javax.ejb.Remote;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.query.BODTO;

@Remote
public interface AdviceFacade {

    public void removeAdvise(BODTO<LOSAdvice> adv) throws InventoryException;
    
    public void finishAdvise(BODTO<LOSAdvice> adv) throws InventoryException;
    
}
