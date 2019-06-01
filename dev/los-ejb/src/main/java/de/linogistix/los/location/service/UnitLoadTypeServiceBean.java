/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package de.linogistix.los.location.service;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.mywms.globals.ServiceExceptionKey;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.UniqueConstraintViolatedException;

import de.wms2.mywms.inventory.UnitLoadType;

/**
 * @see de.linogistix.los.location.service.UnitLoadTypeService
 * @author Taieb El Fakiri
 * @version $Revision$ provided by $Author$
 */
@Stateless
public class UnitLoadTypeServiceBean
    extends BasicServiceBean<UnitLoadType>
    implements UnitLoadTypeService
{
    /**
     * @see de.linogistix.los.location.service.UnitLoadTypeService#create(Client, String)
     */
    public UnitLoadType create(String name)
        throws UniqueConstraintViolatedException
    {
        UnitLoadType ulType = new UnitLoadType();
        ulType.setName(name);
        manager.persist(ulType);
        try {
            manager.flush();
        }
        catch (PersistenceException pe) {
            throw new UniqueConstraintViolatedException(
                ServiceExceptionKey.UNITLOADTYPE_NAME_NOT_UNIQUE);
        }
        return ulType;
    }

    /**
     * @see de.linogistix.los.location.service.UnitLoadTypeService#getByName(Client,
     *      String)
     */
    public UnitLoadType getByName(String name)
        throws EntityNotFoundException
    {
        Query query =
            manager.createQuery("SELECT ult FROM "
                + UnitLoadType.class.getSimpleName()
                + " ult "
                + "WHERE ult.name=:name ");

        query.setParameter("name", name);

        try {
            UnitLoadType ult = (UnitLoadType) query.getSingleResult();
            return ult;
        }
        catch (NoResultException ex) {
            throw new EntityNotFoundException(
                ServiceExceptionKey.NO_UNITLOADTYPE_WITH_NAME);
        }
    }

}
