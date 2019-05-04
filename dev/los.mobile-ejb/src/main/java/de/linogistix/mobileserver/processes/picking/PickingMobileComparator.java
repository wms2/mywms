/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobileserver.processes.picking;

import java.io.Serializable;
import java.util.Comparator;

import de.linogistix.los.inventory.model.LOSPickingPosition;

/**
 * @author krane
 *
 */
public class PickingMobileComparator implements Comparator<PickingMobilePos>, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	boolean preferCompleteUnitLoad = false;
	
	public PickingMobileComparator( boolean preferCompleteUnitLoad ) {
		this.preferCompleteUnitLoad = preferCompleteUnitLoad;
	}
	
	public int compare(PickingMobilePos o1, PickingMobilePos o2) {
		if (o1 == null || o2 == null) {
			return 0;
		}
		if (o1 == o2) {
			return 0;
		}
		if (o1.equals(o2)) {
			return 0;
		}
		
		if( preferCompleteUnitLoad ) {
			if( o1.pickingType == LOSPickingPosition.PICKING_TYPE_COMPLETE && o2.pickingType != LOSPickingPosition.PICKING_TYPE_COMPLETE ) {
				return 1;
			}
			if( o1.pickingType != LOSPickingPosition.PICKING_TYPE_COMPLETE && o2.pickingType == LOSPickingPosition.PICKING_TYPE_COMPLETE ) {
				return -1;
			}
		}
		
		int x = (o1.locationOrderIdx <= 0 || o2.locationOrderIdx <= 0) ? 0 : o1.locationOrderIdx == o2.locationOrderIdx ? 0 : o1.locationOrderIdx > o2.locationOrderIdx ? 1 : -1;
		if( x != 0 ) {
			return x;
		}

		x = (o1.locationName == null || o2.locationName == null) ? 0 : o1.locationName.compareTo(o2.locationName);
		if( x != 0 ) {
			return x;
		}
		
		x = (o1.amount == null || o2.amount == null) ? 0 : o1.amount.compareTo(o2.amount);
		if( x != 0 ) {
			return -1*x;
		}

		x = (o1.unitLoadLabel == null || o2.unitLoadLabel == null) ? 0 : o1.unitLoadLabel.compareTo(o2.unitLoadLabel);
		if( x != 0 ) {
			return x;
		}
		
		return 0;
	
	}

}
