/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.report.businessservice;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import de.linogistix.los.common.businessservice.LOSHostReportGeneratorBean;
import de.linogistix.los.inventory.model.LOSStockUnitRecord;
import de.linogistix.los.report.GenericExcelExporter;
import de.linogistix.los.report.businessservice.ReportServiceBeanTest;

/**
 *
 * @author trautm
 */
public class ReportServiceBeanTest2 extends TestCase {
    
	private static final Logger logger = Logger.getLogger(ReportServiceBeanTest.class);
    
	LOSHostReportGeneratorBean bean;
	
	String url = "";
	
	public ReportServiceBeanTest2(String testName) {
        super(testName);
    }            

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        bean = new LOSHostReportGeneratorBean();
        url = "http://www.mccms.mediacluster.de/media/0000000043.pdf";
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }


    
    public void testTypeExportExcelGeneric(){
   	
    	LOSStockUnitRecord r = new LOSStockUnitRecord();
    	r.setAmount(new BigDecimal(10));
    	r.setFromStockUnitIdentity("123");
    	
    	
    	List<Object> l = new ArrayList<Object>();
    	l.add(r);
    	
    	try {
			byte[] bytes = new GenericExcelExporter().export("Test Generic Excel Export", l, null);
			String filename = "testout/genericexcel.xls";
			logger.info("going to write " + filename);
			OutputStream out = new FileOutputStream(filename);
			out.write(bytes);
			out.close();
			logger.info("wrote " + filename);
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
    }
    
    public void testHttpGet() throws Exception{
    	byte[] bytes;
    	try{
	    	bytes = bean.httpGet("http://13rt2gqersujzee568zw4zt23565z53zqrgt");
	    	assertNull(bytes);
	    	
	    	bytes = bean.httpGet(url);
	    	assertNotNull(bytes);
	    	assertTrue(bytes.length > 0);
	    	
	    	String filename = "testout/httpget.pdf";
	    	
	    	logger.info("going to write " + filename);
			OutputStream out = new FileOutputStream(filename);
			out.write(bytes);
			out.close();
			logger.info("wrote " + filename);
			
    	} catch (Exception t){
    		logger.error(t.getMessage(),t);
    		throw t;
    	}
		
    }
    
    public static void main(String[] args){
    	try{
	    	if (args.length < 1){
	    		System.out.println("usage: command [params]");
	    		System.out.println("commands:");
	    		System.out.println("\t httpget url");
	    		System.exit(0);
	    	}
	    	if (args[0].equals("httpget")){
	    		ReportServiceBeanTest test = new ReportServiceBeanTest(ReportServiceBeanTest.class.getName());
	    		if (args[1] == null){
	    			System.out.println("\t httpget url");
	    			System.exit(0);
	    		} else{
	    			test.url = args[1];
	    			test.testHttpGet();
	    			System.exit(1);
	    		}
	    	}
	    } catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
    }

}
