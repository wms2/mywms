/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.reports.action;

import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.services.J2EEServiceNotAvailable;
import de.linogistix.common.util.ExceptionAnnotator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import org.mywms.model.Document;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.execution.NbProcessDescriptor;
import org.openide.util.ImageUtilities;
import org.openide.util.actions.CookieAction; 
        
/**
 * Action to retieve repports from a service. 
 */
public class BOMasterNodeReportAction extends CookieAction {

    private Class service;
    private String method;
    private String name;

    public BOMasterNodeReportAction() {

    }

    public BOMasterNodeReportAction(String name, String iconpath, Class service, String method) {
        this.service = service;
        this.method = method;
        this.name = name;
        if (iconpath != null) {
            putValue(SMALL_ICON, new ImageIcon(ImageUtilities.loadImage(iconpath, true)));
        }
    }

    @Override
    protected void performAction(Node[] arg0) {
        String initialDirectory = " ";
        File file;
        JFileChooser chooser;
        int returnVal;
        String selected;
        File defaultFile;

        try {
            J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
            Object instance;
            Method m;
            try{
                instance = loc.getStateless(this.service);
    //            Method m = instance.getClass().getMethod(this.method, new Object[0]);
                m = instance.getClass().getMethod(this.method, new Class[]{List.class});
            } catch (Throwable t){
                throw new J2EEServiceNotAvailable();
            }
            List args = new ArrayList(arg0.length + 20);

            for (BOMasterNode n : (BOMasterNode[]) arg0) {
                args.add(n.getEntity());
            }
            final Document doc = (Document) m.invoke(instance, new Object[]{args});

            defaultFile = new File(initialDirectory + System.getProperty("path.separator") + doc.getName()).getCanonicalFile();

            chooser = new JFileChooser(initialDirectory);
            chooser.setDialogType(JFileChooser.SAVE_DIALOG);
            chooser.setFileFilter(new FileFilter() {

                @Override
                public boolean accept(File f) {
                    if (f.getName().endsWith(doc.getName().substring(doc.getName().lastIndexOf(".")))) {
                        return true;
                    } else {
                        return false;
                    }
                }

                @Override
                public String getDescription() {
                    return doc.getType();
                }
                });
            chooser.setSelectedFile(defaultFile);
            returnVal = chooser.showSaveDialog(null);
            if (returnVal == chooser.APPROVE_OPTION) {
                /* To create a URL for a file on the local file-system, we simply
                 * pre-pend the "file" protocol to the absolute path of the file.
                 */

                file = chooser.getSelectedFile();
                selected = file.getCanonicalPath();
                FileOutputStream out = new FileOutputStream(file);
                out.write(doc.getDocument());
                out.close();

                //open File


                try {
                    NbProcessDescriptor desc = new NbProcessDescriptor("start", selected);
                    desc.exec();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, ex);
                }

            }



        } catch (Throwable ex) {
            ExceptionAnnotator.annotate(ex);
        }
    }

    @Override
    protected boolean enable(Node[] arg0) {
        return true;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(CommonBundleResolver.class, this.name);
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    @Override
    protected int mode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Class<?>[] cookieClasses() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
