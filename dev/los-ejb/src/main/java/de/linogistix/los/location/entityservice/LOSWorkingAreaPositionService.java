/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.entityservice;


import javax.ejb.Local;

import org.mywms.service.BasicService;

import de.linogistix.los.location.model.LOSWorkingAreaPosition;



/**
 * @author krane
 *
 */
@Local
public interface LOSWorkingAreaPositionService extends BasicService<LOSWorkingAreaPosition> {
	
}