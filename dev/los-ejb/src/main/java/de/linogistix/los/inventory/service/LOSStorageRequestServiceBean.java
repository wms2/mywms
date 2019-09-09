/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package de.linogistix.los.inventory.service;

import javax.ejb.Stateless;

import org.mywms.service.BasicServiceBean;

import de.wms2.mywms.transport.TransportOrder;

/**
 *  
 * @author Taieb El Fakiri
 * @version $Revision: 276 $ provided by $Author: trautmann $
 */
@Stateless
public class LOSStorageRequestServiceBean
        extends BasicServiceBean<TransportOrder>
        implements LOSStorageRequestService {
}
