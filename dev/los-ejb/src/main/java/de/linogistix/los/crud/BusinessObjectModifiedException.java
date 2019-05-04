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
public class BusinessObjectModifiedException extends FacadeException {

	private static final long serialVersionUID = 1L;

	/** Creates a new instance of BusinessObjectNotFoundException */	
	public BusinessObjectModifiedException(BasicEntity entity) {
		super("BusinessObject not found", "BusinessException.ObjectNotFound",
				new Object[]{entity});
	}

}
