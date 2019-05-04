/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */

package de.linogistix.los.user;

import javax.ejb.Remote;

import org.mywms.facade.Authentication;
import org.mywms.facade.FacadeException;


/**
 * Tests whether the User login will be accepted.
 * 
 */
@Remote
public interface LoginServiceRemote extends Authentication{
  
  /**
   * User has to authenticate, otherwise a {@link SecurityException} is thrown.
   */
	boolean loginCheck(String workstation, String userName, String version) throws FacadeException, SecurityException;

	boolean loginCheck(String workstation, String userName) throws FacadeException, SecurityException;
	
	boolean loginCheck() throws SecurityException;
}
