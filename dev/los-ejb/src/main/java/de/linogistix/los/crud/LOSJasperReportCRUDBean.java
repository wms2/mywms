/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.crud;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.mywms.facade.FacadeException;
import org.mywms.service.BasicService;

import de.linogistix.los.common.service.LOSJasperReportService;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;
import de.wms2.mywms.product.ItemDataState;
import de.wms2.mywms.report.Report;
import de.wms2.mywms.report.ReportEntityService;

/**
 * @author krane
 *
 */
@Stateless
public class LOSJasperReportCRUDBean extends BusinessObjectCRUDBean<Report> implements LOSJasperReportCRUDRemote {

	@EJB
	LOSJasperReportService service;
	@Inject
	private ReportEntityService reportEntityService;

	@Override
	protected BasicService<Report> getBasicService() {
		return service;
	}

	@Override
	public void update(Report entity) throws BusinessObjectNotFoundException, BusinessObjectModifiedException,
			BusinessObjectMergeException, BusinessObjectSecurityException, FacadeException {
		super.update(entity);

		// deactivate other reports
		if (entity.getState() == ItemDataState.ACTIVE) {
			List<Report> allVersions = reportEntityService.readList(entity.getName(), entity.getClient());
			for (Report report : allVersions) {
				if (!report.equals(entity)) {
					report.setState(ItemDataState.INACTIVE);
				}
			}

		}
	}
}
