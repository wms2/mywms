/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;

import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.util.businessservice.ContextService;

@Stateless
public class QueryStockServiceBean 
		implements QueryStockService, QueryStockServiceRemote 
{
	
	@EJB
	private ContextService ctxService;
	
	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	
	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.service.QueryStockService#getListByUnitLoad(de.linogistix.los.location.model.LOSUnitLoad)
	 */
	@SuppressWarnings("unchecked")
	public List<StockUnit> getListByUnitLoad(LOSUnitLoad ul) {
		
		Client callersClient = ctxService.getCallersClient();
		
		StringBuffer qstr = new StringBuffer("SELECT su FROM ");
        qstr.append(StockUnit.class.getSimpleName()+ " su ");
        qstr.append("WHERE su.unitLoad = :ul ");
		
        if (!callersClient.isSystemClient()) {
            qstr.append("AND su.client = :cl ");
        }
        
		Query query = manager.createQuery(qstr.toString());

        query.setParameter("ul", ul);
        
        if (!callersClient.isSystemClient()) {
        	query.setParameter("cl", callersClient);
        }

        return (List<StockUnit>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<StockUnit> getListByUnitLoadLabel( String label ) {
		
		Client callersClient = ctxService.getCallersClient();
		
		StringBuffer qstr = new StringBuffer("SELECT su FROM ");
        qstr.append(StockUnit.class.getSimpleName()+ " su ");
        qstr.append("WHERE su.unitLoad.labelId = :label ");
		
        if (!callersClient.isSystemClient()) {
            qstr.append("AND su.client = :cl ");
        }
        
		Query query = manager.createQuery(qstr.toString());

        query.setParameter("label", label);
        
        if (!callersClient.isSystemClient()) {
        	query.setParameter("cl", callersClient);
        }

        return (List<StockUnit>) query.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.service.QueryStockService#getListByLot(org.mywms.model.Lot)
	 */
	@SuppressWarnings("unchecked")
	public List<StockUnit> getListByLot(Lot lot, boolean checkAvailable) {
		
		Client callersClient = ctxService.getCallersClient();
		
		StringBuffer qstr = new StringBuffer();
        qstr.append("SELECT su FROM " + StockUnit.class.getSimpleName()+ " su ");
                	
    	if(lot != null){
    		qstr.append("WHERE su.lot = :l ");
    	}
    	else{
    		qstr.append("WHERE su.lot IS NULL ");
    	}
		
        if (!callersClient.isSystemClient()) {
            qstr.append("AND su.client = :cl ");
        }
        if( checkAvailable ) {
        	qstr.append(" and lock = 0 and amount > 0 ");
        }

		Query query = manager.createQuery(qstr.toString());

		if(lot != null){
			query.setParameter("l", lot);
		}
        
        if (!callersClient.isSystemClient()) {
        	query.setParameter("cl", callersClient);
        }

        return (List<StockUnit>) query.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.service.QueryStockService#getListByItemData(org.mywms.model.ItemData)
	 */
	@SuppressWarnings("unchecked")
	public List<StockUnit> getListByItemData(ItemData item, boolean checkAvailable) {
		
		Client callersClient = ctxService.getCallersClient();
		
		StringBuffer qstr = new StringBuffer();
        qstr.append("SELECT su FROM " + StockUnit.class.getSimpleName()+ " su ");
        qstr.append("WHERE su.itemData = :it ");
    			
        if (!callersClient.isSystemClient()) {
            qstr.append("AND su.client = :cl ");
        }
        if( checkAvailable ) {
        	qstr.append(" and lock = 0 and amount > 0 ");
        }
        qstr.append(" ORDER BY su.created ASC");

		Query query = manager.createQuery(qstr.toString());

		query.setParameter("it", item);
		        
        if (!callersClient.isSystemClient()) {
        	query.setParameter("cl", callersClient);
        }

        return (List<StockUnit>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<StockUnit> getListByItemDataNoLot(ItemData item) {
		
		Client callersClient = ctxService.getCallersClient();
		
		StringBuffer qstr = new StringBuffer();
        qstr.append("SELECT su FROM " + StockUnit.class.getSimpleName()+ " su ");
        qstr.append("WHERE su.itemData = :it ");
    	qstr.append("AND su.lot IS NULL ");
    			
        if (!callersClient.isSystemClient()) {
            qstr.append("AND su.client = :cl ");
        }
        
		Query query = manager.createQuery(qstr.toString());

		query.setParameter("it", item);
        
        if (!callersClient.isSystemClient()) {
        	query.setParameter("cl", callersClient);
        }

        return (List<StockUnit>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<StockUnit> getListByStorageLocation(LOSStorageLocation loc) {
		
		Client callersClient = ctxService.getCallersClient();
		
		StringBuffer sb = new StringBuffer();
		sb = new StringBuffer(" SELECT su FROM ");
		sb.append(StockUnit.class.getName()+" su  ");
		sb.append(" JOIN su.unitLoad ul  ");
		sb.append(" WHERE ul.storageLocation = :loc ");
		
		if(!callersClient.isSystemClient()){
			sb.append("AND su.client =:cl ");
		}

		Query query = manager.createQuery(sb.toString());

		query.setParameter("loc", loc);
        
        if (!callersClient.isSystemClient()) {
        	query.setParameter("cl", callersClient);
        }

        return (List<StockUnit>) query.getResultList();
	}

}
