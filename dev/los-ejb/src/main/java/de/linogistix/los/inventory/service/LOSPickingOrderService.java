/*
 * Copyright (c) 2009-2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.List;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.service.BasicService;

import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSOrderStrategy;
import de.linogistix.los.inventory.model.LOSPickingOrder;
/**
 * @author krane
 *
 */
@Local
public interface LOSPickingOrderService extends BasicService<LOSPickingOrder>{

	/**
	 * Generate a new picking order (only order, no positions)
	 * @param client. If null, caller client will be used
	 * @param strategy. If null, default values will be used
	 * @param prefix. If null, default values will be used
	 * @return
	 * @throws FacadeException
	 */
	public LOSPickingOrder create(Client client, LOSOrderStrategy strategy, String sequenceName) throws FacadeException;
	public List<LOSPickingOrder> getByCustomerOrderNumber(String orderNumber);
	public List<LOSPickingOrder> getByCustomerOrder(LOSCustomerOrder order);
	public LOSPickingOrder getByNumber(String pickingOrderNumber);

}
