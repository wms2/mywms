/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.ws.manage_itemdata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "DeleteItemDataRequest",
		 namespace = "http://itemdata.management.los.linogistix.de",
		 propOrder = { "clientNumber", "itemNumber" })
public class DeleteItemDataRequest {

	private String clientNumber;
	
	private String itemNumber;

	public String getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	public String getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("DeleteItemDataRequest: clientNumber=");
		sb.append(clientNumber);
		
		sb.append(", number=");
		sb.append(itemNumber);
	    
	    return sb.toString();

	}

}
