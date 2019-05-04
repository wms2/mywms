/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.facade;

import javax.ejb.Remote;

@Remote
public interface VersionFacade {

	public String getInfo();

	public String getTitle();

	public String getVersion();
}
