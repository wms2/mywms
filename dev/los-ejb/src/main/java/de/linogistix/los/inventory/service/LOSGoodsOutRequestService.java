/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.List;

import javax.ejb.Local;

import org.mywms.model.Client;
import org.mywms.service.BasicService;

import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.inventory.UnitLoad;

@Local
public interface LOSGoodsOutRequestService extends
		BasicService<LOSGoodsOutRequest> {

	public List<LOSGoodsOutRequest> getByDeliveryOrder(DeliveryOrder order);

	public LOSGoodsOutRequest getByNumber(Client client, String number);
	
	public LOSGoodsOutRequest getByNumber(String number);
	
	public List<LOSGoodsOutRequest> getByUnitLoad(UnitLoad ul);

}
