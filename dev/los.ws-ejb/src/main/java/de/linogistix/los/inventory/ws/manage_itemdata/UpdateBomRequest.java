/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.ws.manage_itemdata;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "UpdateBomRequest", 
		 namespace = "http://itemdata.management.los.linogistix.de")
public class UpdateBomRequest {
	private String clientNumber;
	private String parentNumber;
	private String childNumber;
	private BigDecimal amount = BigDecimal.ZERO;
	private boolean pickable = true;

	
	
	@XmlElement(required=true)
	public String getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	@XmlElement(required=true)
    public String getParentNumber() {
		return parentNumber;
	}

	public void setParentNumber(String parentNumber) {
		this.parentNumber = parentNumber;
	}

	@XmlElement(required=true)
	public String getChildNumber() {
		return childNumber;
	}

	public void setChildNumber(String childNumber) {
		this.childNumber = childNumber;
	}

	@XmlElement(defaultValue="0.0")
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@XmlElement(defaultValue="true")
	public boolean isPickable() {
		return pickable;
	}

	public void setPickable(boolean pickable) {
		this.pickable = pickable;
	}

	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("UpdateBomRequest: clientNumber=");
		sb.append(clientNumber);
		
		sb.append(", parentNumber=");
		sb.append(parentNumber);
	    
		sb.append(", childNumber=");
		sb.append(childNumber);
	    
		sb.append(", amount=");
		sb.append(amount);
	    
		sb.append(", pickable=");
		sb.append(pickable);
		
	    return sb.toString();
	}
}
