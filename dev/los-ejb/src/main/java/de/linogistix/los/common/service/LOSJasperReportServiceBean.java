/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.service;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mywms.model.Client;
import org.mywms.service.BasicServiceBean;

import de.linogistix.los.model.LOSJasperReport;
import de.wms2.mywms.client.ClientBusiness;

/**
 * @author krane
 *
 */
@Stateless
public class LOSJasperReportServiceBean extends BasicServiceBean<LOSJasperReport> implements LOSJasperReportService {

	@Inject
	private ClientBusiness clientService;

	public LOSJasperReport getByName( Client client, String name ) {
		if( client == null ) {
			client = clientService.getSystemClient();
		}
		StringBuffer b = new StringBuffer();	
		b.append("SELECT des FROM ");
		b.append(LOSJasperReport.class.getSimpleName());
		b.append(" des ");
		b.append(" WHERE name=:name and client=:client " );
	
		Query query = manager.createQuery(new String(b));
		query.setParameter("name", name);
		query.setParameter("client", client);

		try{
			return (LOSJasperReport) query.getSingleResult();
		} catch (NoResultException e) { }
		return null;
	}

}
