/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.service.ClientService;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.common.businessservice.HostMsgService;
import de.linogistix.los.entityservice.BusinessObjectLockState;
import de.linogistix.los.inventory.crud.LOSGoodsOutRequestCRUDRemote;
import de.linogistix.los.inventory.crud.LOSGoodsOutRequestPositionCRUDRemote;
import de.linogistix.los.inventory.crud.LOSStorageRequestCRUDRemote;
import de.linogistix.los.inventory.customization.ManageStockService;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.HostMsgStock;
import de.linogistix.los.inventory.query.LOSAdviceQueryRemote;
import de.linogistix.los.inventory.query.LOSGoodsReceiptPositionQueryRemote;
import de.linogistix.los.inventory.query.LOSPickingPositionQueryRemote;
import de.linogistix.los.inventory.query.LOSStorageRequestQueryRemote;
import de.linogistix.los.inventory.service.ItemDataService;
import de.linogistix.los.inventory.service.LOSGoodsOutRequestPositionService;
import de.linogistix.los.inventory.service.LOSLotService;
import de.linogistix.los.inventory.service.LOSPickingPositionService;
import de.linogistix.los.inventory.service.LotLockState;
import de.linogistix.los.inventory.service.StockUnitLockState;
import de.linogistix.los.inventory.service.StockUnitService;
import de.linogistix.los.location.businessservice.LOSStorage;
import de.linogistix.los.location.businessservice.LocationReserver;
import de.linogistix.los.location.crud.LOSUnitLoadCRUDRemote;
import de.linogistix.los.location.customization.CustomLocationService;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.exception.LOSLocationNotSuitableException;
import de.linogistix.los.location.exception.LOSLocationReservedException;
import de.linogistix.los.location.exception.LOSLocationWrongClientException;
import de.linogistix.los.location.query.LOSUnitLoadQueryRemote;
import de.linogistix.los.model.State;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.util.DateHelper;
import de.linogistix.los.util.businessservice.ContextService;
import de.wms2.mywms.inventory.InventoryJournalRecordType;
import de.wms2.mywms.inventory.JournalHandler;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.inventory.StockState;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadEntityService;
import de.wms2.mywms.inventory.UnitLoadPackageType;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.inventory.UnitLoadTypeEntityService;
import de.wms2.mywms.location.LocationType;
import de.wms2.mywms.location.LocationTypeEntityService;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.location.StorageLocationEntityService;
import de.wms2.mywms.picking.PickingOrderLine;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.product.PackagingUnit;
import de.wms2.mywms.strategy.FixAssignment;
import de.wms2.mywms.strategy.FixAssignmentEntityService;

@Stateless
public class LOSInventoryComponentBean implements LOSInventoryComponent {

	private static final Logger log = Logger.getLogger(LOSInventoryComponentBean.class);
	@PersistenceContext(unitName = "myWMS")
	protected EntityManager manager;

	@EJB
	private StockUnitService stockUnitService;

	@EJB
	private LOSUnitLoadQueryRemote uLoadQueryRemote;

	@EJB
	private LOSStorage storage;

	@EJB
	private ContextService contextService;

	@EJB
	private FixAssignmentEntityService fixAssService;

	@EJB
	private LOSPickingPositionService pickPosService;

	@EJB
	private LOSPickingPositionQueryRemote pickPosQuery;

	@EJB
	private LOSGoodsReceiptPositionQueryRemote grPosQueryRemote;

	@EJB
	private LOSAdviceQueryRemote adviceQuery;

	@EJB
	private ClientService clientService;

	@EJB
	private ItemDataService itemDataService;

	@EJB
	private LocationTypeEntityService slTypeService;

	@EJB
	private LOSLotService lotService;

	@EJB
	private LOSUnitLoadCRUDRemote ulCrud;

	@EJB
	private LOSStorageRequestCRUDRemote storageCrud;

	@EJB
	private LOSStorageRequestQueryRemote storageQuery;

	@EJB
	private LOSGoodsOutRequestPositionCRUDRemote outPosCRUD;

	@EJB
	private LOSGoodsOutRequestCRUDRemote outCRUD;

	@EJB
	private LOSGoodsOutRequestPositionService outPosService;
	@EJB
	private CustomLocationService customLocationService;

	@EJB
	private HostMsgService hostService;
	@EJB
	private LocationReserver locationReserver;
	@EJB
	private ManageStockService manageStockService;
	@Inject 
	private JournalHandler journalHandler;
	@Inject
	private UnitLoadEntityService unitLoadService;
	@Inject
	private StorageLocationEntityService locationService;
	@Inject
	private UnitLoadTypeEntityService unitLoadTypeService;

	public StockUnit createStock(Client client, Lot batch, ItemData item, BigDecimal amount, PackagingUnit packagingUnit, UnitLoad unitLoad, String activityCode, String serialNumber) throws FacadeException {
		return createStock(client, batch, item, amount, packagingUnit, unitLoad, activityCode, serialNumber, null, true);
	}
	public StockUnit createStock(Client client, Lot batch, ItemData item, BigDecimal amount, PackagingUnit packagingUnit, UnitLoad unitLoad, String activityCode, String serialNumber, String operator, boolean sendNotify) throws FacadeException {
		if( operator == null ) {
			operator = contextService.getCallerUserName();
		}

		if (amount.compareTo(new BigDecimal(0)) < 0) {
			throw new InventoryException(InventoryExceptionKey.AMOUNT_MUST_BE_GREATER_THAN_ZERO, "");
		}

		if (serialNumber != null) {
			serialNumber = serialNumber.trim();
			if (serialNumber.length() == 0) {
				serialNumber = null;
			}
		}

		if (serialNumber != null) {
			List<StockUnit> list = stockUnitService.getBySerialNumber(item, serialNumber);
			for (StockUnit su : list) {
				if (BigDecimal.ZERO.compareTo(su.getAmount()) < 0) {
					throw new InventoryException(InventoryExceptionKey.SERIAL_ALREADY_EXISTS, serialNumber);
				}
			}
		}
		StockUnit su = stockUnitService.create(client, unitLoad, item, amount);
		su.setLot(batch);
		su.setUnitLoad(unitLoad);
		su.setSerialNumber(serialNumber);
		su.setPackagingUnit(packagingUnit);
		su.setState(unitLoad.getState());
		unitLoad.getStockUnitList().add(su);

		if( batch != null && batch.getBestBeforeEnd()!=null ) {
			su.setStrategyDate(batch.getBestBeforeEnd());
		}
		
		manager.persist(su);

		if( BigDecimal.ZERO.compareTo(amount) != 0 ) {

			recalculateWeightDiff(unitLoad, item, amount);
			
			journalHandler.recordCreation(su, amount, activityCode, operator, null);

			manageStockService.onStockAmountChange(su, BigDecimal.ZERO);
			if( sendNotify ) {
				try {
					hostService.sendMsg( new HostMsgStock( su, amount, operator, InventoryJournalRecordType.CREATED, activityCode) );
				} catch (FacadeException e) {
					// TODO Do not declare so special throws declarations. A throws FacadeException would be enough.
					throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, e.getLocalizedMessage());
				}
			}
		}

		return su;
	}

	public void transferStock(UnitLoad src, UnitLoad dest, String activityCode) throws FacadeException {
		transferStock(src, dest, activityCode, false);
	}

	
	public void transferStock(UnitLoad src, UnitLoad dest, String activityCode, boolean yesReallyDoIt) throws FacadeException {

		List<StockUnit> sus = new ArrayList<StockUnit>();
		sus.addAll(src.getStockUnitList());

		if( !yesReallyDoIt ) {
			boolean destAllowed = true;
	
			for (StockUnit su : sus) {
				if (!testSuiable(su, dest)) {
					destAllowed = false;
					break;
				}
			}
			if( !destAllowed ) {
				log.error("Transfer of stock unit from unit load="+src.getLabelId()+" to unit load="+dest.getLabelId()+" is not allowed");
				throw new InventoryException(InventoryExceptionKey.STOCKUNIT_TRANSFER_NOT_ALLOWED, new String[] { src.getLabelId(), dest.getLabelId() });
			}
		}
		
		// remove from src
		for (StockUnit su : sus) {
			transferStockUnit(su, dest, activityCode, null, null, yesReallyDoIt);
		}
		src.setStockUnitList(new ArrayList<StockUnit>());
	}

	public void consolidate(UnitLoad ul, String activityCode) throws FacadeException {

		HashMap<Lot, StockUnit> lots;
		HashMap<ItemData, StockUnit> is;

		StockUnit existing;

		lots = new HashMap<Lot, StockUnit>();
		is = new HashMap<ItemData, StockUnit>();

		ul = manager.merge(ul);
		List<StockUnit> sus = ul.getStockUnitList();
		if (sus == null || sus.size() < 1) {
			return;
		}

		List<Long> suIds = new ArrayList<Long>();
		for (StockUnit su : sus) {
			if (su.getSerialNumber() != null) {
				log.warn("Won't consolidate because has serialnumber: " + su.toShortString());
			}
			suIds.add(su.getId());
		}
		
		
		for (Long id : suIds) {
			StockUnit su = manager.find(StockUnit.class, id);

			if (UnitLoadPackageType.CONTAINER == ul.getPackageType() ) {
				// OK. 
			}
			else if (su.getItemData().isLotMandatory() && UnitLoadPackageType.OF_SAME_LOT_CONSOLIDATE.equals(ul.getPackageType())) {

				if (su.getLot() == null) {
					throw new InventoryException(InventoryExceptionKey.STOCKUNIT_NO_LOT, su.getId().toString());
				}
				existing = lots.get(su.getLot());
				if (existing != null && !existing.equals(su)) {
					if (existing.getLock() != 0) {
						log.warn("CANNOT CONSOLIDATE existing su is locked " + existing.toShortString());
						continue;
					}

					if (su.getLock() != 0) {
						log.warn("CANNOT CONSOLIDATE to su is locked " + su.toShortString());
						continue;
					}
					// existing = manager.find(StockUnit.class, su.getId());
					combineStock(su, existing, activityCode);
					lots.put(su.getLot(), existing);
				} else {
					lots.put(su.getLot(), su);
				}

			} else if (UnitLoadPackageType.MIXED_CONSOLIDATE.equals(ul.getPackageType()) || UnitLoadPackageType.OF_SAME_ITEMDATA_CONSOLIDATE.equals(ul.getPackageType())
					|| UnitLoadPackageType.OF_SAME_LOT_CONSOLIDATE.equals(ul.getPackageType())) {

				existing = is.get(su.getItemData());
				if (existing != null && !existing.equals(su)) {
					combineStock(su, existing, activityCode);
					is.put(su.getItemData(), existing);
				} else {
					is.put(su.getItemData(), su);
				}
			} else {
				log.warn("noting to do. Type is" + ul.getPackageType());
			}
		}
	}

	public void transferStockFromReserved(StockUnit su, StockUnit dest, BigDecimal amount, String activityCode) throws FacadeException {

		boolean destAllowed = false;

		if (su.isLocked()) {
			throw new InventoryException(InventoryExceptionKey.STOCKUNIT_IS_LOCKED, su.getId() + "-" + su.getLock());
		}
		if (dest.isLocked()) {
			throw new InventoryException(InventoryExceptionKey.STOCKUNIT_IS_LOCKED, dest.getId() + "-" + dest.getLock());
		}

		if (su.getReservedAmount().compareTo(amount) < 0) {
			throw new InventoryException(InventoryExceptionKey.UNSUFFICIENT_AMOUNT, "" + amount);
		}

		BigDecimal amountSourceOld = su.getAmount();
		BigDecimal amountDestOld = dest.getAmount();

		if (su.getLot() != null) {
			if (dest.getLot() != null && su.getLot().equals(dest.getLot())) {
				destAllowed = true;
			} else {
				log.warn("lot mismatch! Stockunit is of " + su.getLot().toUniqueString() + ", destination of " + dest.getLot().toUniqueString());
				destAllowed = false;
			}
		} else if (!su.getItemData().equals(dest.getItemData())) {
			log.warn("itemData mismatch! Stockunit is of " + su.getItemData().toUniqueString() + ", destination of " + dest.getItemData().toUniqueString());
			destAllowed = false;
		} else if (su.getPackagingUnit() != null && dest.getPackagingUnit() != null && !su.getPackagingUnit().equals(dest.getPackagingUnit())) {
			log.warn("Packagin unit mismatch! Stockunit is of " + su.getPackagingUnit() + ", destination of " + dest.getPackagingUnit());
			destAllowed = false;
		} else {
			destAllowed = true;
		}

		if (destAllowed) {
			// transfer amount
			dest.setAmount(dest.getAmount().add(amount));
			su.setAmount(su.getAmount().subtract(amount));
			su.releaseReservedAmount(amount);
			if (dest.getPackagingUnit() == null) {
				dest.setPackagingUnit(su.getPackagingUnit());
			}
			
			su.getUnitLoad().setOpened(true);
			dest.getUnitLoad().setOpened(true);
			
			recalculateWeightDiff(su.getUnitLoad(), su.getItemData(), amount.negate());
			recalculateWeightDiff(dest.getUnitLoad(), dest.getItemData(), amount);

			String operator = contextService.getCallerUserName();

			journalHandler.recordChangeAmount(su.getClient(), su, amount.negate(), activityCode, operator, null);
			journalHandler.recordCreation(dest, amount, activityCode, operator, null);

			manageStockService.onStockAmountChange(su, amountSourceOld);
			manageStockService.onStockAmountChange(dest, amountDestOld);

			if (su.getAmount().compareTo(new BigDecimal(0)) == 0) {
				sendStockUnitsToNirwana(su, activityCode);
			}
		} else {
			throw new InventoryException(InventoryExceptionKey.STOCKUNIT_TRANSFER_NOT_ALLOWED, new String[] { su.toUniqueString(), "" + su.getId() });
		}
	}

	public void transferStock(StockUnit su, StockUnit dest, BigDecimal amount, String activityCode) throws FacadeException {
		String logStr = "transferStock ";
		su = manager.merge(su);
		dest = manager.merge(dest);
		BigDecimal amountSourceOld = su.getAmount();
		BigDecimal amountDestOld = dest.getAmount();
		boolean destAllowed = false;

		if (su.isLocked()) {
			if (su.getLock() != StockUnitLockState.PICKED_FOR_GOODSOUT.getLock() || su.getLock() != StockUnitLockState.UNEXPECTED_NULL.getLock()) {
				// ok
			} else {
				throw new InventoryException(InventoryExceptionKey.STOCKUNIT_IS_LOCKED, su.getId() + "-" + su.getLock());
			}

		}
		if (dest.isLocked()) {
			throw new InventoryException(InventoryExceptionKey.STOCKUNIT_IS_LOCKED, dest.getId() + "-" + dest.getLock());
		}

		if (su.getLot() != null) {
			if (dest.getLot() != null && su.getLot().equals(dest.getLot())) {
				destAllowed = true;
			} else {
				destAllowed = false;
			}
		} else if (su.getItemData().equals(dest.getItemData())) {
			destAllowed = true;
		} else {
			destAllowed = false;
		}

		if (destAllowed) {
			// transfer amount
			dest.setAmount(dest.getAmount().add(amount));
			su.setAmount(su.getAmount().subtract(amount));
			if (dest.getPackagingUnit() == null) {
				dest.setPackagingUnit(su.getPackagingUnit());
			}

			su.getUnitLoad().setOpened(true);
			dest.getUnitLoad().setOpened(true);

			recalculateWeightDiff(su.getUnitLoad(), su.getItemData(), amount.negate());
			recalculateWeightDiff(dest.getUnitLoad(), dest.getItemData(), amount);

			String operator = contextService.getCallerUserName();

			journalHandler.recordChangeAmount(su.getClient(), su, amount.negate(), activityCode, operator, null);
			journalHandler.recordCreation(dest, amount, activityCode, operator, null);

			manageStockService.onStockAmountChange(su, amountSourceOld);
			manageStockService.onStockAmountChange(dest, amountDestOld);
			if (su.getAmount().compareTo(new BigDecimal(0)) == 0) {
				log.info(logStr+"remove empty stock. id="+su.getId()+" ul="+su.getUnitLoad().getLabelId());
				UnitLoad sourceUnitLoad = su.getUnitLoad();
				sendStockUnitsToNirwana(su, activityCode, null);
				
				if( sourceUnitLoad.getStockUnitList() == null || sourceUnitLoad.getStockUnitList().size()==0 ) {
					log.info(logStr+"remove empty unit load. labelId="+sourceUnitLoad.getLabelId());
					storage.sendToNirwana(contextService.getCallerUserName(), sourceUnitLoad);
				}
			}
		} else {
			throw new InventoryException(InventoryExceptionKey.STOCKUNIT_TRANSFER_NOT_ALLOWED, new String[] { su.toUniqueString(), "" + su.getId() });
		}
	}

	public StockUnit splitStock(StockUnit stockToSplit, UnitLoad destUl, BigDecimal takeAwayAmount, String activityCode) throws FacadeException {
		stockToSplit = manager.merge(stockToSplit);

		if (stockToSplit.isLocked()) {
			if (stockToSplit.getLock() != StockUnitLockState.PICKED_FOR_GOODSOUT.getLock() || stockToSplit.getLock() != StockUnitLockState.UNEXPECTED_NULL.getLock()) {
				// ok
			} else {
				throw new InventoryException(InventoryExceptionKey.STOCKUNIT_IS_LOCKED, stockToSplit.getId() + "-" + stockToSplit.getLock());
			}
		}

		if (destUl.isLocked()) {
			throw new InventoryException(InventoryExceptionKey.DESTINATION_UNITLOAD_LOCKED, destUl.getLabelId());
		}

		StockUnit newStock = createStock(stockToSplit.getClient(), stockToSplit.getLot(), stockToSplit.getItemData(), new BigDecimal(0), stockToSplit.getPackagingUnit(), destUl, activityCode, null, null, true);
		transferStock(stockToSplit, newStock, takeAwayAmount, activityCode);
		newStock.setLock(stockToSplit.getLock());

		return newStock;
	}

	public void combineStock(StockUnit su, StockUnit dest, String activityCode) throws FacadeException {
		String logStr = "combineStock ";
		boolean destAllowed = false;

		su = manager.merge(su);
		dest = manager.merge(dest);
		BigDecimal amountSourceOld = su.getAmount();
		BigDecimal amountDestOld = dest.getAmount();

		// 15.11.2012, krane, allow combination of equal locked stocks
		if( su.getLock() != dest.getLock() ) {
			if (su.isLocked()) {
				log.warn(logStr+"Cannot combine. Source stock unit is locked. id="+su.getId()+", lock="+su.getLock());
				throw new InventoryException(InventoryExceptionKey.STOCKUNIT_IS_LOCKED, new Integer[] { su.getLock() });
			}
	
			if (dest.isLocked()) {
				log.warn(logStr+"Cannot combine. Destination stock unit is locked. id="+dest.getId()+", lock="+dest.getLock());
				throw new InventoryException(InventoryExceptionKey.STOCKUNIT_IS_LOCKED, new Integer[] { dest.getLock() });
			}
		}
		
		if (su.getLot() != null) {
			if (dest.getLot() != null && su.getLot().equals(dest.getLot())) {
				destAllowed = true;
			} else {
				destAllowed = false;
			}
		} else if (su.getItemData().equals(dest.getItemData())) {
			destAllowed = true;
		} else {
			destAllowed = false;
		}
		if( su.getState() != dest.getState() ) {
			destAllowed = false;
		}

		if (destAllowed) {
			// check reservations
			if (BigDecimal.ZERO.compareTo(su.getReservedAmount()) < 0) {
				// there seems to a reservation
				// Try all picks to switch them to the new stock unit
				List<PickingOrderLine> pickList = null;
				try {
					pickList = pickPosService.getByPickFromStockUnit(su);
				} catch (Throwable t) {
					// ignore
				}
				if (pickList != null) {
					for (PickingOrderLine pick : pickList) {
						if ( pick.getState() < State.PICKED ) {
							pick.setPickFromStockUnit(dest);
							pick.setPickFromUnitLoadLabel(dest.getUnitLoad().getLabelId());
						}
					}
				}
			}
			// transfer amount
			BigDecimal amount = su.getAmount();
			dest.setAmount(dest.getAmount().add(amount));
			dest.setReservedAmount(dest.getReservedAmount().add(su.getReservedAmount()));

			su.setAmount(new BigDecimal(0));
			su.setReservedAmount(new BigDecimal(0));
			
			su.getUnitLoad().setOpened(true);
			dest.getUnitLoad().setOpened(true);
			
			recalculateWeightDiff(su.getUnitLoad(), su.getItemData(), amount.negate());
			recalculateWeightDiff(dest.getUnitLoad(), dest.getItemData(), amount);

			String operator = contextService.getCallerUserName();

			journalHandler.recordChangeAmount(su.getClient(), su, amount.negate(), activityCode, operator, null);
			journalHandler.recordCreation(dest, amount, activityCode, operator, null);

			manageStockService.onStockAmountChange(su, amountSourceOld);
			manageStockService.onStockAmountChange(dest, amountDestOld);

			sendStockUnitsToNirwana(su, activityCode);
			manager.flush();
		} else {
			throw new InventoryException(InventoryExceptionKey.STOCKUNIT_TRANSFER_NOT_ALLOWED, new String[] { su.toUniqueString(), "" + su.getId() });
		}
	}

	public void sendStockUnitsToNirwana(StockUnit su, String activityCode) throws FacadeException {
		sendStockUnitsToNirwana(su, activityCode, null);
	}

	public void sendStockUnitsToNirwana(StockUnit su, String activityCode, String operator) throws FacadeException {
		UnitLoad ul = unitLoadService.getTrash();

		if (su.getReservedAmount().compareTo(new BigDecimal(0)) > 0) {
			log.error("Cannot be deleted: " + su.toShortString());
			throw new InventoryException(InventoryExceptionKey.STOCKUNIT_HAS_RESERVATION, "" + su.getId());
		}
		changeAmount(su, new BigDecimal(0), true, activityCode);
		transferStockUnit(su, ul, activityCode, operator, null, false);
		su.setLock(BusinessObjectLockState.GOING_TO_DELETE.getLock());
		su.setState(StockState.DELETABLE);
	}

	public void sendStockUnitsToNirwana(StorageLocation sl, String activityCode) throws FacadeException {

		sl = manager.merge(sl);

		for (UnitLoad ul : sl.getUnitLoads()) {
			sendStockUnitsToNirwana(ul, activityCode);
		}

	}

	public void sendStockUnitsToNirwana(UnitLoad ul, String activityCode) throws FacadeException {

		List<Long> sus = new ArrayList<Long>();
		// List<Long> uls = new ArrayList<Long>();

		for (StockUnit su : ul.getStockUnitList()) {
			sus.add(su.getId());
			// su = manager.find(StockUnit.class, su.getId());
			// manager.remove(su);
		}

		for (Long id : sus) {
			StockUnit su = manager.find(StockUnit.class, id);
			if (su == null) {
				continue;
			}
			sendStockUnitsToNirwana(su, activityCode);
		}
		
		sendUnitLoadToNirwanaIfEmpty(ul);
	}

	public void changeAmount(StockUnit su, BigDecimal amount, boolean forceRelease, String activityCode) throws FacadeException {
		changeAmount(su, amount, forceRelease, activityCode, null, null, true);
	}

	public void changeAmount(StockUnit su, BigDecimal amount, boolean forceRelease, String activityCode, String comment) throws FacadeException {
		changeAmount(su, amount, forceRelease, activityCode, comment, null, true);
	}

	public void changeAmount(StockUnit su, BigDecimal amount, boolean forceRelease, String activityCode, String comment, String operator) throws FacadeException {
		changeAmount(su, amount, forceRelease, activityCode, comment, operator, true);
	}
	
	public void changeAmount(StockUnit su, BigDecimal amount, boolean forceRelease, String activityCode, String comment, String operator, boolean sendNotify) throws FacadeException {
		if( operator == null ) {
			operator = contextService.getCallerUserName();
		}
		BigDecimal diffAmount;

		su = manager.merge(su);
		BigDecimal amountOld = su.getAmount();
		
		if (amount.compareTo(new BigDecimal(0)) < 0) {
			throw new IllegalArgumentException("Amount cannot be negative");
		}

		diffAmount = amount.subtract(su.getAmount());

		if (BigDecimal.ZERO.compareTo(diffAmount) == 0) {
			log.info("No need to change amount of StockUnit " + su.toShortString());
		} else if (BigDecimal.ZERO.compareTo(diffAmount) < 0) {
			log.info("GOING TO SET amount of StockUnit " + su.toShortString() + " *** to *** " + amount);
			su.setAmount(amount);
			
			su.getUnitLoad().setOpened(true);
			
			recalculateWeightDiff(su.getUnitLoad(), su.getItemData(), diffAmount);

			journalHandler.recordChangeAmount(su.getClient(), su, diffAmount, activityCode, operator, comment);
			manageStockService.onStockAmountChange(su, amountOld);
			
			if( sendNotify ) {
				try{
					hostService.sendMsg( new HostMsgStock(su, diffAmount, operator, InventoryJournalRecordType.CHANGED, activityCode));
				}
				catch( FacadeException e ) {
					throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, e.getLocalizedMessage());
				}
			}
			
		} else if (amount.compareTo(su.getReservedAmount()) >= 0 || forceRelease) {
			if (forceRelease && amount.compareTo(su.getReservedAmount()) < 0) {
				log.info("GOING TO SET FORCE RELEASED reservedamount of StockUnit " + su.toShortString() + " *** to *** " + amount);
				su.setReservedAmount(amount);
			}
			log.info("GOING TO SET amount of StockUnit " + su.toShortString() + " *** to *** " + amount);
			su.setAmount(amount);
			
			su.getUnitLoad().setOpened(true);
			recalculateWeightDiff(su.getUnitLoad(), su.getItemData(), diffAmount);

			try{
				if (BigDecimal.ZERO.compareTo(amount) < 0) {
					journalHandler.recordChangeAmount(su.getClient(), su, diffAmount, activityCode, operator, comment);
					manageStockService.onStockAmountChange(su, amountOld);
					if( sendNotify ) {
						hostService.sendMsg( new HostMsgStock(su, diffAmount, operator, InventoryJournalRecordType.CHANGED, activityCode));
					}
				} else {
					journalHandler.recordChangeAmount(su.getClient(), su, diffAmount, activityCode, operator, comment);
					manageStockService.onStockAmountChange(su, amountOld);
					if( sendNotify ) {
						hostService.sendMsg( new HostMsgStock(su, diffAmount, operator, InventoryJournalRecordType.REMOVED, activityCode));
					}
				}
			}
			catch( FacadeException e ) {
				throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, e.getLocalizedMessage());
			}

		} else {
			throw new InventoryException(InventoryExceptionKey.STOCKUNIT_HAS_RESERVATION, "");
		}
	}

	public void changeReservedAmount(StockUnit su, BigDecimal reservedAmount, String activityCode) throws InventoryException {
		if (reservedAmount.compareTo(new BigDecimal(0)) < 0) {
			throw new IllegalArgumentException("Amount cannot be negative");
		}

		if (reservedAmount.compareTo(su.getAmount()) <= 0) {
			log.info("GOING TO SET reservedamount of StockUnit " + su.toShortString() + " *** to *** " + reservedAmount);
			su.setReservedAmount(reservedAmount);
		} else {
			throw new InventoryException(InventoryExceptionKey.CANNOT_RESERVE_MORE_THAN_AVAILABLE, "" + su.getAvailableAmount());
		}
	}

	public void transferStockUnit(StockUnit su, UnitLoad dest, String activityCode) throws FacadeException {
		transferStockUnit(su, dest, activityCode, null, null, false);
	}
	public void transferStockUnit(StockUnit su, UnitLoad dest, String activityCode, String comment) throws FacadeException {
		transferStockUnit(su, dest, activityCode, null, null, false);
	}
	public void transferStockUnit(StockUnit su, UnitLoad dest, String activityCode, String comment, String operator) throws FacadeException {
		transferStockUnit(su, dest, activityCode, comment, operator, false);
	}
	public void transferStockUnit(StockUnit su, UnitLoad dest, String activityCode, String comment, String operator, boolean yesReallyDoIt) throws FacadeException {
		if( operator == null ) {
			operator = contextService.getCallerUserName();
		}

		if( !yesReallyDoIt ) {
			boolean destAllowed = false;
			Vector<Long> suIds = new Vector<Long>();
	
			if (testSuiable(su, dest)) {
				destAllowed = true;
				suIds.add(su.getId());
			} else {
				destAllowed = false;
			}
			if( !destAllowed ) {
				throw new InventoryException(InventoryExceptionKey.STOCKUNIT_TRANSFER_NOT_ALLOWED, new String[] { su.toUniqueString(), dest.getLabelId() });
			}
		}
		UnitLoad old = su.getUnitLoad();
		// Do not actualize stock unit list of container unit loads. This will cause problems in parallel access (Nirwana) 
		if( old.getPackageType() != UnitLoadPackageType.CONTAINER ) {
			old.getStockUnitList().remove(su);
		}

		// add to destination
		su.setUnitLoad(dest);
		su.setState(dest.getState());

		// Do not actualize stock unit list of container unit loads. This will cause problems in parallel access (Nirwana) 
		if( dest.getPackageType() != UnitLoadPackageType.CONTAINER ) {
			dest.getStockUnitList().add(su);
		}

		su.getUnitLoad().setOpened(true);
		dest.setOpened(true);
		
		recalculateWeightDiff(old, su.getItemData(), su.getAmount().negate());
		recalculateWeightDiff(dest, su.getItemData(), su.getAmount());

		journalHandler.recordTransferStock(su, old, old.getStorageLocation(), activityCode, operator, null);
		
		switch (dest.getPackageType()) {
		case MIXED_CONSOLIDATE:
		case OF_SAME_ITEMDATA_CONSOLIDATE:
		case OF_SAME_LOT_CONSOLIDATE:
			log.info("Going to consolidate " + dest.getLabelId());
			consolidate(dest, activityCode);
			break;
		default:
			log.info("No consolidation for " + dest.getLabelId());
			break;
		}

	}

	public void sendUnitLoadToNirwanaIfEmpty(UnitLoad ul) throws FacadeException {
		ul = manager.merge(ul);
		
		// Fix by dbruegmann
		// Check if UL is a carrier.
		if (ul.isCarrier()) {
			log.debug("Unitload is carrier, so it still has unit loads on it, and is not empty. unitLoad="+ul.toShortString());
			return;
		}

		if (ul.getStockUnitList() == null || ul.getStockUnitList().size() == 0) {
			log.info("A UnitLoad has become empty: " + ul.toShortString());
			try {
				UnitLoadType type = unitLoadTypeService.getVirtual();
				if (ul.getType().equals(type)) {
					log.debug("Skip: UnitLoad of type " + type.toUniqueString());
				} else {
					// if ul has carrier who is empty now, delete it, too
					sendUnitLoadWithParentsToNirwana(ul);
				}
			} catch (BusinessObjectNotFoundException ex) {
				log.error(ex.getMessage(), ex);
				return;
			}
		} else {
			log.debug("No UnitLoad has become empty");
		}

	}

	/**
	 * Checks whether the unit load has parents which are empty too. If it does
	 * they are deleted, too.
	 * 
	 * @author dbruegmann
	 * @param ul
	 * @throws FacadeException
	 */
	public void sendUnitLoadWithParentsToNirwana(UnitLoad ul)
			throws FacadeException {
		UnitLoad carrierUl = ul.getCarrierUnitLoad();

		/*
		 * If the carrier, has no stock units itself and only one child (the
		 * current unit load) the carrier should be deleted, too.
		 */
//		if (carrierUl != null
//				&& (carrierUl.getStockUnitList() == null || carrierUl
//						.getStockUnitList().size() == 0)
//				&& carrierUl.getUnitLoadList() != null
//				&& carrierUl.getUnitLoadList().size() == 1) {
//			sendUnitLoadWithParentsToNirwana(carrierUl);
//		} else {
//			storage.sendToNirwana(contextService.getCallerUserName(), ul);
//		}
		
		if (carrierUl != null && (carrierUl.getStockUnitList() == null || carrierUl.getStockUnitList().size() == 0)) {
			if( !unitLoadService.hasOtherChilds(carrierUl, ul)) {
				sendUnitLoadWithParentsToNirwana(carrierUl);
				return;
			}
		}
		storage.sendToNirwana(contextService.getCallerUserName(), ul);	
		
	}

	public boolean testSuiable(StockUnit su, UnitLoad ul) {
		boolean ret;

		if (ul.isLocked()) {
			log.warn("UnitLoad is locked: " + ul.toShortString());
			ret = false;
		} else {
			ret = true;
		}

		FixAssignment ass = fixAssService.readFirst(null, ul.getStorageLocation());
		if (ass != null && !ass.getItemData().equals(su.getItemData())) {
			log.warn("ItemData has fixed location assignment but itemdata of stockunit " + su.getItemData().getNumber() + " doesn't match: " + ass.getItemData().getNumber());
			return false;
		}

		switch (ul.getPackageType()) {
		case MIXED:
		case MIXED_CONSOLIDATE:
		case CONTAINER:
			ret = ret && true;
			break;
		case OF_SAME_ITEMDATA:
		case OF_SAME_ITEMDATA_CONSOLIDATE:
			ret = ret && testSameItemData(su, ul);
			break;
		case OF_SAME_LOT:
		case OF_SAME_LOT_CONSOLIDATE:
			ret = ret && testSameLot(su, ul);
			break;
		default:
			log.error("Unknown type: Assuming true for : " + ul.getPackageType());
			ret = ret && true;
		}

		return ret;

	}

	public boolean testSameItemData(StockUnit su, UnitLoad ul) {

		boolean ret = false;
		if (ul.getStockUnitList() != null && ul.getStockUnitList().size() > 0) {

			for (StockUnit s : ul.getStockUnitList()) {
				if (s.getItemData().equals(su.getItemData())) {
					ret = true;
				} else {
					log.warn("testSameItemData: " + s.getItemData().toUniqueString() + " != " + su.getItemData().toUniqueString());
					ret = false;
					return ret;
				}
			}
		} else {
			ret = true;
		}

		return ret;
	}

	public boolean testSameItemData(ItemData idat, UnitLoad ul) {

		boolean ret = false;
		if (ul.getStockUnitList() != null && ul.getStockUnitList().size() > 0) {

			for (StockUnit s : ul.getStockUnitList()) {
				if (s.getItemData().equals(idat)) {
					ret = true;
				} else {
					log.warn("testSameItemData: " + s.getItemData().toUniqueString() + " != " + idat.toUniqueString());
					ret = false;
					return ret;
				}
			}
		} else {
			ret = true;
		}

		return ret;
	}

	public boolean testSameLot(StockUnit su, UnitLoad ul) {
		boolean ret = false;
		if (ul.getStockUnitList() != null && ul.getStockUnitList().size() > 0) {

			for (StockUnit s : ul.getStockUnitList()) {
				if (s.getItemData().equals(su.getItemData())) {
					ret = true;
				} else {
					log.warn("testSameItemData: " + s.getItemData().toUniqueString() + " != " + su.getItemData().toUniqueString());
					ret = false;
					return ret;
				}

				if (s.getLot() != null && su.getLot() != null) {
					if (s.getLot().equals(su.getLot())) {
						ret = true;
					} else {
						log.warn("testSameItemData: " + s.getLot().toUniqueString() + " != " + su.getLot().toUniqueString());
						ret = false;
						return ret;
					}
				}
			}
		} else {
			log.warn("found empty UnitLoad: " + ul.toShortString());
			ret = true;
		}

		return ret;
	}

	public BigDecimal getAmountOfUnitLoad(ItemData idat, UnitLoad ul) throws InventoryException {
		BigDecimal amount = new BigDecimal(0);
		if( ul.getPackageType() == UnitLoadPackageType.CONTAINER ) {
			return amount;
		}
		
		boolean contains = false;
		for (StockUnit su : ul.getStockUnitList()) {
			if (su.getItemData().equals(idat)) {
				amount = amount.add(su.getAvailableAmount());
				contains = true;
			}
		}
		if (!contains) {
			throw new InventoryException(InventoryExceptionKey.ITEMDATA_NOT_ON_UNITLOAD, new String[] { idat.getNumber(), ul.getLabelId() });
		}
		return amount;
	}

	
	public int getTotalStockUnitCount(UnitLoad ul) throws InventoryException {
		String q = "SELECT count(DISTINCT su) ";
		q += " FROM " + StockUnit.class.getSimpleName() + " su, " + UnitLoad.class.getSimpleName() + " ul";
		q += " WHERE (su.unitLoad = ul ) AND (ul =:ul OR ul.carrierUnitLoad =:ul )";
		Query query = manager.createQuery(q);
		query.setParameter("ul", ul);
		Long l = (Long) query.getSingleResult();
		return l.intValue();
	}
	
	public BigDecimal getAmountOfStorageLocation(ItemData idat, StorageLocation sl) throws InventoryException {
		BigDecimal amount = new BigDecimal(0);

		for (UnitLoad ul : sl.getUnitLoads()) {
			if( ul.getPackageType() == UnitLoadPackageType.CONTAINER ) {
				continue;
			}
			for (StockUnit su : ul.getStockUnitList()) {
				if (su.getItemData().equals(idat)) {
					amount = amount.add(su.getAvailableAmount());
				}
			}
		}

		return amount;
	}

	public boolean testSameLot(Lot lot, UnitLoad ul) {
		boolean ret = false;
		if (ul.getStockUnitList() != null && ul.getStockUnitList().size() > 0) {

			for (StockUnit s : ul.getStockUnitList()) {
				if (s.getLot() != null && lot != null) {
					if (s.getLot().equals(lot)) {
						ret = true;
					} else {
						ret = false;
						return ret;
					}
				}
			}
		} else {
			ret = true;
		}

		return ret;
	}

	public void processLotDates(Lot lot, Date bestBeforeEnd, Date useNotBefore) {

		lot = manager.merge(lot);

		Date today = DateHelper.endOfDay(new Date());

		if (useNotBefore != null && (lot.getUseNotBefore() == null || lot.getUseNotBefore().compareTo(useNotBefore) != 0)) {
			lot.setUseNotBefore(useNotBefore);
		}
		if (useNotBefore != null && lot.getUseNotBefore() != null && lot.getUseNotBefore().after(today)) {
			log.warn("Set Lot to LotLockState.LOT_TOO_YOUNG: " + lot.toShortString());
			lot.setLock(LotLockState.LOT_TOO_YOUNG.getLock());
		}

		today = DateHelper.beginningOfDay(new Date());
		if (bestBeforeEnd != null && (lot.getBestBeforeEnd() == null || lot.getBestBeforeEnd().compareTo(bestBeforeEnd) != 0)) {
			lot.setBestBeforeEnd(bestBeforeEnd);
		}

		if (bestBeforeEnd != null && lot.getBestBeforeEnd() != null && lot.getBestBeforeEnd().before(today)) {
			log.warn("Set Lot to LotLockState.LOT_EXPIRED: " + lot.toShortString());
			lot.setLock(LotLockState.LOT_EXPIRED.getLock());
		}

		manager.flush();
	}

	// -------------------------------------------------------------------------
	// Sanity Checks
	// -------------------------------------------------------------------------

	public StockUnit createStockUnitOnStorageLocation(String clientRef, String slName, String articleRef, String lotRef, BigDecimal amount, String unitLoadRef, String activityCode, String serialNumber)
			throws EntityNotFoundException, InventoryException, FacadeException {

		Client c;
		StorageLocation sl;
		Lot lot = null;
		StockUnit su;
		UnitLoad ul;

		try {
			c = clientService.getByNumber(clientRef);
			Client callerClient = contextService.getCallersClient();
			if ((!callerClient.equals(c)) && (!callerClient.isSystemClient())) {
				throw new EJBAccessException();
			}

			sl = locationService.read(slName);

			if (sl == null) {
				log.warn("NOT FOUND. Going to CREATE StorageLocation " + slName);

				LocationType type;
				try {
					type = slTypeService.getDefault();
					if (type == null)
						throw new NullPointerException("No default location type found.");
				} catch (Throwable e) {
					log.error(e.getMessage(), e);
					throw new RuntimeException(e.getMessage());
				}
				sl = locationService.create(slName, c, type, null, null);
			}

			ItemData idat = itemDataService.getByItemNumber(c, articleRef);

			if (idat == null) {
				log.error("--- !!! NO ITEM WITH NUMBER " + articleRef + " !!! ---");
				throw new InventoryException(InventoryExceptionKey.NO_SUCH_ITEMDATA, articleRef);
			}

			if ((lotRef != null && lotRef.length() > 0) || idat.isLotMandatory()) {
				try {
					lot = getOrCreateLot(c, lotRef, idat);
					ul = getOrCreateUnitLoad(c, idat, sl, unitLoadRef, StockState.ON_STOCK);
				} catch (Throwable ex) {
					log.error(ex.getMessage(), ex);
					throw new InventoryException(InventoryExceptionKey.CREATE_STOCKUNIT_ON_STORAGELOCATION_FAILED, slName);
				}
			}
			try {
				ul = getOrCreateUnitLoad(c, idat, sl, unitLoadRef, StockState.ON_STOCK);
			} catch (Throwable ex) {
				log.error(ex.getMessage(), ex);
				throw new InventoryException(InventoryExceptionKey.CREATE_STOCKUNIT_ON_STORAGELOCATION_FAILED, slName);
			}

			// Fucking hibernate is not able to handle the stock unit list without accessing it in advance
			ul.getStockUnitList().size();

			su = createStock(c, lot, idat, amount, null, ul, activityCode, serialNumber);

			consolidate( su.getUnitLoad(), activityCode);

			return su;

		} catch (FacadeException ex) {
			throw ex;
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new InventoryException(InventoryExceptionKey.CREATE_STOCKUNIT_ONSTOCK, "");
		}
	}

	protected Lot getOrCreateLot(Client c, String lotRef, ItemData idat) {
		Lot lot;
		if (lotRef != null && lotRef.length() != 0) {
			try {
				lot = lotService.getByNameAndItemData(c, lotRef, idat.getNumber());
				if (!lot.getItemData().equals(idat)) {
					throw new RuntimeException("ItemData does not match Lot");
				}
			} catch (EntityNotFoundException ex) {
				log.warn("CREATE Lot: " + ex.getMessage());
				lot = lotService.create(c, idat, lotRef, new Date(), null, null);
			}
		} else {
			throw new IllegalArgumentException("Missing orderRef");
		}
		return lot;
	}

	public UnitLoad getOrCreateUnitLoad(Client c, ItemData idat, StorageLocation sl, String ref, int state) throws FacadeException {
		UnitLoad ul;
		UnitLoadType type;

		if (c == null)
			throw new NullPointerException("Client must not be null");
		if (idat == null)
			throw new NullPointerException("Article must not be null");
		if (sl == null)
			throw new NullPointerException("StorageLocation must not be null");
		if (ref == null)
			throw new NullPointerException("Reference must not be null");

		if (ref != null && ref.length() != 0) {
			ul = unitLoadService.read(ref);
			if (ul == null) {
				try {
					log.warn("Unit load does not exist. create new. labelId="+ref);
					type = idat.getDefaultUnitLoadType();
					if (type == null) {
						type = unitLoadTypeService.getDefault();
					}
					if (type == null) {
						throw new RuntimeException("Cannot retrieve default UnitLoadType");
					}
					ul = createUnitLoad(c, ref, type, sl, state);
					locationReserver.allocateLocation(sl, ul);
				} catch (LOSLocationException lex) {
					throw lex;
				}
			}
		} else {
			throw new IllegalArgumentException("Missing labelId");
		}
		return ul;
	}

	public void deleteStockUnitsFromStorageLocation(StorageLocation sl, String activityCode) throws FacadeException {

		List<Long> sus = new ArrayList<Long>();
		List<Long> uls = new ArrayList<Long>();

		sl = manager.find(StorageLocation.class, sl.getId());

		for (UnitLoad ul : sl.getUnitLoads()) {
			for (StockUnit su : ul.getStockUnitList()) {
				sus.add(su.getId());
				// su = manager.find(StockUnit.class, su.getId());
				// manager.remove(su);
			}
			uls.add(ul.getId());
		}

		for (Long id : sus) {
			StockUnit su = manager.find(StockUnit.class, id);
			if (su == null) {
				continue;
			}

			removeStockUnit(su, activityCode, true);
		}

		for (Long id : uls) {
			UnitLoad ul = manager.find(UnitLoad.class, id);
			if (ul == null) {
				continue;
			}
			manager.remove(ul);
		}
	}

	public void removeStockUnit(StockUnit su, String activityCode, boolean sendNotify) throws FacadeException {

		su = manager.find(StockUnit.class, su.getId());
		log.warn("Going to remove stock Unit: " + su.toShortString());
		BigDecimal amountOld = su.getAmount();
		su.setAmount(BigDecimal.ZERO);
		if (BigDecimal.ZERO.compareTo(amountOld) < 0) {

			su.getUnitLoad().setOpened(true);

			recalculateWeightDiff(su.getUnitLoad(), su.getItemData(), amountOld.negate());

			String operator = contextService.getCallerUserName();
			journalHandler.recordChangeAmount(su.getClient(), su, amountOld.negate(), activityCode, operator, null);
			manageStockService.onStockAmountChange(su, amountOld);

			if( sendNotify ) {
				try{
					hostService.sendMsg( new HostMsgStock(su, amountOld.negate(), null, InventoryJournalRecordType.REMOVED, activityCode) );
				}
				catch( FacadeException e ) {
					throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, e.getLocalizedMessage());
				}
			}
		}
		manager.remove(su);
		manager.flush();

	}

	public BigDecimal recalculateWeight( UnitLoad unitLoad ) {
		String logStr = "recalculateWeight ";
		if( unitLoad.getPackageType() == UnitLoadPackageType.CONTAINER ) {
//			log.debug(logStr+"no weight calculation on CONTAINER unit loads");
			return null;
		}
		BigDecimal weightSum = unitLoad.getType().getWeight();
		weightSum = weightSum == null ? BigDecimal.ZERO : weightSum;
		int numStock=0;
		for( StockUnit stock : unitLoad.getStockUnitList() ) {
			BigDecimal weightItem = stock.getItemData().getWeight();
			if( weightItem != null ) {
				weightSum = weightSum.add( weightItem.multiply(stock.getAmount()));
			}
			numStock++;
			if( numStock>1000 ) {
				log.error(logStr+"Calculation of weight of container unit loads is not supportet. Unit load has more than 1000 stocks. label="+unitLoad.getLabelId());
				weightSum = null;
				break;
			}
		}
		
		unitLoad.setWeightCalculated(weightSum);
		return weightSum;
	}
	
	private BigDecimal recalculateWeightDiff(UnitLoad unitLoad, ItemData item, BigDecimal amount) {
		BigDecimal weightNew = unitLoad.getWeightCalculated();
		if( weightNew == null || weightNew.compareTo(BigDecimal.ZERO)<=0 ) {
			return recalculateWeight(unitLoad);
		}
		BigDecimal weightItem = item.getWeight();
		if( weightItem != null ) {
			weightNew = weightNew.add( weightItem.multiply(amount));
		}
		if( weightNew == null || weightNew.compareTo(BigDecimal.ZERO)<=0 ) {
			return recalculateWeight(unitLoad);
		}
		
		unitLoad.setWeightCalculated(weightNew);
		return weightNew;
	}

	public UnitLoad createUnitLoad(Client client, String labelId, UnitLoadType type, StorageLocation storageLocation,
			int state) throws FacadeException {
		if (client == null || labelId == null || type == null || storageLocation == null) {
			throw new NullPointerException("createLOSUnitLoad: parameter == null");
		}

		UnitLoad ul = unitLoadService.create(client, labelId, type, storageLocation);
		ul.setState(state);

		try {
			locationReserver.checkAllocateLocation(storageLocation, ul, false);
		} catch (LOSLocationReservedException ex) {
			throw ex.createRollbackException();
		} catch (LOSLocationWrongClientException ex) {
			throw ex.createRollbackException();
		} catch (LOSLocationNotSuitableException ex) {
			throw ex.createRollbackException();
		}

		ul.setStorageLocation(storageLocation);

		manager.persist(ul);

		customLocationService.onUnitLoadPlaced(storageLocation, ul);

		log.info("CREATED LOSUnitLoad: " + ul.toShortString());

		return ul;
	}
}
