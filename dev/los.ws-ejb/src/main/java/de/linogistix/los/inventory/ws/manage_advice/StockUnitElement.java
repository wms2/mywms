/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.ws.manage_advice;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "StockUnitElement",
		namespace = "http://advice.management.los.linogistix.de",
		 propOrder = {"itemDataNumber", "notifiedAmount", "lotNumber", 
					  "bestBeforeEnd", "useNotBefore"})
public class StockUnitElement {

	private String itemDataNumber;
	
	private BigDecimal notifiedAmount;
	
	private String lotNumber;
	
	private Date bestBeforeEnd;
	
	private Date useNotBefore;

	@XmlElement(required=true)
	public String getItemDataNumber() {
		return itemDataNumber;
	}

	public void setItemDataNumber(String itemDataNumber) {
		this.itemDataNumber = itemDataNumber;
	}

	@XmlElement(required=true)
	public BigDecimal getNotifiedAmount() {
		return notifiedAmount;
	}

	public void setNotifiedAmount(BigDecimal notifiedAmount) {
		this.notifiedAmount = notifiedAmount;
	}

	public String getLotNumber() {
		return lotNumber;
	}

	public void setLotNumber(String lotNumber) {
		this.lotNumber = lotNumber;
	}

	public Date getBestBeforeEnd() {
		return bestBeforeEnd;
	}

	public void setBestBeforeEnd(Date bestBeforeEnd) {
		this.bestBeforeEnd = bestBeforeEnd;
	}

	public Date getUseNotBefore() {
		return useNotBefore;
	}

	public void setUseNotBefore(Date useNotBefore) {
		this.useNotBefore = useNotBefore;
	}
}
