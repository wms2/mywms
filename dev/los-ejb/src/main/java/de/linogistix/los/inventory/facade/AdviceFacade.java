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
import de.linogistix.los.query.BODTO;
import de.wms2.mywms.advice.AdviceLine;
import de.wms2.mywms.exception.BusinessException;

@Remote
public interface AdviceFacade {

    public void removeAdvise(BODTO<AdviceLine> adv) throws BusinessException;
    
    public void finishAdvise(BODTO<AdviceLine> adv) throws InventoryException, BusinessException;
    
}
