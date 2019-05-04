/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.exception;

import javax.ejb.ApplicationException;

import org.mywms.globals.ServiceExceptionKey;
import org.mywms.service.ServiceException;

//dgrys aenderungen -> ueberfluessig
//import com.arjuna.ats.arjuna.gandiva.inventory.Inventory;



import de.linogistix.los.res.BundleResolver;

/**
 * This Exception will not cause a rollback of the transaction.
 * 
 * @author Jordan
 *
 */
@ApplicationException(rollback=false)
public class OutOfRangeException extends ServiceException {

	private static final long serialVersionUID = 1L;
	
	public OutOfRangeException() {
		super("Out of Range", "OUT_OF_RANGE", new Object[0]);
		setBundleResolver(BundleResolver.class);
	}
	
}
