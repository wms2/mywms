/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.service;

import org.mywms.globals.ServiceExceptionKey;

/**
 * This exception is thrown, if the requested entity could not be found.
 * 
 * @author Olaf Krause
 * @version $Revision$ provided by $Author$
 */
public class EntityNotFoundException
    extends ServiceException
{
    private static final long serialVersionUID = 1L;

    public EntityNotFoundException(ServiceExceptionKey messageKey) {
        super(messageKey);
    }
}
