/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;

import java.security.Principal;

import javax.ejb.EJB;
import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import org.apache.log4j.Logger;

import org.mywms.model.User;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.UserService;

/**
 * @see org.mywms.facade.Authentication
 * @author Olaf Krause
 * @version $Revision$ provided by $Author$
 */
@Stateless
@PermitAll
public class AuthenticationBean
    implements Authentication
{

    private static final Logger log =
        Logger.getLogger(AuthenticationBean.class.getName());

    @Resource
    EJBContext context;

    @EJB
    UserService userService;

    /**
     * @see org.mywms.facade.Authentication#getUserInfo()
     */
    public AuthenticationInfoTO getUserInfo() {
        // check, if the user is valid logged in
        Principal principal = context.getCallerPrincipal();
        if (principal.getName() == null) {
            // user is not logged in
            return null;
        }

        try {
            User user = userService.getByUsername(principal.getName());
            AuthenticationInfoTO infoTO = new AuthenticationInfoTO(user);
            return infoTO;
        }
        catch (EntityNotFoundException ex) {
            log.warn("No User found: " + principal.getName());
            return null;
        }
    }
}
