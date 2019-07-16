/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package de.linogistix.los.location.service;

import javax.ejb.Local;

import org.mywms.service.BasicService;

import de.wms2.mywms.inventory.UnitLoadType;

/**
 * @see org.mywms.service.BasicService#get(String)
 * @author Taieb El Fakiri
 * @version $Revision$ provided by $Author$
 */
@Local
public interface UnitLoadTypeService
    extends BasicService<UnitLoadType>
{
}
