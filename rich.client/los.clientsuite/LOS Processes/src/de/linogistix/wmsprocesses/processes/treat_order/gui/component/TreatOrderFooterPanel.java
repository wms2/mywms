/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.treat_order.gui.component;

import de.linogistix.common.gui.gui_builder.AbstractFooterPanel;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JButton;
import org.openide.util.NbBundle;

/**
 *
 * @author Jordan
 */
public class TreatOrderFooterPanel extends AbstractFooterPanel{

    private TreatOrderTopComponentPanel topComponentPanel;

    public TreatOrderFooterPanel(TreatOrderTopComponentPanel topComponentPanel) {
        this.topComponentPanel = topComponentPanel;
        postInit();
    }
    
    
    @Override
    public List<JButton> getButtonList() {
        List<JButton> buttonList = super.getButtonList();
       
        return buttonList;
        
    }
    
    private void postInit() {
        super.okButton.setText(NbBundle.getMessage(WMSProcessesBundleResolver.class,"startOrder"));
        super.okButton.setMnemonic(NbBundle.getMessage(WMSProcessesBundleResolver.class, "CreateOrderAndStart.mnemonic").charAt(0));
        super.clearButton.setText(NbBundle.getMessage(WMSProcessesBundleResolver.class,"Clear"));
        super.clearButton.setMnemonic(NbBundle.getMessage(WMSProcessesBundleResolver.class, "Clear.mnemonic").charAt(0));
    }

    @Override
    protected void okButtonActionPerformedListener(java.awt.event.ActionEvent evt) {
      topComponentPanel.getCenterPanel().process();
    }
    
    @Override
    protected void  clearButtonActionPerformedListener(java.awt.event.ActionEvent evt) {
      topComponentPanel.getCenterPanel().reset();
    }

}
