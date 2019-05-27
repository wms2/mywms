/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import de.linogistix.los.query.BODTO;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.inventory.StockUnit;

public class StockUnitTO  extends BODTO<StockUnit>{

	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(StockUnitTO.class);
	
	public String lot = "";
	public String itemData;
	public String itemDataName;
	public String storageLocation;
	public BigDecimal amount;
	public BigDecimal reservedAmount;
	public int lock; 
	public String unitLoad;
	
	//dgrys portierung wildfly 8.2
	public StockUnitTO(Long id, int version, int lock, Lot l, String itemData, String itemDataName, int ulVersion, String unitLoad, String storageLocation, BigDecimal amount, BigDecimal reservedAmount, int scale){
		this(id, version, ""+id, lock, l, itemData, itemDataName, ulVersion, unitLoad, storageLocation, amount, reservedAmount, scale);
	}
	
	/**
	 * 
	 * @param id
	 * @param version
	 * @param name
	 * @param lock
	 * @param lot
	 * @param itemData
	 * @param unitLoad
	 * @param storageLocation
	 * @param amount
	 * @param reservedAmount
	 * @param scale
	 */
	public StockUnitTO(Long id, int version, String name, int lock, Lot l, String itemData, String itemDataName, int ulVersion, String unitLoad, String storageLocation, BigDecimal amount, BigDecimal reservedAmount, int scale){
		super(id, version+ulVersion, name);
		
		if(l != null){
			this.lot= l.getName();
		}
		this.lock = lock;
		this.itemData= itemData;
		this.itemDataName= itemDataName;
		this.storageLocation= storageLocation;

		try{
			this.amount= amount.setScale(scale);
		}catch(ArithmeticException ae){
			log.warn("------- LE "+id+" : expected scale = "+scale+" but was "+amount);
			this.amount = amount;
		}
		
		try{
			this.reservedAmount= reservedAmount.setScale(scale);
		}catch(ArithmeticException ae){
			log.warn("------- LE "+id+" : expected scale = "+scale+" but was "+reservedAmount);
			this.reservedAmount = reservedAmount;
		}
		
		this.unitLoad = unitLoad;
		setClassName(StockUnit.class.getName());
	}
	
	public StockUnitTO(Long id, int version, String name, int lock, String lot, String itemData, String itemDataName, int ulVersion, String unitLoad, BigDecimal amount, BigDecimal reservedAmount, int scale){
		super(id, version+ulVersion, name);
		this.lot= lot;
		this.lock = lock;
		this.itemData= itemData;
		this.itemDataName= itemDataName;
		this.storageLocation= "";
		
		try{
			this.amount= amount.setScale(scale);
		}catch(ArithmeticException ae){
			log.warn("------- LE "+id+" : expected scale = "+scale+" but was "+amount);
			this.amount = amount;
		}
		
		try{
			this.reservedAmount= reservedAmount.setScale(scale);
		}catch(ArithmeticException ae){
			log.warn("------- LE "+id+" : expected scale = "+scale+" but was "+reservedAmount);
			this.reservedAmount = reservedAmount;
		}
		
		this.unitLoad = unitLoad;
		setClassName(StockUnit.class.getName());
	}
	
	public StockUnitTO(StockUnit su){
		//dgrys portierung wildfly 8.2
		//super(su.getId(), su.getVersion()+su.getUnitLoad().getVersion(), su.getLabelId());
		super(su.getId(), su.getVersion()+su.getUnitLoad().getVersion(), su.getId());
		this.lot= su.getLot() != null ? su.getLot().getName() : "";
		this.lock = su.getLock();
		this.itemData= su.getItemData().getNumber();
		this.itemDataName= su.getItemData().getName();
		int scale = su.getItemData().getScale();
		this.unitLoad = su.getUnitLoad().getLabelId();
		this.storageLocation= su.getUnitLoad().getStorageLocation().getName();
		BigDecimal amount = su.getAmount();
		BigDecimal reservedAmount  =su.getReservedAmount();
		
		try{
			this.amount= amount.setScale(scale);
		}catch(ArithmeticException ae){
			log.warn("------- LE "+su.getId()+" : expected scale = "+scale+" but was "+amount);
			this.amount = amount;
		}
		
		try{
			this.reservedAmount= reservedAmount.setScale(scale);
		}catch(ArithmeticException ae){
			log.warn("------- LE "+su.getId()+" : expected scale = "+scale+" but was "+reservedAmount);
			this.reservedAmount = reservedAmount;
		}

		setClassName(StockUnit.class.getName());
		
	}
	/**
	 * Optimizes queries. Without unitload und storagelocations a lot less subqueries/joins are needed
	 * @param id
	 * @param version
	 * @param name
	 * @param lock
	 * @param lot
	 * @param itemData
	 * @param amount
	 * @param reservedAmount
	 */
	public StockUnitTO(Long id, int version, String name, int lock, String lot, String itemData, String itemDataName, BigDecimal amount, BigDecimal reservedAmount, int scale){
		super(id, version, name);
		this.lot= lot;
		this.lock = lock;
		this.itemData= itemData;
		this.itemData= itemDataName;
		this.storageLocation= "";
		
		try{
			this.amount= amount.setScale(scale);
		}catch(ArithmeticException ae){
			log.warn("------- LE "+id+" : expected scale = "+scale+" but was "+amount);
			this.amount = amount;
		}
		
		try{
			this.reservedAmount= reservedAmount.setScale(scale);
		}catch(ArithmeticException ae){
			log.warn("------- LE "+id+" : expected scale = "+scale+" but was "+reservedAmount);
			this.reservedAmount = reservedAmount;
		}
		
		this.unitLoad = "";
		setClassName(StockUnit.class.getName());
	}

	public String getLot() {
		return lot;
	}

	public void setLot(String lot) {
		this.lot = lot;
	}

	public String getItemData() {
		return itemData;
	}

	public void setItemData(String itemData) {
		this.itemData = itemData;
	}

	public String getStorageLocation() {
		return storageLocation;
	}

	public void setStorageLocation(String storageLocation) {
		this.storageLocation = storageLocation;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getReservedAmount() {
		return reservedAmount;
	}

	public void setReservedAmount(BigDecimal reservedAmount) {
		this.reservedAmount = reservedAmount;
	}

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	public String getUnitLoad() {
		return unitLoad;
	}

	public void setUnitLoad(String unitLoad) {
		this.unitLoad = unitLoad;
	}

	public String getItemDataName() {
		return itemDataName;
	}

	public void setItemDataName(String itemDataName) {
		this.itemDataName = itemDataName;
	}
	
	
}
