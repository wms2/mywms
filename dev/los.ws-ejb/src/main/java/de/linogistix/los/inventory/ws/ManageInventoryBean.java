/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.ws;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.log4j.Logger;
//dgrys neues paket - aenderung portierung wildfly
//import org.jboss.annotation.security.SecurityDomain;
import org.jboss.ejb3.annotation.SecurityDomain;
//dgrys comment as workaround  - aenderung portierung wildfly
import org.jboss.ws.api.annotation.WebContext;
import org.mywms.facade.FacadeException;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.facade.ManageInventoryFacade;

/**
 * A Webservice for managing ItemData/articles in the wms.
 * 
 * @see ManageInventory
 * 
 * @author trautm
 *
 */

@Stateless
@SecurityDomain("los-login")
@Remote(ManageInventory.class)
@WebService(endpointInterface="de.linogistix.los.inventory.ws.ManageInventory")
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
//dgrys comment as workaround  - aenderung portierung wildfly
@WebContext(contextRoot = "/webservice", authMethod="BASIC", transportGuarantee="NONE", secureWSDLAccess=true)
//dgrys portierung wildfly 8.2, workaround to call web service, required to log in
@PermitAll
public class ManageInventoryBean implements ManageInventory {

	Logger log = Logger.getLogger(ManageInventoryBean.class);
	
    @EJB
    ManageInventoryFacade delegate;
    
	/* 
	 * @see ManageInventoryRemote#createItemData(java.lang.String, java.lang.String)
	 */
	public boolean createItemData (
            @WebParam( name="username") String username, 
            @WebParam( name="password") String password, 
			@WebParam( name="clientRef") String clientRef, 
			@WebParam( name="articleRef") String articleRef)throws InventoryException {
		
        return delegate.createItemData(clientRef, articleRef);
        
	}

	/* 
	 * @see ManageInventoryRemote#deleteItemData(java.lang.String, java.lang.String)
	 */
	public boolean deleteItemData(
            @WebParam( name="username") String username, 
            @WebParam( name="password") String password, 
			@WebParam( name="clientRef") String clientRef, 
			@WebParam( name="articleRef") String articleRef) {


        return delegate.deleteItemData(clientRef, articleRef);
	}

	/* 
	 * @see ManageInventoryRemote#updateItemReference(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean updateItemReference(
            @WebParam( name="username") String username, 
            @WebParam( name="password") String password, 
			@WebParam( name="clientRef") String clientRef, 
			@WebParam( name="existingRef") String existingRef,
			@WebParam( name="newRef") String newRef) {
		
        return delegate.updateItemReference(clientRef,existingRef, newRef);
	}

	public boolean createAvis(
            @WebParam( name="username") String username, 
            @WebParam( name="password") String password, 
			@WebParam( name="clientRef") String clientRef,
			@WebParam( name="articleRef") String articleRef,
			@WebParam( name="batchRef") String batchRef,
			@WebParam(name="amount") BigDecimal amount,
			@WebParam( name="expectedDelivery") Date expectedDelivery,
			@WebParam( name="bestBeforeEnd") Date bestBeforeEnd,
			@WebParam( name="useNotBefore") Date useNotBefore)
			{
		
        return delegate.createAvis(clientRef,articleRef, batchRef, amount, expectedDelivery, bestBeforeEnd, useNotBefore);
		
	}
	
	/**
	 * 
	 */
	public boolean createAvis(
            @WebParam( name="username") String username, 
            @WebParam( name="password") String password, 
			@WebParam( name="clientRef") String clientRef,
			@WebParam( name="articleRef") String articleRef,
			@WebParam( name="batchRef") String batchRef,
			@WebParam(name="amount") BigDecimal amount,
			@WebParam( name="expectedDelivery") Date expectedDelivery,
			@WebParam( name="bestBeforeEnd") Date bestBeforeEnd,
			@WebParam( name="useNotBefore") Date useNotBefore,
			@WebParam(name="parentRequest") String parentRequest)
			{
		
        return delegate.createAvis(clientRef,articleRef, batchRef, amount, expectedDelivery, bestBeforeEnd, useNotBefore, parentRequest);
		
	}

    public void createStockUnitOnStorageLocation(
            @WebParam( name="username") String username, 
            @WebParam( name="password") String password, 
            @WebParam(name = "clientRef") String clientRef, 
            @WebParam(name = "slName") String slName,        
            @WebParam(name = "articleRef") String articleRef,      
            @WebParam(name = "lotRef") String lotRef,          
            @WebParam(name = "amount") BigDecimal amount,            
            @WebParam(name = "unitLoadRef") String unitLoadRef) throws InventoryException, FacadeException, EntityNotFoundException 
    {    
        delegate.createStockUnitOnStorageLocation(clientRef, slName, articleRef, lotRef, amount, unitLoadRef);
    }
	


}
