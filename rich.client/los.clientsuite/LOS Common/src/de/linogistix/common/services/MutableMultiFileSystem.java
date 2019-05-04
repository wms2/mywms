/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.services;

/**
 *
 * @author artur
 */

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.MultiFileSystem;
import org.openide.filesystems.Repository;
import org.openide.filesystems.XMLFileSystem;
import org.openide.util.Exceptions;
import org.xml.sax.SAXException;

public class MutableMultiFileSystem extends MultiFileSystem {

    private static final long serialVersionUID = 1L;

    private final List<URL> layers = new ArrayList<URL>();    
    
    private static MutableMultiFileSystem INSTANCE;
    
    public MutableMultiFileSystem() {
        // let's create the filesystem empty
        
        INSTANCE = this;
    }

    public List<URL> getLayers() {
        return layers;
    }
    
    public void setLayers(Collection<URL> urls) {
        
        final boolean changed;
        
        synchronized (layers) {
            final boolean added = layers.addAll(urls);
            final boolean removed = layers.retainAll(urls);
            changed = added || removed;
        }

        if (changed) {
            update();
        }
    }
    
    public void addLayer(URL layer){
        
        final boolean changed;
        
        synchronized (layers) {
            
            changed = layers.add(layer);
            
        }
        
        if (changed) {
            update();
        }
    }

    public void removeAllLayers() {
        
        final boolean changed;
        
        synchronized (layers) {
            changed = !layers.isEmpty();
            if (changed) {
                layers.clear();
            }
        }

        if (changed) {
            update();
        }
        
        
    }
    
    public void removeLayer(URL url) {
        List<URL> list = new ArrayList<URL>();
        final boolean changed;
        
        synchronized (layers) {
            changed = !layers.isEmpty();
            if (changed) {
                for (URL l: layers) {
                    if (l.sameFile(url) == false) {
                        list.add(l);
                    }    
                }
                layers.clear();
            }
        }

        if (changed) {
            update();
        }
        for (URL l: list) {
            layers.add(l);
        }
        if (changed) {
            update();
        }        
    }
 
    protected void update() {
        
        final URL[] urls;

        synchronized (layers) {
            urls = layers.toArray(new URL[layers.size()]);
        }

        final FileSystem[] fileSystems = new FileSystem[urls.length];

        for (int i = 0; i < fileSystems.length; i++) {
            try {
                fileSystems[i] = new XMLFileSystem(urls[i]);

            } catch (SAXException ex) {
                throw new IllegalArgumentException("Error reading layers", ex);
            }
        }

        INSTANCE.setDelegates(fileSystems);
        
    }
}

