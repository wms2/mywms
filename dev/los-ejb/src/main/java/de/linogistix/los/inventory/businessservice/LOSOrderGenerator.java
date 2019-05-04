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
import org.mywms.model.ItemData;
import org.mywms.model.Lot;

import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSOrderStrategy;

/**
 *
 * @author krane
 */
@Local
public interface LOSOrderGenerator {

	public LOSCustomerOrder createCustomerOrder(Client client, LOSOrderStrategy strat) throws FacadeException;

	public LOSCustomerOrder addCustomerOrderPos(LOSCustomerOrder order, ItemData item, Lot lot, String serialNumber, BigDecimal amount) throws FacadeException;


}
