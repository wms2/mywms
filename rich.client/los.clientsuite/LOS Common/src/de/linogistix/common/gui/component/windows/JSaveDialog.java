/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.windows;

import de.linogistix.common.res.CommonBundleResolver;
import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author trautm
 */
public class JSaveDialog extends JFileChooser {

private static final long serialVersionUID = 1L;
        /** Der Dateifilter. */
        private FileFilter fileFilter;
        /** Mit welchem Wert der Dialog geschlossen wurde. */
        private int closeValue = javax.swing.JFileChooser.CANCEL_OPTION;
        /** Der eigentliche Dialog.  */
        private JDialog dialog;
        private String fileEnding;
        private String description;
        
        /** Erzeugt einen neuen Datei speichern Dialog.
         * 
         * @param fileEnding Die Dateiendung.
         * @param description Die Beschreibung des Dateityps.
         */
        public JSaveDialog(final String fileEnding, final String description, String defaultFileName) {
            super();
            this.fileEnding = fileEnding;
            this.description = description;
            fileFilter = new FileFilter() {

                @Override
                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    }
                    return f.getName().toLowerCase().endsWith(fileEnding);
                }

                @Override
                public String getDescription() {
                    return description;
                }
            };

            this.setApproveButtonText(NbBundle.getMessage(de.linogistix.common.res.CommonBundleResolver.class, "SaveDialog.approveButton.text")); //$NON-NLS-1$

            this.setDialogTitle(NbBundle.getMessage(de.linogistix.common.res.CommonBundleResolver.class, "SaveDialog.cancelButton.text")); //$NON-NLS-1$

            this.setDialogType(SAVE_DIALOG);
            this.setFileFilter(fileFilter);
            this.setSelectedFile(new File(defaultFileName));
        }

        /** Liefert den Wert, mit dem der Dialog verlassen wurde.
         * 
         * @return Mit welchem Button der Dialog verlassen wurde.
         */
        public int getCloseValule() {
            return closeValue;
        }

        /** Bearbeitet das Drücken des Speichern Knopfes .
         * Sollte die gewählte Datei schon existieren, so wird eine Abfrage gestartet,
         * ob diese Datei überschrieben werden soll.
         * 
         * @see javax.swing.JFileChooser#approveSelection()
         */
        @Override
        public void approveSelection() {
            if (getSelectedFile().exists()) {
                NotifyDescriptor d = new NotifyDescriptor.Confirmation(
                        NbBundle.getMessage(CommonBundleResolver.class, "SaveDialog.fileExists.message"),
                        NbBundle.getMessage(CommonBundleResolver.class, "SaveDialog.fileExists.title"),
                        NotifyDescriptor.YES_NO_OPTION);

                if (DialogDisplayer.getDefault().notify(d) != NotifyDescriptor.YES_OPTION) {
                    return;
                }
            }
            this.grabFocus();
            closeValue = JFileChooser.APPROVE_OPTION;
            super.approveSelection();
        }

        /** Bearbeitet das Drücken des Cancel Knopfes.
         * 
         * @see javax.swing.JFileChooser#cancelSelection()
         */
        @Override
        public void cancelSelection() {
            closeValue = JFileChooser.CANCEL_OPTION;
            super.cancelSelection();
        }

        /** Liefert den kompletten Dateinamen inklusive Pfad der ausgewählten Datei.
         * 
         * @return Der Name der ausgewählten Datei.
         */
        public String getFileName() {
            return getSelectedFile().getPath();
        }

        /** Erzeugt den eigentlichen Dialog.
         * 
         * @see javax.swing.JFileChooser#createDialog(java.awt.Component)
         */
        protected JDialog createDialog(Component parent) throws HeadlessException {
            this.dialog = super.createDialog(parent);
            return dialog;
        }

        /** Liefert die ausgewählte Datei zurück.
         * 
         * @return Die ausgewählte Datei.
         * 
         * @see javax.swing.JFileChooser#getSelectedFile()
         */
        @Override
        public File getSelectedFile() {
            File myFile = super.getSelectedFile();
            if (myFile == null) {
                return myFile;
            }
            if (getFileFilter().getDescription().equals(fileFilter.getDescription())) {
                if (!myFile.getName().endsWith(fileEnding )) {
                    return new File(myFile.getPath() +fileEnding ); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
            return myFile;
        }
    }
