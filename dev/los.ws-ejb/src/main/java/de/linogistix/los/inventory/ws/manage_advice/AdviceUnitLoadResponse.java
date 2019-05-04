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
@XmlType(name = "AdviceUnitLoadResponse",
		namespace = "http://advice.management.los.linogistix.de",
		 propOrder = {"adviceNumber", "externalNumber", "clientNumber", "relatedAdvice", "labelId", "unitLoadType",
					  "returns", "reasonForReturn", "stockUnitList"})
public class AdviceUnitLoadResponse {

	private String adviceNumber;
	
	private String externalNumber;
	
	private String clientNumber = "";
		
	private String relatedAdvice  = "";
	
	private String labelId = "";
	
	private String unitLoadType = "";
	
	private boolean returns = false;
	
	private String reasonForReturn = "";
	
	private List<StockUnitElement> stockUnitList = new ArrayList<StockUnitElement>();

	public String getAdviceNumber() {
		return adviceNumber;
	}

	public void setAdviceNumber(String adviceNumber) {
		this.adviceNumber = adviceNumber;
	}

	public String getExternalNumber() {
		return externalNumber;
	}

	public void setExternalNumber(String externalNumber) {
		this.externalNumber = externalNumber;
	}

	@XmlElement(required=true)
	public String getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	public String getRelatedAdvice() {
		return relatedAdvice;
	}

	public void setRelatedAdvice(String relatedAdvice) {
		this.relatedAdvice = relatedAdvice;
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
