/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.linogistix.common.gui.component.controls;

/**
 * Event names for events thrown by LOS components, e.g.
 * <ul>
 * <li> {@link LOSTextField}
 * <li> {@link  LOSComboBox}
 * </ul>
 * @author andreas
 */
public interface LOSComponentEvent {

    public final static String ITEM_CHANGED = "ItemChanged";

    public final static String ITEM_ENTERED = "ItemEntered";
    
}
