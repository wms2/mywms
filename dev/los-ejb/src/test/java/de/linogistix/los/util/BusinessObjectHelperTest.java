/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.util;

import javax.xml.bind.JAXBElement;

import junit.framework.TestCase;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;

import de.linogistix.los.util.BusinessObjectHelper;

public class BusinessObjectHelperTest extends TestCase {

	private static final Logger log = Logger.getLogger(BusinessObjectHelperTest.class);
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}


	public void testBean2String() {
		String out;
		
		out = BusinessObjectHelper.bean2String(new Bean2StringBean());
		log.info(out);
	}

	final class Bean2StringBean{
		
		
		
	    private final QName QNAME = new QName("http://test.solarfabrik.de/xsd", "belegart");
		JAXBElement<String> s1 ;
		
		public Bean2StringBean(){
			s1 = new JAXBElement<String>(QNAME, String.class, "is S1 :-)");
		}

		public JAXBElement<String> getS1() {
			return s1;
		}

		public void setS1(JAXBElement<String> s1) {
			this.s1 = s1;
		}
		
		
		
		
	}
}
