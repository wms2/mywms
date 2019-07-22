/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package de.linogistix.los.location.entityservice;

import javax.ejb.Local;

import org.mywms.service.BasicService;

import de.wms2.mywms.location.Area;

/**
 * 
 * @author Markus Jordan
 * @version $Revision: 339 $ provided by $Author: trautmann $
 */
@Local
public interface LOSAreaService
	extends BasicService<Area>
{
}