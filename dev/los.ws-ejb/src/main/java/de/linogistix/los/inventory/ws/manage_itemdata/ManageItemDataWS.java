/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.ws.manage_itemdata;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebService;

import org.mywms.model.ItemData;

import de.linogistix.los.inventory.service.ClientItemNumberTO;

@Remote
@WebService
public interface ManageItemDataWS {

	/**
	 * Create a new {@link ItemData} if key (client, itemNumber) is unknown yet 
	 * or update existing {@link ItemData}.
	 * 
	 * @param updateReq instance of {@link UpdateItemDataRequest} that contains data of the item.
	 * @throws ManageItemDataWSFault if for some reasons update or creation. See also {@link ManageItemDataErrorCodes}.
	 */
	@WebMethod
	public void updateItemData(UpdateItemDataRequest updateReq) throws ManageItemDataWSFault;
	
	/**
	 * 
	 * @param deleteReq
	 * @throws ManageItemDataWSFault
	 */
	@WebMethod
	public void deleteItemData(DeleteItemDataRequest deleteReq) throws ManageItemDataWSFault;
	
	/**
	 * Create, edit or delete a BOM child entry.
	 * If the amount is set to zero, the BOM child will be deleted. Otherwise it is inserted or updated.
	 *  
	 * @param BOMData
	 * @throws ManageItemDataWSFault
	 */
	@WebMethod
	public void updateBom( UpdateBomRequest data ) throws ManageItemDataWSFault;
	
	/**
	 * Delete a complete BOM.
	 *  
	 * @param clientNumber
	 * @param parentNumber
	 * @throws ManageItemDataWSFault
	 */
	@WebMethod
	public void deleteBom( DeleteItemDataRequest deleteReq ) throws ManageItemDataWSFault;

		/**
	 * Call this method to get the numbers of all {@link ItemData}s in the system.
	 * For security reasons this will only be allowed for callers that are assigned to the system client.
	 * 
	 * @return List of {@link ClientNumberElement}
	 * @throws ManageItemDataWSFault if caller is not authorized to get the list.
	 */
	@WebMethod
	public ClientItemNumberTO[] getItemNumbers() throws ManageItemDataWSFault;
	

}
