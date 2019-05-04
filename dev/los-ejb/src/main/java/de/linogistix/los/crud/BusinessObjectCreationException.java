/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.crud;

import javax.ejb.ApplicationException;

import org.mywms.facade.FacadeException;
import org.mywms.model.BasicEntity;

/**
 * Thrown if a BusinessObject can't be obtained from database.
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@ApplicationException(rollback = true)
public class BusinessObjectCreationException extends FacadeException {

	private static final long serialVersionUID = 1L;

	public static final String MISSING_FIELD_KEY = "BusinessException.MissingField";
	
	/** Creates a new instance of BusinessObjectNotFoundException */

	public BusinessObjectCreationException() {
		super("BusinessObject cannot be created", "BusinessException.CannotBeCreated",
				new Object[0]);
//		setBundleResolver(BundleResolver.class);
	}
	
	public BusinessObjectCreationException(BasicEntity entity) {
		super("BusinessObject cannot be created", "BusinessException.CannotBeCreated",
				new Object[]{entity});
//		setBundleResolver(BundleResolver.class);
	}
	
	@SuppressWarnings("unchecked")
	public BusinessObjectCreationException(String msg, String key, String[] params, Class bundleResolver){
		super(msg, key, params);
		setBundleResolver(bundleResolver);
	}

}
