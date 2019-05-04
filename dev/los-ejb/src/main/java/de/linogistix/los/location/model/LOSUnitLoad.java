/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mywms.model.StockUnit;
import org.mywms.model.UnitLoad;

/**
 *
 * @author Jordan
 */
@Entity
@NamedQueries({
	@NamedQuery(name="LOSUnitLoad.queryByLabel", query="FROM LOSUnitLoad ul WHERE ul.labelId=:label"),
	@NamedQuery(name="LOSUnitLoad.queryByLocation", query="FROM LOSUnitLoad ul WHERE ul.storageLocation=:location"),
	@NamedQuery(name="LOSUnitLoad.existsByLocation", query="SELECT ul.id FROM LOSUnitLoad ul WHERE ul.storageLocation=:location"),
	@NamedQuery(name="LOSUnitLoad.queryByCarrierId", query="FROM LOSUnitLoad ul WHERE ul.carrierUnitLoadId = :carrierId"),
	@NamedQuery(name="LOSUnitLoad.countByCarrierId", query="SELECT count(*) FROM LOSUnitLoad ul WHERE ul.carrierUnitLoadId = :carrierId")
})
public class LOSUnitLoad extends UnitLoad{
	
	private static final long serialVersionUID = 1L;

    private LOSStorageLocation storageLocation;
    
    private List<StockUnit> stockUnitList = new ArrayList<StockUnit>();

    private LOSUnitLoadPackageType packageType = LOSUnitLoadPackageType.OF_SAME_LOT_CONSOLIDATE;
    
    private Date stockTakingDate;
    
    private BigDecimal weightCalculated;
    private BigDecimal weightMeasure;
    private BigDecimal weight;
    
    private boolean opened = false;
    
    private Long carrierUnitLoadId;
    private boolean isCarrier = false;
    
    private LOSUnitLoad carrierUnitLoad;
//    private List<LOSUnitLoad> unitLoadList = new ArrayList<LOSUnitLoad>();
    
	@ManyToOne(optional=false)
    public LOSStorageLocation getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(LOSStorageLocation storageLocation) {
        this.storageLocation = storageLocation;
    }

    @OneToMany(mappedBy="unitLoad")
    public List<StockUnit> getStockUnitList() {
		return stockUnitList;
	}

	public void setStockUnitList(List<StockUnit> stockUnitList) {
		this.stockUnitList = stockUnitList;
	}

	public void setPackageType(LOSUnitLoadPackageType packageType) {
		this.packageType = packageType;
	}

	@Enumerated(EnumType.STRING)
	public LOSUnitLoadPackageType getPackageType() {
		return packageType;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getStockTakingDate() {
		return stockTakingDate;
	}

	public void setStockTakingDate(Date stockTakingDate) {
		this.stockTakingDate = stockTakingDate;
	}

	@Column(precision=16, scale=3)
	public BigDecimal getWeightCalculated() {
		return weightCalculated;
	}
	public void setWeightCalculated(BigDecimal weightCalculated) {
		this.weightCalculated = weightCalculated;
	}

	@Column(precision=16, scale=3)
	public BigDecimal getWeightMeasure() {
		return weightMeasure;
	}
	public void setWeightMeasure(BigDecimal weightMeasure) {
		this.weightMeasure = weightMeasure;
	}

	@Column(precision=16, scale=3)
	public BigDecimal getWeight() {
		return weight;
	}
	// This value is calculated in automatically.
	@SuppressWarnings("unused")
	private void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	@Column(nullable=false)
	public boolean isOpened() {
		return opened;
	}
	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	public Long getCarrierUnitLoadId() {
		return carrierUnitLoadId;
	}
	public void setCarrierUnitLoadId(Long carrierUnitLoadId) {
		this.carrierUnitLoadId = carrierUnitLoadId;
	}

	@Column(nullable=false)
	public boolean isCarrier() {
		return isCarrier;
	}
	public void setCarrier(boolean isCarrier) {
		this.isCarrier = isCarrier;
	}

	@ManyToOne(optional=true)
	public LOSUnitLoad getCarrierUnitLoad() {
		return carrierUnitLoad;
	}
	public void setCarrierUnitLoad(LOSUnitLoad carrierUnitLoad) {
		this.carrierUnitLoad = carrierUnitLoad;
	}
//
//	@OneToMany(mappedBy="carrierUnitLoad")
//	public List<LOSUnitLoad> getUnitLoadList() {
//		return unitLoadList;
//	}
//	public void setUnitLoadList(List<LOSUnitLoad> unitLoadList) {
//		this.unitLoadList = unitLoadList;
//	}
	
	@Override
	public String toShortString() {
    	return super.toShortString() + "[labelId=" + getLabelId() + "][locationName=" + (storageLocation == null ? "" : storageLocation.getName()) + "]";
	}
	
	@PrePersist
	@PreUpdate
	public void sanityCheck() {
		if( weightMeasure != null && weightMeasure.compareTo(BigDecimal.ZERO)>0 ) {
			weight = weightMeasure;
		}
		else if( weightCalculated != null && weightCalculated.compareTo(BigDecimal.ZERO)>0 ) {
			weight = weightCalculated;
		}
	}
}
