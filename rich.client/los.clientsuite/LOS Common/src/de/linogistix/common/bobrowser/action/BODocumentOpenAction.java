/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.action;

import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.bobrowser.query.BOQueryNode;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryRemote;

import java.io.File;
import java.io.FileOutputStream;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import org.mywms.facade.FacadeException;
import org.mywms.model.Document;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

public final class BODocumentOpenAction extends NodeAction {

    private static final Logger log = Logger.getLogger(BODocumentOpenAction.class.getName());

    public String getName() {
        return NbBundle.getMessage(CommonBundleResolver.class, "savePDF");
    }

    protected String iconResource() {
        return "de/linogistix/bobrowser/res/icon/Pdf.gif";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        return false;
    }

    protected boolean enable(Node[] activatedNodes) {
        boolean ret = true;

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

            int returnValue = chooser.showSaveDialog(null);
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
                Document doc = (Document) service.queryById(dto.getId());

                File outf = new File(f, doc.getName() + ".pdf");
                FileOutputStream out = new FileOutputStream(outf);
                if (doc.getDocument().length == 0){
                    FacadeException ex = new FacadeException("Document is empty", "BusinessException.DocumentEmpty", null);
                    ex.setBundleResolver(CommonBundleResolver.class);
                    ExceptionAnnotator.annotate(ex);
                    return;
                }
                out.write(doc.getDocument());
                out.close();
//                FileSystem fs = FileUtil.createMemoryFileSystem();
//                FileObject fob = fs.getRoot().createData(dto.getName(), "pdf");
//     
//                FileLock lock = fob.lock();
//                OutputStream os = fob.getOutputStream(lock);
////                OutputStream os = fob.getOutputStream();
//                os.write(doc.getDocument());
//                os.close();
//                DataObject data = DataObject.find(fob);
//                  
//                OpenCookie cookie = (OpenCookie) data.getCookie(OpenCookie.class);
//                if (cookie != null){
//                    cookie.open();
//                } else{
//                    throw new NullPointerException();
//                }
            }
        } catch (Throwable ex) {
            log.log(Level.SEVERE, ex.getMessage(), ex);
            ExceptionAnnotator.annotate(ex);
        } finally {
            CursorControl.showNormalCursor();
        }
    }
}

