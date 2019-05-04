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

import de.linogistix.los.inventory.model.LOSCustomerOrder;

/**
 * @author krane
 *
 */
@Local
public interface LOSCustomerOrderService extends BasicService<LOSCustomerOrder> {

    public List<LOSCustomerOrder> getByExternalId(Client client, String externalId);
    public List<LOSCustomerOrder> getByExternalId(String externalId);

    public List<LOSCustomerOrder> getByExternalNumber(Client client, String externalNumber);
    public List<LOSCustomerOrder> getByExternalNumber(String externalNumber);

	public LOSCustomerOrder getByNumber(String number);
	public boolean existsByNumber(String number);

}
