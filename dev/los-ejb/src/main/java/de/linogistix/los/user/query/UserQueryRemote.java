/*
 * StorageLocationQueryRemote.java
 *
 * Created on 14. September 2006, 06:59
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.user.query;


import java.util.List;

import javax.ejb.Remote;

import org.mywms.model.User;

import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.QueryDetail;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Remote
public interface UserQueryRemote extends BusinessObjectQueryRemote<User>{ 
  
	/**
	 * 
	 * @param userName
	 * @param detail might be null.
	 * @return
	 */
	List<User> queryByName(String userName, QueryDetail detail);
	
}
