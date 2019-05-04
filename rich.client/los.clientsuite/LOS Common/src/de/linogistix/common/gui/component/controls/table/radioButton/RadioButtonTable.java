/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls.table.radioButton;

import java.util.Vector;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 *
 * @author artur
 */
public class RadioButtonTable extends JTable {

    //Only select if the radioButton Column will pressed

    @Override
    public void valueChanged(ListSelectionEvent e) {
        super.valueChanged(e);
        processSelection(getSelectedRow());
    }

    public RadioButtonTable(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
    }


    public RadioButtonTable(Vector rowData, Vector columnNames) {
        super(rowData, columnNames);
    }

    public RadioButtonTable(int numRows, int numColumns) {
        super(numRows, numColumns);
    }

    public RadioButtonTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
    }

    public RadioButtonTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
    }

    public RadioButtonTable(TableModel dm) {
        super(dm);
    }

    private void disableEnableAllRadioButtons(int row) {
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
    }

    private void processSelection(int row) {
        disableEnableAllRadioButtons(row);
    }
}
