/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.model.Client;

import de.linogistix.los.entityservice.BusinessObjectLockState;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.location.constants.LOSUnitLoadLockState;
import de.wms2.mywms.advice.Advice;
import de.wms2.mywms.advice.AdviceLine;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.inventory.StockState;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.strategy.OrderState;

/**
 * Inventory relevant business operations
 * 
 * @author trautm
 */
@Stateless
public class QueryInventoryBusinessBean implements QueryInventoryBusiness {

	Logger log = Logger.getLogger(QueryInventoryBusinessBean.class);

	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;

	public QueryInventoryTO[] getInventory(Client c, boolean consolidateLot) throws InventoryException{
		return getInventory(c, consolidateLot, false);
	}
	public QueryInventoryTO[] getInventory(Client c, boolean consolidateLot, boolean withAmountOnly) throws InventoryException{
		QueryInventoryTO[] ret;
		Map<String, QueryInventoryTO> m = getInvMap(c, null, null, consolidateLot, withAmountOnly);
		
		ret = m.values().toArray(new QueryInventoryTO[0]);
		Arrays.sort(ret, new QueryInventoryTOComparator());
		return ret;
		
	}
	
	//-------------------------------------------------------------------------
	
	public Map<String, QueryInventoryTO> getInvMap(Client c, Lot lot, ItemData idat, boolean consolidateLot, boolean withAmountOnly) throws InventoryException {

		Map<String, QueryInventoryTO> result = new HashMap<String, QueryInventoryTO>();

		if (lot != null && idat != null && ! lot.getItemData().equals(idat)){
			throw new InventoryException(InventoryExceptionKey.ITEMDATA_LOT_MISMATCH, new String[]{idat.getNumber(), lot.getName()});
		}
		
		if( !withAmountOnly ) {
			if (consolidateLot && lot==null){
				result = getItemData(c, idat);
			} else{
				result = getLots(c, lot, idat);
			}
		}
		
		getStockUnitAmount(c, lot, idat, result, consolidateLot);

		getAdvicedAmount(c, lot, idat, result, consolidateLot);
		
		// ---------------------------------------------------------------------
		// Return All
		// ---------------------------------------------------------------------

		return result;
	}
	
	private Map<String, QueryInventoryTO> getItemData(Client c,ItemData idat) {
		
		Map<String, QueryInventoryTO> result = new HashMap<String, QueryInventoryTO>();
		List<QueryInventoryTO> items = getItemDataInv(c, idat);

		for (QueryInventoryTO to : items){
			result.put(getKey(null, to.articleRef, true), to);
		}
		
		return result;		
	}
	
	@SuppressWarnings("unchecked")
	private List<QueryInventoryTO> getItemDataInv(Client c, ItemData idat) {
		StringBuffer b = new StringBuffer();

		// Query StockUnits
		b.append("SELECT NEW ");
		b.append(QueryInventoryTO.class.getName());
		// client
		b.append("(");
		b.append("itemData.client.number, ");
		b.append("itemData.number, ");
		b.append("itemData.scale ");
		b.append(")");

		b.append(" FROM ");
		b.append(ItemData.class.getSimpleName());
		b.append(" itemData ");

		b.append(" WHERE itemData.client = :client ");
		if (idat != null) {
			b.append(" AND itemData = :idat ");
		}
		
		b.append(" AND itemData.lock <> :dellock ");
		
		
		Query query = manager.createQuery(new String(b));
		query = query.setParameter("client", c);
		query = query.setParameter("dellock", BusinessObjectLockState.GOING_TO_DELETE.getLock());
		if (idat != null) {
			query = query.setParameter("idat", idat);
		}
		
		List<QueryInventoryTO> ret = query.getResultList();
		log.debug("returned elements " + ret.size());
		return ret;
	}
	
	private Map<String, QueryInventoryTO> getLots(Client c, Lot lot, ItemData idat) {
		
		Map<String, QueryInventoryTO> result = new HashMap<String, QueryInventoryTO>();
		List<QueryInventoryTO> items = getLotsInv(c, lot, idat);

		for (QueryInventoryTO to : items){
			result.put(getKey(to.lotRef, to.articleRef, false), to);
		}
		
		return result;		
	}
	
	@SuppressWarnings("unchecked")
	private List<QueryInventoryTO> getLotsInv(Client c, Lot lot, ItemData idat) {
		StringBuffer b = new StringBuffer();

		// Query StockUnits
		b.append("SELECT NEW ");
		b.append(QueryInventoryTO.class.getName());
		// client
		b.append("(");
		b.append("lot.client.number, ");
		b.append("lot.itemData.number, ");
		b.append("lot.name, ");
		b.append("lot.itemData.scale ");
		b.append(")");

		b.append(" FROM ");
		b.append(Lot.class.getSimpleName());
		b.append(" lot ");

		b.append(" WHERE lot.client = :client ");
		
		if (lot != null) {
			b.append(" AND lot = :lot ");
		} else if (idat != null){
			b.append(" AND lot.itemData = :idat ");
		}
		
		b.append(" AND lot.itemData.lock <> :dellock ");

		Query query = manager.createQuery(new String(b));
		query = query.setParameter("client", c);
		query = query.setParameter("dellock", BusinessObjectLockState.GOING_TO_DELETE.getLock());
		
		if (lot != null) {
			query = query.setParameter("lot", lot);
		} else if (idat != null){
			query = query.setParameter("idat", idat);
		}
		
		List<QueryInventoryTO> ret = query.getResultList();
		log.debug("returned elements " + ret.size());

		return ret;
	}
	
	/**
	 * Current Amount of StockUnits
	 * 
	 * @return
	 */
	private Map<String, QueryInventoryTO> getStockUnitAmount(Client c, Lot lot,
			ItemData idat, Map<String, QueryInventoryTO> result, boolean consolidateLot) {
//		String logStr = "getStockUnitAmount ";
		String key;
		QueryInventoryTO inv;

		List<StockUnitResult> sus;
		sus = getStockUnitInv(c, lot, idat);

		for (StockUnitResult r : sus) {
			key = getKey(r.lotRef, r.articleRef, consolidateLot);
			if ((inv = (QueryInventoryTO) result.get(key)) == null) {
				inv = new QueryInventoryTO(r.clientRef, r.articleRef, consolidateLot?null:r.lotRef, r.scale);
				result.put(key, inv);
			} 
			inv.available = inv.available!=null?inv.available.add(r.available):r.available;
			inv.inStock = inv.inStock!=null?inv.inStock.add(r.inStock):r.inStock;
			inv.reserved = inv.reserved!=null?inv.reserved.add(r.reserved):r.reserved;
			inv.locked =  inv.locked!=null?inv.locked.add(r.locked):r.locked;
			inv.addLock(r.lock, r.locked);
			inv.unit=r.unit;
			try{
				inv.available = inv.available.setScale(r.scale);
			}catch(ArithmeticException ae){}
			
			try{
				inv.inStock = inv.inStock.setScale(r.scale);
			}catch(ArithmeticException ae){}
			
			try{
				inv.reserved = inv.reserved.setScale(r.scale);
			}catch(ArithmeticException ae){}
			
			try{
				inv.locked = inv.locked.setScale(r.scale);
			}catch(ArithmeticException ae){}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	private List<StockUnitResult> getStockUnitInv(Client c, Lot lot, ItemData idat) {
		String logStr = "getStockUnitInv ";
		StringBuffer b = new StringBuffer();

		// Query StockUnits
		b.append("SELECT NEW ");
		b.append(StockUnitResult.class.getName());
		b.append("(");
		b.append("client.number, ");
		b.append("item.number, ");
		b.append("item.scale, item.itemUnit.name, ");
		b.append("lot.name, ");
		b.append("su.amount, ");
		b.append("su.reservedAmount, ");
		b.append("su.lock, ");
		b.append("ul.lock");
		b.append(")");

		b.append(" FROM ");
		b.append(StockUnit.class.getSimpleName()+" su, ");
		b.append(UnitLoad.class.getSimpleName()+" ul, ");
		b.append(Client.class.getSimpleName()+" client, ");
		b.append(ItemData.class.getSimpleName()+" item ");
		b.append("LEFT OUTER JOIN su.lot as lot ");

		b.append(" WHERE client = :client and su.client=client and item=su.itemData and ul=su.unitLoad");
		if (idat != null) {
			b.append(" AND su.itemData = :idat ");
		}
		if (lot != null) {
			b.append(" AND lot = :lot ");
		} 

		b.append(" and su.state<"+StockState.PICKED);
		b.append(" and su.lock!="+BusinessObjectLockState.GOING_TO_DELETE.getLock());
		b.append(" AND su.amount > 0 ");
		b.append(" and ul.lock!="+LOSUnitLoadLockState.SHIPPED.getLock());
		
		log.debug(logStr+"QUERY="+b.toString());

		Query query = manager.createQuery(new String(b));
		query = query.setParameter("client", c);
		if (idat != null) {
			query = query.setParameter("idat", idat);
		}
		if (lot != null) {
			query = query.setParameter("lot", lot);
		}

		List<StockUnitResult> suList = query.getResultList();
		
		return suList;
	}
	
	/**
	 * Current Amount of adviced goods
	 * 
	 * @param c
	 * @param lot
	 * @param idat
	 * @return
	 */
	private Map<String, QueryInventoryTO> getAdvicedAmount(Client c, Lot lot,
			ItemData idat, Map<String, QueryInventoryTO> result, boolean consolidateLot) {

//		String logStr = "getAdvicedAmount ";
		String key;
		QueryInventoryTO inv;

		List<AdviseLotResult> ads;
		ads = getAdviseLotInv(c, lot, idat);

		for (AdviseLotResult r : ads) {
			key = getKey(r.lotRef, r.articleRef, consolidateLot);
			if ((inv = (QueryInventoryTO) result.get(key)) == null) {
				inv = new QueryInventoryTO(r.clientRef, r.articleRef, consolidateLot?null:r.lotRef, r.scale);
				result.put(key, inv);
				inv.advised = inv.advised!=null?inv.advised.add(r.advised):new BigDecimal(0);
			} else {
				inv.advised = inv.advised!=null?inv.advised.add(r.advised):new BigDecimal(0);
			}
			
			try{
				inv.advised = inv.advised.setScale(r.scale);
			}catch(ArithmeticException ae){}
			inv.unit=r.unit;
		}

		return result;
	}
	
	@SuppressWarnings("unchecked")
	private List<AdviseLotResult> getAdviseLotInv(Client c, Lot lot, ItemData idat) {
		String logStr = "getAdviseLotInv ";

		StringBuffer b = new StringBuffer();
		b.append("SELECT NEW ");
		b.append(AdviseLotResult.class.getName());
		b.append("(");
		b.append("client.number, item.number, item.scale, item.itemUnit.name, ad.amount, ad.confirmedAmount, ad.lotNumber");
		b.append(")");
		b.append(" FROM ");
		b.append(Advice.class.getSimpleName()+" adv, ");
		b.append(AdviceLine.class.getSimpleName()+" ad, ");
		b.append(Client.class.getSimpleName()+" client, ");
		b.append(ItemData.class.getSimpleName()+" item ");
		b.append(" WHERE client = :client and adv.client=client and ad.advice=adv and item=ad.itemData");
		
		if (lot != null) {
			b.append(" AND lotNumber = :lotNumber ");
		} 
		if (idat != null) {
			b.append(" AND item = :idat ");
		}
		b.append(" AND ad.state<:finished ");
		b.append(" AND adv.state<:finished ");

		log.debug(logStr+"QUERY="+b.toString());

		Query query = manager.createQuery(new String(b));
		query = query.setParameter("client", c);

		if (lot != null) {
			query = query.setParameter("lotNumber", lot.getName());
		}
		if (idat != null) {
			query = query.setParameter("idat", idat);
		}

		query = query.setParameter("finished", OrderState.FINISHED);
		
		List<AdviseLotResult> adList = query.getResultList();
		
		return adList;
	}
		
	//-------------------------------------------------------------------------
	
	public QueryInventoryTO getInventory(Client c, Lot lot, boolean withAmountOnly) throws InventoryException {

		Map<String, QueryInventoryTO> m = getInvMap(c, lot, lot.getItemData(), false, withAmountOnly);
		
		for (QueryInventoryTO to : m.values()){
			return to;
		}

		throw new InventoryException(InventoryExceptionKey.NO_INVENTORY_FOR_LOT, lot.getName());
		
	}

	public QueryInventoryTO[] getInventory(Client c, ItemData idat, boolean consolidateLot, boolean withAmountOnly) throws InventoryException {
		QueryInventoryTO[] ret;
		
		if (idat == null){
			throw new NullPointerException();
		}
		
		Map<String, QueryInventoryTO> m = getInvMap(c, null, idat, consolidateLot, withAmountOnly);
		ret = m.values().toArray(new QueryInventoryTO[0]);
		Arrays.sort(ret, new QueryInventoryTOComparator());
		return ret;
	}
	
	private static String getKey(String lot, String article, boolean consolidate){
		String key;
		
		if (consolidate){
			key = article;
		} else{
			if (lot == null || article == null) throw new NullPointerException();
			key = article + "-*-*-"  + lot;
		}
		
		return key;
	}
	
	//-------------------------------------------------------------------------
	
	final static class StockUnitResult {

		private static final Logger log = Logger.getLogger(StockUnitResult.class);
		
		public StockUnitResult(String clientRef, String articleRef, int scale, String unit,
				String lotRef, BigDecimal amount, BigDecimal reserved, int lock, int ulLock) {

			this.clientRef = clientRef;
			this.articleRef = articleRef;
			this.lotRef = lotRef != null ? lotRef : "";
			this.scale = scale;
			this.unit = unit;
			this.lock = lock;

			if (lock != 0 || ulLock!=0) {
				this.locked = (amount != null ? amount : BigDecimal.ZERO);
			}
			
			try {
				this.locked = this.locked.setScale(scale);
			} catch (ArithmeticException ae) {
				log.warn("------- Expected scale = " + scale + " but was " + amount);
			}

			try {
				this.reserved = reserved.setScale(scale);
			} catch (ArithmeticException ae) {
				log.warn("------- Expected scale = " + scale + " but was " + reserved);
				this.reserved = reserved;
			}

			try {
				this.inStock = amount.setScale(scale);
			} catch (ArithmeticException ae) {
				log.warn("------- Expected scale = " + scale + " but was " + amount);
				this.inStock = amount;
			}

			try {
				this.available = amount.subtract(reserved).subtract(locked).setScale(scale);
			} catch (ArithmeticException ae) {
				log.warn("------- Expected scale = " + scale + " but was " + amount.subtract(reserved));
				this.available = amount.subtract(reserved).subtract(locked);
			}
			
			if( available.compareTo(BigDecimal.ZERO)<0 ) {
				available = BigDecimal.ZERO;
			}
		}

		/**
		 * A unique reference to the ItemData/article
		 */
		public String articleRef;
		/**
		 * A unique reference to the Client
		 */
		public String clientRef;
		/**
		 * A unique reference to the Lot/Lot
		 */
		public String lotRef;
		/**
		 * Number of pieces that are reserved
		 */
		public BigDecimal reserved = BigDecimal.ZERO;
		/**
		 * Number of pieces that are available
		 */
		public BigDecimal available = BigDecimal.ZERO;
		/**
		 * Number of pieces that are locked
		 */
		public BigDecimal locked = BigDecimal.ZERO;

		public BigDecimal inStock = BigDecimal.ZERO;
		
		public int scale  = 0;
		public String unit;
		
		public int lock = 0;
		
	}

	final static class AdviseLotResult implements Serializable {
		private static final long serialVersionUID = 1L;

		private static final Logger log = Logger.getLogger(AdviseLotResult.class);


		public AdviseLotResult(String clientNumber, String itemNumber, int itemScale, String itemUnit, BigDecimal notifiedAmount, BigDecimal receiptAmount, String lotName) {
			this.clientRef = clientNumber;
			this.articleRef = itemNumber;
			this.lotRef = lotName == null ? "" : lotName;
			this.advised = BigDecimal.ZERO;
			if (notifiedAmount.compareTo(receiptAmount) > 0) { // Keine Ueberlieferung
				this.advised = notifiedAmount.subtract(receiptAmount);
			}
			try{
				advised = advised.setScale(itemScale);
			}catch(ArithmeticException ae){
				log.warn("------- Expected scale = "+scale+" but was ");
			}
			this.scale = itemScale;
			this.unit=itemUnit;
		}
		

		/**
		 * A unique reference to the ItemData/article
		 */
		public String articleRef;
		
		/**
		 * A unique reference to the Client
		 */
		public String clientRef;
		/**
		 * A unique reference to the Lot/Lot
		 */
		public String lotRef;
		/**
		 * Amount of adviced but not yet received items
		 */
		public BigDecimal advised = new BigDecimal(0);
		
		public int scale = 0;
		public String unit;
	}


	class QueryInventoryTOComparator implements Comparator<QueryInventoryTO> {
		public int compare(QueryInventoryTO o1, QueryInventoryTO o2) {
			if (o1 == null || o2 == null) {
				return 0;
			}
			if (o1 == o2) {
				return 0;
			}
			if (o1.equals(o2)) {
				return 0;
			}
			
			if( o1.clientRef!=null && o2.clientRef!=null && !o1.clientRef.equals(o2.clientRef)) {
				return o1.clientRef.compareTo(o2.clientRef);
			}
			
			if( o1.articleRef!=null && o2.articleRef!=null && !o1.articleRef.equals(o2.articleRef)) {
				return o1.articleRef.compareTo(o2.articleRef);
			}
			if( o1.lotRef!=null && o2.lotRef!=null && !o1.lotRef.equals(o2.lotRef)) {
				return o1.lotRef.compareTo(o2.lotRef);
			}
			
			return 0;
		}
	}
}
