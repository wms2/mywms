/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */


package de.linogistix.los.inventory.service;

import javax.ejb.Local;

import org.mywms.model.Client;

import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.wms2.mywms.inventory.UnitLoadType;

/**
 *
 * @author Jordan
 */
@Local
public interface InventoryGeneratorService {
     
    
    /**
     * Generates a unique number for a {@link LOSGoodsReceipt}
     * @param c the Client 
     */
    public String generateGoodsReceiptNumber(Client c);
    
    /**
     * Generates a unique number for a {@link LOSAdvice}
     * @param c
     * @return
     */
    public String generateAdviceNumber(Client c);
    
    public String generateUnitLoadAdviceNumber(Client c);

    public String generateUnitLoadLabelId(Client c, UnitLoadType ulType);
    
    public String generatePickOrderNumber(Client c, String prefix);
    
    public String generateOrderNumber(Client c);
    public String generateOrderNumber(Client c, String prefix);
    
    public String generateStorageRequestNumber(Client c);
    
    public String generateGoodsOutNumber(Client c) ;

	public String generateReplenishNumber(Client c);
    
	public String generateManageInventoryNumber();
	
	public String generateStocktakingNumber();

}
