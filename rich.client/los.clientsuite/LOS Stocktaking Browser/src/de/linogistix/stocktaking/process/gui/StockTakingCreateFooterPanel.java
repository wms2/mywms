/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.stocktaking.process.gui;


import de.linogistix.common.gui.gui_builder.AbstractFooterPanel;
import de.linogistix.stocktaking.res.StocktakingBundleResolver;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author artur
 */
public class StockTakingCreateFooterPanel extends AbstractFooterPanel {
    
    private JButton btCreate;
    
    StocktakingCreatePanel controller;
    
    public StockTakingCreateFooterPanel(StocktakingCreatePanel controller) {
        this.controller = controller;
    }
   
    @Override
    public List<JButton> getButtonList() {
        btCreate = new JButton();
        btCreate.setText(NbBundle.getMessage(StocktakingBundleResolver.class, "BT_CREATE"));
        btCreate.setMnemonic(NbBundle.getMessage(StocktakingBundleResolver.class, "BT_CREATE_MNEMONIC").charAt(0));
        btCreate.addActionListener( new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                processCreate();
            }
        });

        
        List<JButton> buttonList = new ArrayList<JButton>();
        buttonList.add(btCreate);
        
        return buttonList;
    }
    
    

    protected void processCreate() {
        controller.processCreate();
    }

}
