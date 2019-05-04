/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.action;

import de.linogistix.common.res.CommonBundleResolver;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author trautm
 */
public class BOCopyToClipBoardAction extends NodeAction{

    @Override
    protected void performAction(Node[] arg0) {
        
        StringBuffer b = new StringBuffer();
        
        for (Node n : arg0){
            b.append(n.toString());
            b.append('\n');
        }
        
        StringSelection content = new StringSelection(new String(b));
        Toolkit.getDefaultToolkit().getSystemClipboard().
                            setContents(content, content);

    }

    @Override
    protected boolean enable(Node[] arg0) {
        return true;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(CommonBundleResolver.class, "copy");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

   
}
