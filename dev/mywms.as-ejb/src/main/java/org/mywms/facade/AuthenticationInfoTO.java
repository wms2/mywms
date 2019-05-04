/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.facade;

import java.io.Serializable;
import java.util.List;

/**
 * Transfers the authentication info.
 * 
 * @author Olaf Krause
 * @version $Revision$ provided by $Author$
 */
public class AuthenticationInfoTO
    implements Serializable
{
    private static final long serialVersionUID = 1L;

    public String userName;
    public String firstName;
    public String lastName;
    public String[] roles;
    public String clientName;
    public String clientNumber;
    public String locale;
    public int lock;

    /**
     * Creates a new AuthenticationInfoTO, using the data of the given
     * User entity.
     * 
     * @param user the origin of the data to be transfered
     */
    public AuthenticationInfoTO(org.mywms.model.User user) {
        this.userName = user.getName();
        this.firstName = user.getFirstname();
        this.lastName = user.getLastname();
        this.clientName =
            user.getClient() == null ? null : user.getClient().getName();
        this.clientNumber =
            user.getClient() == null ? null : user.getClient().getNumber();
        this.locale = user.getLocale();
        this.lock = user.getLock();

        List<org.mywms.model.Role> rolesList = user.getRoles();
        int n = rolesList.size();
        this.roles = new String[n];
        for (int i = 0; i < n; i++) {
            this.roles[i] = rolesList.get(i).getName();
        }
    }
}
