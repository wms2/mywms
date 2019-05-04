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

import de.linogistix.los.inventory.service.ClientItemNumberTO;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "ClientNumberElement", 
		 namespace = "http://itemdata.management.los.linogistix.de",
		 propOrder = {"clientNumber", "itemNumber"})
public class ClientNumberElement extends ClientItemNumberTO{

	private static final long serialVersionUID = 1L;
	
	public ClientNumberElement(String clientNumber, String itemNumber) {
		super(clientNumber, itemNumber);
	}
}
