/*
 * Copyright (c) 2006-2013 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.user;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.mywms.facade.Authentication;
import org.mywms.facade.AuthenticationInfoTO;
import org.mywms.facade.FacadeException;

import de.linogistix.los.common.exception.LOSExceptionRB;
import de.linogistix.los.model.LOSCommonPropertyKey;
import de.linogistix.los.res.BundleResolver;
import de.linogistix.los.util.entityservice.LOSSystemPropertyService;

/**
 *
 * @author trautm
 */
@Stateless
@PermitAll
public class LoginServiceBean implements de.linogistix.los.user.LoginServiceRemote {
	private static final Logger log = Logger.getLogger(LoginServiceBean.class);

	@EJB
	private Authentication authentification;
	@EJB
	private LOSSystemPropertyService propertyService;
	
	public boolean loginCheck() throws SecurityException {
		String logStr = "loginCheck ";
		log.warn(logStr+"Usage of deprecated login.");
		throw new SecurityException();
	}
	
	public boolean loginCheck(String workstation, String userName, String version) throws FacadeException, SecurityException {
		String logStr = "loginCheck ";

		if( version==null || version.length()==0 ) {
			log.warn(logStr+"no version, no check");
			return true;
		}

		String seversClientVersion = propertyService.getStringDefault(workstation, LOSCommonPropertyKey.NBCLIENT_VERSION_MATCHER, ".*");
		if( seversClientVersion==null || seversClientVersion.length()==0 ) {
			return true;
		}
	
		if (version.matches(seversClientVersion)) {
			return true;
		}
		
		log.warn(logStr+"version check failed. version="+version+", workstation="+workstation+", user="+userName+", expression="+seversClientVersion);
		throw new LOSExceptionRB( "CLIENT_VERSION_OUTDATED", new String[]{version,seversClientVersion}, BundleResolver.class );
	}

	public boolean loginCheck(String workstation, String userName) throws FacadeException, SecurityException {
		return true;
	}

	public AuthenticationInfoTO getUserInfo() {
		return this.authentification.getUserInfo();
	}  
  
}
