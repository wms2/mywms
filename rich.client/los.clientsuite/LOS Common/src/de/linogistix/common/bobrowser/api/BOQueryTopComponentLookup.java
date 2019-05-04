/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.api;

import de.linogistix.common.bobrowser.bo.BONode;
import de.linogistix.common.bobrowser.query.BOQueryTopComponent;

/**
 *
 * @author trautm
 */
public interface BOQueryTopComponentLookup {
    
    /**
     * Finds an instance of BOQueryTopComponent for given BONode.
     * @param node
     * @return
     */
    public BOQueryTopComponent findInstance(BONode node);
        
}
