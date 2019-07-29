/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.query;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import de.linogistix.los.query.dto.LOSJasperReportTO;
import de.wms2.mywms.document.Document;
import de.wms2.mywms.report.Report;
import de.wms2.mywms.report.ReportBusiness;

/**
 * @author krane
 *
 */
@Stateless
public class LOSJasperReportQueryBean extends BusinessObjectQueryBean<Report> implements LOSJasperReportQueryRemote {
	private static final Logger log = Logger.getLogger(LOSJasperReportQueryBean.class);

	@Inject
	private ReportBusiness reportBusiness;
	@PersistenceContext(unitName = "myWMS")
	protected EntityManager manager;

	@Override
	public String getUniqueNameProp() {
		return "name";
	}
	
	@Override
	public Class<LOSJasperReportTO> getBODTOClass() {
		return LOSJasperReportTO.class;
	}
	
	@Override
	protected String[] getBODTOConstructorProps() {
		return new String[]{};
	}
		
	@Override
	protected List<BODTOConstructorProperty> getBODTOConstructorProperties() {
		List<BODTOConstructorProperty> propList = super.getBODTOConstructorProperties();
		
		propList.add(new BODTOConstructorProperty("id", false));
		propList.add(new BODTOConstructorProperty("version", false));
		propList.add(new BODTOConstructorProperty("name", false));
		propList.add(new BODTOConstructorProperty("client.number", false));
		
		return propList;
	}



	
    @Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
		TemplateQueryWhereToken token;
		
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "name", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "client.number", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		
		return ret;
	}
    
	@Override
	protected void enrichResultSet(LOSResultList<Report> results) {
		super.enrichResultSet(results);

		try {
			for (Object x : results) {

				LOSJasperReportTO to = (LOSJasperReportTO) x;

				Report report = manager.find(Report.class, to.getId());
				Document sourceDocument = reportBusiness.readSourceDocument(report);
				if (sourceDocument != null && sourceDocument.getData() != null) {
					to.setSourceAttached(true);
				}

				byte[] jasperDocument = reportBusiness.readJasperDocument(report);
				if (jasperDocument != null && jasperDocument.length > 0) {
					to.setCompiled(true);
				}
			}
		} catch (Throwable t) {
			log.warn("Cannot read document information: " + t.getClass().getSimpleName() + ", " + t.getMessage());
		}
	}
}
