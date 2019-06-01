/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.entityservice;

import javax.ejb.Local;

import org.mywms.service.BasicService;

import de.wms2.mywms.strategy.TypeCapacityConstraint;

/**
 *
 * @author Jordan
 */
@Local
public interface LOSTypeCapacityConstraintService 
        extends BasicService<TypeCapacityConstraint>
{
    
}
