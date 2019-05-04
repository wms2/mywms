/*
 * UserCRUDBean.java
 *
 * Created on 20.02.2007, 18:37:29
 *
 * Copyright (c) 2006/2007 LinogistiX GmbH. All rights reserved.
 *
 * <a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.user.crud;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Role;
import org.mywms.model.User;
import org.mywms.service.BasicService;
import org.mywms.service.UserService;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.crud.BusinessObjectCreationException;
import de.linogistix.los.crud.BusinessObjectExistsException;
import de.linogistix.los.crud.BusinessObjectMergeException;
import de.linogistix.los.crud.BusinessObjectModifiedException;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;
import de.linogistix.los.util.entityservice.LOSSystemPropertyService;


/**
 * @author trautm
 *
 */
@Stateless
public class UserCRUDBean extends BusinessObjectCRUDBean<User> implements UserCRUDRemote {

	private static final Logger log = Logger.getLogger(UserCRUDBean.class);
	
	@EJB 
	UserService userService;
	
	@EJB
	LOSSystemPropertyService serviceConfig;
	
	@Override
	protected BasicService<User> getBasicService() {
		
		return userService;
	}

    @Override
    public User create(User entity) throws BusinessObjectExistsException, BusinessObjectCreationException, BusinessObjectSecurityException {
        User u = userService.create(entity.getClient(), entity.getName(), entity.getFirstname(), entity.getLastname(), entity.getPassword());
        
        u.setLocale(entity.getLocale());
        u.setPhone(entity.getPhone());
        u.setEmail(entity.getEmail());
        
        u.setAdditionalContent(entity.getAdditionalContent());
        
        u.setLock(entity.getLock());
        
        for(Role r : entity.getRoles()){
        	u.getRoles().add(r);
    	}
        return u;
    }
    
    @Override
    public void update(User entity) throws BusinessObjectNotFoundException,
    		BusinessObjectModifiedException, BusinessObjectMergeException,
    		BusinessObjectSecurityException, FacadeException {

    	String password = entity.getPassword();
    	User u = manager.find(User.class, entity.getId());
    	if (!u.getPassword().equals(entity.getPassword())){
    		Boolean checkWeak = Boolean.FALSE;
    		try{
    			checkWeak = serviceConfig.getBoolean(UserCRUDRemote.CONFKEY_WEAKPASS);
    		} catch (Throwable t){
    			log.warn("Could not resolve CONFKEY_WEAKPASS: "  + t.getMessage());
    			checkWeak = Boolean.FALSE;
    		}
    		entity = userService.changePasswd(entity, password, checkWeak.booleanValue());
    	}
    	super.update(entity);
    }

    
    
}
