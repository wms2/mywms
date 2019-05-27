/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package de.linogistix.los.inventory.service;

import javax.ejb.Local;

import org.mywms.service.BasicService;

import de.wms2.mywms.product.ItemUnit;

/**
 * This interface declares the service for the entity ItemData. For this
 * service it is save to call the <code>get(String name)</code>
 * method.
 * 
 * @see org.mywms.service.BasicService#get(String)
 * @author Olaf Krause
 * @version $Revision$ provided by $Author$
 */
@Local
public interface ItemUnitService
    extends BasicService<ItemUnit>
{

	ItemUnit getDefault() ;
   
	ItemUnit getByName(String name);
	
}
