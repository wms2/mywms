/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.ws.manage_advice;

import javax.ejb.ApplicationException;

/**
 * This Exception will cause a rollback of the transaction.
 * 
 * @author Jordan
 *
 */
@ApplicationException(rollback=true)
public class ManageAdviceWSFault extends Exception {

	private static final long serialVersionUID = 1L;

	private ManageAdviceErrorCodes errorCode;

	public ManageAdviceWSFault(ManageAdviceErrorCodes errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public ManageAdviceErrorCodes getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(ManageAdviceErrorCodes errorCode) {
		this.errorCode = errorCode;
	}
}
