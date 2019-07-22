/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package de.linogistix.los.location.service;

import javax.ejb.Stateless;

import org.mywms.service.BasicServiceBean;

import de.wms2.mywms.inventory.UnitLoad;

/**
 * @see de.linogistix.los.location.service.UnitLoadService
 * @author Taieb El Fakiri
 * @version $Revision$ provided by $Author$
 */
@Stateless
public class UnitLoadServiceBean
    extends BasicServiceBean<UnitLoad>
    implements UnitLoadService
{
}
