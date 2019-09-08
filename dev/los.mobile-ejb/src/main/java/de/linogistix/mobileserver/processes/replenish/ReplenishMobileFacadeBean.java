package de.linogistix.mobileserver.processes.replenish;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.User;
import org.mywms.service.ClientService;

import de.linogistix.los.common.exception.LOSExceptionRB;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.service.ItemDataService;
import de.linogistix.los.model.State;
import de.linogistix.los.util.StringTools;
import de.linogistix.los.util.businessservice.ContextService;
import de.wms2.mywms.client.ClientBusiness;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.StockUnitEntityService;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadEntityService;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.location.StorageLocationEntityService;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.replenish.ReplenishBusiness;
import de.wms2.mywms.replenish.ReplenishOrder;
import de.wms2.mywms.replenish.ReplenishOrderEntityService;
import de.wms2.mywms.strategy.FixAssignment;
import de.wms2.mywms.strategy.FixAssignmentEntityService;

// TODO krane: I18N
@Stateless
public class ReplenishMobileFacadeBean implements ReplenishMobileFacade {
	Logger log = Logger.getLogger(ReplenishMobileFacadeBean.class);
	
	@Inject
	private ClientBusiness clientBusiness;
	@EJB
	private ClientService clientService;
	@EJB
	private ContextService contextService;
	@Inject
	private FixAssignmentEntityService fixService;
	@EJB
	private ItemDataService itemDataService;

    @PersistenceContext(unitName = "myWMS")
    protected EntityManager manager;

	@Inject
	private StockUnitEntityService stockUnitService;
	@Inject
	private StorageLocationEntityService locationService;
	@Inject
	private UnitLoadEntityService unitLoadService;
	@Inject
	private ReplenishBusiness replenishBusiness;
	@Inject
	private ReplenishOrderEntityService replenishService;

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


	public ReplenishMobileOrder loadOrderByDestination( String locationName ) throws FacadeException {
		StorageLocation loc = locationService.read(locationName);
		if( loc == null ) {
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_STORAGELOCATION, locationName);
		}
		FixAssignment fix = fixService.readFirstByLocation(loc);
		if( fix == null ) {
			throw new InventoryException(InventoryExceptionKey.NOT_A_FIXED_ASSIGNED_LOCATION, new Object[]{});
		}
		
		ReplenishMobileOrder order = new ReplenishMobileOrder();
		List<ReplenishOrder> active = replenishService.readOpenByDestination(loc);
		if (active != null && active.size() > 0) {
			for( ReplenishOrder o : active ) {
				if( o.getState()>State.PROCESSABLE ) {
					throw new LOSExceptionRB("MsgReplenishAlreadyStarted", this.getClass());
				}
				StockUnit sourceStockUnit = manager.find(StockUnit.class, o.getSourceStockUnitId());
				order.setOrder(o, sourceStockUnit);
				break;
			}
		}
		
		order.setItem(fix.getItemData());
		order.setDestination(loc);
		readAddOn(order);
		
		return order;
	}
	
	public ReplenishMobileOrder loadOrderById(long id) {
		ReplenishOrder order = manager.find(ReplenishOrder.class, id);
		if( order == null ) {
			return null;
		}
		StockUnit sourceStockUnit = manager.find(StockUnit.class, order.getSourceStockUnitId());
		ReplenishMobileOrder mOrder = new ReplenishMobileOrder();
		mOrder.setOrder(order, sourceStockUnit);
		readAddOn(mOrder);
		
		return mOrder;
	}
	
	public void startOrder( ReplenishMobileOrder mOrder ) throws FacadeException {
		ReplenishOrder order = readReplenishOrder(mOrder);
		User user = contextService.getCallersUser();
		replenishBusiness.startOperation(order, user);
	}
	
	public void resetOrder( ReplenishMobileOrder mOrder ) throws FacadeException {
		ReplenishOrder order = readReplenishOrder(mOrder);
		replenishBusiness.resetOrder(order);
	}
	
	public ReplenishMobileOrder checkSource( ReplenishMobileOrder mOrder, String code ) throws FacadeException {
		String logStr = "checkSource ";
		if( code.equals(mOrder.getSourceUnitLoadLabel()) ) {
			return mOrder;
		}
		
		log.debug(logStr+"Check location");
		if( code.equals(mOrder.getSourceLocationName()) || code.equals(mOrder.getSourceLocationCode()) || code.toLowerCase().equals(mOrder.getSourceLocationName().toLowerCase())  ) {
			StorageLocation location = locationService.read(code);
			if( location == null ) {
				log.info(logStr+"No storage location found for code="+code);
				throw new LOSExceptionRB("MsgLocationNotFound", this.getClass());
			}
			List<UnitLoad> unitLoadList = unitLoadService.readByLocation(location);
			if( unitLoadList.size()!=1) {
				log.info(logStr+"More than one unit load on location, code="+code);
				throw new LOSExceptionRB("MsgMoreThanOnUnitLoad", this.getClass());
			}
			
			return mOrder;
		}
		
		log.debug(logStr+"Check unit load");
		UnitLoad unitLoad = unitLoadService.readByLabel(code);
		if( unitLoad != null ) {
			// A different unit load has been scanned
		
			// The location must be the requested
			if( !unitLoad.getStorageLocation().getName().equals(mOrder.getSourceLocationName()) ) {
				log.info("The unit load is located on a different location than requested");
				throw new LOSExceptionRB("MsgUnitLoadOnDifferentLocation", this.getClass());
			}
			
			// The material must be unique
			if( unitLoad.getStockUnitList().size() != 1 ) {
				log.info("The switch of mixed unit loads is not possible");
				throw new LOSExceptionRB("MsgSwitchMixedUnitLoadNotAllowed", this.getClass());
			}

			StockUnit stock = unitLoad.getStockUnitList().get(0);
			if( !stock.getItemData().getNumber().equals(mOrder.getItemNumber()) ) {
				log.info("The material is different");
				throw new LOSExceptionRB("MsgWrongMaterial", this.getClass());
			}
			
			if( stock.getReservedAmount().compareTo(BigDecimal.ZERO)>0 ) {
				// TODO: check switch of reservation
				log.info("The stock is reserved");
				throw new LOSExceptionRB("MsgStockReserved", this.getClass());
			}
			
			log.info(logStr+"Switch unit load. old="+mOrder.getSourceUnitLoadLabel()+", new="+unitLoad.getLabelId());
			// Only allow switch for this request. The original reservation remains
			// Hopefully nobody tries to remove the scanned unit load in between
			mOrder.setStock(stock);
			
			return mOrder;
		}
		
		
		throw new LOSExceptionRB("MsgEnterRequestedUnitLoad", this.getClass());
	}
	
	public ReplenishMobileOrder checkDestination( ReplenishMobileOrder mOrder, String code ) throws FacadeException {
		String logStr = "checkDestination ";
		if( code.equals(mOrder.getSourceUnitLoadLabel()) ) {
			return mOrder;
		}
		
		log.debug(logStr+"Check location");
		if( code.equals(mOrder.getDestinationLocationName()) || code.equals(mOrder.getDestinationLocationCode()) ) {
			return mOrder;
		}
		if( code.toLowerCase().equals(mOrder.getDestinationLocationName().toLowerCase()) ) {
			return mOrder;
		}
		
		// TODO: check different or null location
		throw new LOSExceptionRB("MsgEnterTargetLocation", this.getClass());
	}
	
	public void confirmOrder( ReplenishMobileOrder mOrder ) throws FacadeException {
		String logStr = "confirmOrder ";
		log.debug(logStr);
		ReplenishOrder order = readReplenishOrder(mOrder);

		StockUnit sourceStock = null;
		if( mOrder.getSourceStockId()>0 ) {
			sourceStock = manager.find(StockUnit.class, mOrder.getSourceStockId());
		}
		
		StorageLocation destinationLocation = null;
		if( mOrder.getDestinationLocationName() != null ) {
			destinationLocation = locationService.read(mOrder.getDestinationLocationName());
		}
		
		replenishBusiness.confirmOrder(order, sourceStock, destinationLocation, mOrder.getAmountPicked(), null);
	}
	
	public void checkAmountPicked(ReplenishMobileOrder mOrder, BigDecimal amount ) throws FacadeException {
		String logStr = "checkAmountPicked ";
		
		if( BigDecimal.ZERO.compareTo(amount)>=0 ) {
			log.debug(logStr+"Amount must be > 0");
			throw new LOSExceptionRB("MsgEnterNotZeroAmount", this.getClass());
		}
		
		StockUnit stock = null;
		stock = manager.find(StockUnit.class, mOrder.getSourceStockId());
		if( stock == null ) {
			log.debug(logStr+"Cannot read source stock. id="+mOrder.getSourceStockId());
			throw new LOSExceptionRB("MsgSourceStockNotFound", this.getClass());
		}
		if( stock.getAmount().compareTo(amount)<0 ) {
			log.debug(logStr+"Entered amount > available amount. entered="+amount+", onstock="+stock.getAmount());
			throw new LOSExceptionRB("MsgTooMuchRequested", this.getClass());
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getCalculatedOrders( String code ) {
		String logStr = "getCalculatedOrders ";
		Date dateStart = new Date();
		
		if( !StringTools.isEmpty(code) ) {
			code = "%"+code.toLowerCase()+"%";
		}	

		log.debug(logStr+"Search replenish orders. code="+code);
		
		User user = contextService.getCallersUser();
		Client client = user.getClient();

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT o FROM "+ReplenishOrder.class.getName()+ " o ");
		sb.append(" WHERE o.state="+State.PROCESSABLE);
		if (!client.isSystemClient()) {
			sb.append(" AND o.client=:client ");
		}
		if( !StringTools.isEmpty(code) ) {
			sb.append(" AND ( LOWER(o.number) like :code OR LOWER(o.destination.name) like :code OR LOWER(o.destination.scanCode) like :code OR LOWER(o.itemData.number) like :code OR LOWER(o.itemData.name) like :code )");
		}
		sb.append(" ORDER BY o.prio, o.created ASC");
		log.debug(logStr+"query = "+sb.toString());
		Query query = manager.createQuery(sb.toString());
		if (!client.isSystemClient()) {
			query.setParameter("client", client);
		}
		if( !StringTools.isEmpty(code) ) {
			query.setParameter("code", code);
		}		
		List<ReplenishOrder> res = query.getResultList();
		
		ArrayList<SelectItem> orderSelectList = new ArrayList<SelectItem>();
		for (ReplenishOrder order : res) {
			StorageLocation destination = order.getDestinationLocation();
			String label = destination.getName();
			StorageLocation sourceLocation = order.getSourceLocation();
			if (sourceLocation != null) {
				label += " (" + sourceLocation.getName() + ")";
			}
			orderSelectList.add(new SelectItem(order.getId(), label));
		}
		
		
		Date dateEnd = new Date();
		log.debug(logStr+"Found "+res.size()+" orders in "+(dateEnd.getTime()-dateStart.getTime())+" ms");
		return orderSelectList;
	}
	
	
	public ReplenishMobileOrder requestReplenish(ReplenishMobileOrder mOrder) throws FacadeException {
		
		Client client = clientService.getByNumber(mOrder.getClientNumber());
		ItemData item = itemDataService.getByItemNumber(client, mOrder.getItemNumber());
		StorageLocation loc = locationService.read(mOrder.getDestinationLocationName());
		
		ReplenishOrder order = replenishBusiness.generateOrder(item, mOrder.getAmountRequested(), loc);
		if( order == null ) {
			return null;
		}
		StockUnit sourceStockUnit = manager.find(StockUnit.class, order.getSourceStockUnitId());
		ReplenishMobileOrder mOrderNew = new ReplenishMobileOrder();
		mOrderNew.setOrder(order, sourceStockUnit);
		readAddOn(mOrderNew);
		return mOrderNew;
	}
	
	@SuppressWarnings("unchecked")
	public ReplenishMobileOrder getReservedOrder() throws FacadeException {
		String logStr = "getReservedOrder ";
		
		log.debug(logStr+"Search reserved order.");
		
		User user = contextService.getCallersUser();
		Client client = user.getClient();

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT o FROM "+ReplenishOrder.class.getName()+ " o ");
		sb.append(" WHERE o.operator=:user AND o.state >"+State.PROCESSABLE+" AND o.state<"+State.FINISHED);
		if (!client.isSystemClient()) {
			sb.append(" AND o.client=:client ");
		}
		sb.append(" ORDER BY o.state DESC, o.prio, o.created ASC");
		log.debug(logStr+"query = "+sb.toString());
		Query query = manager.createQuery(sb.toString());
		query.setParameter("user", user);
		if (!client.isSystemClient()) {
			query.setParameter("client", client);
		}
		List<ReplenishOrder> res = query.getResultList();
		if( res.size() > 0 ) {
			ReplenishOrder order = (ReplenishOrder)res.get(0);
			ReplenishMobileOrder mOrder = new ReplenishMobileOrder();
			StockUnit sourceStockUnit = manager.find(StockUnit.class, order.getSourceStockUnitId());
			mOrder.setOrder(order, sourceStockUnit);
			readAddOn(mOrder);
			return mOrder;
		}

		return null;
	}

	
	
	private void readAddOn(ReplenishMobileOrder order) {
		if( order == null ) {
			return;
		}
		
		if( order.getDestinationLocationName() != null ) {
			BigDecimal amountDestination = BigDecimal.ZERO;
			
			StorageLocation destination = locationService.read(order.getDestinationLocationName());
			FixAssignment fix = fixService.readFirstByLocation(destination);
			if( fix != null ) {
				order.setAmountDestinationMax(fix.getMaxAmount());
			}
			List<StockUnit> stockList = stockUnitService.readByLocation(destination);
			for( StockUnit stock : stockList ) {
				if( stock.getItemData().getNumber().equals(order.getItemNumber()) ) {
					amountDestination = amountDestination.add(stock.getAmount());
				}
			}
			order.setAmountDestination(amountDestination);
		}
	}

	private ReplenishOrder readReplenishOrder(ReplenishMobileOrder mOrder) throws FacadeException {
		String logStr = "readReplenishOrder ";
		if (mOrder == null) {
			log.info(logStr + "Missing parameter mOrder.");
			throw new LOSExceptionRB("MsgCannotReadOrder", this.getClass());
		}
		ReplenishOrder order = manager.find(ReplenishOrder.class, mOrder.getId());
		if (order == null) {
			log.info(logStr + "ReplenishOrder not found. id=" + mOrder.getId());
			throw new LOSExceptionRB("MsgCannotReadOrder", this.getClass());
		}
		return order;
	}

}
