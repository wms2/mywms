/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.customization;

import java.math.BigDecimal;

import org.mywms.facade.FacadeException;
import org.mywms.model.StockUnit;

/**
 * @author krane
 *
 */
public class ManageStockServiceBean implements ManageStockService {

	public void onStockAmountChange( StockUnit stock, BigDecimal amountOld ) throws FacadeException {
	}
	
}
