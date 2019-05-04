/*
 * UserCRUDRemote.java
 *
 * Created on 14. September 2006, 06:53
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.user.crud;

import javax.ejb.Remote;

import org.mywms.model.Role;

import de.linogistix.los.crud.BusinessObjectCRUDRemote;

/**
 * CRUD operations for User entities
 * @see  BusinessObjectCRUDRemote
 * 
 * @author trautm
 *
 */
@Remote
public interface RoleCRUDRemote extends BusinessObjectCRUDRemote<Role>{
	
}
