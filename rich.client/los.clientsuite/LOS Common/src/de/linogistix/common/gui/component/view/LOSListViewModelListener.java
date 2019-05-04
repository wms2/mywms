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

/**
 *
 * @author Jordan
 */
public interface LOSListViewModelListener {

    public void modelChanged();
    
    public void modelRowsInserted(List<BODTO> insertedList);
    
    public void modelRowsDeleted(List<BODTO> deletedList);
}
