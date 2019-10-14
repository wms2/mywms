/*
 * Copyright (c) 2010 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import javax.ejb.Stateless;

import org.mywms.service.BasicServiceBean;

import de.wms2.mywms.product.ItemDataNumber;

/**
 * @author krane
 *
 */
@Stateless
public class ItemDataNumberServiceBean extends
		BasicServiceBean<ItemDataNumber> implements ItemDataNumberService {
}
