/*
 * Copyright (c) 2006 - 2011 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.reports.action;

import de.linogistix.reports.gui.component.BOExportWizard;
import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.bobrowser.query.BOQueryNode;
//import de.linogistix.common.res.ReportsBundleResolver;
import de.linogistix.common.action.OpenDocumentTask;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.reports.res.ReportsBundleResolver;
import java.awt.Dialog;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.NodeAction;

public final class BOExportCsvAction extends NodeAction {

    private static final Logger log = Logger.getLogger(BOExportCsvAction.class.getName());
    

    public BOExportCsvAction() {
        putValue(SMALL_ICON, new ImageIcon(ImageUtilities.loadImage("de/linogistix/reports/res/icon/csv.gif", true)));
    }
    
    public String getName() {
        return NbBundle.getMessage(ReportsBundleResolver.class, "BOExportCsvAction");
    }

    protected String iconResource() {
        return "de/linogistix/reports/res/icon/csv.gif";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        return false;
    }

    protected boolean enable(Node[] activatedNodes) {
        boolean ret = false;

        if (activatedNodes.length > 0) {
            Node n = activatedNodes[0];
            Node p;
            while ((p = n.getParentNode()) != null) {
                if (n instanceof BOQueryNode || p instanceof BOQueryNode) {
                    ret = true;
                } else if (n instanceof BOMasterNode || p instanceof BOMasterNode) {
                    ret = true;
                }

                n = p;

            }

        }

        return ret;
    }

    protected void performAction(Node[] activatedNodes) {
        String SEPARATOR = ",";

        
        if( activatedNodes == null || activatedNodes.length==0 ) {
            return;
        }

        try {
            BOExportWizard w = new BOExportWizard(null, ".csv", "*.csv", "export.csv");
            //     JSaveDialog saveDialog = new JSaveDialog(".xls", "*.xls", "export.xls");

            Dialog d = DialogDisplayer.getDefault().createDialog(w);
            d.setVisible(true);

            if( !w.getValue().equals(NotifyDescriptor.OK_OPTION) ) {
                return;
            }

            BufferedWriter out = new BufferedWriter(new FileWriter(new File(w.getFileName())));

            CursorControl.showWaitCursor();

            Node start = activatedNodes[0];

            out.write("\"");
            out.write(start.getParentNode() == null ? "X" : start.getParentNode().getDisplayName());
            out.write("\"");
            out.write(SEPARATOR);
            for( Node.PropertySet set : start.getPropertySets()) {
                Property[] properties = set.getProperties();
                for( Property prop : properties ) {
                    out.write("\"");
                    out.write(prop.getDisplayName());
                    out.write("\"");
                    out.write(SEPARATOR);
                }
            }
            out.newLine();

            for (Node act : activatedNodes) {

                out.write("\"");
                out.write(act.getDisplayName() );
                out.write("\"");
                out.write(SEPARATOR);
                for( Node.PropertySet set : act.getPropertySets()) {
                    Property[] properties = set.getProperties();

                    for( Property prop : properties ) {
                        out.write("\"");
                        out.write(prop.getValue() == null ? "" : prop.getValue().toString());
                        out.write("\"");
                        out.write(SEPARATOR);
                    }
                }
                out.newLine();
            }

            out.close();

            if (w.isOpen()) {
                OpenDocumentTask.openDocument(w.getFileName());
            }

        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
        } finally {
            CursorControl.showNormalCursor();
        }
    }
}

