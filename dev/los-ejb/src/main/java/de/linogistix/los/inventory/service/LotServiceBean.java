/*
 * Copyright (c) 2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package de.linogistix.los.inventory.service;

import javax.ejb.Stateless;

import org.mywms.service.BasicServiceBean;

import de.wms2.mywms.inventory.Lot;

/**
 * @author okrause
 */
@Stateless
public class LotServiceBean
    extends BasicServiceBean<Lot>
    implements LotService
{
}
