/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.ws.manage_advice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "DeleteAdviceRequest",
		namespace = "http://advice.management.los.linogistix.de")
public class DeleteAdviceRequest {

	private String clientNumber;
	
	private String adviceNumber;

	private String externalAdviceNumber;
	
	private String externalId;


	@XmlElement(required=true)
	public String getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	public String getAdviceNumber() {
		return adviceNumber;
	}

	public void setAdviceNumber(String adviceNumber) {
		this.adviceNumber = adviceNumber;
	}

	public String getExternalAdviceNumber() {
		return externalAdviceNumber;
	}

	public void setExternalAdviceNumber(String externalAdviceNumber) {
		this.externalAdviceNumber = externalAdviceNumber;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append("DeleteAdviceRequest: adviceNumber=");
		sb.append(adviceNumber);
	
		sb.append(", externalAdviceNumber=");
		sb.append(externalAdviceNumber);
	
		sb.append(", externalId=");
		sb.append(externalId);
	
		sb.append(", clientNumber=");
		sb.append(clientNumber);
	
		return sb.toString();
	}

}
