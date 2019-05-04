/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.losdataimporter.storagelocation.component;

import de.linogistix.common.gui.component.windows.JSaveDialog;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.losdataimporter.res.BundleResolver;
import de.linogistix.losdataimporter.storagelocation.gui_builder.AbstractCreateLabelsPanel;
import java.awt.event.ActionEvent;
import java.io.File;
import org.openide.util.NbBundle;

/**
 *
 * @author trautm
 */
public class CreateLabelsPanel extends AbstractCreateLabelsPanel {

    File file;

    public File getFile() {
        return this.file;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(BundleResolver.class, "CreateLabelsPanel.name");
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    protected void onFileChooserButtonActionPerformed(ActionEvent evt) {
        try {

            JSaveDialog saveDialog = new JSaveDialog(".pdf", "*.pdf", "Labels.pdf");
 
            int returnValue = saveDialog.showOpenDialog(null);
            if ((returnValue == javax.swing.JFileChooser.APPROVE_OPTION)) {
                file = saveDialog.getSelectedFile();
                fileChooserTextField.setText(file.getCanonicalPath());

            }
        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
        }
    }

   
}
