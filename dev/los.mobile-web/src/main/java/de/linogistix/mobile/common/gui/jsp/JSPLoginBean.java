/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobile.common.gui.jsp;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import de.linogistix.mobile.common.gui.bean.BasicBackingBean;
import de.linogistix.mobile.common.system.JSFHelper;


/**
 *
 * @author artur
 */
public class JSPLoginBean extends BasicBackingBean {

    private String logout = resolve("Login", new Object[]{});


	private String sessionExpiredValidator;
    private String message;


    public String getLogin() {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        context.getRequest();
        return logout;
    }

    public void setLogin(String login) {
        this.logout = login;
    }

    public String getSessionExpiredValidator() {
        JSFHelper.getInstance();
        return "";
    }

    public void setSessionExpiredValidator(String sessionExpiredValidator) {
        this.sessionExpiredValidator = sessionExpiredValidator;
    }

    public String getMessage() {
        JSFHelper helper = JSFHelper.getInstance();

        if (helper.getRequestQueryString() != null) {
            if (helper.getRequestQueryString().equals("errors=true")) {
                return getHTMLMessage(resolve("UserOrPasswordWrong"));
            }
            if (helper.getRequestQueryString().equals("errors=timeout")) {            
                return getHTMLMessage(resolve("SessionTimeoutMessage"));
            }
        }
        return "";
    }

    private String getHTMLMessage(String message) {
        return "<ul id='messages' class='error'><li>" + message + "</li></ul>";
    }

    public void setMessage(String message) {
        this.message = message;
    }

    
    
}
