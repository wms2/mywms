/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.businessservice;

import java.util.Comparator;

import org.apache.log4j.Logger;

import de.wms2.mywms.location.StorageLocation;

/**
 * Compares LOSRackLocation by name so that A1-2-1-1 is smaller than A1-10-1-1.
 * 
 * @author trautm
 * 
 */
public class LOSRackLocationComparatorByName implements
		Comparator<StorageLocation> {

	private static final Logger log = Logger.getLogger(LOSRackLocationComparatorByName.class);
	
	public int compare(StorageLocation o1, StorageLocation o2) {
		String[] n1 = o1.getName().split("[_-]");
		String[] n2 = o2.getName().split("[_-]");
		int max = n1.length > n2.length ? n1.length : n2.length;
		for (int i = 0; i < max; i++) {
			try {
				if (i == 0) {
					// extract name prefix
					String pref1 = n1[i].replaceAll("[0-9]", "");
					String pref2 = n2[i].replaceAll("[0-9]", "");
					if (!pref1.equals(pref2)) {
						return pref1.compareTo(pref2);
					} else {
						n1[i] = n1[i].replaceAll("[^0-9]", "");
						n2[i] = n2[i].replaceAll("[^0-9]", "");
					}
				}
				
				Integer i1;
				try{
					i1 = Integer.parseInt(n1[i]);
				} catch (NumberFormatException ex){
					log.warn("name convention exception: " + o1.getName());
					return o1.getName().compareTo(o2.getName());
				}
				
				Integer i2;
				try{
					i2 = Integer.parseInt(n2[i]);
				} catch (NumberFormatException ex){
					log.warn("name convention exception: " + o2.getName());
					return o1.getName().compareTo(o2.getName());
				}
				
				int ret = i1.compareTo(i2);
				if (ret != 0) {
					return ret;
				} else {
					continue;
				}

			} catch (ArrayIndexOutOfBoundsException ex) {
				return new Integer(n1.length).compareTo(new Integer(n2.length));
			}

		}
		return 0;
	}

}
