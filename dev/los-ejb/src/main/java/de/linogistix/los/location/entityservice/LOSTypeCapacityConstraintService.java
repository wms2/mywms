/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.entityservice;

import javax.ejb.Local;

import de.linogistix.los.location.model.LOSTypeCapacityConstraint;
import org.mywms.service.BasicService;

/**
 *
 * @author Jordan
 */
@Local
public interface LOSTypeCapacityConstraintService 
        extends BasicService<LOSTypeCapacityConstraint>
{
    
}
