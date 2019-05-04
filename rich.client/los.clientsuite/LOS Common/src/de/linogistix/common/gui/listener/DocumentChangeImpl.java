/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.listener;

import de.linogistix.common.gui.component.other.NumericDocument;
import java.awt.Component;
import java.awt.Container;
import javax.swing.JPanel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import org.openide.util.Exceptions;

/**
 *
 * @author artur
 */
public class DocumentChangeImpl {

    public void setCallbackListener(DocumentChangeListener documentChangeListener, JTextComponent[] textField, boolean numeric, int limit) {
        for (JTextComponent n : textField) {
            setCallbackListener(documentChangeListener, n, numeric, limit);
        }
    }

    public void setCallbackListener(DocumentChangeListener documentChangeListener, JTextComponent[] textField) {
        setCallbackListener(documentChangeListener, textField, false, 65000);
    }

    public void setCallbackListener(DocumentChangeListener documentChangeListener, Container comp, boolean numeric, int limit) {
        addDocumentChangeListener(comp, documentChangeListener, numeric, limit);
    }

    public void setCallbackListener(DocumentChangeListener documentChangeListener, Container comp) {
        addDocumentChangeListener(comp, documentChangeListener, false, 65000);
    }

    public void addDocumentChangeListener(Container aTop, DocumentChangeListener listener, boolean numeric, int limit) {
        if (aTop != null) {
            final Component[] comp = aTop.getComponents();
            for (int i = 0; i < comp.length; i++) {
                if (comp[i].isFocusable()) {
                    if (comp[i] instanceof JTextComponent) {
                        setCallbackListener(listener, (JTextComponent) comp[i], numeric, limit);
                    }
                }
                if (comp[i] instanceof JPanel) {
                    addDocumentChangeListener((JPanel) comp[i], listener, numeric, limit);
                }
            }
        }
    }

    public void setCallbackListener(final DocumentChangeListener documentChangeListener, final JTextComponent textField, boolean numeric, int limit) {
        if (numeric) {
            NumericDocument doc = new NumericDocument(limit);
            doc.setDocumentChangeListener(documentChangeListener, textField);
            setCallbackListener(documentChangeListener, textField, doc, limit);
        } else {
            setCallbackListener(documentChangeListener, textField, null, limit);
        }
    }
    
    public void setCallbackListener(final DocumentChangeListener documentChangeListener, final JTextComponent textField, Document document) {
        setCallbackListener(documentChangeListener, textField, document, 65000);        
    }
    
    public void setCallbackListener(final DocumentChangeListener documentChangeListener, final JTextComponent textField, Document document, int limit) {
        if (document != null) {
            if (document instanceof NumericDocument) {
                textField.setDocument(document);
                ((NumericDocument)document).setDocumentChangeListener(documentChangeListener, textField);
            }    
        } else {
            textField.setDocument(new PlainDocument() {

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
                    documentChangeListener.onChanged(textField);
                }
            });
        }

    }
}
