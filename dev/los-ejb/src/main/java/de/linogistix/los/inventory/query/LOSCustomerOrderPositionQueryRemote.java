/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query;


import javax.ejb.Remote;

import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSCustomerOrderPosition;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;

/** 
*
* @author krane
*/
@Remote
public interface LOSCustomerOrderPositionQueryRemote extends BusinessObjectQueryRemote<LOSCustomerOrderPosition>{ 
	public LOSResultList<BODTO<LOSCustomerOrderPosition>> autoCompletionByOrderRequest(
			String typed, 
			BODTO<LOSCustomerOrder> order, 
			QueryDetail detail); 
}
