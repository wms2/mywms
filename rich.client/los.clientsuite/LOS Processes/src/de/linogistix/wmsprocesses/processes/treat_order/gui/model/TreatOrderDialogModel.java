/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.treat_order.gui.model;

import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.services.J2EEServiceLocatorException;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.facade.ManageInventoryFacade;
import de.linogistix.los.inventory.model.LOSCustomerOrderPosition;
import de.linogistix.los.inventory.pick.facade.CreatePickRequestPositionTO;
import de.linogistix.los.inventory.query.dto.LOSOrderStockUnitTO;
import de.linogistix.los.query.BODTO;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.mywms.model.StockUnit;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Jordan
 */
public class TreatOrderDialogModel {

    private BigDecimal amountToPick;
    
    private BODTO<LOSCustomerOrderPosition> actuOrderPos;
    
    //Long: id of Orderposition, 
    private HashMap<Long, List<CreatePickRequestPositionTO>> orderPosMap;
    
    private ManageInventoryFacade inventoryFacade;
    
    public TreatOrderDialogModel(){
        orderPosMap = new HashMap<Long, List<CreatePickRequestPositionTO>>();
        
        J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
        try {
            inventoryFacade = loc.getStateless(ManageInventoryFacade.class);
        } catch (J2EEServiceLocatorException ex) {
            ExceptionAnnotator.annotate(ex);
        }
    }
    
    public void setActuOrderPosition(BODTO<LOSCustomerOrderPosition> orderPos, BigDecimal amountToPick){
    
//        if(!orderPosMap.containsKey(orderPos.getId())){
//            orderPosMap.put(orderPos.getId(), new ArrayList<CreatePickRequestPositionTO>());
//        }
        
        actuOrderPos = orderPos;
        this.amountToPick = amountToPick;
        
    }
    
    public List<BODTO<StockUnit>> getChosenStocks(String pickRequest){
               
        if(actuOrderPos == null){
            
            return new ArrayList<BODTO<StockUnit>>();
        }
        
        List<CreatePickRequestPositionTO> chosenStockList = orderPosMap.get(actuOrderPos.getId());
        
        if(chosenStockList == null){
            return new ArrayList<BODTO<StockUnit>>();
        }
        
        List<BODTO<StockUnit>> stockList = new ArrayList<BODTO<StockUnit>>(chosenStockList.size());
        for(CreatePickRequestPositionTO s:chosenStockList){
            if(s.pickRequestNumber.equals(pickRequest)){
                stockList.add(s.stock);
            }
        }
            
        return stockList;
        
    }
    
    public BigDecimal getChosenAmount(){
        return getChosenAmountByOrderId(actuOrderPos.getId());
    }
    
    public BigDecimal getChosenAmountByOrderId(long orderPositionId){
        
        BigDecimal chosen = new BigDecimal(0);
        
        List<CreatePickRequestPositionTO> chosenStockList = getChosenStocksByOrderPosition(orderPositionId);
        
        for(CreatePickRequestPositionTO cs:chosenStockList){
            
            chosen = chosen.add(cs.amountToPick);
        }
        
        return chosen;
    }
    
    public BigDecimal getRemainingAmount(){
        
        BigDecimal chosenAmount = getChosenAmount();
        BigDecimal remaining = amountToPick.subtract(chosenAmount);
        
        return remaining.compareTo(new BigDecimal(0)) > 0 ? remaining : new BigDecimal(0);
    }
    
    public List<CreatePickRequestPositionTO> getChosenStocksByOrderPosition(long orderPositionId){
        
        if(!orderPosMap.containsKey(orderPositionId)){
            return new ArrayList<CreatePickRequestPositionTO>();
        }
        else{
            return orderPosMap.get(orderPositionId);
        }
        
    }
    
    public List<CreatePickRequestPositionTO> getChosenStocks(){
        
        List<CreatePickRequestPositionTO> resList = new ArrayList<CreatePickRequestPositionTO>();
        
        for(List<CreatePickRequestPositionTO> l:orderPosMap.values()){
            
            resList.addAll(l);
        }
        
        return resList;
    }
    
    public BigDecimal addChosenStock(TreatOrderPickRequestTO pickRequest, LOSOrderStockUnitTO stock){
        
        BigDecimal remainingAmount = getRemainingAmount();
        
        if(remainingAmount.compareTo(new BigDecimal(0)) == 0){
            return remainingAmount;
        }
        
        List<CreatePickRequestPositionTO> chosenStockList;
        
        if(!orderPosMap.containsKey(actuOrderPos.getId())){
            chosenStockList = new ArrayList<CreatePickRequestPositionTO>();
            orderPosMap.put(actuOrderPos.getId(), chosenStockList);
        }
        else{
            chosenStockList = orderPosMap.get(actuOrderPos.getId());
        }
        
        CreatePickRequestPositionTO s = new CreatePickRequestPositionTO();
        s.pickRequestNumber = pickRequest.pickRequestNumber;
        s.targetPlace = pickRequest.targetPlace;
        s.stock = stock;
        s.orderPosition = actuOrderPos;
        
        if(remainingAmount.compareTo(stock.availableAmount) >= 0 ){
            s.amountToPick = stock.availableAmount;
        }
        else{
            
            NotifyDescriptor d = new NotifyDescriptor.Confirmation(
                                        NbBundle.getMessage(WMSProcessesBundleResolver.class, "ConfirmDawn.message"),
                                        NbBundle.getMessage(WMSProcessesBundleResolver.class,"ConfirmDawn.header"),
                                        NotifyDescriptor.YES_NO_OPTION);

            if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.YES_OPTION) {
                s.amountToPick = remainingAmount;
            }
            else{
                s.amountToPick = stock.availableAmount;
            }
        }
        
        try{
            inventoryFacade.reserveStock(stock, s.amountToPick);
        }catch(InventoryException invex){
            ExceptionAnnotator.annotate(invex);
            return new BigDecimal(0);
        }
        
        chosenStockList.add(s);
        
        return s.amountToPick;
    }
    
    public void removeChosenStock(BODTO<StockUnit> stock){
        
        List<CreatePickRequestPositionTO> chosenStockList = orderPosMap.get(actuOrderPos.getId());
        
        CreatePickRequestPositionTO stockToRemove = null;
        for(CreatePickRequestPositionTO cs: chosenStockList){
            
            if(cs.stock.getId() == stock.getId()){
                stockToRemove = cs;
            }
            
        }
        
        if(stockToRemove != null){
            chosenStockList.remove(stockToRemove);
            
            try{
                inventoryFacade.releaseReservation(stockToRemove.stock, stockToRemove.amountToPick);
            }catch(InventoryException invex){
                ExceptionAnnotator.annotate(invex);
            }
        }
        
        if(chosenStockList.size() == 0){
            orderPosMap.remove(actuOrderPos.getId());
        }
    }
    
    public List<BODTO<LOSCustomerOrderPosition>> getHandledPositions(){
        
        Set<Long> orderPosIdSet = orderPosMap.keySet();
               
        List<BODTO<LOSCustomerOrderPosition>> handledPosList;
        handledPosList = new ArrayList<BODTO<LOSCustomerOrderPosition>>(orderPosIdSet.size());
        
        for(Long id : orderPosIdSet){
            
            if(orderPosMap.get(id).size() > 0){
                handledPosList.add(orderPosMap.get(id).get(0).orderPosition);
            }
        }
        
        return handledPosList;
    }
    
    public void clear(){
        
        this.amountToPick = new BigDecimal(0);
        
        actuOrderPos = null;
        
        orderPosMap.clear();
        orderPosMap = new HashMap<Long, List<CreatePickRequestPositionTO>>();
    }
    
    public void clearReservation() throws InventoryException{
        
        List<CreatePickRequestPositionTO> posList = getChosenStocks();
        
        for(CreatePickRequestPositionTO pos:posList){
            
            inventoryFacade.releaseReservation(pos.stock, pos.amountToPick);
            
        }
    }
  
}
