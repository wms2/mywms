/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.ws.manage_advice;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "AdviceUnitLoadRequest",
		namespace = "http://advice.management.los.linogistix.de",
		 propOrder = {"clientNumber", "externalAdviceNumber", "relatedAdviceNumber", 
					  "labelId", "unitLoadType", "returns", "reasonForReturn", "stockUnitList"})
public class AdviceUnitLoadRequest {

	private String clientNumber = "";
	
	private String externalAdviceNumber = "";
	
	private String relatedAdviceNumber  = "";
	
	private String labelId = "";
	
	private String unitLoadType = "";
	
	private boolean returns = false;
	
	private String reasonForReturn = "";
	
	private List<StockUnitElement> stockUnitList = new ArrayList<StockUnitElement>();

	@XmlElement(required=true)
	public String getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	public String getExternalAdviceNumber() {
		return externalAdviceNumber;
	}

	public void setExternalAdviceNumber(String externalAdviceNumber) {
		this.externalAdviceNumber = externalAdviceNumber;
	}

	public String getRelatedAdviceNumber() {
		return relatedAdviceNumber;
	}

	public void setRelatedAdviceNumber(String relatedAdvice) {
		this.relatedAdviceNumber = relatedAdvice;
	}

	@XmlElement(required=true)
	public String getLabelId() {
		return labelId;
	}

	public void setLabelId(String labelId) {
		this.labelId = labelId;
	}

	public String getUnitLoadType() {
		return unitLoadType;
	}

	public void setUnitLoadType(String unitLoadType) {
		this.unitLoadType = unitLoadType;
	}

	public boolean isReturns() {
		return returns;
	}

	public void setReturns(boolean returns) {
		this.returns = returns;
	}

	public String getReasonForReturn() {
		return reasonForReturn;
	}

	public void setReasonForReturn(String reasonForReturn) {
		this.reasonForReturn = reasonForReturn;
	}

	public List<StockUnitElement> getStockUnitList() {
		return stockUnitList;
	}

	public void setStockUnitList(List<StockUnitElement> stockUnitList) {
		this.stockUnitList = stockUnitList;
	}
}
