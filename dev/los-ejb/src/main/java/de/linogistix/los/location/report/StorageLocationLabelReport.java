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
import de.linogistix.los.location.model.LOSRack;
import de.linogistix.los.location.model.StorageLocationLabel;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.exception.BusinessObjectQueryException;
import de.linogistix.los.report.ReportException;

/**
 * Generates labels for storage locations. Minimum information is the name of the location and a barcode.
 * @author trautm
 */
@Remote
public interface StorageLocationLabelReport {
    
    StorageLocationLabel generateStorageLocationLabels(int offset) throws LOSLocationException, BusinessObjectQueryException, ReportException;
    
    StorageLocationLabel generateRackLabels(List<BODTO<LOSRack>> list) throws LOSLocationException, BusinessObjectQueryException, ReportException;
    
    StorageLocationLabel generateStorageLocationLabels(List<StorageLocationLabelTO> labels) throws LOSLocationException, BusinessObjectQueryException, ReportException;
}
