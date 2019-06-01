/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.stocktaking.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.mywms.model.BasicEntity;

@Entity
@Table(name = "los_stocktakingorder")
public class LOSStocktakingOrder extends BasicEntity {
	private static final long serialVersionUID = 1L;

	@ManyToOne(optional=true, fetch=FetchType.LAZY)
	private LOSStockTaking stockTaking;
	
	private String locationName;
	
	private String unitLoadLabel;
	
	private String areaName;
	
	private String operator;
	
	private Date countingDate;
	
	private LOSStocktakingState state;

    @OneToMany(mappedBy="stocktakingOrder")
	private List<LOSStocktakingRecord> records = new ArrayList<LOSStocktakingRecord>();

	public LOSStockTaking getStockTaking() {
		return stockTaking;
	}

	public void setStockTaking(LOSStockTaking stockTaking) {
		this.stockTaking = stockTaking;
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

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Date getCountingDate() {
		return countingDate;
	}

	public void setCountingDate(Date countingDate) {
		this.countingDate = countingDate;
	}

	public LOSStocktakingState getState() {
		return state;
	}

	public void setState(LOSStocktakingState state) {
		this.state = state;
	}

	public List<LOSStocktakingRecord> getRecords() {
		return records;
	}

	public void setRecords(List<LOSStocktakingRecord> records) {
		this.records = records;
	}

	public void addRecord(LOSStocktakingRecord record) {
		this.records.add(record);
	}
	
}
