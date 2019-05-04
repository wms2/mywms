/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query;


import javax.ejb.Remote;

import de.linogistix.los.inventory.model.LOSBom;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.query.exception.BusinessObjectQueryException;

/**
 * @author krane
 *
 */
@Remote
public interface LOSBomQueryRemote extends BusinessObjectQueryRemote<LOSBom>{ 
	public LOSResultList<BODTO<LOSBom>> queryByDefault( String master, String child, QueryDetail detail ) throws BusinessObjectNotFoundException, BusinessObjectQueryException;

}
