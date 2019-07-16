/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.treat_order.gui.model;

import de.linogistix.common.gui.component.controls.BOAutoFilteringComboBox;
import de.linogistix.common.gui.component.view.LOSListChooserViewModel;
import de.linogistix.common.bobrowser.query.gui.component.BOQueryComponentProvider;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.inventory.facade.LOSCompatibilityFacade;
import de.linogistix.los.inventory.query.StockUnitQueryRemote;
import de.linogistix.los.inventory.query.dto.LOSOrderStockUnitTO;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.wmsprocesses.processes.treat_order.TreatOrderStockUnitBO;
import de.linogistix.wmsprocesses.processes.treat_order.gui.control.TreatOrderDialogController;
import de.linogistix.wmsprocesses.processes.treat_order.gui.control.TreatOrderStockQueryProvider;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.delivery.DeliveryOrderLine;
import de.wms2.mywms.product.ItemData;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.mywms.facade.FacadeException;
import org.openide.util.Lookup;

/**
 *
 * @author Jordan
 */
public class TreatOrderStockSelectionModel extends LOSListChooserViewModel<StockUnit>{

    private BODTO<DeliveryOrderLine> orderPositionTO;
    
    private BODTO<Lot> lotTO;
    
    private BODTO<StorageLocation> locationTO;
    
    private TreatOrderStockQueryProvider stockQueryProvider;
    
    private LOSCompatibilityFacade orderFacade;
    
    private List<BODTO> mySelectionList = new ArrayList<BODTO>();
    
    private TreatOrderDialogController dialogController;
    
    public TreatOrderStockSelectionModel() throws Exception{
        
        super(new TreatOrderStockUnitBO());
               
        J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
        StockUnitQueryRemote queryRemote = loc.getStateless(StockUnitQueryRemote.class);
        
        stockQueryProvider = new TreatOrderStockQueryProvider(queryRemote, this);
        
        orderFacade = loc.getStateless(LOSCompatibilityFacade.class);
    }
    
    public void setDialogController(TreatOrderDialogController dialogController){
        this.dialogController = dialogController;
    }
    
    @Override
    public BOQueryComponentProvider getDefaultBOQueryProvider() {
        return stockQueryProvider;
    }
    
    @Override
    public List<BOQueryComponentProvider> getQueryComponentProviders() {
        
        List<BOQueryComponentProvider> providerList = new ArrayList<BOQueryComponentProvider>();   
                
        providerList.add(stockQueryProvider);
                
        return providerList;
    }

    @Override
    @SuppressWarnings("unchecked")
    public LOSResultList getResults(BOQueryComponentProvider provider, QueryDetail detail) {
        try{
            List l = orderFacade.querySuitableStocksByOrderPosition(orderPositionTO, lotTO, locationTO);
            return     (LOSResultList<BODTO>) l;
        }catch(FacadeException fex){
            ExceptionAnnotator.annotate(fex);
            return new LOSResultList<BODTO>();
        }
                
    }

    @Override
    public void addToSelectionList(List<BODTO> selectedEntities) {
        
        List<BODTO> showAmountList = new ArrayList<BODTO>();
        
        for(BODTO to:selectedEntities){
            LOSOrderStockUnitTO selStockTO = (LOSOrderStockUnitTO) to;
            
            BigDecimal chosenAmount = dialogController.addChosenStock(selStockTO);
            
            if(chosenAmount.compareTo(new BigDecimal(0)) == 0){
                continue;
            }
            
            LOSOrderStockUnitTO showAmountTO = new LOSOrderStockUnitTO(selStockTO.getId(), 
                                                                       selStockTO.getVersion(), 
                                                                       selStockTO.getId()+" / "+chosenAmount,
                                                                       selStockTO.lotEntity,
                                                                       selStockTO.unitLoad,
                                                                       selStockTO.storageLocation,
                                                                       selStockTO.availableAmount,
                                                                       new BigDecimal(0));
            showAmountList.add(showAmountTO);
        }
        
        super.addToSelectionList(showAmountList);
    }

    
    
    @Override
    public void removeFromSelectionList(List<BODTO> selectedEntities) {
        
        for(BODTO to:selectedEntities){
            LOSOrderStockUnitTO selStockTO = (LOSOrderStockUnitTO) to;
            
            dialogController.removeChosenStock(selStockTO);
        }
        
        super.removeFromSelectionList(selectedEntities);
    }
        
    public void clear(){
        
        lotTO = null;
        locationTO = null;
        orderPositionTO = null;
        
        clearSelectionList();
        
        stockQueryProvider.clear();
    }
    
    public BODTO<StorageLocation> getLocationTO() {
        return locationTO;
    }

    public void setLocationTO(BODTO<StorageLocation> locationTO) {
        this.locationTO = locationTO;
    }

    public BODTO<Lot> getLotTO() {
        return lotTO;
    }

    public void setLotTO(BODTO<Lot> lotTO) {
        this.lotTO = lotTO;
        
        stockQueryProvider.setLotTO(lotTO);
    }

    public void setItemDataTO(BODTO<ItemData> item){
        stockQueryProvider.setItemDataTO(item);
    }
    
    public BODTO<DeliveryOrderLine> getOrderPositionTO() {
        return orderPositionTO;
    }

    public void setOrderPositionTO(BODTO<DeliveryOrderLine> orderPositionTO) {
        this.orderPositionTO = orderPositionTO;
    }
    
    public BOAutoFilteringComboBox<Lot> getLotCombo(){
        return stockQueryProvider.getLotCombo();
    }
    
    public BOAutoFilteringComboBox<StorageLocation> getStorageLocationCombo(){
        return stockQueryProvider.getStorageLocationCombo();
    }
}
