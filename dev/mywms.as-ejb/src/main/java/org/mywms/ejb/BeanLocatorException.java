/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.ejb;

/**
 * The BeanLocatorException is thrown by the BeanLocator. This class has
 * been founded with the friendly support of Rene Preissel, tutor at
 * OOSE.
 * 
 * @see BeanLocator
 * @version $Revision$ provided by $Author$
 */
public class BeanLocatorException
    extends RuntimeException
{

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new BeanLocatorException.
     */
    public BeanLocatorException() {
        super();
    }

    /**
     * Creates a new BeanLocatorException.
     * 
     * @param message the message of the exception
     */
    public BeanLocatorException(String message) {
        super(message);
    }

    /**
     * Creates a new BeanLocatorException.
     * 
     * @param cause the (root) cause of this exception
     */
    public BeanLocatorException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new BeanLocatorException.
     * 
     * @param message the message of the exception
     * @param cause the (root) cause of this exception
     */
    public BeanLocatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
