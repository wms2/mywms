/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.service;

import javax.ejb.Local;

import org.mywms.model.UnitLoadType;

/**
 * This interface declares the service for the entity
 * UnitLoadTypeService. For this service it is save to call the
 * <code>get(String name)</code> method.
 * 
 * @see org.mywms.service.BasicService#get(String)
 * @author Taieb El Fakiri
 * @version $Revision$ provided by $Author$
 */
@Local
public interface UnitLoadTypeService
    extends BasicService<UnitLoadType>
{
	
	
    // ----------------------------------------------------------------
    // set of individual methods
    // ----------------------------------------------------------------

    /**
     * Checks the specified name for being unique. 
     * If the name is valid, a new UnitLoadType will
     * be created and added to persistence
     * context.
     * 
     * @param name the name of the new UnitLoadType.
     * @throws UniqueConstraintViolatedException if system
     *             already a UnitLoadType with Name name.
     * @throws NullPointerException if one of the parameters is null.
     */
    UnitLoadType create(String name)
        throws UniqueConstraintViolatedException;

    /**
     * Resolves a UnitLoadType by its name
     * 
     * @param name the name to search for.
     * @return a UnitLoadType that is unique 
     * @throws EntityNotFoundException if there is no UnitLoadType with name
     */
    UnitLoadType getByName(String name)
        throws EntityNotFoundException;
    
}
