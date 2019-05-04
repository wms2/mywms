/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.businessservice;

import javax.ejb.Local;

/**
 *
 * @author krane
 */
@Local
public interface LOSHostReportGenerator {

    /**
     * Gets a document (e.g. pdf) via http get as byte Array.
     * 
     * @param urlStr
     * @return
     */
    public byte[] httpGet(String urlStr) ;
		
}
