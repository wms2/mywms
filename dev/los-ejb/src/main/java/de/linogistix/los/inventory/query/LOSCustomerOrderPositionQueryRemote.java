/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query;


import javax.ejb.Remote;

import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.delivery.DeliveryOrderLine;

/** 
*
* @author krane
*/
@Remote
public interface LOSCustomerOrderPositionQueryRemote extends BusinessObjectQueryRemote<DeliveryOrderLine>{ 
	public LOSResultList<BODTO<DeliveryOrderLine>> autoCompletionByOrderRequest(
			String typed, 
			BODTO<DeliveryOrder> order, 
			QueryDetail detail); 
}
