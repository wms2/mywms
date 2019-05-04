/*
 * Copyright (c) 2012-2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobileserver.processes.picking;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import org.mywms.globals.SerialNoRecordType;
import org.mywms.model.StockUnit;

import de.linogistix.los.inventory.model.LOSPickingPosition;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;

/**
 * @author krane
 *
 */
public class PickingMobilePos implements Serializable {
	private static final long serialVersionUID = 1L;

	
	public long id;
	public String clientNumber;
	public String pickingOrderNumber;
	public String customerOrderNumber;
	public String itemNo;
	public String itemName;
	public List<String> eanList;
	public String locationName;
	public String locationCode;
	public int locationOrderIdx = 0;
	public String unitLoadLabel;
	public BigDecimal amount;
	public String unitName;
	public int state;
	public long pickingOrderId;
	public int pickingType;
	public boolean serialRequired = false;
	public boolean checkLocationEmpty = false;
	public boolean fixedLocation = false;
	public BigDecimal amountStock = null;
	public String locationTypeName = null;
	public long locationTypeId = 0;

	public void init( LOSPickingPosition pos ) {
		init(pos,null,null);
	}
	
	public void init( LOSPickingPosition pos, List<String> eanList, Boolean fixedLocation ) {
		
		this.id = pos.getId();
		this.clientNumber = pos.getClient().getNumber();
		this.pickingOrderNumber = (pos.getPickingOrder() == null ? null : pos.getPickingOrder().getNumber());
		this.customerOrderNumber = pos.getCustomerOrderPosition() == null ? "" : pos.getCustomerOrderPosition().getOrder().getNumber();
		this.itemNo = pos.getItemData().getNumber();
		this.itemName = pos.getItemData().getName();
		this.locationName = pos.getPickFromLocationName();
		this.unitLoadLabel = pos.getPickFromUnitLoadLabel();
		this.amount = pos.getAmount();
		this.unitName = pos.getItemData().getHandlingUnit().getUnitName();
		this.state = pos.getState();
		this.pickingOrderId = pos.getPickingOrder().getId();
		this.pickingType = pos.getPickingType();
		this.serialRequired = (pos.getItemData().getSerialNoRecordType() == SerialNoRecordType.GOODS_OUT_RECORD);
		if( eanList != null ) {
			this.eanList = eanList;
		}
		if( fixedLocation != null ) {
			this.fixedLocation = fixedLocation;
		}
		
		StockUnit su = pos.getPickFromStockUnit();
		if( su != null ) {
			this.amountStock = su.getAmount();
			LOSUnitLoad ul = (LOSUnitLoad)su.getUnitLoad();
			LOSStorageLocation loc = ul.getStorageLocation();
			if( loc.getUnitLoads().size()<=1 ) {
				if( ul.getStockUnitList().size() == 1 ) {
					if( su.getAmount().compareTo(amountStock)<=0 ) {
						this.checkLocationEmpty = true;
					}
				}
			}
			locationCode = loc.getScanCode();
			locationName = loc.getName();
			locationTypeName = loc.getType().getName();
			locationTypeId = loc.getType().getId();
			
			this.locationOrderIdx = loc.getOrderIndex();
		}

	}
	
	public void init( PickingMobilePos pos ) {
		this.id = pos.id;
		this.clientNumber = pos.clientNumber;
		this.pickingOrderNumber = pos.pickingOrderNumber;
		this.customerOrderNumber = pos.customerOrderNumber;
		this.itemNo = pos.itemNo;
		this.itemName = pos.itemName;
		this.locationName = pos.locationName;
		this.locationCode = pos.locationCode;
		this.locationOrderIdx = pos.locationOrderIdx;
		this.locationTypeName = pos.locationTypeName; 
		this.locationTypeId = pos.locationTypeId;
		this.unitLoadLabel = pos.unitLoadLabel;
		this.amount = pos.amount;
		this.unitName = pos.unitName;
		this.state = pos.state;
		this.pickingOrderId = pos. pickingOrderId;
		this.pickingType = pos.pickingType;
		
		this.checkLocationEmpty = pos.checkLocationEmpty; 
		this.amountStock = pos.amountStock;
		this.serialRequired = pos.serialRequired;
	}
	
	public boolean hasEan() {
		return ( eanList!=null && eanList.size()>0 );
	}
	
	public String toString() {
		return "[id="+id+", location="+locationName+", item="+itemNo+", amount="+amount+", order="+pickingOrderNumber+"]";
	}
}
