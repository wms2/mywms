/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.inventory.report.queryinventory.gui.component;


import de.linogistix.common.gui.gui_builder.AbstractHeaderPanel;
import de.linogistix.reports.res.ReportsBundleResolver;
import org.openide.util.NbBundle;

/**
 *
 * @author artur
 */
public class HeaderPanel extends AbstractHeaderPanel {
    TopComponentPanel topComponentPanel;

    public HeaderPanel(TopComponentPanel topComponentPanel) {
        this.topComponentPanel = topComponentPanel;
        postInit();
    }
    
    
    private void postInit() {
        super.headerLabel.setText(NbBundle.getMessage(ReportsBundleResolver.class,"QueryInventory"));                
    }

}
