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

import org.mywms.model.Role;
import org.mywms.service.BasicService;
import org.mywms.service.RoleService;

import de.linogistix.los.crud.BusinessObjectCRUDBean;


/**
 * @author trautm
 *
 */
@Stateless
public class RoleCRUDBean extends BusinessObjectCRUDBean<Role> implements RoleCRUDRemote {

	@EJB 
	RoleService roleService;
	
	@Override
	protected BasicService<Role> getBasicService() {
		
		return roleService;
	}
}
