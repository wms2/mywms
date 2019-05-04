/*
 * InternalErrorException.java
 *
 * Created on 13. Oktober 2006, 05:56
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.exception;

import de.linogistix.common.res.CommonBundleResolver;
import org.mywms.facade.FacadeException;

/**
 * Fallback Exception: Summarizes all Exceptions that are no ServiceExceptions and therefore can't be 
 * localized and therefore should not be presented to the user.
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class InternalErrorException extends FacadeException {

    /**
     * Creates a new instance of InternalErrorException
     */
    public InternalErrorException(Throwable t) {
        super("Internal Error", "BusinessException.InternalError", new Object[]{t.getMessage()});
        setBundleResolver(CommonBundleResolver.class);
    }

    /**
     * Creates a new instance of InternalErrorException
     */
    public InternalErrorException(String message) {

        super("Internal Error: " + message, "BusinessException.InternalError", new Object[]{message});
        setBundleResolver(CommonBundleResolver.class);
    }

    /**
     * Creates a new instance of InternalErrorException
     */
    public InternalErrorException() {

        super("Internal Error", "BusinessException.InternalError", new String[]{""});
        setBundleResolver(CommonBundleResolver.class);
    }
}
