/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.stocktaking.service;

import javax.ejb.Local;

import org.mywms.service.BasicService;

import de.linogistix.los.stocktaking.model.LOSStocktakingOrder;

/**
 *
 * @author krane
 */
@Local
public interface LOSStocktakingOrderService extends BasicService<LOSStocktakingOrder>{
 
}
