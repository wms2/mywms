/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.system;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.openide.util.Exceptions;

/**
 *
 * @author artur
 */
public class Registry {
//  static Preferences prefs = Preferences.userRoot().node( "/com/tutego/insel" ); 
    private static Registry instance = null;

    /**
     * prevent instantation
     */
    private Registry() {
    }

    public synchronized static Registry getInstance() {
        if (instance == null) {
            instance = new Registry();
        }
        return instance;
    }

/*    public static void fillRegistry() {
        Preferences prefs = Preferences.systemRoot().node("/de/linogistix/los");
        prefs.put("Test", "test2");

        System.out.println("global " + prefs.get("Test", "default"));
    }*/

    public void setSytemParam(String path, String key, String value) {
        Preferences prefs = Preferences.systemRoot().node(path);
        prefs.put(key, value);
    }

    public String getSystemParam(String path, String key) {
        Preferences prefs = Preferences.systemRoot().node(path);
        return prefs.get(key, "");
    }

    public void setUserParam(String path, String key, String value) {
        Preferences prefs = Preferences.userRoot().node(path);
        prefs.put(key, value);
    }

    public String getUserParam(String path, String key) {
        Preferences prefs = Preferences.userRoot().node(path);
        return prefs.get(key, "");
    }
    
    public String[] getSystemKeys(String path) {
        try {
            Preferences prefs = Preferences.systemRoot().node(path);            
            return prefs.keys();
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
    
    public String[] getUserKeys(String path) {
        try {
            Preferences prefs = Preferences.userRoot().node(path);
            return prefs.keys();
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
    
}
