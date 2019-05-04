/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.services;

import java.net.URL;
import org.openide.filesystems.MultiFileSystem;
import org.openide.filesystems.Repository;
import org.openide.filesystems.XMLFileSystem;
import org.xml.sax.SAXException;

/**
 *
 * @author artur
 */

public class LoginFileSystem extends MultiFileSystem {

    private static LoginFileSystem INSTANCE;

    public LoginFileSystem() {
        // let's create the filesystem empty, because the user
        // is not yet logged in
        INSTANCE = this;
        Repository.getDefault().addFileSystem(this);
        
    }

    public static void assignURL(URL u) throws SAXException {
        System.out.println("assignURL");
        INSTANCE.setDelegates(new XMLFileSystem(u));
    }   
}
