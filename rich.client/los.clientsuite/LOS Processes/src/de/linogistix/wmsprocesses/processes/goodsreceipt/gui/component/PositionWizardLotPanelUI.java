/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.goodsreceipt.gui.component;

import de.linogistix.common.gui.component.controls.BOAutoFilteringComboBox;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.inventory.gui.component.controls.LotComboBoxModel;
import de.linogistix.los.inventory.query.ItemDataQueryRemote;
import de.linogistix.los.inventory.query.LotQueryRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.wmsprocesses.lot.gui.component.LotOptionPanel;
import de.linogistix.wmsprocesses.processes.goodsreceipt.gui.gui_builder.AbstractPositionWizardLotPanelUI;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.product.ItemData;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Lookup;

/**
 *
 * @author trautm
 */
public class PositionWizardLotPanelUI extends AbstractPositionWizardLotPanelUI{
   
    private static final Logger log = Logger.getLogger(PositionWizardLotPanelUI.class.getName());
    private PropertyChangeListener delegateTo;
    
    private ItemDataQueryRemote itemQueryRemote;
    private LotQueryRemote lotQueryRemote;
    
    public PositionWizardLotPanelUI(PropertyChangeListener delegate){
        
        this.delegateTo = delegate;
        
        try {
            LotComboBoxModel m = new LotComboBoxModel();
            getLotComboBox().setComboBoxModel(m);
            
            J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
            this.itemQueryRemote = loc.getStateless(ItemDataQueryRemote.class);
            this.lotQueryRemote = loc.getStateless(LotQueryRemote.class);
            
        } catch (Exception ex) {
            ExceptionAnnotator.annotate(ex);
        }
       
        getLotOptionPanel().setBorder(null);
        getLotOptionPanel().initAutofiltering();
        
    }
    
    void initValues(PositionWizardModel wm) {
                          
        String lotStr = wm.lotStr;
        
        getLotOptionPanel().setMandatory(true);
        
        if (lotStr != null && lotStr.length() > 0){
            getLotOptionPanel().getLotNumberTextField().setText(lotStr);
            getLotOptionPanel().setChooseLotOptionEnabled(false);
            getLotOptionPanel().setCreateLotOptionEnabled(true);
        }
        else if(wm.lot != null){
            
            getLotComboBox().getComboBoxModel().setSingleResult(wm.lot);
            
            getLotOptionPanel().setChooseLotOptionEnabled(true);
            getLotOptionPanel().setCreateLotOptionEnabled(false); 
        }
        else if (wm.selectedAdvice != null){
            ItemData item;
            Lot lot = null;
            if(wm.selectedAdvice.getLotNumber() != null){
                lot = lotQueryRemote.queryByNameAndItemData(wm.selectedAdvice.getLotNumber(), wm.selectedAdvice.getItemData());
            }
            if(lot!=null) {
                item = lot.getItemData();
                BODTO<Lot> lotTO = new BODTO<Lot>(lot.getId(), lot.getVersion(), lot.getName());
                getLotComboBox().addItem(lotTO);
                getLotComboBox().getComboBoxModel().setSingleResult(lotTO);
                
                getLotOptionPanel().lockCreateLotOption(true);
                getLotOptionPanel().setChooseLotOptionEnabled(true);
                getLotOptionPanel().setCreateLotOptionEnabled(false);
            }
            else{
                
                item = wm.selectedAdvice.getItemData();
                BODTO<ItemData> itemTO = new BODTO<ItemData>(item.getId(), 
                                                         item.getVersion(), 
                                                         item.getNumber());
                
                ((LotComboBoxModel)getLotComboBox().getComboBoxModel()).setItemDataTO(itemTO);
            }
                
        }
         
        Date validFrom = wm.validFrom;
        
        if (validFrom != null){
            getLotOptionPanel().getValidFromTextField().setText(new SimpleDateFormat("dd.MM.yyyy").format(validFrom));
        }
        
        Date validTo = wm.validTo;
        if (validTo != null){
            getLotOptionPanel().getValidToTextField().setText(new SimpleDateFormat("dd.MM.yyyy").format(validTo));
        }
        
        try{
            ItemData idat = itemQueryRemote.queryById(wm.item.getId());
            getLotOptionPanel().setMandatory(idat.isLotMandatory());
        } catch (Throwable ex){
            log.log(Level.SEVERE, ex.getMessage(), ex);
            getLotOptionPanel().setMandatory(true);
        }
        if (delegateTo != null) {
            delegateTo.propertyChange(null);
        }
        
     }
    
    public void clear() {
        getLotOptionPanel().clear();
    }

    public boolean validateLot(){
        
        if(getLotOptionPanel().isLotChoosen()){
            return getLotComboBox().checkSanity();
        }
        else{
            return getLotOptionPanel().getLotNumberTextField().checkSanity();
        }
        
    }
    
      //------------------------------------------------------------------------
    protected BOAutoFilteringComboBox<Lot> getLotComboBox() {
        return ((LotOptionPanel) lotOptionPanel).getLotComboBox();
    }
    
    

}
