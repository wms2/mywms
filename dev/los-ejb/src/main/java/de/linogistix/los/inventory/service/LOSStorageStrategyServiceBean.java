/*
 * Copyright (c) 2010 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-3PL
 */
package de.linogistix.los.inventory.service;

import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.mywms.service.BasicServiceBean;

import de.wms2.mywms.strategy.StorageStrategy;

/**
 * @author krane
 *
 */
@Stateless
public class LOSStorageStrategyServiceBean extends BasicServiceBean<StorageStrategy> implements LOSStorageStrategyService {
	Logger log = Logger.getLogger(LOSStorageStrategyServiceBean.class);
}
