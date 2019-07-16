/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.pick.facade;

import java.io.Serializable;
import java.math.BigDecimal;

import de.linogistix.los.query.BODTO;
import de.wms2.mywms.delivery.DeliveryOrderLine;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.location.StorageLocation;

public class CreatePickRequestPositionTO implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
	 * ---- PickRequest data
	 */
	public String pickRequestNumber;
	
	public BODTO<StorageLocation> targetPlace;
	
	/**
	 * ---- PickRequestPosition data
	 */
	public BODTO<StockUnit> stock;
	
	public BODTO<DeliveryOrderLine> orderPosition;
//	public long customerOrderPositionId;
//	public long customerOrderPositionNumber;
	
	public BigDecimal amountToPick;
	
}
