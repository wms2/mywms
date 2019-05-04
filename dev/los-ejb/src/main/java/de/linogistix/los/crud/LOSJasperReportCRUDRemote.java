/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.crud;

import javax.ejb.Remote;

import de.linogistix.los.model.LOSJasperReport;

/**
 * @author krane
 *
 */
@Remote
public interface LOSJasperReportCRUDRemote extends BusinessObjectCRUDRemote<LOSJasperReport>{
    
}
