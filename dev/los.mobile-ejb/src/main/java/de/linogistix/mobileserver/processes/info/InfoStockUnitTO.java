/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobileserver.processes.info;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.mywms.model.StockUnit;

import de.linogistix.los.location.model.LOSUnitLoad;

/**
 * @author krane
 *
 */
public class InfoStockUnitTO implements Serializable{

	private static final long serialVersionUID = 1L;

	private String clientName = "";
	private String clientNumber = "";
	private String locationName = "";
	private String unitLoadLabel = "";
	private InfoItemDataTO itemData = new InfoItemDataTO();
	private BigDecimal amount = BigDecimal.ZERO;
	private BigDecimal amountRes = BigDecimal.ZERO;
	private String unit = "";
	private String lotName = "";
	private String serialNumber = "";
	private List<InfoOrderTO> orderList = new ArrayList<InfoOrderTO>();
	private List<InfoOrderTO> pickList = new ArrayList<InfoOrderTO>();
	
	public InfoStockUnitTO( ) {
	}
	
	public InfoStockUnitTO( StockUnit su ) {
		if( su == null ) {
			return;
		}
		this.clientName = su.getClient().getName();
		this.clientNumber = su.getClient().getNumber();
		this.locationName = ((LOSUnitLoad)su.getUnitLoad()).getStorageLocation().getName();
		this.unitLoadLabel = su.getUnitLoad().getLabelId();
		this.itemData = new InfoItemDataTO( su.getItemData() );
		this.amount = su.getAmount().setScale(su.getItemData().getScale());
		if( su.getReservedAmount() != null && su.getReservedAmount().compareTo(BigDecimal.ZERO) != 0 ) {
			this.amountRes = su.getReservedAmount().setScale(su.getItemData().getScale());
		}
		this.unit = su.getItemData().getHandlingUnit().getUnitName();
		
		this.lotName = (su.getLot() == null ? "" : su.getLot().getName());
		this.serialNumber = su.getSerialNumber();
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
	public String getUnitLoadLabel() {
		return unitLoadLabel;
	}
	public void setUnitLoadLabel(String unitLoadLabel) {
		this.unitLoadLabel = unitLoadLabel;
	}
	public InfoItemDataTO getItemData() {
		return itemData;
	}
	public void setItemData(InfoItemDataTO itemData) {
		this.itemData = itemData;
	}
	public String getLotName() {
		return lotName;
	}
	public void setLotName(String lotName) {
		this.lotName = lotName;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
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

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getAmountRes() {
		return amountRes;
	}

	public void setAmountRes(BigDecimal amountRes) {
		this.amountRes = amountRes;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
	

}
