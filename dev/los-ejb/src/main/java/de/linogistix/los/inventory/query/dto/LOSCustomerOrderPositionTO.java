/*
 * Copyright (c) 2009-2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import java.math.BigDecimal;

import de.linogistix.los.query.BODTO;
import de.wms2.mywms.delivery.DeliveryOrderLine;

/**
 * @author krane
 *
 */
public class LOSCustomerOrderPositionTO extends BODTO<DeliveryOrderLine>{

	private static final long serialVersionUID = 1L;

	private String number;
	private String clientNumber;
	private String itemData;
	private String itemDataName;
	private String lot;
	private BigDecimal amount;
	private BigDecimal amountPicked;
	private int state;
	private String orderNumber;
	private String externalId;

	
	public LOSCustomerOrderPositionTO(DeliveryOrderLine pos){
		this(pos.getId(),pos.getVersion(), pos.getLineNumber(), pos.getItemData().getNumber(), pos.getItemData().getName(), pos.getItemData().getScale(), pos.getLot()==null?null:pos.getLot().getName(), pos.getAmount(), pos.getPickedAmount(), pos.getState(), pos.getDeliveryOrder().getOrderNumber(), pos.getClient().getNumber(), pos.getExternalId());
	}
	public LOSCustomerOrderPositionTO(Long id, int version, String name, String itemNumber, String itemName, int itemScale, 
			String lotName, BigDecimal amount, BigDecimal amountPicked, int positionState, String orderNumber, String clientNumber, String externalId){
		super(id,version, name);
		int scale = itemScale;
		this.itemData = itemNumber;
		this.itemDataName = itemName;
		this.lot = lotName;
		this.amount = amount.setScale(scale);
		this.amountPicked = amountPicked.setScale(scale);
		this.state = positionState;
		this.orderNumber = orderNumber;
		this.clientNumber = clientNumber;
		this.externalId = externalId;
		setClassName(DeliveryOrderLine.class.getName());
	}

	public LOSCustomerOrderPositionTO(Long id, int version, String name){
		super(id,version, name);
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

	public String getItemData() {
		return itemData;
	}

	public void setItemData(String itemData) {
		this.itemData = itemData;
	}

	public String getItemDataName() {
		return itemDataName;
	}

	public void setItemDataName(String itemDataName) {
		this.itemDataName = itemDataName;
	}

	public String getLot() {
		return lot;
	}

	public void setLot(String lot) {
		this.lot = lot;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getAmountPicked() {
		return amountPicked;
	}

	public void setAmountPicked(BigDecimal amountPicked) {
		this.amountPicked = amountPicked;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getExternalId() {
		return externalId;
	}
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	
	
	
	
	
}
