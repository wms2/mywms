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
import de.linogistix.los.inventory.service.dto.GoodsReceiptTO;
import de.linogistix.los.util.businessservice.ContextService;
import de.wms2.mywms.advice.AdviceLine;
import de.wms2.mywms.goodsreceipt.GoodsReceipt;
import de.wms2.mywms.goodsreceipt.GoodsReceiptLine;
import de.wms2.mywms.product.ItemDataNumber;

@Stateless
public class QueryGoodsReceiptServiceBean 
		implements QueryGoodsReceiptServiceRemote 
{

	@EJB
	private ContextService ctxService;

	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	
	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.service.QueryGoodsReceiptService#fetchEager(long)
	 */
	@SuppressWarnings("unused")
	public GoodsReceipt fetchEager(long id) throws UnAuthorizedException, EntityNotFoundException {

		GoodsReceipt gr = manager.find(GoodsReceipt.class, id);
        
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
        for(GoodsReceiptLine p:gr.getLines());
        
        for(AdviceLine ad:gr.getAdviceLines());
        
        gr.getClient().getName();
        
        return gr;
    }

	@SuppressWarnings("unchecked")
	public List<GoodsReceiptTO> getDtoListByStates(int minState, int maxState) {
		
		Client callersClient = ctxService.getCallersClient();
		
		StringBuffer qstr = new StringBuffer();
        qstr.append("SELECT new de.linogistix.los.inventory.service.dto.GoodsReceiptTO(");
        qstr.append("gr.id, gr.orderNumber, gr.carrierName, gr.deliveryNoteNumber) ");
        qstr.append("FROM "+GoodsReceipt.class.getSimpleName()+" gr ");
        qstr.append("WHERE gr.state>=:minState and gr.state<=:maxState");
		
		if (!callersClient.isSystemClient()) {
            qstr.append(" and gr.client = :cl ");
        }
		
		qstr.append(" ORDER BY gr.orderNumber");
		
		Query query = manager.createQuery(qstr.toString());
		query.setParameter("minState", minState);
		query.setParameter("maxState", maxState);
		if (!callersClient.isSystemClient()) {
        	query.setParameter("cl", callersClient);
        }
		
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<GoodsReceiptTO> getOpenDtoListByCode(String code, boolean limitAmountToNotified, int minState, int maxState) {
		Client callersClient = ctxService.getCallersClient();
		
		StringBuffer qstr = new StringBuffer();
        qstr.append("SELECT distinct new de.linogistix.los.inventory.service.dto.GoodsReceiptTO(");
        qstr.append("gr.id, gr.orderNumber, gr.carrierName, gr.deliveryNoteNumber) ");
        qstr.append("FROM "+GoodsReceipt.class.getSimpleName()+" gr ");
        qstr.append(" join gr.adviceLines adv");
        qstr.append(" WHERE ( gr.orderNumber like :code or gr.deliveryNoteNumber like :code or adv.externalNumber like :code or adv.itemData.number like :code or adv.itemData.id = ANY(");
        qstr.append(" select idn.itemData.id from "+ItemDataNumber.class.getSimpleName()+" idn where idn.number like :code) ) ");

        if( limitAmountToNotified ) {
        	qstr.append(" and adv.confirmedAmount < adv.Amount ");
        }

        qstr.append(" and gr.state>=:minState and gr.state<=:maxState");

		if (!callersClient.isSystemClient()) {
            qstr.append(" and gr.client = :cl ");
        }

		qstr.append(" ORDER BY gr.orderNumber");
		
		Query query = manager.createQuery(qstr.toString());
		query.setParameter("minState", minState);
		query.setParameter("maxState", maxState);
		query.setParameter("code", code);
		if (!callersClient.isSystemClient()) {
        	query.setParameter("cl", callersClient);
        }
		
		return query.getResultList();
	}

}
