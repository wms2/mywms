/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.Client;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.customization.EntityGenerator;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.product.ItemData;


/**
 * 
 * @author trautm
 */
@Stateless
public class LOSLotServiceBean extends BasicServiceBean<Lot> implements
		LOSLotService {
	@EJB
	private EntityGenerator entityGenerator;
	private static final Logger log = Logger.getLogger(LOSLotServiceBean.class);
	
	public Lot create(Client c, ItemData idat, String lotRef,
			Date production, Date useNotBefore, Date bestBeforeEnd) {

		if (c == null) {
			throw new NullPointerException("Client must not be null");
		}
		if (idat == null) {
			throw new NullPointerException("ItemData must not be null");
		}
		if (production == null) {
			throw new NullPointerException("production date must not be null");
		}
		if (useNotBefore != null && useNotBefore != null
				&& bestBeforeEnd != null && useNotBefore.after(bestBeforeEnd)) {
			throw new IllegalArgumentException(
					"validFrom cannot be after validUntil");
		}

		Lot l = entityGenerator.generateEntity(Lot.class);

		l.setClient(c);
		l.setItemData(idat);
		l.setName(lotRef);
		l.setDate(production);
		l.setUseNotBefore(useNotBefore);
		l.setBestBeforeEnd(bestBeforeEnd);

		manager.persist(l);
		manager.flush();

		log.info("CREATED Lot: " + l.toDescriptiveString());
		
		return l;
	}

	@SuppressWarnings("unchecked")
	public List<Lot> getListByName(Client client, String lotName, String itemDataNumber){
		
		Query query =
            manager.createQuery("SELECT lot FROM "
                + Lot.class.getSimpleName()
                + " lot "
                + "WHERE lot.name=:name "
                + " AND lot.client=:cl "
                + " AND lot.itemData.number=:idat "
                + " AND lot.itemData.client=:cl " );

        query.setParameter("name", lotName);
        query.setParameter("idat", itemDataNumber);
        query.setParameter("cl", client);

        return query.getResultList(); 
	}

	@SuppressWarnings("unchecked")
	public List<Lot> getTooOld(Client c) {
		
		List<Lot> ret;
		
		if (c == null) {
			throw new NullPointerException("Client must not be null");
		}
		GregorianCalendar today = new GregorianCalendar();
		today.set(GregorianCalendar.HOUR_OF_DAY, 0);
		today.set(GregorianCalendar.MINUTE,0);
		today.set(GregorianCalendar.SECOND,0);
		
		String s = "SELECT lot FROM "
		+ Lot.class.getSimpleName() + " lot "
		+ " WHERE lot.bestBeforeEnd < :today ";
		
		if (!c.isSystemClient()){
			s = s + " AND lot.client=:client ";
		}
		Query query = manager.createQuery(s);

		query.setParameter("today", today.getTime());
		if (!c.isSystemClient()){
			query.setParameter("client", c);
		}
		ret  = (query.getResultList());
		return ret;
	
	}

	@SuppressWarnings("unchecked")
	public List<Lot> getNotToUse(Client c) {
		
		List<Lot> ret;
		
		if (c == null) {
			throw new NullPointerException("Client must not be null");
		}

		GregorianCalendar today = new GregorianCalendar();
		
		today.set(GregorianCalendar.HOUR_OF_DAY, 23);
		today.set(GregorianCalendar.MINUTE,59);
		today.set(GregorianCalendar.SECOND,59);
		String s = "SELECT lot FROM "
		+ Lot.class.getSimpleName() + " lot "
		+ " WHERE lot.useNotBefore > :today ";
		
		if (!c.isSystemClient()){
			s = s + " AND lot.client=:client ";
		}
		Query query = manager.createQuery(s);

		query.setParameter("today", today.getTime());
		if (!c.isSystemClient()){
			query.setParameter("client", c);
		}
		ret = (query.getResultList());
		return ret;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Lot> getToUseFromNow(Client c) {
		
		List<Lot> ret;
		
		if (c == null) {
			throw new NullPointerException("Client must not be null");
		}

		GregorianCalendar today = new GregorianCalendar();
		
		today.set(GregorianCalendar.HOUR_OF_DAY, 0);
		today.set(GregorianCalendar.MINUTE,0);
		today.set(GregorianCalendar.SECOND,0);
		String s = "SELECT lot FROM "
		+ Lot.class.getSimpleName() + " lot "
		+ " WHERE lot.useNotBefore <= :today "
		+ " AND lot.lock=:lock ";
		if (!c.isSystemClient()){
			s = s + " AND lot.client=:client ";
		}
		Query query = manager.createQuery(s);

		query.setParameter("today", today.getTime());
		if (!c.isSystemClient()){
			query.setParameter("client", c);
		}
		query.setParameter("lock", LotLockState.LOT_TOO_YOUNG.getLock());
		ret = (query.getResultList());
		return ret;
	}

	public Lot getByNameAndItemData(Client c, String lotName, String idat) throws EntityNotFoundException{
		if (lotName == null) {
			throw new NullPointerException("lotName must not be null");
		}

		if (c == null) {
			throw new NullPointerException("Client must not be null");
		}

		if (idat == null) {
			throw new NullPointerException("Item data must not be null");
		}

		Query query = manager.createQuery("SELECT lot FROM "
				+ Lot.class.getSimpleName() + " lot "
				+ " WHERE lot.name=:name" 
				+ " AND " 
				+ " lot.client=:client"
				+ " AND "
				+ " lot.itemData.number=:idatNumber"
		);

		query.setParameter("name", lotName);
		query.setParameter("client", c);
		query.setParameter("idatNumber", idat);

		try {
			return (Lot) query.getSingleResult();
		} catch (NoResultException e) {
			throw new EntityNotFoundException(
					ServiceExceptionKey.NO_ENTITY_WITH_NAME);
		}
	}
}
