/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package de.linogistix.los.location.entityservice;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.mywms.service.BasicServiceBean;

import de.wms2.mywms.location.Area;
import de.wms2.mywms.location.AreaEntityService;

/**
 * 
 * @author Markus Jordan
 * @version $Revision: 339 $ provided by $Author: trautmann $
 */
@Stateless
public class LOSAreaServiceBean extends BasicServiceBean<Area> implements LOSAreaService, LOSAreaServiceRemote {
	@Inject
	private AreaEntityService areaEntityService;

	public Area getDefault() {
		return areaEntityService.getDefault();
	}

}
