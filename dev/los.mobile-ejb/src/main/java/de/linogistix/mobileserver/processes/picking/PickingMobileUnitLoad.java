/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobileserver.processes.picking;

import java.io.Serializable;

import de.linogistix.los.model.State;
import de.wms2.mywms.picking.PickingOrder;
import de.wms2.mywms.picking.Packet;


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
	
	public PickingMobileUnitLoad(Packet unitLoad) {
		this.state = State.RAW;
		if( unitLoad.getState() >= State.STARTED ) {
			this.state = State.STARTED;
		}
		if( unitLoad.getState() >= State.PICKED ) {
			this.state = State.PICKED;
		}
		PickingOrder pickingOrder = unitLoad.getPickingOrder();
		this.label = unitLoad.getUnitLoad().getLabelId();
		this.index = unitLoad.getPositionIndex();
		this.pickingOrderId = (pickingOrder == null ? null : pickingOrder.getId());
		this.customerOrderNumber = (unitLoad.getDeliveryOrder() == null ? null
				: unitLoad.getDeliveryOrder().getOrderNumber());
		this.clientNumber = unitLoad.getClient().getNumber();
		this.pickingOrderNumber = (pickingOrder == null ? null : pickingOrder.getOrderNumber());
		// 01.08.2013, krane, avoid null pointer
		//		this.targetName = unitLoad.getPickingOrder() ==null?"":unitLoad.getPickingOrder().getDestination().getName();
		this.targetName = "";
		if (pickingOrder != null && pickingOrder.getDestination() != null) {
			this.targetName = pickingOrder.getDestination().getName();
		}
	}
	
	public String toString() {
		return "[label="+label+", index="+index+", order="+pickingOrderNumber+"]";
	}
}
