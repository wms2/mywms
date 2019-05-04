/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.exception;

import de.linogistix.common.res.CommonBundleResolver;
import org.mywms.facade.FacadeException;

/**
 *
 * @author artur
 */
public class ErrorOccurredException extends FacadeException {

    /**
     * Only needed to flag something that goings wrong. e.g. by filling not all fields
     * 
     */
    public ErrorOccurredException() {
        super("An error occurred", "ErrorOccurredException.msg", new Object[0]);
        setBundleResolver(CommonBundleResolver.class);
    }
    
    /**
     * Only needed to flag something that goings wrong. e.g. by filling not all fields
     * 
     */
    public ErrorOccurredException(Class bundleResolver, String key) {
        super(key,key,new Object[0]);
        setBundleResolver(bundleResolver);
    }

    /**
     * Constructs an instance of <code>ErrorException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ErrorOccurredException(String msg) {
        super(msg, msg, new Object[0]);
        setBundleResolver(CommonBundleResolver.class);
    }
}
