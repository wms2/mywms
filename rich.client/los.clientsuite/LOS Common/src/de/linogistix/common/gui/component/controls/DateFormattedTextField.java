/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import org.openide.util.Exceptions;

/**
 *
 * @author artur
 */
public class DateFormattedTextField extends JFormattedTextField implements FocusListener {

    private MaskFormatter getMaskFormat() {
        try {
            MaskFormatter dateFormat = new MaskFormatter("##.##.####");

            return dateFormat;
        } catch (ParseException pe) {
        }
        return null;
    }

    public DateFormattedTextField() {
        super();
        setFormatter(getMaskFormat());
        postInit();
    }

    private void postInit() {
        ActionListener al = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
            }
        };
        addActionListener(al);
        addFocusListener(this);
    }

    public void focusGained(FocusEvent e) {
    }

    /**
     * Reset the field to default value
     */
    public void reformat() {
        MaskFormatter mf;
        try {
            setValue(""); // if the value is on then formatting won't occur, haven't figured it why but it might have something to do with below commented method call.
            mf = new MaskFormatter("##.##.####"); // your mask format as the old example
            DefaultFormatterFactory factory = new DefaultFormatterFactory(mf); // here is the change, you transform the formatter to a factory.
            setFormatterFactory(factory); // and reset the text field with that!           
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
    }

    public void focusLost(FocusEvent e) {
        if (isValidDate() == false) {
            reformat();
        }
    }

    private boolean isValidDate(SimpleDateFormat dateFormat) {
        String inDate = getText();
        if (inDate == null) {
            return false;
        }
        //set the format to use as a constructor argument
        if (inDate.trim().length() != dateFormat.toPattern().length()) {
            return false;
        }
        dateFormat.setLenient(false);
        try {
            //parse the inDate parameter
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    public boolean isValidDate() {
        SimpleDateFormat dateFormatFull = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat dateFormatHalf = new SimpleDateFormat("dd.MM.yy");
        if ((isValidDate(dateFormatFull) == false) && (isValidDate(dateFormatHalf) == false)) {
            return false;
        }
        return true;
    }

    /**
     * 
     * @return It returns null if wrong date or no date would be set
     * @throws java.text.ParseException
     */
    public Date getDate() {
        try {
            if (isValidDate() == false) {
                return null;
            }
            String dateText = getText();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
            dateFormat.setLenient(false);
            if (dateText.trim().length() == dateFormat.toPattern().length()) {
                return dateFormat.parse(dateText);
            }
            dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            if (dateText.trim().length() == dateFormat.toPattern().length()) {
                return dateFormat.parse(dateText);
            }
        } catch (ParseException e) {
        }
        return null;
    }

    /*    static java.util.Date parseDate(String strDate) {
    SimpleDateFormat df4 = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy");
    
    java.util.Date dOut = null;
    df2.setLenient(false);
    df4.setLenient(false);
    try {
    dOut = df2.parse(strDate);
    } catch (ParseException e) {
    try {
    dOut = df4.parse(strDate);
    } catch (ParseException ex) {
    Exceptions.printStackTrace(ex);
    }
    }
    return dOut;
    }*/
}
