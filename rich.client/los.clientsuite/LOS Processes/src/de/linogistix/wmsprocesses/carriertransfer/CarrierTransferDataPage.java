/*
 * Copyright (c) 2012 LinogistiX GmbH
 *
 *  www.linogistix.com
 *
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.carriertransfer;

import de.linogistix.los.query.BODTO;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import de.wms2.mywms.inventory.UnitLoad;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author krane
 */
final public class CarrierTransferDataPage implements WizardDescriptor.ValidatingPanel, PropertyChangeListener, WizardDescriptor.FinishablePanel {

    private CarrierTransferWizard wizard;
    private CarrierTransferDataPanel ui;
    private boolean finishPanel = true;
    /** listener to changes in the wizard */
    private ChangeListener listener;
            
    private CarrierTransferDataPanel getPanelUI() {
        if (ui == null) {
            ui = new CarrierTransferDataPanel();
            
            ui.getUnitLoadAutoFilteringComboBox().addItemChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (wizard == null) {
                        return;
                    }

                    getPanelUI().setSourceInfoText("");
                    BODTO to = getPanelUI().getUnitLoadAutoFilteringComboBox().getSelectedItem();
                    if (to != null) {
                        wizard.setSource(to);
                        UnitLoad ul = (UnitLoad)getPanelUI().getUnitLoadAutoFilteringComboBox().getSelectedAsEntity();
                        if( ul != null ) {
                            getPanelUI().setSourceInfoText(ul.getStorageLocation().getName());
                        }

                    }
                    
                    wizard.stateChanged(null);
                }
            });

            ui.getDestinationAutofilteringComboBox().addItemChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (wizard == null) {
                        return;
                    }

                    getPanelUI().setDestinationInfoText("");
                    BODTO to = getPanelUI().getDestinationAutofilteringComboBox().getSelectedItem();
                    if (to != null) {
                        wizard.setDestination(to);
                        UnitLoad ul = (UnitLoad)getPanelUI().getDestinationAutofilteringComboBox().getSelectedAsEntity();
                        if( ul != null ) {
                            getPanelUI().setDestinationInfoText(ul.getStorageLocation().getName());
                        }

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
       return new HelpCtx("de.linogistix.wmsprocesses.about");
    }


    /** Provides the wizard panel with the current data--either
     * the default data or already-modified settings, if the user used the previous and/or next buttons.
     * This method can be called multiple times on one instance of <code>WizardDescriptor.Panel</code>.
     * @param settings the object representing wizard panel state, as originally supplied to {@link WizardDescriptor#WizardDescriptor(WizardDescriptor.Iterator,Object)}
     */
    @SuppressWarnings("unchecked")
    public void readSettings(Object settings) {
        this.wizard = (CarrierTransferWizard)settings;

        if (this.wizard.getSource() != null) {
            getPanelUI().getUnitLoadAutoFilteringComboBox().addItem(wizard.getSource());
            getPanelUI().getUnitLoadAutoFilteringComboBox().fireItemChangeEvent();
        }

        if (this.wizard.getDestination() != null) {
            getPanelUI().getDestinationAutofilteringComboBox().addItem(wizard.getDestination());
            getPanelUI().getDestinationAutofilteringComboBox().fireItemChangeEvent();
        }
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
        this.wizard = (CarrierTransferWizard) settings;
        this.wizard.setSource(getPanelUI().getUnitLoadAutoFilteringComboBox().getSelectedItem());
        this.wizard.setDestination( getPanelUI().getDestinationAutofilteringComboBox().getSelectedItem() );
    }

    public boolean isFinishPanel() {
        return finishPanel;
    }

    public void setFinishPanel(boolean finishPanel) {
        this.finishPanel = finishPanel;
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
            validate();
        } catch (WizardValidationException ex) {
            wizard.putProperty("WizardPanel_errorMessage", ex.getLocalizedMessage());
            return false;
        }
        return true;
    }

    public void validate() throws WizardValidationException {
        BODTO<UnitLoad> source =  this.wizard.getSource();
        BODTO<UnitLoad> destination =  this.wizard.getDestination();

        if (source == null) {
            throw new WizardValidationException(getPanelUI(), "no unit load set",
                    NbBundle.getMessage(WMSProcessesBundleResolver.class, "ERROR.noUnitLoad", new Object[]{}));
        }
        if (destination == null) {
            throw new WizardValidationException(getPanelUI(), "no unit load set",
                    NbBundle.getMessage(WMSProcessesBundleResolver.class, "ERROR.noUnitLoad", new Object[]{}));
        }
        if (source.equals(destination) ) {
            throw new WizardValidationException(getPanelUI(), "no unit load set",
                    NbBundle.getMessage(WMSProcessesBundleResolver.class, "ERROR.noUnitLoad", new Object[]{}));
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        this.wizard.stateChanged(null);
    }
}
