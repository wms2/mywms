/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mywms.model.Client;
import org.mywms.model.User;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.UserService;

import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.linogistix.los.inventory.model.LOSGoodsReceiptState;
import de.linogistix.los.util.BusinessObjectHelper;

@Stateless
public class LOSGoodsReceiptServiceBean
        extends BasicServiceBean<LOSGoodsReceipt>
        implements LOSGoodsReceiptService {

    @Resource
    private SessionContext ctx;
    @EJB
    private UserService userService;
    @Resource
    private EJBContext context;
	@EJB
	private EntityGenerator entityGenerator;
	
    public LOSGoodsReceipt createGoodsReceipt(Client client, String number) {

        
        User  operator = new BusinessObjectHelper(this.ctx,this.userService,this.context).getCallersUser();
		
        LOSGoodsReceipt grr = entityGenerator.generateEntity(LOSGoodsReceipt.class);
        grr.setClient(client);
        grr.setGoodsReceiptNumber(number);
        grr.setOperator(operator);

        manager.persist(grr);

        return grr;
    }

    public LOSGoodsReceipt getByGoodsReceiptNumber(String number) {

        Query query = manager.createQuery(
                "SELECT gr FROM " + LOSGoodsReceipt.class.getSimpleName() + " gr " +
                "WHERE gr.goodsReceiptNumber=:n");

        query.setParameter("n", number);

        try {
            return (LOSGoodsReceipt) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;

        }
    }
    
    @Override
    public LOSGoodsReceipt getOpenByDeliveryNoteNumber(String number) {

        Query query = manager.createQuery(
                "SELECT gr FROM " + LOSGoodsReceipt.class.getSimpleName() + " gr " +
                "WHERE gr.deliveryNoteNumber=:n"+
                " AND receiptState in (:raw,:accepted)" );

        query.setParameter("n", number);
        query.setParameter("raw", LOSGoodsReceiptState.RAW);
        query.setParameter("accepted", LOSGoodsReceiptState.ACCEPTED);

        try {
            return (LOSGoodsReceipt) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;

        }
    }
    

	public void delete(LOSGoodsReceipt r){
		r = manager.find(LOSGoodsReceipt.class, r.getId());
		List<LOSGoodsReceiptPosition> pos = r.getPositionList();
		for (LOSGoodsReceiptPosition p : pos){
			p = manager.find(LOSGoodsReceiptPosition.class, p.getId());
			if (p != null){
				manager.remove(p);
			}
		}
		manager.remove(r);
		manager.flush();
		
	}
	
	@SuppressWarnings("unchecked")
	public List<LOSGoodsReceipt> getByAdvice( LOSAdvice adv ) {
        Query query = manager.createQuery(
                "SELECT gr FROM " + LOSGoodsReceipt.class.getSimpleName() + " gr " +
                "WHERE :adv MEMBER OF gr.assignedAdvices");

        query.setParameter("adv", adv);

        try {
            return query.getResultList();
        } catch (NoResultException nre) {
            return null;
        }
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LOSGoodsReceipt> getByDeliveryNoteNumber(String number) {
		 Query query = manager.createQuery(
	                "SELECT gr FROM " + LOSGoodsReceipt.class.getSimpleName() + " gr " +
	                "WHERE gr.deliveryNoteNumber=:n" );
        query.setParameter("n", number);
        return query.getResultList();
	}
}
