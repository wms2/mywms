/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common;

import de.linogistix.common.system.ModuleInstallExt;
import de.linogistix.common.userlogin.*;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
//public class Installer extends ModuleInstall {
public class Installer extends ModuleInstallExt {

    
    @Override
    public void restored() {
        
        // 24.07.2015. Does not work until JDK 1.7. PropertyEditorManager works threadlocal so registered values are not available in other threads
        //PropertyEditorManager.registerEditor(Date.class, DatePropertyEditor.class);           
        //PropertyEditorManager.registerEditor(BigDecimal.class, BigDecimalEditor.class);           

        new LoginPanelImpl().doLogin();
        
        
        
    }

    @Override
    public void postRestored() {
          
//        MutableMultiFileSystem mf = (MutableMultiFileSystem) Lookup.getDefault().lookup(FileSystem.class);
        
//        URL url = getClass().getClassLoader().getResource("de/linogistix/common/layer_default.xml");
        
//        mf.addLayer(url);
        
        
    }

}



