/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.stocktaking.process.gui;

import de.linogistix.common.gui.component.controls.BOAutoFilteringComboBox;
import de.linogistix.common.gui.component.controls.LOSDateFormattedTextField;
import de.linogistix.common.gui.component.controls.LOSTextField;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.location.model.LOSArea;
import de.linogistix.los.location.model.LOSRack;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.stocktaking.facade.LOSStocktakingFacade;
import de.linogistix.stocktaking.res.StocktakingBundleResolver;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Zone;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 *
 * @author krane
 */
public class StocktakingCreatePanel extends JPanel {
    private BOAutoFilteringComboBox<LOSArea> areaComboBox;
    private JCheckBox areaEnable;
    private BOAutoFilteringComboBox<Zone> zoneComboBox;
    private JCheckBox zoneEnable;
    private BOAutoFilteringComboBox<Client> clientComboBox;
    private JCheckBox clientEnable;
    private JRadioButton clientModeLocation;
    private JRadioButton clientModeItemData;
    private BOAutoFilteringComboBox<LOSRack> rackComboBox;
    private JCheckBox rackEnable;
    private BOAutoFilteringComboBox<LOSStorageLocation> locationComboBox;
    private JCheckBox locationEnable;
    private LOSTextField locationField;
    private BOAutoFilteringComboBox<ItemData> itemComboBox;
    private JCheckBox itemEnable;
    private LOSTextField itemField;
    private LOSDateFormattedTextField dateField;
    private JCheckBox dateEnable;
    
    private JCheckBox enableEmptyLocations;
    private JCheckBox enableFullLocations;
    protected TopComponent topComponent;
    
   
    public StocktakingCreatePanel( TopComponent topComponent) {
        initComponents();
        this.topComponent = topComponent;
    }
    
    private void initComponents() {
        
        clientComboBox = new BOAutoFilteringComboBox<Client>(Client.class);
        clientComboBox.setEditorLabelTitle(NbBundle.getMessage(StocktakingBundleResolver.class, "LABEL_CLIENT"));
        clientComboBox.setEnabled(false);
        
        clientEnable = new JCheckBox();
        clientEnable.setSelected(false);
        clientEnable.addActionListener( new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                if( clientEnable.isSelected() ) {
                    clientModeItemData.setEnabled(true);
                    clientModeLocation.setEnabled(true);
                    clientComboBox.setEnabled(true);
                    clientComboBox.requestFocus();
                }
                else {
                    clientModeItemData.setEnabled(false);
                    clientModeLocation.setEnabled(false);
                    clientComboBox.setEnabled(false);
                    clientComboBox.clear();
                }
            }
        });

        clientModeItemData = new JRadioButton(NbBundle.getMessage(StocktakingBundleResolver.class, "StocktakingCreatePanel.clientModeItemData"));
        clientModeItemData.setEnabled(false);
        clientModeLocation = new JRadioButton(NbBundle.getMessage(StocktakingBundleResolver.class, "StocktakingCreatePanel.clientModeLocation"));
        clientModeLocation.setEnabled(false);
        clientModeLocation.setSelected(true);
        ButtonGroup clientMode = new ButtonGroup();
        clientMode.add(clientModeItemData);
        clientMode.add(clientModeLocation);

        rackComboBox = new BOAutoFilteringComboBox<LOSRack>(LOSRack.class);
        rackComboBox.setEditorLabelTitle(NbBundle.getMessage(StocktakingBundleResolver.class, "LABEL_RACK"));
        rackComboBox.setEnabled(false);

        rackEnable = new JCheckBox();
        rackEnable.setSelected(false);
        rackEnable.addActionListener( new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                if( rackEnable.isSelected() ) {
                    rackComboBox.setEnabled(true);
                    rackComboBox.requestFocus();
                }
                else {
                    rackComboBox.setEnabled(false);
                    rackComboBox.clear();
                }
            }
        });

        areaComboBox = new BOAutoFilteringComboBox<LOSArea>(LOSArea.class);
        areaComboBox.setEditorLabelTitle(NbBundle.getMessage(StocktakingBundleResolver.class, "LABEL_AREA"));
        areaComboBox.setEnabled(false);

        areaEnable = new JCheckBox();
        areaEnable.setSelected(false);
        areaEnable.addActionListener( new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                if( areaEnable.isSelected() ) {
                    areaComboBox.setEnabled(true);
                    areaComboBox.requestFocus();
                }
                else {
                    areaComboBox.setEnabled(false);
                    areaComboBox.clear();
                }
            }
        });

        zoneComboBox = new BOAutoFilteringComboBox<Zone>(Zone.class);
        zoneComboBox.setEditorLabelTitle(NbBundle.getMessage(StocktakingBundleResolver.class, "LABEL_ZONE"));
        zoneComboBox.setEnabled(false);

        zoneEnable = new JCheckBox();
        zoneEnable.setSelected(false);
        zoneEnable.addActionListener( new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                if( zoneEnable.isSelected() ) {
                    zoneComboBox.setEnabled(true);
                    zoneComboBox.requestFocus();
                }
                else {
                    zoneComboBox.setEnabled(false);
                    zoneComboBox.clear();
                }
            }
        });

        
        locationField = new LOSTextField();
        locationField.setColumns(12);
        locationField.getTextFieldLabel().setTitleText(NbBundle.getMessage(StocktakingBundleResolver.class, "LABEL_LOCATION_PATTERN"));
        locationField.setEnabled(false);

        locationComboBox = new BOAutoFilteringComboBox<LOSStorageLocation>(LOSStorageLocation.class);
        locationComboBox.setEditorLabelTitle(NbBundle.getMessage(StocktakingBundleResolver.class, "LABEL_LOCATION"));
        locationComboBox.setEnabled(false);

        locationEnable = new JCheckBox();
        locationEnable.setSelected(false);
        locationEnable.addActionListener( new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                if( locationEnable.isSelected() ) {
                    locationField.setEnabled(true);
                    locationComboBox.setEnabled(true);
                    locationComboBox.requestFocus();
                }
                else {
                    locationField.setEnabled(false);
                    locationField.setText("");
                    locationComboBox.setEnabled(false);
                    locationComboBox.clear();
                }
            }
        });
        
        
        itemField = new LOSTextField();
        itemField.setColumns(12);
        itemField.getTextFieldLabel().setTitleText(NbBundle.getMessage(StocktakingBundleResolver.class, "LABEL_ITEM_PATTERN"));
        itemField.setEnabled(false);

        itemComboBox = new BOAutoFilteringComboBox<ItemData>(ItemData.class);
        itemComboBox.setEditorLabelTitle(NbBundle.getMessage(StocktakingBundleResolver.class, "LABEL_MATERIAL"));
        itemComboBox.setEnabled(false);
        
        itemEnable = new JCheckBox();
        itemEnable.setSelected(false);
        itemEnable.addActionListener( new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                if( itemEnable.isSelected() ) {
                    itemField.setEnabled(true);
                    itemComboBox.setEnabled(true);
                    itemComboBox.requestFocus();
                    enableEmptyLocations.setSelected(false);
                    enableEmptyLocations.setEnabled(false);
                }
                else {
                    itemField.setEnabled(false);
                    itemField.setText("");
                    itemComboBox.setEnabled(false);
                    itemComboBox.clear();
                    enableEmptyLocations.setEnabled(true);
                }
            }
        });

        
        
        dateField = new LOSDateFormattedTextField();
        dateField.getTextFieldLabel().setTitleText(NbBundle.getMessage(StocktakingBundleResolver.class, "LABEL_LAST_STOCKTAKING_BEFORE"));
        
        dateField.setEnabled(false);
        
        
        dateEnable = new JCheckBox();
        dateEnable.setSelected(false);
        dateEnable.addActionListener( new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                if( dateEnable.isSelected() ) {
                    dateField.setEnabled(true);
                    dateField.requestFocus();
                }
                else {
                    dateField.setEnabled(false);
                    dateField.clear();
                }
            }
        });


        enableEmptyLocations = new JCheckBox();
        enableEmptyLocations.setText(NbBundle.getMessage(StocktakingBundleResolver.class, "LABEL_ORDER_EMPTY_LOCATION"));
        enableEmptyLocations.setSelected(true);

        enableFullLocations = new JCheckBox();
        enableFullLocations.setText(NbBundle.getMessage(StocktakingBundleResolver.class, "LABEL_ORDER_FULL_LOCATION"));
        enableFullLocations.setSelected(true);

        

        JPanel centerPanel1 = new JPanel();
        centerPanel1.setLayout( new GridBagLayout() );

        java.awt.GridBagConstraints gridBagConstraints;
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;

        
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        centerPanel1.add(clientEnable, gridBagConstraints);
        
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        centerPanel1.add(clientComboBox, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        centerPanel1.add(areaEnable, gridBagConstraints);
        
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        centerPanel1.add(areaComboBox, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        centerPanel1.add(zoneEnable, gridBagConstraints);
        
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        centerPanel1.add(zoneComboBox, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        centerPanel1.add(rackEnable, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        centerPanel1.add(rackComboBox, gridBagConstraints);
        
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        centerPanel1.add(locationEnable, gridBagConstraints);
        
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        centerPanel1.add(locationComboBox, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        centerPanel1.add(itemEnable, gridBagConstraints);
        
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        centerPanel1.add(itemComboBox, gridBagConstraints);
        
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        centerPanel1.add(dateEnable, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        centerPanel1.add(dateField, gridBagConstraints);

        
        gridBagConstraints.insets = new java.awt.Insets(10, 30, 0, 0);
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        centerPanel1.add(locationField, gridBagConstraints);

        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        centerPanel1.add(itemField, gridBagConstraints);
        
        JPanel pSwitch = new JPanel();
        pSwitch.setLayout(new GridBagLayout());

        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        pSwitch.add(clientModeLocation, gridBagConstraints);

        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        pSwitch.add(clientModeItemData, gridBagConstraints);

        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        pSwitch.add(enableEmptyLocations, gridBagConstraints);

        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        pSwitch.add(enableFullLocations, gridBagConstraints);

        gridBagConstraints.insets = new java.awt.Insets(10, 80, 0, 0);
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        centerPanel1.add(pSwitch, gridBagConstraints);

        centerPanel1.setFocusable(false);

        
        JPanel headerPanel1 = new JPanel();
        JLabel headerLabel = new javax.swing.JLabel();
        headerPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));
        headerLabel.setFont(new java.awt.Font("Tahoma", 1, 18));
        headerLabel.setText(NbBundle.getMessage(StocktakingBundleResolver.class,"Generate_Orders"));
        headerPanel1.add(headerLabel);

        setLayout(new BorderLayout());
        add(headerPanel1, BorderLayout.NORTH);
        add(centerPanel1, BorderLayout.WEST);
        

    }

    protected void processCreate() {
  
        // Check fields
        Long clientId = null;
        Long areaId = null;
        Long zoneId = null;
        Long rackId = null;
        Long locationId = null;
        Long itemId = null;
        Date invDate = null;
        String locationName = null;
        String itemNo = null;

        if( clientEnable.isSelected() ) {
            BODTO<Client> clientTo = clientComboBox.getSelectedItem();
            if( clientTo == null ) {
                String msg = NbBundle.getMessage(StocktakingBundleResolver.class, "MSG_SELECT_CLIENT");
                NotifyDescriptor nd = new NotifyDescriptor.Message(msg, NotifyDescriptor.PLAIN_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
                clientComboBox.requestFocus();
                return;
            }
            clientId = clientTo.getId();
            
        }
        if( areaEnable.isSelected() ) {
            BODTO<LOSArea> areaTo = areaComboBox.getSelectedItem();
            if( areaTo == null ) {
                String msg = NbBundle.getMessage(StocktakingBundleResolver.class, "MSG_SELECT_AREA");
                NotifyDescriptor nd = new NotifyDescriptor.Message(msg, NotifyDescriptor.PLAIN_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
                areaComboBox.requestFocus();
                return;
            }
            areaId = areaTo.getId();
        }
        if( zoneEnable.isSelected() ) {
            BODTO<Zone> zoneTo = zoneComboBox.getSelectedItem();
            if( zoneTo == null ) {
                String msg = NbBundle.getMessage(StocktakingBundleResolver.class, "MSG_SELECT_ZONE");
                NotifyDescriptor nd = new NotifyDescriptor.Message(msg, NotifyDescriptor.PLAIN_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
                zoneComboBox.requestFocus();
                return;
            }
            zoneId = zoneTo.getId();
        }
        if( rackEnable.isSelected() ) {
            BODTO<LOSRack> rackTo = rackComboBox.getSelectedItem();
            if( rackTo == null ) {
                String msg = NbBundle.getMessage(StocktakingBundleResolver.class, "MSG_SELECT_RACK");
                NotifyDescriptor nd = new NotifyDescriptor.Message(msg, NotifyDescriptor.PLAIN_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
                rackComboBox.requestFocus();
                return;
            }
            rackId = rackTo.getId();
        }
        if( locationEnable.isSelected() ) {
            BODTO<LOSStorageLocation> locationTo = locationComboBox.getSelectedItem();
            locationName = locationField.getText();
            if( locationTo == null && ( locationName == null || locationName.length()==0 )) {
                String msg = NbBundle.getMessage(StocktakingBundleResolver.class, "MSG_SELECT_LOCATION");
                NotifyDescriptor nd = new NotifyDescriptor.Message(msg, NotifyDescriptor.PLAIN_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
                locationComboBox.requestFocus();
                return;
            }
            if( locationTo != null )  {
                locationId = locationTo.getId();
            }
        }
        if( itemEnable.isSelected() ) {
            BODTO<ItemData> itemTo = itemComboBox.getSelectedItem();
            itemNo = itemField.getText();
            if( itemTo == null && ( itemNo == null || itemNo.length()==0 )) {
                String msg = NbBundle.getMessage(StocktakingBundleResolver.class, "MSG_SELECT_ITEM_DATA");
                NotifyDescriptor nd = new NotifyDescriptor.Message(msg, NotifyDescriptor.PLAIN_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
                itemComboBox.requestFocus();
                return;
            }
            if( itemTo != null ) {
                itemId = itemTo.getId();
            }
        }
        if( dateEnable.isSelected() ) {
            invDate = dateField.getDate();
        }

        try {
            CursorControl.showWaitCursor();
            
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            
            loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
        
            LOSStocktakingFacade stFacade;
            
            stFacade = loc.getStateless(LOSStocktakingFacade.class);
            int numOrders = 0;
            numOrders = stFacade.generateOrders(false, clientId, areaId, zoneId, rackId, locationId, locationName, itemId, itemNo, invDate, enableEmptyLocations.isSelected(), enableFullLocations.isSelected(), clientModeLocation.isSelected(), clientModeItemData.isSelected());
  
            String msg;
            if( numOrders == 0 ) {
                msg = NbBundle.getMessage(StocktakingBundleResolver.class, "MSG_NO_ORDER_TO_CREATE");
                NotifyDescriptor nd = new NotifyDescriptor.Message(msg, NotifyDescriptor.PLAIN_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
                return;
            }
            
            msg = NbBundle.getMessage(StocktakingBundleResolver.class, "QST_CREATE_ORDERS", String.valueOf(numOrders) );
            NotifyDescriptor d = new NotifyDescriptor.Confirmation(msg, NotifyDescriptor.OK_CANCEL_OPTION);
            DialogDisplayer.getDefault().notify(d);
            if ( !d.getValue().equals(NotifyDescriptor.OK_OPTION) ){
                return;
            }
            

            numOrders = stFacade.generateOrders(true, clientId, areaId, zoneId, rackId, locationId, locationName, itemId, itemNo, invDate, enableEmptyLocations.isSelected(), enableFullLocations.isSelected(), clientModeLocation.isSelected(), clientModeItemData.isSelected() );
            
            msg = NbBundle.getMessage(StocktakingBundleResolver.class, "MSG_NUM_ORDER_CREATED", String.valueOf(numOrders) );
            NotifyDescriptor nd = new NotifyDescriptor.Message(msg, NotifyDescriptor.PLAIN_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);

        } catch (Throwable ex) {
            ExceptionAnnotator.annotate(ex);
        } finally {
            CursorControl.showNormalCursor();
        }

    }
}
