/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.facade;

import javax.ejb.Remote;

import org.mywms.model.Client;

import de.linogistix.los.query.BODTO;

/**
 * @author krane
 *
 */
@Remote
public interface LOSClientFacade  {
	
	public BODTO<Client> getDefaultClient();
	public BODTO<Client> getUsersClient();
	public BODTO<Client> getSystemClient();
	

}
