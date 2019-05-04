/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.stocktaking.service;

import java.util.List;

import javax.ejb.Local;

import org.mywms.model.Client;
import org.mywms.service.BasicService;

import de.linogistix.los.stocktaking.model.LOSStocktakingRecord;

/**
 *
 * @author krane
 */
@Local
public interface LOSStocktakingRecordService extends BasicService<LOSStocktakingRecord>{


    public List<LOSStocktakingRecord> getListByUnitLoadLabel(Client c, String unitLoadLabel);
    
 
}
