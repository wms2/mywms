/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls;

/**
 *
 * @author krane
 */
public interface LOSTextFieldListener {
    /**
     * Check the Input of a LOSTextField. It is called by the KeyListener.
     * @param value
     * @return
     */
    public boolean checkValue(String value, LosLabel label);
}
