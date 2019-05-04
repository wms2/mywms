/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.Role;

/**
 * @see org.mywms.service.RoleService
 * @author Taieb El Fakiri
 * @version $Revision$ provided by $Author$
 */
@Stateless
public class RoleServiceBean
    extends BasicServiceBean<Role>
    implements RoleService
{

    /**
     * @see org.mywms.service.RoleService#getByName(java.lang.String)
     */
    public Role getByName(String name) throws EntityNotFoundException {
        if (name == null) {
            throw new NullPointerException("getByName: parameter == null");
        }

        Query query =
            manager.createQuery("SELECT r FROM "
                + Role.class.getSimpleName()
                + " r "
                + "WHERE r.name=:name");

        query.setParameter("name", name);

        try {
            Role r = (Role) query.getSingleResult();
            return r;
        }
        catch (NoResultException ex) {
            throw new EntityNotFoundException(
                ServiceExceptionKey.NO_ROLE_WITH_NAME);
        }
    }

    /**
     * @see org.mywms.service.RoleService#create(String)
     */
    @SuppressWarnings("unchecked")
    public Role create(String name) throws UniqueConstraintViolatedException {
        if (name == null)
            throw new NullPointerException(
                "Error > RoleServiceBean.createRole(String name) > name == null");

        Query query =
            manager.createQuery("SELECT ro FROM "
                + Role.class.getSimpleName()
                + " ro "
                + "WHERE ro.name=:na");
        query.setParameter("na", name);

        List<Role> roles = query.getResultList();

        if (roles.size() > 0) {
            throw new UniqueConstraintViolatedException(
                ServiceExceptionKey.ROLE_ALREADY_EXISTS);
        }

        Role newRole = new Role();
        newRole.setName(name);

        manager.persist(newRole);
        manager.flush();

        return newRole;
    }

}
