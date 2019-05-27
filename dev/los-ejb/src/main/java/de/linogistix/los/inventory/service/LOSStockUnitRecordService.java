/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Local;

import org.mywms.service.BasicService;

import de.linogistix.los.inventory.model.LOSStockUnitRecord;
import de.linogistix.los.inventory.model.LOSStockUnitRecordType;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.StorageLocation;

/**
 * 
 * Service for recording/accounting inventory related actions involving {@link StockUnit}.
 * 
 * @author trautm
 *
 */
@Local
public interface LOSStockUnitRecordService extends BasicService<LOSStockUnitRecord>{

	/**
	 * Accounting information: An amount of items has been transferred from on StockUnit to the other.
	 * @param amount
	 * @param from
	 * @param to
	 * @param type
	 * @return
	 */
	public LOSStockUnitRecord record(BigDecimal amount, StockUnit from, StockUnit to, LOSStockUnitRecordType type, String text);
	
	/**
	 * An amount of items has been created on StorageLocation
	 * @param amount
	 * @param to
	 * @param text (activityCode)
	 * @return
	 */
	public LOSStockUnitRecord recordCreation(BigDecimal amount, StockUnit to, String text);

	/**
	 * An amount of items has been created on StorageLocation
	 * @param amount
	 * @param to
	 * @param text (activityCode)
	 * @param comment
	 * @return
	 */
	public LOSStockUnitRecord recordCreation(BigDecimal amount, StockUnit to, String text, String comment, String operator);
	

	/**
	 * Amount has been removed from StockUnit
	 * @param amount
	 * @param su
	 * @param text (additionalContext)
	 * @return
	 */
	public LOSStockUnitRecord recordRemoval(BigDecimal amount, StockUnit su, String text);
	
	/**
	 * Amount has been removed from StockUnit
	 * @param amount
	 * @param su
	 * @param text (activityCode)
	 * @param comment (additionalContent)
	 * @return
	 */
	public LOSStockUnitRecord recordRemoval(BigDecimal amount, StockUnit su, String text, String comment, String operator);
	
	/**
	 * Returns all {@link LOSStockUnitRecord} belonging to given {@link StockUnit} and with given {@link LOSStockUnitRecordType} 
 	 * @param su
	 * @param type
	 * @return
	 */
	public List<LOSStockUnitRecord> getByStockUnitAndType(StockUnit su, LOSStockUnitRecordType type);

	/**
	 * The StockUnit has been transferred from one UnitLoad to another
	 * 
	 * @param su
	 * @param old from this UnitLoad
	 * @param dest to this destination
	 * @param activityCode
	 */
	public LOSStockUnitRecord recordTransfer(StockUnit su, UnitLoad old, UnitLoad dest, String activityCode);
	
	/**
	 * The StockUnit has been transferred from one UnitLoad to another
	 * 
	 * @param su
	 * @param old from this UnitLoad
	 * @param dest to this destination
	 * @param activityCode
	 * @param comment
	 */
	public LOSStockUnitRecord recordTransfer(StockUnit su, UnitLoad old, UnitLoad dest, String activityCode, String comment, String operator);
	
	/**
	 * Something has been counted.
	 * You may leave su, ul or loc empty. Only one has to be not null.
	 * 
	 * @param su
	 * @param ul
	 * @param loc
	 * @param activityCode
	 * @param comment
	 * @return
	 */
	public LOSStockUnitRecord recordCounting(StockUnit su, UnitLoad ul, StorageLocation loc, String activityCode, String comment, String operator);
	
	
	/**
	 * Amount has been changed on StockUnit
	 * @param amount
	 * @param su
	 * @param text (additionalContext)
	 * @return
	 */
	public LOSStockUnitRecord recordChange(BigDecimal amount, StockUnit to, String text);
	
	/**
	 * Amount has been changed on StockUnit
	 * @param amount
	 * @param su
	 * @param text (additionalContext)
	 * @return
	 */
	public LOSStockUnitRecord recordChange(BigDecimal amount, StockUnit to, String text, String comment, String operator);

}
