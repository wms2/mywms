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
import javax.persistence.Query;

import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.Client;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.common.service.QueryClientService;
import de.linogistix.los.util.businessservice.ContextService;
import de.wms2.mywms.advice.AdviceLine;
import de.wms2.mywms.goodsreceipt.GoodsReceipt;
import de.wms2.mywms.product.ItemDataNumber;

@Stateless
public class QueryAdviceServiceBean extends BasicServiceBean<AdviceLine>
		implements QueryAdviceService, QueryAdviceServiceRemote {

	@EJB
	private QueryClientService queryClientService;
	
	@EJB
	private ContextService ctxService;
	
	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.service.QueryAdviceService#getById(long)
	 */
	public AdviceLine getById(long id) throws UnAuthorizedException, EntityNotFoundException{
		
		AdviceLine adv = manager.find(AdviceLine.class, id);
        
        if(adv == null){
        	throw new EntityNotFoundException(
                    ServiceExceptionKey.NO_ENTITY_WITH_ID);
            
        }
        
        Client callersClient = ctxService.getCallersClient();
        
        if (!callersClient.isSystemClient() 
        	&& !adv.getAdvice().getClient().equals(callersClient))
        {
        	throw new UnAuthorizedException();
        }
                
        return adv;
	}
	
	@SuppressWarnings("unchecked")
	public List<AdviceLine> getListByGoodsReceipCode(GoodsReceipt gr, String code, boolean limitAmountToNotified) {
		Client callersClient = ctxService.getCallersClient();

		StringBuffer qstr = new StringBuffer();
        qstr.append("SELECT distinct adv ");
        qstr.append("FROM "+GoodsReceipt.class.getSimpleName()+" gr ");
        qstr.append(" join gr.adviceLines adv");
        qstr.append(" WHERE gr = :gr ");
        if( limitAmountToNotified ) {
        	qstr.append(" and adv.confirmedAmount < adv.Amount ");
        }
        qstr.append(" and (adv.lineNumber like :code or adv.itemData.number like :code or adv.itemData.id = ANY(");
        qstr.append("   select idn.itemData.id from "+ItemDataNumber.class.getSimpleName()+" idn where idn.number like :code) ) ");
        
		if (!callersClient.isSystemClient()) {
            qstr.append(" and gr.client = :cl ");
        }
		qstr.append(" ORDER BY adv.lineNumber ");
		
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
