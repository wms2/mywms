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

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.service.BasicServiceBean;

import de.linogistix.los.util.DateHelper;
import de.wms2.mywms.inventory.Lot;


/**
 * 
 * @author trautm
 */
@Stateless
public class LOSLotServiceBean extends BasicServiceBean<Lot> implements
		LOSLotService {
	private static final Logger log = Logger.getLogger(LOSLotServiceBean.class);
	

	@SuppressWarnings("unchecked")
	public List<Lot> getTooOld() {
		
		List<Lot> ret;
		
		GregorianCalendar today = new GregorianCalendar();
		today.set(GregorianCalendar.HOUR_OF_DAY, 0);
		today.set(GregorianCalendar.MINUTE,0);
		today.set(GregorianCalendar.SECOND,0);
		
		String s = "SELECT lot FROM "
		+ Lot.class.getSimpleName() + " lot "
		+ " WHERE lot.bestBeforeEnd < :today ";
		
		Query query = manager.createQuery(s);

		query.setParameter("today", today.getTime());
		ret  = (query.getResultList());
		return ret;
	
	}

	@SuppressWarnings("unchecked")
	public List<Lot> getNotToUse() {
		
		List<Lot> ret;
		
		GregorianCalendar today = new GregorianCalendar();
		
		today.set(GregorianCalendar.HOUR_OF_DAY, 23);
		today.set(GregorianCalendar.MINUTE,59);
		today.set(GregorianCalendar.SECOND,59);
		String s = "SELECT lot FROM "
		+ Lot.class.getSimpleName() + " lot "
		+ " WHERE lot.useNotBefore > :today ";
		
		Query query = manager.createQuery(s);

		query.setParameter("today", today.getTime());

		ret = (query.getResultList());
		return ret;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Lot> getToUseFromNow() {
		
		List<Lot> ret;
		
		GregorianCalendar today = new GregorianCalendar();
		
		today.set(GregorianCalendar.HOUR_OF_DAY, 0);
		today.set(GregorianCalendar.MINUTE,0);
		today.set(GregorianCalendar.SECOND,0);
		String s = "SELECT lot FROM "
		+ Lot.class.getSimpleName() + " lot "
		+ " WHERE lot.useNotBefore <= :today "
		+ " AND lot.lock=:lock ";
		Query query = manager.createQuery(s);

		query.setParameter("today", today.getTime());

		query.setParameter("lock", LotLockState.LOT_TOO_YOUNG.getLock());
		ret = (query.getResultList());
		return ret;
	}

	public void processLotDates(Lot lot, Date bestBeforeEnd, Date useNotBefore) {

		lot = manager.merge(lot);

		Date today = DateHelper.endOfDay(new Date());

		if (useNotBefore != null && (lot.getUseNotBefore() == null || lot.getUseNotBefore().compareTo(useNotBefore) != 0)) {
			lot.setUseNotBefore(useNotBefore);
		}
		if (useNotBefore != null && lot.getUseNotBefore() != null && lot.getUseNotBefore().after(today)) {
			log.warn("Set Lot to LotLockState.LOT_TOO_YOUNG: " + lot.toShortString());
			lot.setLock(LotLockState.LOT_TOO_YOUNG.getLock());
		}

		today = DateHelper.beginningOfDay(new Date());
		if (bestBeforeEnd != null && (lot.getBestBeforeEnd() == null || lot.getBestBeforeEnd().compareTo(bestBeforeEnd) != 0)) {
			lot.setBestBeforeEnd(bestBeforeEnd);
		}

		if (bestBeforeEnd != null && lot.getBestBeforeEnd() != null && lot.getBestBeforeEnd().before(today)) {
			log.warn("Set Lot to LotLockState.LOT_EXPIRED: " + lot.toShortString());
			lot.setLock(LotLockState.LOT_EXPIRED.getLock());
		}

		manager.flush();
	}
}
