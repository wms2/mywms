/* 
Copyright 2019 Matthias Krane
info@krane.engineer

This file is part of the Warehouse Management System mywms

mywms is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.
 
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/
package de.wms2.mywms.report;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.mywms.model.Client;

import de.wms2.mywms.client.ClientBusiness;
import de.wms2.mywms.document.Document;
import de.wms2.mywms.document.DocumentBusiness;
import de.wms2.mywms.document.DocumentEntityService;
import de.wms2.mywms.document.DocumentType;
import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.product.ItemDataState;
import de.wms2.mywms.property.SystemPropertyBusiness;
import de.wms2.mywms.util.Translator;
import de.wms2.mywms.util.Wms2BundleResolver;
import de.wms2.mywms.util.Wms2Properties;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

/**
 * @author krane
 *
 */
@Stateless
public class ReportBusiness {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private ClientBusiness clientBusiness;
	@Inject
	private ReportEntityService reportService;
	@Inject
	private DocumentEntityService documentService;
	@Inject
	private DocumentBusiness documentBusiness;
	@Inject
	private SystemPropertyBusiness propertyBusiness;
	@Inject
	private PersistenceManager manager;

	public byte[] createPdfDocument(Client client, String name, Class<?> bundleResolver, List<? extends Object> values,
			Map<String, Object> parameters) throws BusinessException {
		return createPdfDocument(client, name, null, bundleResolver, values, parameters);
	}

	public byte[] createPdfDocument(Client client, String name, String reportVersion, Class<?> bundleResolver,
			List<? extends Object> values, Map<String, Object> parameters) throws BusinessException {
		String logStr = "createPdfDocument ";
		logger.log(Level.INFO, logStr + "client=" + client + ", name=" + name + ", reportVersion=" + reportVersion);

		if (parameters == null) {
			parameters = new HashMap<String, Object>();
		}

		// Fill the requested report with the specified data
		JRBeanCollectionDataSource jbCollectionDS = new JRBeanCollectionDataSource(values);
		Report report = readOrCreateReport(client, name, reportVersion);
		if (report == null) {
			logger.log(Level.SEVERE, logStr + "Cannot read report. Abort. client=" + client + ", name=" + name
					+ ", reportVersion=" + reportVersion);
			throw new BusinessException(Wms2BundleResolver.class, "Report.missingReport",
					new Object[] { client, reportVersion });
		}

		JasperReport jasperReport = readJasperReport(report, bundleResolver);
		if (jasperReport == null) {
			logger.log(Level.SEVERE, logStr + "Cannot read compiled report. Abort. client=" + report.getClient()
					+ ", name=" + report.getName() + ", reportVersion=", report.getReportVersion());
			throw new BusinessException(Wms2BundleResolver.class, "Report.creationFailed");
		}

		Document image = documentBusiness.readImage(Report.class, report.getId());
		parameters.put("image", image);

		// Add all attachments to report
		List<Document> attachments = documentBusiness.readDocuments(Report.class, report.getId(), "attachment");
		for (Document document : attachments) {
			parameters.put(document.getSimpleName(), document);
		}

		String localeString = propertyBusiness.getString(Wms2Properties.KEY_REPORT_LOCALE, null);
		Locale locale = Translator.parseLocale(localeString);
		parameters.put(JRParameter.REPORT_LOCALE, locale);

		ResourceBundle bundle = Translator.getBundle(Wms2BundleResolver.class, null, locale);
		parameters.put(JRParameter.REPORT_RESOURCE_BUNDLE, bundle);

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jbCollectionDS);

			JRPdfExporter exporter = new JRPdfExporter();
			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
			exporter.exportReport();

			return out.toByteArray();

		} catch (Throwable e) {
			logger.log(Level.SEVERE, logStr + "Error reading report: " + e.getClass().getName() + ", " + e.getMessage(),
					e);
			throw new BusinessException(Wms2BundleResolver.class, "Report.creationFailed");
		}
	}

	public byte[] compile(Report report) throws BusinessException {
		String logStr = "compile ";
		logger.log(Level.INFO, logStr + "report=" + report);

		Document sourceDocument = readSourceDocument(report);
		if (sourceDocument == null || sourceDocument.getData() == null || sourceDocument.getData().length == 0) {
			logger.log(Level.WARNING, logStr + "Report has no source");
			throw new BusinessException(Wms2BundleResolver.class, "Report.missingSource");
		}

		byte[] data = null;
		try {
			JasperDesign jasperDesign = JRXmlLoader.load(new ByteArrayInputStream(sourceDocument.getData()));
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			JasperCompileManager.compileReportToStream(jasperDesign, outputStream);
			outputStream.flush();
			outputStream.close();

			data = outputStream.toByteArray();
			saveJasperDocument(report, data);

		} catch (JRException e) {
			logger.log(Level.SEVERE,
					logStr + "Error compiling report: " + e.getClass().getName() + ", " + e.getMessage(), e);
			throw new BusinessException(Wms2BundleResolver.class, "Report.creationFailed", e.getLocalizedMessage());
		} catch (IOException e) {
			logger.log(Level.SEVERE, logStr + "Error writing report: " + e.getClass().getName() + ", " + e.getMessage(),
					e);
			throw new BusinessException(Wms2BundleResolver.class, "Report.creationFailed", e.getLocalizedMessage());
		}
		return data;
	}

	/**
	 * Resolve the report to use:<br/>
	 * <li/>If client is given and reportVersion is given:<br>
	 * a) Existing report of client and reportVersion<br>
	 * b) Existing report of system client and reportVersion<br>
	 * c) null
	 * <li/>if client is NOT given and reportVersion is given:<br>
	 * a) Existing report of system client and reportVersion<br>
	 * b) null
	 * <li/>If client is given and reportVersion is NOT given:<br>
	 * a) The ACTIVE report of client<br>
	 * b) The ACTIVE report of system client<br>
	 * c) The DEFAULT report of system client<br>
	 * d) Create DEFAULT report of system client<br>
	 * <li/>If client is NOT given and reportVersion is NOT given:<br>
	 * a) The ACTIVE report of system client<br>
	 * b) The DEFAULT report of system client<br>
	 * c) Create DEFAULT report of system client<br>
	 */
	private Report readOrCreateReport(Client client, String name, String reportVersion) throws BusinessException {
		String jpql = "SELECT entity FROM " + Report.class.getName() + " entity ";
		jpql += " where entity.name=:name";

		if (client != null) {
			jpql += " and (entity.client=:client or entity.client.id=0)";
		} else {
			jpql += " and entity.client.id=0";
		}
		if (!StringUtils.isBlank(reportVersion)) {
			jpql += " and entity.reportVersion=:reportVersion";
		} else {
			jpql += " and entity.state=" + ItemDataState.ACTIVE;
		}
		jpql += " order by entity.client.id desc, entity.state, entity.reportVersion";

		TypedQuery<Report> query = manager.createQuery(jpql, Report.class);
		query.setMaxResults(1);
		query.setParameter("name", name);
		if (client != null) {
			query.setParameter("client", client);
		}
		if (!StringUtils.isBlank(reportVersion)) {
			query.setParameter("reportVersion", reportVersion);
		}

		try {
			Report result = query.getSingleResult();
			logger.log(Level.INFO, "Resolved report: client=" + result.getClient() + ", name=" + result.getName()
					+ ", reportVersion=" + result.getReportVersion());
			return result;
		} catch (NoResultException e) {
		}

		if (StringUtils.isBlank(reportVersion)) {
			jpql = "SELECT entity FROM " + Report.class.getName() + " entity ";
			jpql += " where entity.name=:name and entity.client.id=0";
			jpql += " and entity.reportVersion='" + Report.DEFAULT_REPORT_VERSION + "'";
			query = manager.createQuery(jpql, Report.class);
			query.setParameter("name", name);
			try {
				Report result = query.getSingleResult();
				logger.log(Level.WARNING, "Resolved inactive default report: client=" + result.getClient() + ", name="
						+ result.getName() + ", reportVersion=" + result.getReportVersion());
				return result;
			} catch (NoResultException e) {
			}

			logger.log(Level.WARNING, "Generate not existing DEFAULT report. name=" + name);
			return reportService.create(name, clientBusiness.getSystemClient(), Report.DEFAULT_REPORT_VERSION);
		}

		logger.log(Level.WARNING,
				"Cannot read report for client=" + client + ", name=" + name + ", reportVersion=" + reportVersion);
		return null;
	}

	/**
	 * Read the versions of a report
	 * <p>
	 * The first element of the list is the one with the active-flag set.
	 * <p>
	 * For multi client systems, all available versions are read. For the
	 * active-flag only the system-client is checked.
	 */
	public List<String> readReportVersions(String name, Client client) throws BusinessException {
		String jpql = "select entity.reportVersion from ";
		jpql += Report.class.getSimpleName() + " entity ";
		jpql += " where entity.name=:name";
		if (client != null) {
			jpql += " and (entity.client=:client or entity.client.id=0)";
		} else {
			jpql += " and entity.client.id=0";
		}
		jpql += " order by entity.client.id desc, entity.state, entity.reportVersion";

		TypedQuery<String> versionsQuery = manager.createQuery(jpql, String.class);
		versionsQuery.setParameter("name", name);
		if (client != null) {
			versionsQuery.setParameter("client", client);
		}
		List<String> reportVersions = versionsQuery.getResultList();

		List<String> resultList = new ArrayList<>(reportVersions.size());
		for (String reportVersion : reportVersions) {
			if (!resultList.contains(reportVersion)) {
				resultList.add(reportVersion);
			}
		}

		return resultList;
	}

	private JasperReport readJasperReport(Report report, Class<?> bundleResolver) throws BusinessException {
		String logStr = "readJasperReport ";

		JasperReport jasperReport = null;

		byte[] compiled = readJasperDocument(report);
		if (compiled == null) {
			Document source = readSourceDocument(report);
			if (source == null) {
				generateDefaultSourceDocument(report, bundleResolver);
			}
			compiled = compile(report);
		}

		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(compiled);
			jasperReport = (JasperReport) JRLoader.loadObject(inputStream);
			inputStream.close();
		} catch (JRException e) {
			logger.log(Level.SEVERE,
					logStr + "Jasper error reading report: " + e.getClass().getName() + ", " + e.getMessage(), e);
			throw new BusinessException(Wms2BundleResolver.class, "Report.missingDokument", e.getLocalizedMessage());
		} catch (IOException e) {
			logger.log(Level.SEVERE, logStr + "Error reading report: " + e.getClass().getName() + ", " + e.getMessage(),
					e);
			throw new BusinessException(Wms2BundleResolver.class, "Report.missingDokument", e.getLocalizedMessage());
		}

		return jasperReport;
	}

	private Document generateDefaultSourceDocument(Report report, Class<?> bundleResolver) throws BusinessException {
		String logStr = "generateDefaultSourceDocument ";
		logger.log(Level.INFO, logStr + "report=" + report);

		if (bundleResolver == null) {
			bundleResolver = Wms2BundleResolver.class;
		}

		String filename1 = null;
		String filename2 = null;

		String path = bundleResolver.getPackage().getName();
		path = path.replaceAll("\\.", "/");
		filename1 = "/" + path + "/" + report.getName() + ".jrxml";
		InputStream jrxmlStream = bundleResolver.getResourceAsStream(filename1);

		if (jrxmlStream == null) {
			filename2 = "/reports/" + report.getName() + ".jrxml";
			jrxmlStream = bundleResolver.getResourceAsStream(filename2);
			if (jrxmlStream == null) {
				jrxmlStream = this.getClass().getClassLoader().getResourceAsStream(filename2);
			}
			if (jrxmlStream == null) {
				logger.log(Level.SEVERE, logStr + "Cannot read resource with name=" + filename1 + " or name="
						+ filename2 + ", bundleResolver=" + bundleResolver.getName());
				throw new BusinessException(Wms2BundleResolver.class, "Report.creationFailed");
			}
		}

		try {
			int size = jrxmlStream.available();
			byte data[] = new byte[size];
			jrxmlStream.read(data);
			jrxmlStream.close();

			Document source = saveSourceDocument(report, report.getName() + ".jrxml", data);
			return source;

		} catch (IOException e) {
			logger.log(Level.SEVERE,
					logStr + "Error reading default report: " + e.getClass().getName() + ", " + e.getMessage(), e);
			throw new BusinessException(Wms2BundleResolver.class, "Report.missingDokument", e.getLocalizedMessage());
		}

	}

	public Document readSourceDocument(Report report) {
		String documentNamePrefix = Report.class.getSimpleName() + ":" + report.getId() + "/source";
		Document source = documentService.readFirstByNamePrefix(documentNamePrefix);
		return source;
	}

	public byte[] readJasperDocument(Report report) {
		String documentName = Report.class.getSimpleName() + ":" + report.getId() + "/compiled";
		Document compiled = documentService.readByName(documentName);
		if (compiled == null) {
			return null;
		}
		return compiled.getData();
	}

	/**
	 * @param name Optional. In doubt the name of the report is used
	 */
	public Document saveSourceDocument(Report report, String name, byte[] data) {
		if (name == null) {
			name = report.getName() + ".jrxml";
		}

		String documentName = Report.class.getSimpleName() + ":" + report.getId() + "/source/" + name;
		documentService.deleteByNamePrefix(documentName);
		Document source = documentService.create(documentName, DocumentType.XML, data);
		return source;
	}

	private Document saveJasperDocument(Report report, byte[] data) {
		String documentName = Report.class.getSimpleName() + ":" + report.getId() + "/compiled";
		documentService.delete(documentName);
		Document compiled = documentService.create(documentName, DocumentType.UNDEFINED, data);
		return compiled;
	}

	public int deleteReportDocuments(Report report) throws BusinessException {
		int num = 0;
		if (report == null || report.getId() == null) {
			return 0;
		}
		String documentName = Report.class.getSimpleName() + ":" + report.getId() + "/compiled";
		num += documentService.delete(documentName);

		String documentNamePrefix = Report.class.getSimpleName() + ":" + report.getId() + "/source/";
		num += documentService.deleteByNamePrefix(documentNamePrefix);
		return num;
	}
}
