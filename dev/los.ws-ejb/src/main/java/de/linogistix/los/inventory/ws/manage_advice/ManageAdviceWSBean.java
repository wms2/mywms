/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.ws.manage_advice;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ws.api.annotation.WebContext;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.model.UnitLoadType;
import org.mywms.service.LotService;

import de.linogistix.los.common.exception.OutOfRangeException;
import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.common.service.QueryClientService;
import de.linogistix.los.inventory.customization.ManageAdviceService;
import de.linogistix.los.inventory.customization.ManageUnitLoadAdviceService;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryTransactionException;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSAdviceState;
import de.linogistix.los.inventory.model.LOSAdviceType;
import de.linogistix.los.inventory.model.LOSUnitLoadAdvice;
import de.linogistix.los.inventory.model.LOSUnitLoadAdvicePosition;
import de.linogistix.los.inventory.model.LOSUnitLoadAdviceState;
import de.linogistix.los.inventory.service.QueryAdviceService;
import de.linogistix.los.inventory.service.QueryItemDataService;
import de.linogistix.los.inventory.service.QueryLotService;
import de.linogistix.los.inventory.service.QueryUnitLoadAdviceService;
import de.linogistix.los.location.service.QueryUnitLoadTypeService;

@Stateless
@SecurityDomain("los-login")
@WebService(endpointInterface = "de.linogistix.los.inventory.ws.manage_advice.ManageAdviceWS")
@WebContext(contextRoot = "/webservice", authMethod = "BASIC", transportGuarantee = "NONE", secureWSDLAccess = true)
@SOAPBinding(parameterStyle=ParameterStyle.BARE)
@PermitAll
public class ManageAdviceWSBean implements ManageAdviceWS {
	private static final Logger log = Logger.getLogger(ManageAdviceWSBean.class);

	@EJB
	private QueryUnitLoadTypeService ultService;
		
	@EJB
	private LotService lotService;
	
	@EJB
	private QueryLotService queryLotService;
	
	@EJB
	private QueryItemDataService queryItemService;
	
	@EJB
	private QueryAdviceService queryAdviceService;
	
	@EJB
	private ManageUnitLoadAdviceService manageUlAdvice;
	
	@EJB
	private QueryUnitLoadAdviceService queryUlAdviceService;
	
	@EJB
	private QueryClientService queryClientService;
	
	@EJB
	private ManageAdviceService adviceService;
	
	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.ws.manage_advice.ManageAdviceWS#adviceUnitLoad(de.linogistix.los.inventory.ws.manage_advice.AdviceUnitLoadRequest)
	 */
	public String adviceUnitLoad(AdviceUnitLoadRequest req)	throws ManageAdviceWSFault {
		
		Client cl = null;
		try {
			cl = queryClientService.getByNumber(req.getClientNumber());
		} catch (UnAuthorizedException e1) {
			throw new ManageAdviceWSFault(
					ManageAdviceErrorCodes.UNAUTHORIZED_CALLER, 
					"CALLER IS NOT AUTHORIZED TO GET CLIENT "+req.getClientNumber());
		}
		
		// if client does not exist throw an exception
		if(cl == null){
			throw new ManageAdviceWSFault(
					ManageAdviceErrorCodes.ERROR_UNKNOWN_CLIENT, 
					"UNKNOWN CLIENT > "+req.getClientNumber());
		}
		
		if(req.getLabelId() == null || req.getLabelId().length() == 0){
			throw new ManageAdviceWSFault(
					ManageAdviceErrorCodes.ERROR_LABELID_NULL, 
					"LABEL ID IS NULL OR EMPTY ");
		}
		
		LOSUnitLoadAdvice ulAdv = null;
		try {
			ulAdv = queryUlAdviceService.getByLabelId(req.getLabelId());
		
		} catch (UnAuthorizedException e1) {
			throw new ManageAdviceWSFault(
					ManageAdviceErrorCodes.ERROR_DUPLICATE_UNITLOAD_ADVICE, 
					"THERE IS ALREADY AN ADVICE FOR LABEL "+req.getLabelId());
		}
		
		if(ulAdv != null){
			throw new ManageAdviceWSFault(
					ManageAdviceErrorCodes.ERROR_DUPLICATE_UNITLOAD_ADVICE, 
					"THERE IS ALREADY AN ADVICE FOR LABEL "+req.getLabelId());
		}
		
		String adNo = manageUlAdvice.getNewAdviceNumber();
		
		LOSAdviceType type;
		if(req.isReturns()){
			type = LOSAdviceType.GOODS_IN_RETURNS;
		}
		else{
			type = LOSAdviceType.GOODS_IN;
		}
		
		ulAdv = manageUlAdvice.createAdvice(cl, adNo, type, req.getLabelId());
		
		ulAdv.setExternalNumber(req.getExternalAdviceNumber());
		
		if(req.getRelatedAdviceNumber() != null && req.getRelatedAdviceNumber().length() > 0){
			
			LOSAdvice adv;
			try {
				adv = queryAdviceService.getByAdviceNumber(req.getRelatedAdviceNumber());
			} catch (UnAuthorizedException e) {
				throw new ManageAdviceWSFault(
						ManageAdviceErrorCodes.ERROR_UNKNOWN_ADVICE, 
						"UNKNOWN ADVICE > "+req.getRelatedAdviceNumber());
			}
			
			ulAdv.setRelatedAdvice(adv);
		}
		
		if(req.getUnitLoadType() != null && req.getUnitLoadType().length() > 0){
			
			UnitLoadType ult = null;
			ult = ultService.getByName(req.getUnitLoadType());
			if( ult == null ) {
				throw new ManageAdviceWSFault(
						ManageAdviceErrorCodes.ERROR_UNKNOWN_UNITLOADTYPE, 
						"UNKNOWN UNITLOAD TYPE > "+req.getUnitLoadType());
			}
			
			ulAdv.setUnitLoadType(ult);
		}
		
		for(StockUnitElement suElem : req.getStockUnitList()){
			
			ItemData item = queryItemService.getByItemNumber(cl, suElem.getItemDataNumber());
			
			// if item does not exist throw an exception
			if(item == null){
				throw new ManageAdviceWSFault(
						ManageAdviceErrorCodes.ERROR_UNKNOWN_ITEMNUMBER, 
						"UNKNOWN ITEM NUMBER > "+suElem.getItemDataNumber());
			}
			
			if(suElem.getNotifiedAmount() == null){
				throw new ManageAdviceWSFault(
						ManageAdviceErrorCodes.ERROR_NOTIFIEDAMOUNT_NULL, "NOTIFIED AMOUNT NULL");
			}
			
			Lot lot = null;
			
			// if there is a lot number set
			if(suElem.getLotNumber() != null && suElem.getLotNumber().length()>0){
				
				// Search for existing
				lot = queryLotService.getByNameAndItemData(suElem.getLotNumber(), item);
				
				// if it is an unknown lot, create a new one
				if(lot == null){
					lot = lotService.create(item.getClient(), item, suElem.getLotNumber());
				}
			}
			
			try {
				ulAdv = manageUlAdvice.addPosition(ulAdv, lot, item, suElem.getNotifiedAmount());
			} catch (OutOfRangeException e) {
				throw new ManageAdviceWSFault(
						ManageAdviceErrorCodes.ERROR_NOTIFIEDAMOUNT_NEGATIVE,
						"NOTIFIED AMOUNT IS NEGATIVE"+suElem.getNotifiedAmount());
			}
		}
		
		return ulAdv.getNumber();
	}

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.ws.manage_advice.ManageAdviceWS#getAdviceForUnitLoad(java.lang.String)
	 */
	public AdviceUnitLoadResponse getAdviceForUnitLoad(String unitLoadId) throws ManageAdviceWSFault {
		
		LOSUnitLoadAdvice ulAdv;
		
		try {
			ulAdv = queryUlAdviceService.getByLabelId(unitLoadId);
		} catch (UnAuthorizedException e) {
			
			throw new ManageAdviceWSFault(
					ManageAdviceErrorCodes.UNAUTHORIZED_CALLER, 
					"CALLER IS NOT AUTHORIZED TO GET ADVICE FOR UNITLOAD "+unitLoadId);
		}
		
		if(ulAdv == null
			|| ulAdv.getAdviceType().equals(LOSAdviceType.GOODS_IN)
			|| ulAdv.getAdviceType().equals(LOSAdviceType.GOODS_IN_RETURNS))
		{
			throw new ManageAdviceWSFault(
					ManageAdviceErrorCodes.ERROR_NO_ADVICE_FOR_LABEL, 
					"NO ADVICE FOR UNIT LOAD LABEL > "+unitLoadId);
		}
		
		AdviceUnitLoadResponse resp = new AdviceUnitLoadResponse();
		resp.setAdviceNumber(ulAdv.getNumber());
		resp.setExternalNumber(ulAdv.getExternalNumber());
		resp.setClientNumber(ulAdv.getClient().getNumber());
		resp.setLabelId(ulAdv.getLabelId());
		resp.setReasonForReturn(ulAdv.getReasonForReturn());
		
		if(ulAdv.getRelatedAdvice() != null){
			resp.setRelatedAdvice(ulAdv.getRelatedAdvice().getAdviceNumber());
		}
		
		if(ulAdv.getAdviceType().equals(LOSAdviceType.GOODS_OUT_RETURNS)){
			resp.setReturns(true);
		}
		else{
			resp.setReturns(false);
		}
		
		if(ulAdv.getUnitLoadType() != null){
			resp.setUnitLoadType(ulAdv.getUnitLoadType().getName());
		}
		else{
			resp.setUnitLoadType("");
		}
		
		List<StockUnitElement> suList = new ArrayList<StockUnitElement>(ulAdv.getPositionList().size());
		
		for(LOSUnitLoadAdvicePosition pos:ulAdv.getPositionList()){
			
			StockUnitElement su = new StockUnitElement();
			su.setItemDataNumber(pos.getItemData().getNumber());
			su.setNotifiedAmount(pos.getNotifiedAmount());
			
			if(pos.getLot() != null){
				su.setLotNumber(pos.getLot().getName());
				su.setBestBeforeEnd(pos.getLot().getBestBeforeEnd());
				su.setUseNotBefore(pos.getLot().getUseNotBefore());
			}
			
			suList.add(su);
		}
		
		resp.setStockUnitList(suList);
		
		return resp;
	}

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.ws.manage_advice.ManageAdviceWS#commitUnitLoadAdvice(java.lang.String)
	 */
	public CommitAdviceResponse commitUnitLoadAdvice(String adviceNumber) throws ManageAdviceWSFault {
		
		LOSUnitLoadAdvice ulAdv;
		
		try {
			ulAdv = queryUlAdviceService.getByAdviceNumber(adviceNumber);
		} catch (UnAuthorizedException e) {
			
			throw new ManageAdviceWSFault(
					ManageAdviceErrorCodes.UNAUTHORIZED_CALLER, 
					"CALLER IS NOT AUTHORIZED TO GET ADVICE "+adviceNumber);
		}
		
		if(ulAdv == null){
			throw new ManageAdviceWSFault(
					ManageAdviceErrorCodes.ERROR_NO_ADVICE_WITH_NUMBER, 
					"NO ADVICE FOR NUMBER > "+adviceNumber);
		}
		
		try {
			manageUlAdvice.switchState(ulAdv, LOSUnitLoadAdviceState.FINISHED, "");
			
			return new CommitAdviceResponse();
			
		} catch (InventoryException fe){
			throw new ManageAdviceWSFault(
					ManageAdviceErrorCodes.ERROR_FINISHING_ADVICE, 
					"ERROR FINISHING ADVICE > "+fe.getLocalizedMessage());
		} catch (InventoryTransactionException fe){
			CommitAdviceResponse resp = new CommitAdviceResponse();
			resp.setError(true);
			resp.setMessage(fe.getLocalizedMessage());
			
			return resp;
		}
		
	}

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.ws.manage_advice.ManageAdviceWS#rejectUnitLoadAdvice(java.lang.String, java.lang.String)
	 */
	public void rejectUnitLoadAdvice(RejectAdviceRequest req) throws ManageAdviceWSFault {
		
		LOSUnitLoadAdvice ulAdv;
		
		try {
			ulAdv = queryUlAdviceService.getByAdviceNumber(req.getAdviceNumber());
		} catch (UnAuthorizedException e) {
			
			throw new ManageAdviceWSFault(
					ManageAdviceErrorCodes.UNAUTHORIZED_CALLER, 
					"CALLER IS NOT AUTHORIZED TO GET ADVICE "+req.getAdviceNumber());
		}
		
		if(ulAdv == null){
			throw new ManageAdviceWSFault(
					ManageAdviceErrorCodes.ERROR_NO_ADVICE_WITH_NUMBER, 
					"NO ADVICE FOR NUMBER > "+req.getAdviceNumber());
		}
		
		try {
			manageUlAdvice.switchState(ulAdv, LOSUnitLoadAdviceState.REJECTED, req.getReason());
			
		} catch (InventoryException e) {
			throw new ManageAdviceWSFault(
					ManageAdviceErrorCodes.ERROR_FINISHING_ADVICE, 
					"ERROR FINISHING ADVICE > "+e.getLocalizedMessage());
		} catch (InventoryTransactionException e) {
			// do nothing special
		}
	}

	
	public String updateAdvice(UpdateAdviceRequest data) throws ManageAdviceWSFault {
		String logStr = "updateAdvice ";
		log.debug(logStr+"START "+data);
		
		if(data.getClientNumber() == null || data.getClientNumber().length() == 0){
			log.error(logStr+"Client number must be set. Abort");
			throw new ManageAdviceWSFault(
					ManageAdviceErrorCodes.ERROR_UNKNOWN_CLIENT, "Client number must be set");
		}
		
		Client client = null;
		try {
			client = queryClientService.getByNumber(data.getClientNumber());
		} 
		catch (UnAuthorizedException e1) {
			log.error(logStr+"Caller is not authorized to use client "+data.getClientNumber()+". Abort");
			throw new ManageAdviceWSFault(
					ManageAdviceErrorCodes.UNAUTHORIZED_CALLER, 
					"Caller is not authorized to use client "+data.getClientNumber());
		}
		
		// if client does not exist throw an exception
		if(client == null){
			log.error(logStr+"Client not found: "+data.getClientNumber()+". Abort");
			throw new ManageAdviceWSFault(
					ManageAdviceErrorCodes.ERROR_UNKNOWN_CLIENT, 
					"Client not found: "+data.getClientNumber());
		}

		// Try to find an existing advice
		LOSAdvice adv = null;
		try {
			if(data.getAdviceNumber() != null && data.getAdviceNumber().length() > 0){
				adv = queryAdviceService.getByAdviceNumber(client, data.getAdviceNumber());
			}
			if( adv == null && data.getExternalId() != null && data.getExternalId().length() > 0 ) {
				adv = queryAdviceService.getByExternalId(client, data.getExternalId());
			}
			if( adv == null && data.getExternalAdviceNumber() != null && data.getExternalAdviceNumber().length() > 0 ) {
				adv = queryAdviceService.getByExternalNumber(client, data.getExternalAdviceNumber());
			}
		}
		catch( UnAuthorizedException e ) {
			log.error(logStr+"Caller is not authorized access an advice");
			throw new ManageAdviceWSFault(
					ManageAdviceErrorCodes.UNAUTHORIZED_CALLER, 
					"Caller is not authorized access an advice");
		}
		
		ItemData itemData = null;
		if(data.getItemNumber() != null && data.getItemNumber().length() > 0){
			itemData = queryItemService.getByItemNumber(client, data.getItemNumber());
			if(itemData == null){
				log.error(logStr+"Item data not found: "+data.getItemNumber()+". Abort");
				throw new ManageAdviceWSFault(
						ManageAdviceErrorCodes.ERROR_UNKNOWN_ITEMNUMBER, 
						"Item data not found: "+data.getItemNumber());
			}
		}
		
		Lot lot = null;
		if(data.getLotNumber() != null && data.getLotNumber().length()>0){
			
			lot = queryLotService.getByNameAndItemData(data.getLotNumber(), itemData);
			
			if(lot == null){
				lot = lotService.create(itemData.getClient(), itemData, data.getLotNumber());
			}
		}


		if( adv == null ) {
			// Generate a new advice
			log.debug(logStr+"Generate new adcive");
			
			if(itemData == null){
				log.error(logStr+"Item data not found: "+data.getItemNumber()+". Abort");
				throw new ManageAdviceWSFault(
						ManageAdviceErrorCodes.ERROR_UNKNOWN_ITEMNUMBER, 
						"Item data not found: "+data.getItemNumber());
			}
			
			if(data.getNotifiedAmount() == null){
				log.error(logStr+"Nofified amount must not be null. Abort");
				throw new ManageAdviceWSFault( 
						ManageAdviceErrorCodes.ERROR_NOTIFIEDAMOUNT_NULL, 
						"Nofified amount must not be null");
			}
			
			String advNumber = adviceService.getNewAdviceNumber();
			
			try {
				
				adv = adviceService.createAdvice(client, advNumber, itemData, data.getNotifiedAmount());
				adv.setAdditionalContent(data.getAdditionalContent());
				adv.setExpectedDelivery(data.getExpectedDelivery());
				adv.setExternalAdviceNumber(data.getExternalAdviceNumber());
				adv.setExternalId(data.getExternalId());
				adv.setLot(lot);
				
				log.info(logStr+"Inserted advice="+adv.getAdviceNumber());
				return adv.getAdviceNumber();
				
			} catch ( Throwable e ) {
				// It is unbelieveable what exceptions are thrown
				log.error(logStr+"Something went wrong when creating the advice: "+e.getMessage());
				throw new ManageAdviceWSFault(
						ManageAdviceErrorCodes.ERROR_CREATION, e.getMessage());
			}

		}
		else {
			// Update existing advice
			log.debug(logStr+"Update existing adcive="+adv.getAdviceNumber());
			
			
			if(adv.getAdviceState().equals(LOSAdviceState.FINISHED)){
				log.error(logStr+"The advice is already finished. Advice="+adv.getAdviceNumber()+". Abort");
				throw new ManageAdviceWSFault(
						ManageAdviceErrorCodes.ERROR_ADVICE_FINISHED,
						"The advice is already finished. Advice="+adv.getAdviceNumber());
			}
			
			if( !adviceService.isAdviceChangeable( adv ) ) {
				log.error(logStr+"The advice is not changeable. Advice="+adv.getAdviceNumber()+". Abort");
				throw new ManageAdviceWSFault(
						ManageAdviceErrorCodes.ERROR_ADVICE_STARTED,
						"The advice is not changeable. Advice="+adv.getAdviceNumber());
			}
			
			adv.setItemData(itemData);
			adv.setNotifiedAmount(data.getNotifiedAmount());
			adv.setAdditionalContent(data.getAdditionalContent());
			adv.setExpectedDelivery(data.getExpectedDelivery());
			adv.setExternalAdviceNumber(data.getExternalAdviceNumber());
			adv.setExternalId(data.getExternalId());
			adv.setLot(lot);
			
			
			log.info(logStr+"Updated advice="+adv.getAdviceNumber());
			return adv.getAdviceNumber();
		}
	}

	public void deleteAdvice( DeleteAdviceRequest data ) throws ManageAdviceWSFault {
		String logStr = "deleteAdvice ";
		log.debug(logStr+"START "+data);
		
		if(data.getClientNumber() == null || data.getClientNumber().length() == 0){
			log.error(logStr+"Client number must be set. Abort");
			throw new ManageAdviceWSFault(
					ManageAdviceErrorCodes.ERROR_UNKNOWN_CLIENT, "Client number must be set");
		}
		
		Client client = null;
		try {
			client = queryClientService.getByNumber(data.getClientNumber());
		} 
		catch (UnAuthorizedException e1) {
			log.error(logStr+"Caller is not authorized to use client "+data.getClientNumber()+". Abort");
			throw new ManageAdviceWSFault(
					ManageAdviceErrorCodes.UNAUTHORIZED_CALLER, 
					"Caller is not authorized to use client "+data.getClientNumber());
		}
		
		// if client does not exist throw an exception
		if(client == null){
			log.error(logStr+"Client not found: "+data.getClientNumber()+". Abort");
			throw new ManageAdviceWSFault(
					ManageAdviceErrorCodes.ERROR_UNKNOWN_CLIENT, 
					"Client not found: "+data.getClientNumber());
		}

		// Try to find an existing advice
		LOSAdvice adv = null;
		try {
			if(data.getAdviceNumber() != null && data.getAdviceNumber().length() > 0){
				adv = queryAdviceService.getByAdviceNumber(client, data.getAdviceNumber());
			}
			if( adv == null && data.getExternalId() != null && data.getExternalId().length() > 0 ) {
				adv = queryAdviceService.getByExternalId(client, data.getExternalId());
			}
			if( adv == null && data.getExternalAdviceNumber() != null && data.getExternalAdviceNumber().length() > 0 ) {
				adv = queryAdviceService.getByExternalNumber(client, data.getExternalAdviceNumber());
			}
		}
		catch( UnAuthorizedException e ) {
			log.error(logStr+"Caller is not authorized access an advice");
			throw new ManageAdviceWSFault(
					ManageAdviceErrorCodes.UNAUTHORIZED_CALLER, 
					"Caller is not authorized access an advice");
		}
		
		if( adv == null ) {
			log.error(logStr+"Advice not found. Abort");
			throw new ManageAdviceWSFault(
					ManageAdviceErrorCodes.ERROR_NO_ADVICE_WITH_NUMBER, 
					"Advice not found");
		}
			
		// Update existing advice
		log.debug(logStr+"Delete existing adcive="+adv.getAdviceNumber());
		
		if(adv.getAdviceState().equals(LOSAdviceState.FINISHED)){
			log.error(logStr+"The advice is already finished. Advice="+adv.getAdviceNumber()+". Abort");
			throw new ManageAdviceWSFault(
					ManageAdviceErrorCodes.ERROR_ADVICE_FINISHED,
					"The advice is already finished. Advice="+adv.getAdviceNumber());
		}
		
		if( !adviceService.isAdviceChangeable( adv ) ) {
			log.error(logStr+"The advice is not changeable. Advice="+adv.getAdviceNumber()+". Abort");
			throw new ManageAdviceWSFault(
					ManageAdviceErrorCodes.ERROR_ADVICE_STARTED,
					"The advice is not changeable. Advice="+adv.getAdviceNumber());
		}

		try {
			adviceService.deleteAdvice(adv);
		} catch (InventoryException e) {
			log.error(logStr+"Cannot delete advice="+adv.getAdviceNumber()+". Abort. "+e.getMessage() );
			throw new ManageAdviceWSFault(
					ManageAdviceErrorCodes.ERROR_DELETE,e.getLocalizedMessage() );
		}
		
		
		log.info(logStr+"Deleted advice="+adv.getAdviceNumber());
	}
	
}
