/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.crud;

import javax.ejb.Remote;

import de.wms2.mywms.report.Report;

/**
 * @author krane
 *
 */
@Remote
public interface LOSJasperReportCRUDRemote extends BusinessObjectCRUDRemote<Report>{
    
}
