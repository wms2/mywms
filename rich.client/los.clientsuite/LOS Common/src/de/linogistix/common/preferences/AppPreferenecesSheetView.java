/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.preferences;

import org.openide.explorer.propertysheet.PropertySheetView;
import org.openide.nodes.Node;

/**
 *
 * @author trautm
 */
public class AppPreferenecesSheetView extends PropertySheetView{
    
    private AppPreferencesNode aNode;
    
    /** Creates a new instance of AppPreferenecesSheetView */
    public AppPreferenecesSheetView(AppPreferencesNode aNode) {
        this.aNode = aNode;
    }

    public void removeNotify() {
//        System.out.println("removeNotify");
        super.removeNotify();
    }

    public void addNotify() {
//        System.out.println("addNotify");
        super.addNotify();
    }

    public void setNodes(Node[] node) {
        super.setNodes(new Node[]{aNode});
    }

    public AppPreferencesNode getANode() {
        return aNode;
    }

    public void setANode(AppPreferencesNode aNode) {
        this.aNode = aNode;
    }
    
}
