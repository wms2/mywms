/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.system;

import java.awt.Component;
import org.openide.util.HelpCtx;

/**
 *
 * @author artur
 */
public class HelpResolver {
    private static HelpResolver instance = null;
    /** Creates a new instance of GraphicUtil */
    private HelpResolver() {
        // Exists only to defeat instantiation.
    }
    
    public synchronized static HelpResolver getInstance() {
        if (instance == null) {
            instance = new HelpResolver();
        }
        return instance;
    }
    
   
    public HelpCtx getHelpCtx(Component comp) {
//        return new HelpCtx(comp.getLocale().getLanguage()+"/"+comp.getClass().getName().replace('.', '-'));
        return new HelpCtx(comp.getClass().getName().replace('.', '-'));        
    }

}
