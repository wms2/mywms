/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.ws;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.facade.OrderPositionTO;

/**
 * A Facade for ordering items from stock
 * @author trautm
 *
 */
@Remote
@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
public interface Order {
	
	/**
	 * creates a new Order for retrieving items from stock.
	 * 
	 * @param clientRef a reference to the client
	 * @param orderRef a reference to the order
	 * @param articleRefs a list of article references
	 * @param document an url to the document to be printed with the order
	 * @param label an url to the label to be printed with the order
	 * @return true if order has been created
	 */
	@WebMethod
	boolean order(
            @WebParam( name="username") String username, 
            @WebParam( name="password") String password, 
			@WebParam( name="clientRef") String clientRef, 
			@WebParam( name="orderRef") String orderRef,
			@WebParam( name="positions") OrderPositionTO[] positions,
			@WebParam( name="documentUrl") String documentUrl, 
			@WebParam( name="labelUrl") String labelUrl,
            @WebParam( name="destination") String destination) throws FacadeException;
	
//	/**
//	 * creates a new Order for retrieving items from stock.
//	 * 
//	 * @param clientRef a reference to the client
//	 * @param orderRef a reference to the order
//	 * @param articleRefs a list of article references
//	 * @return true if order has been created
//	 */
//	@WebMethod
//	boolean orderlocal(
//			@WebParam( name="clientRef") String clientRef, 
//			@WebParam( name="orderRef") String orderRef,
//			@WebParam( name="positions") OrderPositionTO[] positions,
//                        @WebParam( name="destination") String destination);
}
