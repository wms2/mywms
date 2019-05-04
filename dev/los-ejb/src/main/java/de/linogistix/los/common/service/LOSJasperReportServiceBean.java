/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.service;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mywms.model.Client;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.ClientService;

import de.linogistix.los.model.LOSJasperReport;

/**
 * @author krane
 *
 */
@Stateless
public class LOSJasperReportServiceBean extends BasicServiceBean<LOSJasperReport> implements LOSJasperReportService {

	@EJB
	private ClientService clientService;

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
