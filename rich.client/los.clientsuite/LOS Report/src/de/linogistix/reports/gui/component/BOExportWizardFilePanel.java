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
package de.linogistix.reports.gui.component;

import java.io.File;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.WizardDescriptor.FinishablePanel;
import org.openide.util.HelpCtx;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
final public class BOExportWizardFilePanel implements FinishablePanel {

    private BOExportWizard wizard;
    private BOExportWizardFileJPanel ui;
    private boolean finishPanel = true;
    /** listener to changes in the wizard */
    private ChangeListener listener;

    String fileEnding;
    String description;
    String defaultFileName;


    public void setParams(String fileEnding, String description, String defaultFileName) {
        this.fileEnding = fileEnding;
        this.description = description;
        this.defaultFileName = defaultFileName;
    }

    private BOExportWizardFileJPanel getPanelUI() {
        
        if (ui == null) {
            ui = new BOExportWizardFileJPanel(fileEnding, description, defaultFileName);
            
            ui.getFileNameTextField().getDocument().addDocumentListener(new DocumentListener() {

                public void insertUpdate(DocumentEvent e) {
                    if (wizard.getFileName() == null){
                        File f = new File(ui.getFileNameTextField().getText().trim());
                        wizard.setFileName(ui.getFileNameTextField().getText().trim());
                    }
                    wizard.stateChanged(null);
                }

                public void removeUpdate(DocumentEvent e) {
                    if (wizard.getFileName() != null){
                       if (ui.getFileNameTextField().getText() == null 
                             || ui.getFileNameTextField().getText().length() == 0){
                        wizard.setFileName(null);
                       } else{
                         File f = new File(ui.getFileNameTextField().getText().trim());
                        wizard.setFileName(ui.getFileNameTextField().getText().trim());
                       }
                    }
                    wizard.stateChanged(null);
                }

                public void changedUpdate(DocumentEvent e) {
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
        return true;
    }

    /** Provides the wizard panel with the current data--either
     * the default data or already-modified settings, if the user used the previous and/or next buttons.
     * This method can be called multiple times on one instance of <code>WizardDescriptor.Panel</code>.
     * @param settings the object representing wizard panel state, as originally supplied to {@link WizardDescriptor#WizardDescriptor(WizardDescriptor.Iterator,Object)}
     */
    public void readSettings(Object settings) {
        this.wizard = (BOExportWizard) settings;
        
        if (ui == null) return;
            
        ui.fileNameTextfield.setText(wizard.getFileName()==null?"":wizard.getFileName());
        
        ui.openCheckBox.setState(true);
       
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
        this.wizard = (BOExportWizard) settings;
        this.wizard.setFileName(ui.getSelectedFileName());
        this.wizard.setOpen(ui.openCheckBox.getState());
  
    }

    public boolean isFinishPanel() {
        return finishPanel;
    }

    public void setFinishPanel(boolean finishPanel) {
        this.finishPanel = finishPanel;
    }

}
