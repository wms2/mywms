/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.create_avis.gui.component;

import de.linogistix.common.gui.gui_builder.AbstractFooterPanel;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import java.awt.event.ActionEvent;
import org.netbeans.api.javahelp.Help;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author artur
 */
public class FooterPanel extends AbstractFooterPanel {
    TopComponentPanel topComponentPanel;

    public FooterPanel(TopComponentPanel topComponentPanel) {
        this.topComponentPanel = topComponentPanel;
        postInit();
    }
    
    private void postInit() {
        super.okButton.setText(NbBundle.getMessage(WMSProcessesBundleResolver.class,"Create order"));
        super.okButton.setMnemonic(NbBundle.getMessage(WMSProcessesBundleResolver.class, "CreateOrder.mnemonic").charAt(0));
        clearButton.setText(NbBundle.getMessage(WMSProcessesBundleResolver.class, "Clear"));
        clearButton.setMnemonic(NbBundle.getMessage(WMSProcessesBundleResolver.class, "Clear.mnemonic").charAt(0));
    }

    @Override
    protected void okButtonActionPerformedListener(ActionEvent evt) {
        topComponentPanel.centerPanel.createOrder();
    }

    @Override
    protected void clearButtonActionPerformedListener(ActionEvent evt) {
        topComponentPanel.centerPanel.clear();
    }

    @Override
    protected void flatButtonActionPerformedListener(ActionEvent evt) {
        Help help = (Help) Lookup.getDefault().lookup(Help.class);
        help.showHelp(topComponentPanel.topComponent.getHelpCtx());
    }
    
     
}
