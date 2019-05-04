/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.report;

public final class BarcodeLabelTO {
	private String label;

	public BarcodeLabelTO(String label2) {
		this.label = label2;
	}
	
	public BarcodeLabelTO() {
		this.label = null;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
