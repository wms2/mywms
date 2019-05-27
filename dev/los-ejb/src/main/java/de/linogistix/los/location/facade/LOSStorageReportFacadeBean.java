/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.mywms.model.Client;
import org.mywms.service.ClientService;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.inventory.service.ItemDataService;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.exception.LOSLocationExceptionKey;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.product.ItemData;

@Stateless
public class LOSStorageReportFacadeBean implements LOSStorageReportFacade {

	@EJB
	private ItemDataService itemService;
	
	@EJB
	private ClientService clientService;
	
	
	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	
	//--------------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public List<LOSStockListItem> getStockList(String clientName, String itemNumber) throws LOSLocationException
	{
		
		Client client;
		try {
			client = clientService.getByName(clientName);
		} catch (EntityNotFoundException e) {
			throw new LOSLocationException(LOSLocationExceptionKey.NO_CLIENT_WITH_NAME, new Object[]{clientName});
		}
		
		boolean limitClient = false;
		if(!client.isSystemClient()){
			limitClient = true;
		}
		
		boolean limitItem = false;
		ItemData item = null;
		
		if(itemNumber != null && itemNumber.length() > 0){
			
			limitItem = true;
			item = itemService.getByItemNumber(client, itemNumber);
			
			if(item == null){
				item = itemService.getByItemNumber(clientService.getSystemClient(), itemNumber);
			}
			
			if(item == null){
				throw new LOSLocationException(
						LOSLocationExceptionKey.NO_ITEM_WITH_NUMBER, new Object[]{itemNumber});
			}
		}
		
		StringBuffer sb = new StringBuffer("SELECT NEW de.linogistix.los.location.facade.");
		sb.append("LOSStockListItem(su.client.name, su.itemData.number, l.name, su.amount, ul.labelId, sl.name) ");
		sb.append("FROM ");
		sb.append(StockUnit.class.getSimpleName());
		sb.append(" su ");
		sb.append("JOIN su.unitLoad ul JOIN ul.storageLocation sl LEFT JOIN su.lot l ");
		
		String concat = "WHERE";
		if(limitClient){
			sb.append("WHERE su.client=:cl ");
			concat = "AND";
		}
		
		if(limitItem){
			sb.append(concat);
			sb.append(" su.itemData=:it ");
		}else{
			sb.append("ORDER BY su.itemData.number");
		}
		
		Query query = manager.createQuery(sb.toString());
		
		if(limitClient){
			query.setParameter("cl", client);
		}
		
		if(limitItem){
			query.setParameter("it", item);
		}
		
		return query.getResultList();
	}

	//--------------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public List<String> autocompleteClientName(String namepart) {
		
		String lower = namepart.toLowerCase();
		
		Query query = manager.createQuery(
						"SELECT cl.name FROM "+Client.class.getSimpleName()+" cl "+
						"WHERE LOWER(cl.name) LIKE :cln");
		
		query.setParameter("cln", lower+"%");
		
		return query.getResultList();
	}

	//--------------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public List<String> autocompleteItemNumber(String clientName, String numberpart) throws LOSLocationException {
		
		String lower = numberpart.toLowerCase();
		
		Client client;
		try {
			client = clientService.getByName(clientName);
		} catch (EntityNotFoundException e) {
			throw new LOSLocationException(LOSLocationExceptionKey.NO_CLIENT_WITH_NAME, new Object[]{clientName});
		}
		
		boolean limitClient = false;
		if(!client.isSystemClient()){
			limitClient = true;
		}
		
		StringBuffer sb = new StringBuffer("SELECT DISTINCT it.number FROM ");
		sb.append(ItemData.class.getSimpleName());
		sb.append(" it WHERE LOWER(it.number) LIKE :itn ");
		
		if(limitClient){
			sb.append("AND it.client=:cl ");
		}
		
		sb.append("ORDER BY it.number ASC");
		
		Query query = manager.createQuery(sb.toString());
        query.setParameter("itn", lower+"%");

        if (limitClient) {
            query.setParameter("cl", client);
        }
        
		return query.getResultList();
	}

}
