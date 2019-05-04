/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.bo.editor;

import de.linogistix.los.entityservice.BusinessObjectLock;
import java.util.MissingResourceException;
import java.util.logging.Logger;
import org.openide.util.NbBundle;

/**
 * For use in Comboboxes 
 * 
 * @author trautm
 */
public class BOLockEntry {
    
    private static final Logger log = Logger.getLogger(BOLockEntry.class.getName());
    
    BusinessObjectLock lock;
    
    public BOLockEntry(BusinessObjectLock l){
        this.lock = l;
    }

    public BusinessObjectLock getBOLock(){
        return lock;
    }
    
    @Override
    public String toString() {
        try {
              String s = NbBundle.getMessage(lock.getBundleResolver(), lock.getMessageKey());
              return s;
            }
            catch( MissingResourceException e ) {
                return lock.getMessage();
//                log.warning("Can't resolve key: " + lock.getMessageKey() + ", BundleResolver="+lock.getBundleResolver().getName()+" ->" + e.getMessage());
//                return lock.toString();
            }
    }

}
