/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.Client;
import org.mywms.model.ItemDataNumber;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.common.service.QueryClientService;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSAdviceState;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.linogistix.los.util.StringTools;
import de.linogistix.los.util.businessservice.ContextService;

@Stateless
public class QueryAdviceServiceBean extends BasicServiceBean<LOSAdvice>
		implements QueryAdviceService, QueryAdviceServiceRemote {
    private static final Logger log = Logger.getLogger(QueryAdviceServiceBean.class);

	@EJB
	private QueryClientService queryClientService;
	
	@EJB
	private ContextService ctxService;
	
	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.service.QueryAdviceService#getById(long)
	 */
	public LOSAdvice getById(long id) throws UnAuthorizedException, EntityNotFoundException{
		
		LOSAdvice adv = manager.find(LOSAdvice.class, id);
        
        if(adv == null){
        	throw new EntityNotFoundException(
                    ServiceExceptionKey.NO_ENTITY_WITH_ID);
            
        }
        
        Client callersClient = ctxService.getCallersClient();
        
        if (!callersClient.isSystemClient() 
        	&& !adv.getClient().equals(callersClient))
        {
        	throw new UnAuthorizedException();
        }
                
        return adv;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.linogistix.los.inventory.service.QueryAdviceService#getByAdviceNumber
	 * (java.lang.String)
	 */
	public LOSAdvice getByAdviceNumber(String number) throws UnAuthorizedException {

		if( StringTools.isEmpty(ctxService.getCallerUserName()) ){
    		throw new UnAuthorizedException();
    	}
		
		Query query = manager.createQuery("SELECT o FROM "
				+ LOSAdvice.class.getSimpleName() + " o "
				+ "WHERE o.adviceNumber=:no");

		query.setParameter("no", number);

		try {
			LOSAdvice adv = (LOSAdvice) query.getSingleResult();
			
			if(!ctxService.getCallersClient().equals(queryClientService.getSystemClient())
	        	&& !ctxService.getCallersClient().equals(adv.getClient()))
        	{
        		throw new UnAuthorizedException();
        	}
			
			return adv;
			
		} catch (NoResultException ex) {
			return null;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.linogistix.los.inventory.service.QueryAdviceService#getByGoodsReceipt
	 * (de.linogistix.los.inventory.model.LOSGoodsReceipt)
	 */
	@SuppressWarnings("unchecked")
	public List<LOSAdvice> getByGoodsReceipt(LOSGoodsReceipt gr) {

		StringBuffer sb = new StringBuffer(
				"SELECT DISTINCT pos.relatedAdvice FROM ");
		sb.append(LOSGoodsReceiptPosition.class.getSimpleName() + " pos ");
		sb.append("WHERE pos.goodsReceipt=:gr");

		Query query = manager.createQuery(sb.toString());

		query = query.setParameter("gr", gr);

		return query.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.service.QueryAdviceService#getByStateAndDateLimit(int, de.linogistix.los.inventory.model.LOSAdviceState[])
	 */
	@SuppressWarnings("unchecked")
	public List<LOSAdvice> getByStateAndDateLimit(int daysFromNow,	LOSAdviceState... adviceStates) throws UnAuthorizedException {
		
		if( StringTools.isEmpty( ctxService.getCallerUserName() ) ||
			!ctxService.getCallersClient().equals(queryClientService.getSystemClient()))
		{
			throw new UnAuthorizedException();
		}
		
		StringBuffer sb = new StringBuffer("SELECT ad FROM ");
		sb.append(LOSAdvice.class.getSimpleName()+" ad ");
		sb.append("WHERE ad.expectedDelivery <= :date ");
		
		if(adviceStates.length>0){
    		sb.append(" AND ( ad.adviceState=?1 ");
    	}
    	
    	int i=1;
    	while(i<adviceStates.length){
    		sb.append(" OR ad.adviceState=?"+(i+1));
    		i++;
    	}
    	
    	if(adviceStates.length>0){
    		sb.append(" )");
    	}
		
    	Query query = manager.createQuery(sb.toString());
    	
    	Calendar cal = Calendar.getInstance();
    	
    	cal.add(Calendar.DAY_OF_MONTH, daysFromNow+1);
    	cal.set(Calendar.SECOND, 0);
    	cal.set(Calendar.MINUTE, 0);
    	cal.set(Calendar.HOUR, 0);
    	
    	query.setParameter("date", cal.getTime());
    	
    	int p = 1;
        for(LOSAdviceState s:adviceStates){
        	query.setParameter(p, s);
        	p++;
        }
    	
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public LOSAdvice getByAdviceNumber(Client client, String number) {

		Client callersClient = ctxService.getCallersClient();
        if (!callersClient.isSystemClient()) {
        	client = callersClient;
        }
        
		StringBuffer sb = new StringBuffer();
        sb.append("SELECT o FROM ");
        sb.append(LOSAdvice.class.getSimpleName()+ " o ");
        sb.append("WHERE o.adviceNumber=:number ");
        if( client != null ) {
            sb.append(" AND id.client = :client ");
        }
        
        Query query = manager.createQuery(sb.toString());
        
        query.setParameter("number", number);
        if( client != null ) {
        	query.setParameter("client", client);
        }
		
		try {
			List<LOSAdvice> advList = query.getResultList();
			if( advList.size()>0 ) {
				return advList.get(0);
			}
		} 
		catch (NoResultException ex) {
			return null;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public LOSAdvice getByExternalId(Client client, String number) {
		
		Client callersClient = ctxService.getCallersClient();
        if (!callersClient.isSystemClient()) {
        	client = callersClient;
        }
        
		StringBuffer sb = new StringBuffer();
        sb.append("SELECT o FROM ");
        sb.append(LOSAdvice.class.getSimpleName()+ " o ");
        sb.append("WHERE o.externalId=:number ");
        if( client != null ) {
            sb.append(" AND id.client = :client ");
        }
        
        Query query = manager.createQuery(sb.toString());
        
        query.setParameter("number", number);
        if( client != null ) {
        	query.setParameter("client", client);
        }
		
		try {
			List<LOSAdvice> advList = query.getResultList();
			if( advList.size()>0 ) {
				return advList.get(0);
			}
		} 
		catch (NoResultException ex) {
			return null;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public LOSAdvice getByExternalNumber(Client client, String number)  {
		
		Client callersClient = ctxService.getCallersClient();
        if (!callersClient.isSystemClient()) {
        	client = callersClient;
        }
        
		StringBuffer sb = new StringBuffer();
        sb.append("SELECT o FROM ");
        sb.append(LOSAdvice.class.getSimpleName()+ " o ");
        sb.append("WHERE o.externalAdviceNumber=:number ");
        if( client != null ) {
            sb.append(" AND id.client = :client ");
        }
        
        Query query = manager.createQuery(sb.toString());
        
        query.setParameter("number", number);
        if( client != null ) {
        	query.setParameter("client", client);
        }
		
		try {
			List<LOSAdvice> advList = query.getResultList();
			if( advList.size()>0 ) {
				return advList.get(0);
			}
		} 
		catch (NoResultException ex) {
			return null;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<LOSAdvice> getListByGoodsReceipCode(LOSGoodsReceipt gr, String code, boolean limitAmountToNotified) {
		Client callersClient = ctxService.getCallersClient();

		StringBuffer qstr = new StringBuffer();
        qstr.append("SELECT distinct adv ");
        qstr.append("FROM "+LOSGoodsReceipt.class.getSimpleName()+" gr ");
        qstr.append(" join gr.assignedAdvices adv");
        qstr.append(" WHERE gr = :gr ");
        if( limitAmountToNotified ) {
        	qstr.append(" and adv.receiptAmount < adv.notifiedAmount ");
        }
        qstr.append(" and (adv.adviceNumber like :code or adv.itemData.number like :code or adv.itemData.id = ANY(");
        qstr.append("   select idn.itemData.id from "+ItemDataNumber.class.getSimpleName()+" idn where idn.number like :code) ) ");
        
		if (!callersClient.isSystemClient()) {
            qstr.append(" and gr.client = :cl ");
        }
		qstr.append(" ORDER BY adv.adviceNumber ");
		
		String q = qstr.toString();
//		log.debug("Query="+q);
		Query query = manager.createQuery(q);
		query.setParameter("code", code);
		query.setParameter("gr", gr);
		
		if (!callersClient.isSystemClient()) {
        	query.setParameter("cl", callersClient);
        }

		return query.getResultList();
	}


}
