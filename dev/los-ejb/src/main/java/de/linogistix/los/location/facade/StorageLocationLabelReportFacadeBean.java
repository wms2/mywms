/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.model.StorageLocationLabel;
import de.linogistix.los.location.report.StorageLocationLabelReport;
import de.linogistix.los.location.report.StorageLocationLabelTO;
import de.linogistix.los.query.exception.BusinessObjectQueryException;
import de.linogistix.los.report.ReportException;

@Stateless
public class StorageLocationLabelReportFacadeBean implements
		StorageLocationLabelReportFacade {

	@EJB
	StorageLocationLabelReport report;

	public byte[] generateStorageLocationLabels(
			List<StorageLocationLabelTO> labels) throws LOSLocationException,
			BusinessObjectQueryException, ReportException {
		StorageLocationLabel label = report.generateStorageLocationLabels(labels);
		if (label != null)
			return label.getDocument();
		else{
			return new byte[0];
		}
	}

}
