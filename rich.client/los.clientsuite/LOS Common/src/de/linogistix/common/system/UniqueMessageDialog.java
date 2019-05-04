/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.system;

import de.linogistix.common.util.ExceptionAnnotator;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author artur
 */
public class UniqueMessageDialog {

    private static UniqueMessageDialog instance = null;
    private boolean visible;

    /** Creates a new instance of GraphicUtil */
    private UniqueMessageDialog() {
    // Exists only to defeat instantiation.
    }

    public synchronized static UniqueMessageDialog getInstance() {
        if (instance == null) {
            instance = new UniqueMessageDialog();
        }
        return instance;
    }

    public void show(Throwable t) {
        new MessageThread(t);
    }
    
    public void show(String message) {
        new MessageThread(message).start();
    }
    
    private boolean isVisible() {
        return visible;
    }

    private void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    class MessageThread extends Thread {
        Throwable t;
        String message;

        public MessageThread(Throwable t) {
            this.t = t;
        }
     
        public MessageThread(String message) {
            this.message = message;
        }

        
        public void run() {
            if (isVisible() == false) {
                setVisible(true);
                NotifyDescriptor exNd;
                if (t != null) {
                    exNd = new NotifyDescriptor.Message(ExceptionAnnotator.resolveMessage(t), NotifyDescriptor.ERROR_MESSAGE);
                } else if (message != null) {
                    exNd = new NotifyDescriptor.Message(message, NotifyDescriptor.ERROR_MESSAGE);                
                } else {
                    exNd = new NotifyDescriptor.Message("unknown message", NotifyDescriptor.ERROR_MESSAGE);                                    
                }                     
                DialogDisplayer.getDefault().notify(exNd);
                setVisible(false);
            }
        }
    }
    
    
}
