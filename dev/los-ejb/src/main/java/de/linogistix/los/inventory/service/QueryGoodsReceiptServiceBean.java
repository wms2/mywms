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

import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.Client;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.linogistix.los.inventory.model.LOSGoodsReceiptState;
import de.linogistix.los.inventory.service.dto.GoodsReceiptTO;
import de.linogistix.los.util.businessservice.ContextService;
import de.wms2.mywms.product.ItemDataNumber;

@Stateless
public class QueryGoodsReceiptServiceBean 
		implements QueryGoodsReceiptService, QueryGoodsReceiptServiceRemote 
{

	@EJB
	private ContextService ctxService;

	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	
	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.service.QueryGoodsReceiptService#getById(long)
	 */
	public LOSGoodsReceipt getById(long id) throws UnAuthorizedException, EntityNotFoundException {

        LOSGoodsReceipt gr = manager.find(LOSGoodsReceipt.class, id);
        
        if(gr == null){
        	throw new EntityNotFoundException(
                    ServiceExceptionKey.NO_ENTITY_WITH_ID);
            
        }
        
        Client callersClient = ctxService.getCallersClient();
        
        if (!callersClient.isSystemClient() 
        	&& !gr.getClient().equals(callersClient))
        {
        	throw new UnAuthorizedException();
        }
                
        return gr;
    }
	
	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.service.QueryGoodsReceiptService#fetchEager(long)
	 */
	@SuppressWarnings("unused")
	public LOSGoodsReceipt fetchEager(long id) throws UnAuthorizedException, EntityNotFoundException {

        LOSGoodsReceipt gr = manager.find(LOSGoodsReceipt.class, id);
        
        if(gr == null){
        	throw new EntityNotFoundException(
                    ServiceExceptionKey.NO_ENTITY_WITH_ID);
            
        }
        
        Client callersClient = ctxService.getCallersClient();
        
        if (!callersClient.isSystemClient() 
        	&& !gr.getClient().equals(callersClient))
        {
        	throw new UnAuthorizedException();
        }
        
        // undo lazy initialization
        for(LOSGoodsReceiptPosition p:gr.getPositionList());
        
        for(LOSAdvice ad:gr.getAssignedAdvices());
        
        gr.getClient().getName();
        
        return gr;
    }
	
	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.service.QueryGoodsReceiptService#getListByStates(de.linogistix.los.inventory.model.LOSGoodsReceiptState[])
	 */
	@SuppressWarnings("unchecked")
	public List<LOSGoodsReceipt> getListByStates(LOSGoodsReceiptState... states) {
		
		Client callersClient = ctxService.getCallersClient();
		
		StringBuffer qstr = new StringBuffer();
        qstr.append("SELECT gr FROM ");
        qstr.append(LOSGoodsReceipt.class.getSimpleName()+" gr ");
        
        String conj = "WHERE ";
        
        if(states.length>0){
			qstr.append(conj+"( gr.receiptState=:s0 ");
			conj = "AND ";
		}
		
		for(int i=1;i<states.length;i++){
			qstr.append(" OR gr.receiptState=:s"+i);
		}
		
		if(states.length>0){
			qstr.append(" )");
		}
		
		if (!callersClient.isSystemClient()) {
            qstr.append(conj+"gr.client = :cl ");
        }
		
		Query query = manager.createQuery(qstr.toString());
        
		int y = 0;
		for(LOSGoodsReceiptState s:states){
			query.setParameter("s"+y, s);
			y++;
		}
		
		if (!callersClient.isSystemClient()) {
        	query.setParameter("cl", callersClient);
        }
		
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<GoodsReceiptTO> getDtoListByStates(LOSGoodsReceiptState... states) {
		
		Client callersClient = ctxService.getCallersClient();
		
		StringBuffer qstr = new StringBuffer();
        qstr.append("SELECT new de.linogistix.los.inventory.service.dto.GoodsReceiptTO(");
        qstr.append("gr.id, gr.goodsReceiptNumber, gr.forwarder, gr.deliveryNoteNumber) ");
        qstr.append("FROM "+LOSGoodsReceipt.class.getSimpleName()+" gr ");
        
        String conj = "WHERE ";
        
        if(states.length>0){
			qstr.append(conj+"( gr.receiptState=:s0 ");
			conj = "AND ";
		}
		
		for(int i=1;i<states.length;i++){
			qstr.append(" OR gr.receiptState=:s"+i);
		}
		
		if(states.length>0){
			qstr.append(" )");
		}
		
		if (!callersClient.isSystemClient()) {
            qstr.append(conj+"gr.client = :cl ");
        }
		
		qstr.append(" ORDER BY gr.goodsReceiptNumber");
		
		Query query = manager.createQuery(qstr.toString());
        
		int y = 0;
		for(LOSGoodsReceiptState s:states){
			query.setParameter("s"+y, s);
			y++;
		}
		
		if (!callersClient.isSystemClient()) {
        	query.setParameter("cl", callersClient);
        }
		
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<GoodsReceiptTO> getOpenDtoListByCode(String code, boolean limitAmountToNotified, LOSGoodsReceiptState... states) {
		Client callersClient = ctxService.getCallersClient();
		
		StringBuffer qstr = new StringBuffer();
        qstr.append("SELECT distinct new de.linogistix.los.inventory.service.dto.GoodsReceiptTO(");
        qstr.append("gr.id, gr.goodsReceiptNumber, gr.forwarder, gr.deliveryNoteNumber) ");
        qstr.append("FROM "+LOSGoodsReceipt.class.getSimpleName()+" gr ");
        qstr.append(" join gr.assignedAdvices adv");
        qstr.append(" WHERE ( gr.goodsReceiptNumber like :code or gr.deliveryNoteNumber like :code or adv.externalAdviceNumber like :code or adv.adviceNumber like :code or adv.itemData.number like :code or adv.itemData.id = ANY(");
        qstr.append(" select idn.itemData.id from "+ItemDataNumber.class.getSimpleName()+" idn where idn.number like :code) ) ");

        if( limitAmountToNotified ) {
        	qstr.append(" and adv.receiptAmount < adv.notifiedAmount ");
        }

        String conj = " and ";
        
        if(states.length>0){
			qstr.append(conj+"( gr.receiptState=:s0 ");
			conj = "AND ";
		}
		
		for(int i=1;i<states.length;i++){
			qstr.append(" OR gr.receiptState=:s"+i);
		}
		
		if(states.length>0){
			qstr.append(" )");
		}
		
		if (!callersClient.isSystemClient()) {
            qstr.append(conj+"gr.client = :cl ");
        }
		
		qstr.append(" ORDER BY gr.goodsReceiptNumber");
		
		Query query = manager.createQuery(qstr.toString());
        
		int y = 0;
		for(LOSGoodsReceiptState s:states){
			query.setParameter("s"+y, s);
			y++;
		}
		
		query.setParameter("code", code);
		
		if (!callersClient.isSystemClient()) {
        	query.setParameter("cl", callersClient);
        }
		
		return query.getResultList();
	}

}
