/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.mywms.model.Client;

import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.sequence.SequenceBusiness;

/**
 * 
 * @author Jordan
 */
@Stateless
public class InventoryGeneratorBean implements InventoryGeneratorService {


    @Inject
    private SequenceBusiness seqService;
    // -----------------------------------------------------------------------
    
    public String generateGoodsReceiptNumber(Client c){
         return seqService.readNextValue("GoodsReceipt");
     }
     
     public String generateAdviceNumber(Client c){
         return seqService.readNextValue("Advice");
     }

     public String generateUnitLoadAdviceNumber(Client c){
         return seqService.readNextValue("UnitLoadAdvice");
     }
     
    public String generateUnitLoadLabelId(Client c, UnitLoadType ulType) {
        return seqService.readNextValue("UnitLoad");
    }

    public String generatePickOrderNumber(Client c, String prefix) {
        return seqService.readNextValue("PickingOrder");
    }

    public String generateOrderNumber(Client c) {
    	return generateOrderNumber(c, null);
    }
    public String generateOrderNumber(Client c, String prefix) {
        return seqService.readNextValue("DeliveryOrder");
    }

    public String generateStorageRequestNumber(Client c) {
        return seqService.readNextValue("Storage");
    }

	public String generateGoodsOutNumber(Client c) {
        return seqService.readNextValue("ShippingOrder");
	}

	public String generateReplenishNumber(Client c) {
        return seqService.readNextValue("ReplenishOrder");
	}

	public String generateManageInventoryNumber() {
        return seqService.readNextValue("Inventory");
	}
	
	public String generateStocktakingNumber() {
        return seqService.readNextValue("StocktakingOrder");
	}

}
