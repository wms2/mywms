/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls.table;

import de.linogistix.common.gui.component.controls.table.comboBox.EachRowEditor;
import de.linogistix.common.gui.component.controls.table.comboBox.JComboBoxExt;
import java.util.HashMap;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 *
 * @author artur
 */
public class JTableExt extends JTable {

    HashMap<Integer, TableObject> valueEditingHashMap = new HashMap<Integer, TableObject>();
    HashMap<Integer, TableObject> columnEditingHashMap = new HashMap<Integer, TableObject>();
    boolean radioButtonSingleColumnSelection = false;
    EachRowEditor rowEditor;


    public JTableExt() {
        super();
        addTableSetSelectionListener();
        rowEditor = new EachRowEditor(this);        
    }

    public JTableExt(boolean radioButtonSingleColumnSelection) {
        super();
        this.radioButtonSingleColumnSelection = radioButtonSingleColumnSelection;
        addTableSetSelectionListener();
        rowEditor = new EachRowEditor(this);        
    }
    
    private EachRowEditor getEachRowEditor() {
        return rowEditor;
    }
    
    public void addComboBox(int row, int column, Object[] items) {
        javax.swing.JComboBox comboBox = new JComboBoxExt(items);
        //Make sure that the JComboBox Renderer will be set
        setValueAt(comboBox, row, column);        
        //Adding Editor
        getEachRowEditor().setEditorAt(row, new javax.swing.DefaultCellEditor(comboBox));
//        rowEditor.setEditorAt(7, new javax.swing.DefaultCellEditor(comboBox2));
        getColumnModel().getColumn(column).setCellEditor(getEachRowEditor());        
    }

    public TableCellRenderer getCellRenderer(int row, int column) {
        TableColumn tableColumn = getColumnModel().getColumn(column);
        TableCellRenderer renderer = tableColumn.getCellRenderer();
        if (renderer == null) {
            Class c = getColumnClass(column);
            if (c.equals(Object.class)) {
                Object o = getValueAt(row, column);
                if (o != null) {
                    c = getValueAt(row, column).getClass();
                }
            }
            renderer = getDefaultRenderer(c);
        }
        return renderer;
    }

    public TableCellEditor getCellEditor(int row, int column) {
        TableColumn tableColumn = getColumnModel().getColumn(column);
        TableCellEditor editor = tableColumn.getCellEditor();
        if (editor == null) {
            Class c = getColumnClass(column);
            if (c.equals(Object.class)) {
                Object o = getValueAt(row, column);
                if (o != null) {
                    c = getValueAt(row, column).getClass();
                }
            }
            editor = getDefaultEditor(c);
        }
        return editor;
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        if (valueEditingHashMap.isEmpty() || (TableObject) valueEditingHashMap.get(new Integer(column)) == null) {
            super.setValueAt(aValue, row, column);
        } else {
            TableObject eo = (TableObject) valueEditingHashMap.get(new Integer(column));
            if (eo.enable) {
                super.setValueAt(aValue, row, column);
            }
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (columnEditingHashMap.isEmpty() || (TableObject) columnEditingHashMap.get(new Integer(column)) == null) {
            return true;
        } else {
            TableObject eo = (TableObject) columnEditingHashMap.get(new Integer(column));
            return eo.enable;
        }
    }

    /**
     * To prevent manipulate text by a component which was adding to a cell. e.g. JComboBox
     * @param enable
     */
    public void setValueEditing(boolean enable, int column) {
        valueEditingHashMap.put(new Integer(column), new TableObject(enable, column));
    }

    public void setCellEditable(boolean enable, int column) {
        columnEditingHashMap.put(new Integer(column), new TableObject(enable, column));
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        super.valueChanged(e);
//        processSelection();
    }
    
    

    public void setCenterHeader() {
        ((DefaultTableCellRenderer) getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
    //alternativ
    //table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }


    private void addTableSetSelectionListener() {

        getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                tableSelectionChangedListener(e);
            }
        });
        getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                tableSelectionChangedListener(e);
            }
        });
    }
    
    
    private void processRadioButtonSelection(int row) {
        int count = getRowCount();
        for (int i = 0; i < count; i++) {
            if (getModel().getValueAt(i, 0) instanceof JRadioButton) {
                JRadioButton radio = (JRadioButton) getModel().getValueAt(i, 0);
                if (radio != null) {
                    if (i != row) {
                        radio.setSelected(false);

                    } else {
                        radio.setSelected(true);
                    }
                }
            }
        }
        repaint();
    }

    private void tableSelectionChangedListener(ListSelectionEvent e) {
        int row = getSelectedRow();
        int column = getSelectedColumn();
        if (radioButtonSingleColumnSelection) {
            if (column == 0) {
                processRadioButtonSelection(row);
            }
        } else {
            processRadioButtonSelection(row);
        }
    }

    class TableObject {

        boolean enable = true;
        int column;

        public TableObject(boolean enable, int column) {
            this.enable = enable;
            this.column = column;
        }
    }
}
