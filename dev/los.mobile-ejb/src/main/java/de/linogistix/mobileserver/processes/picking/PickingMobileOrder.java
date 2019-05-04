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

import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSPickingOrder;
import de.linogistix.los.inventory.model.LOSPickingPosition;
import de.linogistix.los.model.State;

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
	public PickingMobileOrder( LOSPickingOrder order ) {
		String customerOrder = null;
		boolean orderValid = true;
		String externalNumber = null;
		
		this.id = order.getId();
		this.pickingOrderNumber = order.getNumber();
		this.numPos = 0;
		this.clientNumber = order.getClient().getNumber();
		if( order.getDestination() != null ) {
			this.targetName = order.getDestination().getName();
		}
		for( LOSPickingPosition pos : order.getPositions() ) {
			if( pos == null ) {
				continue;
			}
			
			if( pos.getCustomerOrderPosition() != null ) {
				if( customerOrder == null ) {
					customerOrder = pos.getCustomerOrderPosition().getOrder().getNumber();
					externalNumber = pos.getCustomerOrderPosition().getOrder().getExternalNumber();
				}
				else if( !customerOrder.equals(pos.getCustomerOrderPosition().getOrder().getNumber())){
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
    
        strategy = order.getStrategy().getName();
	}
	
	public void setCustomerOrder( LOSCustomerOrder order ) {
		this.customerNumber = order.getCustomerNumber();
		this.customerName = order.getCustomerName();
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
