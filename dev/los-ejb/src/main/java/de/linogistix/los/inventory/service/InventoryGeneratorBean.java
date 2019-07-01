/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.model.Client;

import de.linogistix.los.inventory.facade.InventoryProcessFacade;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSPickingOrder;
import de.linogistix.los.inventory.model.LOSStockUnitRecordType;
import de.linogistix.los.inventory.model.LOSStorageRequest;
import de.linogistix.los.inventory.model.LOSUnitLoadAdvice;
import de.linogistix.los.inventory.pick.model.PickReceipt;
import de.linogistix.los.util.StringTools;
import de.linogistix.los.util.businessservice.LOSSequenceGeneratorService;
import de.wms2.mywms.inventory.UnitLoadType;

/**
 * 
 * @author Jordan
 */
@Stateless
public class InventoryGeneratorBean implements InventoryGeneratorService {


    @EJB
    private LOSSequenceGeneratorService seqService;
    // -----------------------------------------------------------------------
    
    public String generateGoodsReceiptNumber(Client c){
         String ret;
         String NUMBER_PREFIX = "WE";
         long n = seqService.getNextSequenceNumber(LOSGoodsReceipt.class);
         ret = String.format(NUMBER_PREFIX  + " %1$06d", n);
         return ret;
     }
     
     public String generateAdviceNumber(Client c){
         String ret;
         String NUMBER_PREFIX = "AVIS";
         long n = seqService.getNextSequenceNumber(LOSAdvice.class);
         ret = String.format(NUMBER_PREFIX  + " %1$06d", n);
         return ret;
     }

     public String generateUnitLoadAdviceNumber(Client c){
         String ret;
         String NUMBER_PREFIX = "UAV";
         long n = seqService.getNextSequenceNumber(LOSUnitLoadAdvice.class);
         ret = String.format(NUMBER_PREFIX  + " %1$08d", n);
         return ret;
     }
     
    public String generateUnitLoadLabelId(Client c, UnitLoadType ulType) {
        
        String ret;
        
        long n = seqService.getNextSequenceNumber(UnitLoadType.class);
        ret = String.format("%1$06d", n);
        return ret;
        
    }

    public String generatePickOrderNumber(Client c, String prefix) {
        String ret;
        if( StringTools.isEmpty(prefix) ) {
        	prefix = "PICK";
        }
        long n = seqService.getNextSequenceNumber(LOSPickingOrder.class);
        ret = String.format(prefix  + " %1$06d", n);
        return ret;
    }

    public String generateOrderNumber(Client c) {
    	return generateOrderNumber(c, null);
    }
    public String generateOrderNumber(Client c, String prefix) {
        String ret;
        if( StringTools.isEmpty(prefix) ) {
        	prefix = "ORDER";
        }
        long n = seqService.getNextSequenceNumber(LOSCustomerOrder.class);
        ret = String.format(prefix  + " %1$06d", n);
        return ret;
    }

    public String generateRecordNumber(Client c, String prefix, LOSStockUnitRecordType type) {
        String ret;
        String NUMBER_PREFIX = "R";
        long n = seqService.getNextSequenceNumber("de.linogistix.los.inventory.model.LOSStockUnitRecord");
        ret = String.format(NUMBER_PREFIX  + "-%2$s-%3$s %1$06d", n, prefix, type.toString());
        return ret;
    }

    public String generateStorageRequestNumber(Client c) {
        String ret;
        String NUMBER_PREFIX = "STORE";
        long n = seqService.getNextSequenceNumber(LOSStorageRequest.class);
        ret = String.format(NUMBER_PREFIX  + " %1$06d", n);
        return ret;
    }

	public String generateGoodsOutNumber(Client c) {
		String ret;
		String NUMBER_PREFIX = "GOUT";
		long n = seqService.getNextSequenceNumber(LOSGoodsOutRequest.class);
		ret = String.format(NUMBER_PREFIX + " %1$06d", n);
		return ret;
	}

	public String generateReplenishNumber(Client c) {
		String ret;
		String NUMBER_PREFIX = "REPL";
		long n = seqService.getNextSequenceNumber(PickReceipt.class);
		ret = String.format(NUMBER_PREFIX + " %1$06d", n);
		return ret;
	}

	public String generateManageInventoryNumber() {
		String ret;
		String NUMBER_PREFIX = "IMAN";
		long n = seqService.getNextSequenceNumber("de.linogistix.los.inventory.ws.ManageInventory");
		ret = String.format(NUMBER_PREFIX + " %1$06d", n);
		return ret;
	}
	
	public String generateInventoryProcessNumber() {
		String ret;
		String NUMBER_PREFIX = "IINV";
		long n = seqService.getNextSequenceNumber(InventoryProcessFacade.class);
		ret = String.format(NUMBER_PREFIX + " %1$06d", n);
		return ret;
	}
	
	public String generateStocktakingNumber() {
		String ret;
		String NUMBER_PREFIX = "IV";
		long n = seqService.getNextSequenceNumber("de.linogistix.los.stocktaking.model.LOSStockTaking");
		ret = String.format(NUMBER_PREFIX + " %1$08d", n);
		return ret;
	}

}
