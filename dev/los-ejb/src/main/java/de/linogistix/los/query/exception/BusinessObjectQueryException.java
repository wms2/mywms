/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.query.exception;

import javax.ejb.ApplicationException;
import org.mywms.facade.FacadeException;

/**
 * Thrown if a BusinessObject can't be obtained from database. 
 * Or the Query itself produces exception. 
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@ApplicationException(rollback = true)
public class BusinessObjectQueryException extends FacadeException {

	private static final long serialVersionUID = 1L;

	public final static String RESOURCE_KEY = "BusinessException.QueryException";
	
    public final static String RESOURCE_KEY_CLIENTCONFLICT = "BusinessException.QueryExceptionClientConflict";
    
	/** Creates a new instance of BusinessObjectNotFoundException */	
	public BusinessObjectQueryException() {
		super("Query Exception", RESOURCE_KEY,
				new Object[0]);
	}
    
    /** Creates a new instance of BusinessObjectNotFoundException */	
	public BusinessObjectQueryException(String resourceKey, String param1) {
		super("Query Exception", resourceKey,
				new Object[]{param1});
	}
}
