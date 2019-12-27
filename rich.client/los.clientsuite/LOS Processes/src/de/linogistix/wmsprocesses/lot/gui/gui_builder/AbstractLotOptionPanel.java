/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.lot.gui.gui_builder;

import de.linogistix.common.gui.component.controls.BOAutoFilteringComboBox;
import de.linogistix.common.gui.component.controls.LOSDateFormattedTextField;
import de.linogistix.common.gui.component.controls.LOSTextField;
import de.wms2.mywms.inventory.Lot;
import javax.swing.JCheckBox;
import org.openide.util.NbBundle;

/**
 *
 * @author  Jordan
 */
public class AbstractLotOptionPanel extends javax.swing.JPanel {

    private javax.swing.ButtonGroup buttonGroup;
    protected javax.swing.JRadioButton chooseLotButton;
    private javax.swing.JPanel chooseLotIndentionPanel;
    private javax.swing.JPanel chooseLotPanel;
    protected javax.swing.JRadioButton createLotButton;
    private javax.swing.JPanel lotCreationIndentionPanel;
    private javax.swing.JPanel lotCreationPanel;
    private LOSTextField lotNumberTextField;
    private javax.swing.JPanel validFromTextField;
    private javax.swing.JPanel validToTextField;
    
    private BOAutoFilteringComboBox<Lot> lotComboBox = null;
    
    private boolean alignHorizontal;
    
    private boolean mandatory = false;
    
    /** Creates new form AbstractLotOptionPanel */
    public AbstractLotOptionPanel(boolean alignHorizontal) {
        
        this.alignHorizontal = alignHorizontal;
        
        initMyComponents();
        
    }

    @SuppressWarnings("unchecked")
    private void initMyComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup = new javax.swing.ButtonGroup();
        chooseLotButton = new javax.swing.JRadioButton();
        chooseLotIndentionPanel = new javax.swing.JPanel();
        chooseLotPanel = new javax.swing.JPanel();
        createLotButton = new javax.swing.JRadioButton();
        lotCreationIndentionPanel = new javax.swing.JPanel();
        lotCreationPanel = new javax.swing.JPanel();
        lotNumberTextField = new LOSTextField();
        validFromTextField = new LOSDateFormattedTextField();
        validToTextField = new LOSDateFormattedTextField();

        setBorder(javax.swing.BorderFactory.createTitledBorder(" "+NbBundle.getMessage(de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver.class, "lot")+" "));
        setLayout(new java.awt.GridBagLayout());

        buttonGroup.add(chooseLotButton);
        chooseLotButton.setText(org.openide.util.NbBundle.getMessage(de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver.class, "CHOOSE_LOT_OPTION"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(chooseLotButton, gridBagConstraints);
        chooseLotButton.getAccessibleContext().setAccessibleName(null);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(chooseLotIndentionPanel, gridBagConstraints);

        chooseLotPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        add(chooseLotPanel, gridBagConstraints);

        buttonGroup.add(createLotButton);
        createLotButton.setText(org.openide.util.NbBundle.getMessage(de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver.class, "CREATE_LOT_OPTION"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        
        if(alignHorizontal){
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 0;
        }else{
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
        }
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(createLotButton, gridBagConstraints);
        createLotButton.getAccessibleContext().setAccessibleName(null);

        gridBagConstraints = new java.awt.GridBagConstraints();
        if(alignHorizontal){
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 1;
        }else{
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 3;
        }
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(lotCreationIndentionPanel, gridBagConstraints);

        lotNumberTextField.setColumns(12);
        lotCreationPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        lotCreationPanel.add(lotNumberTextField, gridBagConstraints);
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        lotCreationPanel.add(validFromTextField, gridBagConstraints);
        validFromTextField.getAccessibleContext().setAccessibleParent(null);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        lotCreationPanel.add(validToTextField, gridBagConstraints);
        validToTextField.getAccessibleContext().setAccessibleParent(null);

        gridBagConstraints = new java.awt.GridBagConstraints();
        if(alignHorizontal){
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = 1;
        }else{
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 3;
        }
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(lotCreationPanel, gridBagConstraints);

        getAccessibleContext().setAccessibleName(null);
    }
    
    public BOAutoFilteringComboBox<Lot> getLotComboBox() {
        if(lotComboBox == null){
            lotComboBox = new BOAutoFilteringComboBox<Lot>(Lot.class);
            
            java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
            chooseLotPanel.add(lotComboBox, gridBagConstraints);
        }
        
        return lotComboBox;
    }
    
    public LOSTextField getLotNumberTextField(){
        return (LOSTextField) lotNumberTextField;
    }
    
    public LOSDateFormattedTextField getValidFromTextField(){
        return (LOSDateFormattedTextField) validFromTextField;
    }
    
    public LOSDateFormattedTextField getValidToTextField(){
        return (LOSDateFormattedTextField) validToTextField;
    }
    
    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }
}
