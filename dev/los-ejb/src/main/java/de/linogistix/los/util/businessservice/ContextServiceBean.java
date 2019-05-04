/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.util.businessservice;

import java.security.Principal;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.mywms.model.BasicClientAssignedEntity;
import org.mywms.model.BasicEntity;
import org.mywms.model.Client;
import org.mywms.model.User;
import org.mywms.service.UserService;

@Stateless
public class ContextServiceBean implements ContextService {

	private static final Logger log = Logger.getLogger(ContextServiceBean.class);

	@EJB
	UserService userService;
	
	@Resource
	EJBContext context;

	/*
	 * @see de.linogistix.los.util.businessservice.ContextService#getCallersUser()
	 */
	public User getCallersUser() {
		String logStr="getCallersUser ";
		if(context==null) {
			log.error(logStr+"Context is null!");
		}
		
		Principal principal = context.getCallerPrincipal();

		if (principal.getName() == null) {
			log.error(logStr+"Principal not found!");
			return null;
		}

		try {
			User user = userService.getByUsername(principal.getName());
			return user;
		} catch (org.mywms.service.EntityNotFoundException ex) {
			return null;
		}
	}
	public String getCallerUserName() {
		Principal principal = context.getCallerPrincipal();
		if( principal == null ) {
			return null;
		}
		return principal.getName(); 
	}
	/*
	 * @see de.linogistix.los.util.businessservice.ContextService#getCallersClient()
	 */
	public Client getCallersClient() {
		User user = getCallersUser();
		if (user != null) {
			return getCallersUser().getClient();
		}
		return null;
	}
	
	/**
	   *Checks whether <code>T</code> of Type {@link BasicEntity} might be changed by
	   * callers user.
	   * @return true if caller has role {@link org.mywms.globals.Role.ADMIN} or belongs to the same client than <code>T</code>
	   */
	  public boolean checkClient(BasicEntity bo) {
		  String logStr="checkClient ";
	    if (bo == null) {
	      throw new NullPointerException("bo must not be null");
	    }
	    
	    User callersUser = getCallersUser();
	    
	    if (callersUser == null) {
	      log.error(logStr+"Cannot identify callers User");
	      return false;
	    }
	    
	    if (callersUser.hasRole(org.mywms.globals.Role.ADMIN)) {
	      // anything goes
	      return true;
	    }
	    
	    if (bo instanceof BasicClientAssignedEntity) {
	      BasicClientAssignedEntity bcae = (BasicClientAssignedEntity) bo;
	      if (bcae.getClient() == null) {
	        // no client assigned
	        return true;
	      }
	      if (callersUser.getClient() == null) {
	        return false;
	      }
	      if (bcae.getClient().equals(callersUser.getClient())) {
	    	  return true;
	      } else if (callersUser.getClient().isSystemClient()){
	    	  return true;
	      } else{
	    	  return false;
	      }
	    } else{
	    	return true;
	    }
	  }

}
