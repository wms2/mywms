/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.reports.action;

import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.util.ExceptionAnnotator;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.mywms.model.BasicEntity;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.query.BOQueryNode;
import de.linogistix.reports.res.ReportsBundleResolver;
import java.io.File;
import java.io.FileOutputStream;

public final class ExportCsvMasterNodeAction extends NodeAction {

    private static final Logger log = Logger.getLogger(ExportCsvMasterNodeAction.class.getName());

    public ExportCsvMasterNodeAction() {
    }

    @Override
    protected void initialize() {
        super.initialize();
        putValue("noIconInMenu", Boolean.FALSE);
    }

    public String getName() {
        return NbBundle.getMessage(ReportsBundleResolver.class, "CsvExportAction");
    }

    @Override
    protected String iconResource() {
        return "de/linogistix/bobrowser/res/icon/Excel.gif";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }

    protected boolean enable(Node[] activatedNodes) {

        for (Node n : activatedNodes) {
            if (!(n instanceof BOMasterNode)) {
                return false;
            }
        }

        return true;
    }
    @SuppressWarnings("unchecked")
    protected void performAction(Node[] activatedNodes) {

        BasicEntity e;

        if (activatedNodes == null || activatedNodes.length == 0) {
            return;
        }

        List masterNodes = Arrays.asList(activatedNodes);
        createCsvReport(masterNodes);

    }

    private BO getBO(BOMasterNode masterNode) {
        BOQueryNode queryNode = (BOQueryNode) masterNode.getParentNode();
        return queryNode.getModel().getBoNode().getBo();
    }

    @SuppressWarnings("unchecked")
    public void createCsvReport(List<Node> nodes) {

//            ReportService reportService = Lookup.getDefault().lookup(ReportService.class);
//            ReportService reportService = new ReportServiceBean();
//            JRException ex;

        if (nodes == null || nodes.size() < 1) {
            return;
        }
        String title = "export";
        Map pMap = new HashMap();
        Property[] props = nodes.get(0).getPropertySets()[0].getProperties();
        pMap.put("name", "name");
        for (Property p : props) {
            pMap.put(p.getName(), p.getDisplayName());
        }
        try {
            javax.swing.JFileChooser chooser = new javax.swing.JFileChooser(java.lang.System.getProperty("user.dir"));
            int returnValue = chooser.showSaveDialog(null);
            if ((returnValue == javax.swing.JFileChooser.APPROVE_OPTION)) {
                File f = chooser.getSelectedFile();
                FileOutputStream out = new FileOutputStream(f);
                out.write("name;".getBytes());
                for (Property p : props) {
                    out.write(p.getDisplayName().getBytes());
                    out.write(';');
                }
                out.write("\n".getBytes());

                for (Node n : nodes) {
                    String s;
                    out.write(n.getDisplayName().getBytes());
                    out.write(';');
                    
                    Node.PropertySet ps = n.getPropertySets()[0];
                    for (Property p : ps.getProperties()) {
                        out.write(p.getValue().toString().getBytes());
                        out.write(';');
                    }
                    out.write("\n".getBytes());
                }
                out.close();
            }
            
        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
        }
    }
}

