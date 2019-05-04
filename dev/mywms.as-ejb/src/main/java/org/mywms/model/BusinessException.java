/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.model;

/**
 * This is an exception which can be thrown by BusinessObjects, if a
 * parameter is set in a wrong range or any other business constraint
 * would be violated. The BusinessException has been tagged a
 * RuntimeException to ease development. Nevertheless, the
 * BusinessException is declared and documented for each BusinessObject.
 * 
 * @author Olaf Krause
 * @version $Revision$ provided by $Author$
 */
public class BusinessException
    extends java.lang.RuntimeException
{

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of <code>BusinessException</code>
     * without detail message.
     */
    public BusinessException() {
    }

    /**
     * Constructs an instance of <code>BusinessException</code> with
     * the specified detail message.
     * 
     * @param msg the detail message.
     */
    public BusinessException(String msg) {
        super(msg);
    }
}
