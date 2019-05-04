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

package de.linogistix.los.inventory.crud;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.service.QueryAdviceService;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.service.BasicService;



/**
 * @author trautm
 *
 */
@Stateless
public class LOSAdviceCRUDBean extends BusinessObjectCRUDBean<LOSAdvice> implements LOSAdviceCRUDRemote {

	@EJB 
	QueryAdviceService service;
	
	@Override
	protected BasicService<LOSAdvice> getBasicService() {
		
		return service;
	}
}
