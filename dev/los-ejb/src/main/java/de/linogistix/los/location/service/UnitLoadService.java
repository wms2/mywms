/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package de.linogistix.los.location.service;

import javax.ejb.Local;

import org.mywms.service.BasicService;

import de.wms2.mywms.inventory.UnitLoad;

/**
 * This interface declares the service for the entity UnitLoad. For this
 * service the method <code>get(String name)</code> will overwrite,
 * because the entity <code>UnitLaod</code> does not contain the
 * property <code>name</code>.
 * 
 * @see org.mywms.service.BasicService#get(String)
 * @author Taieb El Fakiri
 * @version $Revision$ provided by $Author$
 */
@Local
public interface UnitLoadService
    extends BasicService<UnitLoad>
{
}
