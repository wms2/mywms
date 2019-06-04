/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.businessservice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;

import de.linogistix.los.common.service.LOSJasperReportService;
import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.model.LOSJasperReport;
import de.linogistix.los.report.ReportException;
import de.linogistix.los.report.ReportExceptionKey;
import de.wms2.mywms.client.ClientBusiness;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

/**
 * @author krane
 *
 */
@Stateless
public class LOSJasperReportGeneratorBean implements LOSJasperReportGenerator {
	private static final Logger log = Logger.getLogger(LOSJasperReportGeneratorBean.class);

	@EJB
	private LOSJasperReportService jasperReportService;
	@Inject
	private ClientBusiness clientService;
	@EJB
	private EntityGenerator entityGenerator;
    @PersistenceContext(unitName = "myWMS")
	private EntityManager manager;


	
	public byte[] createPdf(Client client, String name, Class<? extends Object> bundleResolver, List<? extends Object> values, Map<String, Object> parameters) throws FacadeException {
		String logStr = "createPdf ";
		try {
			JRExporter exporter = null;
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			if (parameters == null) {
				parameters = new HashMap<String, Object>();
				parameters.put("REPORT_LOCALE", Locale.GERMANY);
			}

			// Fill the requested report with the specified data
			JRBeanCollectionDataSource jbCollectionDS = new JRBeanCollectionDataSource(values);
			JasperReport jasperReport = getJasportReport(client, bundleResolver, name);
			if( jasperReport == null ) {
				log.error(logStr+"Cannot read report. Abort");
				throw new ReportException(ReportExceptionKey.CREATION_FAILED, "");
			}
			
			JasperPrint jasperPrint = null;
			jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jbCollectionDS);
			exporter = new JRPdfExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
			exporter.exportReport();
			return out.toByteArray();
		} catch (JRException e) {
			log.error(logStr+"Error reading report: "+e.getMessage(), e);
			throw new ReportException(ReportExceptionKey.CREATION_FAILED, "");
		}

	}
	
	
	
	public JasperReport getJasportReport(Client client, Class<? extends Object> bundleResolver, String name) throws FacadeException {
		String logStr = "getJasportReport ";
		
		LOSJasperReport losReport = null;
		JasperReport jasperReport = null;
		
		try {
			if( client == null ) {
				client = clientService.getSystemClient();
			}
			
			// Read report definition of client
			losReport = jasperReportService.getByName(client, name);
			
			if( losReport == null && !client.isSystemClient() ) {
				log.info(logStr+"Read system clients report. name="+name+", client="+client.getNumber());
				// Read report definition of system client
				losReport = jasperReportService.getByName(clientService.getSystemClient(), name);
			}
			
			if( losReport == null || ( !losReport.isCompiledAttached() && !losReport.isSourceAttached()) ) {
				log.info(logStr+"Read report definition fomr jar. name="+name+", client="+client.getNumber());
				// No source report and no compiled report are available
				// Read report definition from jar
				InputStream is;
				String dir = bundleResolver.getPackage().toString();
				dir = dir.replaceAll("package", "/");
				dir = dir.replaceAll("\\.", "/");
				dir = dir.replaceAll("\\s", "");
				String fileName = dir + "/" + name + ".jrxml";
				log.info(logStr+"Read design from " + fileName);
				is = bundleResolver.getResourceAsStream(fileName);
	
				if (is == null) {
					is = this.getClass().getClassLoader().getResourceAsStream(fileName);
					if (is == null) {
						log.error("Cannot read resource with name="+fileName);
						throw new ReportException(ReportExceptionKey.CREATION_FAILED, "");
					}
				}

				
				
	            int numBytes = is.available();
	            byte data[] = new byte[numBytes];
	            is.read(data);
	            is.close();
	            
				
				if( losReport == null ) {
					losReport = entityGenerator.generateEntity(LOSJasperReport.class);
					losReport.setClient(client);
					losReport.setName(name);
					manager.persist(losReport);
				}
				losReport.setSourceDocument(new String(data));
			}

			if( ! losReport.isCompiledAttached() ) {
				if( ! losReport.isSourceAttached() ) {
					log.error(logStr+"No source for report");
					throw new ReportException(ReportExceptionKey.REPORT_DOCUMENT_MISSING, name);
				}
				log.info(logStr+"Compile report. name="+name+", client="+client.getNumber());
				
				JasperDesign jasperDesign = JRXmlLoader.load( new ByteArrayInputStream(losReport.getSourceDocument().getBytes()) );
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream ();
				JasperCompileManager.compileReportToStream(jasperDesign, outputStream);
				outputStream.flush();
				losReport.setCompiledDocument(outputStream.toByteArray());

				outputStream.close();
			}

			ByteArrayInputStream inputStream = new ByteArrayInputStream( losReport.getCompiledDocument() );
			jasperReport = (JasperReport)JRLoader.loadObject( inputStream );
			inputStream.close();
			
			return jasperReport;
		}
		
		catch( JRException e ) {
			log.error(logStr+"Jasper error reading report: "+e.getMessage(), e);
			throw new ReportException(ReportExceptionKey.REPORT_CREATION_FAILED_CAUSE, e.getLocalizedMessage());
		}
		catch( IOException e ) {
			log.error(logStr+"Error reading report: "+e.getMessage(), e);
			throw new ReportException(ReportExceptionKey.REPORT_CREATION_FAILED_CAUSE, e.getLocalizedMessage());
		}

	}


}
