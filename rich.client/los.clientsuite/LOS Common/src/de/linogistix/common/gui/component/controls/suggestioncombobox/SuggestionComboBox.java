/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls.suggestioncombobox;

import de.linogistix.common.gui.component.controls.*;
import de.linogistix.common.gui.object.IconType;
import de.linogistix.common.gui.object.LOSAutoFilteringComboBoxNode;
import de.linogistix.common.res.CommonBundleResolver;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Iterator;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import org.openide.util.NbBundle;

/**
 *
 * @author artur
 */
public class SuggestionComboBox extends JComboBox {

    private javax.swing.Timer timer;
    private CommonObject co = new CommonObject();
    private UIPanel panel;
    private UIComboBoxEditor comboEditor;
    
    ActionListener taskPerformer = new ActionListener() {

        public void actionPerformed(ActionEvent evt) {
            if (isValidKey(co.getKeyEvent())) {
                co.getKeyListener().keyPressed(co.getKeyEvent());
                if (co.isPopupPressed() == false) {
                    setKeyHandle(co.getKeyEvent());
                }
            }
            co.setAllowSetText(false);
            co.setPopupPressed(false);
        }
    };

    /** Creates a new instance of AutofilteringComboBox */
    public SuggestionComboBox() {
        panel = new UIPanel(co, this);
        comboEditor = new UIComboBoxEditor(co, panel);
        setEditor(comboEditor);
        setEditable(true);

        //by choosing an item by the mouse from the popup, 
        //the callback mechanism will be called 
        addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if ((evt.getModifiers() & evt.MOUSE_EVENT_MASK) != 0) {
                    callTimer();
                }
            }
        });
        setUI(new UI(co));
    }

    public void restartTimer() {
        timer.restart();
    }

    @Override
    public boolean isPopupVisible() {
        return super.isPopupVisible();
    }

    @Override
    public void setPopupVisible(boolean visible) {
        co.setAllowPopup(true);
        super.setPopupVisible(visible);
        co.setAllowPopup(false);
    }

    /**
     * send a custom Event
     */
    private void callTimer() {
        if (co.getKeyListener() != null) {
            co.setPopupPressed(true);
            co.setAllowSetText(true);
            KeyEvent k = new KeyEvent((JTextField) ((UIPanel) getEditor().getEditorComponent()).textField, KeyEvent.KEY_PRESSED, 0, 0, 0, KeyEvent.CHAR_UNDEFINED);
            co.setKeyEvent(k);
            timer.restart();
        }
    }

    public void setCallbackListener(KeyListener keyCallbackListener) {
        co.setKeyListener(keyCallbackListener);
        timer = new javax.swing.Timer(co.getDelay(), taskPerformer);
        timer.setRepeats(false);
    }

    public void setCallbackListener(KeyListener keyCallbackListener, int delay) {
        co.setKeyListener(keyCallbackListener);
        co.setDelay(delay);
        timer = new javax.swing.Timer(delay, taskPerformer);
    }

    /**
     * Check if the entry exist in the combobox at first position. If yes it gives true back
     * @return if item exist the result will be true else false
     */
    public boolean isValid() {
        if (getModel() != null) {
            if (getModel().getSize() > 0) {
                if ((getText().equals(((LOSAutoFilteringComboBoxNode) getItemAt(0)).toString().trim()))) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean startsWith() {
        if (getModel() != null) {
            if (getModel().getSize() > 0) {
                if (((LOSAutoFilteringComboBoxNode) getItemAt(0)).toString().trim().startsWith(getText())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Set the Error to the given Label if not matching with the user entry
     * @param label
     */
    public void setMatchError(LosLabel label) {
        setMatchError(label, "Entry does not exist");
    }

    /**
     * 
     * @param label
     * @param errorKey The key for the Bundle.properties
     */
    public void setMatchError(final LosLabel label, final String errorKey) {
        if (getText().equals("")) {
            label.setText();
        } else if (isValid() == false) {
            label.setHiddenText(NbBundle.getMessage(CommonBundleResolver.class, errorKey), IconType.ERROR);
            if (startsWith() == false) {
                label.setText(NbBundle.getMessage(CommonBundleResolver.class, errorKey), IconType.ERROR);
            }
        }
    }

    public boolean isEmpty() {
        if (getText().equals("")) {
            return true;
        }
        return false;
    }

    /**
     * Set items to combobox without chance the textfield editor
     * @param items array of items to set
     */
    public void addItems(List items) {
        co.setEditing(false);
        removeAllItems();
        Iterator<String> iter = items.iterator();
        while (iter.hasNext()) {
            super.addItem(iter.next());
        }
        setSelectedIndex(-1);
        co.setEditing(true);
    }

    public void addItem(Object item) {
        co.setEditing(false);
        super.addItem(item);
        setSelectedIndex(-1);
        co.setEditing(true);
    }

    private boolean isValidKey(KeyEvent e) {
        if ((e.getKeyCode() != KeyEvent.VK_DOWN) &&
                (e.getKeyCode() != KeyEvent.VK_UP) &&
                (e.getKeyCode() != KeyEvent.VK_LEFT) &&
                (e.getKeyCode() != KeyEvent.VK_END) &&
                (e.getKeyCode() != KeyEvent.VK_BEGIN) &&
                (e.getKeyCode() != KeyEvent.VK_RIGHT)) {
            return true;
        }
        return false;
    }

    public void setKeyHandle(KeyEvent e) {
        JTextField text = ((UIPanel) getEditor().getEditorComponent()).textField;
        if (isValidKey(e)) {
            //set the momentan selecting entry to the textfield editor
            if ((e.getKeyCode() == KeyEvent.VK_ENTER)) {
                if (getSelectedItem() != null) {
                    text.setText(getSelectedItem().toString());
                }
            } else {
                if (isDisplayable()) {
                    if ((text.getText().equals("") || (getModel().getSize()) == 0)) {
                        if (text.getText().equals("")) {
                            removeAllItems();
                        }
                        setPopupVisible(false);
                    } else {
                        //necessary for (re)calcute the height for the popup
                        setPopupVisible(false);
                        setPopupVisible(true);
                    }
                }
                //disable all selection in the combobox        
                setSelectedIndex(-1);
            }
        }
    }

    public String getText() {
        return ((UIPanel) getEditor().getEditorComponent()).textField.getText();
    }

    public void setText(String text) {
        ((UIPanel) getEditor().getEditorComponent()).textField.setText(text);
    }

    @Override
    public void removeAllItems() {
        super.removeAllItems();
    }

    public void removeAllItems(boolean clearTextField) {
        if (clearTextField == false) {
            co.setEditing(false);
            super.removeAllItems();
            co.setEditing(true);
        } else {
            super.removeAllItems();
        }
    }
}