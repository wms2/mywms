/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.ws.manage_itemdata;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ws.api.annotation.WebContext;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.ItemUnitType;
import org.mywms.service.ConstraintViolatedException;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.common.service.QueryClientService;
import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.inventory.customization.ManageItemDataService;
import de.linogistix.los.inventory.exception.InventoryTransactionException;
import de.linogistix.los.inventory.exception.StockExistException;
import de.linogistix.los.inventory.model.LOSBom;
import de.linogistix.los.inventory.service.ClientItemNumberTO;
import de.linogistix.los.inventory.service.ItemUnitService;
import de.linogistix.los.inventory.service.LOSBomService;
import de.linogistix.los.inventory.service.QueryItemDataService;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.product.ItemDataNumber;
import de.wms2.mywms.product.ItemDataNumberEntityService;
import de.wms2.mywms.product.ItemUnit;

@Stateless
@SecurityDomain("los-login")
@WebService(endpointInterface = "de.linogistix.los.inventory.ws.manage_itemdata.ManageItemDataWS")

//dgrys comment as workaround  - aenderung portierung wildfly
@WebContext(contextRoot = "/webservice", authMethod = "BASIC", transportGuarantee = "NONE", secureWSDLAccess = true)
@SOAPBinding(parameterStyle=ParameterStyle.BARE)
@PermitAll
public class ManageItemDataWSBean implements ManageItemDataWS {
	Logger log = Logger.getLogger(ManageItemDataWSBean.class);
	
	@EJB
	private QueryItemDataService queryItemService;
	
	@EJB
	private ItemUnitService unitService;
	
	@EJB
	private QueryClientService queryClientService;
	
	@EJB
	private ManageItemDataService manageItemService;

	@EJB
	private LOSBomService bomService;
	
	@Inject
	private ItemDataNumberEntityService idnService;	
	
	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	@EJB
	private EntityGenerator entityGenerator;
	
	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.ws.manage_itemdata.ManageItemDataWS#updateItemData(UpdateItemDataRequest)
	 */
	public void updateItemData(UpdateItemDataRequest updateReq) throws ManageItemDataWSFault{
		String logStr = "updateItemData ";
		log.info(logStr+updateReq);
		
		Client cl = null;
		try {
			cl = queryClientService.getByNumber(updateReq.getClientNumber());
		} catch (UnAuthorizedException e1) {
			log.error(logStr+"No permission to access client. Number="+updateReq.getClientNumber());
			throw new ManageItemDataWSFault(
					ManageItemDataErrorCodes.UNAUTHORIZED_CALLER, 
					"CALLER IS NOT AUTHORIZED TO GET CLIENT "+updateReq.getClientNumber());
		}
			
		if(cl == null){
			log.error(logStr+"Client not found. Number="+updateReq.getClientNumber());
			throw new ManageItemDataWSFault(
					ManageItemDataErrorCodes.ERROR_UNKNOWN_CLIENT, 
					"UNKNOWN CLIENT > "+updateReq.getClientNumber());
		}
		
		ItemUnit reqUnit = unitService.getByName(updateReq.getHandlingUnit());
			
		if(reqUnit == null) {
			log.warn(logStr+"Unit not found="+updateReq.getHandlingUnit()+", Try to create");
			try {
				reqUnit = entityGenerator.generateEntity(ItemUnit.class);
				reqUnit.setName(updateReq.getHandlingUnit());
				reqUnit.setUnitType(ItemUnitType.PIECE);
				reqUnit.setBaseFactor(1);

				manager.persist(reqUnit);
			}
			catch( Throwable t ) {
				log.error(logStr+"Unit not found="+updateReq.getHandlingUnit());
				throw new ManageItemDataWSFault(
					ManageItemDataErrorCodes.ERROR_UNKNOWN_ITEMUNIT, 
					"UNKNOWN ITEMUNIT > "+updateReq.getHandlingUnit());
			}
		}
		
		if(updateReq.getName() == null){
			log.error(logStr+"Name not set. name=NULL");
			throw new ManageItemDataWSFault(
					ManageItemDataErrorCodes.ERROR_ITEMNAME_NULL,
					"NAME OF ITEM DATA MUST NOT BE NULL !");
		}
		
		ItemData item = queryItemService.getByItemNumber(cl, updateReq.getNumber());
		
		// if item does not exist, create one
		if(item == null){
			try {
				
				item = manageItemService.createItemData(cl, 
									    	   updateReq.getNumber(), 
									    	   updateReq.getName(), 
									    	   reqUnit);
				
			} catch (InventoryTransactionException e) {
				log.error(logStr+"Impossible Exception: " +e.getMessage(), e);
				// Not possible as we checked before
				item = new ItemData();
			}
	
			item.setDescription(updateReq.getDescription());
			item.setScale(updateReq.getScale());
			item.setLotMandatory(updateReq.isLotMandatory());
			item.setAdviceMandatory(updateReq.isAdviceMandatory());
			item.setSerialNoRecordType(updateReq.getSerialNoRecordType());
		}
		// otherwise update the existing
		else{
						
			try {
				manageItemService.updateItemUnit(item, reqUnit);
			} catch (StockExistException e) {
				log.error(logStr+"StockExistException Exception: " +e.getMessage());
				throw new ManageItemDataWSFault(
						ManageItemDataErrorCodes.UPDATE_ERROR_ITEMUNIT_STOCK_EXIST, 
						"COULD NOT UPDATE ITEM UNIT BECAUSE OF EXISTING STOCKS");
			}	
			
			try {
				manageItemService.updateScale(item, updateReq.getScale());
			} catch (StockExistException e) {
				log.error(logStr+"StockExistException Exception: " +e.getMessage());
				throw new ManageItemDataWSFault(
						ManageItemDataErrorCodes.UPDATE_ERROR_SCALE_STOCK_EXIST, 
						"COULD NOT UPDATE SCALE BECAUSE OF EXISTING STOCKS");
			}	
										
			try {
				manageItemService.updateLotMandatory(item, updateReq.isLotMandatory());
			} catch (StockExistException e) {
				log.error(logStr+"StockExistException Exception: " +e.getMessage());
				throw new ManageItemDataWSFault(
						ManageItemDataErrorCodes.UPDATE_ERROR_LOTMANDATORY_STOCK_EXIST, 
						"COULD NOT UPDATE LOT MANDATORY BECAUSE OF EXISTING STOCKS");
			}	
							
			try {
				manageItemService.updateSerialNoRecordType(item, updateReq.getSerialNoRecordType());
			} catch (StockExistException e) {
				log.error(logStr+"StockExistException Exception: " +e.getMessage());
				throw new ManageItemDataWSFault(
						ManageItemDataErrorCodes.UPDATE_ERROR_SERIALRECORDTYPE_STOCK_EXIST, 
						"COULD NOT UPDATE SERIAL RECORD TYPE BECAUSE OF EXISTING STOCKS");
			}	
			
			// things we can update without side effects
			manageItemService.updateName(item, updateReq.getName());
			manageItemService.updateDescription(item, updateReq.getDescription());
			manageItemService.updateAdviceMandatory(item, updateReq.isAdviceMandatory());
		}
		
		String[] eans = updateReq.getEanCodes();
		List<ItemDataNumber> idnList = idnService.readByItemData( item );
		
		if( eans != null ) {
			for( String s : eans ) {
				if( s == null || s.length()==0 ) {
					log.warn(logStr+"Empty EAN code transmitted. ignore");
					continue;
				}
				boolean found = false;
				for( ItemDataNumber idn : idnList ) {
					if( idn.getNumber().equals(s) ) {
						idnList.remove(idn);
						found = true;
						break;
						
					}
				}
				if( found ) {
					continue;
				}
				
				try {
					idnService.create( item, s );
				} catch (FacadeException e) {
					log.error(logStr+"Exception in create: " + e.getMessage(), e);
					throw new ManageItemDataWSFault(
							ManageItemDataErrorCodes.ERROR_UPDATE_BOM, 
							"COULD NOT CREATE EAN="+s);
				}
	
			}
			for( ItemDataNumber idn : idnList ) {
				try {
					manager.remove(idn);
				} catch (Exception e) {
					log.error(logStr+"Exception in delete: " + e.getMessage(), e);
					throw new ManageItemDataWSFault(
							ManageItemDataErrorCodes.ERROR_UPDATE_BOM, 
							"COULD NOT DELETE EAN="+idn);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.ws.manage_itemdata.ManageItemDataWS#deleteItemData(DeleteItemDataRequest)
	 */
	public void deleteItemData(DeleteItemDataRequest deleteReq)	throws ManageItemDataWSFault {
		String logStr = "deleteItemData ";
		log.info(logStr+deleteReq);

		Client cl = null;
		try {
			cl = queryClientService.getByNumber(deleteReq.getClientNumber());
		} catch (UnAuthorizedException e1) {
			log.error(logStr+"No permission to access client="+deleteReq.getClientNumber());
			throw new ManageItemDataWSFault(
					ManageItemDataErrorCodes.UNAUTHORIZED_CALLER, 
					"CALLER IS NOT AUTHORIZED TO GET CLIENT "+deleteReq.getClientNumber());
		}
		
		if(cl == null){
			log.error(logStr+"Client not found="+deleteReq.getClientNumber());
			throw new ManageItemDataWSFault(
					ManageItemDataErrorCodes.ERROR_UNKNOWN_CLIENT, 
					"UNKNOWN CLIENT > "+deleteReq.getClientNumber());
		}
		
		ItemData item = queryItemService.getByItemNumber(cl, deleteReq.getItemNumber());
		
		// if item does not exist, create one
		if(item == null){
			log.error(logStr+"Item not found="+deleteReq.getItemNumber());
			throw new ManageItemDataWSFault(
					ManageItemDataErrorCodes.DELETE_ERROR_UNKNOWN_ITEMNUMBER, 
					"UNKNOWN ITEM NUMBER > "+deleteReq.getItemNumber());
			
		}

    	List<LOSBom> bomList = bomService.getChildBomList(item);
    	for( LOSBom bom : bomList ) {
    		try {
				bomService.delete(bom);
			} catch (ConstraintViolatedException e) {
				log.error(logStr+"Cannot delete Bom-parent parentnumber="+bom.getParent().getNumber()+", childnumber="+bom.getChild().getNumber());
				throw new ManageItemDataWSFault(ManageItemDataErrorCodes.ERROR_DELETE, e.getLocalizedMessage());
			}
    	}
    	
    	bomList = bomService.getParentBomList(item);
    	for( LOSBom bom : bomList ) {
    		try {
    			bomService.delete(bom);
			} catch (ConstraintViolatedException e) {
				log.error(logStr+"Cannot delete Bom-child parentnumber="+bom.getParent().getNumber()+", childnumber="+bom.getChild().getNumber());
				throw new ManageItemDataWSFault(ManageItemDataErrorCodes.ERROR_DELETE, e.getLocalizedMessage());
			}
    	}

		
		List<ItemDataNumber> idnList = idnService.readByItemData(item);
		for( ItemDataNumber idn:idnList ) {
			try {
				manager.remove(idn);
			} catch (Exception e) {
				log.error(logStr+"Cannot delete ItemDataNumber number="+idn.getNumber()+", item="+item.getNumber());
				throw new ManageItemDataWSFault(ManageItemDataErrorCodes.ERROR_DELETE, e.getLocalizedMessage());
			}
		}
		
		try {
			manageItemService.deleteItemData(item);
		} catch (StockExistException e) {
			log.error(logStr+"Cannot delete ItemData="+item.getNumber());
			throw new ManageItemDataWSFault(
					ManageItemDataErrorCodes.DELETE_ERROR_STOCK_EXIST, 
					"COULD NOT DELETE ITEMDATA BECAUSE OF EXISTING STOCKS");
		}	
		
	}

	/* (non-Javadoc)
	 * @see de.linogistix.los.inventory.ws.manage_itemdata.ManageItemDataWS#updateBOMData(de.linogistix.los.inventory.ws.manage_itemdata.BOMData)
	 */
	public void updateBom( UpdateBomRequest data ) throws ManageItemDataWSFault {
		String logStr = "updateBom ";
		log.debug(logStr+data);
		
		Client client = null;
		try {
			client = queryClientService.getByNumber( data.getClientNumber() );
		} catch (UnAuthorizedException e) {
			log.error(logStr+"Caller is not authorized to use client "+data.getClientNumber()+". Abort");
			throw new ManageItemDataWSFault(
					ManageItemDataErrorCodes.UNAUTHORIZED_CALLER, 
					"Caller is not authorized to use client "+data.getClientNumber());
		}
		if( client == null ) {
			log.error(logStr+"Client not found: "+data.getClientNumber()+". Abort");
			throw new ManageItemDataWSFault(
					ManageItemDataErrorCodes.ERROR_UNKNOWN_CLIENT, 
					"Client not found: "+data.getClientNumber());
		}
		
		ItemData parent = queryItemService.getByItemNumber(client, data.getParentNumber() );
		if( parent == null ) {
			log.error(logStr+"Parent not found: "+data.getParentNumber()+". Abort");
			throw new ManageItemDataWSFault(
					ManageItemDataErrorCodes.ERROR_UNKNOWN_ITEMDATA, 
					"Parent not found: "+data.getParentNumber());
		}

		ItemData child = queryItemService.getByItemNumber( client, data.getChildNumber() );
		if( child == null ) {
			log.error(logStr+"Child not found: "+data.getChildNumber()+". Abort");
			throw new ManageItemDataWSFault(
					ManageItemDataErrorCodes.ERROR_UNKNOWN_ITEMDATA, 
					"Child not found: "+data.getChildNumber());
		}
		
		LOSBom bom = bomService.getBom( parent, child );
		
		BigDecimal amount = data.getAmount() == null ? BigDecimal.ZERO : data.getAmount();
		
		if( bom == null ) {
			// Generate a new advice
			log.debug(logStr+"Generate new bom");

			if( BigDecimal.ZERO.compareTo( amount ) < 0 ) {
				try {
					bomService.create(parent, child, amount, data.isPickable());
				} catch (FacadeException e) {
					log.error(logStr+"Exception in create: " + e.getMessage(), e);
					throw new ManageItemDataWSFault(
							ManageItemDataErrorCodes.ERROR_UPDATE_BOM, 
							e.getLocalizedMessage());
				}
			}
			else {
				log.warn(logStr+"Cannot create BOM without amount");
			}
		} 
		else { 
			if( BigDecimal.ZERO.compareTo( amount ) >= 0 ) {
				try {
					bomService.delete(bom);
				} catch (ConstraintViolatedException e) {
					log.error(logStr+"Exception in delete: " + e.getMessage(), e);
					throw new ManageItemDataWSFault(
							ManageItemDataErrorCodes.ERROR_UPDATE_BOM, 
							e.getLocalizedMessage());
				}
			}
			else {
				bom.setAmount( amount );
				bom.setPickable( data.isPickable() );
			}
		}
	}
	
	public void deleteBom( DeleteItemDataRequest deleteReq ) throws ManageItemDataWSFault {
		String logStr = "deleteBom ";
		log.debug(logStr+deleteReq);
		
		Client client = null;
		try {
			client = queryClientService.getByNumber( deleteReq.getClientNumber() );
		} catch (UnAuthorizedException e) {
			log.error(logStr+"Caller is not authorized to use client "+deleteReq.getClientNumber()+". Abort");
			throw new ManageItemDataWSFault(
					ManageItemDataErrorCodes.UNAUTHORIZED_CALLER, 
					"Caller is not authorized to use client "+deleteReq.getClientNumber());
		}
		if( client == null ) {
			log.error(logStr+"Client not found: "+deleteReq.getClientNumber()+". Abort");
			throw new ManageItemDataWSFault(
					ManageItemDataErrorCodes.ERROR_UNKNOWN_CLIENT, 
					"Client not found: "+deleteReq.getClientNumber());
		}
		
		ItemData parent = queryItemService.getByItemNumber(client, deleteReq.getItemNumber() );
		if( parent == null ) {
			log.error(logStr+"Parent not found: "+deleteReq.getItemNumber()+". Abort");
			throw new ManageItemDataWSFault(
					ManageItemDataErrorCodes.ERROR_UNKNOWN_ITEMDATA, 
					"Parent not found: "+deleteReq.getItemNumber());
		}
		
		try {
			bomService.deleteAll(parent);
		} catch (Exception e) {
			log.error(logStr+"Exception in delete: " + e.getMessage(), e);
			throw new ManageItemDataWSFault(
					ManageItemDataErrorCodes.ERROR_UPDATE_BOM, 
					e.getLocalizedMessage());
		}

	}

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.ws.manage_itemdata.ManageItemDataWS#getItemNumbers()
	 */
	public ClientItemNumberTO[] getItemNumbers() throws ManageItemDataWSFault {
		
		try {
			return queryItemService.getItemNumbers().toArray(new ClientItemNumberTO[]{});
		} catch (UnAuthorizedException e) {
			
			throw new ManageItemDataWSFault(
					ManageItemDataErrorCodes.UNAUTHORIZED_CALLER, 
					"CALLER IS NOT AUTHORIZED TO GET A LIST OF ALL ITEM NUMBERS");
		}
	}

}
