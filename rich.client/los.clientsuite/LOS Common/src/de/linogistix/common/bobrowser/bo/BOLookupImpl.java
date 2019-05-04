/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.bobrowser.bo;

import de.linogistix.common.bobrowser.api.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 *Looks up an instance of BO corresponding to a given class of BasicEntity.
 * 
 * @author trautm
 */
public class BOLookupImpl implements BOLookup {

    static Map<Class, BO> lookupMap = new HashMap<Class, BO>();
    
    private static final Logger log = Logger.getLogger(BOLookupImpl.class.getName());

    public Object lookup(Class c) {

        synchronized (lookupMap) {

            Object ret;

            if (c == null) {
                return null;
            }

            ret = lookupMap.get(c);

            if (ret != null) {
                return ret;
            } else {
                //try to probe for subclases/superclasses
                for (Class key : lookupMap.keySet()) {
//                            log.info("is " + key.getName() + " is assignable from " + c.getName() + "?");
                    if (c.isAssignableFrom(key)) {
//                                  log.info("... yes!");
                        return lookupMap.get(key);
                    } else {
//                                  log.info("... no!");
                    }
                }
            }
        }
        log.warning("No BO found for class " + c.getName());
        return null;
    }

    public void addBO(Class c, BO bo) {
        synchronized (lookupMap) {
            lookupMap.put(c, bo);
        }
    }

    public void removeBO(Class c) {
        synchronized (lookupMap) {
            lookupMap.remove(c);
        }
    }

    public Collection<BO> getBOs() {
        return lookupMap.values();
    }

}
