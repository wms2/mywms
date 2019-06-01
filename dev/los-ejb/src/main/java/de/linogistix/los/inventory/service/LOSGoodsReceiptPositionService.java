/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.List;

import javax.ejb.Local;

import org.mywms.service.BasicService;

import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.wms2.mywms.inventory.StockUnit;

@Local
public interface LOSGoodsReceiptPositionService extends BasicService<LOSGoodsReceiptPosition> {

	public List<LOSGoodsReceiptPosition> getByStockUnit(StockUnit su);
	
	public List<LOSGoodsReceiptPosition> getByStockUnit(String stockUnitStr);

	public LOSGoodsReceiptPosition getByNumber(String number);

	public boolean existsByNumber(String number);

	public List<LOSGoodsReceiptPosition> getByUnitloadLabel(String labelId);
	
	public long queryNumPos( LOSGoodsReceipt gr );

}
