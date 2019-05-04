/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import org.mywms.model.ItemData;

import de.linogistix.los.query.BODTO;

public class ItemDataTO extends BODTO<ItemData> {

	private static final long serialVersionUID = 1L;

	private String nameX;
	private String number;
	private String clientNumber;
	private String itemUnitName;
	private int scale;

	public ItemDataTO( ItemData idat ) {
		super(idat.getId(), idat.getVersion(), idat.getNumber());
		this.nameX = idat.getName();
		this.number = idat.getNumber();
		this.clientNumber = idat.getClient().getNumber();
		this.itemUnitName = idat.getHandlingUnit().getUnitName();
		this.scale = idat.getScale();
	}
	
	public ItemDataTO(Long id, int version, String number) {
		super(id, version, number);
	}
	
	public ItemDataTO(Long id, int version, String number, 
			String name, String clientNumber, String itemUnitName) {
		super(id, version, number);
		this.nameX = name;
		this.number = number;
		this.clientNumber = clientNumber;
		this.itemUnitName = itemUnitName;
	}


	public String getNameX() {
		return nameX;
	}


	public void setNameX(String nameX) {
		this.nameX = nameX;
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


	public String getItemUnitName() {
		return itemUnitName;
	}


	public void setItemUnitName(String itemUnitName) {
		this.itemUnitName = itemUnitName;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}


}
