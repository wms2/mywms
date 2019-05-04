/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.goodsreceipt.gui.component;

import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptType;
import de.linogistix.los.query.BODTO;
import java.math.BigDecimal;
import java.util.Date;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.model.UnitLoadType;

/**
 *
 * @author trautm
 */
public class PositionWizardModel {

    public BODTO<Client> client;
    
    public LOSGoodsReceipt gr;
    
    public LOSAdvice selectedAdvice;
//
//    public LOSAdviceTO selectedAdviceTO;
    
    public boolean isSingleUnitLoad = false;
    
    BODTO<Lot> lot;
    String lotStr;
    
    BODTO<ItemData> item;
    String unitLoadLabelId;
    BODTO<UnitLoadType> ulType;
    BigDecimal amount;
//    BODTO<LOSAdvice> advice;
    LOSGoodsReceiptType type;
    int lock = 0;
    String info;
    
    int sameCount = 1;
    boolean expire;
    Date validTo;
    Date validFrom;
    
    public boolean createLot(){
        return (lotStr != null && lotStr.length() > 0) && lot == null;
    }
}
