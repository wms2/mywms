/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobile.common.gui.bean;

import java.util.List;

import de.linogistix.mobile.common.listener.ButtonListener;
import de.linogistix.mobile.common.object.Constants;
import de.linogistix.mobile.common.system.JSFHelper;


/**
 *
 * @author artur
 */
public class NotifyDescriptorExt extends BasicBackingBean {

    NotifyDescriptorExtBean bean;
    final static String NOTIFY_DESCRIPTOR_EXT_PANEL = "NotifyDescriptorExtPanel";
    final static String NOTIFY_DESCRIPTOR_EXT_BEAN = "NotifyDescriptorExtBean";
    NotifyEnum notifyType;
    
    public NotifyDescriptorExt(String icon, String pageTitle, String message, List<String> buttonTextList) {
    	notifyType = NotifyEnum.INFORMATION;
    	setBean(icon, pageTitle, message, buttonTextList);
    }
    
    public NotifyDescriptorExt(final NotifyEnum notify, String message, List<String> buttonTextList) {
        this.notifyType = notify;
    	switch (notify) {
            case WARNING:
                setBean(Constants.WARNING_ICON, resolve("Warning", new Object[]{}), message, buttonTextList);
                break;
            case ERROR:
                setBean(Constants.ERROR_ICON, resolve("Error", new Object[]{}), message, buttonTextList);
                break;
            case INFORMATION:
                setBean(Constants.INFORMATION_ICON, resolve("Information", new Object[]{}), message, buttonTextList);
                break;
            case QUESTION:
                setBean(Constants.QUESTION_ICON, resolve("Question", new Object[]{}), message, buttonTextList);
                break;
            case INPUT:
            	setBean(Constants.QUESTION_ICON, resolve("Question", new Object[]{}), message, buttonTextList);
                break;
            case HELP:
                setBean(Constants.HELP_ICON, resolve("Help", new Object[]{}), message, buttonTextList);
                break;
        }
    }

    public NotifyDescriptorExt(NotifyEnum input, String pageTitle,
			List<String> buttonTextList, String paramDefault) {
		this(input, pageTitle, buttonTextList);
		bean.setParam(paramDefault);
	}

	private void setBean(String icon, String pageTitle, String message, List<String> buttonTextList) {
        bean = (NotifyDescriptorExtBean) JSFHelper.getInstance().getSessionBean(NotifyDescriptorExtBean.class);
        if (bean == null) {
            bean = new NotifyDescriptorExtBean();
            JSFHelper.getInstance().setSessionBean(bean);
        }
        bean.setIcon(icon);
        bean.setPageTitle(pageTitle);
        bean.setTitle(pageTitle);
        bean.setMessage(message);
        bean.setButtons(buttonTextList);
        if (this.notifyType == NotifyEnum.INPUT){
        	bean.setParam(new String());
        }
    }

    public String setCallbackListener(ButtonListener listener) {
        bean.setCallbackListener(listener);
        return NOTIFY_DESCRIPTOR_EXT_PANEL;
    }

    public String getParam(){
    	return bean.getParam();
    }
    
    public enum NotifyEnum {

        WARNING,
        INFORMATION,
        QUESTION,
        INPUT,
        ERROR,
        HELP
    }
}
