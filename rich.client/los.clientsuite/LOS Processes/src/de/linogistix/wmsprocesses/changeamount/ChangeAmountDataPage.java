/*
 * OrderByWizardPanel1.java
 *
 * Created on 27. Juli 2006, 00:46
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.wmsprocesses.changeamount;

import de.linogistix.los.query.BODTO;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
final public class ChangeAmountDataPage implements WizardDescriptor.ValidatingPanel, PropertyChangeListener,WizardDescriptor.FinishablePanel  {

    private ChangeAmountWizard wizard;
    private ChangeAmountDataPanel ui;
    private boolean finishPanel = true;
    /** listener to changes in the wizard */
    private ChangeListener listener;

    @SuppressWarnings("unchecked")
    private ChangeAmountDataPanel getPanelUI() {
        if (ui == null) {
            ui = new ChangeAmountDataPanel();
         
            ui.getStockUnitAutoFilteringComboBox().addItemChangeListener(new PropertyChangeListener() {

                @SuppressWarnings("unchecked")
                public void propertyChange(PropertyChangeEvent evt) {

                    if (wizard == null ){
                        return;
                    }

                    BODTO to = getPanelUI().getStockUnitAutoFilteringComboBox().getSelectedItem();
                    if (to != null) {
                        wizard.setSu(to);
                        StockUnit su = ((StockUnit) getPanelUI().getStockUnitAutoFilteringComboBox().getSelectedAsEntity());
                        if( su != null ) {
                            wizard.setStockUnit(su);
                            wizard.setAmount(su.getAmount());
                            wizard.setReserveAmount(su.getReservedAmount());
                        }
                    }

                    getPanelUI().getItemDataField().setText("");
                    getPanelUI().getAmountField().setText("");
                    getPanelUI().getUnitLoadField().setText("");
                    getPanelUI().getLocationField().setText("");
                    getPanelUI().setReserveAmount(null);
                    getPanelUI().setAmount(null);

                    if (wizard.getStockUnit() != null ) {
                        String s = NbBundle.getMessage(WMSProcessesBundleResolver.class, "is");
                        s = s + ": " + wizard.getStockUnit().getAmount();
//                        getPanelUI().getAmountLabel().setText(s, IconType.INFORMATION);
                        getPanelUI().setAmount(wizard.getStockUnit().getAmount());
                        s = NbBundle.getMessage(WMSProcessesBundleResolver.class, "is");
                        s = s + ": " + wizard.getStockUnit().getReservedAmount();
//                        getPanelUI().getReservedAmountLabel().setText(s, IconType.INFORMATION);
                        getPanelUI().setReserveAmount(wizard.getStockUnit().getReservedAmount());

                        ItemData idat = wizard.getStockUnit().getItemData();
                        if( idat != null ) {
                            getPanelUI().getItemDataField().setText( idat.getNumber() + " ("+idat.getName()+")");
                            String amount = wizard.getStockUnit().getAmount()+" ("+wizard.getStockUnit().getReservedAmount()+") "+idat.getItemUnit().getName();
                            getPanelUI().getAmountField().setText(amount);
                        }
                        UnitLoad sul = wizard.getStockUnit() .getUnitLoad();
                        if( sul != null ) {
                            getPanelUI().getUnitLoadField().setText(sul.getLabelId());
                            StorageLocation loc = sul.getStorageLocation();
                            if( loc != null )
                                getPanelUI().getLocationField().setText(loc.getName());
                        }

                    } else{
                       
                    }
                    
                    wizard.stateChanged(null);
                }
            });
        }
        return ui;
    }

    /** Add a listener to changes of the panel's validity.
     * @param l the listener to add
     * @see #isValid
     */
    public void addChangeListener(ChangeListener l) {
        if (listener != null) {
            throw new IllegalStateException();
        }
        listener = l;
    }

    /** Remove a listener to changes of the panel's validity.
     * @param l the listener to remove
     */
    public void removeChangeListener(ChangeListener l) {
        listener = null;

    }

    /** Get the component displayed in this panel.
     *
     * Note; method can be called from any thread, but not concurrently
     * with other methods of this interface.
     *
     * @return the UI component of this wizard panel
     *
     */
    public java.awt.Component getComponent() {
        return getPanelUI();
    }

    /** Help for this panel.
     * @return the help or <code>null</code> if no help is supplied
     */
    public HelpCtx getHelp() {
        return null;
    }

    /** Test whether the panel is finished and it is safe to proceed to the next one.
     * If the panel is valid, the "Next" (or "Finish") button will be enabled.
     * @return <code>true</code> if the user has entered satisfactory information
     */
    public boolean isValid() {

        if (ui == null) {
            return false;
        }

        try {
            if (this.wizard.getSu() == null) {
                throw new WizardValidationException(getPanelUI(), "no stock unit set",
                        NbBundle.getMessage(WMSProcessesBundleResolver.class, "ERROR.noStockUnit", new Object[]{}));
            }

        } catch (WizardValidationException ex) {

            wizard.putProperty("WizardPanel_errorMessage", ex.getLocalizedMessage());
            return false;
        }
        return true;
    }

    /** Provides the wizard panel with the current data--either
     * the default data or already-modified settings, if the user used the previous and/or next buttons.
     * This method can be called multiple times on one instance of <code>WizardDescriptor.Panel</code>.
     * @param settings the object representing wizard panel state, as originally supplied to {@link WizardDescriptor#WizardDescriptor(WizardDescriptor.Iterator,Object)}
     */
    @SuppressWarnings("unchecked")
    public void readSettings(Object settings) {
        this.wizard = (ChangeAmountWizard) settings;
        if (this.wizard.getSu() != null) {
            getPanelUI().getStockUnitAutoFilteringComboBox().addItem(wizard.getSu());
            getPanelUI().getStockUnitAutoFilteringComboBox().fireItemChangeEvent();
        }
        
        getPanelUI().setAmount(wizard.getAmount());
        getPanelUI().setReserveAmount(wizard.getReserveAmount());
        
    }

    /** Provides the wizard panel with the opportunity to update the
     * settings with its current customized state.
     * Rather than updating its settings with every change in the GUI, it should collect them,
     * and then only save them when requested to by this method.
     * Also, the original settings passed to {@link #readSettings} should not be modified (mutated);
     * rather, the (copy) passed in here should be mutated according to the collected changes.
     * This method can be called multiple times on one instance of <code>WizardDescriptor.Panel</code>.
     * @param settings the object representing a settings of the wizard
     */
    public void storeSettings(Object settings) {
        this.wizard = (ChangeAmountWizard) settings;

        this.wizard.setAmount(getPanelUI().getAmount()==null?new BigDecimal(0):getPanelUI().getAmount());
        this.wizard.setReserveAmount(getPanelUI().getReservedAmount()==null?new BigDecimal(0):getPanelUI().getReservedAmount());
        
    }

    public boolean isFinishPanel() {
        return finishPanel;
    }

    public void setFinishPanel(boolean finishPanel) {
        this.finishPanel = finishPanel;
    }

    public void validate() throws WizardValidationException {

        if (this.wizard.getSu() == null) {
            throw new WizardValidationException(getPanelUI(), "no stock unit set",
                    NbBundle.getMessage(WMSProcessesBundleResolver.class, "ERROR.noStockUnit", new Object[]{}));
        }
        
        if (this.wizard.getAmount().compareTo(this.wizard.getReserveAmount()) < 0 && !this.wizard.isReleaseReservation() ){
            throw new WizardValidationException(getPanelUI(), "amount to reserve cannot be greater than amount",
              NbBundle.getMessage(WMSProcessesBundleResolver.class, "ERROR.reserveTooMany", new Object[]{}));
        }

    }

    public void propertyChange(PropertyChangeEvent evt) {
        this.wizard.stateChanged(null);
    }
}
