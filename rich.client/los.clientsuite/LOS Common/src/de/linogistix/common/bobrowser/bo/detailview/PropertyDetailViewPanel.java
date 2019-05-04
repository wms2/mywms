/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 *
 *  www.linogistix.com
 *
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.bo.detailview;

import de.linogistix.common.res.CommonBundleResolver;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.explorer.propertysheet.PropertySheetView;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 *
 * @author andreas
 */
public class PropertyDetailViewPanel extends AbstractDetailViewPanel{

    PropertySheet propertySheet;

    JPanel propertySheetPanel;

    @Override
    protected void initializeGUI() {
        super.initializeGUI();
        this.propertySheet = new PropertySheetView();
        this.propertySheet.setDescriptionAreaVisible(false);
        
        this.propertySheetPanel = new JPanel(new BorderLayout());

        this.propertySheetPanel.add(this.propertySheet, BorderLayout.CENTER);
        add(this.propertySheetPanel, BorderLayout.CENTER);
       
        
    }


    @Override
    public void setNode(Node node) {
        
        if (! isGuiInitialized())
            return;

        if (node == null){
            this.propertySheet.setNodes(new Node[0]);
        }
        else{
            this.propertySheet.setNodes(new Node[]{node});
        }
    }

    @Override
    public String getPanelName() {
        return NbBundle.getMessage(CommonBundleResolver.class, "PropertyDetailViewPanel.panelName");
    }



}
