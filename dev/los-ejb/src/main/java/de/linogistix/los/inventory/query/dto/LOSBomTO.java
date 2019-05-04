/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import java.math.BigDecimal;

import org.mywms.model.ItemData;

import de.linogistix.los.inventory.model.LOSBom;
import de.linogistix.los.query.BODTO;

public class LOSBomTO extends BODTO<ItemData> {

	private static final long serialVersionUID = 1L;

	private String clientNumber = "";
	private String parentNumber = "";
	private String parentName = "";
	private String childNumber = "";
	private String childName = "";
	private BigDecimal amount = BigDecimal.ONE;
	private int index = 0;
	private boolean pickable = true;

	public LOSBomTO( LOSBom bom ) {
		super(bom.getId(), bom.getVersion(), bom.getId());
		this.clientNumber = bom.getParent().getClient().getNumber();
		this.parentNumber = bom.getParent().getNumber();
		this.parentName = bom.getParent().getName();
		this.childNumber = bom.getChild().getNumber();
		this.childName = bom.getChild().getName();
		this.amount = bom.getAmount();
		this.index = bom.getIndex();
		this.pickable = bom.isPickable();
	}
	
	public LOSBomTO(Long id, int version) {
		super(id, version, id);
	}
	
	public LOSBomTO(Long id, int version, Long name,
			String clientNumber, String parentNumber, String parentName, String childNumber, String childName, BigDecimal amount, int index, boolean pickable, int scale) {
		super(id, version, id);
		this.clientNumber = clientNumber;
		this.parentNumber = parentNumber;
		this.parentName = parentName;
		this.childNumber = childNumber;
		this.childName = childName;
		try {
			this.amount = amount.setScale(scale);
		}
		catch( Throwable t ) {
			this.amount = amount;
		}
		this.index = index;
		this.pickable = pickable;
	}

	public String getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	public String getParentNumber() {
		return parentNumber;
	}

	public void setParentNumber(String parentNumber) {
		this.parentNumber = parentNumber;
	}

	public String getChildNumber() {
		return childNumber;
	}

	public void setChildNumber(String childNumber) {
		this.childNumber = childNumber;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isPickable() {
		return pickable;
	}

	public void setPickable(boolean pickable) {
		this.pickable = pickable;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getChildName() {
		return childName;
	}

	public void setChildName(String childName) {
		this.childName = childName;
	}


}
