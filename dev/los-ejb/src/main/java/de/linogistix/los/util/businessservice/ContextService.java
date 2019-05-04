/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.util.businessservice;

import javax.ejb.Local;

import org.mywms.model.BasicEntity;
import org.mywms.model.Client;
import org.mywms.model.User;

@Local
public interface ContextService {

	/**
	 * All session beans should be assigned to security domain "los-login".
	 * This means a client has to authenticate with username and password.
	 * The login will be checked against table mywms_user. 
	 * 
	 * This helper method will resolve the logged in user.
	 * 
	 * @return the logged in user
	 */
	User getCallersUser();
	public String getCallerUserName();

	/**
	 * All session beans should be assigned to security domain "los-login".
	 * This means a client has to authenticate with username and password.
	 * The login will be checked against table mywms_user. 
	 * 
	 * This helper method will resolve the client a logged in user is assigned to.
	 * 
	 * @return the client of the logged in user.
	 */
	Client getCallersClient();
	
	boolean checkClient(BasicEntity bo);
	
}
