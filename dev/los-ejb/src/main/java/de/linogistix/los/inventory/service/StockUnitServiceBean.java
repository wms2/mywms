/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package de.linogistix.los.inventory.service;

import javax.ejb.Stateless;

import org.mywms.service.BasicServiceBean;

import de.wms2.mywms.inventory.StockUnit;

/**
 * @see de.linogistix.los.inventory.service.StockUnitService
 * @author Taieb El Fakiri
 * @version $Revision$ provided by $Author$
 */
@Stateless
public class StockUnitServiceBean
    extends BasicServiceBean<StockUnit>
    implements StockUnitService
{
}
