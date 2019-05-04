/*
 * OrderByWizardPanel1.java
 *
 * Created on 27. Juli 2006, 00:46
 *
 * Copyright (c) 2006-2012 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.wmsprocesses.processes.goodsreceipt.gui.component;

import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
final public class PositionWizardSUPanel implements WizardDescriptor.ValidatingPanel, PropertyChangeListener {

    private PositionWizard wizard;
    private PositionWizardSUPanelUI ui;
    /** listener to changes in the wizard */
    private ChangeListener listener;

    private PositionWizardSUPanelUI getPanelUI() {
        if (ui == null) {
            ui = new PositionWizardSUPanelUI(this);
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

        if (getPanelUI() == null) {
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

    /** Provides the wizard panel with the current data--either
     * the default data or already-modified settings, if the user used the previous and/or next buttons.
     * This method can be called multiple times on one instance of <code>WizardDescriptor.Panel</code>.
     * @param settings the object representing wizard panel state, as originally supplied to {@link WizardDescriptor#WizardDescriptor(WizardDescriptor.Iterator,Object)}
     */
    @SuppressWarnings("unchecked")
    public void readSettings(Object settings) {
        this.wizard = (PositionWizard) settings;
        getPanelUI().setGoodsREceipt(this.wizard.model.gr);
        getPanelUI().initValues(wizard.model);
        
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
        try {
            this.wizard = (PositionWizard) settings;
            getPanelUI().setGoodsREceipt(this.wizard.model.gr);
//            wizard.model.item = getPanelUI().getItemDataComboBox().getSelectedItem();
            wizard.model.amount = getPanelUI().getAmountTextField().getValue();
//            wizard.model.advice = getPanelUI().getAdviceComboBox().getSelectedItem();
            wizard.model.sameCount = getPanelUI().getUnitLoadAmountTextField().getValue().intValue();
        } catch (ParseException ex) {
            ExceptionAnnotator.annotate(ex);
        }
    }

    public void validate() throws WizardValidationException {

//        if (!getPanelUI().validateItemData()) {
//            throw new WizardValidationException(getPanelUI(), "no valid item data", NbBundle.getMessage(WMSProcessesBundleResolver.class, "WizardValidationException.NO_VALID_ITEMDATA"));
//        }

        if(!getPanelUI().validateAmount()){
            throw new WizardValidationException(getPanelUI(), 
                                                "no valid amount", 
                                                NbBundle.getMessage(WMSProcessesBundleResolver.class, "WizardValidationException.NO_VALID_AMOUNT"));
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        this.wizard.stateChanged(null);
    }
}
