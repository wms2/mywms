/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package de.linogistix.los.location.service;

import javax.ejb.Local;

import org.mywms.service.BasicService;

import de.wms2.mywms.strategy.Zone;

/**
 * This interface declares the service for the entity Zone.
 * 
 * @see org.mywms.service.BasicService#get(String)
 * @author Taieb El Fakiri
 * @version $Revision$ provided by $Author$
 */
@Local
public interface ZoneService
    extends BasicService<Zone>
{
}
