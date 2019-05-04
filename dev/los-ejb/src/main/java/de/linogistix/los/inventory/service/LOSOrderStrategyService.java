/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import javax.ejb.Local;

import org.mywms.model.Client;
import org.mywms.service.BasicService;

import de.linogistix.los.inventory.model.LOSOrderStrategy;

/**
 * @author krane
 *
 */
@Local
public interface LOSOrderStrategyService extends BasicService<LOSOrderStrategy> {

	public LOSOrderStrategy getByName(Client client, String name);
	
	public LOSOrderStrategy getDefault(Client client);

	public LOSOrderStrategy getExtinguish(Client client);
}
