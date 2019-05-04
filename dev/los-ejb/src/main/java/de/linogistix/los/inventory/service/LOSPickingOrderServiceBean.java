/*
 * Copyright (c) 2009-2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.service.BasicServiceBean;

import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSOrderStrategy;
import de.linogistix.los.inventory.model.LOSPickingOrder;
import de.linogistix.los.inventory.model.LOSPickingPosition;
import de.linogistix.los.util.businessservice.ContextService;
/**
 * @author krane
 *
 */
@Stateless
public class LOSPickingOrderServiceBean extends BasicServiceBean<LOSPickingOrder> implements LOSPickingOrderService{
	Logger log = Logger.getLogger(LOSPickingOrderServiceBean.class);

	@EJB
	private ContextService contextService;
	@EJB
	private InventoryGeneratorService sequenceService;
	@EJB
	private EntityGenerator entityGenerator;
	@EJB
	private LOSOrderStrategyService orderStratService;
	@EJB
	private LOSPickingOrderService pickingOrderService;
	
	public LOSPickingOrder create(Client client, LOSOrderStrategy strategy, String sequenceName) throws FacadeException {
		String logStr = "create ";

		if( client == null ) {
			client = contextService.getCallersClient();
		}
		if( strategy == null ) {
			strategy = orderStratService.getDefault(client);
		}
		
		
		// Check order number
		LOSPickingOrder order = null;
		String number = null; 
		int i = 0;
		while( i++ < 10000 ) {
			number = sequenceService.generatePickOrderNumber(client, sequenceName);
			order = pickingOrderService.getByNumber(number);
			if( order != null ) {
				log.warn(logStr+"order already exists!!! number=" + number+". Try next...");
				continue;
			}
			break;
		}		
		
		order = entityGenerator.generateEntity(LOSPickingOrder.class);

		order.setClient(client);
		order.setNumber(number);
		order.setStrategy(strategy);
		order.setPositions(new ArrayList<LOSPickingPosition>());
		
		manager.persist(order);
		
		return order;
	}
	
	@SuppressWarnings("unchecked")
	public List<LOSPickingOrder> getByCustomerOrder(LOSCustomerOrder order) {
//		String logStr = "getByCustomerOrder ";
//		Date dateStart = new Date();
		Query query = manager.createNamedQuery("LOSPickingOrder.queryByCustomerOrder");
		query.setParameter("customerOrder", order);
		List<LOSPickingOrder> res = query.getResultList();
		
//		Date dateEnd = new Date();
//		log.info(logStr+"select picking orders in "+(dateEnd.getTime()-dateStart.getTime())+" ms");
		return res;
	}

	@SuppressWarnings("unchecked")
	public List<LOSPickingOrder> getByCustomerOrderNumber(String customerOrderNumber) {
		StringBuilder b = new StringBuilder();	
		b.append("SELECT po FROM ");
		b.append(LOSPickingOrder.class.getSimpleName());
		b.append(" po ");
		b.append(" WHERE po.customerOrderNumber=:number " );
	
		Query query = manager.createQuery(new String(b));
		query.setParameter("number", customerOrderNumber);

		return query.getResultList();
	}

	public LOSPickingOrder getByNumber( String number ) {
		StringBuffer b = new StringBuffer();	
		b.append("SELECT po FROM ");
		b.append(LOSPickingOrder.class.getSimpleName());
		b.append(" po ");
		b.append(" WHERE po.number=:number " );
	
		Query query = manager.createQuery(new String(b));
		query.setParameter("number", number);

		try{
			return (LOSPickingOrder) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}

	}


}
