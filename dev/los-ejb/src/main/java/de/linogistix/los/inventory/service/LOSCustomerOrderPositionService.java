/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import javax.ejb.Local;

import org.mywms.service.BasicService;

import de.linogistix.los.inventory.model.LOSCustomerOrderPosition;

/**
 * @author krane
 *
 */
@Local
public interface LOSCustomerOrderPositionService extends BasicService<LOSCustomerOrderPosition> {

	public LOSCustomerOrderPosition getByNumber(String number);
	public boolean existsByNumber(String number);

}
