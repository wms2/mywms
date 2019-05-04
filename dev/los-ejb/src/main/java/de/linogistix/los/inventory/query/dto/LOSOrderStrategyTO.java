/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */package de.linogistix.los.inventory.query.dto;

import de.linogistix.los.inventory.model.LOSOrderStrategy;
import de.linogistix.los.query.BODTO;

/**
 * @author krane
 *
 */
public class LOSOrderStrategyTO extends BODTO<LOSOrderStrategy>{

	private static final long serialVersionUID = 1L;

	private String name;
	private String clientNumber;

	
	public LOSOrderStrategyTO(Long id, int version, String name, String clientNumber){
		super(id,version, name);
		this.name = name;
		this.clientNumber = clientNumber;
	}

	public LOSOrderStrategyTO(Long id, int version, String name){
		super(id,version, name);
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getClientNumber() {
		return clientNumber;
	}
	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	
	
}
