/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobileserver.processes.info;

import java.io.Serializable;
import java.util.List;

import org.mywms.globals.SerialNoRecordType;
import org.mywms.model.ItemData;
import org.mywms.model.StockUnit;

import de.linogistix.los.location.model.LOSFixedLocationAssignment;

/**
 * @author krane
 *
 */
public class InfoItemDataTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	
	private String clientName = "";
	private String clientNumber = "";
	private String number = "";
	private String name = "";
	private String desc = "";
	private String fixedLocationName = "";
	private String unitName;
	private String zoneName;
	private int numStock = -1;
	
	public boolean isLotMandatory = false;
	public SerialNoRecordType serialNoRecordType = SerialNoRecordType.NO_RECORD;
	
	public InfoItemDataTO() {
	}
	
	public InfoItemDataTO( ItemData idat ) {
		this(idat, null, null);
	}
	public InfoItemDataTO( ItemData idat, List<LOSFixedLocationAssignment> fixList, List<StockUnit> stockList ) {
		if( idat == null ) {
			return;
		}
		
		this.clientName = idat.getClient().getName();
		this.clientNumber = idat.getClient().getNumber();
		this.number = idat.getNumber();
		this.name = idat.getName();
		this.desc = idat.getDescription();
		this.isLotMandatory = idat.isLotMandatory();
		this.unitName = idat.getHandlingUnit().getUnitName();
		this.serialNoRecordType = idat.getSerialNoRecordType();
		this.zoneName = idat.getZone() == null ? null : idat.getZone().getName();
		this.numStock = stockList == null ? -1: stockList.size(); 

		if( fixList != null ) {
			for( LOSFixedLocationAssignment ass : fixList ) {
				if( fixedLocationName.length() > 0 ) {
					fixedLocationName += ", ";
				}
				fixedLocationName += ass.getAssignedLocation().getName();
			}
		}

	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getFixedLocationName() {
		return fixedLocationName;
	}

	public void setFixedLocationName(String fixedLocationName) {
		this.fixedLocationName = fixedLocationName;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public boolean isLotMandatory() {
		return isLotMandatory;
	}

	public void setLotMandatory(boolean isLotMandatory) {
		this.isLotMandatory = isLotMandatory;
	}

	public SerialNoRecordType getSerialNoRecordType() {
		return serialNoRecordType;
	}

	public void setSerialNoRecordType(SerialNoRecordType serialNoRecordType) {
		this.serialNoRecordType = serialNoRecordType;
	}

	public String getZoneName() {
		return zoneName;
	}
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public int getNumStock() {
		return numStock;
	}
	public void setNumStock(int numStock) {
		this.numStock = numStock;
	}
	
	
	
}
