/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls.table.multiLineTable;

import de.linogistix.common.gui.component.controls.table.radioButton.RadioButtonTableModel;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

public class CustomTextFieldRenderer extends CustomTextField implements TableCellRenderer {

    Color newColor = new Color(235, 235, 235);
    CalculateWarp ca = new CalculateWarp();

    public CustomTextFieldRenderer() {
        setOpaque(true);
        this.setBorder(null);
        this.setEditable(false);
    }

    private void defaultLookAndFeel(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(((row & 1) == 1
                    ? UIManager.getColor("Table.background")
                    : newColor));
            setForeground(((row & 1) == 0
                    ? UIManager.getColor("Table.foreground")
                    : UIManager.getColor("Table.foreground")));
        }
    }

    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {
        this.setFont(table.getFont());
        defaultLookAndFeel(table, value, isSelected, hasFocus, row, column);
        value = ca.deleteReturns(value.toString());
        ca.calculate(table, value.toString(), column);
        if (column == RadioButtonTableModel.DESCRIPTION) {
            if (table.getRowHeight(row) != ca.getHeight()) {
                table.setRowHeight(row, ca.getHeight());
            }
        }
        setText((value == null)
                ? ""
                : ca.getString());
        return this;

    }
}

