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
    
    private boolean itemDataMandatory = false;
    
    private Container parentContainer;
    
    public ClientItemDataLotFilteringComponent(Container parent) throws Exception{
        this();
        this.parentContainer = parent;
    }
        
    public ClientItemDataLotFilteringComponent() throws Exception{
        
        this(new BOAutoFilteringComboBox<Client>(),
             new BOAutoFilteringComboBox<ItemData>());
        
        itemDataCombo.setBoClass(ItemData.class);
        itemDataCombo.initAutofiltering();
        itemDataCombo.setEditorLabelTitle(NbBundle.getMessage(CommonBundleResolver.class, "ItemData"));
    }
    
    private ClientItemDataLotFilteringComponent(BOAutoFilteringComboBox<Client> clientCombo,
                                               BOAutoFilteringComboBox<ItemData> itemDataCombo) throws Exception
    {

        this.itemDataCombo = itemDataCombo;
        this.itemDataCombo.setComboBoxModel(new ItemDataComboBoxModel());
        this.itemDataCombo.addItemChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
               itemDataChanged(evt);
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
    
    private void initCombosFromItemData(){
        
        System.out.println("--- Init Combos From ItemData ---");
        
        ItemData itemData = itemDataCombo.getSelectedAsEntity();
        BODTO<ItemData> itemTO = itemDataCombo.getSelectedItem();
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
    
    public void clear(){
        this.itemDataCombo.clear();
    }

    public boolean isItemDataMandatory() {
        return itemDataMandatory;
    }

    public void setItemDataMandatory(boolean itemDataMandatory) {
        this.itemDataMandatory = itemDataMandatory;
    }

    //-----------------------------------------------------------------------
    public void enableComponent() {
        SortedMap<Integer, Component[]> map = LiveHelp.getInstance().getComponentMap();
        //default enable all. Disable all components which named in second param
        map.put(1, LiveHelp.getInstance().getComponents(parentContainer, new Component[]{
            getItemDataCombo().getEditorLabel(),
            getItemDataCombo().getAutoFilteringComboBox(),
            getItemDataCombo().getOpenChooserButton()}));
        //default enable all
        map.put(10, new Component[]{
            getItemDataCombo().getEditorLabel(),
            getItemDataCombo().getAutoFilteringComboBox(),
            getItemDataCombo().getOpenChooserButton()});

        LiveHelp.getInstance().processEnable(parentContainer);

    }

    protected Vector<Component[]> getEmptyFieldsArguments() {
        Vector<Component[]> v = new Vector<Component[]>();
        if (itemDataMandatory) v.add(new Component[]{getItemDataCombo().getEditorLabel(), getItemDataCombo().getAutoFilteringComboBox()});
        
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
