/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.system;

import org.openide.modules.ModuleInstall;

/**
 *
 * @author artur
 */
public abstract class ModuleInstallExt extends ModuleInstall {

    public ModuleInstallExt() {
        ModulInstallExtSingleton.getInstance().addListener(this);
    }
    
    /**
     * Will be called after login
     */
    abstract public void postRestored();
    
}
