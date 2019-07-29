/*
 * Copyright (c) 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobileserver.processes.picking;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import de.linogistix.los.model.State;
import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.picking.PickingOrder;
import de.wms2.mywms.picking.PickingOrderLine;

/**
 * @author krane
 *
 */
public class PickingMobileOrder implements Serializable {
	private static final long serialVersionUID = 1L;

	public Long id;
	public String clientNumber;
	public String pickingOrderNumber;
	public String customerOrderNumber;
	public String customerOrderExternalNumber;
	public int numPos = 0;
	public int state = State.RAW;
	public String targetName;
	public String created;
	public String strategy;
	public String customerNumber;
	public String customerName;
	
	public PickingMobileOrder() {
	}
	public PickingMobileOrder( PickingOrder order ) {
		String customerOrder = null;
		boolean orderValid = true;
		String externalNumber = null;
		
		this.id = order.getId();
		this.pickingOrderNumber = order.getOrderNumber();
		this.numPos = 0;
		this.clientNumber = order.getClient().getNumber();
		if( order.getDestination() != null ) {
			this.targetName = order.getDestination().getName();
		}
		for( PickingOrderLine pos : order.getLines() ) {
			if( pos == null ) {
				continue;
			}
			
			if( pos.getDeliveryOrderLine() != null ) {
				if( customerOrder == null ) {
					customerOrder = pos.getDeliveryOrderLine().getDeliveryOrder().getOrderNumber();
					externalNumber = pos.getDeliveryOrderLine().getDeliveryOrder().getExternalNumber();
				}
				else if( !customerOrder.equals(pos.getDeliveryOrderLine().getDeliveryOrder().getOrderNumber())){
					orderValid = false;
				}
			}
			
			if( (pos.getState() < State.PICKED) && (pos.getState() >= State.ASSIGNED) ) {
				numPos++;
			}
		}
		if( orderValid ) {
			this.customerOrderNumber = customerOrder;
			this.customerOrderExternalNumber = externalNumber;
		}
		
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        created = formatter.format(order.getCreated());
    
        strategy = order.getOrderStrategy().getName();
	}
	
	public void setCustomerOrder( DeliveryOrder order ) {
		this.customerNumber = order.getCustomerNumber();
		this.customerName = (order.getAddress() == null ? null : order.getAddress().toUniqueString());
	}
	
	@Override
	public boolean equals(Object obj) {
		if( obj == null ) {
			return false;
		}
		if( !(obj instanceof PickingMobileOrder) ) {
			return false;
		}
		if( id != (((PickingMobileOrder)obj).id) ) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	public String toString() {
		return "[id="+id+", order="+pickingOrderNumber+"]";
	}
}
