/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.other;

import java.awt.BorderLayout;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.util.Lookup;

/**
 *
 * @author trautm
 */
public class LOSExplorerProviderPanel extends JPanel implements ExplorerManager.Provider, Lookup.Provider{

    ExplorerManager manager;
    
    Lookup lookup;
    
    public LOSExplorerProviderPanel() {
        setLayout(new BorderLayout());
        ActionMap map = getActionMap();
        if (map == null){
            map = new ActionMap();
        }
        InputMap keys = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        //keys.put(KeyStroke.getKeyStroke(""), "delete");
        ExplorerUtils.createLookup(getExplorerManager(), map);
    }

    public ExplorerManager getExplorerManager() {
        if (manager == null){
            manager = new ExplorerManager();
        }
        return manager;
    }

    public Lookup getLookup() {
        return lookup;
    }

}
