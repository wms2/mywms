/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.pick.facade;

import junit.framework.TestCase;

/**
 * 
 * @author trautm
 */
public class PickOrderFacadeBeanTest extends TestCase {

//	private static final Logger logger = Logger
//			.getLogger(PickOrderFacadeBeanTest.class);
//
//	PickOrderFacade bean;
//
//	public PickOrderFacadeBeanTest(String testName) {
//		super(testName);
//	}
//
//	@Override
//	protected void setUp() throws Exception {
//		super.setUp();
//		bean = TestUtilities.beanLocator.getStateless(PickOrderFacade.class);
//	}
//
//	@Override
//	protected void tearDown() throws Exception {
//		super.tearDown();
//	}
//
//	public void testCancelAccepted() throws Exception {
//		
//		LOSPickRequestQueryRemote pickReqQuery = TestUtilities.beanLocator
//				.getStateless(LOSPickRequestQueryRemote.class);
//		ClientQueryRemote clientQuery = TestUtilities.beanLocator
//				.getStateless(ClientQueryRemote.class);
//
//		try {
//
//			LOSPickRequest r = null;
//			Client c = clientQuery
//					.queryByIdentity(CommonTestTopologyRemote.TESTCLIENT_NUMBER);
//			r = pickReqQuery.queryByOrderReference(c, "TEST 1");
//			assertNotNull(r);
//			assertEquals(r.getClient().getNumber(),	CommonTestTopologyRemote.TESTCLIENT_NUMBER);
//
//			r = bean.loadPickingRequest(r);
//			r = bean.accept(r);
//			r = bean.loadPickingRequest(r);
//			assertTrue(r.getState().equals(PickingRequestState.ACCEPTED));
//			bean.cancel(r);
//			r = bean.loadPickingRequest(r);
//			assertTrue(r.getState().equals(PickingRequestState.RAW));
//		} catch (Exception ex) {
//			logger.error(ex.getMessage(), ex);
//			throw ex;
//		}
//	}
//
//	public void testPickWithUnexpectedNull() throws Exception {
//		
//		StockUnitQueryRemote suQuery = TestUtilities.beanLocator
//				.getStateless(StockUnitQueryRemote.class);
//		LOSPickRequestPositionQueryRemote posQuery = TestUtilities.beanLocator
//				.getStateless(LOSPickRequestPositionQueryRemote.class);
//		LOSPickRequestQueryRemote pickReqQuery = TestUtilities.beanLocator
//				.getStateless(LOSPickRequestQueryRemote.class);
//		ClientQueryRemote clientQuery = TestUtilities.beanLocator
//				.getStateless(ClientQueryRemote.class);
//
//		InventoryProcessFacade inventoryProcessFacade = TestUtilities.beanLocator
//				.getStateless(InventoryProcessFacade.class);
//		try {
//
//			LOSPickRequest r = null;
//			Client c = clientQuery
//					.queryByIdentity(CommonTestTopologyRemote.TESTCLIENT_NUMBER);
//			r = pickReqQuery.queryByOrderReference(c, "TEST 1");
//			assertNotNull(r);
//			assertEquals(r.getClient().getNumber(),	CommonTestTopologyRemote.TESTCLIENT_NUMBER);
//
//			r = bean.loadPickingRequest(r);
//			r = bean.accept(r);
//			r = bean.loadPickingRequest(r);
//			assertTrue(r.getState().equals(PickingRequestState.ACCEPTED));
//			LOSStorageLocation dest = bean.getDestination(r);
//			assertNotNull(dest);
//			assertEquals(LocationTestTopologyRemote.SL_WA_TESTCLIENT_NAME, dest.getName());
//
//			// position 2 with unexpected null
//			LOSPickRequestPosition pos = r.getPositions().get(1);
//			logger.info("process position " + pos.toDescriptiveString());
//
//			LOSUnitLoad fromUl = (LOSUnitLoad) pos.getStockUnit().getUnitLoad();
//
//			try {
//				bean.testCanProcess(pos, false, fromUl.getStorageLocation()
//						.getName(), pos.getAmount().subtract(new BigDecimal(1)) );
//				// pos = bean.processPickRequestPosition(pos, false, fromUl
//				// .getStorageLocation().getName(),
//				// pos.getAmount() - 1);
//				fail("Expected PickingException that didn't come");
//			} catch (PickingException ex) {
//				if (ex.getPickingExceptionKey().equals(
//						PickingExceptionKey.PICK_UNEXPECTED_NULL)) {
//					StockUnit old = pos.getStockUnit();
//
//					try {
//						// ... on a fixed assigned StorageLocation
//						inventoryProcessFacade.doInventoyForStorageLocation(
//								CommonTestTopologyRemote.TESTCLIENT_NUMBER, "T1-1-5-2",
//								"SubstUnexpectedNull",null, null,old.getItemData()
//										.getNumber(), old.getLot().getName(),
//										new BigDecimal(1500), true, true, true);
//
//					} catch (FacadeException e) {
//						logger.error(e.getMessage(), e);
//						fail(e.getMessage());
//					}
//
//					try {
//						pos = bean.processPickRequestPosition(pos, true, fromUl
//								.getStorageLocation().getName(),  pos
//								.getAmount().subtract(new BigDecimal(1) ), false, false);
//						StockUnit newS = pos.getStockUnit();
//						old = suQuery.queryById(old.getId());
//						newS = suQuery.queryById(newS.getId());
//						assertTrue(old.equals(newS));
//						LOSUnitLoad ul = (LOSUnitLoad) old.getUnitLoad();
//						logger.info("UL: " + ul.getLabelId() + " SL "
//								+ ul.getStorageLocation().getName());
//						LOSPickRequestPosition newPos = null;
//						LOSPickRequest rTmp = bean.loadPickingRequest(r);
//						for (LOSPickRequestPosition p2 : rTmp.getPositions()) {
//							if (!p2.isPicked()
//									&& p2.getItemData().equals(
//											pos.getItemData())) {
//								newPos = posQuery.queryById(p2.getId());
//								break;
//							}
//						}
//						LOSUnitLoad newFromUl = (LOSUnitLoad) newPos
//								.getStockUnit().getUnitLoad();
//						bean.processPickRequestPosition(newPos, false,
//								newFromUl.getStorageLocation().getName(),
//								 newPos.getAmount(), false, false);
//					} catch (NullAmountNoOtherException ex2) {
//						logger.error(ex2.getMessage(), ex2);
//						fail(ex2.getMessage());
//						pos = posQuery.queryById(pos.getId());
//						assertTrue(pos.isCanceled());
//					}
//				} else {
//					logger.error(ex.getMessage(), ex);
//					fail(ex.getMessage());
//				}
//			}
//
//			// Now the first position
//			try {
//				pos = r.getPositions().get(0);
//				fromUl = (LOSUnitLoad) pos.getStockUnit().getUnitLoad();
//				pos = bean.processPickRequestPosition(pos, false, fromUl
//						.getStorageLocation().getName(),  pos.getAmount(), false, false);
//			} catch (InventoryException ex) {
//				logger.error(ex.getMessage());
//				throw ex;
//
//			}
//
//			r = bean.loadPickingRequest(r);
//			assertEquals(r.getState(), PickingRequestState.PICKED);
//			r = bean.finishPickingRequest(r, r.getDestination().getName());
//			assertEquals(PickingRequestState.FINISHED, r.getState());
//		} catch (Exception ex) {
//			logger.error(ex, ex);
//			throw ex;
//		}
//	}
//
//	public void testPickStockUnitHasChanged() throws Exception {
//
//		LOSPickRequestPositionQueryRemote posQuery = TestUtilities.beanLocator
//				.getStateless(LOSPickRequestPositionQueryRemote.class);
//		LOSPickRequestQueryRemote pickReqQuery = TestUtilities.beanLocator
//				.getStateless(LOSPickRequestQueryRemote.class);
//		ClientQueryRemote clientQuery = TestUtilities.beanLocator
//				.getStateless(ClientQueryRemote.class);
//		
//		LOSStorageLocationQueryRemote slQuery = TestUtilities.beanLocator
//				.getStateless(LOSStorageLocationQueryRemote.class);
//		try {
//			LOSPickRequest r = null;
//			Client c = clientQuery
//					.queryByIdentity(CommonTestTopologyRemote.TESTCLIENT_NUMBER);
//
//			r = pickReqQuery.queryByOrderReference(c, "TEST 2");
//			assertNotNull(r);
//			assertEquals(r.getClient().getNumber(),	CommonTestTopologyRemote.TESTCLIENT_NUMBER);
//
//			r = bean.loadPickingRequest(r);
//			r = bean.accept(r);
//			r = bean.loadPickingRequest(r);
//			assertTrue(r.getState().equals(PickingRequestState.ACCEPTED));
//			LOSStorageLocation dest = bean.getDestination(r);
//			assertNotNull(dest);
//			assertEquals(LocationTestTopologyRemote.SL_WA_TESTCLIENT_NAME, dest.getName());
//
//			// position 1
//			LOSPickRequestPosition pos = r.getPositions().get(0);
//			logger.info("process position " + pos.toDescriptiveString());
//
//			LOSUnitLoad fromUl = (LOSUnitLoad) pos.getStockUnit().getUnitLoad();
//
//			try {
//				try {
//					assertTrue(bean.testCanProcess(pos, false, fromUl
//							.getStorageLocation().getName(),  pos
//							.getAmount()));
//
//					pos = bean.processPickRequestPosition(pos, false, fromUl
//							.getStorageLocation().getName(),  pos
//							.getAmount(), false, false);
//				} catch (PickingExpectedNullException ex) {
//					pos = bean.processPickRequestPositionExpectedNull(pos,
//							fromUl.getStorageLocation().getName(),  pos
//									.getAmount(), new BigDecimal(0), false, false);
//				}
//			} catch (InventoryException ex) {
//				logger.error(ex.getMessage(), ex);
//				throw ex;
//			}
//			LOSStorageLocation sl = slQuery.queryById(r.getCart().getId());
//			for (int i=0;i<sl.getUnitLoads().size();i++) {
//				r = bean.finishCurrentUnitLoad(r, r.getDestination().getName());
//			}
//			// position 2
//			pos = r.getPositions().get(1);
//			logger.info("process position " + pos.toDescriptiveString());
//
//			
//			fromUl = (LOSUnitLoad) pos.getStockUnit().getUnitLoad();
//
//			try {
//				assertTrue(pos.isCanceled()
//						&& !bean.testCanProcess(pos, false, fromUl
//								.getStorageLocation().getName(), pos
//								.getAmount()));
//
//				pos = bean.processPickRequestPosition(pos, false, fromUl
//						.getStorageLocation().getName(), pos.getAmount(), false, false);
//				assertTrue(pos.isCanceled());
//			} catch (InventoryException ex) {
//				logger.error(ex.getMessage(), ex);
//				throw ex;
//			}
//
//			// position 3
//			pos = r.getPositions().get(2);
//			pos = posQuery.queryById(pos.getId());
//			logger.info("process position " + pos.toDescriptiveString());
//
//			fromUl = (LOSUnitLoad) pos.getStockUnit().getUnitLoad();
//
//			try {
//				assertTrue(bean.testCanProcess(pos, false, fromUl
//						.getStorageLocation().getName(), pos.getAmount()));
//
//				pos = bean.processPickRequestPosition(pos, false, fromUl
//						.getStorageLocation().getName(), pos.getAmount(), false, false);
//			} catch (InventoryException ex) {
//				logger.error(ex.getMessage(), ex);
//				throw ex;
//			}
//
//			r = bean.loadPickingRequest(r);
//			assertEquals(r.getState(), PickingRequestState.PICKED);
//			r = bean.finishPickingRequest(r, r.getDestination().getName());
//			r = bean.loadPickingRequest(r);
//			assertEquals(PickingRequestState.FINISHED, r.getState());
//
//		} catch (Exception ex) {
//			logger.error(ex.getMessage(), ex);
//			throw ex;
//		}
//	}
//
//	public void testPickFromPickingFirst() {
//		// create StockUnits
//		InventoryProcessFacade inventoryProcessFacade = TestUtilities.beanLocator
//				.getStateless(InventoryProcessFacade.class);
//		try {
//			// ... on a fixed assigned StorageLocation
//			inventoryProcessFacade.doInventoyForStorageLocation(
//					CommonTestTopologyRemote.TESTCLIENT_NUMBER, "T1-1-1-1", "T1-1-1-1", null, null,
//					InventoryTestTopologyRemote.ITEM_A1_NUMBER, InventoryTestTopologyRemote.LOT_N1_A1_NAME,
//					new BigDecimal(100), true, true, true);
//			// ... on a non fixed assigned sl
//			inventoryProcessFacade.doInventoyForStorageLocation(
//					CommonTestTopologyRemote.TESTCLIENT_NUMBER, "T1-1-4-1", "T1-1-4-1",null, null,
//					InventoryTestTopologyRemote.ITEM_A1_NUMBER, InventoryTestTopologyRemote.LOT_N1_A1_NAME,
//					new BigDecimal(500), true, true, true);
//		} catch (FacadeException e) {
//			logger.error(e.getMessage(), e);
//			fail(e.getMessage());
//		}
//		// create Order
//		String clientRef = CommonTestTopologyRemote.TESTCLIENT_NUMBER;
//		String orderRef = "testPickFromFixAssignment";
//		String documentUrl = "";
//		String labelUrl = "";
//		String destination = LocationTestTopologyRemote.SL_WA_TESTCLIENT_NAME;
//
//		OrderPositionTO to = new OrderPositionTO();
//		to.amount = new BigDecimal(50);
//		to.articleRef = InventoryTestTopologyRemote.ITEM_A1_NUMBER;
//		to.batchRef = InventoryTestTopologyRemote.LOT_N1_A1_NAME;
//		to.clientRef = CommonTestTopologyRemote.TESTCLIENT_NUMBER;
//
//		OrderPositionTO to2 = new OrderPositionTO();
//		to2.amount = new BigDecimal(100);
//		to2.articleRef = InventoryTestTopologyRemote.ITEM_A1_NUMBER;
//		to2.batchRef = InventoryTestTopologyRemote.LOT_N1_A1_NAME;
//		to2.clientRef = CommonTestTopologyRemote.TESTCLIENT_NUMBER;
//
//		OrderPositionTO[] positions = new OrderPositionTO[] { to, to2 };
//
//		try {
//			OrderFacade orderFacade = TestUtilities.beanLocator
//					.getStateless(OrderFacade.class);
//			orderFacade.order(clientRef, orderRef, positions, documentUrl,
//					labelUrl, destination);
//
//		} catch (FacadeException e) {
//			// TODO Auto-generated catch block
//			logger.error(e, e);
//			fail(e.getMessage());
//		}
//
//		// solve pickrequests
//		LOSPickRequestQueryRemote pickRequestQueryRemote = TestUtilities.beanLocator
//				.getStateless(LOSPickRequestQueryRemote.class);
//		// List<LOSPickRequest> l =
//		// pickRequestQueryRemote.queryByParentRequest("testPickFromFixAssignment");
//		List<LOSPickRequest> l = pickRequestQueryRemote
//				.queryByOrderReference("testPickFromFixAssignment");
//		if (l == null || l.size() < 1)
//			fail("No pickrequest found");
//		LOSPickRequest r = l.get(0);
//		try {
//			r = bean.loadPickingRequest(r);
//			for (LOSPickRequestPosition p : r.getPositions()) {
//				logger.info("inspect " + p.toDescriptiveString());
//				if (p.getIndex() == 0) {
//					// expect 50 from T1-1-1-1
//					assertTrue(new BigDecimal(50).compareTo(p.getAmount()) == 0);
//					assertEquals("T1-1-1-1", p.getStorageLocation().getName());
//				} else if (p.getIndex() == 1) {
//					// expect 50 from T1-1-1-1
//					assertTrue(new BigDecimal(50).compareTo(p.getAmount()) == 0);
//					assertEquals("T1-1-1-1", p.getStorageLocation().getName());
//				} else if (p.getIndex() == 2) {
//					// expect 50 from T1-1-4-1
//					assertTrue(new BigDecimal(50).compareTo(p.getAmount()) == 0);
//					assertEquals("T1-1-4-1", p.getStorageLocation().getName());
//				} else {
//					fail("unexpected number of positions");
//				}
//				try {
//					bean.testCanProcess(p, false, p.getStorageLocation()
//							.getName(), p.getAmount());
//					bean.processPickRequestPosition(p, false, p
//							.getStorageLocation().getName(),  p
//							.getAmount(), false, false);
//				} catch (PickingExpectedNullException ex) {
//					bean.processPickRequestPositionExpectedNull(p, p
//							.getStorageLocation().getName(),  p
//							.getAmount(), new BigDecimal(0), false, false);
//				}
//			}
//
//			r = bean.finishPickingRequest(r, r.getDestination().getName());
//			assertEquals(PickingRequestState.FINISHED, r.getState());
//
//		} catch (Exception e) {
//			logger.error("testPickFromPickingFirst: " + e.getMessage(), e);
//			fail(e.getMessage());
//		}
//		// cleanup
//		ManageLocationFacade manageLocationFacade = TestUtilities.beanLocator
//				.getStateless(ManageLocationFacade.class);
//		try {
//			// manageLocationFacade.sendUnitLoadToNirwana("T1-1-1-1");
//			manageLocationFacade.sendUnitLoadToNirwana("T1-1-4-1");
//		} catch (FacadeException e) {
//			logger.error(e, e);
//			fail(e.getMessage());
//		}
//	}
//
//	public void testPickProduction() throws Exception {
//		
//		LOSPickRequestQueryRemote pickReqQuery = TestUtilities.beanLocator
//				.getStateless(LOSPickRequestQueryRemote.class);
//		ClientQueryRemote clientQuery = TestUtilities.beanLocator
//				.getStateless(ClientQueryRemote.class);
//		LOSGoodsOutRequestQueryRemote goodsOutQuery = TestUtilities.beanLocator
//				.getStateless(LOSGoodsOutRequestQueryRemote.class);
//
//		try {
//
//			LOSPickRequest r = null;
//			Client c = clientQuery
//					.queryByIdentity(CommonTestTopologyRemote.TESTCLIENT_NUMBER);
//			r = pickReqQuery.queryByOrderReference(c, "PROD 1");
//			assertNotNull(r);
//			assertEquals(r.getClient().getNumber(), CommonTestTopologyRemote.TESTCLIENT_NUMBER);
//
//			r = bean.loadPickingRequest(r);
//			r = bean.accept(r);
//			r = bean.loadPickingRequest(r);
//			assertTrue(r.getState().equals(PickingRequestState.ACCEPTED));
//			LOSStorageLocation dest = bean.getDestination(r);
//			assertNotNull(dest);
//			assertEquals(LocationTestTopologyRemote.SL_PRODUCTION_TESTCLIENT_NAME, dest.getName());
//
//			// position 1 with unexpected null
//			LOSPickRequestPosition pos = r.getPositions().get(0);
//			logger.info("process position " + pos.toDescriptiveString());
//
//			LOSUnitLoad fromUl = (LOSUnitLoad) pos.getStockUnit().getUnitLoad();
//
//			try {
//
//				assertTrue(bean.testCanProcess(pos, false, fromUl
//						.getStorageLocation().getName(), pos.getAmount()));
//				pos = bean.processPickRequestPosition(pos, false, fromUl
//						.getStorageLocation().getName(), pos.getAmount(), false, false);
//			} catch (PickingException ex) {
//				logger.error(ex.getMessage(), ex);
//				throw ex;
//			}
//
//			r = bean.loadPickingRequest(r);
//			r = bean.finishPickingRequest(r, r.getDestination().getName());
//			assertEquals(PickingRequestState.FINISHED, r.getState());
//
//			// Should be finished. Goods out process is bypassed for production
//			assertEquals(r.getState(), PickingRequestState.FINISHED);
//			LOSOrderRequest order = r.getParentRequest();
//			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
//					TemplateQueryWhereToken.OPERATOR_EQUAL, "parentRequest",
//					order);
//			TemplateQuery q = new TemplateQuery();
//			q.addWhereToken(t);
//			q.setBoClass(LOSGoodsOutRequest.class);
//			QueryDetail detail = new QueryDetail(0, Integer.MAX_VALUE);
//			List<LOSGoodsOutRequest> list = goodsOutQuery.queryByTemplate(
//					detail, q);
//			assertNotNull(list);
//			assertTrue(list.size() == 1);
//			assertEquals(LOSGoodsOutRequestState.FINISHED, list.get(0)
//					.getOutState());
//
//		} catch (Exception ex) {
//			logger.error(ex.getMessage(), ex);
//			throw ex;
//		}
//	}
//
//	/**
//	 * Iterates through all pickrequests of second test client and solves them.
//	 * 
//	 * @throws IOException
//	 */
//	public void testPickFacadeMulitClient() throws IOException {
//
//		OrderRequestQueryRemote orderQuery = TestUtilities.beanLocator
//				.getStateless(OrderRequestQueryRemote.class);
//		
//		List<PickingRequestTO> l = bean.getRawPickingRequest();
//		assertNotNull(l);
//		assertTrue(l.size() > 0);
//		assertNotNull(l.get(0));
//		assertNotNull(l.get(0).client);
//
//		for (PickingRequestTO to : l) {
//
//			LOSPickRequest r = null;
//			try {
//				r = bean.loadPickingRequest(to);
//				if (r.getParentRequest().getRequestId().equals("TEST 1")
//						|| r.getParentRequest().getRequestId().equals("TEST 2")) {
//					// OK
//				} else {
//					logger.warn("Skip : " + to.toString());
//					continue;
//				}
//				if (!(r.getClient().getNumber().equals(
//						CommonTestTopologyRemote.TESTMANDANT_NUMBER) || r.getClient()
//						.getNumber().equals(CommonTestTopologyRemote.TESTMANDANT_NUMBER))) {
//					logger.warn("!!!! Not assigned to a test client: "
//							+ r.toDescriptiveString());
//					continue;
//				}
//				logger.info(r.toDescriptiveString());
//			} catch (Throwable e) {
//				// TODO Auto-generated catch block
//				logger.error(e, e);
//				fail(e.getMessage());
//			}
//
//			try {
//				assertNotNull(r);
//				r = bean.loadPickingRequest(r);
//				r = bean.accept(r);
//				r = bean.loadPickingRequest(r);
//				assertTrue(r.getState().equals(PickingRequestState.ACCEPTED));
//				LOSStorageLocation dest = bean.getDestination(r);
//				assertNotNull(dest);
//				assertEquals(LocationTestTopologyRemote.SL_WA_TESTMANDANT_NAME, dest.getName());
//				int p = 0;
//
//				for (LOSPickRequestPosition pos : r.getPositions()) {
//					p++;
//					logger
//							.info("process position "
//									+ pos.toDescriptiveString());
//
//					LOSUnitLoad fromUl = (LOSUnitLoad) pos.getStockUnit().getUnitLoad();
//					try {
//						assertTrue(bean.testCanProcess(pos, false, fromUl
//								.getStorageLocation().getName(), pos
//								.getAmount()));
//						pos = bean.processPickRequestPosition(pos, false,
//								fromUl.getStorageLocation().getName(),
//								pos.getAmount(), false, false);
//					} catch (PickingExpectedNullException ex) {
//						pos = bean.processPickRequestPositionExpectedNull(pos,
//								fromUl.getStorageLocation().getName(),
//								pos.getAmount(), new BigDecimal(0), false, false);
//					}
//
//					r = bean.loadPickingRequest(r);
//
//					assertTrue(r.getState().equals(
//							PickingRequestState.PICKED_PARTIAL)
//							|| r.getState().equals(PickingRequestState.PICKED));
//					if (r.getState().equals(PickingRequestState.PICKED)) {
//						assertTrue(pos.isSolved());
//
//						if (pos.getWithdrawalType().equals(
//								PickingWithdrawalType.TAKE_UNITLOAD)) {
//							r = bean.finishCurrentUnitLoad(r, dest.getName());
//							dest = bean.getDestination(r);
//							assertTrue(dest.getUnitLoads().size() > 0);
//						}
//					}
//					logger.info(pos.toDescriptiveString());
//				}
//				try {
//					bean.finishPickingRequest(r, bean.getDestination(r)
//							.getName());
//				} catch (PickingException ex) {
//					if (ex.getPickingExceptionKey().equals(
//							PickingExceptionKey.UNFINISHED_POSITIONS)) {
//						logger.error(ex.getMessage(), ex);
//						throw ex;
//					}
//				}
//				r = bean.loadPickingRequest(r);
//				LOSOrderRequest order = orderQuery.queryById(r
//						.getParentRequest().getId());
//				assertEquals(LOSOrderRequestState.PICKED, order.getOrderState());
//			} catch (Exception e) {
//				logger.error(e, e);
//				fail(e.getMessage());
//			}
//		}
//
//	}
//
//	public void testPickExpectedNull() {
//		// create StockUnits
//		InventoryProcessFacade inventoryProcessFacade = TestUtilities.beanLocator
//				.getStateless(InventoryProcessFacade.class);
//
//		LOSUnitLoadQueryRemote ulQuery = TestUtilities.beanLocator
//				.getStateless(LOSUnitLoadQueryRemote.class);
//		LOSStorageLocationQueryRemote slQuery = TestUtilities.beanLocator
//				.getStateless(LOSStorageLocationQueryRemote.class);
//		StockUnitQueryRemote suQuery = TestUtilities.beanLocator
//				.getStateless(StockUnitQueryRemote.class);
//
//		try {
//			// ... on a fixed assigned StorageLocation
//			inventoryProcessFacade.doInventoyForStorageLocationFromScratch(
//					CommonTestTopologyRemote.TESTCLIENT_NUMBER, "T1-1-1-1", "T1-1-1-1",null, null,
//					InventoryTestTopologyRemote.ITEM_A1_NUMBER, InventoryTestTopologyRemote.LOT_N1_A1_NAME,
//					new BigDecimal(100));
//			// ... on a non fixed assigned sl
//			inventoryProcessFacade.doInventoyForStorageLocationFromScratch(
//					CommonTestTopologyRemote.TESTCLIENT_NUMBER, "T1-1-4-1", "T1-1-4-1",null,null,
//					InventoryTestTopologyRemote.ITEM_A1_NUMBER, InventoryTestTopologyRemote.LOT_N1_A1_NAME,
//					new BigDecimal(500));
//		} catch (FacadeException e) {
//			// TODO Auto-generated catch block
//			logger.error(e.getMessage(), e);
//			fail(e.getMessage());
//		}
//		// create Order
//		String clientRef = CommonTestTopologyRemote.TESTCLIENT_NUMBER;
//		String orderRef = "testExpectedNull";
//		String documentUrl = "";
//		String labelUrl = "";
//		String destination = LocationTestTopologyRemote.SL_WA_TESTCLIENT_NAME;
//
//		OrderPositionTO to = new OrderPositionTO();
//		to.amount = new BigDecimal(100);
//		to.articleRef = InventoryTestTopologyRemote.ITEM_A1_NUMBER;
//		to.batchRef = InventoryTestTopologyRemote.LOT_N1_A1_NAME;
//		to.clientRef = CommonTestTopologyRemote.TESTCLIENT_NUMBER;
//
//		OrderPositionTO to2 = new OrderPositionTO();
//		to2.amount = new BigDecimal(500);
//		to2.articleRef = InventoryTestTopologyRemote.ITEM_A1_NUMBER;
//		to2.batchRef = InventoryTestTopologyRemote.LOT_N1_A1_NAME;
//		to2.clientRef = CommonTestTopologyRemote.TESTCLIENT_NUMBER;
//
//		OrderPositionTO[] positions = new OrderPositionTO[] { to, to2 };
//
//		try {
//			OrderFacade orderFacade = TestUtilities.beanLocator
//					.getStateless(OrderFacade.class);
//			orderFacade.order(clientRef, orderRef, positions, documentUrl,
//					labelUrl, destination);
//
//		} catch (FacadeException e) {
//			// TODO Auto-generated catch block
//			logger.error(e, e);
//			fail(e.getMessage());
//		}
//
//		// solve pickrequests
//		LOSPickRequestQueryRemote pickRequestQueryRemote = TestUtilities.beanLocator
//				.getStateless(LOSPickRequestQueryRemote.class);
//		// List<LOSPickRequest> l =
//		// pickRequestQueryRemote.queryByParentRequest("testPickFromFixAssignment");
//		List<LOSPickRequest> l = pickRequestQueryRemote
//				.queryByOrderReference("testExpectedNull");
//		if (l == null || l.size() < 1)
//			fail("No pickrequest found");
//		LOSPickRequest r = l.get(0);
//		try {
//			r = bean.loadPickingRequest(r);
//			logger.info("PickRequest.User=" + r.toDescriptiveString());
//			for (LOSPickRequestPosition p : r.getPositions()) {
//				logger.info("inspect " + p.getIndex() + " of " + p.getParentRequest().toUniqueString());
//				if ("T1-1-1-1".equals(p.getStorageLocation().getName())) {
//					// expect 100 from T1-1-1-1
//					assertTrue(new BigDecimal(100).compareTo(p.getAmount()) == 0);
//					assertEquals("T1-1-1-1", p.getStorageLocation().getName());
//					try {
//						bean.testCanProcess(p, false, p.getStorageLocation()
//								.getName(), p.getAmount());
//						fail("Expected PickingExpectedNullException that didn't come");
//					} catch (PickingExpectedNullException ex) {
//						// ok
//						StockUnit su = p.getStockUnit();
//						bean.processPickRequestPositionExpectedNull(p, p
//								.getStorageLocation().getName(),  p
//								.getAmount(), new BigDecimal(0), false, false);
//						su = suQuery.queryById(su.getId());
//
//						assertEquals(100, su.getLock());
//						LOSStorageLocation sl = slQuery
//								.queryByIdentity("T1-1-1-1");
//						// should still have an empty Unitload
//						assertEquals(1, sl.getUnitLoads().size());
//						LOSUnitLoad ul = sl.getUnitLoads().get(0);
//						ul = ulQuery.queryById(ul.getId());
//						assertEquals(0, ul.getStockUnitList().size());
//					}
//				} else if (("T1-1-4-1".equals(p.getStorageLocation().getName()))) {
//					// expect 500 from T1-1-4-1
//					assertTrue(new BigDecimal(500).compareTo(p.getAmount()) == 0);
//					assertEquals("T1-1-4-1", p.getStorageLocation().getName());
//					try {
//						bean.testCanProcess(p, false, p.getStorageLocation()
//								.getName(), p.getAmount());
//						if (p.getWithdrawalType().equals(
//								PickingWithdrawalType.TAKE_UNITLOAD)) {
//							// ok
//							bean.setPickRequestPositionTakeUnitLoad(p, false);
//							bean.testCanProcess(p, false, p
//									.getStorageLocation().getName(),  p
//									.getAmount());
//						} else {
//							fail("Expected PickingExpectedNullException that didn't come");
//						}
//					} catch (PickingExpectedNullException ex) {
//						// ok
//						StockUnit su = p.getStockUnit();
//						bean.processPickRequestPositionExpectedNull(p, p
//								.getStorageLocation().getName(),  p
//								.getAmount(), new BigDecimal(100), false, false);
//						su = suQuery.queryById(su.getId());
//						assertTrue(new BigDecimal(100).compareTo(su.getAvailableAmount()) == 0);
//
//						LOSStorageLocation sl = slQuery
//								.queryByIdentity("T1-1-4-1");
//						// should have an Unitload
//						assertEquals(1, sl.getUnitLoads().size());
//						// UnitLoad transferred to?
//						LOSUnitLoad ul = (LOSUnitLoad) su.getUnitLoad();
//						ul = ulQuery.queryById(ul.getId());
//						// assertEquals(0, ul.getStockUnitList().size());
//						// assertEquals(TopologyBean.SL_NIRWANA_NAME,
//						// ul.getStorageLocation().getName());
//						assertEquals(1, ul.getStockUnitList().size());
//						assertEquals("T1-1-4-1", ul.getStorageLocation()
//								.getName());
//					}
//				} else {
//					fail("unexpected number of positions");
//				}
//
//			}
//
//			r = bean.finishPickingRequest(r, r.getDestination().getName());
//			assertEquals(PickingRequestState.FINISHED, r.getState());
//
//		} catch (Exception e) {
//			logger.error("testPickExpectedNull: " + e.getMessage(), e);
//			fail(e.getMessage());
//		}
//
//		// Now the last 100
//		// create Order
//		clientRef = CommonTestTopologyRemote.TESTCLIENT_NUMBER;
//		orderRef = "testExpectedNullUnitLoadGone";
//		documentUrl = "";
//		labelUrl = "";
//		destination = LocationTestTopologyRemote.SL_WA_TESTCLIENT_NAME;
//
//		to2 = new OrderPositionTO();
//		to2.amount = new BigDecimal(100);
//		to2.articleRef = InventoryTestTopologyRemote.ITEM_A1_NUMBER;
//		to2.batchRef = InventoryTestTopologyRemote.LOT_N1_A1_NAME;
//		to2.clientRef = CommonTestTopologyRemote.TESTCLIENT_NUMBER;
//
//		positions = new OrderPositionTO[] { to2 };
//
//		try {
//			OrderFacade orderFacade = TestUtilities.beanLocator
//					.getStateless(OrderFacade.class);
//			orderFacade.order(clientRef, orderRef, positions, documentUrl,
//					labelUrl, destination);
//
//		} catch (FacadeException e) {
//			// TODO Auto-generated catch block
//			logger.error(e, e);
//			fail(e.getMessage());
//		}
//
//		// solve pickrequests
//		pickRequestQueryRemote = TestUtilities.beanLocator
//				.getStateless(LOSPickRequestQueryRemote.class);
//		// List<LOSPickRequest> l =
//		// pickRequestQueryRemote.queryByParentRequest("testPickFromFixAssignment");
//		l = pickRequestQueryRemote
//				.queryByOrderReference("testExpectedNullUnitLoadGone");
//		if (l == null || l.size() < 1)
//			fail("No pickrequest found");
//		r = l.get(0);
//		try {
//			r = bean.loadPickingRequest(r);
//			for (LOSPickRequestPosition p : r.getPositions()) {
//				logger.info("inspect " + p.toDescriptiveString());
//				if (("T1-1-4-1".equals(p.getStorageLocation().getName()))) {
//					// expect 500 from T1-1-4-1
//					assertTrue(new BigDecimal(100).compareTo(p.getAmount()) == 0);
//					assertEquals("T1-1-4-1", p.getStorageLocation().getName());
//					try {
//						bean.testCanProcess(p, false, p.getStorageLocation()
//								.getName(),  p.getAmount());
//						if (p.getWithdrawalType().equals(
//								PickingWithdrawalType.TAKE_UNITLOAD)) {
//							// ok
//							bean.setPickRequestPositionTakeUnitLoad(p, false);
//							bean.testCanProcess(p, false, p
//									.getStorageLocation().getName(), p
//									.getAmount());
//						} else {
//							fail("Expected PickingExpectedNullException that didn't come");
//						}
//					} catch (PickingExpectedNullException ex) {
//						// ok
//						StockUnit su = p.getStockUnit();
//						bean.processPickRequestPositionExpectedNull(p, p
//								.getStorageLocation().getName(),  p
//								.getAmount(), new BigDecimal(0), false, false);
//						su = suQuery.queryById(su.getId());
//						assertTrue(new BigDecimal(100).compareTo(su.getAvailableAmount()) == 0);
//
//						LOSStorageLocation sl = slQuery
//								.queryByIdentity("T1-1-4-1");
//						// should not have an Unitload
//						assertEquals(0, sl.getUnitLoads().size());
//						// UnitLoad transferred to Nirwana?
//						LOSUnitLoad ul = (LOSUnitLoad) su.getUnitLoad();
//						ul = ulQuery.queryById(ul.getId());
//					}
//				} else {
//					fail("unexpected number of positions");
//				}
//
//				r = bean.finishPickingRequest(r, r.getDestination().getName());
//				assertEquals(PickingRequestState.FINISHED, r.getState());
//			}
//		} catch (Exception e) {
//			logger.error(e, e);
//			fail(e.getMessage());
//		}
//		// cleanup
//		ManageLocationFacade manageLocationFacade = TestUtilities.beanLocator
//				.getStateless(ManageLocationFacade.class);
//		try {
//			// manageLocationFacade.sendUnitLoadToNirwana("T1-1-1-1");
//			manageLocationFacade.sendUnitLoadToNirwana("T1-1-4-1");
//		} catch (BusinessObjectNotFoundException e) {
//			return;
//		} catch (FacadeException e) {
//			logger.error(e, e);
//			fail(e.getMessage());
//		}
//		fail("The unitLoad should have got an other name");
//	}

}
