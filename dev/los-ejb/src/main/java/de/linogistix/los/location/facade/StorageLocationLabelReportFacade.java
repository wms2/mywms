/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.facade;

import java.util.List;

import javax.ejb.Remote;

import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.report.StorageLocationLabelTO;
import de.linogistix.los.query.exception.BusinessObjectQueryException;
import de.linogistix.los.report.ReportException;

/**
 * faacde for creating labels naming storage locations.
 * @author trautm
 *
 */
@Remote
public interface StorageLocationLabelReportFacade {

	/**
	 * Returns a pdf document as byte array, containing the labels for 
	 * the given  StorageLocationLabelTO.
	 * 
	 * @param labels a list of StorageLocationLabelTO, one element per location
	 * @return labels as pdf document as byte array
	 * @throws LOSLocationException
	 * @throws BusinessObjectQueryException
	 * @throws ReportException
	 */
	byte[] generateStorageLocationLabels(List<StorageLocationLabelTO> labels) throws LOSLocationException, BusinessObjectQueryException, ReportException;
	
}
