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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.mywms.globals.SerialNoRecordType;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "UpdateItemDataRequest", 
		 namespace = "http://itemdata.management.los.linogistix.de",
		 propOrder = {"clientNumber", "name", "number", "description", 
					  "safetyStock", "lotMandatory", "adviceMandatory",
					  "serialNoRecordType", "handlingUnit", "scale", "eanCodes"})
public class UpdateItemDataRequest {

	private String clientNumber = "";
	
	private String name = "";
    
    private String number = null;
    
    private String description;
    
    private int safetyStock = 0;
    
    private boolean lotMandatory = false;
    
    private boolean adviceMandatory = false;
    
    private SerialNoRecordType serialNoRecordType = SerialNoRecordType.NO_RECORD;
    
    private String handlingUnit;
    
    private int scale = 0;
    
    private String[] eanCodes;
    
    
    @XmlElement(required=true)
    public String getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	@XmlElement(required=true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(required=true)
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = (number == null ? null : number.trim());
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlElement(defaultValue="0")
	public int getSafetyStock() {
		return safetyStock;
	}

	public void setSafetyStock(int safetyStock) {
		this.safetyStock = safetyStock;
	}

	public boolean isLotMandatory() {
		return lotMandatory;
	}

	public void setLotMandatory(boolean lotMandatory) {
		this.lotMandatory = lotMandatory;
	}

	public boolean isAdviceMandatory() {
		return adviceMandatory;
	}

	public void setAdviceMandatory(boolean adviceMandatory) {
		this.adviceMandatory = adviceMandatory;
	}

	@XmlElement(defaultValue="NO_RECORD")
	public SerialNoRecordType getSerialNoRecordType() {
		return serialNoRecordType;
	}

	public void setSerialNoRecordType(SerialNoRecordType serialNoRecordType) {
		this.serialNoRecordType = serialNoRecordType;
	}

	@XmlElement(required=true)
	public String getHandlingUnit() {
		return handlingUnit;
	}

	public void setHandlingUnit(String handlingUnit) {
		this.handlingUnit = handlingUnit;
	}

	@XmlElement(defaultValue="0")
	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	// added after release 1.2.0
	@XmlElement(required=false)
	public String[] getEanCodes() {
		return eanCodes;
	}

	public void setEanCodes(String[] eanCodes) {
		this.eanCodes = eanCodes;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("UpdateItemDataRequest: clientNumber=");
		sb.append(clientNumber);
		
		sb.append(", name=");
		sb.append(name);
	    
		sb.append(", number=");
		sb.append(number);
	    
		sb.append(", description=");
		sb.append(description);
	    
		sb.append(", safetyStock=");
		sb.append(safetyStock);
	    
		sb.append(", lotMandatory=");
		sb.append(lotMandatory);
		
		sb.append(", adviceMandatory=");
		sb.append(adviceMandatory);
	    
		sb.append(", serialNoRecordType=");
		sb.append(serialNoRecordType);
	    
		sb.append(", handlingUnit=");
		sb.append(handlingUnit);
	    
		sb.append(", scale=");
		sb.append(scale);
		
		if( eanCodes != null ) {
			sb.append(", eanCodes={");
			for( String s : eanCodes ) {
				sb.append(s);
				sb.append(",");
			}
			sb.append("}");
		}
		
	    return sb.toString();

	}
}
