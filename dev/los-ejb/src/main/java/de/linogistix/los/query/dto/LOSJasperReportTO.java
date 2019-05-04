/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.query.dto;

import de.linogistix.los.model.LOSJasperReport;
import de.linogistix.los.query.BODTO;

/**
 * @author krane
 *
 */
public class LOSJasperReportTO extends BODTO<LOSJasperReport> {

	private static final long serialVersionUID = 1L;
    
	private String name;
	private String clientNumber;
	private boolean compiled;
	private boolean sourceAttached;
	
	public LOSJasperReportTO(LOSJasperReport x){
		this(x.getId(), x.getVersion(), x.getName(), x.getClient().getNumber(), x.getCompiledDocument(), x.getSourceDocument());
	}
	
	public LOSJasperReportTO(Long id, int version, String name, String clientNumber, byte[] compiled, String source){
		super(id, version, name);
		this.clientNumber = clientNumber;
		this.name = name;
		this.compiled = ( compiled != null && compiled.length>0 );
		this.sourceAttached = ( source != null && source.length()>0 );
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
