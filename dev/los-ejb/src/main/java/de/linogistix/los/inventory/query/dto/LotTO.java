/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import java.util.Date;

import org.mywms.model.Lot;

import de.linogistix.los.query.BODTO;

public class LotTO extends BODTO<Lot>{
	
	public static final long serialVersionUID = 1L;
	
	public String itemData;
	
	public String itemDataName;
	
	public String clientNumber;

	public int lock;
	
	public Date useNotBefore;
	
	public Date bestBeforeEnd;
	
	
	public LotTO(Lot lot){
		this(lot.getId(), lot.getVersion(), lot.getName(), lot.getItemData().getNumber(), lot.getItemData().getName(), lot.getLock(), lot.getUseNotBefore(), lot.getBestBeforeEnd(), lot.getClient().getNumber());
	}
	
	public LotTO(Long id, int version, String name, String idat, String idatName, int lock, Date useNotBefore, Date bestBeforeEnd){
		this(id, version, name, idat, idatName, lock, useNotBefore, bestBeforeEnd, "");
	}
	public LotTO(Long id, int version, String name, String idat, String idatName, int lock, Date useNotBefore, Date bestBeforeEnd, String clientNumber){
		super(id, version, name);
		this.itemData = idat;
		this.itemDataName = idatName;
		this.lock = lock;
		this.useNotBefore = useNotBefore;
		this.bestBeforeEnd = bestBeforeEnd	;
		this.clientNumber = clientNumber;
		setClassName(Lot.class.getName());
	}


	public String getItemData() {
		return itemData;
	}


	public void setItemData(String itemData) {
		this.itemData = itemData;
	}


	public int getLock() {
		return lock;
	}


	public void setLock(int lock) {
		this.lock = lock;
	}


	public Date getUseNotBefore() {
		return useNotBefore;
	}


	public void setUseNotBefore(Date useNotBefore) {
		this.useNotBefore = useNotBefore;
	}


	public Date getBestBeforeEnd() {
		return bestBeforeEnd;
	}


	public void setBestBeforeEnd(Date bestBeforeEnd) {
		this.bestBeforeEnd = bestBeforeEnd;
	}


	public String getClientNumber() {
		return clientNumber;
	}


	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	public String getItemDataName() {
		return itemDataName;
	}

	public void setItemDataName(String itemDataName) {
		this.itemDataName = itemDataName;
	}

}
