/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls.table.radioButton;

import de.linogistix.common.gui.component.controls.table.multiLineTable.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;




public class RadioButtonTableModel extends DefaultTableModel {

    public final static int OPTION = 0;
    public final static int DESCRIPTION = 1;
    public final static String OPTION_LABEL = "ID";
    public final static String DESCRIPTION_LABEL = "Description";

    public RadioButtonTableModel() {
        super();
    }

    /**
     * Use setRadioButtonRenderer instate
     * @param table
     * @deprecated
     */
    @Deprecated
    public void setCustomColumnSize(JTable table) {
        table.getColumnModel().getColumn(0).setPreferredWidth(25);
        table.getColumnModel().getColumn(0).setMinWidth(25);
        table.getColumnModel().getColumn(0).setMaxWidth(25);
        TableCellRenderer descriptionRenderer = new CustomTextFieldRenderer();
        table.getColumn(DESCRIPTION_LABEL).setCellRenderer(descriptionRenderer);
        table.getColumn(OPTION_LABEL).setCellRenderer(
                new RadioButtonRenderer());
    }

    //DefaultTableCellRenderer.LEFT, CENTER, RIGHT orientation in the jtable
    public void setRadioButtonRenderer(JTable table,int alignment,int width) {        
        table.getColumnModel().getColumn(0).setMinWidth(width);
        table.getColumnModel().getColumn(0).setMaxWidth(width);        
        table.getColumnModel().getColumn(0).setPreferredWidth(width);
        table.getColumnModel().getColumn(0).setCellRenderer(
                new RadioButtonRenderer(alignment));        
    }
    
    
    public RadioButtonTableModel(int numRows, int numColumns) {
        super(numRows, numColumns);
    }

    public RadioButtonTableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
    }

    public RadioButtonTableModel(Object[] columnNames, int numRows) {
        super(columnNames, numRows);
    }

    public RadioButtonTableModel(Vector columnNames, int numRows) {

    }

    public RadioButtonTableModel(Vector data, Vector columnNames) {
        super(data, columnNames);
    }

    public Object getValueAt(int row, int col) {
        return super.getValueAt(row, col);
    }

    public void setValueAt(Object aValue, int row, int column) {
        super.setValueAt(aValue, row, column);
        fireTableCellUpdated(row, column);
    }

    public boolean isCellEditable(int row, int col) {
        return false;
    }

    public void addTableModelListener(TableModelListener my) {
        super.addTableModelListener(my);
    }
}
