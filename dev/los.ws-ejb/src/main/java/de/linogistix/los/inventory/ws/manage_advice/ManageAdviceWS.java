/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.ws.manage_advice;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebService;

@Remote
@WebService
public interface ManageAdviceWS {

	@WebMethod
	public String adviceUnitLoad(AdviceUnitLoadRequest req) throws ManageAdviceWSFault;
	
	@WebMethod
	public AdviceUnitLoadResponse getAdviceForUnitLoad(String unitLoadId) throws ManageAdviceWSFault;
	
	@WebMethod
	public CommitAdviceResponse commitUnitLoadAdvice(String adviceNumber) throws ManageAdviceWSFault;
	
	@WebMethod
	public void rejectUnitLoadAdvice(RejectAdviceRequest req) throws ManageAdviceWSFault;

	/**
	 * Create or update a LOSAdvice.
	 * The Operation will create a new advice, if it is not known. Otherwise it will be updated.
	 * Update is only allowed, if the advice is not used. 
	 * After assigning it to a goods receipt or receiving material on it, an update will be rejected.   
	 * To update, the advice is searched by one of the fields adviceNumber, externalId, externalAdviceNumber
	 * 
	 * @param UpdateAdviceRequest, a data object containing the parameters
	 * @return The LOS Advice Number
	 * @throws ManageAdviceWSFault if for some goes wrong. See also {@link ManageAdviceErrorCodes}
	 */
	@WebMethod
	public String updateAdvice(UpdateAdviceRequest updateReq) throws ManageAdviceWSFault;
	
	/**
	 * delete a LOSAdvice.
	 * The advice is searched by one of the fields adviceNumber, externalId, externalAdviceNumber
	 * 
	 * @param UpdateAdviceRequest, a data object containing the parameters
	 * @return The LOS Advice Number
	 * @throws ManageAdviceWSFault if for some goes wrong. See also {@link ManageAdviceErrorCodes}
	 */
	@WebMethod
	public void deleteAdvice( DeleteAdviceRequest data ) throws ManageAdviceWSFault;

}
