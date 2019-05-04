/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.report;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

/**
 *
 * @author trautm
 */
public class StockUnitLabelReportBeanTest extends TestCase {
    
    private static final Logger logger = Logger.getLogger(StockUnitLabelReportBeanTest.class);
    
    public StockUnitLabelReportBeanTest(String testName) {
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

//    /**
//     * Test of generateStockUnitLabelGR method, of class StockUnitLabelReportBean.
//     */
//    public void testGenerateStockUnitLabelGR() throws Exception {
//         try {
//            StockUnitLabelReportBean b = new StockUnitLabelReportBean();
//        
//            LOSUnitLoadQueryRemote uLoadQueryRemote = TestUtilities.beanLocator.getStateless(LOSUnitLoadQueryRemote.class);
//            List<LOSUnitLoad> uls = uLoadQueryRemote.queryAll(new QueryDetail(0, Integer.MAX_VALUE));
//            
//            for (LOSUnitLoad ul :uls ){
//            
//            	ul = uLoadQueryRemote.queryById(ul.getId());
//            	StockUnit su = ul.getStockUnitList().get(0);	
//            
//	            String client = su.getClient().getNumber();
//	            String label = ul.getLabelId();
//	            String itemData = su.getItemData().getNumber();
//	            String lot = su.getLot().getName();
//	            Date date = new Date(System.currentTimeMillis() - 24*60*60*1000); 
//	            
//	            StockUnitLabel s = b.generateStockUnitLabelGR(client, label,itemData, lot, date);    
//	            byte[] pdf = s.getDocument();
//	            
//	            if (pdf == null){
//	                fail();
//	            }
//	            String filename = "testout/"  + label + ".pdf";
//	            logger.info("going to write "+  filename);
//	            OutputStream out = new FileOutputStream(filename);
//	            out.write(pdf);
//	            out.close();
//	            logger.info("wrote "+  filename);
//            }
//            
//        } catch (Throwable ex) {
//            logger.error(ex, ex);
//            fail(ex.getMessage());
//        }
//    }
//
//        public void testPrintStockUnitLabelGR() throws Exception {
//         try {
//            StockUnitLabelReportBean b = new StockUnitLabelReportBean();
//        
//            StockUnitLabel s = b.generateStockUnitLabelGR("TEST", "1233456","TEST ITEM", "TEST LOT", new Date());    
//            byte[] pdf = s.getDocument();
//            
//            if (pdf == null){
//                fail();
//            }
//            String filename = "testout/"  + s.getName() + ".pdf";
//            logger.info("going to print "+  filename);
//            ReportServiceBean rep = new ReportServiceBean();
//            rep.print(null, pdf, DocumentTypes.APPLICATION_PDF.toString());
//            logger.info("printed "+  filename);
//            
//        } catch (Throwable ex) {
//            logger.error(ex, ex);
//            fail(ex.getMessage());
//        }
//    }
    
}
