/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * Users can log in and can operate the system.
 * 
 * @author Olaf Krause
 * @version $Revision$ provided by $Author$
 */
@Entity
@Table(name = "mywms_user")
public class User
    extends BasicClientAssignedEntity
{
    private static final long serialVersionUID = 1L;

    private String firstname = "";
    private String lastname = "";
    private String email = "";
    private String phone = "";

    @Column(nullable = false)
    private String password = "";

    @Column(nullable = false, unique = true)
    private String name = null;

    private String locale = Locale.getDefault().toString();

    @ManyToMany
    private List<Role> roles = new ArrayList<Role>();

    /**
     * Returns the user name, used to log into the system. The username
     * can be cleartext as well as a number. Please remark, that using
     * german umlauts can be difficult in several environments. The name
     * is system wide unique.
     * 
     * @return Returns the name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @see #getName()
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the email.
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * @param email The email to set.
     */

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return Returns the firstname.
     */
    public String getFirstname() {
        return this.firstname;
    }

    /**
     * @param firstname The firstname to set.
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * @return Returns the lastname.
     */
    public String getLastname() {
        return this.lastname;
    }

    /**
     * @param lastname The lastname to set.
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * Returns the password of the user. Please remark, that using
     * german umlauts can be difficult in several environments.
     * 
     * @return Returns the password.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * @see #getPassword()
     * @param password The password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return Returns the phone.
     */
    public String getPhone() {
        return this.phone;
    }

    /**
     * @param phone The phone to set.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return Returns the roles.
     */
    public List<Role> getRoles() {
        if (this.roles == null) {
            this.roles = new ArrayList<Role>();
        }
        return this.roles;
    }

    /**
     * @param roles The roles to set.
     */
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    /**
     * Tests whether user has de.mywms.model.Role corresponding to
     * org.mywms.globals.Role.
     * 
     * @see org.mywms.globals.Role
     * @param role Enum type for roles
     * @return true if user has role
     */
    public boolean hasRole(org.mywms.globals.Role role) {
        for (Role r: getRoles()) {
            if (r.getName().equals(role.toString()))
                return true;
        }
        return false;
    }

    /**
     * Tests whether user has de.mywms.model.Role with name role
     * 
     * @param name the name of a Role
     * @return true if user has role
     */
    public boolean hasRole(String name) {
        for (Role r: getRoles()) {
            if (r.getName().equals(name))
                return true;
        }
        return false;
    }

    /**
     * Tests whether user has de.mywms.model.Role
     * 
     * @param role
     * @return true if user has role
     */
    public boolean hasRole(Role role) {
        for (Role r: getRoles()) {
            if (r.equals(role))
                return true;
        }
        return false;
    }

    /**
     * @return Returns the locale.
     */
    public String getLocale() {
        return this.locale;
    }

    /**
     * @param locale The locale to set.
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Override
    public String toUniqueString() {
        return getName();
    }

    @Override
    public String toShortString() {
    	return super.toShortString() + "[name=" + name + "]";

    }
}
