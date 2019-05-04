/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.treat_order.gui.component;

import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.services.J2EEServiceLocatorException;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.inventory.facade.LOSCompatibilityFacade;
import de.linogistix.wmsprocesses.processes.treat_order.TreatOrderStockUnitBO;
import de.linogistix.wmsprocesses.processes.treat_order.gui.control.TreatOrderDialogController;
import de.linogistix.wmsprocesses.processes.treat_order.gui.gui_builder.AbstractTreatOrderCenterPanel;
import de.linogistix.wmsprocesses.processes.treat_order.gui.model.TreatOrderPickRequestListModel;
import de.linogistix.wmsprocesses.processes.treat_order.gui.model.TreatOrderStockSelectionModel;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import javax.swing.SwingUtilities;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Jordan
 */
public class TreatOrderCenterPanel extends AbstractTreatOrderCenterPanel{
    
    private TreatOrderPickRequestListModel pickRequestModel;
    
    TreatOrderDialogController dialogController;
    
    public TreatOrderCenterPanel(){
        
        pickRequestModel = new TreatOrderPickRequestListModel();
        
        getPickRequestListView().setModel(pickRequestModel);
        
        getPickRequestListView().getTextFieldLabel()
                                .setTitleText(NbBundle.getMessage(WMSProcessesBundleResolver.class,"TREAT_ORDER_PICK_REQUESTS"));
        
        getStockChooserView().getChosenListLabel().setTitleText(NbBundle.getMessage(WMSProcessesBundleResolver.class,"TREAT_ORDER_CHOSEN_STOCKS"));
        
        getStockChooserView().getQueryListLabel().setTitleText(NbBundle.getMessage(WMSProcessesBundleResolver.class,"TREAT_ORDER_QUERY_STOCKS"));
        
        getPrefixTextField().getTextFieldLabel().setTitleText(NbBundle.getMessage(WMSProcessesBundleResolver.class,"TREAT_ORDER_PREFIX_LABEL"));
        
        addPickRequestButton.setText(NbBundle.getMessage(WMSProcessesBundleResolver.class,"TREAT_ORDER_ADD_PICKREQUEST"));
        addPickRequestButton.setMnemonic(NbBundle.getMessage(WMSProcessesBundleResolver.class, "TREAT_ORDER_ADD_PICKREQUEST.mnemonic").charAt(0));

        clientLabel.setText(NbBundle.getMessage(WMSProcessesBundleResolver.class,"client")+" : ");
        clientValueLabel.setText("");
        itemDataLabel.setText(NbBundle.getMessage(WMSProcessesBundleResolver.class,"itemData")+" : ");
        itemDataValueLabel.setText("");
        lotLabel.setText(NbBundle.getMessage(WMSProcessesBundleResolver.class, "lot")+" : ");
        lotValueLabel.setText("");
        
        Font bold = requiredAmountLabel.getFont().deriveFont(Font.BOLD);
        requiredAmountLabel.setFont(bold);
        requiredAmountLabel.setText(NbBundle.getMessage(WMSProcessesBundleResolver.class,"TREAT_ORDER_REQUIRED_AMOUNT"));
        requiredAmountValueLabel.setFont(bold);
        requiredAmountValueLabel.setText("");
        
        chosenAmountLabel.setFont(bold);
        chosenAmountLabel.setText(NbBundle.getMessage(WMSProcessesBundleResolver.class,"TREAT_ORDER_CHOSEN_AMOUNT"));
        chosenAmountValueLabel.setFont(bold);
        chosenAmountValueLabel.setText("");

        posAmountLabel.setFont(bold);
        posAmountLabel.setText(NbBundle.getMessage(WMSProcessesBundleResolver.class,"TREAT_ORDER_POS_AMOUNT"));
        posAmountValueLabel.setFont(bold);
        posAmountValueLabel.setText("");

        getCommentLabel().setText(NbBundle.getMessage(WMSProcessesBundleResolver.class,"TREAT_ORDER_COMMENT"));
        
        getCommentArea().setEnabled(false);
        getCommentArea().setBackground(Color.WHITE);
        getCommentArea().setDisabledTextColor(Color.BLACK);
        getCommentArea().setLineWrap(true);
        getCommentArea().setWrapStyleWord(true);
        getCommentArea().setMargin(new Insets(10, 10, 10, 10));

        getPrefixTextField().setColumns(10);
        
        addPickRequestButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    addPickRequestToList();
                } catch (J2EEServiceLocatorException ex) {
                    ExceptionAnnotator.annotate(ex);
                }
                
                // Try set cursor to the first line, if nothing is selected
                Node[] nodes = getPickRequestListView().getExplorerManager().getSelectedNodes();
                if( nodes == null || nodes.length==0 ) {
                    nodes = getPickRequestListView().getExplorerManager().getRootContext().getChildren().getNodes();
                    if( nodes != null && nodes.length>0 ) {
                        try {
                            getPickRequestListView().getExplorerManager().setSelectedNodes(new Node[]{nodes[0]});
                        } catch (PropertyVetoException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
            }
        });
    }
    
    private void initAutofiltering(){
        
        getOrderComboBox().setEnabled(true);
        getOrderComboBox().setMandatory(true);
        getOrderComboBox().setEditorLabelTitle(NbBundle.getMessage(WMSProcessesBundleResolver.class,"TREAT_ORDER_ORDER_REQUEST"));
        
        getOrderPositionComboBox().setEnabled(false);
        getOrderPositionComboBox().setMandatory(true);
        getOrderPositionComboBox().setEditorLabelTitle(NbBundle.getMessage(WMSProcessesBundleResolver.class,"TREAT_ORDER_ORDER_REQUEST_POSITION"));       
        
        getTargetPlaceComboBox().setEnabled(true);
        getTargetPlaceComboBox().setMandatory(false);
        getTargetPlaceComboBox().setEditorLabelTitle(NbBundle.getMessage(WMSProcessesBundleResolver.class,"TREAT_ORDER_TARGET_PLACE"));
    }

    @Override
    public void componentOpened() {
        
        initAutofiltering();
        
        try {
            
            TreatOrderStockUnitBO stockBO = new TreatOrderStockUnitBO();
            
            TreatOrderStockSelectionModel stockModel = new TreatOrderStockSelectionModel();
            
            getStockChooserView().init(stockBO, stockModel);
            
            dialogController = new TreatOrderDialogController(this,
                                           getOrderComboBox(), 
                                           getOrderPositionComboBox(),
                                           stockModel,
                                           getTargetPlaceComboBox());
            
        } catch (Exception ex) {
            ExceptionAnnotator.annotate(ex);
            return;
        }
        
        //invalidate();
    }

    @Override
    public void componentShowing() {
        invalidate();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                validate();
                getStockChooserView().reload();
                getOrderComboBox().requestFocus();
            }
        });
    }
    private void addPickRequestToList() throws J2EEServiceLocatorException{
        
        String prefix = getPrefixTextField().getText();
        
        J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
        
        LOSCompatibilityFacade pickManager = loc.getStateless(LOSCompatibilityFacade.class);
        
        String newPickNumber = pickManager.getNewPickRequestNumber();
        
        if(prefix.length()>0){
            newPickNumber = prefix+"_"+newPickNumber;
        }
        
        pickRequestModel.addNewPickRequest(newPickNumber, getTargetPlaceComboBox().getSelectedItem());
        
    }
    
    public void process(){
        if (dialogController != null ) dialogController.process();
    }
    
    public void reset(){
        if (dialogController != null ) dialogController.reset();
    }
}
