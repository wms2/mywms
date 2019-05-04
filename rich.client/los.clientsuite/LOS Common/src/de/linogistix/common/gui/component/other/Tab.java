/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.other;

import de.linogistix.common.system.Registry;
import javax.swing.UIManager;

/**
 *
 * @author artur
 */
public class Tab {

    private static Tab instance = null;
    private String defaultTab = null;

    public synchronized static Tab getInstance() {
        if (instance == null) {
            instance = new Tab();
            instance.init();
        }
        return instance;
    }
    
    private void saveDefaultToRegistry() {
        if (defaultTab != null) {
            Registry registry = Registry.getInstance();            
            registry.setSytemParam("/default", "Tab", defaultTab);
        }
    }
    
    private void loadDefaultFromRegistry() {
       Registry registry = Registry.getInstance();            
       defaultTab = registry.getSystemParam("/default", "Tab");        
    }
    
    public String getDeafaultTab() {
        if (defaultTab == null) {
            loadDefaultFromRegistry();
        }
        return defaultTab;
    }
    
    private void init() {       
        String tab = (String)UIManager.get("EditorTabDisplayerUI");
        if (tab.equals("de.linogistix.common.gui.component.other.TabUI") == false) {
            defaultTab = tab;
            saveDefaultToRegistry();
        }
    }
    
   public void hideTab() {       
                     //"ViewTabDisplayerUI"
        UIManager.put ("EditorTabDisplayerUI", "de.linogistix.common.gui.component.other.TabUI");       
   }
   
   public void showTab() {
//        UIManager.put ("EditorTabDisplayerUI", "de.linogistix.common.gui.netbeans.TabUI");       
   }
   
   public void hideCloseButton() {
        System.setProperty("netbeans.tab.close.button.enabled","false");
        System.setProperty("nb.tabs.suppressCloseButton","true");       
   }
   
   public void showCloseButton() {
        System.setProperty("netbeans.tab.close.button.enabled","true");
        System.setProperty("nb.tabs.suppressCloseButton","false");              
   }
    
}
