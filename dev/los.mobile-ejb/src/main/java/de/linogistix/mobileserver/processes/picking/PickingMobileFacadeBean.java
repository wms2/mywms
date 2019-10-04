/*
 * Copyright (c) 2012-2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobileserver.processes.picking;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.persistence.EntityManager;
//import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.User;
import org.mywms.service.ClientService;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.common.businessservice.LOSPrintService;
import de.linogistix.los.common.exception.LOSExceptionRB;
import de.linogistix.los.inventory.businessservice.LOSOrderBusiness;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.inventory.service.ItemDataNumberService;
import de.linogistix.los.inventory.service.ItemDataService;
import de.linogistix.los.inventory.service.LOSCustomerOrderService;
import de.linogistix.los.inventory.service.LOSPickingOrderService;
import de.linogistix.los.inventory.service.LOSPickingPositionService;
import de.linogistix.los.inventory.service.StockUnitService;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.exception.LOSLocationExceptionKey;
import de.linogistix.los.location.model.LOSWorkingArea;
import de.linogistix.los.location.model.LOSWorkingAreaPosition;
import de.linogistix.los.model.State;
import de.linogistix.los.util.StringTools;
import de.linogistix.los.util.businessservice.ContextService;
import de.linogistix.mobileserver.processes.controller.ManageMobile;
import de.wms2.mywms.client.ClientBusiness;
import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.delivery.DeliveryOrderLine;
import de.wms2.mywms.document.Document;
import de.wms2.mywms.inventory.InventoryBusiness;
import de.wms2.mywms.inventory.StockState;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadEntityService;
import de.wms2.mywms.inventory.UnitLoadReportGenerator;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.inventory.UnitLoadTypeEntityService;
import de.wms2.mywms.location.LocationCluster;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.location.StorageLocationEntityService;
import de.wms2.mywms.picking.Packet;
import de.wms2.mywms.picking.PacketEntityService;
import de.wms2.mywms.picking.PickingOrder;
import de.wms2.mywms.picking.PickingOrderLine;
import de.wms2.mywms.picking.PickingType;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.product.ItemDataNumber;
import de.wms2.mywms.strategy.FixAssignmentEntityService;

/**
 * @author krane
 *
 */
@Stateless
public class PickingMobileFacadeBean implements PickingMobileFacade {
	Logger log = Logger.getLogger(PickingMobileFacadeBean.class);
	
	@Inject
	private ClientBusiness clientBusiness;
	@EJB
	private ClientService clientService;
	@EJB
	private ContextService contextService;
	@EJB
	private LOSOrderBusiness pickingBusiness;
	@EJB
	private LOSPickingOrderService pickingOrderService;
	@EJB
	private LOSPickingPositionService pickingPositionService;
	@EJB
	private LOSCustomerOrderService customerOrderService;
	@EJB
	private InventoryGeneratorService sequenceService;
	@EJB
	private StockUnitService stockUnitService;
	@EJB
	private ItemDataService itemDataService;
	@Inject
	private UnitLoadReportGenerator unitLoadReport;
	@EJB
	private LOSPrintService printService;
	@EJB
	private ManageMobile manageMobile;
	@EJB
	private ItemDataNumberService eanService;
	@Inject
	private FixAssignmentEntityService fixService;

    @PersistenceContext(unitName = "myWMS")
    protected EntityManager manager;

	@Inject
	private PacketEntityService pickingUnitLoadService;
	@Inject
	private UnitLoadEntityService unitLoadService;
	@Inject
	private StorageLocationEntityService locationService;
	@Inject
	private UnitLoadTypeEntityService unitLoadTypeService;
	@Inject
	private InventoryBusiness inventoryBusiness;

	public Client getDefaultClient() {
		Client systemClient = clientBusiness.getSingleClient();
		if (systemClient != null) {
			log.info("Only one client in system");
			return systemClient;
		}
		
		systemClient = clientBusiness.getSystemClient();

		// Callers client not system-client
		Client callersClient = contextService.getCallersClient();
		if( !systemClient.equals(callersClient) ) {
			log.info("Caller is not system-client => only one client to use");
			return callersClient; 
		}
		
		log.info("Plenty clients");
		return null;
	}

	public PickingMobileOrder readOrder(long orderId) {
		String logStr = "readOrder ";
		PickingOrder order = getPickingOrder(orderId);
		if( order == null ) {
			log.warn(logStr+"Cannot read order. id="+orderId);
			return null;
		}
		PickingMobileOrder mOrder = new PickingMobileOrder(order);
		
		if( !StringTools.isEmpty(mOrder.customerOrderNumber) ) {
			DeliveryOrder deliveryOrder = customerOrderService.getByNumber(mOrder.customerOrderNumber);
			if( deliveryOrder != null ) {
				mOrder.setCustomerOrder(deliveryOrder);
			}
		}
		
		return mOrder;
	}
	
	public void reserveOrder(long orderId) throws FacadeException {
		PickingOrder order = getPickingOrder(orderId);
		if( order != null && order.getState() < State.STARTED ) {
			User user = contextService.getCallersUser();
			pickingBusiness.reservePickingOrder(order, user, false);
		}
	}
	
	public void startOrder( long orderId ) throws FacadeException {
		PickingOrder order = getPickingOrder(orderId);
		if( order != null && order.getState() < State.PICKED ) {
			pickingBusiness.startPickingOrder(order, true);
		}
	}
	
	public void releaseOrder(long orderId) throws FacadeException {
		PickingOrder order = getPickingOrder(orderId);
		if( order != null ) {
			pickingBusiness.resetPickingOrder(order);
		}
	}

	public void finishOrder(long orderId) throws FacadeException {
		PickingOrder order = getPickingOrder(orderId);
		
		if( order != null ) {
			if( order.getState() >= State.PICKED && order.getState() < State.FINISHED ) {
				pickingBusiness.finishPickingOrder(order);
			}
			else if( order.getState() < State.PICKED ) {
				pickingBusiness.resetPickingOrder(order);
			}
			else {
				log.warn("Cannot finish order twice. number="+order.getOrderNumber());
			}
		}
		else {
			log.error("Cannot finish order. order=NULL");
		}

	}
	

	public void confirmPick(PickingMobilePos pickTO, PickingMobileUnitLoad pickTo, BigDecimal amountPicked, BigDecimal amountRemain, List<String> serialNoList, boolean counted ) throws FacadeException {
		String logStr = "confirmPick ";
		PickingOrderLine pick = getPickingPosition(pickTO.id);
		if( pick == null ) {
			log.warn(logStr+"Pick not found. id="+pickTO.id);
			throw new LOSExceptionRB("CannotReadCurrentPick", this.getClass());
		}
		
		Packet pickingUnitLoad = null;
		if( pickTo != null ) {
			pickingUnitLoad = pickingUnitLoadService.readFirstByLabel(pickTo.label);
			
			if( pickingUnitLoad == null ) {
				UnitLoad unitLoad = null;
				unitLoad = unitLoadService.readByLabel(pickTo.label);
				if (unitLoad == null) {
					UnitLoadType type = unitLoadTypeService.getPicking();
					StorageLocation storageLocation = locationService.getCurrentUsersLocation();
					String activityCode = (pick.getPickingOrder() == null ? null
							: pick.getPickingOrder().getOrderNumber());
					unitLoad = inventoryBusiness.createUnitLoad(pick.getClient(), pickTo.label, type, storageLocation,
							StockState.PICKED, activityCode, null, null);

				}
				pickingUnitLoad = pickingUnitLoadService.create(unitLoad);
				pickingUnitLoad.setPickingOrder(pick.getPickingOrder());
				pickingUnitLoad.setPositionIndex(pickTo.index);
			}
		}
		
		pickingBusiness.confirmPick(pick, pickingUnitLoad, amountPicked, amountRemain, serialNoList, counted);
		
	}

	public void confirmCompletePick(PickingMobilePos pickTO, String targetLocName) throws FacadeException {
		String logStr = "confirmPick ";
		PickingOrderLine pick = getPickingPosition(pickTO.id);
		if( pick == null ) {
			log.warn(logStr+"Pick not found. id="+pickTO.id);
			throw new LOSExceptionRB("CannotReadCurrentPick", this.getClass());
		}

		StorageLocation targetLoc = null;
		if( targetLocName == null ) {
			locationService.getClearing();
		}
		else {
			targetLoc = locationService.read(targetLocName);
		}

		if( targetLoc == null ) {
			log.error(logStr+"location not found. name="+targetLocName);
			throw new LOSLocationException(LOSLocationExceptionKey.NO_SUCH_LOCATION, new Object[]{targetLocName});
		}

		pickingBusiness.confirmCompletePick(pick, targetLoc);
	}

	public void transferUnitLoad(String label, String targetLocName, int state) throws FacadeException {
		String logStr = "transferUnitLoad ";
		
		Packet pickingUnitLoad = null;
		pickingUnitLoad = pickingUnitLoadService.readFirstByLabel(label);
		if( pickingUnitLoad == null ) {
			log.info(logStr+"unit load not found. Cannot transfer. label="+label);
			return;
		}
		
		StorageLocation targetLoc = null;
		if( targetLocName == null ) {
			locationService.getClearing();
		}
		else {
			targetLoc = locationService.read(targetLocName);
		}
		
		if( targetLoc == null ) {
			log.error(logStr+"location not found. name="+targetLocName);
			throw new LOSLocationException(LOSLocationExceptionKey.NO_SUCH_LOCATION, new Object[]{targetLocName});
		}
		
		pickingBusiness.confirmPickingUnitLoad(pickingUnitLoad, targetLoc, state);
	}

	public String generatePickToLabel() throws FacadeException {
		String logStr = "generatePickToLabel ";
		log.debug(logStr);

		for( int i = 0; i < 1000; i++ ) {
			String label = sequenceService.generateUnitLoadLabelId(null, null);
			UnitLoad ul = null;
			ul = unitLoadService.readByLabel(label);
			if( ul == null ) {
				return label;
			}
			log.warn(logStr+"Label already exists. Try next. label="+label);
		}
		log.error(logStr+"Was not able to generate new label!");
		
		return null;
	}
	
	public void changeUnitLoadLabel( String labelOld, String labelNew, long pickingOrderId ) throws FacadeException {
		String logStr = "changeUnitLoadLabel ";
		
		if( labelOld != null && labelNew != null && labelOld.equals(labelNew) ) {
			log.info(logStr+"Label did not change. label="+labelOld);
			return;
		}
		
		UnitLoad unitLoadNew = null;

		unitLoadNew = unitLoadService.readByLabel(labelNew);
		if( unitLoadNew != null ) {
			log.warn(logStr+"Unit load already exists. label="+labelNew);
			throw new LOSExceptionRB( "UnitLoadAlreadyExists", this.getClass() );
		}

		if( labelOld == null ) {
			log.info(logStr+"Label does not exists => do not change. label="+labelOld);
			return;
		}
		
		UnitLoad unitLoad = null;
		unitLoad = unitLoadService.readByLabel(labelOld);
		if( unitLoad == null )  { 
			log.info(logStr+"Label does not exists => do not change. label="+labelOld);
			return;
		}

		unitLoad.setLabelId(labelNew);
		
	}
	
	public PickingMobileUnitLoad createUnitLoad( String label, long pickingOrderId, int index ) throws FacadeException {
		String logStr = "createUnitLoad ";
		UnitLoad unitLoad = null;
		unitLoad = unitLoadService.readByLabel(label);
		if( unitLoad != null ) {
			log.warn(logStr+"Unit load already exists. label="+label);
			throw new LOSExceptionRB( "UnitLoadAlreadyExists", this.getClass() );
		}
			
		UnitLoadType type = unitLoadTypeService.getPicking();
		Client client = contextService.getCallersClient();
		StorageLocation storageLocation = locationService.getCurrentUsersLocation();

		PickingOrder pickingOrder = null;
		try {
			pickingOrder = pickingOrderService.get(pickingOrderId);
		}
		catch( EntityNotFoundException e ) {
			log.warn(logStr+"Picking order not found. id="+pickingOrderId);
			throw new LOSExceptionRB( "CannotReadCurrentOrder", this.getClass() );
		}

		unitLoad = inventoryBusiness.createUnitLoad(pickingOrder.getClient(), label, type, storageLocation,
				StockState.PICKED, pickingOrder.getOrderNumber(), null, null);
		Packet pul = pickingUnitLoadService.create(unitLoad);
		pul.setPickingOrder(pickingOrder);
		pul.setPositionIndex(index);

		return new PickingMobileUnitLoad(pul);
	}

	public boolean removeUnitLoadIfEmpty( String label ) throws FacadeException {
		String logStr = "removeUnitLoadIfEmpty ";
		
		
		Packet pickingUnitLoad = null;
		UnitLoad unitLoad = null;
		pickingUnitLoad = pickingUnitLoadService.readFirstByLabel(label);
		if( pickingUnitLoad == null ) {
			log.info(logStr+"Picking unit load not found. Try LOSUnitLoad. label="+label);
			unitLoad = unitLoadService.readByLabel(label);
			if( unitLoad == null ) {
				return true;
			}
		}
		else {
			unitLoad = pickingUnitLoad.getUnitLoad();
		}

		if( unitLoad.getStockUnitList().size()>0 ) {
			return false;
		}
		
		
		List<PickingOrderLine> pickList = pickingPositionService.getByPickToUnitLoad(pickingUnitLoad);
		if( pickList != null && pickList.size()>0 ) {
			log.info(logStr+"Some picks are referencing the picking unit load. Cannot remove. label="+label);
			return false;
		}
		
		log.info(logStr+"Remove unused unit load. label="+label);
		
		if( pickingUnitLoad != null )  {
			manager.remove(pickingUnitLoad);
		}
		if( unitLoad != null ) {
			manager.remove(unitLoad);
		}

		return true;
	}
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getCalculatedPickingOrders(String code, Long clientId, Long workingAreaId, boolean usePick, boolean useTransport, int limit) {
		String logStr = "getCalculatedPickingOrders ";
		Date dateStart = new Date();
		
		log.debug(logStr+"Search picking orders. code="+code+", usePick="+usePick+", useTransport="+useTransport);
		
		User user = contextService.getCallersUser();
		Client clientUser = user.getClient();
		Client clientSearch = null;
//		client = manager.find(Client.class, client.getId());
		if( clientId != null ) {
			clientSearch = manager.find(Client.class, clientId);
		}

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT po FROM ");
		sb.append(PickingOrder.class.getSimpleName()+" po");
		sb.append(" WHERE po.state>="+State.PROCESSABLE+" and po.state<"+State.FINISHED);
		sb.append(" AND ( ");
		sb.append("     ( po.state="+State.PROCESSABLE+" )");
		sb.append("  OR ( exists( select 1 from "+User.class.getSimpleName()+" user where user=po.operator and user=:user ) )");
		sb.append(" ) ");
		if (!clientUser.isSystemClient()) {
			sb.append(" AND po.client=:clientUser ");
		}
		if ( clientSearch != null ) {
			sb.append(" AND po.client=:clientSearch ");
		}
		if( !StringTools.isEmpty(code) ) {
//			sb.append(" and lower(po.number) like :code");
			sb.append(" and (");
			sb.append("     lower(po.orderNumber) like :code");
			sb.append("  or exists(select 1 from "+DeliveryOrder.class.getSimpleName()+" co, "+DeliveryOrderLine.class.getSimpleName()+" cp, "+PickingOrderLine.class.getSimpleName()+" pp ");
			sb.append("     where pp.pickingOrder=po and pp.deliveryOrderLine=cp and cp.deliveryOrder=co ");
			sb.append("       and co.state<"+State.PICKED+" and cp.state<"+State.PICKED);
			sb.append("       and (lower(co.orderNumber) like :code or lower(co.externalNumber) like :code) )");
			sb.append(" )");
		}

		sb.append(" and exists( select 1 from "+PickingOrderLine.class.getSimpleName()+" pp ");
		if( workingAreaId != null ) {
			sb.append(", ");
			sb.append(StockUnit.class.getSimpleName()+" su, ");
			sb.append(UnitLoad.class.getSimpleName()+" ul, ");
			sb.append(StorageLocation.class.getSimpleName()+" loc, ");
			sb.append(LocationCluster.class.getSimpleName()+" cluster, ");
			sb.append(LOSWorkingAreaPosition.class.getSimpleName()+" workingAreaPos ");
		}
		sb.append("  WHERE pp.pickingOrder = po and pp.pickingType in (:typeList)");
		if( workingAreaId != null ) {
			sb.append(" and su=pp.pickFromStockUnit");
			sb.append(" and ul=su.unitLoad");
			sb.append(" and loc=ul.storageLocation");
			sb.append(" and cluster=loc.cluster");
			sb.append(" and cluster=workingAreaPos.cluster");
			sb.append(" and workingAreaPos.workingArea.id=:workingAreaId");
		}
		sb.append(")");

		sb.append(" ORDER BY po.state DESC, po.prio, po.created ASC");
		
		log.debug(logStr+"query = "+sb.toString());
		
		Query query = manager.createQuery(sb.toString());
		
		List<Integer> typeList = new ArrayList<Integer>();
		typeList.add(Integer.valueOf(PickingType.DEFAULT));
		if( usePick ) {
			typeList.add(Integer.valueOf(PickingType.PICK));
		}
		if( useTransport ) {
			typeList.add(Integer.valueOf(PickingType.COMPLETE));
		}
		query.setParameter("typeList", typeList);
		query.setParameter("user", user);
		
		
		if (!clientUser.isSystemClient()) {
			query.setParameter("clientUser", clientUser);
		}
		if ( clientSearch != null ) {
			query.setParameter("clientSearch", clientSearch);
		}
		
		if( !StringTools.isEmpty(code) ) {
			query.setParameter("code", "%"+code.toLowerCase()+"%");
		}
		if( workingAreaId != null ) {
			query.setParameter("workingAreaId", workingAreaId);
		}
		if( limit>=0 ) {
			query.setMaxResults(limit);
		}
		
		List<PickingOrder> res = query.getResultList();
		
		ArrayList<SelectItem> orderSelectList = new ArrayList<SelectItem>();
		for( PickingOrder o : res ) {
			String label = manageMobile.getPickingSelectionText(o);
			orderSelectList.add(new SelectItem(o.getId(), label));
		}

		Date dateEnd = new Date();
		log.debug(logStr+"Found "+res.size()+" orders in "+(dateEnd.getTime()-dateStart.getTime())+" ms");
		return orderSelectList;
	}

	
	@SuppressWarnings("unchecked")
	public List<PickingOrder> getStartedOrders(boolean useTransport) {
		String logStr = "getStartedOrders ";
		Date dateStart = new Date();
		
		log.debug(logStr);
		
		User user = contextService.getCallersUser();

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT po FROM "+PickingOrder.class.getSimpleName()+" po ");
		sb.append(" WHERE po.operator=:user AND po.state >="+State.STARTED+" AND po.state<"+State.FINISHED);
		if( !useTransport ) {
			sb.append(" and not exists (select 1 from "+PickingOrderLine.class.getSimpleName()+" pos where pos.pickingOrder=po and pos.pickingType="+PickingType.COMPLETE+")");
		}
		sb.append(" ORDER BY po.state DESC, po.prio, po.created ASC");
		log.debug(logStr+"query = "+sb.toString());
		Query query = manager.createQuery(sb.toString());
		query.setParameter("user", user);
		List<PickingOrder> res = query.getResultList();
		
		// initialize positions
		for( PickingOrder order : res ) {
			for( PickingOrderLine pick : order.getLines() ) {
				pick.getId();
			}
		}
		
		Date dateEnd = new Date();
		log.debug(logStr+"Found "+res.size()+" orders in "+(dateEnd.getTime()-dateStart.getTime())+" ms");
		return res;
	}
	
	public PickingMobileUnitLoad readUnitLoad( long orderId ) {
		String logStr = "readUnitLoad ";
		Packet unitLoadUsable = null;
		PickingOrder order = null;
		try {
			order = pickingOrderService.get(orderId);
			List<Packet> unitLoadList = pickingUnitLoadService.readByPickingOrder(order);
			for( Packet unitLoad : unitLoadList ) {
				if( unitLoad.getState() < State.PICKED ) {
					unitLoadUsable = unitLoad;
					break;
				}
			}
		} catch (EntityNotFoundException e) {}
		if( unitLoadUsable == null || order == null ) {
			log.info(logStr+"No unit load defined for order. id="+orderId);
			return null;
		}
		
		PickingMobileUnitLoad mul = new PickingMobileUnitLoad(unitLoadUsable);

		return mul;
	}
	
	public List<PickingMobileUnitLoad> readUnitLoadList( long orderId ) {
		String logStr = "readUnitLoadList ";
		PickingOrder order = null;
		List<PickingMobileUnitLoad> unitLoadUsableList = new ArrayList<PickingMobileUnitLoad>();
		try {
			order = pickingOrderService.get(orderId);
			List<Packet> unitLoadList = pickingUnitLoadService.readByPickingOrder(order);
			for( Packet unitLoad : unitLoadList ) {
				log.debug(logStr+"Found unit load for order. unitload="+unitLoad+", state="+unitLoad.getState()+", order="+order);
				if( unitLoad.getState() <= State.PICKED ) {
					unitLoadUsableList.add(new PickingMobileUnitLoad(unitLoad));
				}
			}
		} catch (EntityNotFoundException e) {}
		

		return unitLoadUsableList;
	}	
	public List<PickingMobilePos> readPickList( long orderId ) {
		String logStr = "readPickList ";
		PickingOrder order = null;
		try {
			order = pickingOrderService.get(orderId);
		} catch (EntityNotFoundException e) {}
		if( order == null ) {
			log.info(logStr+"Order not found. id="+orderId);
			return null;
		}
		
		List<PickingOrderLine> pickList = order.getLines();
		List<PickingMobilePos> pickListMobile = new ArrayList<PickingMobilePos>();
		
		for( PickingOrderLine pick : pickList ) {
			List<ItemDataNumber> numberList = eanService.getListByItemData(pick.getItemData());
			List<String> eanList = null;
			if( numberList != null && numberList.size()>0 ) {
				eanList = new ArrayList<String>();
				for( ItemDataNumber number : numberList ) {
					eanList.add(number.getNumber());
				}
			}
			boolean isFixedLocation = false;
			StockUnit su = pick.getPickFromStockUnit();
			if( su != null ) {
				UnitLoad ul = su.getUnitLoad();
				isFixedLocation = fixService.exists(null, ul.getStorageLocation() );
			}
			PickingMobilePos pickMobile = new PickingMobilePos();
			pickMobile.init(pick, eanList, isFixedLocation);
			pickListMobile.add(pickMobile);
		}

		return pickListMobile;
	}
	public PickingMobilePos reloadPick(PickingMobilePos to) {
		String logStr = "reloadPick ";
		
		PickingOrderLine pick = getPickingPosition(to.id);
		if( pick == null ) {
			log.warn(logStr+"Failed to reload pick. id="+to.id);
			return to;
		}

		to.init(pick);
		return to;
	}
	
	
	public void checkSerial( String clientNumber, String itemNumber, String code ) throws FacadeException {
		String logStr = "checkSerial ";
		Client client = clientService.getByNumber(clientNumber);
		
		ItemData item = itemDataService.getByItemNumber(client, itemNumber);
		
		// 01.07.2013, krane. Service does not work correct. It's ignoring the item.
//		List<StockUnit> stockList = stockUnitService.getBySerialNumber(item, code);
		
        Query query = manager.createQuery("SELECT count(*) FROM "
                + StockUnit.class.getSimpleName() + " su "
                + " WHERE su.itemData=:item and su.serialNumber = :serialNumber");
        query = query.setParameter("item", item);
        query = query.setParameter("serialNumber", code);
        Long numSerial = 0L; 
        try{
        	numSerial = (Long)query.getSingleResult();
        } catch(Throwable t){
        	log.error(logStr+t.getClass().getSimpleName()+", "+t.getMessage());
        }
	        
		if( numSerial > 0 ) {
			throw new LOSExceptionRB("SerialDuplicate", this.getClass());
		}
	}

	public PickingMobilePos changePickFromStockUnit( PickingMobilePos to, String label ) throws FacadeException {
		String logStr = "changePickFromStockUnit ";
		
		UnitLoad unitLoadNew = null; 
		unitLoadNew = unitLoadService.readByLabel(label);
		if( unitLoadNew == null ) {
			log.info(logStr+"No unit load found. label="+label);
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_UNITLOAD, label);
		}
		
		PickingOrderLine pick = getPickingPosition(to.id);
		if( pick == null ) {
			log.info(logStr+"The current picking position cannot be read");
			throw new LOSExceptionRB("CannotReadCurrentPick", this.getClass());
		}
		UnitLoad unitLoadOrg = pick.getPickFromStockUnit().getUnitLoad();

		if( !unitLoadNew.getStorageLocation().equals(unitLoadOrg.getStorageLocation()) ) {
			log.info(logStr+"The unit load is located on a different location. label="+label);
			throw new LOSExceptionRB("SwitchLocationMismatch", this.getClass());
		}
		
		if( unitLoadNew.getStockUnitList().size() != 1 ) {
			log.info(logStr+"Cannot switch to mixed unit load. label="+label);
			throw new LOSExceptionRB("SwitchMixUnitLoadMismatch", this.getClass());
		}
		StockUnit stockNew = unitLoadNew.getStockUnitList().get(0);
		
		if( !stockNew.getItemData().equals(pick.getItemData()) ) {
			log.info(logStr+"Wrong item data on unit load. label="+label);
			throw new LOSExceptionRB("SwitchWrongItemData", this.getClass());
		}
		
		if( pick.getDeliveryOrderLine() != null ) {
			if( pick.getDeliveryOrderLine().getLot() != null ) {
				if( !pick.getDeliveryOrderLine().getLot().equals(stockNew.getLot()) ) {
					log.info(logStr+"Wrong lot on unit load. label="+label);
					throw new LOSExceptionRB("SwitchWrongItemLot", this.getClass());
				}
			}
		}
		else {
			log.info(logStr+"Predefined unit load required");
			throw new LOSExceptionRB("SwitchPredefinedRequired", this.getClass());
		}
		
		pick = pickingBusiness.changePickFromStockUnit(pick, stockNew);
		
		to.init(pick);
		
		return to;
	}
	
	public void printLabel( String label, String printer ) throws FacadeException {
		String logStr = "printLabel ";
		UnitLoad unitLoad = null; 
		unitLoad = unitLoadService.readByLabel(label);
		if( unitLoad == null ) {
			log.warn(logStr+"Cannot read unit load. label="+label);
			throw new LOSExceptionRB("CannotReadUnitLoad", this.getClass());
		}
		
		Document receipt = unitLoadReport.generateReport(unitLoad);
		printService.print(printer, receipt.getData(), receipt.getDocumentType());

	}

    @SuppressWarnings("unchecked")
	public List<SelectItem> getWorkingAreaList(String code) {
        List<LOSWorkingArea> waList = null;
    	if( StringTools.isEmpty(code) ) {
            String queryStr = "SELECT wa FROM " + LOSWorkingArea.class.getSimpleName() + " wa ORDER BY name ";
            Query query = manager.createQuery(queryStr);
            waList = query.getResultList();
    	}
    	else {
            String queryStr = "SELECT wa FROM " + LOSWorkingArea.class.getSimpleName() + " wa WHERE lower(wa.name) like :code ORDER BY name ";
            Query query = manager.createQuery(queryStr);
            query.setParameter("code", code.toLowerCase());
            waList = query.getResultList();
    		if( waList == null || waList.size()==0 ) {
    	        query.setParameter("code", "%"+code.toLowerCase()+"%");
    	        waList = query.getResultList();
    		}
    	}
		
		List<SelectItem> selectList = new ArrayList<SelectItem>();
		for( LOSWorkingArea workingArea : waList ) {
			selectList.add( new SelectItem(workingArea.getId(),workingArea.getName()) );
		}
		return selectList;
    }
    
	public void checkLocationScan( String name ) throws FacadeException {
		String logStr = "checkLocationScan ";
		StorageLocation loc = locationService.read(name);
		if( loc.getId() < 1 ) {
			log.info(logStr+"Invalid location. id="+loc.getId());
			throw new LOSExceptionRB("LocationInvalid", this.getClass());
		}
		if( loc.getUnitLoads().size()>1 ) {
			log.info(logStr+"Too much unit loads on location. name="+name);
			throw new LOSExceptionRB("LocationNotUnique", this.getClass());
		}
	}

	
	private PickingOrder getPickingOrder(long orderId) {
		PickingOrder order = null;
		try {
			order = pickingOrderService.get(orderId);
		} catch (EntityNotFoundException e) {}
		return order;
	}
	
	private PickingOrderLine getPickingPosition(long id) {
		PickingOrderLine pick = null;
		try {
			pick = pickingPositionService.get(id);
		} catch (EntityNotFoundException e) {}
		return pick;
	}

	@Override
	public Comparator<PickingMobilePos> getPickingComparator() {
		return manageMobile.getPickingComparator();
	}
	
	
}
