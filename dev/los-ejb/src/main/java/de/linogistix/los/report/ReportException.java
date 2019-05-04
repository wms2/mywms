/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.report;


import java.util.Arrays;
import javax.ejb.ApplicationException;
import org.mywms.facade.FacadeException;

import de.linogistix.los.res.BundleResolver;

/**
 *
 * @author trautm
 */
@ApplicationException(rollback=false)
public class ReportException extends FacadeException{
    
    private static final long serialVersionUID = 1L;

	private static final String resourceBundle = "de.linogistix.los.res.Bundle";
	
	private ReportExceptionKey invKey;
	
    public ReportException(){
        this(ReportExceptionKey.CREATION_FAILED, "" );
        
    }
	public ReportException(ReportExceptionKey key, Object[] parameters){
		super(key.name() + ": " + Arrays.toString(parameters), key.name(), parameters, resourceBundle);
        invKey = key;
        setBundleResolver(BundleResolver.class);
	}
    
    public ReportException(ReportExceptionKey key, String param1){
		
		super(key.name() + ":" +param1, key.name(), new Object[]{param1}, resourceBundle);
        invKey = key;
        setBundleResolver(BundleResolver.class);
	}

	public ReportExceptionKey getReportExceptionKey() {
		return invKey;
	}
}

