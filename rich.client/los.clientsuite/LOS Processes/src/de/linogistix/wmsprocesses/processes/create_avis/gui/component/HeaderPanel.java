/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.create_avis.gui.component;

import de.linogistix.common.gui.gui_builder.AbstractHeaderPanel;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
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
        super.headerLabel.setText(NbBundle.getMessage(WMSProcessesBundleResolver.class,"CreateAvis"));                
    }

}
