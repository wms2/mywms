/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */

package de.linogistix.los.inventory.businessservice;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.StockUnitService;

import de.linogistix.los.location.constants.LOSStorageLocationLockState;
import de.linogistix.los.location.model.LOSArea;
import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;

/**
 * @author krane
 *
 */
@Stateless
public class LOSReplenishStockServiceBean implements LOSReplenishStockService {
	private final static Logger log = Logger.getLogger(LOSReplenishStockServiceBean.class);

	@EJB
	private StockUnitService stockUnitService;
	
	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;

	private boolean preferCompleteAmount = true;




	public StockUnit findReplenishStock( ItemData itemData, Lot lot, BigDecimal amount, Collection<LOSStorageLocation> vetoLocations ) throws FacadeException {
		String logStr ="findReplenishStock ";

		List<ReplenishStockUnitTO> stockList = null;

		if( itemData == null ) {
			log.error(logStr+"itemData==null => Cannot find stock");
			return null;
		}
		if( amount == null ) {
			amount = BigDecimal.ZERO;
		}
		
//		log.debug(logStr+"itemNumber="+itemData.getNumber()+", lot="+(lot==null?"NULL":lot.getName())+", amount="+amount);

		
		stockList = readReplenishStockList( itemData, lot );
		if( stockList == null || stockList.size()==0 ) {
			log.info(logStr+"No stock available. Abort. itemNumber="+itemData.getNumber()+", lot="+(lot==null?"NULL":lot.getName()));
			return null;
		}

		
		// --------------------------------------------------------------------
		// 1. Find unit loads with enough amount
		// --------------------------------------------------------------------
		if( BigDecimal.ZERO.compareTo(amount)<0 && preferCompleteAmount ) {
			log.debug(logStr+"Search unit load with complete amount...");
			for( ReplenishStockUnitTO stock : stockList ) {
				if( stock.amount.compareTo(amount) < 0 ) {
					continue;
				}
				log.info(logStr+"Take stock with complete amount. amountMin="+amount+", stock="+stock);
				return readStockUnit(stock.stockId);
			}
		}
		
		log.debug(logStr+"Search replenish stock...");
		for( ReplenishStockUnitTO stock : stockList ) {
			log.info(logStr+"Take replenish stock, stock="+stock);
			return readStockUnit(stock.stockId);
		}

		log.info(logStr+"No stock found.");
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	private List<ReplenishStockUnitTO> readReplenishStockList( ItemData itemData, Lot lot ) {
		String logStr = "readReplenishStockList ";
//		log.debug(logStr+" itemData="+(itemData==null?"NULL":itemData.getNumber()));

		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT NEW ");
		sb.append(ReplenishStockUnitTO.class.getName());
		sb.append("(");
		sb.append("su.id,");
		sb.append("su.amount,");
		sb.append("su.reservedAmount,");
		sb.append("loc.id, loc.name");
		sb.append(") FROM ");			
		sb.append(StockUnit.class.getSimpleName()+" su, ");
		sb.append(LOSUnitLoad.class.getSimpleName()+" ul, ");
		sb.append(LOSStorageLocation.class.getSimpleName()+" loc, ");
		sb.append(LOSArea.class.getSimpleName()+" area ");
		sb.append("WHERE ul = su.unitLoad AND loc = ul.storageLocation AND area = loc.area ");
		sb.append(" AND ul.lock=0 AND loc.lock in (0,:lockStorage) and area.lock=0 ");
		sb.append(" AND su.lock=0 ");
		sb.append(" AND su.amount > 0 ");
		sb.append(" AND area.useForReplenish=true ");
		sb.append(" AND su.reservedAmount=0 ");
		sb.append(" AND su.itemData =:item ");
		sb.append(" AND not exists( select 1 from "+LOSFixedLocationAssignment.class.getSimpleName()+" fix ");
		sb.append(                " where fix.assignedLocation = loc )");
		if( lot != null ) {
			sb.append(" AND su.lot =:lot ");
		}
		sb.append(" ORDER BY su.strategyDate, su.amount, su.created ");

		Query query = manager.createQuery(sb.toString());

        query.setParameter("lockStorage", LOSStorageLocationLockState.STORAGE.getLock());
        query.setParameter("item", itemData);
		if( lot != null ) {
	        query.setParameter("lot", lot);
		}
        
		List<ReplenishStockUnitTO> ret = query.getResultList();
		
		if( ret == null || ret.size()==0 ) {
			log.debug("No Stock found: QUERY="+sb.toString());
		}
		
		return ret;
	}
	
	private StockUnit readStockUnit( long id ) {
		try {
			return stockUnitService.get(id);
		} catch (EntityNotFoundException e) {
			return null;
		}
	}
}

class ReplenishStockUnitTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public long stockId;
	public long locationId;
	public String locationName;
	public BigDecimal amount;
	public BigDecimal amountReserved;
	
	public ReplenishStockUnitTO(long id, BigDecimal amount, BigDecimal amountReserved, long locationId, String locationName) {
		this.stockId = id;
		this.locationId = locationId;
		this.locationName = locationName;
		this.amount = amount;
		this.amountReserved = amountReserved;
	}
	
	public String toString() {
		return "id="+stockId+", amount="+amount+", location="+locationName;
	}
	

		
}
