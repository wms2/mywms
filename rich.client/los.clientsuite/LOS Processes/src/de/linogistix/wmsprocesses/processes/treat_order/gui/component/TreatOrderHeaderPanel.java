/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.treat_order.gui.component;

import de.linogistix.common.gui.gui_builder.AbstractHeaderPanel;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import org.openide.util.NbBundle;

/**
 *
 * @author Jordan
 */
public class TreatOrderHeaderPanel extends AbstractHeaderPanel {
    
    private TreatOrderTopComponentPanel topComponentPanel;
    
    public TreatOrderHeaderPanel(TreatOrderTopComponentPanel topComponentPanel) {
        
        this.topComponentPanel = topComponentPanel;
        
        super.headerLabel.setText(NbBundle.getMessage(WMSProcessesBundleResolver.class,"treatOrderHeader"));
    }

}
