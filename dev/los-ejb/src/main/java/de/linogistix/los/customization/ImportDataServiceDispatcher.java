/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.customization;

import java.util.HashMap;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;

@Local
public interface ImportDataServiceDispatcher {

	/**
	 * 
	 * @param className the classname of the entity to create
	 * @param dataRecord the xml structure to parse
	 * @return An Object (i.e. an String) indicating what has been imported. Null if nothing has been imported.
	 * @throws FacadeException
	 */
	public Object handleDataRecord(String className, HashMap<String, String> dataRecord) throws FacadeException;
	
}
