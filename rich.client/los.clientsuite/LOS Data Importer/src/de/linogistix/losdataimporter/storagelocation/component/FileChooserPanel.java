/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.losdataimporter.storagelocation.component;

import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.losdataimporter.res.BundleResolver;
import de.linogistix.losdataimporter.storagelocation.gui_builder.AbstractFileChooserPanel;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import javax.swing.filechooser.FileFilter;
import org.openide.util.NbBundle;

/**
 *
 * @author trautm
 */
public class FileChooserPanel extends AbstractFileChooserPanel {

    File file;

    public File getFile() {
        return this.file;
    }
    
    @Override
    public String getName() {
        return NbBundle.getMessage(BundleResolver.class, "FileChooserPanel.name");
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    protected void onFileChooserButtonActionPerformed(ActionEvent evt) {
         try {
             
            javax.swing.JFileChooser chooser = new javax.swing.JFileChooser(java.lang.System.getProperty("user.home"));
            chooser.setFileFilter(new FileFilter() {

                @Override
                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    }
                    return f.getName().toLowerCase().endsWith(".xml");
                }

                @Override
                public String getDescription

                     () {
                return "*.xml";
                }
                }
            );
            
            int returnValue = chooser.showOpenDialog(this.getParent());
                if((returnValue == javax.swing.JFileChooser.APPROVE_OPTION)) {
                    file = chooser.getSelectedFile();
                    fileChooserTextField.setText(file.getCanonicalPath());
                    
                }
            } catch  (Throwable t) {
            ExceptionAnnotator.annotate(t);
        }
    }
    
}
