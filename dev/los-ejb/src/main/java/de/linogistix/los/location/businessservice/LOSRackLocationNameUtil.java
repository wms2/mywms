/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.businessservice;

import org.apache.log4j.Logger;

public class LOSRackLocationNameUtil {
	
	private final static Logger log = Logger.getLogger(LOSRackLocationNameUtil.class);
	
	String slName;
	
	public LOSRackLocationNameUtil(String slName){
		this.slName = slName;
	}
	
	public String extractSlName(){
		int last = slName.lastIndexOf("-");
		return slName.substring(0,last);

		
	}
	
	public int extractIndex() {
		int last = slName.lastIndexOf("-");
		int index ;
		String indexStr = slName.substring(last+1,slName.length());
		
		try{
			index = Integer.parseInt(indexStr);
		} catch (Exception ex){
			log.warn(ex.getMessage());
			index = -1;
		}
		
		return index;
	}
	
	
	
}
