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
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import java.util.logging.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.BasicEntity;
import org.mywms.model.Document;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

public final class SavePDFAction extends NodeAction {

    private static final Logger log = Logger.getLogger(SavePDFAction.class.getName());

    public String getName() {
        return NbBundle.getMessage(CommonBundleResolver.class, "OpenPDFAction.open");
    }

    protected String iconResource() {
        return "de/linogistix/bobrowser/res/icon/pdf.gif";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        return false;
    }

    protected boolean enable(Node[] activatedNodes) {
       
        if (activatedNodes.length !=  1){
            return false;
        }
        
        return true;
    }

    protected void performAction(Node[] activatedNodes) {

        BasicEntity e;

        if (activatedNodes == null) {
            return;
        }

        try {
           for (Node n : activatedNodes) {
                if (n == null) {
                    continue;
                }
                if (n instanceof BOMasterNode) {
                    Long id = ((BOMasterNode)n).getEntity().getId();
                    BOMasterNode m = ((BOMasterNode)n);
                    BusinessObjectQueryRemote q =m.getBo().getQueryService();
                    BasicEntity entity = q.queryById(id);
                    if (entity instanceof Document){
                        Document d = (Document)entity;
                        byte[] document = d.getDocument();
                        //store in tmp
                        
                        // open id viewer
                    } else{
                        ExceptionAnnotator.annotate(new RuntimeException("Can only be invoked on Document type "));
                    }
                    
                } else{
                    ExceptionAnnotator.annotate(new RuntimeException("wrong Node typ: " + n.toString()));
                }
                
                
            }
        } catch (FacadeException ex) {
            ExceptionAnnotator.annotate(ex);
        } finally {
            CursorControl.showNormalCursor();
        }
    }
    
    
}

