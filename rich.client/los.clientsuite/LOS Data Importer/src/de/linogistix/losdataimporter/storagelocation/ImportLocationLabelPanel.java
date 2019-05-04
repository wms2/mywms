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
package de.linogistix.losdataimporter.storagelocation;

import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.losdataimporter.res.BundleResolver;
import de.linogistix.losdataimporter.storagelocation.component.CreateLabelsPanel;
import de.linogistix.losdataimporter.storagelocation.component.FileChooserPanel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
final public class ImportLocationLabelPanel implements WizardDescriptor.ValidatingPanel, PropertyChangeListener, WizardDescriptor.FinishablePanel {

    private ImportLocationWizard wizard;
    private CreateLabelsPanel ui;
    /** listener to changes in the wizard */
    private ChangeListener listener;

    private CreateLabelsPanel getPanelUI() {
        if (ui == null) {
            ui = new CreateLabelsPanel();

            ui.getFileChooserTextField().getDocument().addDocumentListener(new DocumentListener() {

                public void insertUpdate(DocumentEvent e) {
                    if (wizard.getFile() == null){
                        File f = new File(ui.getFileChooserTextField().getText().trim());
                        wizard.setFile(f);
                        getPanelUI().setFile(f);
                    }
                    wizard.stateChanged(null);
                }

                public void removeUpdate(DocumentEvent e) {
                    if (wizard.getFile() != null){
                       if (ui.getFileChooserTextField().getText() == null 
                             || ui.getFileChooserTextField().getText().length() == 0){
                        wizard.setFile(null);
                       } else{
                         File f = new File(ui.getFileChooserTextField().getText());
                        wizard.setFile(f);
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
        return new HelpCtx("de.linogistix.losdataimporter.storagelocation.wizard");
    }

    /** Test whether the panel is finished and it is safe to proceed to the next one.
     * If the panel is valid, the "Next" (or "Finish") button will be enabled.
     * @return <code>true</code> if the user has entered satisfactory information
     */
    public boolean isValid() {
        try {
            validate();
        } catch (WizardValidationException ex) {
            wizard.putProperty("WizardPanel_errorMessage", ex.getLocalizedMessage());
            return false;
        }
        
        return true;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        this.wizard.stateChanged(null);
    }

    public void readSettings(Object settings) {
        this.wizard = (ImportLocationWizard) settings;
        if (this.wizard.getLabelFile() != null) {
            try {
                getPanelUI().setFile(this.wizard.getFile());
                getPanelUI().fileChooserTextField.setText(this.wizard.getFile().getCanonicalPath());
            } catch (IOException ex) {
                ExceptionAnnotator.annotate(ex);
                getPanelUI().setFile(null);
                getPanelUI().fileChooserTextField.setText("");
            }
            getPanelUI().enableCreation(true);
        } else{
            getPanelUI().enableCreation(false);
        }
    }

    public void storeSettings(Object settings) {
        this.wizard = (ImportLocationWizard) settings;
        this.wizard.setLabelFile(getPanelUI().getFile());
        this.wizard.setOpenLabels(getPanelUI().getOpenCheckBox().isSelected());
    }

    public void validate() throws WizardValidationException {

        if (this.wizard.getFile() == null) {
            throw new WizardValidationException(getPanelUI(), "no file",
                    NbBundle.getMessage(BundleResolver.class, "ImportLocationFilePanel.ERROR.nofile", null));
        }
        if (!this.wizard.getFile().canRead()) {
            throw new WizardValidationException(getPanelUI(), "cannot read file",
                    NbBundle.getMessage(BundleResolver.class, "ImportLocationFilePanel.ERROR.cannotreadfile", null));
        }
    }

    public boolean isFinishPanel() {
        return false;
    }
 
}
