/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;

import de.linogistix.los.inventory.example.TopologyBeanTest;
import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPosition;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPositionState;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestState;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.query.LOSGoodsOutRequestQueryRemote;
import de.linogistix.los.inventory.query.dto.LOSGoodsOutRequestTO;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.query.LOSUnitLoadQueryRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.test.TestUtilities;

/**
 *
 * @author trautm
 */
    public class GoodsOutFacadeBeanTest extends TestCase {

    private static final Logger logger = Logger.getLogger(GoodsOutFacadeBeanTest.class);
    public LOSGoodsReceiptFacade bean;
    protected TopologyBeanTest topology;
    
    public LOSGoodsReceipt GR;
    
    BODTO<Client> cdto;
    BODTO<LOSStorageLocation> goodsInLocation;

    public GoodsOutFacadeBeanTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.bean = TestUtilities.beanLocator.getStateless(LOSGoodsReceiptFacade.class);
        topology = new TopologyBeanTest();
        topology.initServices();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }


    /**
     * Test of createGoodsReceipt method, of class LOSGoodsReceiptFacadeBean.
     */
    public void testGoodsOut() {
        
    	LOSGoodsOutRequestQueryRemote orderQuery = TestUtilities.beanLocator.getStateless(LOSGoodsOutRequestQueryRemote.class);
    	List<LOSGoodsOutRequestTO> reqs;
    	
    	LOSUnitLoadQueryRemote ulQuery = TestUtilities.beanLocator.getStateless(LOSUnitLoadQueryRemote.class);
    	
//    	try {
//            System.out.println("goodsOut");
//            LOSGoodsOutFacade bean = TestUtilities.beanLocator.getStateless(LOSGoodsOutFacade.class);
//            reqs = bean.getRaw();
//            if (reqs == null || reqs.size() < 1){
//            	fail("No GoodsOutRequest");
//            }
//            for (LOSGoodsOutRequestTO to : reqs){
//            	
//            	LOSGoodsOutRequest r = bean.load(to.getNumber()).getOrder();
//            	r = bean.start(r);
//            	r = orderQuery.queryById(r.getId());
//            	for (LOSGoodsOutRequestPosition pos : r.getPositions()){
//            		pos = bean.finishPosition(pos.getSource().getLabelId(), r);
//            		assertEquals(pos.getOutState(), LOSGoodsOutRequestPositionState.FINISHED);
//            	}
//            	r = bean.finish(r);
//            	assertEquals(r.getOutState(), LOSGoodsOutRequestState.FINISHED);
////            	LOSOrderRequest order = (LOSOrderRequest)r.getParentRequest();
////            	//At least in this case...
////            	assertEquals(order.getOrderState(), LOSOrderRequestState.FINISHED);
////            	for ( LOSGoodsOutRequestPosition p : r.getPositions()){
////            		LOSUnitLoad ul = (LOSUnitLoad) p.getSource();
////            		ul = ulQuery.queryById(ul.getId());
////            		for (StockUnit su : ul.getStockUnitList()){
////            			assertTrue(su.getAmount().compareTo(new BigDecimal(0)) == 0);
////            		}
////            	}
//            }
//        
//        } catch (FacadeException ex) {
//            logger.error("testGoodsOut: " + ex.getMessage(),ex);
//            fail(ex.getMessage());
//        } 
        

    }

   
}
