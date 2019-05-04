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

import de.linogistix.common.util.BusinessExceptionResolver;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.util.businessservice.ImportDataException;
import de.linogistix.losdataimporter.res.BundleResolver;
import de.linogistix.losdataimporter.storagelocation.component.FileChooserPanel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.mywms.facade.FacadeException;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
final public class ImportLocationFilePanel implements WizardDescriptor.ValidatingPanel, PropertyChangeListener, WizardDescriptor.FinishablePanel {

    private ImportLocationWizard wizard;
    private FileChooserPanel ui;
    /** listener to changes in the wizard */
    private ChangeListener listener;

    private FileChooserPanel getPanelUI() {
        if (ui == null) {
            ui = new FileChooserPanel();

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
                         File f = new File(ui.getFileChooserTextField().getText().trim());
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
        if (this.wizard.getFile() != null) {
            try {
                getPanelUI().setFile(this.wizard.getFile());
                getPanelUI().fileChooserTextField.setText(this.wizard.getFile().getCanonicalPath());
            } catch (IOException ex) {
                ExceptionAnnotator.annotate(ex);
                getPanelUI().setFile(null);
                getPanelUI().fileChooserTextField.setText("");
            }
        }
    }

    public void storeSettings(Object settings) {
        this.wizard = (ImportLocationWizard) settings;
        this.wizard.setFile(getPanelUI().getFile());
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
        if (!checkFormat(this.wizard.getFile())) {
            throw new WizardValidationException(getPanelUI(), "wrong XML format",
                    NbBundle.getMessage(BundleResolver.class, "ImportLocationFilePanel.ERROR.wrongXMLFormat", null));
        }
        try {
            this.wizard.setNumberOfEntries(getNumberOfEntries(this.wizard.getFile()));
        } catch (XMLStreamException ex) {
            throw new WizardValidationException(getPanelUI(), "wrong XML format",
                    NbBundle.getMessage(BundleResolver.class, "ImportLocationFilePanel.ERROR.wrongXMLFormat", null));
        } catch (FacadeException ex) {
            throw new WizardValidationException(getPanelUI(), ex.getMessage(),
                    BusinessExceptionResolver.resolve(ex, ex.getBundleResolver()));
        } catch (IOException ex) {
            throw new WizardValidationException(getPanelUI(), "cannot read file",
                    NbBundle.getMessage(BundleResolver.class, "ImportLocationFilePanel.ERROR.cannotreadfile", null));
        } catch (ImportDataException ex){
            throw new WizardValidationException(getPanelUI(), "cannot import file",
                    NbBundle.getMessage(BundleResolver.class, "ImportLocationFilePanel.ERROR.cannotimportfile", null));
        }
    }

    private boolean checkFormat(File file) {
        return true;
    }

    private int getNumberOfEntries(File file) throws ImportDataException, FacadeException, XMLStreamException, FileNotFoundException, IOException {

        int noe = 0;

        try {

            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fileInputStream.read(data);
            fileInputStream.close();

            ByteArrayInputStream bin = new ByteArrayInputStream(data);

            XMLInputFactory factory = XMLInputFactory.newInstance();

            XMLStreamReader reader = factory.createXMLStreamReader(bin);

            int event = 0;

            // skip Excel header

            while (reader.hasNext()) {
                event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    String element = reader.getLocalName();

                    if (element.equalsIgnoreCase("Row")) {
                        break;
                    }
                }
            }

            // first row defines the keys
            ArrayList<String> keyList = new ArrayList<String>();
            // iterate Cell-Elements
            for (int ce = reader.nextTag();
                    ce == XMLStreamConstants.START_ELEMENT;
                    ce = reader.nextTag()) {
                reader.require(XMLStreamConstants.START_ELEMENT, null, "Cell");

                int de = reader.nextTag();
                if (de == XMLStreamConstants.START_ELEMENT && reader.getLocalName().equalsIgnoreCase("Data")) {

                    // set cursor on text element
                    reader.next();

                    String key = reader.getText();
                    key = key.toLowerCase();
                    key = key.trim();

                    System.out.println("--- Read key > " + key);

                    keyList.add(key);

                    // set cursor on End-Tag of Data-Element
                    reader.nextTag();

                    // set cursor on EndTag of Cell-Element
                    reader.nextTag();
                }
            }

            // iterate Row-Elements
            for (int e = reader.nextTag();
                    e == XMLStreamConstants.START_ELEMENT;
                    e = reader.nextTag()) {
                reader.require(XMLStreamConstants.START_ELEMENT, null, "Row");

                // iterate Cell-Elements
                int x = 0;
                HashMap<String, String> attrMap = new HashMap<String, String>(keyList.size());

                for (int ce = reader.nextTag();
                        ce == XMLStreamConstants.START_ELEMENT;
                        ce = reader.nextTag()) {
                    reader.require(XMLStreamConstants.START_ELEMENT, null, "Cell");
                    if (reader.getAttributeCount() > 0 && reader.getAttributeLocalName(0).equalsIgnoreCase("index")) {

                        String index = reader.getAttributeValue(0);
                        x = Integer.parseInt(index) - 1;

                        System.out.println("--- Skipping empty attribute : setting index to " + index + " -1");

                    }

                    int de = reader.nextTag();
                    if (de == XMLStreamConstants.START_ELEMENT && reader.getLocalName().equalsIgnoreCase("Data")) {

                        // set cursor on text element
                        reader.next();

                        String value = reader.getText();

                        value = value.trim();

                        attrMap.put(keyList.get(x), value);

                        // set cursor on End-Tag of Data-Element
                        reader.nextTag();

                        // set cursor on EndTag of Cell-Element
                        reader.nextTag();
                    }
                    x++;

                }

                if (!attrMap.isEmpty()) {
                    //dataServiceDispatcher.handleDataRecord(className, attrMap);
                }

                noe ++;
            // set cursor on End-Tag of Row-Element through loop back
            }

            System.out.println(" End Document");
            return noe;

        } catch (XMLStreamException e) {
            throw e;
        } 

    }

    public boolean isFinishPanel() {
        return false;
    }
 
}
