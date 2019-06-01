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

import de.linogistix.common.exception.InternalErrorException;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.util.businessservice.ImportDataException;
import de.linogistix.los.util.businessservice.ImportDataService;
import de.linogistix.losdataimporter.res.BundleResolver;
import de.linogistix.losdataimporter.storagelocation.component.HintPanel;
import de.wms2.mywms.location.StorageLocation;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
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
final public class ImportLocationHintPanel implements WizardDescriptor.ValidatingPanel, PropertyChangeListener, WizardDescriptor.FinishablePanel {

    private static final Logger log = Logger.getLogger(ImportLocationHintPanel.class.getName());
    
    private ImportLocationWizard wizard;
    private HintPanel ui;
    /** listener to changes in the wizard */
    private ChangeListener listener;
    private boolean importedSucessful = false;

    ImportTimerTask timerTask;
    ImportTask task;

        
    private HintPanel getPanelUI() {
        if (ui == null) {
            ui = new HintPanel();

            ui.getImportButton().addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    onImportButtonActioNPerformed();
                }
            });
        }
        ;
        return ui;
    }

    private void onImportButtonActioNPerformed() {

        CursorControl.showWaitCursor();
        getPanelUI().getImportButton().setEnabled(false);

        timerTask = new ImportTimerTask();
        task = new ImportTask(wizard.getFile());
        task.execute();
        timerTask.execute();

    }

    private void onImportTaskDone() {

        if (timerTask != null && ! timerTask.isDone()){
            timerTask.finish();
        }
        
        importedSucessful = true;
        CursorControl.showNormalCursor();
        wizard.stateChanged(null);
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
        return importedSucessful;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        this.wizard.stateChanged(null);
    }

    public boolean isFinishPanel() {
        return false;
    }

    public void readSettings(Object settings) {
        try {
            this.wizard = (ImportLocationWizard) settings;

            int noe = wizard.getNumberOfEntries();
            String filename = this.wizard.getFile().getCanonicalPath();
            StringBuffer b = new StringBuffer();
            b.append(NbBundle.getMessage(BundleResolver.class, "HintPanel.textpanel.importhint", new Object[]{noe, filename}));

            getPanelUI().getTextPane().setText(new String(b));
        } catch (IOException ex) {
            ExceptionAnnotator.annotate(ex);
        }

    }

    public void storeSettings(Object settings) {
        this.wizard = (ImportLocationWizard) settings;
    }

    public void validate()
            throws WizardValidationException {
        //
    }

    class ImportTask extends org.jdesktop.swingx.util.SwingWorker<Void, Void> {

        private File f;

        public ImportTask(File f) {
            this.f = f;
        }

        @Override
        protected Void doInBackground() throws Exception {
            try {
                J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
                ImportDataService importDataService = loc.getStateless(ImportDataService.class);

                FileInputStream fileInputStream = new FileInputStream(f);
                byte[] data = new byte[(int) f.length()];
                fileInputStream.read(data);
                fileInputStream.close();

                log.info("Going to import Data...");
                
                List<Object> list = importDataService.importData(StorageLocation.class.getSimpleName(), data);
                wizard.setImportedLocations(list);
               

            } catch (FacadeException ex) {
                ExceptionAnnotator.annotate(ex);
            } catch (IOException ex) {
                FacadeException fex = new InternalErrorException(NbBundle.getMessage(BundleResolver.class, "ImportLocationFilePanel.ERROR.cannotreadfile", null));
                ExceptionAnnotator.annotate(fex);
            } catch (ImportDataException ex) {
                FacadeException fex = new InternalErrorException(NbBundle.getMessage(BundleResolver.class, "ImportLocationFilePanel.ERROR.cannotimportfile", null));
                ExceptionAnnotator.annotate(fex);
            }
            return null;
        }

        @Override
        protected void done() {
            onImportTaskDone();
        }
    }

    class ImportTimerTask extends org.jdesktop.swingx.util.SwingWorker<Void, Void> {
        boolean goon = true;

        @Override
        protected Void doInBackground() throws Exception {
            int secs = 0;
            while (goon) {
                secs+=10;
                if( secs >= 100 ) {
                    secs = 0;
                }
                getPanelUI().getImportProgessBar().setValue(secs);
                Thread.sleep(500);
            }

            getPanelUI().getImportProgessBar().setValue(100);
            return null;
        }

        private void finish() {
            goon = false;
        }
    }
}
