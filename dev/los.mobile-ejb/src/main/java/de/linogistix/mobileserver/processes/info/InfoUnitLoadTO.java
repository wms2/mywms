/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobileserver.processes.info;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.linogistix.los.location.model.LOSUnitLoad;

/**
 * @author krane
 *
 */
public class InfoUnitLoadTO implements Serializable{

	private static final long serialVersionUID = 1L;

	private String clientName = "";
	private String clientNumber = "";
	private String locationName = "";
	private String label = "";
	private String type = "";
	
	public List<InfoStockUnitTO> stockUnitList = new ArrayList<InfoStockUnitTO>();
	public List<InfoOrderTO> orderList = new ArrayList<InfoOrderTO>();
	public List<InfoOrderTO> pickList = new ArrayList<InfoOrderTO>();
	
	public InfoUnitLoadTO() {
	}
	
	public InfoUnitLoadTO( LOSUnitLoad ul ) {
		if( ul == null ) {
			return;
		}
		this.clientName = ul.getClient().getName();
		this.clientNumber = ul.getClient().getNumber();
		this.locationName = ul.getStorageLocation().getName();
		this.label = ul.getLabelId();
		this.type = ul.getType().getName();

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

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<InfoStockUnitTO> getStockUnitList() {
		return stockUnitList;
	}

	public void setStockUnitList(List<InfoStockUnitTO> stockUnitList) {
		this.stockUnitList = stockUnitList;
	}

	public List<InfoOrderTO> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<InfoOrderTO> orderList) {
		this.orderList = orderList;
	}
	
	public List<InfoOrderTO> getPickList() {
		return pickList;
	}

	public void setPickList(List<InfoOrderTO> pickList) {
		this.pickList = pickList;
	}
	
	
}
