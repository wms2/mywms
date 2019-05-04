/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls.table;

import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author artur
 */
public class JComboBoxCellRenderer implements TableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof JComboBox) {
            if (isSelected) {
                ((JComboBox) value).setBackground(table.getSelectionBackground());
                ((JComboBox) value).setForeground(table.getSelectionForeground());
            } else {
                ((JComboBox) value).setBackground(UIManager.getColor("Table.background"));
                ((JComboBox) value).setForeground(UIManager.getColor("Table.foreground"));
            }
        }
        return (JComponent) value;
    }
}
