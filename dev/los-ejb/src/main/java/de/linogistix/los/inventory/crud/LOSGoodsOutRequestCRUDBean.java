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

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.service.BasicService;
import org.mywms.service.ConstraintViolatedException;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.crud.BusinessObjectDeleteException;
import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.service.LOSGoodsOutRequestService;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;



/**
 * @author trautm
 *
 */
@Stateless
public class LOSGoodsOutRequestCRUDBean extends BusinessObjectCRUDBean<LOSGoodsOutRequest> implements LOSGoodsOutRequestCRUDRemote{

	@EJB 
	LOSGoodsOutRequestService service;
	
	@Override
	protected BasicService<LOSGoodsOutRequest> getBasicService() {	
		return service;
	}
	
	@Override
	public void delete(LOSGoodsOutRequest entity)
			throws BusinessObjectNotFoundException,
			BusinessObjectDeleteException, BusinessObjectSecurityException {
		try {
			service.delete(entity);
		} catch (ConstraintViolatedException e) {
			throw new BusinessObjectDeleteException();
		}
	}
}
