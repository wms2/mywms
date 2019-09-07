/*
 * Copyright (c) 2009 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import javax.ejb.Local;

import org.mywms.service.BasicService;

import de.wms2.mywms.replenish.ReplenishOrder;

/**
 * @author krane
 *
 */
@Local
public interface LOSReplenishOrderService extends BasicService<ReplenishOrder> {
}
