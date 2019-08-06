/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.facade;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;

import de.linogistix.los.common.exception.LOSExceptionRB;
import de.wms2.mywms.document.Document;
import de.wms2.mywms.report.Report;
import de.wms2.mywms.report.ReportBusiness;

/**
 * @author krane
 *
 */
@Stateless
public class LOSJasperReportFacadeBean implements LOSJasperReportFacade  {
	Logger log = Logger.getLogger(LOSJasperReportFacadeBean.class);

	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;
	@Inject
	private ReportBusiness reportBusiness;

	public void writeSourceDocument( long id, String document ) throws FacadeException {
		String logStr = "writeSourceDocument ";
		
		Report report = manager.find(Report.class, id);
		if( report == null ) {
			log.warn(logStr+"Cannot read client");
			throw new LOSExceptionRB("Cannot read client");
		}

//		log.debug(logStr+"report="+report.getName()+", source="+document);
		log.debug(logStr+"report="+report.getName());
		
		reportBusiness.saveSourceDocument(report, report.getName(), document.getBytes());
		report.setVersion(report.getVersion()+1);
	}
	
	public void compileReport( long id ) throws FacadeException {
		String logStr = "compileReport ";
		
		Report report = manager.find(Report.class, id);
		if (report == null) {
			log.warn(logStr+"Cannot read client");
			throw new LOSExceptionRB("Cannot read client");
		}
		log.debug(logStr+"report="+report.getName());
		reportBusiness.compile(report);
		report.setVersion(report.getVersion()+1);
	}

	public Document readSource( long id ) throws FacadeException {
		String logStr = "readSource ";

		Report report = manager.find(Report.class, id);
		if (report == null) {
			log.warn(logStr + "Cannot read report");
			throw new LOSExceptionRB("Cannot read report");
		}
		return reportBusiness.readSourceDocument(report);
	}
}
