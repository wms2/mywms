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

import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.model.LOSStorageLocation;

/**
 * @author krane
 *
 */
public class InfoLocationTO implements Serializable{

	private static final long serialVersionUID = 1L;


	private String clientName = "";
	private String clientNumber = "";
	private String name = "";
	private String type;
	private String fixedItemDataNumber = "";
	private int numUnitLoads = 0;
	private InfoUnitLoadTO unitLoad = new InfoUnitLoadTO();

	public InfoLocationTO() {
	}
	public InfoLocationTO( LOSStorageLocation loc, List<LOSFixedLocationAssignment> fixList ) {
		if( loc == null ) {
			return;
		}
		
		this.clientName = loc.getClient().getName();
		this.clientNumber = loc.getClient().getNumber();
		this.name = loc.getName();
		this.type = loc.getType().getName();
		
		this.numUnitLoads = loc.getUnitLoads().size();
		if( numUnitLoads == 1 ) {
			this.unitLoad = new InfoUnitLoadTO( loc.getUnitLoads().get(0) );
		}
		
		if( fixList != null ) {
			for( LOSFixedLocationAssignment ass : fixList ) {
				if( fixedItemDataNumber.length() > 0 ) {
					fixedItemDataNumber += ", ";
				}
				fixedItemDataNumber += ass.getItemData().getNumber();
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFixedItemDataNumber() {
		return fixedItemDataNumber;
	}
	public void setFixedItemDataNumber(String fixedItemDataNumber) {
		this.fixedItemDataNumber = fixedItemDataNumber;
	}
	public int getNumUnitLoads() {
		return numUnitLoads;
	}
	public void setNumUnitLoads(int numUnitLoads) {
		this.numUnitLoads = numUnitLoads;
	}
	public InfoUnitLoadTO getUnitLoad() {
		return unitLoad;
	}
	public void setUnitLoad(InfoUnitLoadTO unitLoad) {
		this.unitLoad = unitLoad;
	}
	
	
}
