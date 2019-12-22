/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.inventory.gui.component.controls;

import de.linogistix.common.gui.component.controls.BOAutoFilteringComboBox;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.gui.component.other.LiveHelp;
import de.linogistix.los.query.BODTO;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.product.ItemData;
import java.awt.Component;
import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.SortedMap;
import java.util.Vector;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.mywms.model.Client;
import org.openide.util.NbBundle;

/**
 *
 * @author trautm
 */
public class ClientItemDataLotFilteringComponent {

    private static final Logger log =  Logger.getLogger(ClientItemDataLotFilteringComponent.class.getName());
    
    private BOAutoFilteringComboBox<ItemData> itemDataCombo;
    private BOAutoFilteringComboBox<Lot> lotCombo;
    
    private boolean itemDataMandatory = false;
    private boolean lotMandatory = false;
    
    private Container parentContainer;
    
    public ClientItemDataLotFilteringComponent(Container parent) throws Exception{
        this();
        this.parentContainer = parent;
    }
        
    public ClientItemDataLotFilteringComponent() throws Exception{
        
        this(new BOAutoFilteringComboBox<Client>(),
             new BOAutoFilteringComboBox<ItemData>(),
             new BOAutoFilteringComboBox<Lot>());
        
        itemDataCombo.setBoClass(ItemData.class);
        itemDataCombo.initAutofiltering();
        itemDataCombo.setEditorLabelTitle(NbBundle.getMessage(CommonBundleResolver.class, "ItemData"));
        lotCombo.setBoClass(Lot.class);
        lotCombo.initAutofiltering();
        lotCombo.setEditorLabelTitle(NbBundle.getMessage(CommonBundleResolver.class, "Lot"));
    }
    
    private ClientItemDataLotFilteringComponent(BOAutoFilteringComboBox<Client> clientCombo,
                                               BOAutoFilteringComboBox<ItemData> itemDataCombo,
                                               BOAutoFilteringComboBox<Lot> lotCombo) throws Exception
    {

        this.itemDataCombo = itemDataCombo;
        this.itemDataCombo.setComboBoxModel(new ItemDataComboBoxModel());
        this.itemDataCombo.addItemChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
               itemDataChanged(evt);
            }
        });

        this.lotCombo = lotCombo;
        this.lotCombo.setComboBoxModel(new LotComboBoxModel());
        this.lotCombo.addItemChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
               lotChanged(evt);
            }
        });

    }

    protected void itemDataChanged(PropertyChangeEvent evt){
        
        System.out.println("--- ItemChanged Event ---");
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              initCombosFromItemData();
            }
        });
    }
    
    protected void lotChanged(PropertyChangeEvent evt){
        
        System.out.println("--- LotChanged Event ---");
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              initCombosFromLot();
            }
        });
    }
    
    private void initCombosFromItemData(){
        
        System.out.println("--- Init Combos From ItemData ---");
        
        ItemData itemData = itemDataCombo.getSelectedAsEntity();
        BODTO<ItemData> itemTO = itemDataCombo.getSelectedItem();
        
        // if user clears item combo box
        if(itemData == null || itemTO == null){
            
            ((LotComboBoxModel)lotCombo.getComboBoxModel()).setItemDataTO(null);
            
            return;
        }
        
        lotCombo.clear();
        ((LotComboBoxModel)lotCombo.getComboBoxModel()).setItemDataTO(itemTO);
    }
    
    private void initCombosFromLot(){
        
        System.out.println("--- Init Combos From Lot ---");
        
        Lot lot = lotCombo.getSelectedAsEntity();
        BODTO<Lot> lotTO = lotCombo.getSelectedItem();  
        
        // if user clears lot combo box
        if(lot == null || lotTO == null){
            
            ((ItemDataComboBoxModel)itemDataCombo.getComboBoxModel()).setLotTO(null);
            
            return;
        }
        
        // if lot is set, itemData is fixed      
        ItemData itemData = lot.getItemData();
        
        itemDataCombo.clear();
        
        BODTO<ItemData> itemTO = new BODTO<ItemData>(itemData.getId(), itemData.getVersion(), itemData.getNumber());
        
        itemDataCombo.addItem(itemTO);
        
        ((ItemDataComboBoxModel)itemDataCombo.getComboBoxModel()).setLotTO(lotTO);
        
        ((LotComboBoxModel)lotCombo.getComboBoxModel()).setItemDataTO(itemTO);
    }
    
    public BODTO<Lot> getLot(){
        if (getLotCombo() != null){
            return getLotCombo().getSelectedItem();
        } else{
            return null;
        }
        
    }
    public BODTO<ItemData> getItemData(){
        if (getItemDataCombo() != null){
            return getItemDataCombo().getSelectedItem();
        } else {
            return null;
        }
    }

    public BOAutoFilteringComboBox<ItemData> getItemDataCombo() {
        return itemDataCombo;
    }

    public BOAutoFilteringComboBox<Lot> getLotCombo() {
        return lotCombo;
    }
    
    public void clear(){
        this.itemDataCombo.clear();
        this.lotCombo.clear();
    }

    public boolean isItemDataMandatory() {
        return itemDataMandatory;
    }

    public void setItemDataMandatory(boolean itemDataMandatory) {
        this.itemDataMandatory = itemDataMandatory;
    }

    public boolean isLotMandatory() {
        return lotMandatory;
    }

    public void setLotMandatory(boolean lotMandatory) {
        this.lotMandatory = lotMandatory;
    }
    
    //-----------------------------------------------------------------------
    public void enableComponent() {
        SortedMap<Integer, Component[]> map = LiveHelp.getInstance().getComponentMap();
        //default enable all. Disable all components which named in second param
        map.put(1, LiveHelp.getInstance().getComponents(parentContainer, new Component[]{
            getLotCombo().getEditorLabel(),
            getLotCombo().getAutoFilteringComboBox(),
            getLotCombo().getOpenChooserButton(),
            getItemDataCombo().getEditorLabel(),
            getItemDataCombo().getAutoFilteringComboBox(),
            getItemDataCombo().getOpenChooserButton()}));
        //default enable all
        map.put(10, new Component[]{
            getLotCombo().getEditorLabel(),
            getLotCombo().getAutoFilteringComboBox(),
            getLotCombo().getOpenChooserButton(),
            getItemDataCombo().getEditorLabel(),
            getItemDataCombo().getAutoFilteringComboBox(),
            getItemDataCombo().getOpenChooserButton()});

        LiveHelp.getInstance().processEnable(parentContainer);

    }

    protected Vector<Component[]> getEmptyFieldsArguments() {
        Vector<Component[]> v = new Vector<Component[]>();
        if (itemDataMandatory) v.add(new Component[]{getItemDataCombo().getEditorLabel(), getItemDataCombo().getAutoFilteringComboBox()});
        if (lotMandatory) v.add(new Component[]{getLotCombo().getEditorLabel() , getLotCombo().getAutoFilteringComboBox()});
        
        return v;
    }

    public void processLiveHelp(){
        if (LiveHelp.getInstance().hasEmptyFields(parentContainer, getEmptyFieldsArguments(), true)) {
            return;
        } else if (LiveHelp.getInstance().hasErrorFields(parentContainer, true)) {
            return;
        }
    }
    
    public Container getParentContainer() {
        return parentContainer;
    }

    public void setParentContainer(Container parentContainer) {
        this.parentContainer = parentContainer;
    }

    //-----------------------------------------------------------------------
    
    
    
}
