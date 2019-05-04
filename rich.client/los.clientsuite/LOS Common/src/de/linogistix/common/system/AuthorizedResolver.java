/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.system;

import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.userlogin.LoginState;
import org.openide.util.Lookup;

/**
 *
 * @author artur
 */
public class AuthorizedResolver {

    private static AuthorizedResolver instance = null;
    /** Creates a new instance of GraphicUtil */
    private AuthorizedResolver() {
        // Exists only to defeat instantiation.
    }
    
    public synchronized static AuthorizedResolver getInstance() {
        if (instance == null) {
            instance = new AuthorizedResolver();
        }
        return instance;
    }
    
   
    public boolean isAuthorized() {
        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class); 
        if (login.getState() == LoginState.AUTENTICATED) {
            return true;
        }
        return false;
    }
    
    public boolean isAuthorized(boolean informUser) {
        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class); 
        if (login.getState() == LoginState.AUTENTICATED) {
            return true;
        }
        return false;
    }

}
