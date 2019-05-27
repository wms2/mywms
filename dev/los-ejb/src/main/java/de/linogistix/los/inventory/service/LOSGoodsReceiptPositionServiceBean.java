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

import org.mywms.service.BasicServiceBean;
import org.mywms.service.UserService;

import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.wms2.mywms.inventory.StockUnit;

@Stateless
public class LOSGoodsReceiptPositionServiceBean
        extends BasicServiceBean<LOSGoodsReceiptPosition>
        implements LOSGoodsReceiptPositionService {

    @Resource
    SessionContext ctx;
    @EJB
    UserService userService;
    @Resource
    EJBContext context;
    
	@SuppressWarnings("unchecked")
	public List<LOSGoodsReceiptPosition> getByStockUnit(StockUnit su) {
		StringBuffer s = new StringBuffer();
		s.append(" SELECT o FROM ");
		s.append(LOSGoodsReceiptPosition.class.getName());
		s.append(" o ");
		s.append(" WHERE ");
		s.append(" o.stockUnit=:su ");
		
		Query q = manager.createQuery(new String(s));
		q = q.setParameter("su", su);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<LOSGoodsReceiptPosition> getByStockUnit(String stockUnitStr){
		StringBuffer s = new StringBuffer();
		s.append(" SELECT o FROM ");
		s.append(LOSGoodsReceiptPosition.class.getName());
		s.append(" o ");
		s.append(" WHERE ");
		s.append(" o.stockUnitStr=:stockUnitStr ");
		
		Query q = manager.createQuery(new String(s));
		q = q.setParameter("stockUnitStr", stockUnitStr);
		return q.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<LOSGoodsReceiptPosition> getByUnitloadLabel(String labelId){
		StringBuffer s = new StringBuffer();
		s.append(" SELECT o FROM ");
		s.append(LOSGoodsReceiptPosition.class.getName());
		s.append(" o ");
		s.append(" WHERE ");
		s.append(" o.unitLoad=:unitLoad ");
		
		Query q = manager.createQuery(new String(s));
		q = q.setParameter("unitLoad", labelId);
		return q.getResultList();
	}
	
	public LOSGoodsReceiptPosition getByNumber(String number) {
		Query q = manager.createNamedQuery("LOSGoodsReceiptPosition.queryByNumber");
		q = q.setParameter("number", number);
        try {
            return (LOSGoodsReceiptPosition) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

	public boolean existsByNumber(String number) {
		Query q = manager.createNamedQuery("LOSGoodsReceiptPosition.existsByNumber");
		q = q.setParameter("number", number);
        try {
            q.getSingleResult();
        } catch (NoResultException nre) {
            return false;
        }
        return true;
    }
	public long queryNumPos( LOSGoodsReceipt gr ) {
        Query query = manager.createQuery(
                "SELECT count(grPos) FROM " + LOSGoodsReceiptPosition.class.getSimpleName() + " grPos " +
                "WHERE grPos.goodsReceipt = :gr");

        query.setParameter("gr", gr);

        try {
            Long num = (Long)query.getSingleResult();
            if( num != null ) {
            	return num.longValue();
            }
        } catch (NoResultException nre) {
        }
        return 0L;
	}

}
