/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls.suggestioncombobox;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.Timer;

/**
 *
 * @author artur
 */
public final class CommonObject {
    private int delay = 100;
    private KeyEvent keyEvent;
    private KeyListener keyListener;
    private boolean allowSetText = false;
    private boolean popupPressed = false;
    private boolean allowPopup;
    //flag for control the setText in the Textfield. 
    //removeAllItems, addItem.. clear elsewher the TextField field too.
    //Set it on false to forbidden the clear of the TextField field and only
    //to clear the combobox.
    private boolean editing = true;

    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }

    
    
    public boolean isAllowPopup() {
        return allowPopup;
    }

    public void setAllowPopup(boolean allowPopup) {
        this.allowPopup = allowPopup;
    }

    public boolean isAllowSetText() {
        return allowSetText;
    }

    public void setAllowSetText(boolean allowSetText) {
        this.allowSetText = allowSetText;
    }


    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public KeyListener getKeyListener() {
        return keyListener;
    }

    public void setKeyListener(KeyListener keyCallbackListener) {
        this.keyListener = keyCallbackListener;
    }

    public KeyEvent getKeyEvent() {
        return keyEvent;
    }

    public void setKeyEvent(KeyEvent keyEvent) {
        this.keyEvent = keyEvent;
    }

    public boolean isPopupPressed() {
        return popupPressed;
    }

    public void setPopupPressed(boolean popupPressed) {
        this.popupPressed = popupPressed;
    }
    
    
}
