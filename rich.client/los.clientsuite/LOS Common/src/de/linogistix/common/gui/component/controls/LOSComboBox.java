/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.openide.util.NbBundle;

/**
 *
 * @author Jordan
 */
public class LOSComboBox extends JPanel{

    private static final Logger log = Logger.getLogger(LOSComboBox.class.getName());

    private JComboBox myComboBox;
      
    private boolean i18n;
    
    private Class bundleResolver;

    private LosLabel textFieldLabel = new LosLabel();

     private List<PropertyChangeListener> changeListeners = new ArrayList<PropertyChangeListener>();

    
    public LOSComboBox(){
        
        setLayout(new GridBagLayout());
//        setBorder(new EmptyBorder(2, 2, 2, 2));
        myComboBox = new JComboBox();
        myComboBox.addPopupMenuListener(new PopupMenuListener() {

            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                fireItemEnteredEvent();
            }

            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });
            
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        add(textFieldLabel, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        add(myComboBox, gbc);
        
    }
    
    public void setTitle(String text){
        textFieldLabel.setTitleText(text);
    }
    
    public void addItem(Object item){
        myComboBox.addItem(item);
    }
    
    public Object getSelectedItem(){
        return myComboBox.getSelectedItem();
    }
    public void setSelectedItem(Object sel){
        myComboBox.setSelectedItem(sel);
    }
    public void removeAllItems() {
        myComboBox.removeAllItems();
    }
    
    public int getIndexOf(Object item){
        DefaultComboBoxModel model = (DefaultComboBoxModel) myComboBox.getModel();
        return model.getIndexOf(item);
    }

    public int getSelectedIndex(){
        return myComboBox.getSelectedIndex();
    }
    public void setSelectedIndex(int idx){
        myComboBox.setSelectedIndex(idx);
    }
    public int getItemCount() {
        return myComboBox.getItemCount();
    }
    public Object getItemAt(int i) {
        return myComboBox.getItemAt(i);
    }
    public void setEditable(boolean editable){
        myComboBox.setEditable(editable);
    }
    @Override
    public void setEnabled(boolean editable){
        myComboBox.setEnabled(editable);
    }

    public boolean isEditable(){
        return myComboBox.isEditable();
    }


     @Override
    public Font getFont(){
        if (myComboBox != null)
            return myComboBox.getFont();
        else
            return super.getFont();
    }

    @Override
    public void setFont(Font font){
        if (myComboBox != null)
            myComboBox.setFont(font);
    }

//    public void setPreferredComboboxSize(Dimension preferredSize) {
//        myComboBox.setPreferredSize(preferredSize);
//    }
//
//    public Dimension getPreferredComboboxSize() {
//        return myComboBox.getPreferredSize();
//    }

     /**
     * Registering a listener for the field.
     * It is called, when the input of the field is finished (focusLost).
     * @param PropertyChangeListener
     */
    public void addItemChangeListener(PropertyChangeListener l) {
        if(!changeListeners.contains(l))
            changeListeners.add(l);
    }
    public void removeItemChangedListener(PropertyChangeListener l) {
        changeListeners.remove(l);
    }
    
    public void fireItemEnteredEvent(){

        if (changeListeners.size() > 0) {
            PropertyChangeEvent pce = new PropertyChangeEvent(this, LOSComponentEvent.ITEM_ENTERED, null, myComboBox.getSelectedItem().toString());
            for (PropertyChangeListener p : changeListeners) {
                p.propertyChange(pce);
            }
        }
    }

    /**
     * @return the i18n
     */
    public boolean isI18n() {
        return i18n;
    }

    /**
     * @param i18n the i18n to set
     */
    public void setI18n(boolean i18n) {
        this.i18n = i18n;
    }

    /**
     * @return the bundleResolver
     */
    public Class getBundleResolver() {
        return bundleResolver;
    }

    /**
     * @param bundleResolver the bundleResolver to set
     */
    public void setBundleResolver(Class bundleResolver) {
        this.bundleResolver = bundleResolver;
    }

    /**
     * Call this method if you have set i18n to true and provided a bundleResolver class.
     * Enum types will be resolved using <code>NbBundle.getMessage(getBundleResolver(), enu.getClass().getSimpleName() + "." + enu.name())</code>
     * Other values will be resolved using
     */
    public void initI18N(){

        if (! isI18n())
            return;
        if (getBundleResolver() == null)
            return;

        myComboBox.setRenderer(new DefaultListCellRenderer() {
           @Override
           public Component getListCellRendererComponent(JList list,Object value, int index, boolean isSelected, boolean cellHasFocus) {
               super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
               try{
                   if (value.getClass().isEnum()) {
                       Enum enu = (Enum)value;
                       setText(NbBundle.getMessage(getBundleResolver(), enu.getClass().getSimpleName() + "." + enu.name()));
                   } else {
                       setText(NbBundle.getMessage(getBundleResolver(), value.toString()));
                   }
               } catch (Throwable t){

               }
               return this;
           }
       });

    }

    @Override
    public void requestFocus() {
        myComboBox.requestFocus();
    }

//    /**
//     * @return the myComboBox
//     */
//    public JComboBox getMyComboBox() {
//        return myComboBox;
//    }
//
//    public LosLabel getTextfieldLabel(){
//        return this.textFieldLabel;
//    }




}
