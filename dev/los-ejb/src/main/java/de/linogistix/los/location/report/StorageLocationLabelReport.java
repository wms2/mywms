/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.report;

import java.util.List;

import javax.ejb.Remote;

import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.query.exception.BusinessObjectQueryException;
import de.linogistix.los.report.ReportException;
import de.wms2.mywms.document.Document;

/**
 * Generates labels for storage locations. Minimum information is the name of the location and a barcode.
 * @author trautm
 */
@Remote
public interface StorageLocationLabelReport {
    
    Document generateStorageLocationLabels(int offset) throws LOSLocationException, BusinessObjectQueryException, ReportException;
    
    Document generateRackLabels(List<String> list) throws LOSLocationException, BusinessObjectQueryException, ReportException;
    
    Document generateStorageLocationLabels(List<StorageLocationLabelTO> labels) throws LOSLocationException, BusinessObjectQueryException, ReportException;
}
