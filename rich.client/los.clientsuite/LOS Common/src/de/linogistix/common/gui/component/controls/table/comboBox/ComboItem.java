/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls.table.comboBox;

/**
 *
 * @author artur
 */
public class ComboItem implements CanEnable {

    Object obj;
    boolean isEnable;

    public ComboItem(Object obj, boolean isEnable) {
        this.obj = obj;
        this.isEnable = isEnable;
    }

    public ComboItem(Object obj) {
        this(obj, true);
    }

    public boolean isEnabled() {
        return isEnable;
    }

    public void setEnabled(boolean isEnable) {
        this.isEnable = isEnable;
    }

    public String toString() {
        return obj.toString();
    }
}


