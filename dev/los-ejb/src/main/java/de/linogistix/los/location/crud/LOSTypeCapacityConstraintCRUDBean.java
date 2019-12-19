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

package de.linogistix.los.location.crud;

import java.math.BigDecimal;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.facade.FacadeException;
import org.mywms.service.BasicService;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.crud.BusinessObjectCreationException;
import de.linogistix.los.crud.BusinessObjectExistsException;
import de.linogistix.los.crud.BusinessObjectMergeException;
import de.linogistix.los.crud.BusinessObjectModifiedException;
import de.linogistix.los.location.entityservice.LOSTypeCapacityConstraintService;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.exception.LOSLocationExceptionKey;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;
import de.wms2.mywms.strategy.TypeCapacityConstraint;

/**
 * @author trautm
 *
 */
@Stateless
public class LOSTypeCapacityConstraintCRUDBean extends BusinessObjectCRUDBean<TypeCapacityConstraint> implements LOSTypeCapacityConstraintCRUDRemote {

	@EJB 
	LOSTypeCapacityConstraintService service;
	
	@Override
	protected BasicService<TypeCapacityConstraint> getBasicService() {		
		return service;
	}
	
	@Override
	public TypeCapacityConstraint create(TypeCapacityConstraint entity) throws BusinessObjectExistsException, BusinessObjectCreationException, BusinessObjectSecurityException {
		// TODO: make it possible to throw a regular i18n exception
		try {
			checkValues( entity );
		}
		catch( FacadeException e ) {
			throw new BusinessObjectCreationException(e.getLocalizedMessage(), null, null, null);
		}
		return super.create(entity);
	}
	
	public void update(TypeCapacityConstraint entity) throws BusinessObjectNotFoundException,	BusinessObjectModifiedException, BusinessObjectMergeException, BusinessObjectSecurityException, FacadeException {
		checkValues( entity );
		super.update(entity);
	}
	
	
	protected void checkValues(TypeCapacityConstraint entity) throws FacadeException {
		if( entity.getAllocation() == null ) {
			throw new LOSLocationException(LOSLocationExceptionKey.ALLOCATION_MANDATORY, new Object[]{});
		}
		if( BigDecimal.ZERO.compareTo(entity.getAllocation())>=0 ) {
			throw new LOSLocationException(LOSLocationExceptionKey.ALLOCATION_MORE_THAN_0, new Object[]{});
		}
	}
}
