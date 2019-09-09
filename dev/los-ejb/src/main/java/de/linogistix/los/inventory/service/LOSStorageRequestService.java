/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package de.linogistix.los.inventory.service;

import javax.ejb.Local;

import org.mywms.service.BasicService;

import de.wms2.mywms.transport.TransportOrder;


@Local
public interface LOSStorageRequestService	extends BasicService<TransportOrder>
{
}
