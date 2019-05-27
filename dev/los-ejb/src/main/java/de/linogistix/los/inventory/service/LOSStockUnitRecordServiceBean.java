/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.service.BasicServiceBean;

import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.inventory.model.LOSStockUnitRecord;
import de.linogistix.los.inventory.model.LOSStockUnitRecordType;
import de.linogistix.los.util.businessservice.ContextService;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.StorageLocation;

@Stateless
public class LOSStockUnitRecordServiceBean 
				extends BasicServiceBean<LOSStockUnitRecord> 
				implements LOSStockUnitRecordService 
{
	
	private static final Logger log = Logger.getLogger(LOSStockUnitRecordServiceBean.class);

	@EJB
	private ContextService contextService;
	@EJB
	private EntityGenerator entityGenerator;
	
	public LOSStockUnitRecord record(BigDecimal amount, StockUnit from, StockUnit to, LOSStockUnitRecordType type, String text){
		
		LOSStockUnitRecord rec = entityGenerator.generateEntity(LOSStockUnitRecord.class);
		rec.setAmount(amount);
		rec.setClient(from.getClient());
		
		rec.setFromStockUnitIdentity(from.toUniqueString());
		rec.setFromUnitLoad(from.getUnitLoad().getLabelId());
		rec.setFromStorageLocation(from.getUnitLoad().getStorageLocation().getName());
		
		rec.setToStockUnitIdentity(to.toUniqueString());
		rec.setToUnitLoad(to.getUnitLoad().getLabelId());
		rec.setToStorageLocation(to.getUnitLoad().getStorageLocation().getName());
		
		rec.setItemData(from.getItemData().getNumber());
		rec.setScale(from.getItemData().getScale());
		if (from.getLot() != null) rec.setLot(from.getLot().getName());
		rec.setOperator(contextService.getCallerUserName());
		rec.setType(type);
		rec.setActivityCode(text);
		
		rec.setSerialNumber(from.getSerialNumber());
		
		rec.setUnitLoadType( from.getUnitLoad().getUnitLoadType().getName() );
		
		manager.persist(rec);
//		manager.flush();
		
		return rec;
		
	}

	public LOSStockUnitRecord recordCreation(BigDecimal amount, StockUnit to, String activityCode) {
		return recordCreation(amount, to, activityCode, null, null);
	}
	public LOSStockUnitRecord recordCreation(BigDecimal amount, StockUnit to, String activityCode, String comment, String operator) {
		if( BigDecimal.ZERO.compareTo(amount) == 0 ) {
			log.debug("Do not record zero amount creation");
			return null;
		}
		LOSStockUnitRecord rec = entityGenerator.generateEntity(LOSStockUnitRecord.class);
		rec.setOperator(operator == null ? contextService.getCallerUserName() : operator);
		rec.setAmount(amount);
		rec.setAmountStock(to.getAmount());
		rec.setClient(to.getClient());
		
		rec.setFromStockUnitIdentity(to.toUniqueString());
		rec.setFromUnitLoad(to.getUnitLoad().getLabelId());
		rec.setFromStorageLocation(to.getUnitLoad().getStorageLocation().getName());

		rec.setToStockUnitIdentity(to.toUniqueString());
		rec.setToUnitLoad(to.getUnitLoad().getLabelId());
		rec.setToStorageLocation(to.getUnitLoad().getStorageLocation().getName());
		
		rec.setItemData(to.getItemData().getNumber());
		rec.setScale(to.getItemData().getScale());
		if (to.getLot() != null) rec.setLot(to.getLot().getName());
		rec.setType(LOSStockUnitRecordType.STOCK_CREATED);
		rec.setActivityCode(activityCode);
		rec.setAdditionalContent(comment);
		
		rec.setSerialNumber(to.getSerialNumber());
		
		rec.setUnitLoadType( to.getUnitLoad().getType().getName() );

		manager.persist(rec);
//		manager.flush();
		
		return rec;
	}

	public LOSStockUnitRecord recordChange(BigDecimal amount, StockUnit to, String activityCode) {
		return recordChange(amount, to, activityCode, null, null);
	}
	public LOSStockUnitRecord recordChange(BigDecimal amount, StockUnit to, String activityCode, String comment, String operator) {
		LOSStockUnitRecord rec = entityGenerator.generateEntity(LOSStockUnitRecord.class);
		rec.setOperator(operator == null ? contextService.getCallerUserName() : operator);
		rec.setAmount(amount);
		rec.setAmountStock(to.getAmount());
		rec.setClient(to.getClient());
		
		rec.setFromStockUnitIdentity(to.toUniqueString());
		rec.setFromUnitLoad(to.getUnitLoad().getLabelId());
		rec.setFromStorageLocation(to.getUnitLoad().getStorageLocation().getName());

		rec.setToStockUnitIdentity(to.toUniqueString());
		rec.setToUnitLoad(to.getUnitLoad().getLabelId());
		rec.setToStorageLocation(to.getUnitLoad().getStorageLocation().getName());
		
		rec.setItemData(to.getItemData().getNumber());
		rec.setScale(to.getItemData().getScale());
		if (to.getLot() != null) rec.setLot(to.getLot().getName());
		rec.setType(LOSStockUnitRecordType.STOCK_ALTERED);
		rec.setActivityCode(activityCode);
		rec.setAdditionalContent(comment);
		
		rec.setSerialNumber(to.getSerialNumber());
		
		rec.setUnitLoadType( to.getUnitLoad().getType().getName() );

		manager.persist(rec);
//		manager.flush();
		
		return rec;
	}


	public LOSStockUnitRecord recordRemoval(BigDecimal amount, StockUnit su, String activityCode) {
		return this.recordRemoval(amount, su, activityCode, null, null);
	}
	
	public LOSStockUnitRecord recordRemoval(BigDecimal amount, StockUnit su, String activityCode, String comment, String operator) {
		LOSStockUnitRecord rec = entityGenerator.generateEntity(LOSStockUnitRecord.class);
		rec.setOperator(operator == null ? contextService.getCallerUserName() : operator);
		rec.setAmount(amount);
		rec.setAmountStock(su.getAmount());
		rec.setClient(su.getClient());
		
		rec.setFromStockUnitIdentity(su.toUniqueString());
		rec.setFromUnitLoad(su.getUnitLoad().getLabelId());
		rec.setFromStorageLocation(su.getUnitLoad().getStorageLocation().getName());
		
		rec.setToStockUnitIdentity(su.toUniqueString());
		rec.setToUnitLoad(su.getUnitLoad().toUniqueString());
		rec.setToStorageLocation(su.getUnitLoad().getStorageLocation().getName());
		
		rec.setItemData(su.getItemData().getNumber());
		rec.setScale(su.getItemData().getScale());
		if (su.getLot() != null) rec.setLot(su.getLot().getName());
		rec.setType(LOSStockUnitRecordType.STOCK_REMOVED);
		rec.setActivityCode(activityCode);
		rec.setAdditionalContent(comment);
		
		rec.setSerialNumber(su.getSerialNumber());
		
		rec.setUnitLoadType( su.getUnitLoad().getType().getName() );

		manager.persist(rec);
//		manager.flush();
		
		return rec;
	}

	@SuppressWarnings("unchecked")
	public List<LOSStockUnitRecord> getByStockUnitAndType(StockUnit su,
			LOSStockUnitRecordType type) {
		
		StringBuffer b = new StringBuffer();
		b.append(" SELECT o ");
		b.append(" FROM ");
		b.append(LOSStockUnitRecord.class.getName());
		b.append(" o WHERE o.type=:type ");
		b.append(" AND ( o.toStockUnitIdentity=:su OR o.fromStockUnitIdentity=:su )");
		b.append(" ORDER BY o.id ");
		
		Query q = manager.createQuery(new String(b));
		q = q.setParameter("su", su.toUniqueString());
		q = q.setParameter("type", type);
		
		List<LOSStockUnitRecord> ret = q.getResultList();
		
		return ret;
		
	}

	public LOSStockUnitRecord recordTransfer(StockUnit su, UnitLoad old, UnitLoad dest, String activityCode) {
		return recordTransfer(su, old, dest, activityCode, null, null);
	}
	
	public LOSStockUnitRecord recordTransfer(StockUnit su, UnitLoad old, UnitLoad dest, String activityCode, String comment, String operator) {
		if( BigDecimal.ZERO.compareTo(su.getAmount()) == 0 ) {
			log.debug("Do not record zero amount transfer");
			return null;
		}
		LOSStockUnitRecord rec = entityGenerator.generateEntity(LOSStockUnitRecord.class);
		rec.setOperator(operator == null ? contextService.getCallerUserName() : operator);
		rec.setAmount(BigDecimal.ZERO);
		rec.setAmountStock(su.getAmount());
		rec.setClient(su.getClient());
		
		rec.setFromStockUnitIdentity(su.toUniqueString());
		rec.setFromUnitLoad(old.getLabelId());
		rec.setFromStorageLocation((old).getStorageLocation().getName());
		
		rec.setToStockUnitIdentity(su.toUniqueString());
		rec.setToUnitLoad(dest.getLabelId());
		rec.setToStorageLocation(dest.getStorageLocation().getName());
		
		rec.setItemData(su.getItemData().getNumber());
		rec.setScale(su.getItemData().getScale());
		if (su.getLot() != null) rec.setLot(su.getLot().getName());
		rec.setType(LOSStockUnitRecordType.STOCK_TRANSFERRED);
		rec.setActivityCode(activityCode);
		rec.setAdditionalContent(comment);
		
		rec.setSerialNumber(su.getSerialNumber());
		
		rec.setUnitLoadType( su.getUnitLoad().getType().getName() );

		manager.persist(rec);
//		manager.flush();
		
		return rec;
		
	}
	
	public LOSStockUnitRecord recordCounting(StockUnit su, UnitLoad ul, StorageLocation loc, String activityCode, String comment, String operator) {
		LOSStockUnitRecord rec = entityGenerator.generateEntity(LOSStockUnitRecord.class);
		
		rec.setClient(su != null ? su.getClient() : ul != null ? ul.getClient() : loc != null ? loc.getClient() : null);
		
		rec.setOperator(operator == null ? contextService.getCallerUserName() : operator);
		rec.setType(LOSStockUnitRecordType.STOCK_COUNTED);
		rec.setActivityCode(activityCode);
		rec.setAdditionalContent(comment);
		rec.setScale( 0 );
		rec.setFromStorageLocation("-");
		rec.setToStorageLocation("-");
		
		if( su != null ) {
			rec.setFromStockUnitIdentity(su.toUniqueString());
			rec.setFromUnitLoad(su.getUnitLoad().getLabelId());
			rec.setFromStorageLocation(su.getUnitLoad().getStorageLocation().getName());
			rec.setToStockUnitIdentity(su.toUniqueString());
			rec.setToUnitLoad(su.getUnitLoad().getLabelId());
			rec.setToStorageLocation(su.getUnitLoad().getStorageLocation().getName());
			rec.setAmountStock( su.getAmount() );
			
			rec.setItemData(su.getItemData().getNumber());
			rec.setScale(su.getItemData().getScale());
			if (su.getLot() != null) rec.setLot(su.getLot().getName());
			rec.setSerialNumber(su.getSerialNumber());
			rec.setUnitLoadType( su.getUnitLoad().getType().getName() );
		}
		if( ul != null ) {
			rec.setFromUnitLoad(ul.toUniqueString());
			rec.setFromStorageLocation(ul.getStorageLocation().getName());
			rec.setToUnitLoad(ul.toUniqueString());
			rec.setToStorageLocation(ul.getStorageLocation().getName());
			rec.setUnitLoadType( ul.getType().getName() );
		}
		if( loc != null ) {
			rec.setFromStorageLocation(loc.getName());
			rec.setToStorageLocation(loc.getName());
		}
		
		manager.persist(rec);
		
		return rec;
	}

}
