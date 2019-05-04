/*
 * Copyright (c) 2012 LinogistiX GmbH
 *
 *  www.linogistix.com
 *
 *  Project myWMS-LOS
 */
package de.linogistix.location.dialog;

import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.location.model.LOSRack;
import de.linogistix.los.location.query.RackQueryRemote;
import de.linogistix.los.location.query.dto.LOSRackTO;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;

/**
 *
 * @author krane
 */
final public class LocationOrderDataPage implements WizardDescriptor.ValidatingPanel, PropertyChangeListener, WizardDescriptor.FinishablePanel {

    private LocationOrderWizard wizard;
    private LocationOrderDataPanel ui;
    private boolean finishPanel = true;
    /** listener to changes in the wizard */
    private ChangeListener listener;
            
    private LocationOrderDataPanel getPanelUI() {
        if (ui == null) {
            ui = new LocationOrderDataPanel();
            
            ui.getRackComboBox().addItemChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (wizard == null) {
                        return;
                    }
                    ui.setIndexMin(null);
                    ui.setIndexMax(null);
                    ui.setNumLocation(null);

                    try {
                        BODTO to = getPanelUI().getRackComboBox().getSelectedItem();
                        RackQueryRemote queryRemote = (RackQueryRemote)getPanelUI().getRackComboBox().getQueryRemote();
                        LOSResultList<BODTO<LOSRack>> res = queryRemote.autoCompletion(to.getName());
                        if( res.size()==1 ) {
                            LOSRackTO rackTo = (LOSRackTO)res.get(0);
                            ui.setIndexMin(rackTo.getLocationIndexMin());
                            ui.setIndexMax(rackTo.getLocationIndexMax());
                            ui.setNumLocation(rackTo.getNumLocation());
                            wizard.valueStart = rackTo.getLocationIndexMin();
                        }
                    }
                    catch( Throwable t ) {
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
        wizard = (LocationOrderWizard)settings;

        if (wizard.rack != null) {
            getPanelUI().getRackComboBox().addItem(wizard.rack);
            getPanelUI().getRackComboBox().fireItemChangeEvent();
        }

        getPanelUI().setValueStart(wizard.valueStart);
        getPanelUI().setValueDiff(wizard.valueDiff);
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
        wizard = (LocationOrderWizard) settings;
        wizard.rack = getPanelUI().getRackComboBox().getSelectedItem();
        wizard.valueStart = getPanelUI().getValueStart();
        wizard.valueDiff = getPanelUI().getValueDiff();
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
    }

    public void propertyChange(PropertyChangeEvent evt) {
        this.wizard.stateChanged(null);
    }
}
