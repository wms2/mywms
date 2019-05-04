/*
 * Copyright (c) 2010 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-3PL
 */
package de.linogistix.los.inventory.service;

import javax.ejb.Local;

import org.mywms.service.BasicService;

import de.linogistix.los.inventory.model.LOSStorageStrategy;

/**
 * @author krane
 *
 */
@Local
public interface LOSStorageStrategyService extends BasicService<LOSStorageStrategy> {
	public LOSStorageStrategy getByName( String name );
	public LOSStorageStrategy getDefault();
}
