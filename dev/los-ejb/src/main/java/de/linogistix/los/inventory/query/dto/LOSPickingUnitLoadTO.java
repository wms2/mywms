/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import de.linogistix.los.inventory.model.LOSPickingPosition;
import de.linogistix.los.inventory.model.LOSPickingUnitLoad;
import de.linogistix.los.query.BODTO;

/**
 * @author krane
 *
 */
public class LOSPickingUnitLoadTO extends BODTO<LOSPickingPosition>{

	private static final long serialVersionUID = 1L;

	private String label;
	private String clientNumber;
	private int state;
	private String locationName;
	private String pickingOrderNumber;
	private String customerOrderNumber;

	public LOSPickingUnitLoadTO( LOSPickingUnitLoad pickingUnitLoad ) {
		super(pickingUnitLoad.getId(), pickingUnitLoad.getVersion(), pickingUnitLoad.getUnitLoad().getLabelId());

		this.clientNumber = pickingUnitLoad.getClient().getNumber();
		this.state = pickingUnitLoad.getState();
		this.label = pickingUnitLoad.getUnitLoad().getLabelId();
		this.locationName = pickingUnitLoad.getUnitLoad().getStorageLocation().getName();
		this.pickingOrderNumber = pickingUnitLoad.getPickingOrder().getNumber();
		this.customerOrderNumber = pickingUnitLoad.getCustomerOrderNumber();
	}
	
	public LOSPickingUnitLoadTO(
			Long id, 
			int version,
			String clientNumber, 
			int state, String unitLoadLabel, String locationName, String pickingOrderNumber, String customerOrderNumber ){
		super(id, version, unitLoadLabel);
		
		this.clientNumber = clientNumber;
		this.state = state;
		this.label = unitLoadLabel;
		this.locationName = locationName;
		this.pickingOrderNumber = pickingOrderNumber;
		this.customerOrderNumber = customerOrderNumber;
	}

	public LOSPickingUnitLoadTO(
			Long id, 
			int version,
			String name)
	{
		super(id, version, name);
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getPickingOrderNumber() {
		return pickingOrderNumber;
	}

	public void setPickingOrderNumber(String pickingOrderNumber) {
		this.pickingOrderNumber = pickingOrderNumber;
	}

	public String getCustomerOrderNumber() {
		return customerOrderNumber;
	}

	public void setCustomerOrderNumber(String customerOrderNumber) {
		this.customerOrderNumber = customerOrderNumber;
	}



	
}
