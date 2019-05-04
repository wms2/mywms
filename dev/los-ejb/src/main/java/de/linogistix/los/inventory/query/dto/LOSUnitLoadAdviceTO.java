/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import de.linogistix.los.inventory.model.LOSAdviceType;
import de.linogistix.los.inventory.model.LOSUnitLoadAdvice;
import de.linogistix.los.query.BODTO;

public class LOSUnitLoadAdviceTO extends BODTO<LOSUnitLoadAdvice>{
	
	private static final long serialVersionUID = 1L;
	
	private LOSAdviceType adviceType;
	private String ulLabelId;
	
	
	public LOSUnitLoadAdviceTO(Long id, int version, String number){
		super(id, version, number);
	}
	
	public LOSUnitLoadAdviceTO(Long id, int version, String number, String ulLabelId, LOSAdviceType adviceType){
		super(id, version, number);
		this.adviceType = adviceType;
		this.ulLabelId = ulLabelId;
	}


	public String getUlLabelId() {
		return ulLabelId;
	}


	public void setUlLabelId(String ulLabelId) {
		this.ulLabelId = ulLabelId;
	}


	public LOSAdviceType getAdviceType() {
		return adviceType;
	}


	public void setAdviceType(LOSAdviceType adviceType) {
		this.adviceType = adviceType;
	}



}
