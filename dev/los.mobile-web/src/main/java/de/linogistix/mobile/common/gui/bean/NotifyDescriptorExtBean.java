/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobile.common.gui.bean;

import java.io.Serializable;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import de.linogistix.mobile.common.listener.ButtonListener;


/**
 *
 * @author artur
 */
public class NotifyDescriptorExtBean extends BasicBackingBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String pageTitle;
    private String title;
    private String icon;
    private String message;
    
    private String param;
    
    private boolean visibleParam;

    List<String> buttonList;
    ButtonListener listener;

    public NotifyDescriptorExtBean() {

    }

    public void setButtons(List<String> buttonList) {
        this.buttonList = buttonList;
    }
    
    public void setParam(String param){
    	this.param = param;
    	this.visibleParam = true;
    	
    }

    private String getButtonText(int buttonNumber) {
        if (buttonList != null) {
            if (buttonList.size() >= buttonNumber) {
                return buttonList.get(buttonNumber - 1);
            }
        }
        return "";
    }

    public void setCallbackListener(ButtonListener listener) {
        this.listener = listener;
    }

    public String getButton1Text() {
        return getButtonText(1);
    }

    public String getButton2Text() {
        return getButtonText(2);

    }

    public String getButton3Text() {
        return getButtonText(3);

    }

    public String getButton4Text() {
        return getButtonText(4);

    }

    public String getButton5Text() {
        return getButtonText(5);
    }

    public String button1ActionPerformedListener() {
        return listener.buttonClicked(1, this);
    }

    public String button2ActionPerformedListener() {
        return listener.buttonClicked(2, this);
    }

    public String button3ActionPerformedListener() {
        return listener.buttonClicked(3, this);
    }

    public String button4ActionPerformedListener() {
        return listener.buttonClicked(4, this);
    }

    public String button5ActionPerformedListener() {
        return listener.buttonClicked(5, this);
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private boolean isVisible(int buttonNumber) {
        if (buttonList != null) {
            if (buttonList.size() >= buttonNumber) {
                return true;
            }
        }
        return false;

    }

    public boolean isVisible1() {
        return isVisible(1);
    }

    public boolean isVisible2() {
        return isVisible(2);
    }

    public boolean isVisible3() {
        return isVisible(3);
    }

    public boolean isVisible4() {
        return isVisible(4);
    }

    public boolean isVisible5() {
        return isVisible(5);
    }

	public String getParam() {
		return param;
	}

	public boolean isVisibleParam() {
		return visibleParam;
	}
	
	 public void paramValidator(FacesContext context, UIComponent toValidate, Object value) {
	 
	 }
}
