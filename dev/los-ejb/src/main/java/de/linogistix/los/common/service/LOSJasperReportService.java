/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.service;

import javax.ejb.Local;

import org.mywms.service.BasicService;

import de.wms2.mywms.report.Report;
/**
 * @author krane
 *
 */
@Local
public interface LOSJasperReportService extends BasicService<Report>{
}
