/*
 * Copyright (c) 2012-2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.action;

import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.bobrowser.query.BOQueryNode;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.model.LOSJasperReport;

import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import java.io.File;
import java.io.FileOutputStream;

import java.util.logging.Logger;
import javax.swing.JFileChooser;
import org.mywms.facade.FacadeException;
import org.mywms.globals.Role;
import org.mywms.model.Document;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.openide.windows.WindowManager;

/**
 * @author krane
 *
 */
public final class BOJasperReportSaveSourceAction extends NodeAction {

    private static final Logger log = Logger.getLogger(BOJasperReportSaveSourceAction.class.getName());
    private static String[] allowedRoles = new String[]{Role.ADMIN_STR};

    public String getName() {
        return NbBundle.getMessage(CommonBundleResolver.class, "BOLOSJasperReportSaveSourceAction.name");
    }

    @Override
    protected String iconResource() {
        return "de/linogistix/bobrowser/res/icon/Document.png";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }

    protected boolean enable(Node[] activatedNodes) {
        boolean ret = true;

        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
        if (!login.checkRolesAllowed(allowedRoles)) {
            return false;
        }
        
        return ret;
    }

    protected void performAction(Node[] activatedNodes) {

        if (activatedNodes == null) {
            return;
        }

        CursorControl.showWaitCursor();

        try {
            File f;
            javax.swing.JFileChooser chooser = new javax.swing.JFileChooser(java.lang.System.getProperty("user.home"));
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int returnValue = chooser.showSaveDialog(WindowManager.getDefault().getMainWindow());
            if ((returnValue == javax.swing.JFileChooser.APPROVE_OPTION)) {
                f = chooser.getSelectedFile();
                if (!f.isDirectory()){
                    FacadeException ex = new FacadeException("Please choose a director", "BusinessException.ChooseDirectory", null);
                    ex.setBundleResolver(CommonBundleResolver.class);
                    ExceptionAnnotator.annotate(ex);
                    return;
                }
            } else{
                return;
            }
            
            for (Node n : activatedNodes) {
                
                if (n == null) {
                    continue;
                }
                if (!(n instanceof BOMasterNode)) {
                    log.warning("Not a BOMasterNodeType: " + n.toString());
                    continue;
                }

                if (!(n.getParentNode() instanceof BOQueryNode)) {
                    log.warning("No BOQueryNode found: " + n.toString());
                    continue;
                }

                BODTO<Document> dto = ((BOMasterNode) n).getEntity();
                BOQueryNode parent = (BOQueryNode) n.getParentNode();
                BusinessObjectQueryRemote service = parent.getModel().getBoNode().getQueryService();
                LOSJasperReport doc = (LOSJasperReport) service.queryById(dto.getId());

                File outf = new File(f, doc.getName() + "-" + doc.getClient().getNumber()+".jrxml");
                FileOutputStream out = new FileOutputStream(outf);
                if( doc.getSourceDocument()==null || doc.getSourceDocument().length() == 0 ){
                    FacadeException ex = new FacadeException("Document is empty", "BusinessException.DocumentEmpty", null);
                    ex.setBundleResolver(CommonBundleResolver.class);
                    ExceptionAnnotator.annotate(ex);
                    return;
                }
                out.write( doc.getSourceDocument().getBytes() );
                out.close();
            }
        } catch (Throwable ex) {
            ExceptionAnnotator.annotate(ex);
        } finally {
            CursorControl.showNormalCursor();
        }
    }
}

