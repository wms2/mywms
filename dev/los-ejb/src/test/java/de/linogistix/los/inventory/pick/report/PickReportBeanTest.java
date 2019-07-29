/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.pick.report;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import de.linogistix.los.example.CommonTestTopologyRemote;
import de.linogistix.los.inventory.pick.model.PickReceipt;
import de.linogistix.los.inventory.pick.model.PickReceiptPosition;
import de.linogistix.los.model.State;

/**
 * 
 * @author trautm
 */
public class PickReportBeanTest extends TestCase {
    
	private static final Logger logger = Logger.getLogger(PickReportBeanTest.class);
	
    public PickReportBeanTest(String testName) {
        super(testName);
    }            

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

//    public void testGenerateGoodsOutReceipt() throws Exception {
//        System.out.println("generateGoodsOutReceipt");
//        String c = CommonTestTopologyRemote.TESTCLIENT_NUMBER;
//        String orderNo = "123";
//        String pickNo = "321";
//        String labelId = "000001";
//        String name = "pickReceipt";
//        int state = State.PENDING;
//        
//        List<PickReceiptPosition> positions = new ArrayList<PickReceiptPosition>();
//       
//        for (int i=0;i<35;i++){
//	        PickReceiptPosition p = new PickReceiptPosition();
//	        p.setAmount(new BigDecimal(9999));
//	        p.setAmountordered(new BigDecimal(10000));
//	        p.setArticleDescr("Dies ist ein Test");
//	        p.setArticleRef("Test " + i);
//	        p.setLotRef("NN" + i);
//	        positions.add(p);
//        }
//       
//        
//        PickReportBean instance = new PickReportBean();
//        PickReceipt r = instance.generateGoodsOutReceipt(c, 
//        		DocumentTypes.APPLICATION_PDF.toString(),
//        		labelId, name, orderNo, pickNo, state, positions);
//        
//        byte[] pdf = r.getDocument();
//
//		if (pdf == null) {
//			fail();
//		}
//		String filename = "testout/" + r.getName() + ".pdf";
//		logger.info("going to write " + filename);
//		FileOutputStream out = new FileOutputStream(filename);
//		out.write(pdf);
//		out.close();
//		logger.info("wrote " + filename);
//    }
}
