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
public interface LOSListViewSelectionListener {

    public void selectionChanged(List<BODTO> selectedEntities);
    
}
