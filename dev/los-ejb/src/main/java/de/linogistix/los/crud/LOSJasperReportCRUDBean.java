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
import de.linogistix.los.model.LOSJasperReport;

/**
 * @author krane
 *
 */
@Stateless
public class LOSJasperReportCRUDBean extends BusinessObjectCRUDBean<LOSJasperReport> implements LOSJasperReportCRUDRemote {

    @EJB
    LOSJasperReportService service;

    @Override
    protected BasicService<LOSJasperReport> getBasicService() {
        return service;
    }

}
