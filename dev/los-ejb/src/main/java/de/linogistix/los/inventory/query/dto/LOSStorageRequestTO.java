/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import java.math.BigDecimal;

import de.linogistix.los.query.BODTO;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.transport.TransportOrder;

public class LOSStorageRequestTO extends BODTO<TransportOrder> {

	private static final long serialVersionUID = 1L;

	private String unitLoadLabel;
	private int state;
	private int orderType;
	private String clientNumber;
	private String sourceLocationName;
	private String destinationLocationName;
	private String itemDataNumber;
	private String itemDataName;
	private BigDecimal amount;

	public LOSStorageRequestTO() {
	}

	public LOSStorageRequestTO(Long id, int version, String name) {
		super(id, version, name);
	}

	public LOSStorageRequestTO(Long id, int version, String number, int state, int orderType, String unitLoadLabel,
			ItemData item, String sourceLocationName, String destinationName, BigDecimal amount, String clientNumber) {
		super(id, version, number);
		this.clientNumber = clientNumber;
		this.sourceLocationName = sourceLocationName;
		this.destinationLocationName = destinationName;
		this.state = state;
		this.orderType = orderType;
		this.unitLoadLabel = unitLoadLabel;
		if (item != null) {
			this.itemDataNumber = item.getNumber();
			this.itemDataName = item.getName();
		}
		if (amount != null && item != null) {
			this.amount = amount.setScale(item.getScale());
		}
	}

	public String getUnitLoadLabel() {
		return unitLoadLabel;
	}

	public void setUnitLoadLabel(String unitLoadLabel) {
		this.unitLoadLabel = unitLoadLabel;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
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

	public int getOrderType() {
		return orderType;
	}

	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}

}
