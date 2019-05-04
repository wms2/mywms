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
package de.linogistix.wmsprocesses.unitloadtransfer;

import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.location.facade.ManageLocationFacade;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote;
import de.linogistix.los.location.query.dto.StorageLocationTO;
import de.linogistix.los.query.BODTO;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeListener;
import org.mywms.facade.FacadeException;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
final public class UnitLoadTransferDataPage implements WizardDescriptor.ValidatingPanel, PropertyChangeListener, WizardDescriptor.FinishablePanel {

    private UnitLoadTransferWizard wizard;
    private UnitLoadTransferDataPanel ui;
    private boolean finishPanel = true;
    /** listener to changes in the wizard */
    private ChangeListener listener;
            
    private UnitLoadTransferDataPanel getPanelUI() {
        if (ui == null) {
            ui = new UnitLoadTransferDataPanel();
            ui.getSlReserveLabel().setEnabled(false);
            ui.getSlReserveCheckbox().setEnabled(false);
            
            ui.getUnitLoadAutoFilteringComboBox().addItemChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {

                    if (wizard == null) {
                        return;
                    }

                    getPanelUI().setSourceInfoText("");
                    BODTO to = getPanelUI().getUnitLoadAutoFilteringComboBox().getSelectedItem();
                    if (to != null) {
                        wizard.setUnitLoadTO(to);
                        LOSUnitLoad ul = (LOSUnitLoad)getPanelUI().getUnitLoadAutoFilteringComboBox().getSelectedAsEntity();
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
                     
                    try{
                        if (getPanelUI().getDestinationAutofilteringComboBox().getSelectedItem() != null) {
                            J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
                            LOSStorageLocationQueryRemote slQuery = loc.getStateless(LOSStorageLocationQueryRemote.class);
                            BODTO<LOSStorageLocation> to = getPanelUI().getDestinationAutofilteringComboBox().getSelectedItem();
                            LOSStorageLocation sl = slQuery.queryById(to.getId());
                            BODTO slTo = new StorageLocationTO(sl);
                            wizard.setStorageLocationTO(slTo); 
                            if (sl.getLock() > 0 ){
                                ui.getSlLockLabel().setEnabled(true);
                                ui.getSlLockCheckbox().setEnabled(true);
                            }
                            else{
                                ui.getSlLockLabel().setEnabled(false);
                                ui.getSlLockCheckbox().setEnabled(false);
                            }
                        }
                    } catch (Throwable t){
                        ExceptionAnnotator.annotate(t);
                        return;
                    }

                    wizard.stateChanged(null);
                }
            });
            
             ui.getSlLockCheckbox().addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                 if (wizard == null) {
                        return;
                    }
             
                if (e.getStateChange() == ItemEvent.SELECTED || (e.getStateChange() == ItemEvent.DESELECTED))
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

    /** Test whether the panel is finished and it is safe to proceed to the next one.
     * If the panel is valid, the "Next" (or "Finish") button will be enabled.
     * @return <code>true</code> if the user has entered satisfactory information
     */
    public boolean isValid() {

        if (ui == null) {
            return false;
        }

        try {

            J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ManageLocationFacade mangeLoc = loc.getStateless(ManageLocationFacade.class);
            StorageLocationTO sl = (StorageLocationTO) this.wizard.getStorageLocationTO();
            BODTO<LOSUnitLoad> ul =  this.wizard.getUnitLoadTO();
                    
            if (ul == null) {
                throw new WizardValidationException(getPanelUI(), "no unit load set",
                        NbBundle.getMessage(WMSProcessesBundleResolver.class, "ERROR.noUnitLoad", new Object[]{}));
            }

            if (sl  == null) {
                throw new WizardValidationException(getPanelUI(), "no storage location set",
                        NbBundle.getMessage(WMSProcessesBundleResolver.class, "ERROR.noStorageLocation", new Object[]{}));
            }

//            if (sl != null && sl.getReserved() > 0) {
//                throw new WizardValidationException(getPanelUI(), "Set 'Unlock unit load' option or choose other unit load",
//                        NbBundle.getMessage(WMSProcessesBundleResolver.class, "ERROR.noStorageLocation", new Object[]{}));
//            }
//            
            if (sl != null && sl.getLock() > 0 && ( ! ui.getSlLockCheckbox().isSelected()) ){
                    throw new WizardValidationException(getPanelUI(), "Set 'Unlock storage location' option or choose other location",
                            NbBundle.getMessage(WMSProcessesBundleResolver.class, "ERROR.storageLocationLocked", new Object[]{sl.getName()}));
            }

            try {
                mangeLoc.checkUnitLoadSuitable(sl, ul, ui.getSlLockCheckbox().isSelected());
            } catch (FacadeException ex) {
                throw new WizardValidationException(getPanelUI(), ex.getMessage(), ex.getLocalizedMessage());
            }
            
        } catch (WizardValidationException ex) {

            wizard.putProperty("WizardPanel_errorMessage", ex.getLocalizedMessage());
            return false;
        } catch (FacadeException ex){
            ExceptionAnnotator.annotate(ex);
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
        this.wizard = (UnitLoadTransferWizard)settings;

        if (this.wizard.getUnitLoadTO() != null) {
            getPanelUI().getUnitLoadAutoFilteringComboBox().addItem(wizard.getUnitLoadTO());
            getPanelUI().getUnitLoadAutoFilteringComboBox().fireItemChangeEvent();
        }

        if (this.wizard.getStorageLocationTO() != null) {
            getPanelUI().getDestinationAutofilteringComboBox().addItem(wizard.getStorageLocationTO());
            getPanelUI().getDestinationAutofilteringComboBox().fireItemChangeEvent();
        }
        
        getPanelUI().getSlLockCheckbox().setSelected(this.wizard.isIgnoreLock());
        
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
        this.wizard = (UnitLoadTransferWizard) settings;
        this.wizard.setUnitLoadTO(getPanelUI().getUnitLoadAutoFilteringComboBox().getSelectedItem());
        this.wizard.setIgnoreLock( getPanelUI().getSlLockCheckbox().isSelected());
    }

    public boolean isFinishPanel() {
        return finishPanel;
    }

    public void setFinishPanel(boolean finishPanel) {
        this.finishPanel = finishPanel;
    }

    public void validate() throws WizardValidationException {

        try {
                        
            J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ManageLocationFacade mangeLoc = loc.getStateless(ManageLocationFacade.class);
            StorageLocationTO sl = (StorageLocationTO) this.wizard.getStorageLocationTO();
            BODTO<LOSUnitLoad> ul =  this.wizard.getUnitLoadTO();
                    
            if (ul == null) {
                throw new WizardValidationException(getPanelUI(), "no unit load set",
                        NbBundle.getMessage(WMSProcessesBundleResolver.class, "ERROR.noUnitLoad", new Object[]{}));
            }

            if (sl  == null) {
                throw new WizardValidationException(getPanelUI(), "no storage location set",
                        NbBundle.getMessage(WMSProcessesBundleResolver.class, "ERROR.noStorageLocation", new Object[]{}));
            }

//            if (sl != null && sl.getReserved() > 0) {
//                throw new WizardValidationException(getPanelUI(), "Set 'Unlock unit load' option or choose other unit load",
//                        NbBundle.getMessage(WMSProcessesBundleResolver.class, "ERROR.noStorageLocation", new Object[]{}));
//            }
//            
            if (sl != null && sl.getLock() > 0 && ( ! ui.getSlLockCheckbox().isSelected())){
                    throw new WizardValidationException(getPanelUI(), "Set 'Unlock storage location' option or choose other location",
                            NbBundle.getMessage(WMSProcessesBundleResolver.class, "ERROR.storageLocationLocked", new Object[]{sl.getName()}));
            }


            try {
                mangeLoc.checkUnitLoadSuitable(sl, ul, ui.getSlLockCheckbox().isSelected());
            } catch (FacadeException ex) {
                throw new WizardValidationException(getPanelUI(), ex.getMessage(), ex.getLocalizedMessage());
            }
        } catch (FacadeException ex) {
            throw new WizardValidationException(getPanelUI(), ex.getMessage(), ex.getLocalizedMessage());
        }

    }

    public void propertyChange(PropertyChangeEvent evt) {
        this.wizard.stateChanged(null);
    }
}
