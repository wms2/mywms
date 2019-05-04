/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.stocktaking.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.mywms.model.Client;
import org.mywms.service.BasicServiceBean;

import de.linogistix.los.stocktaking.model.LOSStocktakingRecord;


/**
 * 
 * @author krane
 */
@Stateless
public class LOSStocktakingRecordServiceBean extends BasicServiceBean<LOSStocktakingRecord> implements LOSStocktakingRecordService {
	
    
    @SuppressWarnings("unchecked")
	public List<LOSStocktakingRecord> getListByUnitLoadLabel(Client c, String unitLoadLabel) {
		Query query = manager.createQuery("SELECT sr FROM "
				+ LOSStocktakingRecord.class.getSimpleName() + " sr "
				+ " WHERE sr.client=:client"
				+ " AND sr.unitLoadLabel = :label"
		);

		query.setParameter("client", c);
		query.setParameter("label", unitLoadLabel);


		return query.getResultList();
    }

}
