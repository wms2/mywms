/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import java.math.BigDecimal;

import de.linogistix.los.inventory.model.LOSPickingPosition;
import de.linogistix.los.query.BODTO;
import de.wms2.mywms.product.ItemData;

/**
 * @author krane
 *
 */
public class LOSPickingPositionTO extends BODTO<LOSPickingPosition>{

	private static final long serialVersionUID = 1L;

	private String clientNumber;
	private int state;
	private BigDecimal amount = BigDecimal.ZERO;
	private BigDecimal amountPicked = BigDecimal.ZERO;
	private String pickFromLocationName;
	private String pickFromUnitLoadLabel;
	private String itemDataNumber;
	private String pickingOrderNumber;
	private String itemDataName;
	private int pickingType = 0;
	
	
	public LOSPickingPositionTO( LOSPickingPosition pos ) {
		this(pos.getId(), pos.getVersion(), pos.getId(), pos.getState(), pos.getPickingType(),
				pos.getAmount(), pos.getAmountPicked(),
				pos.getPickFromUnitLoadLabel(), pos.getPickFromLocationName(), 
				pos.getPickingOrderNumber(), pos.getItemData(), pos.getClient().getNumber());
	}

	public LOSPickingPositionTO(
			Long id, 
			int version,
			Long name,
			int state, int pickingType, 
			BigDecimal amount, BigDecimal amountPicked,
			String pickFromUnitLoadLabel,
			String pickFromLocationName,
			String pickingOrderNumber,
			ItemData itemData,
			String clientNumber ) {
		super(id, version, name);
		
		this.clientNumber = clientNumber;
		this.state = state;
		this.amount = amount.setScale(itemData.getScale());
		this.amountPicked = amountPicked.setScale(itemData.getScale());
		this.pickFromLocationName = pickFromLocationName;
		this.pickFromUnitLoadLabel = pickFromUnitLoadLabel;
		this.itemDataNumber = itemData.getNumber();
		this.itemDataName = itemData.getName();
		this.pickingType = pickingType;
		this.pickingOrderNumber = pickingOrderNumber;
		setClassName(LOSPickingPosition.class.getName());
	}

	public LOSPickingPositionTO(
			Long id, 
			int version,
			String name)
	{
		super(id, version, name);
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

	public String getPickFromLocationName() {
		return pickFromLocationName;
	}

	public void setPickFromLocationName(String pickFromLocationName) {
		this.pickFromLocationName = pickFromLocationName;
	}

	public String getPickFromUnitLoadLabel() {
		return pickFromUnitLoadLabel;
	}

	public void setPickFromUnitLoadLabel(String pickFromUnitLoadLabel) {
		this.pickFromUnitLoadLabel = pickFromUnitLoadLabel;
	}

	public String getItemDataNumber() {
		return itemDataNumber;
	}

	public void setItemDataNumber(String itemDataNumber) {
		this.itemDataNumber = itemDataNumber;
	}

	public String getPickingOrderNumber() {
		return pickingOrderNumber;
	}

	public void setPickingOrderNumber(String pickingOrderNumber) {
		this.pickingOrderNumber = pickingOrderNumber;
	}

	public String getItemDataName() {
		return itemDataName;
	}

	public void setItemDataName(String itemDataName) {
		this.itemDataName = itemDataName;
	}

	public int getPickingType() {
		return pickingType;
	}

	public void setPickingType(int pickingType) {
		this.pickingType = pickingType;
	}


	
	
	
}
