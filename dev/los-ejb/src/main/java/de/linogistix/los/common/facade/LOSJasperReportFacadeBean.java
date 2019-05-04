/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.common.businessservice.LOSJasperReportGenerator;
import de.linogistix.los.common.exception.LOSExceptionRB;
import de.linogistix.los.common.service.LOSJasperReportService;
import de.linogistix.los.model.LOSJasperReport;
import de.linogistix.los.util.StringTools;

/**
 * @author krane
 *
 */
@Stateless
public class LOSJasperReportFacadeBean implements LOSJasperReportFacade  {
	Logger log = Logger.getLogger(LOSJasperReportFacadeBean.class);

	@EJB
	private LOSJasperReportService reportService;
	@EJB
	private LOSJasperReportGenerator reportGenerator;
	
	
	public void writeSourceDocument( long id, String document ) throws FacadeException {
		String logStr = "writeSourceDocument ";

		
		LOSJasperReport report = null;
		try {
			report = reportService.get(id);
		} catch (EntityNotFoundException e) {}
		if( report == null ) {
			log.warn(logStr+"Cannot read client");
			throw new LOSExceptionRB("Cannot read client");
		}

//		log.debug(logStr+"report="+report.getName()+", source="+document);
		log.debug(logStr+"report="+report.getName());
		
		report.setSourceDocument(document);
		report.setCompiledDocument(null);
	}
	
	public void compileReport( long id ) throws FacadeException {
		String logStr = "compileReport ";
		
		LOSJasperReport report = null;
		try {
			report = reportService.get(id);
		} catch (EntityNotFoundException e) {}
		if( report == null ) {
			log.warn(logStr+"Cannot read client");
			throw new LOSExceptionRB("Cannot read client");
		}
		log.debug(logStr+"report="+report.getName());

		if( StringTools.isEmpty(report.getSourceDocument()) ) {
			log.warn(logStr+"Report has no source");
			throw new LOSExceptionRB("Report has no source");
		}
		
		report.setCompiledDocument(null);
		
		reportGenerator.getJasportReport(report.getClient(), null, report.getName());
	}
}
