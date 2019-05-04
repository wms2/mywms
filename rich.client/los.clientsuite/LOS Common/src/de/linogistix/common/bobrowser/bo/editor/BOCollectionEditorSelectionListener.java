/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.bo.editor;

import de.linogistix.common.exception.VetoException;
import de.linogistix.los.query.BODTO;

/**
 *
 * @author Jordan
 */
public interface BOCollectionEditorSelectionListener {

    public void addSelection(BODTO selectedValue) throws VetoException;
    
    public void removeSelection(BODTO selectedValue) throws VetoException;
}
