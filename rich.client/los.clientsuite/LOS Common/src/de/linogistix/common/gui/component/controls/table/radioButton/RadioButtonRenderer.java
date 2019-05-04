/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls.table.radioButton;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

class RadioButtonRenderer extends DefaultTableCellRenderer {

    Color newColor = new Color(235, 235, 235);
    int alignment = RIGHT;
    
    public RadioButtonRenderer() {

     }
    
    public RadioButtonRenderer(int alignment) {
        this.alignment = alignment;
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof JRadioButton) {            
            super.getTableCellRendererComponent(table, value, isSelected,
                    hasFocus, row, column);
            //Left,Center,Right orientation in the jtable

            ((JRadioButton) value).setHorizontalAlignment(alignment);            
            setOpaque(true);
            if (isSelected) {
                ((JRadioButton) value).setBackground(table.getSelectionBackground());
                ((JRadioButton) value).setForeground(table.getSelectionForeground());
            } else {
/*                ((JRadioButton) value).setBackground(((row & 1) == 1
                        ? UIManager.getColor("Table.background")
                        : newColor));*/
                ((JRadioButton) value).setBackground(UIManager.getColor("Table.background"));                
                ((JRadioButton) value).setForeground(UIManager.getColor("Table.foreground"));
            }
            
            return (Component) value;
        }
        super.getTableCellRendererComponent(table, value, isSelected,
                hasFocus, row, column);
        return this;
                    

    }
}
