/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.system;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author artur
 */
public class ModulInstallExtSingleton {
    private static ModulInstallExtSingleton instance = null;
    /** Creates a new instance of PostModulInstallSingleton */
    List<ModuleInstallExt> informList = new ArrayList();
    
    private ModulInstallExtSingleton() {
        // Exists only to defeat instantiation.
    }
    
    
    public synchronized static ModulInstallExtSingleton getInstance() {
        if (instance == null) {
            instance = new ModulInstallExtSingleton();
        }
        return instance;
    }

    public void addListener(ModuleInstallExt modul) {
        if (modul != null)
            informList.add(modul);
    }

    public void informClasses() {
        for (ModuleInstallExt pos: informList) {
            pos.postRestored();
        }
        informList = new ArrayList();
    }
}
