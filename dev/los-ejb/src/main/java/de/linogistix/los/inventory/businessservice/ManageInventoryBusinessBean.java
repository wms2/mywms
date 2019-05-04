/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
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

import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;
import org.mywms.model.UnitLoad;
import org.mywms.service.ClientService;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.ItemDataService;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.service.LOSLotService;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.entityservice.LOSStorageLocationTypeService;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSStorageLocationType;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote;
import de.linogistix.los.util.businessservice.ContextService;

/**
 * 
 * @author trautm
 */
@Stateless
public class ManageInventoryBusinessBean implements ManageInventoryBusiness {

	private final static Logger log = Logger
			.getLogger(ManageInventoryBusinessBean.class);

	@EJB
	private ClientService clientService;
	@EJB
	private LOSStorageLocationService slService;
	@EJB
	private ItemDataService idatService;
	
	@EJB 
	private LOSLotService lotService;
	@EJB
	private LOSStorageLocationTypeService typeService;
	@EJB
	private LOSStorageLocationQueryRemote slQuery;
	@EJB
	private LOSInventoryComponent invComp;
	@EJB
	private ContextService contextService;

	
	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;
		
	public StockUnit createStockUnitOnStorageLocation(String clientRef,
			String slName, String articleRef, String lotRef, BigDecimal amount,
			String unitLoadRef, String activityCode, String serialNumber) throws EntityNotFoundException,
			InventoryException, FacadeException {

		Client c;
		LOSStorageLocation sl;
		
		Lot lot;
		StockUnit su;
		UnitLoad ul;

		try {
			c = clientService.getByNumber(clientRef);
			Client callerClient = contextService.getCallersClient();
			if ((!callerClient.equals(c)) && (!callerClient.isSystemClient())) {
				throw new EJBAccessException();
			}
			
			sl = slQuery.queryByIdentity(slName);

			if (sl == null){
				log
						.warn("NOT FOUND. Going to CREATE StorageLocation "
								+ slName);
				LOSStorageLocationType type = typeService.getDefaultStorageLocationType();
				sl = slService.createStorageLocation(c, slName, type);
			}

			ItemData idat = idatService.getByItemNumber(c, articleRef);
			
			if(idat == null){
				log.error("--- !!! NO ITEM WITH NUMBER " + articleRef+" !!! ---");
				throw new InventoryException(InventoryExceptionKey.NO_SUCH_ITEMDATA, articleRef);
			}
			
			try {
				lot = getOrCreateLot(c, lotRef, idat);
				ul = invComp.getOrCreateUnitLoad(c, idat, sl, unitLoadRef);
			} 
			catch (Throwable ex) {
				log.error(ex.getMessage(), ex);
				throw new InventoryException(
						InventoryExceptionKey.CREATE_STOCKUNIT_ON_STORAGELOCATION_FAILED,
						slName);
			}
						
			su = invComp.createStock(c, lot, idat, amount, (LOSUnitLoad) ul, activityCode, serialNumber);
						
			invComp.consolidate((LOSUnitLoad) su.getUnitLoad(), activityCode);
			
			return su;
			
		} catch (FacadeException ex){
			throw ex;
		}
		catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new InventoryException(InventoryExceptionKey.CREATE_STOCKUNIT_ONSTOCK, "");
		}
	}

	protected Lot getOrCreateLot(Client c, String lotRef, ItemData idat) {
		Lot lot;
		
		if( lotRef == null ) {
			return null;
		}
		
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



	public void deleteStockUnitsFromStorageLocation(LOSStorageLocation sl, String activityCode)
			throws FacadeException {
		
		List<Long> sus = new ArrayList<Long>();
		List<Long> uls = new ArrayList<Long>();
		
		sl = manager.find(LOSStorageLocation.class, sl.getId());
		
		for (LOSUnitLoad ul : (List<LOSUnitLoad>)sl.getUnitLoads()){
			for (StockUnit su : ul.getStockUnitList()){
				sus.add(su.getId());
//				su = manager.find(StockUnit.class, su.getId());
//				manager.remove(su);
			}
			uls.add(ul.getId());
		}
		
		for (Long id : sus){
			StockUnit su = manager.find(StockUnit.class, id);
			if (su == null){
				continue;
			}
			deleteStockUnit(su, activityCode);
		}
		
		for (Long id : uls){
			UnitLoad ul = manager.find(UnitLoad.class, id);
			if (ul == null){
				continue;
			}
			manager.remove(ul);
		}
	}
	
	public void deleteStockUnit(StockUnit su, String activityCode) throws FacadeException {
		throw new RuntimeException();
	}
}
