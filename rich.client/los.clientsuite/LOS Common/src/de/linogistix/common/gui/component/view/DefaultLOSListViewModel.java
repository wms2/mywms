/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.view;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jordan
 */
    public class DefaultLOSListViewModel implements LOSListViewModel{

    private List<LOSListViewModelListener> myListeners = new ArrayList<LOSListViewModelListener>();

    public List getResultList() {
        return new ArrayList();
    }

    public void addModelListener(LOSListViewModelListener listener) {
        myListeners.add(listener);
    }

    public void fireModelChangedEvent() {
        
        for(LOSListViewModelListener l:myListeners){
            l.modelChanged();
        }
    }
    
    public void clear(){
        
        fireModelChangedEvent();
    }
    
}
