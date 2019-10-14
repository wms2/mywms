/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;


import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.mywms.model.Client;
import org.mywms.service.ClientService;

import de.linogistix.los.inventory.businessservice.QueryInventoryBusiness;
import de.linogistix.los.inventory.businessservice.QueryInventoryTO;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.service.ItemDataService;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.inventory.LotEntityService;
import de.wms2.mywms.product.ItemData;


/**
 * A Webservice for retrieving inventory information from the wms.
 *  
 * @see de.linogistix.los.inventory.connector.QueryInventoryFacade
 * @author trautm
 *
 */
@Stateless 
public class QueryInventoryFacadeBean implements QueryInventoryFacade{

	Logger log = Logger.getLogger(QueryInventoryFacadeBean.class);
	
	@EJB
	private ItemDataService itemDataService;
	
	@EJB
	private ClientService clientService;
	
	@Inject
	private LotEntityService lotService;
	
	@EJB
	private QueryInventoryBusiness invBusiness;
	
	

	/*  
	 * @see de.linogistix.los.inventory.connector.QueryInventoryRemote#getInventoryByArticle(java.lang.String, java.lang.String)
	 */
	public QueryInventoryTO[] getInventoryByArticle( String clientRef, String articleRef, boolean consolidateLot) throws InventoryException{
		return getInventoryByArticle( clientRef, articleRef, consolidateLot, false );
	}
	public QueryInventoryTO[] getInventoryByArticle( String clientRef, String articleRef, boolean consolidateLot, boolean withAmountOnly) throws InventoryException{
		String logStr = "getInventoryByArticle ";
		Date dateStart = new Date();
		log.debug(logStr+"client="+clientRef+", item="+articleRef+", consolidateLot="+consolidateLot);
		QueryInventoryTO[] ret = new QueryInventoryTO[0];
		
		Client c = clientService.getByNumber(clientRef);
		
		if(c == null){
			log.error("--- !!! NO SUCH CLIENT "+clientRef+" !!! ---");
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_CLIENT, clientRef);
		}
		
		ItemData idat = itemDataService.getByItemNumber(c,  articleRef);
		
		if(idat == null){
			log.error("--- !!! NO ITEM WITH NUMBER > "+articleRef+" !!! ---");
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_ITEMDATA, articleRef);
		}else{
			ret = invBusiness.getInventory(c, idat, consolidateLot, withAmountOnly);
		}
		
		Date dateEnd=new Date();
		log.debug(logStr+"size="+ret.length+", time="+(dateEnd.getTime()-dateStart.getTime())+" ms");
		return ret;
	}

	/*  
	 * @see de.linogistix.los.inventory.connector.QueryInventoryRemote#getInventoryByBatch(java.lang.String, java.lang.String, java.lang.String)
	 */
	public QueryInventoryTO getInventoryByLot( String clientRef, String articleRef, String lotRef) throws InventoryException {
		return getInventoryByLot( clientRef, articleRef, lotRef, false );
	}
	public QueryInventoryTO getInventoryByLot( String clientRef, String articleRef, String lotRef, boolean withAmountOnly) throws InventoryException {
		String logStr = "getInventoryByLot ";
		Date dateStart = new Date();
		log.debug(logStr+"client="+clientRef+", item="+articleRef+", lot="+lotRef);

		Client c = clientService.getByNumber(clientRef);
		Lot lot;
		ItemData idat;
		
		if(c == null){
			log.error("--- !!! NO SUCH CLIENT "+clientRef+" !!! ---");
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_CLIENT, clientRef);
		}

		idat = itemDataService.getByItemNumber(c, articleRef);
		if (idat == null){
			log.error("--- !!! NO ITEM WITH NUMBER > "+articleRef+" !!! ---");
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_ITEMDATA, articleRef);
		}
		
		lot = lotService.read(idat, lotRef);
		if (lot == null) {
			log.error("--- !!! NO LOT WITH NUMBER > " + lotRef + " !!! ---");
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_LOT, lotRef);
		}
		
		QueryInventoryTO ret = invBusiness.getInventory(c, lot, withAmountOnly); 
		Date dateEnd=new Date();
		log.debug(logStr+"time="+(dateEnd.getTime()-dateStart.getTime())+" ms");
		return ret;
		
	}

	
	/*  
	 * @see de.linogistix.los.inventory.connector.QueryInventoryRemote#getInventoryList(java.lang.String)
	 */
	public QueryInventoryTO[] getInventoryList( String clientRef, boolean consolidateLot) throws InventoryException{
		return getInventoryList( clientRef, consolidateLot, false );
	}
	public QueryInventoryTO[] getInventoryList( String clientRef, boolean consolidateLot, boolean withAmountOnly) throws InventoryException{
		String logStr = "getInventoryList ";
		Date dateStart = new Date();
		log.debug(logStr+"client="+clientRef+", consolidateLot="+consolidateLot+", withAmountOnly="+withAmountOnly);
		
		QueryInventoryTO[] ret = new QueryInventoryTO[0];
		
		Client c = clientService.getByNumber(clientRef);
		
		if(c == null){
			log.error("--- !!! NO SUCH CLIENT "+clientRef+" !!! ---");
		}
		else {
			ret = invBusiness.getInventory(c, consolidateLot, withAmountOnly);
		}
		
		Date dateEnd=new Date();
		log.debug(logStr+"size="+ret.length+", time="+(dateEnd.getTime()-dateStart.getTime())+" ms");
		return ret;

	}
	
}
