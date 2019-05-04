/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.stocktaking.component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.globals.SerialNoRecordType;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;
import org.mywms.model.UnitLoadType;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.common.service.QueryClientService;
import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.entityservice.BusinessObjectLockState;
import de.linogistix.los.inventory.businessservice.LOSInventoryComponent;
import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.inventory.service.LOSStockUnitRecordService;
import de.linogistix.los.inventory.service.QueryItemDataService;
import de.linogistix.los.inventory.service.QueryLotService;
import de.linogistix.los.inventory.service.StockUnitLockState;
import de.linogistix.los.location.businessservice.LOSStorage;
import de.linogistix.los.location.constants.LOSUnitLoadLockState;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.entityservice.LOSUnitLoadService;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.service.QueryStorageLocationService;
import de.linogistix.los.location.service.QueryTypeCapacityConstraintService;
import de.linogistix.los.location.service.QueryUnitLoadService;
import de.linogistix.los.location.service.QueryUnitLoadTypeService;
import de.linogistix.los.stocktaking.customization.LOSManageStocktakingService;
import de.linogistix.los.stocktaking.exception.LOSStockTakingException;
import de.linogistix.los.stocktaking.exception.LOSStockTakingExceptionKey;
import de.linogistix.los.stocktaking.model.LOSStockTaking;
import de.linogistix.los.stocktaking.model.LOSStockTakingType;
import de.linogistix.los.stocktaking.model.LOSStocktakingOrder;
import de.linogistix.los.stocktaking.model.LOSStocktakingRecord;
import de.linogistix.los.stocktaking.model.LOSStocktakingState;
import de.linogistix.los.stocktaking.service.QueryStockTakingOrderService;
import de.linogistix.los.util.businessservice.ContextService;

@Stateless
public class LOSStockTakingProcessCompBean implements LOSStockTakingProcessComp {

	private static final Logger log = Logger
			.getLogger(LOSStockTakingProcessComp.class);
	private static final int STOCKTAKING_LOCK_NO = 7;

	@EJB
	private QueryStorageLocationService queryLocService;

	@EJB
	private QueryStockTakingOrderService queryStOrderService;

	@EJB
	private LOSStorage storage;

	@EJB
	private LOSInventoryComponent invComp;

	@EJB
	private QueryUnitLoadService queryUlService;

	@EJB
	private QueryItemDataService queryItemService;

	@EJB
	private QueryLotService queryLotService;

	@EJB
	private QueryUnitLoadTypeService queryUlTypeService;
	
	@EJB
	private QueryTypeCapacityConstraintService queryTccService;

	@EJB
	private QueryClientService queryClientService;

	@EJB
	private InventoryGeneratorService seqService;

	@EJB
	private LOSStorageLocationService slService;

	@EJB
	private ContextService ctxService;

	@EJB
	private LOSManageStocktakingService manageStocktakingService;
	
	@EJB
	private LOSUnitLoadService ulService;
	
	@EJB
	private LOSStockUnitRecordService recordService;

	@EJB
	private EntityGenerator entityGenerator;
	
	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @seede.linogistix.los.stocktaking.component.LOSStockTakingProcessComp#
	 * startNewStockTaking
	 * (de.linogistix.los.stocktaking.model.LOSStockTakingType)
	 */
	public LOSStockTaking startNewStockTaking(LOSStockTakingType type) {

		LOSStockTaking st = entityGenerator.generateEntity( LOSStockTaking.class );
		
		String stNo = seqService.generateStocktakingNumber();

		st.setStockTakingNumber(stNo);
		st.setStockTakingType(type);
		st.setStarted(Calendar.getInstance().getTime());

		manager.persist(st);

		if (type == LOSStockTakingType.END_OF_PERIOD_INVENTORY) {

			List<LOSStorageLocation> slList = queryLocService.getListForStorage();

			int i = 0;
			for (LOSStorageLocation sl : slList) {
				
				createStockTakingOrder(st, sl);
				
				if ((i++)%30 == 0){
					manager.flush();
					manager.clear();
				}

			}

		}

		return st;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.linogistix.los.stocktaking.component.LOSStockTakingProcessComp#
	 * createStockTakingOrder
	 * (de.linogistix.los.stocktaking.model.LOSStockTaking,
	 * de.linogistix.los.location.model.LOSStorageLocation)
	 */
	public LOSStocktakingOrder createStockTakingOrder(LOSStockTaking st,
			LOSStorageLocation sl) {

		List<LOSStocktakingOrder> orders = null;
		try {
			orders = queryStOrderService.getListByLocationAndState(sl.getName(), LOSStocktakingState.CREATED, LOSStocktakingState.COUNTED, LOSStocktakingState.FREE, LOSStocktakingState.STARTED);
		} catch (UnAuthorizedException e) {
		}
		
		if( orders == null || orders.size() == 0 ) {
			LOSStocktakingOrder so = entityGenerator.generateEntity( LOSStocktakingOrder.class );			
			so.setState(LOSStocktakingState.CREATED);
			so.setLocationName(sl.getName());
			so.setStockTaking(st);
	
			manager.persist(so);
			log.info("Created LOSStocktakingOrder for " + sl.toUniqueString());
			return so;
		}

		
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.linogistix.los.stocktaking.component.LOSStockTakingProcessComp#
	 * processLocationCountedEmpty
	 * (de.linogistix.los.location.model.LOSStorageLocation)
	 */
	public void processLocationEmpty(LOSStorageLocation sl)
			throws UnAuthorizedException, LOSStockTakingException {

		String operator = null;
		List<LOSStocktakingOrder> soList;
		soList = queryStOrderService.getListByLocationAndState(sl.getName(),
				LOSStocktakingState.CREATED, LOSStocktakingState.STARTED);

		if (soList.size() == 0) {

			throw new LOSStockTakingException(
					LOSStockTakingExceptionKey.NO_ACTIVE_STOCKTAKING_ORDER,
					new Object[] { sl.getName() });
		}

		// avoid lazily initialize exception
		sl = manager.find(LOSStorageLocation.class, sl.getId());

		List<LOSUnitLoad> ulList = sl.getUnitLoads();

		// if there are no unit loads booked on the location
		if (ulList.size() == 0) {

			for (LOSStocktakingOrder so : soList) {

				LOSStocktakingRecord rec = entityGenerator.generateEntity( LOSStocktakingRecord.class );				
				rec.setStocktakingOrder(so);
				rec.setState(LOSStocktakingState.FINISHED);
				rec.setClientNo(queryClientService.getSystemClient().getNumber());
				rec.setItemNo("");
				rec.setLotNo("");
				rec.setSerialNo("");
				rec.setUnitLoadLabel("");
				rec.setLocationName(sl.getName());
				rec.setPlannedQuantity(BigDecimal.ZERO);
				rec.setCountedQuantity(BigDecimal.ZERO);

				manager.persist(rec);

				so.setState(LOSStocktakingState.FINISHED);
				so.setCountingDate(Calendar.getInstance().getTime());
				so.setOperator(ctxService.getCallerUserName());

				manager.merge(so);

				sl.setStockTakingDate(Calendar.getInstance().getTime());
				sl.setLock(0);
				recordService.recordCounting(null, null, sl, "ST "+so.getId().toString(), null, so.getOperator());

			}
		} else {

			List<LOSUnitLoad> emptyUlList = new ArrayList<LOSUnitLoad>();
			List<LOSUnitLoad> missingUlList = new ArrayList<LOSUnitLoad>();

			for (LOSUnitLoad ul : ulList) {

				// if there are stocks booked on the unit load
				if (ul.getStockUnitList().size() > 0) {

					missingUlList.add(ul);
				}
				// if it is an empty unit load
				else {
					emptyUlList.add(ul);
				}
			}


			// if we have only empty unit loads booked
			if (ulList.size() == emptyUlList.size()) {

				// location is correctly counted empty
				for (LOSStocktakingOrder so : soList) {
					operator = so.getOperator();
					
					// we will mark location as correctly counted empty
					LOSStocktakingRecord rec = entityGenerator.generateEntity( LOSStocktakingRecord.class );					
					rec.setStocktakingOrder(so);
					rec.setState(LOSStocktakingState.FINISHED);
					rec.setClientNo(queryClientService.getSystemClient().getNumber());
					rec.setItemNo("");
					rec.setLotNo("");
					rec.setSerialNo("");
					rec.setUnitLoadLabel("");
					rec.setLocationName(sl.getName());
					rec.setPlannedQuantity(BigDecimal.ZERO);
					rec.setCountedQuantity(BigDecimal.ZERO);

					manager.persist(rec);

					so.setState(LOSStocktakingState.FINISHED);
					so.setCountingDate(Calendar.getInstance().getTime());
					so.setOperator(ctxService.getCallersUser().getName());

					manager.merge(so);

					sl.setStockTakingDate(Calendar.getInstance().getTime());
					sl.setLock(0);

				}
			} else {

				for (LOSStocktakingOrder so : soList) {
					operator = so.getOperator();

					for (LOSUnitLoad ul : missingUlList) {

						for (StockUnit su : ul.getStockUnitList()) {

							// create a record for every missing stock
							// with plannedQuantity = stock.amount and
							// countedQuantity = 0

							LOSStocktakingRecord rec = entityGenerator.generateEntity( LOSStocktakingRecord.class );							
							rec.setStocktakingOrder(so);
							rec.setState(LOSStocktakingState.COUNTED);
							rec.setClientNo(queryClientService
									.getSystemClient().getNumber());
							rec.setItemNo(su.getItemData().getNumber());
//							rec.setLotNo(su.getLot() == null ? "---" : su.getLot().getName());
//							rec.setSerialNo("---");
							rec.setLotNo(su.getLot() == null ? "" : su.getLot().getName());
							rec.setSerialNo("");
							rec.setUnitLoadLabel(ul.getLabelId());
							rec.setLocationName(sl.getName());
							rec.setPlannedQuantity(su.getAmount());
							rec.setCountedQuantity(BigDecimal.ZERO);
							rec.setCountedStockId(su.getId());

							manager.persist(rec);
						}
					}

					so.setState(LOSStocktakingState.COUNTED);
					so.setOperator(ctxService.getCallersUser().getName());
					manager.merge(so);
				}
			}

			// send empty unit loads to Nirwana
			for (LOSUnitLoad ul : emptyUlList) {
				try {
					storage.sendToNirwana(operator, ul);
				} catch (FacadeException e) {
					throw new LOSStockTakingException(
							LOSStockTakingExceptionKey.ERROR_DELETING_EMPTY_UNITLOAD,
							new Object[] { ul.getLabelId() });
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.linogistix.los.stocktaking.component.LOSStockTakingProcessComp#
	 * startCountingLocation
	 * (de.linogistix.los.location.model.LOSStorageLocation)
	 */
	public void processLocationStart(LOSStorageLocation sl)
			throws LOSStockTakingException, UnAuthorizedException {
		String logStr = "processLocationStart ";
		sl = manager.find(LOSStorageLocation.class, sl.getId());

		checkStocktakingAllowed(sl);
		
		List<LOSStocktakingOrder> soList;
		soList = queryStOrderService.getListByLocationAndState(sl.getName(),
				LOSStocktakingState.CREATED, LOSStocktakingState.STARTED);

		if (soList.size() == 0) {

			throw new LOSStockTakingException(
					LOSStockTakingExceptionKey.NO_ACTIVE_STOCKTAKING_ORDER,
					new Object[] { sl.getName() });
		}

		sl.setLock(STOCKTAKING_LOCK_NO);

		for (LOSStocktakingOrder so : soList) {

			// 29.10.2012, krane, do not allow usage of stocktaking order if it is already started by different user
			if( so.getState() == LOSStocktakingState.STARTED && so.getOperator()!=null && !so.getOperator().equals(ctxService.getCallerUserName()) ) {
				if( ! manageStocktakingService.isAllowUseStartedOrder() ) {

					log.info(logStr+"It is not allowed to start a started order with a new user. location="+sl.getName());
					throw new LOSStockTakingException(LOSStockTakingExceptionKey.ORDER_ALREADY_STARTED, new Object[] { so.getOperator() });
				}
			}
			so.setState(LOSStocktakingState.STARTED);
			so.setCountingDate(Calendar.getInstance().getTime());
			so.setOperator(ctxService.getCallerUserName());

			manager.merge(so);
		}
		
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @seede.linogistix.los.stocktaking.component.LOSStockTakingProcessComp#
	 * cancelCountingLocation
	 * (de.linogistix.los.location.model.LOSStorageLocation)
	 */
	public void processLocationCancel(LOSStorageLocation sl)
			throws LOSStockTakingException, UnAuthorizedException {
		String logStr = "processLocationCancel ";
		sl = manager.find(LOSStorageLocation.class, sl.getId());

		
		List<LOSStocktakingOrder> soList;
		soList = queryStOrderService.getListByLocationAndState(sl.getName(),
				LOSStocktakingState.STARTED);

		if (soList.size() == 0) {
			// 29.10.2012 krane, Do not fail on cancellation of no more existing orders
			log.info(logStr+"Will not cancel none existing order- location="+sl.getName());
			return;
//			throw new LOSStockTakingException(
//					LOSStockTakingExceptionKey.NO_ACTIVE_STOCKTAKING_ORDER,
//					new Object[] { sl.getName() });
		}
		
		
		if(sl.getLock() == STOCKTAKING_LOCK_NO) {
			sl.setLock(0);
		}
		
		for (LOSStocktakingOrder so : soList) {
			so.setState(LOSStocktakingState.CREATED);
			so.setCountingDate(null);
			so.setOperator(null);

			manager.merge(so);
			
			for( LOSStocktakingRecord rec : so.getRecords() ) {
				manager.remove(rec);
			}
		}
		
		
	}

	private void checkStocktakingAllowed( LOSStorageLocation sl ) throws LOSStockTakingException {
		int lock = sl.getLock();
		if( lock != 0 && lock != STOCKTAKING_LOCK_NO ) {
			throw new LOSStockTakingException(LOSStockTakingExceptionKey.LOCATION_LOCKED, new Object[]{sl.getName()});
		}
		for( LOSUnitLoad ul : sl.getUnitLoads() ) {
			lock = ul.getLock();
			if( lock != 0 && lock != STOCKTAKING_LOCK_NO ) {
				throw new LOSStockTakingException(LOSStockTakingExceptionKey.UNITLOAD_LOCKED, new Object[]{ul.getLabelId()});
			}

			for( StockUnit su : ul.getStockUnitList() ) {
				lock = su.getLock();
				if( lock != 0 && lock != STOCKTAKING_LOCK_NO ) {
					throw new LOSStockTakingException(LOSStockTakingExceptionKey.STOCK_LOCKED, new Object[]{ul.getLabelId()});
				}
				if( BigDecimal.ZERO.compareTo(su.getReservedAmount())<0 ) {
					throw new LOSStockTakingException(LOSStockTakingExceptionKey.STOCK_WITH_RESERVATION, new Object[]{ul.getLabelId()});
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.linogistix.los.stocktaking.component.LOSStockTakingProcessComp#
	 * processStockCount(org.mywms.model.StockUnit, java.math.BigDecimal)
	 */
	public void processStockCount(StockUnit su, BigDecimal counted)
			throws LOSStockTakingException, UnAuthorizedException {
		String methodName = "processStockCount ";
		log.debug(methodName+"Start");

		su = manager.find(StockUnit.class, su.getId());

		LOSStorageLocation sl = ((LOSUnitLoad) su.getUnitLoad())
				.getStorageLocation();

		List<LOSStocktakingOrder> soList;
		soList = queryStOrderService.getListByLocationAndState(sl.getName(),
				LOSStocktakingState.CREATED, LOSStocktakingState.STARTED);

		if (soList.size() == 0) {

			throw new LOSStockTakingException(
					LOSStockTakingExceptionKey.NO_ACTIVE_STOCKTAKING_ORDER,
					new Object[] { sl.getName() });
		}
		

		if( su.getItemData().getSerialNoRecordType() == SerialNoRecordType.ALWAYS_RECORD ) {
			if( BigDecimal.ONE.compareTo(counted) != 0 ) {
				log.error("Wrong amount <"+counted+"> for serial-number-material <"+su.getItemData().getNumber()+">");
				throw new LOSStockTakingException(LOSStockTakingExceptionKey.SERIAL_AMOUNT_MISMATCH, new Object[]{});
			}
				
		}

		for (LOSStocktakingOrder so : soList) {

			LOSStocktakingRecord rec = entityGenerator.generateEntity( LOSStocktakingRecord.class );			
			rec.setStocktakingOrder(so);

			rec.setClientNo(su.getClient().getNumber());
			rec.setItemNo(su.getItemData().getNumber());
//			rec.setLotNo(su.getLot() == null ? "---" : su.getLot().getName());
//			rec.setSerialNo("---");
			rec.setLotNo(su.getLot() == null ? "" : su.getLot().getName());
			rec.setSerialNo("");
			rec.setUnitLoadLabel(su.getUnitLoad().getLabelId());
			rec.setLocationName(sl.getName());
			rec.setPlannedQuantity(su.getAmount());
			rec.setCountedQuantity(counted);
			rec.setCountedStockId(su.getId());

			if (su.getAmount().compareTo(counted) == 0) {
				rec.setState(LOSStocktakingState.FINISHED);
			} else {
				rec.setState(LOSStocktakingState.COUNTED);
			}

			manager.persist(rec);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.linogistix.los.stocktaking.component.LOSStockTakingProcessComp#
	 * processUnexpectedStockCount(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.math.BigDecimal)
	 */
	public void processStockCount(String clientNo, String location,
			String unitLoad, String itemNo, String lotNo, String serialNo,
			BigDecimal counted, String ulType) throws LOSStockTakingException,
			UnAuthorizedException {
		String methodName = "processUnexpectedStockCount ";
		log.debug(methodName+"Start");

		List<LOSStocktakingOrder> soList;
		soList = queryStOrderService.getListByLocationAndState(location,
				LOSStocktakingState.CREATED, LOSStocktakingState.STARTED);

		if (soList.size() == 0) {

			throw new LOSStockTakingException(
					LOSStockTakingExceptionKey.NO_ACTIVE_STOCKTAKING_ORDER,
					new Object[] { location });
		}

		ItemData item = queryItemService.getByItemNumber(itemNo);
		if( item.getSerialNoRecordType() == SerialNoRecordType.ALWAYS_RECORD ) {
			if( BigDecimal.ONE.compareTo(counted) != 0 ) {
				log.error("Wrong amount <"+counted+"> for serial-number-material <"+itemNo+">");
				throw new LOSStockTakingException(LOSStockTakingExceptionKey.SERIAL_AMOUNT_MISMATCH, new Object[]{});
			}
				
		}
		
		for (LOSStocktakingOrder so : soList) {

			LOSStocktakingRecord rec = entityGenerator.generateEntity( LOSStocktakingRecord.class );			
			rec.setStocktakingOrder(so);

			if (clientNo == null) {
				
				LOSStorageLocation loc = queryLocService.getByName(so.getLocationName());
				if( loc == null ) {
					rec.setClientNo(queryClientService.getSystemClient().getNumber());
				}
				else {
					rec.setClientNo(loc.getClient().getNumber());
				}
			} else {
				rec.setClientNo(clientNo);
			}
			rec.setItemNo(itemNo);
			rec.setLotNo(lotNo);
			rec.setSerialNo(serialNo);
			rec.setUnitLoadLabel(unitLoad);
			rec.setLocationName(location);
			rec.setPlannedQuantity(BigDecimal.ZERO);
			rec.setCountedQuantity(counted);
			rec.setUlTypeNo(ulType);

			rec.setState(LOSStocktakingState.COUNTED);

			manager.persist(rec);
		}
	}

	public void processUnitloadStart(String location, String unitLoadLabel) throws LOSStockTakingException,
			UnAuthorizedException {
		String methodName = "startUnexpectedUnitLoadCount ";
		log.debug(methodName+"Start");

		if( unitLoadLabel == null ) {
			return;
		}
		
		List<LOSStocktakingOrder> soList;
		soList = queryStOrderService.getListByLocationAndState(location,
				LOSStocktakingState.CREATED, LOSStocktakingState.STARTED);

		if (soList.size() == 0) {

			throw new LOSStockTakingException(
					LOSStockTakingExceptionKey.NO_ACTIVE_STOCKTAKING_ORDER,
					new Object[] { location });
		}

		for (LOSStocktakingOrder so : soList) {
			for( LOSStocktakingRecord rec : so.getRecords() ) {
				if( unitLoadLabel.equals(rec.getUnitLoadLabel() ) ) {
					manager.remove(rec);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.linogistix.los.stocktaking.component.LOSStockTakingProcessComp#
	 * processMissingUnitLoad(de.linogistix.los.location.model.LOSUnitLoad)
	 */
	public void processUnitloadMissing(LOSUnitLoad ul)
			throws UnAuthorizedException, LOSStockTakingException {

		// avoid lazily initialize exception
		ul = manager.find(LOSUnitLoad.class, ul.getId());

		LOSStorageLocation sl = ul.getStorageLocation();

		List<LOSStocktakingOrder> soList;
		soList = queryStOrderService.getListByLocationAndState(sl.getName(),
				LOSStocktakingState.CREATED, LOSStocktakingState.STARTED);

		if (soList.size() == 0) {

			throw new LOSStockTakingException(
					LOSStockTakingExceptionKey.NO_ACTIVE_STOCKTAKING_ORDER,
					new Object[] { sl.getName() });
		}

		for (LOSStocktakingOrder so : soList) {
			for (StockUnit su : ul.getStockUnitList()) {

				// create a record for every missing stock
				// with plannedQuantity = stock.amount and countedQuantity = 0

				LOSStocktakingRecord rec = entityGenerator.generateEntity( LOSStocktakingRecord.class );				
				rec.setStocktakingOrder(so);
				rec.setState(LOSStocktakingState.COUNTED);
//				rec.setClientNo(queryClientService.getSystemClient()
//						.getNumber());
				rec.setClientNo(su.getClient().getNumber());
				rec.setItemNo(su.getItemData().getNumber());
//				rec.setLotNo(su.getLot() == null ? "---" : su.getLot().getName());
//				rec.setSerialNo("---");
				rec.setLotNo(su.getLot() == null ? "" : su.getLot().getName());
				rec.setSerialNo("");
				rec.setUnitLoadLabel(ul.getLabelId());
				rec.setLocationName(sl.getName());
				rec.setPlannedQuantity(su.getAmount());
				rec.setCountedQuantity(BigDecimal.ZERO);
				rec.setCountedStockId(su.getId());

				manager.persist(rec);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.linogistix.los.stocktaking.component.LOSStockTakingProcessComp#
	 * finishCountingLocation
	 * (de.linogistix.los.location.model.LOSStorageLocation)
	 */
	public void processLocationFinish(LOSStorageLocation sl)
			throws UnAuthorizedException, LOSStockTakingException {

		List<LOSStocktakingOrder> soList;
		soList = queryStOrderService.getListByLocationAndState(sl.getName(),
				LOSStocktakingState.CREATED, LOSStocktakingState.STARTED);

		if (soList.size() == 0) {

			throw new LOSStockTakingException(
					LOSStockTakingExceptionKey.NO_ACTIVE_STOCKTAKING_ORDER,
					new Object[] { sl.getName() });
		}

		boolean allFinished = true;
		LOSStocktakingOrder so = manager.find(LOSStocktakingOrder.class, soList
				.get(0).getId());
		List<LOSStocktakingRecord> recList = so.getRecords();

		for (LOSStocktakingRecord rec : recList) {
			if (rec.getState() != LOSStocktakingState.FINISHED) {
				allFinished = false;
				break;
			}
		}

		// Not all finished counting record mean, that everything is counted.
		// There may be empty unit loads. In this case the state will be set to counted
		List<LOSUnitLoad> ulList = ulService.getListEmptyByStorageLocation(sl);
		if( ulList.size()>0 ) {
			log.info("There are empty unit loads on location " + so.getLocationName()  );
			allFinished = false;
		}
		
		if (allFinished) {

			// avoid lazily initialize exception
			sl = manager.find(LOSStorageLocation.class, sl.getId());

			ulList = sl.getUnitLoads();

			for (LOSUnitLoad ul : ulList) {
				ul.setStockTakingDate(Calendar.getInstance().getTime());
				recordService.recordCounting(null, ul, sl, "ST "+so.getId().toString(), null, so.getOperator());
				
				for( StockUnit su : ul.getStockUnitList() ) {
					recordService.recordCounting(su, ul, sl, "ST "+so.getId().toString(), null, so.getOperator());
				}
			}

			sl.setStockTakingDate(Calendar.getInstance().getTime());
			sl.setLock(0);
			recordService.recordCounting(null, null, sl, "ST "+so.getId().toString(), null, so.getOperator());

			for (LOSStocktakingOrder sot : soList) {

				sot.setCountingDate(Calendar.getInstance().getTime());
				sot.setState(LOSStocktakingState.FINISHED);

				manager.merge(sot);
			}

		} else {

			for (LOSStocktakingOrder sot : soList) {

				sot.setCountingDate(Calendar.getInstance().getTime());
				sot.setState(LOSStocktakingState.COUNTED);

				manager.merge(sot);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.linogistix.los.stocktaking.component.LOSStockTakingProcessComp#
	 * acceptStockTakingResult
	 * (de.linogistix.los.location.model.LOSStorageLocation)
	 */
	public void acceptOrder( long orderId )
			throws FacadeException, LOSStockTakingException {
		String methodName = "acceptOrder ";
		log.info(methodName + "Start (" + orderId + ")");
		
		

		LOSStocktakingOrder stOrder = manager.find(LOSStocktakingOrder.class, Long.valueOf(orderId) );
		if( stOrder == null ) {
			log.info(methodName + "StocktakingOrder <" + orderId + "> does not exist");
			throw new LOSStockTakingException(LOSStockTakingExceptionKey.UNKNOWN_LOCATION, new Object[0]);
		}

		if( stOrder.getState() != LOSStocktakingState.COUNTED ) {
			log.info(methodName + "StocktakingOrder <" + orderId + "> not in State COUNTED");
			throw new LOSStockTakingException(LOSStockTakingExceptionKey.STOCKTAKING_ORDER_NOT_COUNTED, new Object[0]);
		}
		
		LOSStorageLocation sl;
		try {
			sl = queryLocService.getByName( stOrder.getLocationName() );
		} catch (UnAuthorizedException e) {
			log.error(methodName + "No Permission to access location " +  stOrder.getLocationName());
			throw new LOSStockTakingException(LOSStockTakingExceptionKey.NOT_AUTHORIZED, new Object[]{});
		}
		if( sl == null ) {
			log.info(methodName + "Location does not exist <" + stOrder.getLocationName() + ">");
			throw new LOSStockTakingException(LOSStockTakingExceptionKey.UNKNOWN_LOCATION, new Object[]{stOrder.getLocationName()});
		}
		sl = manager.find(LOSStorageLocation.class, sl.getId());

		HashMap<String, List<ResolvedStockTakingRecord>> ulLabelRecMap;
		try {
			ulLabelRecMap = sortRecords(stOrder.getRecords(), sl);
		} catch (UnAuthorizedException e) {
			log.error(methodName + "No Permission to sort records on " +  stOrder.getLocationName());
			throw new LOSStockTakingException(LOSStockTakingExceptionKey.NOT_AUTHORIZED, new Object[]{});
		}

		
		// Check first for empty UnitLoads and move them away
		// Possibly a new UnitLoad can only be created on an empty Location
		for (String ulLabel : ulLabelRecMap.keySet()) {
			boolean hasNewStock = false;

			List<ResolvedStockTakingRecord> records = ulLabelRecMap.get(ulLabel);
	
// 			For empty unit loads, there are no counting records. But they should be sent to clearing too
//			if (records.size() == 0){
//				log.info(methodName + "There are no stocktaking records to handle.");
//				throw new LOSStockTakingException(LOSStockTakingExceptionKey.NO_RECORDS, new String[]{ulLabel});
//			}
			
			for (ResolvedStockTakingRecord res : records) {

				if( BigDecimal.ZERO.compareTo(res.countedQuantity) < 0 ) {
					hasNewStock = true;
					break;
				}
			}
			if( hasNewStock ) {
				continue;
			}
				
			LOSUnitLoad ul = null;
			try {
				ul = queryUlService.getByLabelId(ulLabel);
			} catch (UnAuthorizedException e) {
				log.error(methodName + "No Permission to access unitload " + ulLabel);
				throw new LOSStockTakingException(LOSStockTakingExceptionKey.NOT_AUTHORIZED, new Object[]{});
			}

			// No new Stock on the UnitLoad
			if( ul != null ) {
				
				// 05.01.2013, krane, ignore unit loads, which are actually not booked on the counting location
				// They may have been moved by a different counting order
				if( !sl.equals(ul.getStorageLocation()) ) {
					log.warn(methodName+"Ignore unit load on different location. label="+ulLabel+", unit load location="+ul.getStorageLocation()+", counted location="+sl);
					continue;
				}
				
				// Move the UnitLoad to trash
				log.debug(methodName + "Move UL <" + ulLabel + "> to CLEARING");
				LOSStorageLocation clearing = slService.getClearing();
				
				clearing = manager.find(LOSStorageLocation.class, clearing.getId());
				ul = manager.find(LOSUnitLoad.class, ul.getId());
				
				storage.transferUnitLoad(stOrder.getOperator(), clearing, ul, -1, true, "", "ST "+stOrder.getId().toString());
			}
			else {
				log.info(methodName + "UnitLoad should be moved to CLEARING, but there is no UnitLoad");
			}
		}


		// treat every unit load 
		for (String ulLabel : ulLabelRecMap.keySet()) {
			boolean hasNewStock = false;
			
			List<ResolvedStockTakingRecord> records = ulLabelRecMap.get(ulLabel);
			
			if (records.size() == 0){
				// It has already been processed
				log.info(methodName + "There are no stocktaking records to handle.");
				continue;
			}
			
			for (ResolvedStockTakingRecord res : records) {
				if( BigDecimal.ZERO.compareTo(res.countedQuantity) < 0 )
					hasNewStock = true;
				
			}
			
			if( !hasNewStock ) {
				// It has already been processed
				continue;
			}
			log.debug(methodName + "Found new Stock on UnitLoad <" + ulLabel + "> on location <" + sl.getName() + ">");

			LOSUnitLoad ul = null;
			try {
				ul = queryUlService.getByLabelId(ulLabel);
			} catch (UnAuthorizedException e) {
				log.error(methodName + "No Permission to access unitload " +  ulLabel);
				throw new LOSStockTakingException(LOSStockTakingExceptionKey.NOT_AUTHORIZED, new Object[]{});
			}

			// Check the unit load type
			// the creation or transfer of the unit load may only be possible with the correct unit load type
			UnitLoadType ulType = null;
			for(ResolvedStockTakingRecord res:records){
				ulType = res.ulType;
				
				if(ulType != null)
					break;
			}
			if(ulType == null && ul != null ) {
				ulType = ul.getType();
			}
			if(ulType == null){
				throw new LOSStockTakingException(
						LOSStockTakingExceptionKey.NO_UNITLOADTYPE_SET, new Object[0]);
			}
			if( ul != null ) {
				ul.setType(ulType);
			}
			
			if( ul != null && !ul.getStorageLocation().equals(sl) ) {
				// Move the UnitLoad to the correct Location
				log.debug(methodName + "Moving UnitLoad <" + ul.getLabelId() + "> to new location <" + sl.getName() + ">");
				
				sl = manager.find(LOSStorageLocation.class, sl.getId());
				ul = manager.find(LOSUnitLoad.class, ul.getId());
				
				storage.transferUnitLoad(stOrder.getOperator(), sl, ul, -1, true, "", "ST "+stOrder.getId().toString());
			}
			
			if( ul == null ) {
				// Create a new UnitLoad
				log.debug(methodName + "Create new UnitLoad <" + ulLabel + "> on location <" + sl.getName() + ">");
				
				// TODO: What about a service, which is able to create project-specific UnitLoads ???
				
				
				Client ulClient = null;
				for(ResolvedStockTakingRecord res:records){
					ulClient = res.client;
					if(ulClient != null)
						break;
				}
				if( ulClient == null ) {
					ulClient = queryClientService.getSystemClient();
				}
				
				
				ul = entityGenerator.generateEntity( LOSUnitLoad.class );
				ul.setClient(ulClient);
				ul.setLabelId(ulLabel);
				ul.setType(ulType);
				ul.setStorageLocation(sl);
				ul.setStockTakingDate(Calendar.getInstance().getTime());
				
				manager.persist(ul);
				
				sl.setCurrentTypeCapacityConstraint(queryTccService.getByTypes(sl.getType(), ulType));
				

			}
			
			// check the stock
			acceptChangeStockUnit(ul, ulLabelRecMap.get(ulLabel), stOrder);
	
		}

		stOrder.setState(LOSStocktakingState.FINISHED);
		for( LOSStocktakingRecord rec : stOrder.getRecords() ) {
			rec.setState(LOSStocktakingState.FINISHED);
		}

		if( sl != null ) {
			sl = manager.find(LOSStorageLocation.class, sl.getId());
			sl.setStockTakingDate( new Date() );
			sl.setLock(0);
			stOrder.getStockTaking();
			recordService.recordCounting(null, null, sl, "ST "+stOrder.getId().toString(), null, stOrder.getOperator());

			for( LOSUnitLoad ul : sl.getUnitLoads() ) {
				if( !ul.getStorageLocation().equals(sl) ) {
					continue;
				}
				recordService.recordCounting(null, ul, sl, "ST "+stOrder.getId().toString(), null, stOrder.getOperator());
				
				for( StockUnit su : ul.getStockUnitList() ) {
					if( !su.getUnitLoad().equals(ul) ) {
						continue;
					}
					recordService.recordCounting(su, ul, sl, "ST "+stOrder.getId().toString(), null, stOrder.getOperator());
				}
			}
		}
		
		log.info(methodName + "End");
	}

	
	/**
	 * Accept the StocktakingRecords for a UnitLoad.
	 * The original StockUnits are compared to the StocktakingRecords.
	 * No handling of Locations and empty UnitLoads
	 *  
	 * @param ul
	 * @param recordList
	 * @throws FacadeException
	 */
	private void acceptChangeStockUnit(LOSUnitLoad ul, List<ResolvedStockTakingRecord> recordList, LOSStocktakingOrder stOrder) throws FacadeException, LOSStockTakingException {
		String methodName = "acceptChangeStockUnit ";
		log.debug(methodName+"Start");
		
		ul = manager.merge(ul);
		
		log.debug(methodName+"Post Unresolved");
		for (StockUnit su : ul.getStockUnitList()){
			// remove all su not having a ResolvedStockTakingRecord
			boolean found = false;
			for (ResolvedStockTakingRecord res : recordList){
				if (res.countedStockId != null && res.countedStockId.equals(su.getId())){
					found = true;
				}
			}
			if( found == false ) {
				log.debug(methodName+"Found unresolved Stock");
				ResolvedStockTakingRecord resNew = new ResolvedStockTakingRecord();
				resNew.client = su.getClient();
				resNew.countedQuantity = BigDecimal.ZERO;
				resNew.countedStockId = su.getId();
				resNew.item = su.getItemData();
				resNew.lot = su.getLot();
				resNew.serialNumber = su.getSerialNumber();

				recordList.add(resNew);
				
			}
		}
		
		log.debug(methodName+"Post Resolved");
		// For every known StockUnit a StocktakingRecord must have been inserted!!
		for (ResolvedStockTakingRecord res : recordList){
			StockUnit suDiff = null;
			
			for (StockUnit suCheck : ul.getStockUnitList()){
				log.debug(methodName+"Check... " + res.countedStockId + ", " + suCheck.getId());
				
				if (res.countedStockId != null && res.countedStockId.equals(suCheck.getId())){
					suDiff = suCheck;
					checkStockUnit(suCheck, res);
					break;
				}
			}

			
			if (suDiff != null) {
				// change existing
				if( BigDecimal.ZERO.compareTo(res.countedQuantity) >= 0 ) {
					// The stock has been counted with zero quantity. remove it.
					log.debug(methodName+"Stock not counted => Nirvana");
					// The InventoryComponent does not allow to delete!
					// And it is also not possible to send Stock to the Nirvana!!
					// Only empty stocks can be sent to Nirvana. Why? An empty Stock should disappear automatically-
					if( BigDecimal.ZERO.compareTo(suDiff.getAmount()) < 0 ) {
						invComp.changeAmount(suDiff, BigDecimal.ZERO, true, "ST "+stOrder.getId().toString(), null, stOrder.getOperator());
					}
					invComp.sendStockUnitsToNirwana(suDiff, "ST "+stOrder.getId().toString(), stOrder.getOperator());
				}
				else {
					invComp.changeAmount(suDiff, res.countedQuantity, true, "ST "+stOrder.getId().toString(), null, stOrder.getOperator());
				}
			} else{
				// new stock unit
				invComp.createStock(res.client, res.lot, res.item, res.countedQuantity, ul, "ST "+stOrder.getId().toString(), res.serialNumber, stOrder.getOperator(), true);
			}

		}
		
		ul.setStockTakingDate( new Date() );
		log.debug(methodName+"End");
	}

	private void checkStockUnit(StockUnit su, ResolvedStockTakingRecord res) throws LOSStockTakingException{
		if (!su.getItemData().equals(res.item)){
			throw new LOSStockTakingException(LOSStockTakingExceptionKey.ITEM_MISMATCH, new String[]{res.item.toUniqueString(), su.getItemData().toUniqueString()});
		}
		if (su.getLot() != null && res.lot != null && !(su.getLot().equals(res.lot))){
			throw new LOSStockTakingException(LOSStockTakingExceptionKey.LOT_MISMATCH, new String[]{res.lot.toUniqueString(), su.getLot().toUniqueString()});
		}
		if (!su.getClient().equals(res.client)){
			throw new LOSStockTakingException(LOSStockTakingExceptionKey.CLIENT_MISMATCH, new String[]{res.client.toUniqueString(), su.getClient().toUniqueString()});
		}
		
		
	}
	private HashMap<String, List<ResolvedStockTakingRecord>> sortRecords(
			List<LOSStocktakingRecord> recordList, LOSStorageLocation sl)
			throws LOSStockTakingException, UnAuthorizedException {
		// resolve records and sort them by unit load
		HashMap<String, List<ResolvedStockTakingRecord>> ulLabelRecMap;
		ulLabelRecMap = new HashMap<String, List<ResolvedStockTakingRecord>>();


		for (LOSStocktakingRecord rec : recordList) {

			if (!ulLabelRecMap.containsKey(rec.getUnitLoadLabel())) {

				List<ResolvedStockTakingRecord> recList = new ArrayList<ResolvedStockTakingRecord>();
				recList.add(resolveStockRecord(rec));
				ulLabelRecMap.put(rec.getUnitLoadLabel(), recList);

			} else {

				ulLabelRecMap.get(rec.getUnitLoadLabel()).add(
						resolveStockRecord(rec));

			}
		} 
		
		List<LOSUnitLoad> ulList = ulService.getListEmptyByStorageLocation(sl);
		for( LOSUnitLoad ul : ulList ) {

			if (!ulLabelRecMap.containsKey(ul.getLabelId())) {

				List<ResolvedStockTakingRecord> recList = new ArrayList<ResolvedStockTakingRecord>();
				ulLabelRecMap.put(ul.getLabelId(), recList);
			}
		} // sorting finished

		return ulLabelRecMap;
	}

	public void recountOrder( long orderId ) throws LOSStockTakingException {
		String methodName = "recountOrder ";
		log.info(methodName + "(" + orderId + ")");
		
		LOSStocktakingOrder stOrder = manager.find(LOSStocktakingOrder.class, Long.valueOf(orderId) );
		if( stOrder == null ) {
			log.info(methodName + "StocktakingOrder <" + orderId + "> does not exist");
			throw new LOSStockTakingException(LOSStockTakingExceptionKey.UNKNOWN_LOCATION, new Object[0]);
		}

		if( stOrder.getState() != LOSStocktakingState.COUNTED ) {
			log.info(methodName + "StocktakingOrder <" + orderId + "> not in State COUNTED");
			throw new LOSStockTakingException(LOSStockTakingExceptionKey.STOCKTAKING_ORDER_NOT_COUNTED, new Object[0]);
		}
		

		for (LOSStocktakingRecord rec : stOrder.getRecords()) {
			rec.setState(LOSStocktakingState.CANCELLED);
		}

        stOrder.setState(LOSStocktakingState.CANCELLED);

        LOSStocktakingOrder stOrderNew = entityGenerator.generateEntity( LOSStocktakingOrder.class );
        
        stOrderNew.setState(LOSStocktakingState.CREATED);
        stOrderNew.setLocationName(stOrder.getLocationName());
        stOrderNew.setStockTaking(stOrder.getStockTaking());

		manager.persist(stOrderNew);


	}

	public int generateOrders(
			boolean execute, 
			Long clientId, Long areaId, Long zoneId, Long rackId, Long locationId, String locationName, Long itemId, String itemNo, 
			Date invDate, 
			boolean enableEmptyLocations, boolean enableFullLocations ) {
		return generateOrders(execute, clientId, areaId, zoneId, rackId, locationId, locationName, itemId, itemNo, invDate, enableEmptyLocations, enableFullLocations, true, false);
		
	}

	@SuppressWarnings("unchecked")
	public int generateOrders(
					boolean execute, 
					Long clientId, Long areaId, Long zoneId, Long rackId, Long locationId, String locationName, Long itemId, String itemNo, 
					Date invDate, 
					boolean enableEmptyLocations, boolean enableFullLocations, boolean clientModeLocations, boolean clientModeItemData ) {
		String methodName = " ";
		log.debug(methodName+"Start");
		
		if( locationName != null && locationName.length()==0)
			locationName = null;
		
		if( itemNo != null && itemNo.length()==0)
			itemNo = null;
		
		if( !enableEmptyLocations && !enableFullLocations ) {
			return 0;
		}

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT distinct sl FROM " +LOSStorageLocation.class.getSimpleName() + " sl ");
		sb.append( " WHERE sl.lock=0  "); // use only not locked locations
		sb.append( "   AND sl.id>0 "); // disable nirwana

		sb.append( " and not exists( SELECT 1 from " + LOSStocktakingOrder.class.getSimpleName() + " so ");
		sb.append( "  WHERE so.locationName = sl.name and so.state in (:created,:free,:started,:counted) )");
		
		if( locationId != null ) {
			sb.append(" and sl.id=" + locationId );
		}
		if( locationName != null ) {
			sb.append(" and sl.name like :locationName ");
		}
		if( areaId != null ) {
			sb.append(" and sl.area.id=" + areaId );
		}
		if( rackId != null ) {
			sb.append(" and sl.rack.id=" + rackId );
		}
		if( zoneId != null ) {
			sb.append(" and sl.zone.id=" + zoneId );
		}
		if( invDate != null ) {
			sb.append( " and ( stockTakingDate is null or stockTakingDate < :date ) " );
		}
		
		if( clientId != null && clientModeLocations) {
			sb.append( " and sl.client.id=" + clientId );
		}
		if( clientId != null && clientModeItemData) {
			sb.append( " and exists( select 1 from " + StockUnit.class.getSimpleName() + " su ");
			sb.append( ", " + LOSUnitLoad.class.getSimpleName() + " ul ");
			sb.append( "  where su.unitLoad = ul and ul.storageLocation = sl " );
			sb.append(" and su.client.id=" + clientId );
			sb.append( ")" );
		}
		
		if( itemId != null || itemNo != null ) {
			sb.append( " and exists( select 1 from " + StockUnit.class.getSimpleName() + " su ");
			sb.append( ", " + LOSUnitLoad.class.getSimpleName() + " ul ");
			sb.append( "  where su.unitLoad = ul and ul.storageLocation = sl and su.amount>0 " );
			sb.append( "    and not su.lock in (:lockPicked,:lockDeleted,:lockShipped) ");
			sb.append( "    and not ul.lock in (:lockPicked,:lockDeleted,:lockShipped) ");
			if( itemId != null ) {
				sb.append(" and su.itemData.id=" + itemId );
			}
			if( itemNo != null ) {
				sb.append(" and (su.itemData.number like :itemNo or su.itemData.name like :itemNo or su.itemData.description like :itemNo or su.itemData.tradeGroup like :itemNo)" );
			}
			if( clientId != null ) {
				sb.append(" and su.client.id=" + clientId );
			}
			sb.append( ")" );
		}
		if( !enableEmptyLocations ) {
			sb.append( " and exists( select 1 from " + StockUnit.class.getSimpleName() + " su ");
			sb.append( ", " + LOSUnitLoad.class.getSimpleName() + " ul ");
			sb.append( "  where ul.storageLocation = sl and su.unitLoad = ul ) " );
		}
		if( !enableFullLocations ) {
			sb.append( " and not exists( select 1 from " + StockUnit.class.getSimpleName() + " su ");
			sb.append( ", " + LOSUnitLoad.class.getSimpleName() + " ul ");
			sb.append( "  where ul.storageLocation = sl and su.unitLoad = ul ) " );
		}
		log.debug(methodName+ "QUERY: " + sb.toString() );

		Query query = manager.createQuery( sb.toString() );

		query.setParameter("created", LOSStocktakingState.CREATED);
		query.setParameter("free", LOSStocktakingState.FREE);
		query.setParameter("started", LOSStocktakingState.STARTED);
		query.setParameter("counted", LOSStocktakingState.COUNTED);
		
		if( locationName != null ) {
			query.setParameter("locationName", locationName);
		}
		if( itemNo != null ) {
			query.setParameter("itemNo", itemNo);
		}
		if( itemId != null || itemNo != null ) {
			query.setParameter("lockPicked", StockUnitLockState.PICKED_FOR_GOODSOUT.getLock());
			query.setParameter("lockDeleted", BusinessObjectLockState.GOING_TO_DELETE.getLock());
			query.setParameter("lockShipped", LOSUnitLoadLockState.SHIPPED.getLock());
		}
		if( invDate != null ) {
			query.setParameter("date", invDate);
		}

		List<LOSStorageLocation> slList = query.getResultList();
		
		if( !execute ) {
			log.debug(methodName+"End. Found " + slList.size() + " Locations for StocktakingOrders");
			return slList.size();
		}
		
		log.debug(methodName+"Start to generate " + slList.size() + " Orders");
		
		LOSStockTaking st = entityGenerator.generateEntity( LOSStockTaking.class );
		
		String stNo = seqService.generateStocktakingNumber();
		
		LOSStockTakingType type = LOSStockTakingType.END_OF_PERIOD_INVENTORY;
		
		st.setStockTakingNumber(stNo);
		st.setStockTakingType(type);
		st.setStarted(Calendar.getInstance().getTime());

		manager.persist(st);

		int i = 0;
		int numOrders = 0;
		for (LOSStorageLocation sl : slList) {
			
			if( createStockTakingOrder(st, sl) != null ) {
				numOrders++;
			}
			
			if ((i++)%30 == 0){
				manager.flush();
				manager.clear();
			}

		}

		log.debug(methodName+"End. Created " + numOrders + " orders");
		return numOrders;
	}

	private ResolvedStockTakingRecord resolveStockRecord(
			LOSStocktakingRecord rec) throws LOSStockTakingException,
			UnAuthorizedException {
		String methodName = "resolveStockRecord ";

		ResolvedStockTakingRecord resolved = new ResolvedStockTakingRecord();

		resolved.client = queryClientService.getByNumber(rec.getClientNo());

		if (resolved.client == null) {
			log.info(methodName+"Unknown client <"+rec.getClientNo()+">");
			throw new LOSStockTakingException(
					LOSStockTakingExceptionKey.UNKOWN_CLIENT,
					new Object[] { rec.getClientNo() });
		}

		resolved.item = queryItemService.getByItemNumber(resolved.client, rec
				.getItemNo());

		if (resolved.item == null) {
			log.info(methodName+"Unknown ItemData<"+rec.getItemNo()+">");
			throw new LOSStockTakingException(
					LOSStockTakingExceptionKey.UNKOWN_ITEMNUMBER,
					new Object[] { rec.getItemNo() });
		}


		rec.setLotNo( manageStocktakingService.checkLotNo( resolved.client, resolved.item, rec.getLotNo() ) );
		
		resolved.lot = null;
		if (rec.getLotNo() != null && rec.getLotNo().length() > 0) {
			resolved.lot = queryLotService.getByNameAndItemData(rec.getLotNo(), resolved.item);

			if (resolved.lot == null) {
				log.info(methodName+"Unknown Lot <"+rec.getItemNo()+" / "+rec.getLotNo()+">");
				throw new LOSStockTakingException(
						LOSStockTakingExceptionKey.UNKOWN_LOT,
						new Object[] { rec.getLotNo(), rec.getItemNo() });
			}
		}
		if( resolved.item.isLotMandatory() ) {
			if( resolved.lot == null ) {
				log.info(methodName+"Missing lot for item data <"+rec.getItemNo()+">");
				throw new LOSStockTakingException(
						LOSStockTakingExceptionKey.LOT_MISSING, new Object[]{});
			}
		}
		
		resolved.serialNumber = rec.getSerialNo();
		
		if(rec.getUlTypeNo() != null && rec.getUlTypeNo().length() > 0){
			
			resolved.ulType = queryUlTypeService.getByName(rec.getUlTypeNo());
			
			if(resolved.ulType == null){
				throw new LOSStockTakingException(
						LOSStockTakingExceptionKey.UNKOWN_UNITLOADTYPE,
						new Object[] { rec.getUlTypeNo() });
			}
			
		}

		resolved.countedQuantity = rec.getCountedQuantity();

		resolved.countedStockId = rec.getCountedStockId();

		return resolved;

	}

	private class ResolvedStockTakingRecord {

		public Client client;

		public ItemData item;
		
		public UnitLoadType ulType = null;

		public Lot lot;

		public String serialNumber;
		
		public BigDecimal countedQuantity;

		public Long countedStockId;
	}

	
	public void removeOrder( Long orderId ) throws LOSStockTakingException {
		
		LOSStocktakingOrder order = manager.find(LOSStocktakingOrder.class, orderId);
		if( order == null ) {
			throw new LOSStockTakingException(
					LOSStockTakingExceptionKey.STOCKTAKING_ORDER_NOT_FOUND, null);
		}

		LOSStorageLocation sl = null;
		if( order.getLocationName() != null ) {
			try {
				sl = queryLocService.getByName(order.getLocationName());
			} catch (UnAuthorizedException e) {
				throw new LOSStockTakingException(
						LOSStockTakingExceptionKey.NO_PERMISSION, null);
			}
		}

		if(sl != null && sl.getLock() == STOCKTAKING_LOCK_NO) {
			sl.setLock(0);
		}
		
		for( LOSStocktakingRecord rec : order.getRecords() ) {
			try {
				manager.remove(rec);
			}
			catch( Exception e ) {
				log.error("Cannot remove LOSStocktakingRecord " + rec.getId() );
				throw new LOSStockTakingException(
						LOSStockTakingExceptionKey.CANNOT_DELETE, new Object[]{"LOSStocktakingRecord="+rec.getId()});
			}
		}
		try {
			manager.remove(order);
		}
		catch( Exception e ) {
			log.error("Cannot remove LOSStocktakingOrder " + order.getId() );
			throw new LOSStockTakingException(
					LOSStockTakingExceptionKey.CANNOT_DELETE, new Object[]{"LOSStocktakingOrder="+order.getId()});
		}
	}

}
