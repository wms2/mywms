/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.ws;

import de.linogistix.los.inventory.businessservice.QueryInventoryTO;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.log4j.Logger;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.facade.QueryInventoryFacade;

//dgrys neues paket - - aenderung portierung wildfly
//import org.jboss.annotation.security.SecurityDomain;
import org.jboss.ejb3.annotation.SecurityDomain;

//dgrys comment as workaround  - aenderung portierung wildfly
import org.jboss.ws.api.annotation.WebContext;


/**
 * A Webservice for retrieving inventory information from the wms.
 *  
 * @see de.linogistix.los.inventory.connector.QueryInventory
 * @author trautm
 *
 */


@Stateless 
@SecurityDomain("los-login")
@Remote(QueryInventory.class)
@WebService(endpointInterface="de.linogistix.los.inventory.ws.QueryInventory")
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
@WebContext(contextRoot = "/webservice", authMethod="BASIC", transportGuarantee="NONE", secureWSDLAccess=true)
//dgrys portierung wildfly 8.2, workaround to call web service, required to log in
@PermitAll
public class QueryInventoryBean implements QueryInventory{

	Logger log = Logger.getLogger(QueryInventoryBean.class);
	
    @EJB
    QueryInventoryFacade delegate;
	 
	
	/*  
	 * @see de.linogistix.los.inventory.connector.QueryInventoryRemote#getInventoryByArticle(java.lang.String, java.lang.String)
	 */
	@WebMethod
	public QueryInventoryTO[] getInventoryByArticle(
            @WebParam(name = "clientRef") String clientRef, 
            @WebParam(name = "articleRef") String articleRef, 
			@WebParam(name="consolidateLot") boolean consolidateLot) throws InventoryException{
		
        return delegate.getInventoryByArticle(clientRef, articleRef, consolidateLot);
	}
	
	/*  
	 * @see de.linogistix.los.inventory.connector.QueryInventoryRemote#getInventoryByBatch(java.lang.String, java.lang.String)
	 */
	@WebMethod
	public QueryInventoryTO getInventoryByLot(
            @WebParam(name="clientRef") String clientRef, 
            @WebParam(name="articleRef") String articleRef, 
			@WebParam(name="lotRef") String lotRef) throws InventoryException{
		
        return delegate.getInventoryByLot(clientRef, articleRef, lotRef);
	}

	/*  
	 * @see de.linogistix.los.inventory.connector.QueryInventoryRemote#getInventoryList(java.lang.String)
	 */
	@WebMethod
	public QueryInventoryTO[] getInventoryAmountList(
            @WebParam(name = "clientRef")  String clientRef, 
            @WebParam(name="consolidateLot") boolean consolidateLot,
        	@WebParam(name="withAmountOnly") boolean withAmountOnly) throws InventoryException {
		
        return delegate.getInventoryList(clientRef, consolidateLot, withAmountOnly);

	}

}
