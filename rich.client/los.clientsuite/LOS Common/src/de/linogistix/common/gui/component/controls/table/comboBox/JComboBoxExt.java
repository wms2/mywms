/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls.table.comboBox;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author artur
 */
public class JComboBoxExt extends JComboBox {
    
    public JComboBoxExt() {
    super();
       
    }

    public JComboBoxExt(Object[] items) {
        super(items);
    setRenderer(new ComboRenderer());
    addActionListener(new ComboListener(this));        
    }
    
  class ComboRenderer extends JLabel implements ListCellRenderer {

      
      
      
    public ComboRenderer() {
      setOpaque(true);
      setBorder(new EmptyBorder(1, 1, 1, 1));
    }

    public Component getListCellRendererComponent( JList list, 
           Object value, int index, boolean isSelected, boolean cellHasFocus) {
      if (isSelected) {
        setBackground(list.getSelectionBackground());
        setForeground(list.getSelectionForeground());
      } else {
        setBackground(list.getBackground());
        setForeground(list.getForeground());
      }	
      if (value instanceof CanEnable )
      if (! ((CanEnable)value).isEnabled()) {
        setBackground(list.getBackground());
//        setForeground(UIManager.getColor("Label.disabledForeground"));
          setForeground(UIManager.getColor("Label.Foreground"));
      }
      setFont(list.getFont());
      setText((value == null) ? "" : value.toString());
      return this;
    }  
  }
  
  class ComboListener implements ActionListener {
    JComboBox combo;
    Object currentItem;
    
    ComboListener(JComboBox combo) {
      this.combo  = combo;
      combo.setSelectedIndex(0);
      currentItem = combo.getSelectedItem();
    }
    
    public void actionPerformed(ActionEvent e) {
      Object tempItem = combo.getSelectedItem();
        if (tempItem instanceof CanEnable )
      if (! ((CanEnable)tempItem).isEnabled()) {
        combo.setSelectedItem(currentItem);
      } else {
        currentItem = tempItem;
      }
    }
  }
  
}    
    

