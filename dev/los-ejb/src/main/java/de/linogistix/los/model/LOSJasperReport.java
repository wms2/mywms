/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.mywms.model.BasicClientAssignedEntity;

/**
 * @author krane
 *
 */
@Entity
@Table(name = "los_jasperreport", uniqueConstraints = {
	    @UniqueConstraint(columnNames = {
	    	"name", "client_id"
	    })
	})
public class LOSJasperReport extends BasicClientAssignedEntity {
	
	private static final long serialVersionUID = 1L;

    @Column(nullable = false)
	private String name;

    @Lob
	private byte[] compiledDocument;

    @Lob
	private String sourceDocument;

    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public byte[] getCompiledDocument() {
		return compiledDocument;
	}
	public void setCompiledDocument(byte[] compiledDocument) {
		this.compiledDocument = compiledDocument;
	}
	
	public String getSourceDocument() {
		return sourceDocument;
	}
	public void setSourceDocument(String sourceDocument) {
		this.sourceDocument = sourceDocument;
	}
	
	
	@Transient
	public boolean isCompiledAttached() {
		if( compiledDocument == null ) {
			return false;
		}
		if( compiledDocument.length == 0 ) {
			return false;
		}
		return true;
	}
	
	
	@Transient
	public boolean isSourceAttached() {
		if( sourceDocument == null ) {
			return false;
		}
		if( sourceDocument.length() == 0 ) {
			return false;
		}
		return true;
	}

	@Override
	public String toDescriptiveString() {
		return name + getClient() == null ? "" : getClient().getNumber();
	}
}
