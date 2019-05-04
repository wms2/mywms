/*
 * Copyright (c) 2012 LinogistiX GmbH
 *
 *  www.linogistix.com
 *
 *  Project myWMS-LOS
 */
package de.linogistix.inventory.browser.dialog;

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
final public class CustomerOrderStartHintPage implements WizardDescriptor.ValidatingPanel, PropertyChangeListener, WizardDescriptor.FinishablePanel {

    private CustomerOrderStartWizard wizard;
    private CustomerOrderStartHintPanel ui;
    private boolean finishPanel = true;
    /** listener to changes in the wizard */
    private ChangeListener listener;
            
    private CustomerOrderStartHintPanel getPanelUI() {
        if (ui == null) {
            ui = new CustomerOrderStartHintPanel();
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

    /** Test whether the panel is finished and it is safe to proceed to the next one.
     * If the panel is valid, the "Next" (or "Finish") button will be enabled.
     * @return <code>true</code> if the user has entered satisfactory information
     */
    public boolean isValid() {
        return true;
    }
      

    public void propertyChange(PropertyChangeEvent evt) {
        this.wizard.stateChanged(null);
    }

    public boolean isFinishPanel() {
        return true;
    }

    public void readSettings(Object settings) {
        this.wizard = (CustomerOrderStartWizard)settings;
        if (this.wizard.hint != null ) getPanelUI().setText(this.wizard.hint);
    }

    public void storeSettings(Object settings) {
        this.wizard = (CustomerOrderStartWizard)settings;
        this.wizard.hint = (getPanelUI().getText());
    }

    public void validate() throws WizardValidationException {
        //
    }

}
