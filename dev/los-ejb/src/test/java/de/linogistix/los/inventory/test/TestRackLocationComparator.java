/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.test;

import de.linogistix.los.location.businessservice.LOSRackLocationComparatorByName;
import de.linogistix.los.location.model.LOSStorageLocation;

public class TestRackLocationComparator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		LOSStorageLocation rl1 = new LOSStorageLocation();
		rl1.setName("R4-23-24");
		
		LOSStorageLocation rl2 = new LOSStorageLocation();
		rl2.setName("R1-43-44");
		
		
		LOSRackLocationComparatorByName comp = new LOSRackLocationComparatorByName();
		
		System.out.println("--- "+comp.compare(rl1, rl2));

	}

}
