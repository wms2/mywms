/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.service;

import javax.ejb.Local;

import org.mywms.model.Client;
import org.mywms.service.BasicService;

import de.linogistix.los.model.LOSJasperReport;
/**
 * @author krane
 *
 */
@Local
public interface LOSJasperReportService extends BasicService<LOSJasperReport>{

	public LOSJasperReport getByName( Client client, String name );
}
