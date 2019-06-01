/*
 * Copyright (c) 2010 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-3PL
 */
package de.linogistix.los.inventory.query.dto;

import de.linogistix.los.query.BODTO;
import de.wms2.mywms.strategy.StorageStrategy;


/**
 * @author krane
 *
 */
public class LOSStorageStrategyTO extends BODTO<StorageStrategy> {

	private static final long serialVersionUID = 1L;

	private String name;
	
	public LOSStorageStrategyTO( long id, int version, Long id2) {
		super(id, version, id2);
	}
	
	public LOSStorageStrategyTO( StorageStrategy rec ) {
		super(rec.getId(), rec.getVersion(), rec.getName());
		this.name = rec.getName();
	}
	
	public LOSStorageStrategyTO( long id, int version, String name ) {
		super(id, version, name);
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}


}
