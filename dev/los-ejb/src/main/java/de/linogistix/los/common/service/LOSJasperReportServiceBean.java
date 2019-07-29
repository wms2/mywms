/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.service;

import javax.ejb.Stateless;

import org.mywms.service.BasicServiceBean;

import de.wms2.mywms.report.Report;

/**
 * @author krane
 *
 */
@Stateless
public class LOSJasperReportServiceBean extends BasicServiceBean<Report> implements LOSJasperReportService {
}
