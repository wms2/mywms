/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.entityservice;

import javax.ejb.Local;

import org.mywms.model.BasicEntity;

import de.linogistix.los.runtime.BusinessObjectSecurityException;

@Local
public interface BusinessObjectLockService {

	void lock(BasicEntity entity, int lock, String lockCause) throws BusinessObjectSecurityException;
	
}
