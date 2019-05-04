/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobileserver.processes.picking;

import java.io.Serializable;

import de.linogistix.los.inventory.model.LOSPickingUnitLoad;
import de.linogistix.los.model.State;


/**
 * @author krane
 *
 */
public class PickingMobileUnitLoad implements Serializable {
	private static final long serialVersionUID = 1L;

	public String label;
	public int index;
	public long pickingOrderId;
	public int state = State.RAW;
	
	public String customerOrderNumber;
	public String pickingOrderNumber;
	public String clientNumber;
	public String targetName;
	
	public PickingMobileUnitLoad(PickingMobileOrder order, String label) {
		this.state = State.RAW;
		this.label = label;
		this.pickingOrderId = order.id;
		this.customerOrderNumber = order.customerOrderNumber;
		this.clientNumber = order.clientNumber;
		this.pickingOrderNumber = order.pickingOrderNumber;
		this.targetName = order.targetName;
	}
	
	public PickingMobileUnitLoad(LOSPickingUnitLoad unitLoad) {
		this.state = State.RAW;
		if( unitLoad.getState() >= State.STARTED ) {
			this.state = State.STARTED;
		}
		if( unitLoad.getState() >= State.PICKED ) {
			this.state = State.PICKED;
		}

		this.label = unitLoad.getUnitLoad().getLabelId();
		this.index = unitLoad.getPositionIndex();
		this.pickingOrderId = unitLoad.getPickingOrder().getId();
		this.customerOrderNumber = unitLoad.getCustomerOrderNumber();
		this.clientNumber = unitLoad.getClient().getNumber();
		this.pickingOrderNumber = unitLoad.getPickingOrder().getNumber();
		// 01.08.2013, krane, avoid null pointer
		//		this.targetName = unitLoad.getPickingOrder() ==null?"":unitLoad.getPickingOrder().getDestination().getName();
		this.targetName = "";
		if( unitLoad.getPickingOrder()!=null && unitLoad.getPickingOrder().getDestination()!=null ) {
			this.targetName = unitLoad.getPickingOrder().getDestination().getName();
		}
	}
	
	public String toString() {
		return "[label="+label+", index="+index+", order="+pickingOrderNumber+"]";
	}
}
