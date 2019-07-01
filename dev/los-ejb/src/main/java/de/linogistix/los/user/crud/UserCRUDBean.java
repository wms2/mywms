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

import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

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
import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.user.UserBusiness;


/**
 * @author trautm
 *
 */
@Stateless
public class UserCRUDBean extends BusinessObjectCRUDBean<User> implements UserCRUDRemote {

	private static final Logger log = Logger.getLogger(UserCRUDBean.class);
	
	@EJB 
	UserService userService;
	
	@Inject
	private UserBusiness userBusiness;
	@Inject
	private PersistenceManager manager;

	@EJB
	LOSSystemPropertyService serviceConfig;
	
	@Override
	protected BasicService<User> getBasicService() {
		
		return userService;
	}

    @Override
    public User create(User entity) throws BusinessObjectExistsException, BusinessObjectCreationException, BusinessObjectSecurityException {
		Locale locale = userBusiness.getCurrentUsersLocale();
    	User user;
		try {
			user = userBusiness.createUser(entity.getClient(), entity.getName(), entity.getPassword());
		} catch (BusinessException e) {
			log.error("Cannot create user. " + e.getMessage());
			throw new BusinessObjectCreationException(e.getLocalizedMessage(locale), null, new String[] {}, null);
		}
    	user.setFirstname(entity.getFirstname());
    	user.setLastname(entity.getLastname());
    	user.setLocale(entity.getLocale());
    	user.setPhone(entity.getPhone());
    	user.setEmail(entity.getEmail());
        
    	user.setAdditionalContent(entity.getAdditionalContent());
        
    	user.setLock(entity.getLock());
        
        for(Role r : entity.getRoles()){
        	user.getRoles().add(r);
    	}
        return user;
    }
    
    @Override
    public void update(User entity) throws BusinessObjectNotFoundException,
    		BusinessObjectModifiedException, BusinessObjectMergeException,
    		BusinessObjectSecurityException, FacadeException {

    	String password = entity.getPassword();
    	User user = manager.find(User.class, entity.getId());
    	if (!user.getPassword().equals(entity.getPassword())){
    		try {
				userBusiness.changePassword(entity, password, true);
			} catch (BusinessException e) {
				throw e.toFacadeException();
			}
    	}
    	super.update(entity);
    }
}
