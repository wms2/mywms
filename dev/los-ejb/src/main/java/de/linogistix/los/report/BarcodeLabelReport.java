/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.report;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;

/**
 *
 * @author trautm
 */
@Local
public interface BarcodeLabelReport {

	/**
	 * Generation of a barcode label
	 * 
	 * @param unitLoad
	 * @return
	 * @throws FacadeException
	 */
	public byte[] generateBarcodeLabels(String docName, String[] labels) throws FacadeException;
	
    	
}
