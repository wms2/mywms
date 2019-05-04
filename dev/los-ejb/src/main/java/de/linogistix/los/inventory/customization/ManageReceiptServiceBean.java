/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.customization;

import java.math.BigDecimal;

import javax.ejb.EJB;

import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.UnitLoadType;

import de.linogistix.los.common.businessservice.HostMsgService;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.HostMsgGR;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;

public class ManageReceiptServiceBean implements ManageReceiptService {

	@EJB
	HostMsgService hostService;
	
	public void finishGoodsReceiptEnd(LOSGoodsReceipt gr) throws InventoryException {
		try {
			hostService.sendMsg( new HostMsgGR(gr) );
		} catch (FacadeException e) {
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, e.getLocalizedMessage());
		}
	}

	public void finishGoodsReceiptStart(LOSGoodsReceipt gr)
			throws InventoryException {
	}

	public void onGoodsReceiptPositionCollected(LOSGoodsReceiptPosition grPos) throws FacadeException {
	}
	
	public void onGoodsReceiptPositionDeleted(Client client, ItemData itemData, BigDecimal amount, LOSAdvice advice, String unitLoadLabel, UnitLoadType unitLoadType) throws FacadeException {
	}
}
