/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.query.gui.component;

/**
 * Listens to changes in {@link BOQueryComponentProvider}
 * @author trautm
 */
public interface ProviderChangeEventListener {
    
    /**
     * This component has been selected
     * @param prov
     */
    void providerSelected(BOQueryComponentProvider prov);

    /**
     * A search string changed. please update result set
     * @param prov
     * @param searchStr
     */
    void searchStringChanged(BOQueryComponentProvider prov, String searchStr);

    /**
     *  please update result set
     */
    void reloadRequest();
    
}
