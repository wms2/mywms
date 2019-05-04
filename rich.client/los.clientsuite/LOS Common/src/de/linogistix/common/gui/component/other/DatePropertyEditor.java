/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.other;

/*
 * DataPropertyEditor.java
 *
 * Created on Jul 26, 2007, 9:05:44 AM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.jdesktop.swingx.calendar.DateSpan;
import org.jdesktop.swingx.calendar.JXMonthView;
import org.openide.util.Exceptions;

/**
 *
 * @author artur
 */
public class DatePropertyEditor extends PropertyEditorSupport {

    private Date d = new Date();

    public DatePropertyEditor() {

        this.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
            }
         });
        _monthView.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                DateSpan span = _monthView.getSelectedDateSpan();
                d = new Date(span.getStart());
                //info : if dialog will be closed with cancel or x, this setting will be ignore
                setValue(d);
            }
        });
        _monthView.setTraversable(true);
    }

    public String getAsText() {       
        d = (Date)getValue();
        if (d == null) {
            return null;
        }
//        return new SimpleDateFormat("dd.MM.yy HH:mm:ss").format(d);
        return new SimpleDateFormat("dd.MM.yyyy").format(d);        
    }
    

    public void setAsText(String s) {       
        try {
//            setValue(new SimpleDateFormat("dd.MM.yy HH:mm:ss").parse(s));
            setValue(new SimpleDateFormat("dd.MM.yyyy").parse(s));            
        } catch (ParseException pe) {
//            IllegalArgumentException iae = new IllegalArgumentException("Could not parse date");
//            throw iae;
        }
    }

    @Override
    public boolean supportsCustomEditor() {
        //return super.supportsCustomEditor();
        return true;
    }
    JXMonthView _monthView = new JXMonthView();

    @Override
    public Component getCustomEditor() {
        return _monthView;
    }
}
