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

import org.mywms.model.StockUnit;

import de.linogistix.los.inventory.model.LOSCustomerOrderPosition;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.query.BODTO;

public class CreatePickRequestPositionTO implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
	 * ---- PickRequest data
	 */
	public String pickRequestNumber;
	
	public BODTO<LOSStorageLocation> targetPlace;
	
	/**
	 * ---- PickRequestPosition data
	 */
	public BODTO<StockUnit> stock;
	
	public BODTO<LOSCustomerOrderPosition> orderPosition;
//	public long customerOrderPositionId;
//	public long customerOrderPositionNumber;
	
	public BigDecimal amountToPick;
	
}
