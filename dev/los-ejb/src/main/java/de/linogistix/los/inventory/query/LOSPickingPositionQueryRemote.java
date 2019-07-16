/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query;


import java.util.List;

import javax.ejb.Remote;

import org.mywms.model.Client;

import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.wms2.mywms.picking.PickingOrderLine;


/**
 * @author krane
 *
 */
@Remote
public interface LOSPickingPositionQueryRemote extends BusinessObjectQueryRemote<PickingOrderLine>{ 

	public List<PickingOrderLine> queryAll( Client client );

}
