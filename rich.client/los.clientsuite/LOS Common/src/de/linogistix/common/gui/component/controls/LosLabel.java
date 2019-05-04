/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls;

import de.linogistix.common.gui.object.IconType;
import java.awt.Dimension;
import javax.swing.JLabel;
import org.openide.util.NbBundle;

/**
 *
 * @author artur
 */
public class LosLabel extends JLabel {

    private LabelObject labelObject = new LabelObject();
    private String title = null;

    private boolean showMandatoryFlag = false;
    
    public void setText(String message, IconType icon) {
        setText(message, icon, "");
    }

    public void setText(String key, Class bundle, IconType icon) {
        String message = NbBundle.getMessage(bundle, key);
        setText(message, icon, key);
    }

    public void setText(String message, IconType icon, String key) {
        labelObject.setMessage(message);
        labelObject.setIconType(icon);
        labelObject.setMessageKey(key);
        
        if (icon != null) {
            if (icon == IconType.ERROR) {
                setText("<HTML><nobr>" + getTitleText() + "&nbsp&nbsp<font color=red>&laquo; " + message + " &raquo</font></nobr></HTML>");
            } else if (icon == IconType.WARNING) {
                setText("<HTML><nobr>" + getTitleText() + "&nbsp&nbsp<font color=#FF6600>&laquo; " + message + " &raquo</font></nobr></HTML>");
            } else{
                setText("<HTML><nobr>" + getTitleText() + "&nbsp&nbsp<font>&laquo; " + message + " &raquo</font></nobr></HTML>");
            }
        }
    }

    public void setHiddenText(String key, Class bundle, IconType icon) {
        String message = NbBundle.getMessage(bundle, key);
        setHiddenText(message, icon, key);
    }

    public void setHiddenText(String message, IconType icon) {        
        setHiddenText(message, icon, "");
    }

    public void setHiddenText(String message, IconType icon, String messageKey) {
        labelObject.setMessage(message);
        labelObject.setIconType(icon);
        labelObject.setMessageKey(messageKey);
        labelObject.setVisible(false);
    }

    /**
     * After setting it will be by next call not setting again 
     * because in setText it will set the vaulue on labelObject.setVisible = true
     */
    public void showHiddenText() {
        if (labelObject.isVisible() == false) {
            setText(labelObject.message, labelObject.iconType, labelObject.messageKey);
        }
    }

    public void setText() {
        if( getTitleText() != null ) {
            resetLabelObject();
            setText("<HTML><nobr>" + getTitleText() + "</nobr></HTML>");
        }
    }

    public String getTitleText() {
        if(showMandatoryFlag){
            return "<font color=#FF6600>** </font> "+title;
        }
        else{
            return title;
        }
    }

    private void resetLabelObject() {
        labelObject.setMessage(null);
        labelObject.setIconType(null);
        labelObject.setMessageKey(null);
        revalidate();
    }

    public IconType getIconType() {
        return labelObject.getIconType();
    }

    public String getMessage() {
        return labelObject.getMessage();
    }

    public String getMessageKey() {
        return labelObject.getMessageKey();
    }

    public String getDialogMessageKey() {
        return labelObject.getMessageKey() + " NOTIFYDESCRIPTOR";
    }
    
    @Override
    public void setEnabled(boolean enable) {
        String text = title;
        if (enable == false) {
            if (labelObject.getMessage() != null) {
                setText(title + "  « " + labelObject.getMessage() + " »");
            }
        } else {
            setDefaultColor();
            setText(labelObject.message, labelObject.iconType, labelObject.messageKey);
        }
        super.setEnabled(enable);
    }

    private void setDefaultColor() {
        setForeground(new JLabel().getForeground());
    }

    public void setTitleText(String title) {
        this.title = title;
        setText();
    }

    public boolean isShowMandatoryFlag() {
        return showMandatoryFlag;
    }

    public void setShowMandatoryFlag(boolean showMandatoryFlag) {
        this.showMandatoryFlag = showMandatoryFlag;
        this.setText();
        validate();
    }

    class LabelObject {

        Class bundle;
        String messageKey;
        IconType iconType;
        String message;
        boolean visible;

        public LabelObject() {

        }

        public Class getBundle() {
            return bundle;
        }

        public void setBundle(Class bundle) {
            this.bundle = bundle;
        }

        public IconType getIconType() {
            return iconType;
        }

        public void setIconType(IconType iconType) {
            this.iconType = iconType;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getMessageKey() {
            return messageKey;
        }

        public void setMessageKey(String messageKey) {
            this.messageKey = messageKey;
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }
    }
}
