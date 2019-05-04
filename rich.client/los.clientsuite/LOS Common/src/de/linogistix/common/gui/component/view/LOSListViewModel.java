/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.view;

import de.linogistix.los.query.BODTO;
import java.util.List;
import org.mywms.model.BasicEntity;

/**
 *
 * @author Jordan
 */
public interface LOSListViewModel<E extends BasicEntity> {

    public List<BODTO<E>> getResultList();
    
    public void addModelListener(LOSListViewModelListener listener);
    
    public void fireModelChangedEvent();
    
    public void clear();
}
