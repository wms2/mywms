/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import javax.ejb.Local;

import org.mywms.service.BasicService;

import de.linogistix.los.inventory.model.StockUnitLabel;

@Local
public interface StockUnitLabelService extends BasicService<StockUnitLabel>{
	
	public StockUnitLabel getByLabelId(String labelId);
}
