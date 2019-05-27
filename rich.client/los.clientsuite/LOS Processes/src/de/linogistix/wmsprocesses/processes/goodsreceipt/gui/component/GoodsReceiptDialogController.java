/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.goodsreceipt.gui.component;

import de.linogistix.common.gui.component.controls.BOAutoFilteringComboBox;
import de.linogistix.common.gui.component.controls.LOSNumericFormattedTextField;
import de.linogistix.inventory.gui.component.controls.AdviceComboBoxModel;
import de.linogistix.inventory.gui.component.controls.ItemDataComboBoxModel;
import de.linogistix.inventory.gui.component.controls.LotComboBoxModel;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.query.dto.LotTO;
import de.linogistix.los.query.BODTO;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.mywms.model.Client;

/**
 *
 * @author Jordan
 */
public class GoodsReceiptDialogController {

//    private static final Logger log =  Logger.getLogger(GoodsReceiptDialogController.class.getName());
//    
//    private BOAutoFilteringComboBox<Client> clientCombo;
//    private BOAutoFilteringComboBox<ItemData> itemDataCombo;
//    private BOAutoFilteringComboBox<Lot> lotCombo;
//    private BOAutoFilteringComboBox<LOSAdvice> adviceCombo;
//    private LOSNumericFormattedTextField amountTextField;
//    private CenterPanel centerPanel;
//    
//    public GoodsReceiptDialogController(BOAutoFilteringComboBox<Client> clientCombo,
//                                        BOAutoFilteringComboBox<ItemData> itemDataCombo,
//                                        BOAutoFilteringComboBox<Lot> lotCombo,
//                                        BOAutoFilteringComboBox<LOSAdvice> adviceCombo,
//                                        LOSNumericFormattedTextField amountTextfield,
//                                        CenterPanel centerPanel) throws Exception
//    {
//        this.clientCombo = clientCombo;
//        this.clientCombo.addItemChangeListener(new PropertyChangeListener() {
//            public void propertyChange(PropertyChangeEvent evt) {
//                clientChanged(evt);
//            }
//        });
//        
//        this.itemDataCombo = itemDataCombo;
//        this.itemDataCombo.setComboBoxModel(new ItemDataComboBoxModel());        
//        this.itemDataCombo.addItemChangeListener(new PropertyChangeListener() {
//            public void propertyChange(PropertyChangeEvent evt) {
//               itemDataChanged(evt);
//            }
//        });
//        
//        
//        this.lotCombo = lotCombo;
//        this.lotCombo.setComboBoxModel(new LotComboBoxModel());
//        this.lotCombo.addItemChangeListener(new PropertyChangeListener() {
//            public void propertyChange(PropertyChangeEvent evt) {
//               lotChanged(evt);
//            }
//        });
//        
//        this.adviceCombo = adviceCombo;
//        this.adviceCombo.setComboBoxModel(new AdviceComboBoxModel());
//        this.adviceCombo.addItemChangeListener(new PropertyChangeListener() {
//            public void propertyChange(PropertyChangeEvent evt) {
//               adviceChanged(evt);
//            }
//
//        });
//        
//        this.amountTextField = amountTextfield;
//        
//        this.centerPanel = centerPanel;
//    }
//    
//    protected void clientChanged(PropertyChangeEvent evt){
//           
//        lotCombo.clear();
//        itemDataCombo.clear();
//        adviceCombo.clear();
//
//        BODTO<Client> client = clientCombo.getSelectedItem();
//
//        ((LotComboBoxModel)lotCombo.getComboBoxModel()).setClientTO(client);
//        ((ItemDataComboBoxModel)itemDataCombo.getComboBoxModel()).setClientTO(client);
//        ((AdviceComboBoxModel)adviceCombo.getComboBoxModel()).setClientTO(client);
//        
//    }
//    
//    protected void adviceChanged(PropertyChangeEvent evt) {
//        
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//              initCombosFromAdvice();
//            }
//        });
//        
//    }
//    
//    protected void itemDataChanged(PropertyChangeEvent evt){
//       
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//              initCombosFromItemData();
//            }
//        });
//    }
//    
//    protected void lotChanged(PropertyChangeEvent evt){
//        
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//              initCombosFromLot();
//            }
//        });
//    }
//    
//    public void initCombosFromAdvice(){
//        
//        LOSAdvice advice = adviceCombo.getSelectedAsEntity();
//        BODTO<LOSAdvice> adviceTO = adviceCombo.getSelectedItem();
//        
//        ItemDataComboBoxModel itemComboModel = (ItemDataComboBoxModel) itemDataCombo.getComboBoxModel();
//        LotComboBoxModel lotComboModel = (LotComboBoxModel)lotCombo.getComboBoxModel();
//        
//        // if user clears advice combo box
//        if(advice == null || adviceTO == null){
//            
//            itemComboModel.setSingleResult(null);
//            lotComboModel.setSingleResult(null);
//            
//            return;
//        }
//        
//        // if user did not select a client before
//        BODTO<Client> client = clientCombo.getSelectedItem();
//        if(client == null){
//            clientCombo.clear();
//            clientCombo.addItem(new BODTO<Client>(advice.getClient().getId(), 
//                                                  advice.getClient().getVersion(), 
//                                                  advice.getClient().getName()));
//            
//            client = clientCombo.getSelectedItem();
//        }
//        
//        // if advice contains a lot, preset lot 
//        Lot lot = advice.getLot();
//        if(lot != null){
//            lotCombo.clear();
//            lotComboModel.setSingleResult(new LotTO(lot.getId(), lot.getVersion(), 
//                                                    lot.getName(), lot.getItemData().getName(),
//                                                    lot.getLock(), lot.getUseNotBefore(),
//                                                    lot.getBestBeforeEnd()));
//
//            lotCombo.addItem(new BODTO<Lot>(lot.getId(), lot.getVersion(), lot.getName()));
//            
//            centerPanel.getLotOptionPanel().setChooseLotOptionEnabled(true);
//            centerPanel.getLotOptionPanel().setCreateLotOptionEnabled(false);
//        }
//        else{
//            centerPanel.getLotOptionPanel().setChooseLotOptionEnabled(false);
//            centerPanel.getLotOptionPanel().setCreateLotOptionEnabled(true);
//        }
//        
//        // preset item data
//        itemDataCombo.clear();
//        
//        ItemData item = advice.getItemData();
//        BODTO<ItemData> itemTO = new BODTO<ItemData>(item.getId(), item.getVersion(), item.getName());
//        itemComboModel.setSingleResult(itemTO);
//        
//        itemDataCombo.addItem(itemTO);
//        
//        ((LotComboBoxModel)lotCombo.getComboBoxModel()).setItemDataTO(itemTO);
//        ((LotComboBoxModel)lotCombo.getComboBoxModel()).setClientTO(client);
//        
//        amountTextField.setEnabled(true);
//        amountTextField.setUnitName(item.getHandlingUnit().getUnitName());
//        amountTextField.setScale(item.getScale());
//
//        centerPanel.validate();
//        centerPanel.getLotOptionPanel().validate();
//        
//    }
//    
//    private void initCombosFromItemData(){
//        
//        System.out.println("--- Init Combos From ItemData ---");
//        
//        ItemData itemData = itemDataCombo.getSelectedAsEntity();
//        BODTO<ItemData> itemTO = itemDataCombo.getSelectedItem();
//        
//        // if user clears item combo box
//        if(itemData == null || itemTO == null){
//            
//            ((LotComboBoxModel)lotCombo.getComboBoxModel()).setItemDataTO(null);
//            ((AdviceComboBoxModel)adviceCombo.getComboBoxModel()).setItemDataTO(null);
//            
//            amountTextField.setEnabled(false);
//            
//            return;
//        }
//        
//        // if user did not select a client before
//        BODTO<Client> client = clientCombo.getSelectedItem();
//        if(client == null){
//            clientCombo.clear();
//            clientCombo.addItem(new BODTO<Client>(itemData.getClient().getId(), 
//                                                  itemData.getClient().getVersion(), 
//                                                  itemData.getClient().getName()));
//            
//            client = clientCombo.getSelectedItem();
//        }
//        
//        ((LotComboBoxModel)lotCombo.getComboBoxModel()).setItemDataTO(itemTO);
//        ((LotComboBoxModel)lotCombo.getComboBoxModel()).setClientTO(client);
//        
//        ((AdviceComboBoxModel)adviceCombo.getComboBoxModel()).setItemDataTO(itemTO);
//        ((AdviceComboBoxModel)adviceCombo.getComboBoxModel()).setClientTO(client);
//        
//        amountTextField.setEnabled(true);
//        amountTextField.setUnitName(itemData.getHandlingUnit().getUnitName());
//        amountTextField.setScale(itemData.getScale());
//    }
//    
//    private void initCombosFromLot(){
//        
//        System.out.println("--- Init Combos From Lot ---");
//        
//        Lot lot = lotCombo.getSelectedAsEntity();
//        BODTO<Lot> lotTO = lotCombo.getSelectedItem();  
//        
//        // if user clears lot combo box
//        if(lot == null || lotTO == null){
//            
//            itemDataCombo.getComboBoxModel().setSingleResult(null);
//            ((ItemDataComboBoxModel)itemDataCombo.getComboBoxModel()).setLotTO(null);
//            ((AdviceComboBoxModel)adviceCombo.getComboBoxModel()).setLotTO(null);
//            
//            return;
//        }
//        
//        // if user did not select a client before
//        BODTO<Client> client = clientCombo.getSelectedItem();
//        if(client == null){
//            clientCombo.clear();
//            clientCombo.addItem(new BODTO<Client>(lot.getClient().getId(), 
//                                                  lot.getClient().getVersion(), 
//                                                  lot.getClient().getName()));
//            
//            client = clientCombo.getSelectedItem();
//        }
//        
//        // if user did not select a item before
//        BODTO<ItemData> itemTO = itemDataCombo.getSelectedItem();
//        if(itemTO == null){
//               
//            ItemData itemData = lot.getItemData();
//            itemDataCombo.addItem(new BODTO<ItemData>(itemData.getId(), 
//                                                      itemData.getVersion(), 
//                                                      itemData.getName()));
//            
//            itemTO = itemDataCombo.getSelectedItem();
//            
//            itemDataCombo.getComboBoxModel().setSingleResult(itemTO);
//            
//            ItemData item = itemDataCombo.getSelectedAsEntity();
//            amountTextField.setEnabled(true);
//            amountTextField.setUnitName(item.getHandlingUnit().getUnitName());
//            amountTextField.setScale(item.getScale());
//        }
//             
//        ((LotComboBoxModel)lotCombo.getComboBoxModel()).setClientTO(client);
//        ((LotComboBoxModel)lotCombo.getComboBoxModel()).setItemDataTO(itemTO);
//        
//        ((ItemDataComboBoxModel)itemDataCombo.getComboBoxModel()).setLotTO(lotTO);
//        ((ItemDataComboBoxModel)itemDataCombo.getComboBoxModel()).setClientTO(client);
//        
//        ((AdviceComboBoxModel)adviceCombo.getComboBoxModel()).setClientTO(client);
//        ((AdviceComboBoxModel)adviceCombo.getComboBoxModel()).setItemDataTO(itemTO);
//        ((AdviceComboBoxModel)adviceCombo.getComboBoxModel()).setLotTO(lotTO);
//    }
//    
//    public void clear(){
//        
//        BODTO<Client> client = clientCombo.getSelectedItem();
//            
//        lotCombo.setQueryMethodArgs(new Object[]{"",client, null});
//        itemDataCombo.setQueryMethodArgs(new Object[]{"", client, null});
//        adviceCombo.setQueryMethodArgs(new Object[]{"", client, null, null});
//        
//    }
}
