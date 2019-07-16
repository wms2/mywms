/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.entityservice;

import javax.ejb.Stateless;

import org.mywms.service.BasicServiceBean;

import de.wms2.mywms.strategy.FixAssignment;

@Stateless
public class LOSFixedLocationAssignmentServiceBean 
				extends BasicServiceBean<FixAssignment>
				implements LOSFixedLocationAssignmentService 
{
}
