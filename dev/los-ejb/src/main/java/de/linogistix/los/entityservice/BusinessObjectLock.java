/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.entityservice;

import java.io.Serializable;

public interface BusinessObjectLock extends Serializable{
	
	int getLock();
	
	String getMessage();
	
	@SuppressWarnings("unchecked")
	Class getBundleResolver();
	
	String getMessageKey();
	
}
