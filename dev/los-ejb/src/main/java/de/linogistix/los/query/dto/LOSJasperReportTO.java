/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.query.dto;

import de.linogistix.los.query.BODTO;
import de.wms2.mywms.report.Report;

/**
 * @author krane
 *
 */
public class LOSJasperReportTO extends BODTO<Report> {

	private static final long serialVersionUID = 1L;
    
	private String name;
	private String clientNumber;
	private boolean compiled;
	private boolean sourceAttached;
	
	public LOSJasperReportTO(Report x){
		this(x.getId(), x.getVersion(), x.getName(), x.getClient().getNumber());
	}
	
	public LOSJasperReportTO(Long id, int version, String name, String clientNumber){
		super(id, version, name);
		this.clientNumber = clientNumber;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	public boolean isCompiled() {
		return compiled;
	}

	public void setCompiled(boolean compiled) {
		this.compiled = compiled;
	}

	public boolean isSourceAttached() {
		return sourceAttached;
	}

	public void setSourceAttached(boolean sourceAttached) {
		this.sourceAttached = sourceAttached;
	}

	
}
