/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.crud;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.service.BasicService;

import de.linogistix.los.common.service.LOSJasperReportService;
import de.wms2.mywms.report.Report;

/**
 * @author krane
 *
 */
@Stateless
public class LOSJasperReportCRUDBean extends BusinessObjectCRUDBean<Report> implements LOSJasperReportCRUDRemote {

    @EJB
    LOSJasperReportService service;

    @Override
    protected BasicService<Report> getBasicService() {
        return service;
    }

}
