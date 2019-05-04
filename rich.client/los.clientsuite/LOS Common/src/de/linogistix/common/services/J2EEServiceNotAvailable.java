/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.services;

/**
 *
 * @author trautm
 */
public class J2EEServiceNotAvailable extends J2EEServiceLocatorException{
    
    /** Creates a new instance of J2EEServiceNotAvailable */
    public J2EEServiceNotAvailable() {
        super("BusinessException.ServiceNotAvailable", new Object[0]);
    }
    
}
