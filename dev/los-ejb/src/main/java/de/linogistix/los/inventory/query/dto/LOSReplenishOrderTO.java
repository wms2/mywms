/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import java.math.BigDecimal;

import de.linogistix.los.inventory.model.LOSReplenishOrder;
import de.linogistix.los.query.BODTO;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.product.ItemData;

/**
 * @author krane
 *
 */
public class LOSReplenishOrderTO extends BODTO<LOSReplenishOrder>{

	private static final long serialVersionUID = 1L;

	private String number;
	private String clientNumber;
	private int state;
	private int prio = 50;
	private String sourceLocationName;
	private String destinationLocationName;
    private String itemDataNumber;
    private String itemDataName;
    private BigDecimal amount;
	
	public LOSReplenishOrderTO(Long id, int version, String name){
		super(id, version, name);
	}
	

	public LOSReplenishOrderTO(Long id, int version, String number, int state, int prio, ItemData item, StockUnit stock, String destinationName, BigDecimal amount, String clientNumber){
		super(id, version, number);
		this.number = number;
		this.clientNumber = clientNumber;
		this.sourceLocationName = stock.getUnitLoad().getStorageLocation().getName();
		this.destinationLocationName = destinationName;
		this.state = state;
		this.prio = prio;
		this.itemDataNumber = item.getNumber();
		this.itemDataName = item.getName();
		this.amount = (amount == null ? null : amount.setScale(item.getScale()));
	}


	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}

	public String getClientNumber() {
		return clientNumber;
	}
	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}

	public int getPrio() {
		return prio;
	}
	public void setPrio(int prio) {
		this.prio = prio;
	}

	public String getItemDataNumber() {
		return itemDataNumber;
	}
	public void setItemDataNumber(String itemDataNumber) {
		this.itemDataNumber = itemDataNumber;
	}

	public String getItemDataName() {
		return itemDataName;
	}
	public void setItemDataName(String itemDataName) {
		this.itemDataName = itemDataName;
	}

	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getSourceLocationName() {
		return sourceLocationName;
	}
	public void setSourceLocationName(String sourceLocationName) {
		this.sourceLocationName = sourceLocationName;
	}

	public String getDestinationLocationName() {
		return destinationLocationName;
	}
	public void setDestinationLocationName(String destinationLocationName) {
		this.destinationLocationName = destinationLocationName;
	}

	
	
}
