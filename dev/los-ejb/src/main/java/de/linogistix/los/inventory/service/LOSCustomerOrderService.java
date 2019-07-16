/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
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

import de.wms2.mywms.delivery.DeliveryOrder;

/**
 * @author krane
 *
 */
@Local
public interface LOSCustomerOrderService extends BasicService<DeliveryOrder> {

    public List<DeliveryOrder> getByExternalId(Client client, String externalId);
    public List<DeliveryOrder> getByExternalId(String externalId);

    public List<DeliveryOrder> getByExternalNumber(Client client, String externalNumber);
    public List<DeliveryOrder> getByExternalNumber(String externalNumber);

	public DeliveryOrder getByNumber(String number);
	public boolean existsByNumber(String number);

}
