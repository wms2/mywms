/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.other;

import de.linogistix.common.gui.listener.DocumentChangeListener;
import java.awt.Toolkit;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import org.openide.util.Exceptions;

public class NumericDocument extends PlainDocument {
    //Variables
    private int limit = Integer.MAX_VALUE;
    protected String NUMBERS = "0123456789.";
    private DocumentChangeListener documentChangeListener = null;
    private JTextComponent textField = null;
    //Constructor
    public NumericDocument() {
        super();
    }

    public NumericDocument(int limit) {
        super();
        this.limit = limit;
    }

    private boolean isNumeric(String str) {
        if (NUMBERS.indexOf(str) != -1) {
            return true;
        } else {
            return false;
        }
    }
    
    public void setSpecialCharacter(String specialCharacter) {
        NUMBERS = NUMBERS + specialCharacter;
    }

    public void setDocumentChangeListener(DocumentChangeListener documentChangeListener, JTextComponent textField) {
        this.documentChangeListener = documentChangeListener;
        this.textField = textField;
    }
    //Insert string method
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {

        if (str != null) {
//              System.out.println("insertString "+str);
            for (int i = 0; i < str.length(); i++) {
                String tmpStr = String.valueOf(str.charAt(i));
                if (isNumeric(tmpStr) == false || (getLength() + str.length()) > limit) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
            }
            //All is fine, so add the character to the text box
            super.insertString(offset, str, attr);

        }

        return;

    }

    @Override
    protected void postRemoveUpdate(AbstractDocument.DefaultDocumentEvent chng) {
        super.postRemoveUpdate(chng);
        try {
            onTextChanged(chng.getDocument().getText(0, chng.getDocument().getLength()));
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    protected void insertUpdate(AbstractDocument.DefaultDocumentEvent chng, AttributeSet attr) {
        super.insertUpdate(chng, attr);
        try {
            onTextChanged(chng.getDocument().getText(0, chng.getDocument().getLength()));

        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void onTextChanged(String text) {
        if (documentChangeListener != null) {
            documentChangeListener.onChanged(textField);
        }
    }
}