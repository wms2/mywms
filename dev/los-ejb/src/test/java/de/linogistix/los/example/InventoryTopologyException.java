/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.example;

import javax.ejb.ApplicationException;
import org.mywms.facade.FacadeException;

/**
 *  Thrown if creating a topology for the warehouse failed.
 *  
 * @author trautm
 *
 */
@ApplicationException(rollback = true)
public class InventoryTopologyException extends FacadeException {

	private static final long serialVersionUID = 1L;
	
	public static final String RESOURCE_KEY = "BusinessException.TopologyFailed";
	public InventoryTopologyException() {
		super("creation of topology failed", RESOURCE_KEY, new Object[0]);
		
	}

	

	
}
