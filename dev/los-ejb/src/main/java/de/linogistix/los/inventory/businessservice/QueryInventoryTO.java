/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import de.linogistix.los.entityservice.BusinessObjectLockState;

/**
 * TO for communication Inventory information.
 * 
 * @author trautm
 *
 */
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)             
@XmlType(
		name = "InventoryTO",
		namespace="http://com.linogistix/inventory" )
public class QueryInventoryTO implements Serializable{
	
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public QueryInventoryTO(){
		this.clientRef = "<<no client reference>>";
		this.articleRef = "<<no article reference>>";
		this.lotRef = "";
	}
	
	/**
	 * @param clientRef
	 * @param articleRef
	 */
	public QueryInventoryTO(String clientRef, String articleRef, int scale){
		this.clientRef = clientRef;
		this.articleRef = articleRef;
		this.lotRef = "";
		this.scale = scale;
	}
	
	/**
	 * @param clientRef
	 * @param articleRef
	 */
	public QueryInventoryTO(String clientRef, String articleRef, String lotRef, int scale){
		this.clientRef = clientRef;
		this.articleRef = articleRef;
		this.lotRef = lotRef;
		this.scale = scale;
	} 
	
	/**
	 * @param clientRef
	 * @param articleRef
	 * @param reserved
	 * @param available
	 * @param locked
	 * @param advised
	 * @param inStock
	 * @param lastIncoming
	 */
	public QueryInventoryTO(
			String clientRef, 
			String articleRef,
			String lotRef,
			BigDecimal reserved,
			BigDecimal amount,
			boolean locked){
		
		this.clientRef = clientRef;
		this.articleRef = articleRef;
		this.lotRef = lotRef;
		this.reserved = reserved;
		this.inStock = amount;
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
	 * A unique reference to the Batch/Lot
	 */
	public String lotRef;
	
	/**
	 * Number of pieces that are reserved
	 */
	public BigDecimal reserved = new BigDecimal(0);
	
	/**
	 * Number of pieces that are available
	 */
	public BigDecimal available = new BigDecimal(0);;
	
	/**
	 * Number of pieces that are locked
	 */
	public BigDecimal locked = new BigDecimal(0);;
	
	/**
	 * Number of pieces that are advised
	 */
	public BigDecimal advised = new BigDecimal(0);;
	
	/**
	 * Number of pieces in stock.
	 * 
	 * <code>inStock = available + locked + reserved</code>
	 */
	public BigDecimal inStock = new BigDecimal(0);;

	/**
	 * The scale of all amounts
	 */
	public int scale;
	
	/**
	 * The unit of all amounts
	 */
	public String unit;
	
	public List<LockAmountEntry> lockList = new ArrayList<LockAmountEntry>();
	
	public void addLock(int lock, BigDecimal amount){
		
		if (lock == BusinessObjectLockState.NOT_LOCKED.getLock()) return;
		
		LockAmountEntry e = new LockAmountEntry(lock, amount.setScale(scale));
		int index;
		
		if ((index = lockList.indexOf(e)) > -1){
			e = lockList.get(index);
			e.amount = e.amount.add(amount).setScale(scale);
		} else{
			lockList.add(e);
		}
		
	}
	
	@Override
	public String toString() {
		StringBuffer ret = new StringBuffer();
		ret.append("InventoryTO: ");
		
		ret.append("[clientRef=");
		ret.append(clientRef);
		ret.append("] ");
		ret.append("[articleRef=");
		ret.append(articleRef);
		ret.append("] ");
		
		ret.append("[inStock=");
		ret.append(inStock);
		ret.append("] ");
		ret.append("[available=");
		ret.append(available);
		ret.append("] ");
		ret.append("[reserved=");
		ret.append(reserved);
		ret.append("] ");
		ret.append("[advised=");
		ret.append(advised);
		ret.append("] ");
		ret.append("[locked=");
		ret.append(locked);
		ret.append("] ");
		
		return new String(ret);
		
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj  == null){
			return false;
		}
		if (obj instanceof QueryInventoryTO){
			QueryInventoryTO to = (QueryInventoryTO)obj;
			return this.clientRef.equals(to.clientRef) 
                    && this.articleRef.equals(to.articleRef)
                    && this.lotRef.equals(to.lotRef);
		} else{
			return false;
		}
		
	}
	
	public static final class LockAmountEntry implements Serializable{
		
		private static final long serialVersionUID = 1L;
		
		public int lock;
		
		public BigDecimal amount;
		
		public LockAmountEntry(){
			lock = 0;
			amount = BigDecimal.ZERO;
		}
		
		public LockAmountEntry(int lock, BigDecimal amount){
			this.lock = lock;
			this.amount = amount;
		}
		
		@Override
		public String toString() {
			return "" + lock + "=" + amount;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (! (obj instanceof LockAmountEntry)) return false;
			
			return this.lock == ((LockAmountEntry)obj).lock;
			
		}
		
		@Override
		public int hashCode() {
			return lock;
		}
		
		
		
	}

}
