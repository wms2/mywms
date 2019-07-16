/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.order.gui.component;

import de.linogistix.common.gui.component.controls.BOAutoFilteringComboBox;
import de.linogistix.wmsprocesses.processes.order.gui.gui_builder.AbstractCenterPanel;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.inventory.gui.component.controls.ClientItemDataLotFilteringComponent;
import de.linogistix.inventory.gui.component.controls.ItemDataComboBoxModel;
import de.linogistix.inventory.gui.component.controls.LotComboBoxModel;
import de.linogistix.los.inventory.facade.LOSOrderFacade;
import de.linogistix.los.inventory.facade.OrderPositionTO;
import de.linogistix.los.inventory.query.LOSOrderStrategyQueryRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.util.entityservice.LOSSystemPropertyServiceRemote;
import de.linogistix.los.util.StringTools;
import de.linogistix.wmsprocesses.processes.order.gui.object.OrderItem;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.strategy.OrderStrategy;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.util.Wms2Properties;
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author artur
 */
public class CenterPanel extends AbstractCenterPanel {

    private J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
    private final static Logger log = Logger.getLogger(CenterPanel.class.getName());
    TopComponentPanel topComponentPanel;
    ClientItemDataLotFilteringComponent cilComp;
     
    public CenterPanel(TopComponentPanel topComponentPanel) {
        this.topComponentPanel = topComponentPanel;
        try {
            cilComp = new ClientItemDataLotFilteringComponent();
            clientComboBoxPanel.add(getClientComboBox(), BorderLayout.CENTER);
            lotComboBoxPanel.add(getLotComboBox(), BorderLayout.CENTER);
            itemComboBoxPanel.add(getItemDataComboBox(), BorderLayout.CENTER);

        }
        catch(Exception e) {
            e.printStackTrace();
        }

        getDeliveryDateTextField().setEnabled(true);
        getDeliveryDateTextField().setMandatory(true);
        getDeliveryDateTextField().setColumns(8);
        getDeliveryDateTextField().getTextFieldLabel().setTitleText(NbBundle.getMessage(WMSProcessesBundleResolver.class,"DATE_OF_ALLOCATION"));
        
//        getAmountTextField().setEnabled(false);
        getAmountTextField().setMinimumValue(new BigDecimal(0), false);
        getAmountTextField().setMandatory(true);
        getAmountTextField().setColumns(8);
        getAmountTextField().getTextFieldLabel().setTitleText(NbBundle.getMessage(WMSProcessesBundleResolver.class,"Amount"));
        
        getCommentLabel().setText(NbBundle.getMessage(WMSProcessesBundleResolver.class,"Comment"));

        orderTypeCombo.addItemListener(new ItemListener(){

            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() ==ItemEvent.SELECTED){
                    onStrategySelect();
                }
            }
        });

    }

    private void initAutofiltering(){
        
        getClientComboBox().setEnabled(true);
        getClientComboBox().setMandatory(true);
        getClientComboBox().setEditorLabelTitle(NbBundle.getMessage(WMSProcessesBundleResolver.class,"client"));
        
        getItemDataComboBox().setEnabled(true);
        getItemDataComboBox().setMandatory(true);
        getItemDataComboBox().setEditorLabelTitle(NbBundle.getMessage(WMSProcessesBundleResolver.class,"itemData"));
        
        getItemDataComboBox().addItemChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
               
                ItemData selItemData = getItemDataComboBox().getSelectedAsEntity();

                if(selItemData != null){
//                    getAmountTextField().setEnabled(true);
                    getAmountTextField().setUnitName(selItemData.getItemUnit().getUnitName());
                    getAmountTextField().setScale(selItemData.getScale());
                }
//                else {
//                    getAmountTextField().setEnabled(false);
//                }
                
            }
        });
        
        getLotComboBox().setMandatory(false); 
        getLotComboBox().setEditorLabelTitle(NbBundle.getMessage(WMSProcessesBundleResolver.class, "lot"));
        getLotComboBox().addItemChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                
                if(getLotComboBox().getSelectedAsEntity() == null){
                    return;
                }
                
                ItemData selItemData = getLotComboBox().getSelectedAsEntity().getItemData();

                if(selItemData != null){
//                    getAmountTextField().setEnabled(true);
                    getAmountTextField().setUnitName(selItemData.getItemUnit().getUnitName());
                    getAmountTextField().setScale(selItemData.getScale());
                }
//                else {
//                    getAmountTextField().setEnabled(false);
//                }
                
            }
        });
        
//        try{
//            cilComp = new ClientItemDataLotFilteringComponent(getClientComboBox(), getItemDataComboBox(), getLotComboBox());
//        } catch (Exception ex) {
//            ExceptionAnnotator.annotate(ex);
//        }
        
    }

    protected BOAutoFilteringComboBox<Client> getClientComboBox() {
        return cilComp.getClientCombo();
    }

    protected BOAutoFilteringComboBox<Lot> getLotComboBox() {
        return cilComp.getLotCombo();
    }

    protected BOAutoFilteringComboBox<ItemData> getItemDataComboBox() {
        return cilComp.getItemDataCombo();
    }

    @Override
    public void componentOpened() {
        super.componentOpened();
        
        initOrderTreeTable();
        
        initAutofiltering();      
        
        processTargetplace();
        
        this.requestFocus();
        
        invalidate();
        getParent().invalidate();
        
        initDefaults();
        clear();
    }

    @Override
    public void componentActivated() {
        super.componentActivated();
        treeTable.repaint();
        getClientComboBox().requestFocus();
    }

    
    void clear() {

        
        orderNumberTextField.setText("");
        labelTextField.setText("");
        documentTextField.setText("");
        // ---------------
        
//        getDeliveryDateTextField().setText("");
        
        getCommentArea().setText("");
        
        setOrderDetailsEnabled(true);
        
        clearPositionDetail();

        treeTable.clear();
        treeTable.repaint();
        
        getClientComboBox().requestFocus();
    }

//    void clearAlt() {
//
//
//        orderNumberTextField.setText("");
//        labelTextField.setText("");
//        documentTextField.setText("");
//        // ---------------
//
//        getDeliveryDateTextField().setText("");
//
//        getCommentArea().setText("");
//
//        setOrderDetailsEnabled(true);
//
//        clearPositionDetail();
//
//        processTargetplace();
//        treeTable.clear();
//        treeTable.repaint();
//
//        initDefaults();
//        getClientComboBox().requestFocus();
//    }

    private void clearPositionDetail() {
        
        getAmountTextField().setValue(BigDecimal.ZERO);
//        getAmountTextField().setEnabled(false);
 
        BODTO<Client> clientTO = getClientComboBox().getSelectedItem();
        
        getLotComboBox().clear();
        ((LotComboBoxModel)getLotComboBox().getComboBoxModel()).setClientTO(clientTO);
        
        getItemDataComboBox().clear();
        ((ItemDataComboBoxModel)getItemDataComboBox().getComboBoxModel()).setClientTO(clientTO);
        
    }
    
    private void setOrderDetailsEnabled(boolean enabled){
        
        getClientComboBox().setEnabled(enabled);
        getDeliveryDateTextField().setEnabled(enabled);
        documentTextField.setEnabled(enabled);
        targetplaceComboBox.setEnabled(enabled);
        orderTypeCombo.setEnabled(enabled);
        orderNumberTextField.setEnabled(enabled);
        labelTextField.setEnabled(enabled);
    }

    private void processTargetplace() {
        targetplaceComboBox.removeAllItems();
        try {
            LOSOrderFacade r = loc.getStateless(LOSOrderFacade.class);
            List<String> items = r.getGoodsOutLocations();
            if (items != null) {
                for (String pos : items) {
                    targetplaceComboBox.addItem(pos);
                }
            }
        } catch (FacadeException ex) {
            ExceptionAnnotator.annotate(ex);
        }
    }

    @Override
    protected void addButtonActionPerformedListener(java.awt.event.ActionEvent evt) {

        if(!getClientComboBox().checkSanity()
           || !getLotComboBox().checkSanity()
           || !getItemDataComboBox().checkSanity()
           || !getAmountTextField().checkSanity())
        {
            return;
        }
        
        String selectedItem = getItemDataComboBox().getSelectedItem().getName();
        if( selectedItem != null ) {
            ItemData item = getItemDataComboBox().getSelectedAsEntity();
            if( item.getLock() != 0 ) {
                selectedItem = "* "+selectedItem;
            }
        }
        String selectedLot = "";
        if(getLotComboBox().getSelectedItem() != null){
            selectedLot = getLotComboBox().getSelectedItem().getName();
            Lot lot = getLotComboBox().getSelectedAsEntity();
            if( lot != null && lot.getLock() != 0 ) {
                selectedLot = "* " + selectedLot;
            }
        }
        
        if(treeTable.contains(selectedItem, selectedLot)){
            
            FacadeException fex = new FacadeException("Duplicate position for item", 
                                                      "ORDER_DUPLICATE_POSITION", 
                                                      new Object[]{selectedItem, selectedLot});
            fex.setBundleResolver(WMSProcessesBundleResolver.class);
            
            ExceptionAnnotator.annotate(fex);
            
            return;
        }
        
        int pos;
        if (treeTable.getExplorerManager().getRootContext() != null){
        Node[] nodes = treeTable.getExplorerManager().getRootContext().getChildren().getNodes();
            if (nodes != null && nodes.length > 0){
                pos = nodes.length + 1;
            } else{
                pos = 1;
            }
        } else{
            pos = 1;
        }
        try {
            treeTable.addRow("" + pos, 
                             selectedLot, 
                             selectedItem,
                             getAmountTextField().getValue().toString());
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }

        setOrderDetailsEnabled(false);
        
        clearPositionDetail();
    }

    @Override
    protected void delButtonActionPerformedListener(java.awt.event.ActionEvent evt) {
        treeTable.delSelectedRows();
    }
    
    public void process(boolean processAutomaticly) {
        CursorControl.showWaitCursor();
        try {
//            OrderFacade orderFacade = (OrderFacade) loc.getStateless(OrderFacade.class);
            LOSOrderFacade orderFacade = loc.getStateless(LOSOrderFacade.class);
            List<OrderItem> items = treeTable.getOrderItems();
            if (items.size() < 1) {
                
                FacadeException fex = new FacadeException("No order position", 
                                                         "ERROR_NO_ORDER_POSITION", 
                                                         new Object[0]);
                
                fex.setBundleResolver(WMSProcessesBundleResolver.class);
                
                ExceptionAnnotator.annotate(fex);
                
                return;
                
            }
            
            OrderPositionTO[] tos = new OrderPositionTO[items.size()];
            int i = 0;
            String clientStr = getClientComboBox().getSelectedItem().getName();
            for (OrderItem item : items) {
                OrderPositionTO to = new OrderPositionTO();
                to.amount = new BigDecimal(item.getAmount());
                String val = item.getArticel();
                if( val != null && val.startsWith("* ") )
                    val = val.substring(2);
                to.articleRef = val;
                val = item.getPrintnorm();
                if( val != null && val.startsWith("* ") )
                    val = val.substring(2);
                to.batchRef = val;
                to.clientRef = clientStr;
                tos[i] = to;
                i++;
            }

//            OrderType orderType = ((OrderTypeItem) orderTypeCombo.getSelectedItem()).t;
//
//            orderFacade.order(clientStr,
//                              orderNumberTextField.getText(),
//                              tos,
//                              documentTextField.getText(),
//                              labelTextField.getText(),
//                              targetplaceComboBox.getSelectedItem().toString(),
//                              orderType,
//                              getDeliveryDateTextField().getDate(),
//                              processAutomaticly,
//                              getCommentArea().getText());

            String orderStrategyName = (String) orderTypeCombo.getSelectedItem();
            orderFacade.order(clientStr,
                              orderNumberTextField.getText(),
                              tos,
                              documentTextField.getText(),
                              labelTextField.getText(),
                              targetplaceComboBox.getSelectedItem().toString(),
                              orderStrategyName,
                              getDeliveryDateTextField().getDate(),
                              50,
                              processAutomaticly, true,
                              getCommentArea().getText());
            clear();
            
        } catch (FacadeException ex) {
            ExceptionAnnotator.annotate(ex);
        } finally{
            CursorControl.showNormalCursor();
        }
    }


    
    /** write default-values and default-selections to the fields */
    private void initDefaults() {
        
        if( getClientComboBox().getSelectedItem() == null ) {
            BODTO<Client> client = loc.getDefaultClient();
            if( client != null ) {
                try {
                    getClientComboBox().clear();
                    getClientComboBox().addItem(client);
                }
                catch( Exception e) {}
            }
        }
        
        try {
            LOSSystemPropertyServiceRemote propertyFacade = loc.getStateless(LOSSystemPropertyServiceRemote.class);
            
//            String locationName = propertyFacade.getString(loc.getWorkstationName(), LOSInventoryPropertyKey.DEFAULT_GOODS_OUT_LOCATION_NAME);
//            if( locationName != null ) {
//                int max = targetplaceComboBox.getItemCount();
//                for( int i = 0; i<max; i++ ) {
//                    String str = (String)targetplaceComboBox.getItemAt(i);
//                    if( locationName.equals(str) ) {
//                        targetplaceComboBox.setSelectedIndex(i);
//                        break;
//                    }
//                }
//            }

            orderTypeCombo.removeAllItems();
            LOSOrderStrategyQueryRemote orderStrategyQuery;
            orderStrategyQuery = loc.getStateless(LOSOrderStrategyQueryRemote.class);
            List<String> strategies = orderStrategyQuery.getNametList();
            for( String strat : strategies ) {
                orderTypeCombo.addItem(strat);
            }

            String typeName = propertyFacade.getString(loc.getWorkstationName(), Wms2Properties.KEY_ORDERSTRATEGY_DEFAULT);
            if( typeName != null ) {
                int max = orderTypeCombo.getItemCount();
                for( int i = 0; i<max; i++ ) {
                    String oti = (String)orderTypeCombo.getItemAt(i);
                    if( typeName.equals(oti) ) {
                        orderTypeCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }

        } catch (Throwable ex) { 
            System.out.println("Exception initDefaults: " + ex.toString());
            ex.printStackTrace();
        }
        
        getDeliveryDateTextField().setDate( new Date() );

    }

    private void onStrategySelect() {
        try {
            String stratName = (String)orderTypeCombo.getSelectedItem();
            if( !StringTools.isEmpty(stratName) ) {
                LOSOrderStrategyQueryRemote orderStratQuery = loc.getStateless(LOSOrderStrategyQueryRemote.class);
                OrderStrategy strat = orderStratQuery.queryByIdentity(stratName);
                if( strat != null ) {
                    String locationName = strat.getDefaultDestination().getName();
                    int max = targetplaceComboBox.getItemCount();
                    for( int i = 0; i<max; i++ ) {
                        String str = (String)targetplaceComboBox.getItemAt(i);
                        if( locationName.equals(str) ) {
                            targetplaceComboBox.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        }
        catch( Throwable t ) {
        }
    }
}
