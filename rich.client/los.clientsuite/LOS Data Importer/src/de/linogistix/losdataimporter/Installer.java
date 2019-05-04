/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.losdataimporter;

import de.linogistix.common.services.MutableMultiFileSystem;
import de.linogistix.common.system.ModuleInstallExt;
import java.net.URL;
import org.openide.util.Lookup;
import org.openide.filesystems.FileSystem;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
public class Installer extends ModuleInstallExt {

    @Override
    public void restored() {
        
        
    }

    @Override
    public void postRestored() {
        MutableMultiFileSystem mf = (MutableMultiFileSystem) Lookup.getDefault().lookup(FileSystem.class);
        
        URL url = getClass().getClassLoader().getResource("de/linogistix/losdataimporter/res/layer_ext.xml");
        
        mf.addLayer(url);
    }
}
