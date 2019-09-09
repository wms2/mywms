/*
 * TopologyBean.java
 *
 * Created on 12. September 2006, 09:57
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.los.example;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.BasicEntity;
import org.mywms.model.Client;
import org.mywms.model.ItemUnitType;

import de.linogistix.los.crud.BusinessObjectCreationException;
import de.linogistix.los.crud.BusinessObjectExistsException;
import de.linogistix.los.crud.BusinessObjectMergeException;
import de.linogistix.los.crud.BusinessObjectModifiedException;
import de.linogistix.los.inventory.customization.ManageUnitLoadAdviceService;
import de.linogistix.los.inventory.model.LOSUnitLoadAdvice;
import de.linogistix.los.inventory.query.ItemDataQueryRemote;
import de.linogistix.los.inventory.query.ItemUnitQueryRemote;
import de.linogistix.los.inventory.query.LOSAdviceQueryRemote;
import de.linogistix.los.inventory.query.LOSCustomerOrderQueryRemote;
import de.linogistix.los.inventory.query.LOSGoodsOutRequestQueryRemote;
import de.linogistix.los.inventory.query.LOSGoodsReceiptPositionQueryRemote;
import de.linogistix.los.inventory.query.LOSGoodsReceiptQueryRemote;
import de.linogistix.los.inventory.query.LOSPickingOrderQueryRemote;
import de.linogistix.los.inventory.query.LOSStorageRequestQueryRemote;
import de.linogistix.los.inventory.query.LOSUnitLoadAdviceQueryRemote;
import de.linogistix.los.inventory.query.LotQueryRemote;
import de.linogistix.los.inventory.query.StockUnitQueryRemote;
import de.linogistix.los.inventory.service.ItemUnitService;
import de.linogistix.los.inventory.service.LOSOrderStrategyService;
import de.linogistix.los.inventory.service.LOSPickingOrderService;
import de.linogistix.los.inventory.service.LOSStorageStrategyService;
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote;
import de.linogistix.los.query.ClientQueryRemote;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;
import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.delivery.DeliveryOrderLine;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.picking.PickingOrder;
import de.wms2.mywms.picking.PickingOrderLine;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.product.ItemUnit;
import de.wms2.mywms.strategy.FixAssignment;
import de.wms2.mywms.strategy.FixAssignmentEntityService;
import de.wms2.mywms.strategy.OrderStrategy;
import de.wms2.mywms.transport.TransportOrder;

/**
 * Creates an example topology
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless()
public class InventoryTestTopologyBean implements InventoryTestTopologyRemote {

	private static final Logger log = Logger.getLogger(InventoryTestTopologyBean.class);
	// --------------------------------------------------------------------------
	protected Client SYSTEMCLIENT;
	protected Client TESTCLIENT;
	protected Client TESTMANDANT;
	
	protected String TEST_RACK_1;
	
	protected String TEST_RACK_2;
	
	protected ItemData ITEM_A1;
	protected ItemData ITEM_A2;
	protected Lot LOT_N1_A1;
	protected Lot LOT_N2_A2;
		
	protected ItemUnit ITEM_KG;
	
	protected ItemUnit ITEM_G;
	
	protected ItemUnit ITEM_STK;
	
	@EJB
	private ClientQueryRemote clientQuery;
	@EJB
	private LOSStorageLocationQueryRemote slQuery;
	@EJB
	private StockUnitQueryRemote suQuery;
	@EJB
	private FixAssignmentEntityService assService;
	@EJB
	private ItemDataQueryRemote itemQuery;
	@EJB
	private ItemUnitQueryRemote itemUnitQuery;
	@EJB
	private LotQueryRemote lotQuery;
	@EJB
	private LOSPickingOrderQueryRemote pickQuery;
	@EJB
	private LOSPickingOrderService pickService;
	@EJB
	private ItemUnitService itemUnitService;
	@EJB
	private LOSGoodsOutRequestQueryRemote outQuery;
	@EJB
	private LOSCustomerOrderQueryRemote orderReqQuery;
	@EJB
	private LOSStorageRequestQueryRemote storeReqQuery;
	@EJB
	private LOSGoodsReceiptQueryRemote goodsRecQuery;
	@EJB
	private LOSAdviceQueryRemote adQuery;
	@EJB
	private LOSGoodsReceiptPositionQueryRemote goodsRecPosQuery;
	@EJB
	private LOSUnitLoadAdviceQueryRemote ulAdvQueryRemote;
	@EJB
	private ManageUnitLoadAdviceService manageUlAdviceService;
	@EJB
	private LOSStorageStrategyService storageStratService;
	@EJB
	private LOSOrderStrategyService orderStratService;
	
	
	@PersistenceContext(unitName = "myWMS")
	protected EntityManager em;

	/** Creates a new instance of TopologyBean */
	public InventoryTestTopologyBean() {
	}
	
	//---------------------------------------------------
	
	//-----------------------------------------------------------------

	public void create() throws InventoryTopologyException {
		try {
			
			initClient();
			
			createItemUnit();
			createItemData();
			createFixedLocationAssignments();
			createLots();
			
			em.flush();
		} catch (FacadeException ex) {
			log.error(ex, ex);
			throw new InventoryTopologyException();
		}

	}
	
	public void createItemUnit() throws InventoryTopologyException {
		
		// default unit will be created during setup
	
		try {
			ITEM_G = itemUnitQuery.queryByIdentity(ITEM_G_NAME);
		} catch (BusinessObjectNotFoundException ex) {
			ITEM_G = new ItemUnit();
			ITEM_G.setName(ITEM_G_NAME);
			ITEM_G.setBaseFactor(1);
			ITEM_G.setUnitType(ItemUnitType.WEIGHT);
			ITEM_G.setBaseUnit(null);
			em.persist(ITEM_G);
		}
		
		try {
			ITEM_KG = itemUnitQuery.queryByIdentity(ITEM_KG_NAME);
		} catch (BusinessObjectNotFoundException ex) {
			ITEM_KG = new ItemUnit();
			ITEM_KG.setName(ITEM_KG_NAME);
			ITEM_KG.setBaseFactor(1000);
			ITEM_KG.setUnitType(ItemUnitType.WEIGHT);			ITEM_KG.setBaseUnit(ITEM_G);
			em.persist(ITEM_KG);
		}
		
		em.flush();

	}
	public void createItemData() throws InventoryTopologyException {

		ITEM_STK = itemUnitService.getDefault();
		
		try {
			ITEM_A1 = itemQuery.queryByIdentity(ITEM_A1_NUMBER);
		} catch (BusinessObjectNotFoundException ex) {
			ITEM_A1 = new ItemData();
			ITEM_A1.setClient(TESTCLIENT);
			ITEM_A1.setName(ITEM_A1_NUMBER);
			ITEM_A1.setNumber(ITEM_A1_NUMBER);
			ITEM_A1.setItemUnit(ITEM_STK);
			em.persist(ITEM_A1);
		}

		try {
			ITEM_A2 = itemQuery.queryByIdentity(ITEM_A2_NUMBER);
		} catch (BusinessObjectNotFoundException ex) {
			ITEM_A2 = new ItemData();
			ITEM_A2.setClient(TESTCLIENT);
			ITEM_A2.setName(ITEM_A2_NUMBER);
			ITEM_A2.setNumber(ITEM_A2_NUMBER);
			ITEM_A2.setItemUnit(ITEM_STK);
			em.persist(ITEM_A2);
		}
	}

	public void createLots() throws InventoryTopologyException {

		GregorianCalendar nextMonth = (GregorianCalendar) GregorianCalendar
				.getInstance();
		nextMonth.add(GregorianCalendar.MONTH, 1);

		GregorianCalendar today = (GregorianCalendar) GregorianCalendar
				.getInstance();

		GregorianCalendar nextNextMonth = (GregorianCalendar) GregorianCalendar
				.getInstance();
		nextNextMonth.add(GregorianCalendar.MONTH, 2);

		try {
			LOT_N1_A1 = lotQuery.queryByIdentity(LOT_N1_A1_NAME);
		} catch (BusinessObjectNotFoundException ex) {
			LOT_N1_A1 = new Lot();
			LOT_N1_A1.setClient(TESTCLIENT);
			LOT_N1_A1.setName(LOT_N1_A1_NAME);
			LOT_N1_A1.setBestBeforeEnd(nextMonth.getTime());
			LOT_N1_A1.setUseNotBefore(today.getTime());
			LOT_N1_A1.setDate(today.getTime());
			LOT_N1_A1.setItemData(ITEM_A1);
			em.persist(LOT_N1_A1);
		}

		try {
			LOT_N2_A2 = lotQuery.queryByIdentity(LOT_N2_A2_NAME);
		} catch (BusinessObjectNotFoundException ex) {
			LOT_N2_A2 = new Lot();
			LOT_N2_A2.setClient(TESTCLIENT);
			LOT_N2_A2.setName(LOT_N2_A2_NAME);
			LOT_N2_A2.setBestBeforeEnd(nextMonth.getTime());
			LOT_N2_A2.setUseNotBefore(today.getTime());
			LOT_N2_A2.setDate(today.getTime());
			LOT_N2_A2.setItemData(ITEM_A2);
			em.persist(LOT_N2_A2);
		}
	}

	public void createFixedLocationAssignments() throws InventoryTopologyException,
			BusinessObjectExistsException, BusinessObjectCreationException,
			BusinessObjectSecurityException, BusinessObjectNotFoundException,
			BusinessObjectModifiedException, BusinessObjectMergeException {
		
		TEST_RACK_1 = LocationTestTopologyBean.TEST_RACK_1_NAME;
		
		TEST_RACK_2 = LocationTestTopologyBean.TEST_RACK_2_NAME;
		
		for (int x = 1; x < 5; x++) {
			for (int y = 1; y < 4; y++) {
				StorageLocation rl;
				String locName = TEST_RACK_1 + "-1-" + y + "-" + x;
				
				rl = slQuery.queryByIdentity(locName);
							
				FixAssignment ass = assService.readFirstByLocation(rl);
				if(ass == null){
					ass = new FixAssignment();
					ass.setStorageLocation(rl);
					ass.setItemData(ITEM_A1);
					em.persist(ass);
				}
			}
		}
		
		for (int x = 1; x < 3; x++) {
			for (int y = 1; y < 4; y++) {
				StorageLocation rl;
				String locName = TEST_RACK_2 + "-1-" + y + "-" + x;
				
				rl = slQuery.queryByIdentity(locName);
				
				FixAssignment ass = assService.readFirstByLocation(rl);
				if(ass == null){
					ass = new FixAssignment();
					ass.setStorageLocation(rl);
					ass.setItemData(ITEM_A1);
					em.persist(ass);
				}
			}
		}	
	}

	// ------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public void remove(Class clazz) throws InventoryTopologyException {

		try {
			List<BasicEntity> l;
			l = em.createQuery("SELECT o FROM " + clazz.getName() + " o")
					.getResultList();
			for (Iterator<BasicEntity> iter = l.iterator(); iter.hasNext();) {
				BasicEntity element = iter.next();
				element = (BasicEntity) em.find(clazz, element.getId());
				em.remove(element);
			}
			em.flush();

		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new InventoryTopologyException();
		}
	}

	private void initClient() throws InventoryTopologyException{
		try {
			TESTCLIENT = clientQuery.queryByIdentity(CommonTestTopologyRemote.TESTCLIENT_NUMBER);
			TESTCLIENT = em.find(Client.class, TESTCLIENT.getId());
			
			TESTMANDANT = clientQuery.queryByIdentity(CommonTestTopologyRemote.TESTMANDANT_NUMBER);
			TESTMANDANT = em.find(Client.class, TESTMANDANT.getId());
			
		}catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new InventoryTopologyException();
		}		
	}

	public void clear() throws InventoryTopologyException {
		try {
			initClient();

			clearPickingRequests();
			clearOrderRequests();
			clearStorageRequests();
			clearUnitloadAdvices();
			clearOrderStrat();
			
			clearStockUnits();
			
			clearLots();
			clearFixedLocationAssignments();
			clearItemData();
			//clearItemUnit();
			
		} catch (InventoryTopologyException ex) {
			throw ex;
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new InventoryTopologyException();
		}
	}

	public void clearStorageRequests() throws InventoryTopologyException {
		initClient();
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTCLIENT);
			TemplateQueryWhereToken t2 = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTMANDANT);
			t2.setParameterName("client2");t2.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.addWhereToken(t2);
			q.setBoClass(TransportOrder.class);

			List<TransportOrder> l = storeReqQuery.queryByTemplate(d, q);
			for (TransportOrder u : l) {
				u = em.find(TransportOrder.class, u.getId());
				em.remove(u);
			}
			em.flush();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new InventoryTopologyException();
		}
	}

	public void clearOrderRequests() throws InventoryTopologyException {
		initClient();
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTCLIENT);
			TemplateQueryWhereToken t2 = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTMANDANT);
			t2.setParameterName("client2");t2.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.addWhereToken(t2);
			q.setBoClass(DeliveryOrder.class);
			List<DeliveryOrder> l;
			l = orderReqQuery.queryByTemplate(d, q);
			
			
			
			for (DeliveryOrder u : l) {
				u = em.find(DeliveryOrder.class, u.getId());
				Vector<Long> ids = new Vector<Long>();	
				for (DeliveryOrderLine pos : u.getLines()){
					ids.add(pos.getId());
				}
				for (Long id : ids){
					DeliveryOrderLine pos = em.find(DeliveryOrderLine.class, id);
					em.remove(pos);
				}
				
				u.setLines(new ArrayList<DeliveryOrderLine>());
				
				em.remove(u);
				
				
			}
			em.flush();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new InventoryTopologyException();
		}
	}
	
	public void clearUnitloadAdvices() throws InventoryTopologyException {
		initClient();
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTCLIENT);
			TemplateQueryWhereToken t2 = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTMANDANT);
			t2.setParameterName("client2");t2.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.addWhereToken(t2);
			q.setBoClass(LOSUnitLoadAdvice.class);
			List<LOSUnitLoadAdvice> l;
			l = ulAdvQueryRemote.queryByTemplate(d, q);
			
			for (LOSUnitLoadAdvice u : l) {
				u = em.find(LOSUnitLoadAdvice.class, u.getId());
				manageUlAdviceService.deleteUnitLoadAdvice(u);
			}
			
			em.flush();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new InventoryTopologyException();
		}
	}

	public void clearPickingRequests() throws InventoryTopologyException {
		initClient();
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTCLIENT);
			TemplateQueryWhereToken t2 = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTMANDANT);
			t2.setParameterName("client2");t2.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.addWhereToken(t2);
			q.setBoClass(PickingOrder.class);			
			
			List<PickingOrder> l = pickQuery.queryByTemplate(d, q);
			for (PickingOrder u : l) {
				u = em.find(PickingOrder.class, u.getId());
				Vector<Long> ids = new Vector<Long>();	
				for (PickingOrderLine pos : u.getLines()){
					ids.add(pos.getId());
				}
				for (Long id : ids){
					PickingOrderLine pos = em.find(PickingOrderLine.class, id);
					em.remove(pos);
				}
				
				u.setLines(new ArrayList<PickingOrderLine>());

				em.remove(u);
			}
			
			em.flush();
			
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new InventoryTopologyException();
		}

	}

	private void clearStockUnits() throws InventoryTopologyException {

		initClient();
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTCLIENT);
			TemplateQueryWhereToken t2 = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTMANDANT);
			t2.setParameterName("client2");t2.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.addWhereToken(t2);
			q.setBoClass(StockUnit.class);

			List<StockUnit> l = suQuery.queryByTemplate(d, q);
			for (StockUnit rl : l) {
				try {
					rl = suQuery.queryById(rl.getId());
					rl = em.find(StockUnit.class, rl.getId());
					em.remove(rl);
				} catch (Throwable ex) {
					log.error(ex.getMessage(), ex);
				}
			}
			em.flush();
		} catch (Throwable e) {
			log.error(e, e);
			throw new InventoryTopologyException();
		}

	}

	public void clearFixedLocationAssignments() throws InventoryTopologyException,
														BusinessObjectExistsException, BusinessObjectCreationException,
														BusinessObjectSecurityException, BusinessObjectNotFoundException,
														BusinessObjectModifiedException, BusinessObjectMergeException {

		try{
			TEST_RACK_1 = LocationTestTopologyBean.TEST_RACK_1_NAME;
		
			for (int x = 1; x < 5; x++) {
				for (int y = 1; y < 4; y++) {
					StorageLocation rl;
					String locName = TEST_RACK_1 + "-1-" + y + "-" + x;
					
					rl = slQuery.queryByIdentity(locName);
								
					FixAssignment ass = assService.readFirstByLocation(rl);
					
					if(ass != null){
						em.remove(ass);
					}
				}
			}
			
			em.flush();
			
		} catch (BusinessObjectNotFoundException ex){
			log.warn("Skip: " + ex.getMessage());
		}
		
		try{
		
			TEST_RACK_2 = LocationTestTopologyBean.TEST_RACK_2_NAME;
			
			
			
			for (int x = 1; x < 3; x++) {
				for (int y = 1; y < 4; y++) {
					StorageLocation rl;
					String locName = TEST_RACK_2 + "-1-" + y + "-" + x;
					
					rl = slQuery.queryByIdentity(locName);
					
					FixAssignment ass = assService.readFirstByLocation(rl);
					
					if(ass != null){
						em.remove(ass);
					}
				}
			}	
			
			em.flush();
		} catch (BusinessObjectNotFoundException ex){
			log.warn("Skip: " + ex.getMessage());
		}
	}

	public void clearItemData() throws InventoryTopologyException {
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTCLIENT);
			TemplateQueryWhereToken t2 = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTMANDANT);
			t2.setParameterName("client2");t2.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.addWhereToken(t2);
			q.setBoClass(ItemData.class);

			List<ItemData> l = itemQuery.queryByTemplate(d, q);
			for (ItemData u : l) {
				u = em.find(ItemData.class, u.getId());
				em.remove(u);
			}
			em.flush();
		} catch (Throwable e) {
			log.error(e, e);
			throw new InventoryTopologyException();
		}
	}

	public void clearItemUnit() throws InventoryTopologyException {
		
		try {
			ITEM_KG = itemUnitQuery.queryByIdentity(ITEM_KG_NAME);
			ITEM_KG = em.find(ItemUnit.class, ITEM_KG.getId());
			em.remove(ITEM_KG);
			em.flush();
		} catch (BusinessObjectNotFoundException ex) {
			//
		}
		
		try {
			ITEM_G = itemUnitQuery.queryByIdentity(ITEM_G_NAME);
			ITEM_G = em.find(ItemUnit.class, ITEM_G.getId());
			em.remove(ITEM_G);
			em.flush();
		} catch (BusinessObjectNotFoundException ex) {
			//
		}

	}
	
	public void clearLots() throws InventoryTopologyException {
		try {
			QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
			TemplateQueryWhereToken t = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTCLIENT);
			TemplateQueryWhereToken t2 = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "client",
					TESTMANDANT);
			t2.setParameterName("client2");t2.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			TemplateQuery q = new TemplateQuery();
			q.addWhereToken(t);
			q.addWhereToken(t2);
			q.setBoClass(Lot.class);

			List<Lot> l = lotQuery.queryByTemplate(d, q);
			for (Lot u : l) {
				
				u = em.find(Lot.class, u.getId());
				em.remove(u);
			}
			em.flush();
		} catch (Throwable e) {
			log.error(e, e);
			throw new InventoryTopologyException();
		}
	}
	

	private void clearOrderStrat() {
		List<OrderStrategy> strats = orderStratService.getList(TESTCLIENT);
		for( OrderStrategy strat : strats ) {
			em.remove(strat);
		}
		strats = orderStratService.getList(TESTMANDANT);
		for( OrderStrategy strat : strats ) {
			em.remove(strat);
		}
		em.flush();
	}

}
