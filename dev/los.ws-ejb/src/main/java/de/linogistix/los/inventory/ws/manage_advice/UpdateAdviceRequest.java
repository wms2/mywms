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
@XmlType(name = "UpdateAdviceRequest",
		namespace = "http://advice.management.los.linogistix.de")
public class UpdateAdviceRequest {
	
	private String adviceNumber;
	
	private String externalAdviceNumber;
	
	private String externalId;
	
	private String clientNumber;
	
	private String itemNumber;
	
	private String lotNumber = null;
	
	private Date expectedDelivery;
	
	private BigDecimal notifiedAmount;
	
	private String additionalContent;
	
	public String getAdviceNumber() {
		return adviceNumber;
	}

	public void setAdviceNumber(String adviceNumber) {
		this.adviceNumber = (adviceNumber == null ? null : adviceNumber.trim());
	}

	public String getExternalAdviceNumber() {
		return externalAdviceNumber;
	}

	public void setExternalAdviceNumber(String externalAdviceNumber) {
		this.externalAdviceNumber = (externalAdviceNumber == null ? null : externalAdviceNumber.trim());
	}

	// added after release 1.2.0
	@XmlElement(required=false)
	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = (externalId == null ? null : externalId.trim());
	}
	
	@XmlElement(required=true)
	public String getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	@XmlElement(required=true)
	public String getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}

	public String getLotNumber() {
		return lotNumber;
	}

	public void setLotNumber(String lotNumber) {
		this.lotNumber = lotNumber;
	}

	public Date getExpectedDelivery() {
		return expectedDelivery;
	}

	public void setExpectedDelivery(Date expectedDelivery) {
		this.expectedDelivery = expectedDelivery;
	}

	@XmlElement(required=true)
	public BigDecimal getNotifiedAmount() {
		return notifiedAmount;
	}

	public void setNotifiedAmount(BigDecimal notifiedAmount) {
		this.notifiedAmount = notifiedAmount;
	}

	public String getAdditionalContent() {
		return additionalContent;
	}

	public void setAdditionalContent(String additionalContent) {
		this.additionalContent = additionalContent;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append("UpdateAdviceRequest: adviceNumber=");
		sb.append(adviceNumber);
	
		sb.append(", externalAdviceNumber=");
		sb.append(externalAdviceNumber);
	
		sb.append(", externalId=");
		sb.append(externalId);
	
		sb.append(", clientNumber=");
		sb.append(clientNumber);
	
		sb.append(", itemNumber=");
		sb.append(itemNumber);
	
		sb.append(", lotNumber=");
		sb.append(lotNumber);
	
		sb.append(", expectedDelivery=");
		sb.append(expectedDelivery);
	
		sb.append(", notifiedAmount=");
		sb.append(notifiedAmount);
	
		sb.append(", additionalContent=");
		sb.append(additionalContent);

		return sb.toString();
	}
}
