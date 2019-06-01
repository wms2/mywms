/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.businessservice;

import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;
import org.mywms.model.Client;

import net.sf.jasperreports.engine.JasperReport;

/**
 * Handling of Jasper-Reports.<br>
 * 
 * Jasper reports are based on a design, which is compiled, filled with data 
 * and exported as pdf, excel, ...
 * 
 * With this service, the compiled design is stored in the database. 
 * When generating a report, only the fill and export processes are done.
 * 
 * In case of missing the design in the database, it will be read from
 * a resource within a jar, located by bundleResolver.
 * The name of the resource is <name>.jrxml. 
 * 
 * For each client an own report can be stored in database. If no client own
 * report exists, the system-clients report will be used.
 * 
 * @author krane
 */
@Local
public interface LOSJasperReportGenerator {

	/**
	 * Generate a PDF document.
	 *  
	 * @param client
	 * @param name
	 * @param bundleResolver
	 * @param values
	 * @param parameters
	 * @return
	 * @throws FacadeException
	 */
	public byte[] createPdf(Client client, String name, Class<? extends Object> bundleResolver, List<? extends Object> values, Map<String, Object> parameters) throws FacadeException;
	
	/**
	 * Read and compile a report.
	 * 
	 * @param client
	 * @param bundleResolver
	 * @param name
	 * @return
	 * @throws FacadeException
	 */
	public JasperReport getJasportReport(Client client, Class<? extends Object> bundleResolver, String name) throws FacadeException;

}
