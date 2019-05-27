/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import de.linogistix.los.query.BODTO;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.product.ItemDataNumber;

/**
 * @author krane
 *
 */
public class ItemDataNumberTO extends BODTO<ItemData> {

	private static final long serialVersionUID = 1L;

	private int index;
	private String clientNumber;
	private String itemDataNumber;
	private String itemDataName;

	public ItemDataNumberTO( ItemDataNumber idat ) {
		super(idat.getId(), idat.getVersion(), idat.getNumber());
		this.index = idat.getIndex();
		this.clientNumber = idat.getClient().getNumber();
		this.itemDataName = idat.getItemData().getName();
		this.itemDataNumber = idat.getItemData().getNumber();
	}
	
	public ItemDataNumberTO(Long id, int version, String number) {
		super(id, version, number);
	}
	
	public ItemDataNumberTO(Long id, int version, String number, 
			int index, String clientNumber, String itemDataNumber, String itemDataName) {
		super(id, version, number);
		this.index = index;
		this.clientNumber = clientNumber;
		this.itemDataName = itemDataName;
		this.itemDataNumber = itemDataNumber;
	}

	public String getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
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

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}



}
