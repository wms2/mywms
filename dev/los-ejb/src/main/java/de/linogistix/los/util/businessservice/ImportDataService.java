/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.util.businessservice;

import java.util.List;

import javax.ejb.Remote;

import org.mywms.facade.FacadeException;

@Remote
public interface ImportDataService {

	/**
	 * 
	 * @param className the classname of entity to create
	 * @param data the data to parse, e.g. XML
	 * @return a list of objects indicating what has been imported - null if nothing has been imported. List might contain null values!
	 * @throws ImportDataException
	 * @throws FacadeException
	 */
	public List<Object> importData(String className, byte[] data) throws ImportDataException, FacadeException;
	
}
