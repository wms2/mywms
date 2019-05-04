/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobile.common.gui.jsp;

import de.linogistix.mobile.common.gui.bean.BasicBackingBean;

/**
 *
 * @author artur
 */
public class JSPLogoutBean extends BasicBackingBean {

    private String login = resolve("Login");
   
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
