/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.customization;

import java.math.BigDecimal;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;
import org.mywms.model.StockUnit;

/**
 * User exits of stock processing.
 * 
 * @author krane
 *
 */
@Local
public interface ManageStockService {

	/**
	 * User exit. Is called when the amount of a stock changes.<br>
	 * 
	 * @param stock
	 * @param amountOld
	 * @throws FacadeException
	 */
	public void onStockAmountChange( StockUnit stock, BigDecimal amountOld ) throws FacadeException;
	
}
