/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.mywms.model.Client;

import de.linogistix.los.common.exception.OutOfRangeException;
import de.linogistix.los.inventory.customization.ManageAdviceService;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.product.ItemData;

/**
 * Inventory relevant business operations
 * 
 * @author trautm
 */
@Stateless
public class LOSAdviceBusinessBean implements LOSAdviceBusiness {

	Logger log = Logger.getLogger(LOSAdviceBusinessBean.class);

	@EJB
	ManageAdviceService adviceService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.linogistix.connector.business.GoodsAdviceBusiness#goodsAdvise(java.lang.String,
	 *      java.lang.String, int, boolean, java.util.Date)
	 */
    public LOSAdvice goodsAdvise(Client c, ItemData item, Lot lot, BigDecimal amount, boolean expireLot, Date expectedDelivery, String adviceNumber) throws InventoryException 
	{	
		try {
			if (adviceNumber == null || adviceNumber.length() == 0){
				adviceNumber =  adviceService.getNewAdviceNumber();
			}
			LOSAdvice ret =  adviceService.createAdvice(c, adviceNumber, item, amount);
			ret.setLot(lot);
			ret.setExpectedDelivery(expectedDelivery);
			
			return ret;
			
		} catch (OutOfRangeException e) {
			
			throw new InventoryException(InventoryExceptionKey.ERROR_NOTIFIEDAMOUNT_NEGATIVE, new Object[]{});
		}
		
	}

    public void removeAdvise(Client c, LOSAdvice adv) throws InventoryException {
        adviceService.deleteAdvice(adv);
    }
    

	
}
