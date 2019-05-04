/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.facade.FacadeException;

import de.linogistix.los.common.businessservice.LOSJasperReportGenerator;
import de.linogistix.los.res.BundleResolver;

/**
 *
 * @author trautm
 */
@Stateless
public class BarcodeLabelReportBean implements BarcodeLabelReport {
	
	@EJB
	private LOSJasperReportGenerator reportGenerator;
	
	public byte[] generateBarcodeLabels(String docName, String[] labels) throws FacadeException {
		List<BarcodeLabelTO> list = new ArrayList<BarcodeLabelTO>();
		for (String label : labels) {
			list.add(new BarcodeLabelTO(label));
		}

		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("REPORT_LOCALE", Locale.GERMANY);

		byte[] bytes = reportGenerator.createPdf(null, docName, BundleResolver.class, list, null);
		return bytes;
	}

}
