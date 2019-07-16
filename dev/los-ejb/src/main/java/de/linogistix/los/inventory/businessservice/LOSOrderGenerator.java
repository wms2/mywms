/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.math.BigDecimal;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;
import org.mywms.model.Client;

import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.strategy.OrderStrategy;

/**
 *
 * @author krane
 */
@Local
public interface LOSOrderGenerator {

	public DeliveryOrder createDeliveryOrder(Client client, OrderStrategy strat) throws FacadeException;

	public DeliveryOrder addDeliveryOrderLine(DeliveryOrder order, ItemData item, Lot lot, String serialNumber, BigDecimal amount) throws FacadeException;


}
