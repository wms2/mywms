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
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;
import org.mywms.model.UnitLoadType;
import org.mywms.service.ClientService;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.LotService;
import org.mywms.service.StockUnitService;
import org.mywms.service.UserService;

import de.linogistix.los.common.exception.LOSExceptionRB;
import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.inventory.customization.ManageAdviceService;
import de.linogistix.los.inventory.customization.ManageReceiptService;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.exception.InventoryTransferException;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSAdviceState;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.linogistix.los.inventory.model.LOSGoodsReceiptState;
import de.linogistix.los.inventory.model.LOSGoodsReceiptType;
import de.linogistix.los.inventory.model.LOSStorageRequest;
import de.linogistix.los.inventory.model.LOSStorageRequestState;
import de.linogistix.los.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.inventory.service.LOSGoodsReceiptPositionService;
import de.linogistix.los.inventory.service.LOSGoodsReceiptService;
import de.linogistix.los.inventory.service.LOSStorageRequestService;
import de.linogistix.los.inventory.service.LotLockState;
import de.linogistix.los.location.businessservice.LOSStorage;
import de.linogistix.los.location.businessservice.LocationReserver;
import de.linogistix.los.location.entityservice.LOSUnitLoadService;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.exception.LOSLocationExceptionKey;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.service.QueryStorageLocationService;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.util.businessservice.ContextService;
import de.linogistix.los.util.entityservice.LOSSystemPropertyService;

@Stateless
public class LOSGoodsReceiptComponentBean implements LOSGoodsReceiptComponent {

	private final Logger logger = Logger
			.getLogger(LOSGoodsReceiptComponentBean.class);
	@EJB
	private QueryStorageLocationService slService;
	@EJB
	private LOSInventoryComponent inventoryComp;
	@EJB
	private LOSGoodsReceiptService grService;
	@EJB
	private ClientService clientService;
	@EJB
	private ManageReceiptService manageGrService;
	@EJB
	private LOSGoodsReceiptPositionService grPosService;
	@EJB
	private UserService userService;
	@EJB
	private LotService lotService;
	@EJB
	private InventoryGeneratorService genService;
	@EJB
	private LOSUnitLoadService ulService;
	@EJB
	private StockUnitService suService;
	@EJB
	private ManageAdviceService manageAdviceService;
	@EJB
	private LocationReserver locationReserver;
	
	@EJB
	private LOSStorageRequestService storeReqService;
	@EJB
	private LOSStorage storageService;
	@EJB
	private ContextService contextService;
	@EJB
	private EntityGenerator entityGenerator;
	@EJB
	private StorageBusiness storageBusiness;
	@EJB
	private LOSSystemPropertyService propertyService;
	
	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;

	// --------------------------------------------------------------------------------------------

	public List<LOSStorageLocation> getGoodsReceiptLocations()
			throws LOSLocationException {

		List<LOSStorageLocation> slList;
		slList = slService.getListForGoodsIn();

		if (slList.size() == 0) {
			// LOSStorageLocation defLoc;
			// defLoc =
			// createGoodsReceiptLocation(LOSGoodsReceiptConstants.GOODSRECEIPT_DEFAULT_LOCATION_NAME);
			// slList.add(defLoc);
			throw new LOSLocationException(
					LOSLocationExceptionKey.NO_GOODS_IN_LOCATION, new Object[0]);
		}

		return slList;

	}

	public LOSGoodsReceipt getByGoodsReceiptNumber(String number) {

		return grService.getByGoodsReceiptNumber(number);
	}

	// --------------------------------------------------------------------------------------------
	public LOSGoodsReceipt createGoodsReceipt(Client client,
			String licencePlate, String driverName, String forwarder,
			String deliveryNoteNumber, Date receiptDate) {

		String sn = genService.generateGoodsReceiptNumber(client);

		LOSGoodsReceipt grr;
		grr = grService.createGoodsReceipt(client, sn);
		grr.setLicencePlate(licencePlate);
		grr.setDriverName(driverName);
		grr.setForwarder(forwarder);
		grr.setDeliveryNoteNumber(deliveryNoteNumber);
		grr.setReceiptDate(receiptDate);
		grr.setOperator(contextService.getCallersUser());

		return grr;

	}

	// --------------------------------------------------------------------------------------------
	public LOSGoodsReceiptPosition createGoodsReceiptPosition(Client client,
			LOSGoodsReceipt gr, String orderReference, BigDecimal amount) throws FacadeException {
		return createGoodsReceiptPosition(client, gr, orderReference, amount,
				LOSGoodsReceiptType.INTAKE, "");
	}

	public LOSGoodsReceiptPosition createGoodsReceiptPosition(Client client,
			LOSGoodsReceipt gr, String orderReference, BigDecimal amount,
			LOSGoodsReceiptType receiptType, String qaFault) throws FacadeException {
		String logStr = "createGoodsReceiptPosition ";
		
		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
			throw new InventoryException(InventoryExceptionKey.AMOUNT_MUST_BE_GREATER_THAN_ZERO, "" + amount);
		}
		
		LOSGoodsReceiptPosition pos = entityGenerator.generateEntity(LOSGoodsReceiptPosition.class);
		pos.setClient(client);
		pos.setGoodsReceipt(gr);
		pos.setOrderReference(orderReference);
		pos.setReceiptType(receiptType);
		pos.setQaFault(qaFault);
		
		// The position list is not filled in batch processing. And the query is more fast than reading the whole list
		long positionNumber = grPosService.queryNumPos(gr);
		
		// Make insert with dummy number
		// This waits until other transactions on the goods receipt are closed. 
		// After this a correct position number will be created
		pos.setPositionNumber(gr.getGoodsReceiptNumber());

		pos.setAmount(amount);
		
		manager.persist(pos);
		manager.flush();

		String positionNumberStr = "";
		int numCheck = 0; // emergency brake
		do {
			if( numCheck>0 ) {
				logger.warn(logStr+"Numer already exists. try next");
			}
			positionNumber ++;
			numCheck ++;
			positionNumberStr = gr.getGoodsReceiptNumber() + "-" + positionNumber;
		}
		while( checkPosNumber(gr, positionNumberStr) == false && numCheck < 1000 );

		pos.setPositionNumber(positionNumberStr);

		return pos;
	}

	private boolean checkPosNumber( LOSGoodsReceipt gr, String posNumber ) {
		if( posNumber == null || posNumber.length() == 0 ) {
			return false;
		}

		return !grPosService.existsByNumber(posNumber);
	}
	
	// --------------------------------------------------------------------------------------------
	public StockUnit receiveStock(LOSGoodsReceiptPosition grPosition,
			Lot batch, ItemData item, BigDecimal amount, LOSUnitLoad unitLoad, String serialNumber)
			throws FacadeException {
		StockUnit su;

		if( batch == null ) {
			if( item.isLotMandatory() ) {
				throw new InventoryException(
						InventoryExceptionKey.LOT_MANDATORY, new String[] {
								item.getNumber() });
			}
		}
		else {
			if (!batch.getItemData().equals(item)) {
				throw new InventoryException(
						InventoryExceptionKey.ITEMDATA_LOT_MISMATCH, new String[] {
								batch.getName(), item.getNumber() });
			}
	
			if (batch.isLocked()) {
	
				if (batch.getLock() == LotLockState.LOT_TOO_YOUNG.getLock()) {
					logger.info("lot is too young - receive anyway");
	
				} else {
					throw new InventoryException(
							InventoryExceptionKey.LOT_ISLOCKED,
							new String[] { batch.getName() });
				}
			}
		}

// Place logical checks somewhere else
//		LOSArea a = (LOSArea) unitLoad.getStorageLocation().getArea();
//		if (!a.getAreaType().equals(LOSAreaType.GOODS_IN)) {
//			throw new InventoryException(
//					InventoryExceptionKey.NOT_A_GOODSIN_LOCATION,
//					new Object[] { unitLoad.getStorageLocation().getName() });
//		}

		String activityCode = grPosition.getPositionNumber();

		su = inventoryComp.createStock(grPosition.getClient(), batch, item,
				amount, unitLoad, activityCode, serialNumber, null, false);
		manager.flush();

		grPosition.setStockUnit(su);

		if (grPosition.getGoodsReceipt().getForwarder() != null
				&& grPosition.getGoodsReceipt().getForwarder().length() > 0) {
			
			su.addAdditionalContent(InventoryBundleResolver.resolve(InventoryBundleResolver.class,"deliverer",Locale.getDefault() )
					+ ": " + grPosition.getGoodsReceipt().getForwarder() + "\n");

		}

		manager.flush();

		// 19.12.2012, krane. moved to calling methods. here is no advice and location info. 
//		manageGrService.onGoodsReceiptPositionCollected(grPosition);

		return su;
	}

	public void assignAdvice(LOSAdvice adv, LOSGoodsReceipt r) throws InventoryException{
		r = manager.find(LOSGoodsReceipt.class, r.getId());
		adv = manager.find(LOSAdvice.class, adv.getId());
		
		if(!adv.getClient().equals(r.getClient())){
			throw new InventoryException(InventoryExceptionKey.CLIENT_MISMATCH, new Object[0]);
		}
		
		logger.debug("assignAdvice: " + adv.getAdviceNumber() + " to GR: " + r.getGoodsReceiptNumber());

		try {
			manageAdviceService.updateFromHost(adv);
		}
		catch( Exception e ) {	
			throw new InventoryException(InventoryExceptionKey.ADVICE_REFRESH_ERROR, adv.toUniqueString());
		}
		
		adv = manager.find(LOSAdvice.class, adv.getId());
		
		if (!r.containsAssignedAdvices(adv)){
			r.addAssignedAdvices(adv);
			switch (adv.getAdviceState()){
				case RAW:
					adv.setAdviceState(LOSAdviceState.PROCESSING);
					adv.setProcessDate(new Date());
					 break;
				case FINISHED:
					throw new InventoryException(InventoryExceptionKey.ADVICE_FINISHED, adv.toUniqueString());
				case GOODS_TO_COME:
				case OVERLOAD:
				case PROCESSING:
				default:
					break;
			}
		} else{
			throw new InventoryException(InventoryExceptionKey.ADVICE_ASSIGNED, adv.toUniqueString());
		}
	}
	
	public void removeAssignedAdvice(LOSAdvice adv, LOSGoodsReceipt r) throws InventoryException{
		r = manager.find(LOSGoodsReceipt.class, r.getId());
		adv = manager.find(LOSAdvice.class, adv.getId());
		logger.debug("removeAssignedAdvice: " + adv.getAdviceNumber() + " from GR: " + r.getGoodsReceiptNumber());
		
		if (r.containsAssignedAdvices(adv)){
			
			try{
				r.removeAssignedAdvices(adv);
			} catch (IllegalArgumentException ex){
				throw new InventoryException(InventoryExceptionKey.ADVICE_POSITIONS_ASSIGNED, adv.getAdviceNumber());
			}
			if( adv.getAdviceState() == LOSAdviceState.PROCESSING ) {
				if( BigDecimal.ZERO.compareTo(adv.getReceiptAmount()) >= 0 ) { 
					adv.setAdviceState(LOSAdviceState.RAW);
				}
				else if( adv.getReceiptAmount().compareTo(adv.getNotifiedAmount())>0 ) {
					adv.setAdviceState(LOSAdviceState.OVERLOAD);
				}
				else if( adv.getReceiptAmount().compareTo(adv.getNotifiedAmount())<0 ) {
					adv.setAdviceState(LOSAdviceState.GOODS_TO_COME);
				}
				else {
					adv.setAdviceState(LOSAdviceState.FINISHED);
				}
			}

		} else{
			//
		}
	}
	
	public void assignAdvice(LOSAdvice adv, LOSGoodsReceiptPosition pos) throws FacadeException {
		String logStr = "assignAdvice ";
		
		if (adv.getLot() != null
				&& (!adv.getLot().equals(pos.getStockUnit().getLot()))) {
			throw new InventoryException(InventoryExceptionKey.LOT_MISMATCH,
					new String[] { adv.getLot().getName() });
		}
		
		if(pos.getRelatedAdvice() != null){
		
			if (!pos.getRelatedAdvice().equals(adv)) {
				
				throw new InventoryException(
						InventoryExceptionKey.POSITION_ALREADY_ASSIGNED_ADVICE,
						new String[] { pos.getRelatedAdvice().toUniqueString(),
								pos.toUniqueString() });
			} else {
				logger.warn("Tried to assign Position twice to advice "
						+ pos.getRelatedAdvice().getAdviceNumber());
			}
		}
		else{
		
			adv.addGrPos(pos);
			if (adv.diffAmount().compareTo(BigDecimal.ZERO) == 0){
				adv.setAdviceState(LOSAdviceState.FINISHED);
			} else if (adv.diffAmount().compareTo(BigDecimal.ZERO) > 0){
				//Unterliefert
				adv.setAdviceState(LOSAdviceState.GOODS_TO_COME);
				
			} else{
				//Ueberliefert
				
				boolean limit = propertyService.getBoolean(adv.getClient(), null, GR_LIMIT_AMOUNT_TO_NOTIFIED);
				if( limit ) {
					logger.info(logStr+"Amount is limited to notified. Cannot post goods receipt position. advice="+adv.getAdviceNumber());
					throw new LOSExceptionRB("ADVICE_AMOUNT_LIMITED", InventoryBundleResolver.class);
				}
				
				
				adv.setAdviceState(LOSAdviceState.OVERLOAD);
			}

			if (adv.isExpireBatch() && pos.getStockUnit().getLot() != null) {
				expireOlderBatch(pos.getStockUnit().getLot());
			}
			
			logger.info("ASSIGNED POSITION: " + pos.getPositionNumber()
					+ " to advice " + adv.getAdviceNumber());
		}
	}

	public void expireOlderBatch(Lot newer) {
		List<Lot> lots = lotService.getListByItemData(newer.getItemData());
		for (Lot l : lots) {
			if (newer.equals(l)) {
				continue;
			}
			if (l.getCreated().after(newer.getCreated())) {
				continue;
			}
			l.setLock(LotLockState.LOT_EXPIRED.getLock());
		}
	}

	@SuppressWarnings("unchecked")
	public List<LOSAdvice> getSuitableLOSAdvice(String exp, Client client,
			Lot lot, ItemData idat) throws InventoryException {

		if (client == null) {
			throw new NullPointerException("Client must not be null");
		}
		if (lot == null) {
			// throw new NullPointerException("Lot must not be null");
		}
		if (idat == null) {
			// throw new NullPointerException("ItemData must not be null");
		}
		if (lot != null && idat != null && !lot.getItemData().equals(idat)) {
			throw new IllegalArgumentException("lot and itemdata don't match");
		}

		StringBuffer b = new StringBuffer();
		b.append(" SELECT DISTINCT a FROM ");
		b.append(LOSAdvice.class.getSimpleName());
		b.append(" a ");
		// b.append(" LEFT JOIN FETCH a.grPos ");
		b.append(" WHERE a.client = :client ");
		if (lot != null) {
			b.append(" AND a.lot = :lot ");
		}
		// b.append(" AND a.amount >= :amount ");
		b.append(" AND a.adviceState IN (:raw, :goodstocome, :overload, :processing) ");
		b.append(" AND LOWER(a.adviceNumber) LIKE :exp");
		b.append(" ORDER BY a.receiptAmount ASC, a.created ASC");

		Query q = manager.createQuery(new String(b)).setParameter("client",
				client);

		if (lot != null) {
			q = q.setParameter("lot", lot);
		}
		q = q
				.
				// setParameter("amount",
				// pos.getStockUnitRecord().getNewAmount()).
				setParameter("raw", LOSAdviceState.RAW).setParameter(
						"overload", LOSAdviceState.OVERLOAD).setParameter(
						"goodstocome", LOSAdviceState.GOODS_TO_COME)
				.setParameter("processing", LOSAdviceState.PROCESSING)
				.setParameter("exp", TemplateQueryWhereToken.transformLikeParam(exp));

		List<LOSAdvice> ret;
		ret = q.getResultList();

		return ret;

	}

	public void remove(LOSGoodsReceipt r, LOSGoodsReceiptPosition pos)
			throws FacadeException {

		if( (r.getReceiptState() == LOSGoodsReceiptState.FINISHED) ||
			(r.getReceiptState() == LOSGoodsReceiptState.TRANSFER) ) {
			logger.info("Won't remove position because of LOSGoodsReceipt.state = " + r.getReceiptState());
			throw new InventoryException(
					InventoryExceptionKey.WRONG_STATE, new Object[]{"LOSGoodsReceipt",r.getReceiptState()});
		}
		
		if( (pos.getState() == LOSGoodsReceiptState.FINISHED) ||
			(pos.getState() == LOSGoodsReceiptState.TRANSFER) ) {
			logger.info("Won't remove position because of LOSGoodsReceiptPosition.state = " + pos.getState());
			throw new InventoryException(
					InventoryExceptionKey.WRONG_STATE, new Object[]{"LOSGoodsReceiptPosition", pos.getState()});
		}

		logger.info("Remove GR-position " + pos.getPositionNumber());

		ItemData itemData = null;
		StockUnit su = pos.getStockUnit();
		if (su == null) {
			try {
				su = suService.getByLabelId(pos.getStockUnitStr());
			} catch (EntityNotFoundException ex) {
				logger.debug("EntityNotFound StockUnit AdditionalId=" + pos.getStockUnitStr());
				su = null;
			}
		}

		LOSUnitLoad ul = null;
		
		if (su != null) {
			itemData = su.getItemData();
			BigDecimal amountSu = su.getAmount();
			BigDecimal amountGr = pos.getAmount();
			if( BigDecimal.ZERO.compareTo(amountSu) < 0 && !(amountSu.compareTo(amountGr)==0) ) {
				logger.info("The amount of StockUnit="+amountSu+" is not equal to amount of position="+amountGr+". Abort");
				throw new InventoryException(InventoryExceptionKey.GOODS_RECEIPT_STOCK_ADDED, new Object[0]);
			}
		}
		if (su != null) {
			ul = (LOSUnitLoad)su.getUnitLoad();
			
			pos.setStockUnit(null);
			//dgrys portierung wildfly 8.2
			//logger.info("Going to remove SU " + su.getLabelId());
			logger.info("Going to remove SU " + su.getId());
			inventoryComp.removeStockUnit(su, pos.getPositionNumber(), false);
		} else {
			logger.warn("position holds no StockUnit: " + pos.getPositionNumber());
		}

		if (ul == null) {
						
			try {
				ul = ulService.getByLabelId(pos.getClient(), pos.getUnitLoad());
			} catch (EntityNotFoundException ex) {
				logger.debug("EntityNotFound UnitLoad Label=" + pos.getUnitLoad());
			}
		}
		LOSUnitLoad ulNirwana = ulService.getNirwana();
		if( ul != null && ul.equals(ulNirwana) ) {
			// The nirwana unit load is written to the position, when the stock is moved to another unit load
			logger.info("The UnitLoad is NIRWANA. So Stock has changed!. Abort");
			throw new InventoryException(InventoryExceptionKey.GOODS_RECEIPT_STOCK_ADDED, new Object[0]);
		}
		if( ul != null ) {
			logger.info("Going to remove UNITLOAD " + ul.getLabelId());
			
			List<LOSStorageRequest> storageList = storeReqService.getListByLabelId(ul.getLabelId());
			for( LOSStorageRequest sr : storageList ) {
				if( sr.getRequestState() == LOSStorageRequestState.CANCELED
						|| sr.getRequestState() == LOSStorageRequestState.FAILED
						|| sr.getRequestState() == LOSStorageRequestState.RAW
						|| sr.getRequestState() == LOSStorageRequestState.TERMINATED ) {
					logger.info("Remove unit load from storage request " + sr.getNumber());
					storageBusiness.removeStorageRequest(sr);
				}
				else {
					logger.info("Active storage request found for unit load. label=" + ul.getLabelId());
					throw new InventoryException(InventoryExceptionKey.UNITLOAD_DELETE_ERROR_STORAGEREQUEST, new Object[0]);
				}
			}
			
			if( ul.getStockUnitList().size() <= 1 ) {
				logger.info("Remove UnitLoad " + ul.getLabelId());
				storageService.sendToNirwana(contextService.getCallerUserName(), ul);
			}
		}
		
		LOSAdvice advice = pos.getRelatedAdvice();
		if (advice != null) {
			advice.removeGrPos(pos);
			if (advice.diffAmount().compareTo(BigDecimal.ZERO) == 0){
				advice.setAdviceState(LOSAdviceState.FINISHED);
			} else if (advice.diffAmount().compareTo(BigDecimal.ZERO) > 0){
				//Unterliefert
				advice.setAdviceState(LOSAdviceState.GOODS_TO_COME);
				
			} else{
				//too much delivered
				advice.setAdviceState(LOSAdviceState.OVERLOAD);
			}
		}

		
		r.getPositionList().remove(pos);
		manager.remove(pos);

		manageGrService.onGoodsReceiptPositionDeleted(pos.getClient(), itemData, pos.getAmount(), advice,  pos.getUnitLoad(), ul == null ? null : ul.getType());

	}

	public void cancel(LOSGoodsReceipt gr) throws FacadeException {
		gr = manager.find(LOSGoodsReceipt.class, gr.getId());

		List<Long> positionIds = new ArrayList<Long>();

		// remove postions
		for (LOSGoodsReceiptPosition pos : gr.getPositionList()) {
			positionIds.add(pos.getId());
		}
		for (Long id : positionIds) {
			LOSGoodsReceiptPosition pos = manager.find(
					LOSGoodsReceiptPosition.class, id);
			remove(gr, pos);
		}

		// delete LOSGoodsReceipt
		switch (gr.getReceiptState()) {
		case RAW:
			manager.remove(gr);
			break;
		default:
			logger.warn("Won't remove receipt: " + gr.toDescriptiveString()
					+ " *** Setting state to " + LOSGoodsReceiptState.CANCELED);
			gr.setReceiptState(LOSGoodsReceiptState.CANCELED);
			break;
		}
	}

	public LOSUnitLoad getOrCreateUnitLoad(Client c, LOSStorageLocation sl,
			UnitLoadType type, String ref) throws FacadeException {

		LOSUnitLoad ul;
		Client cl;
		if (c != null) {
			// do not read twice
//			cl = manager.find(Client.class, c.getId());
			cl=c;
		} else {
			cl = clientService.getSystemClient();

		}

		if (sl == null) {
			throw new NullPointerException("StorageLocation must not be null");
		}
		if (type == null) {
			throw new NullPointerException("UnitLoadType must not be null");
		}
		if (ref == null) {
			ref = genService.generateUnitLoadLabelId(cl, type);
		}

		if (ref != null && ref.length() != 0) {
			try {
				ul = (LOSUnitLoad) ulService.getByLabelId(cl, ref);
				if( !sl.equals(ul.getStorageLocation()) ) {
					logger.warn("UnitLoad not on location. label="+ul.getLabelId()+", location="+ul.getStorageLocation().getName()+", check location="+sl.getName());
					throw new LOSLocationException(
							LOSLocationExceptionKey.UNITLOAD_NOT_ON_LOCATION,
							new String[] { ul.getLabelId(), sl.getName() });
				}
//				boolean contains = false;
//				for (UnitLoad comp : sl.getUnitLoads()) {
//					if (comp.equals(ul)) {
//						contains = true;
//					}
//				}
//				if (!contains) {
//					throw new LOSLocationException(
//							LOSLocationExceptionKey.UNITLOAD_NOT_ON_LOCATION,
//							new String[] { ul.getLabelId(), sl.getName() });
//				}
			} catch (EntityNotFoundException ex) {
				logger.info("CREATE UnitLoad: " + ex.getMessage());
				ul = ulService.createLOSUnitLoad(cl, ref, type, sl);
				locationReserver.allocateLocation(sl, ul);

// use the default value
//				ul.setPackageType(LOSUnitLoadPackageType.OF_SAME_LOT);
			}
		} else {
			throw new IllegalArgumentException("Missing labelId");
		}

		return ul;
	}

	public void finishGoodsReceipt(LOSGoodsReceipt r) throws InventoryException, InventoryTransferException {

		LOSGoodsReceipt foundGr = manager.find(r.getClass(), r.getId());

		if (foundGr == null) {
			throw new InventoryException(
					InventoryExceptionKey.CREATE_GOODSRECEIPT, r
							.getGoodsReceiptNumber());
		}
		
		manageGrService.finishGoodsReceiptStart(foundGr);
		
		foundGr = manager.find(r.getClass(), r.getId());

		for (LOSGoodsReceiptPosition pos : foundGr.getPositionList()) {
			pos = manager.find(LOSGoodsReceiptPosition.class, pos.getId());
			pos.setStockUnit(null);
		}
		
		for (LOSAdvice adv : foundGr.getAssignedAdvices()){
			if( adv.getReceiptAmount().compareTo(adv.getNotifiedAmount())>0 ) {
				adv.setAdviceState(LOSAdviceState.OVERLOAD);
			}
			else if( adv.getReceiptAmount().compareTo(adv.getNotifiedAmount())<0 ) {
				adv.setAdviceState(LOSAdviceState.GOODS_TO_COME);
			}
			else {
				adv.setAdviceState(LOSAdviceState.FINISHED);
			}
		}
		
		foundGr.setReceiptState(LOSGoodsReceiptState.FINISHED);
		manageGrService.finishGoodsReceiptEnd(foundGr);
	}
	
	public void acceptGoodsReceipt(LOSGoodsReceipt r) throws InventoryException {
//		LOSGoodsReceipt foundGr = manager.find(r.getClass(), r.getId());
		switch (r.getReceiptState()) {
		case RAW:
		case ACCEPTED:
			r.setReceiptState(LOSGoodsReceiptState.ACCEPTED);
			break;
		default:
			throw new InventoryException(InventoryExceptionKey.ADVICE_CANNOT_BE_ACCEPTED, r.getGoodsReceiptNumber());
		}
		
	}

//	public StockUnitLabel createStockUnitLabel(LOSGoodsReceiptPosition pos, String printer) throws FacadeException {
//		String logStr = "createStockUnitLabel ";
//		pos = manager.find(LOSGoodsReceiptPosition.class, pos.getId());
//
//		StockUnit stock = pos.getStockUnit();
//
//		if( stock == null ) {
//			String s = pos.getStockUnitStr();
//			if( !StringTools.isEmpty(s) ) {
//				try {
//					Long l = Long.valueOf(s);
//					stock = manager.find(StockUnit.class, l);
//				}
//				catch( Throwable t ) {}
//			}
//		}
//		if( stock == null ) {
//			logger.info(logStr+"Position has no stock unit. => Cannot print label");
//			throw new InventoryException(InventoryExceptionKey.GRPOS_HAS_NO_STOCK, "");
//		}
//		
//		LOSUnitLoad unitLoad = (LOSUnitLoad)stock.getUnitLoad();
//		StockUnitLabel label = suLabelReport.generateStockUnitLabel(unitLoad);
//		suLabelReport.storeStockUnitLabel(label);
//		
//		return label;
//	}



	// ------------------------------------------------------------------------
	// ------------------------------------------------------------------------
	// private Area getGoodsReceiptArea() {
	//
	// Area grArea = null;
	// Client client = clientService.getSystemClient();
	//
	// try {
	// grArea = areaService.getByName(client,
	// LOSGoodsReceiptConstants.GOODSRECEIPT_AREA_NAME);
	// } catch (EntityNotFoundException e) {
	// try {
	// grArea = areaService.create(client,
	// LOSGoodsReceiptConstants.GOODSRECEIPT_AREA_NAME);
	// } catch (UniqueConstraintViolatedException e1) {
	// logger.error("Could not create GoodsReceipt Area !!");
	//
	// }
	// }
	//
	// return grArea;
	// }

	// //--------------------------------------------------------------------------------------------
	// private LOSStorageLocationType getGoodsReceiptLocationType() {
	//
	// LOSStorageLocationType giType = null;
	// Client client = clientService.getSystemClient();
	//
	// try {
	// giType = slTypeService.getByName(client,
	// LOSGoodsReceiptConstants.GOODSRECEIPT_LOCATION_TYPE_NAME);
	// } catch (EntityNotFoundException e1) {
	// try {
	// giType = slTypeService.create(client,
	// LOSGoodsReceiptConstants.GOODSRECEIPT_LOCATION_TYPE_NAME);
	// } catch (Exception e) {
	// logger.error("Could not create GoodsReceipt StorageLocationType!!");
	//
	// }
	// }
	//
	// return giType;
	// }
}
