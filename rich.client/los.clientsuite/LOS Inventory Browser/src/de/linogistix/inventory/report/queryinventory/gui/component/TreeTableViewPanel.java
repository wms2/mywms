/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.inventory.report.queryinventory.gui.component;

import de.linogistix.common.gui.listener.TopComponentListener;
import de.linogistix.inventory.report.queryinventory.gui.gui_builder.AbstractTreeTableViewPanel;
import java.util.Arrays;
import java.util.List;
import org.openide.nodes.Node;


/**
 *
 * @author artur
 */
public class TreeTableViewPanel extends AbstractTreeTableViewPanel implements TopComponentListener {

    public TreeTableViewPanel() {
        super();
    }

    public void clear(){
        setNodes(null, null, null, false, true);
    }
    
    public void componentOpened() {
        clear();
    }

    public void componentClosed() {
        clear();
    }

    public void componentActivated() {
        
    }

    public void componentDeactivated() {
        
    }

    public void componentHidden() {
        
    }

    public void componentShowing() {
       
    }

    List<Node> getNodes() {
       return Arrays.asList( manager.getRootContext().getChildren().getNodes());
    }

}