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
 * Thrown if a BusinessObject can't be merged into an other.
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@ApplicationException(rollback = true)
public class BusinessObjectMergeException extends FacadeException {

	private static final long serialVersionUID = 1L;

	/** Creates a new instance of BusinessObjectNotFoundException */

	public BusinessObjectMergeException() {
		super("BusinessObject cannot be merged", "BusinessException.ObjectCannotBeMerged",
				new Object[0]);
	}
	
	public BusinessObjectMergeException(BasicEntity entity) {
		super("BusinessObject cannot be merged", "BusinessException.ObjectCannotBeMerged",
				new Object[]{entity});
	}

}
