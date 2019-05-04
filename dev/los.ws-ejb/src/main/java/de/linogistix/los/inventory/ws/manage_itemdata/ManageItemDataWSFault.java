/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.ws.manage_itemdata;

import javax.ejb.ApplicationException;

/**
 * This Exception will cause a rollback of the transaction.
 * 
 * @author Jordan
 *
 */
@ApplicationException(rollback=true)
public class ManageItemDataWSFault extends Exception {

	private static final long serialVersionUID = 1L;
	
	private ManageItemDataErrorCodes errorCode;

	public ManageItemDataWSFault(ManageItemDataErrorCodes errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public ManageItemDataErrorCodes getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(ManageItemDataErrorCodes errorCode) {
		this.errorCode = errorCode;
	}
}
