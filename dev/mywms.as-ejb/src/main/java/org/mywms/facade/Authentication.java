/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;

import javax.ejb.Remote;

/**
 * This fassade declares the interface to get info about the user and
 * the roles used by the application server.
 * 
 * @author Olaf Krause
 * @version $Revision$ provided by $Author$
 */
@Remote
public interface Authentication {

    /**
     * Returns the authentication info for the current request.
     * 
     * @return the AuthenticationInfoTO for this request
     */
    AuthenticationInfoTO getUserInfo();
}
