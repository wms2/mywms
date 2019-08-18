/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import javax.ejb.Stateless;

import org.mywms.service.BasicServiceBean;

import de.wms2.mywms.shipping.ShippingOrderLine;


@Stateless
public class LOSGoodsOutRequestPositionServiceBean extends
		BasicServiceBean<ShippingOrderLine> implements
		LOSGoodsOutRequestPositionService{
}
