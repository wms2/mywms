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

import de.wms2.mywms.inventory.UnitLoadType;

/**
 *
 * @author Jordan
 */
@Local
public interface InventoryGeneratorService {
     
    
    public String generateGoodsReceiptNumber(Client c);
    
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
