/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.businessservice;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import javax.ejb.Stateless;

import org.apache.log4j.Logger;

/**
 * @author krane
 *
 */
@Stateless
public class LOSHostReportGeneratorBean implements LOSHostReportGenerator {
	private static final Logger log = Logger.getLogger(LOSHostReportGeneratorBean.class);
	
	public byte[] httpGet(String urlStr) {
		InputStream in = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			URL url = new URL(urlStr); // Create the URL
			in = url.openStream(); // Open a stream to it
			// Now copy bytes from the URL to the output stream
			byte[] buffer = new byte[4096];
			int bytes_read;
			while ((bytes_read = in.read(buffer)) != -1){
				out.write(buffer, 0, bytes_read);
			}	
			
			return out.toByteArray();
		}
		catch (Throwable e) {
			log.error(e.getMessage(), e);
			return null;
		} finally { // Always close the streams, no matter what.
			try {
				in.close();
			} catch (Exception e) {
			}
		}
	}
	
}
