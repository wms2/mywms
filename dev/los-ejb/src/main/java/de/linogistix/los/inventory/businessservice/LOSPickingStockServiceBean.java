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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.mywms.model.UnitLoad;

import de.linogistix.los.inventory.model.LOSOrderStrategy;
import de.linogistix.los.inventory.service.LOSOrderStrategyService;
import de.linogistix.los.location.constants.LOSStorageLocationLockState;
import de.linogistix.los.location.model.LOSArea;
import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.service.QueryFixedAssignmentService;
import de.linogistix.los.util.businessservice.ContextService;

/**
 * @author krane
 *
 */
@Stateless
public class LOSPickingStockServiceBean implements LOSPickingStockService {
	Logger log = Logger.getLogger(LOSPickingStockServiceBean.class);

	@EJB
	private QueryFixedAssignmentService fixService;
	@EJB
	private ContextService contextService;
	@EJB
	private LOSOrderStrategyService orderStrategyService;
	
	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;
	
	
	private boolean verbose = false;

	public StockUnit findPickFromStock( Client client, ItemData itemData, Lot lot, BigDecimal amount, String serialNumber, String reservationNumber, LOSOrderStrategy strategy ) throws FacadeException {
		String logStr ="findPickFromStock ";

		List<PickingStockUnitTO> stockList = null;
		if( client == null ) {
			client = contextService.getCallersClient();
		}

		if( itemData == null ) {
			log.error(logStr+"itemData==null => Cannot generate pick");
			return null;
		}
		if( amount == null ) {
			log.error(logStr+"amount==null => Cannot generate pick");
			return null;
		}
		if( strategy == null ) {
			log.error(logStr+"strategy==null => Cannot generate pick");
			return null;
		}
		if( BigDecimal.ZERO.compareTo(amount) >= 0 ) {
			log.error(logStr+"amount==0 => Cannot generate pick");
			return null;
		}
		
		log.debug(logStr+"clientNumber="+client.getNumber()+", itemNumber="+itemData.getNumber()+", lot="+(lot==null?"NULL":lot.getName())+", amount="+amount+", serialNumber="+serialNumber);

		stockList = readPickFromStockList( client, itemData, lot, serialNumber, reservationNumber, strategy, false );
		if( stockList == null || stockList.size()==0 ) {
			log.info(logStr+"No stock available. Abort. itemNumber="+itemData.getNumber()+", lot="+(lot==null?"NULL":lot.getName()+", serialNumber="+serialNumber));
			return null;
		}

		boolean hasFixedAssignment = false;
		List<LOSFixedLocationAssignment> fixList = fixService.getByItemData(itemData);
		if( fixList.size()>0 ) {
			hasFixedAssignment=true;
			for( LOSFixedLocationAssignment fix : fixList ) {
				for( PickingStockUnitTO stock : stockList ) {
					if( fix.getAssignedLocation().getId().equals(stock.locationId) ) {
						stock.fix = true;
						break;
					}
				}
			}
		}
		
		if( verbose ) {
			for( PickingStockUnitTO stock : stockList ) {
				log.info(logStr+stock);
			}
		}
		
		// --------------------------------------------------------------------
		// 1. Find complete and unopened unit loads usable for forklifts matching this pick
		// --------------------------------------------------------------------
		if( strategy.isPreferUnopened() ) {
			if( strategy.isPreferMatchingStock() ) {
				log.debug(logStr+"Search complete matching unit load...");
				for( PickingStockUnitTO stock : stockList ) {
					if( stock.fix ) {
						continue;
					}
					if( stock.opened ) {
						continue;
					}
					if( !stock.useForTransport ) {
						continue;
					}
					if( BigDecimal.ZERO.compareTo(stock.amountReserved) < 0 ) {
						continue;
					}
	
					if( stock.amountAvailable.compareTo(amount) == 0 ) {
						if( !isOneStockOnUnitLoad(stock.unitLoadLabel) ) {
							continue;
						}
						log.info(logStr+"Take matching unopened stock. "+stock);
						return readStockUnit(stock.stockId);
					}
				}
			}
			
			log.debug(logStr+"Search complete unit load...");
			for( PickingStockUnitTO stock : stockList ) {
				if( stock.fix ) {
					continue;
				}
				if( stock.opened ) {
					continue;
				}
				if( !stock.useForTransport ) {
					continue;
				}
				if( BigDecimal.ZERO.compareTo(stock.amountReserved) < 0 ) {
					continue;
				}
				
				if( stock.amountAvailable.compareTo(amount) <= 0 ) {
					if( !isOneStockOnUnitLoad(stock.unitLoadLabel) ) {
						continue;
					}
					log.info(logStr+"Take unopened stock. amountMax="+amount+", stock="+stock);
					return readStockUnit(stock.stockId);
				}
			}
		}
		
		
		// --------------------------------------------------------------------
		// 3. Look for pickable stock
		// --------------------------------------------------------------------
		if( hasFixedAssignment ) {
			log.debug(logStr+"Search fix stock...");
			for( PickingStockUnitTO stock : stockList ) {
				if( !stock.useForPick ) {
					continue;
				}
				
				if( !stock.fix ) {
					continue;
				}
				
				log.info(logStr+"Take fix stock. amountMax="+amount+", stock="+stock);
				return readStockUnit(stock.stockId);
			}
		}
		
		// --------------------------------------------------------------------
		// 4. Look for pickable stock
		// --------------------------------------------------------------------
		if( strategy.isPreferMatchingStock() ) {
			log.debug(logStr+"Search matching pick stock...");
			for( PickingStockUnitTO stock : stockList ) {
				if( !stock.useForPick ) {
					continue;
				}
				
				if( stock.amountAvailable.compareTo(amount) == 0 ) {
					log.info(logStr+"Take matching pick stock. amountMax="+amount+", stock="+stock);
					return readStockUnit(stock.stockId);
				}
			}
		}
			
		log.debug(logStr+"Search pick stock...");
		for( PickingStockUnitTO stock : stockList ) {
			if( !stock.useForPick ) {
				continue;
			}
			
			log.info(logStr+"Take pickable stock. amountMax="+amount+", stock="+stock);
			return readStockUnit(stock.stockId);
		}
		
		// --------------------------------------------------------------------
		// 4. Wait for replenishment or goods receipt
		// --------------------------------------------------------------------
		log.info(logStr+"No stock found.");
		return null;

	}
	
	public List<StockUnit> getPickFromStockList(Client client, ItemData itemData, Lot lot, String serialNumber, String reservationNumber, LOSOrderStrategy strategy, boolean readReserved) {
		String logStr = "getPickFromStockList ";
		
		if( strategy == null ) {
			strategy = orderStrategyService.getDefault(client);
		}
		
		if( strategy == null ) {
			log.error(logStr+"strategy==null => Cannot generate pick-from-stock-list");
			return null;
		}

		List<PickingStockUnitTO> stockToList = null;
		List<StockUnit> stockUnitList = new ArrayList<StockUnit>();
		
		stockToList = readPickFromStockList( client, itemData, lot, serialNumber, reservationNumber, strategy, false );
		for( PickingStockUnitTO to : stockToList ) {
			StockUnit su = readStockUnit(to.stockId);
			stockUnitList.add(su);
		}
		
		return stockUnitList;
	}
	
	
	@SuppressWarnings("unchecked")
	private List<PickingStockUnitTO> readPickFromStockList( Client client, ItemData itemData, Lot lot, String serialNumber, String reservationNumber, LOSOrderStrategy strategy, boolean readReserved) {
		String logStr = "readPickFromStockList ";
		Date dateStart = new Date();
		log.debug(logStr+" itemData="+(itemData==null?"NULL":itemData.getNumber()));

		if( serialNumber != null && serialNumber.length()==0 ) {
			serialNumber = null;
		}
		StringBuffer sb = new StringBuffer();

		sb.append(" SELECT NEW ");
		sb.append(PickingStockUnitTO.class.getName());
		sb.append("(su.id, su.amount, su.reservedAmount");
		sb.append(", area.useForPicking, area.useForStorage");
		sb.append(", loc.id, loc.name");
		sb.append(", ul.id, ul.labelId, ul.opened ");
		sb.append(") FROM ");			
		sb.append(StockUnit.class.getSimpleName()+" su, ");
		sb.append(LOSUnitLoad.class.getSimpleName()+" ul, ");
		sb.append(LOSStorageLocation.class.getSimpleName()+" loc, ");
		sb.append(LOSArea.class.getSimpleName()+" area ");
		sb.append("WHERE ul = su.unitLoad AND loc = ul.storageLocation AND loc.area=area ");
		sb.append(" AND ul.lock=0 AND loc.lock in (0,:lockStorage) and su.lock!=2 and area.lock=0 ");
		
		if( !strategy.isUseLockedStock() ) {
			sb.append(" AND su.lock=0 ");
		}
		sb.append(" AND su.amount > 0 ");
		if( ! readReserved ) {
			sb.append(" AND su.reservedAmount < su.amount ");
		}

		sb.append(" AND su.client =:client ");
		sb.append(" AND su.itemData =:item ");
		if( serialNumber != null ) {
			sb.append(" AND su.serialNumber =:serial ");
		}
		if( lot != null ) {
			sb.append(" AND su.lot =:lot ");
		}
		if( !strategy.isUseLockedLot() ) {
			sb.append(" and not exists( select 1 FROM "+Lot.class.getSimpleName()+" lot where su.lot=lot and lot.lock!=0) ");
		}

		sb.append(" ORDER BY su.strategyDate, su.amount, su.created ");
		
		Query query = manager.createQuery(sb.toString());

        query.setParameter("client", client);
        query.setParameter("item", itemData);
        query.setParameter("lockStorage", LOSStorageLocationLockState.STORAGE.getLock());

		if( serialNumber != null ) {
	        query.setParameter("serial", serialNumber);
		}
		if( lot != null ) {
	        query.setParameter("lot", lot);
		}
        
		List<PickingStockUnitTO> ret = query.getResultList();
		Date dateEnd=new Date();
		log.debug(logStr+"found stocks: "+ret.size()+" in "+(dateEnd.getTime()-dateStart.getTime())+" ms");
		return ret;
	}
	

	private boolean isOneStockOnUnitLoad( String label ) {
        String q = "SELECT count(*) FROM " +
        		StockUnit.class.getSimpleName() + " su, " +
        		UnitLoad.class.getSimpleName() + " ul " +
        		" WHERE ul = su.unitLoad and ul.labelId=:label ";
        Query query = manager.createQuery(q);
        query.setParameter("label", label);
	    Long i = (Long)query.getSingleResult();
	    return i == 1;
	}
	
	private StockUnit readStockUnit( long id ) {
		return manager.find(StockUnit.class, id);
	}



}

class PickingStockUnitTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public long stockId;
	public boolean opened = true;
	public long locationId;
	public boolean fix = false;
	public String locationName;
	public long unitLoadId;
	public String unitLoadLabel;
	public long lotId=0;
	
	public boolean useForPick = true;
	public boolean useForTransport = true;

	public BigDecimal amount;
	public BigDecimal amountReserved;
	public BigDecimal amountAvailable;
	
	public PickingStockUnitTO(long id, BigDecimal amount, BigDecimal amountReserved, boolean useForPick, boolean useForTransport, long locationId, String locationName, long unitLoadId, String unitLoadLabel, boolean opened) {
		this.stockId = id;
		this.locationId = locationId;
		this.locationName = locationName;
		this.amount = amount;
		this.amountReserved = amountReserved;
		this.amountAvailable = (amountReserved == null ? amount : amount.subtract(amountReserved));
		this.unitLoadId = unitLoadId;
		this.unitLoadLabel = unitLoadLabel;
		this.opened = opened;
		this.useForPick = useForPick;
		this.useForTransport = useForTransport;
	}

	
	public String toString() {
		return "id="+stockId+", amount="+amount+", location="+locationName;
	}
	

		
}
