/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.userlogin;

import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.services.J2EEServiceNotAvailable;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.common.util.StatusLineServer;
import de.linogistix.los.user.LoginServiceRemote;
import de.linogistix.los.user.query.UserQueryRemote;
import de.wms2.mywms.util.Translator;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.User;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

public class SecurityManager {

    private static final Logger log = Logger.getLogger(SecurityManager.class.getName());

    /**
     * Login for user with password. 
     * 
     * Regard that given an empty username no authentification takes place. Most 
     * services won't be available then. 
     * 
     * @param username
     * @param password
     * @return true if login has been sucessful; false if no login has been done (empty username)
     * @throws de.linogistix.common.userlogin.LoginException if login has not been successful
     */
    public static boolean login(String username, String password) throws LoginException, J2EEServiceNotAvailable, FacadeException {
        LoginService login;
        StatusLineServer statusLineServer;
        LoginServiceRemote authService;
        LoginException lex = null;
        FacadeException fex = null;
        J2EEServiceNotAvailable sex = null;
        UserQueryRemote userQueryRemote;
        login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
        statusLineServer = (StatusLineServer) Lookup.getDefault().lookup(StatusLineServer.class);

/*        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalArgumentException("cannot encrypt password");
        }
        md5.reset();
        md5.update(password.getBytes());
        StringBuffer hexString = new StringBuffer(32);
        Formatter f = new Formatter(hexString);
        for (byte b : md5.digest()) {
            f.format("%02x", b);
        }    
        password = hexString.toString();*/
        if ("".equals(username)) {
            login.reset();
            login.fireLoginStateChange();
            String noLoginMsg = NbBundle.getMessage(CommonBundleResolver.class, "BusinessException.loginSkipped");
            NotifyDescriptor d = new NotifyDescriptor.Message(noLoginMsg, NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
            login.setState(LoginState.NOT_AUTENTICATED);
            return false;
        } else if (login.validate()) {
            login.reset();
            statusLineServer.loggedIn(login.validate());
            login.fireLoginStateChange();
        }
        try {

            CursorControl.showWaitCursor();
            login.reset();
            login.setUser(username);
            login.setPassword(password);
            login.setState(LoginState.LOG_IN);

            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            if (loc != null) {

                
                loc.updateLoginInfo(login.getUser(), password);

                authService = (LoginServiceRemote) loc.getStateless(LoginServiceRemote.class);
                userQueryRemote = (UserQueryRemote)loc.getStateless(UserQueryRemote.class);
                
                if (authService != null) {
                    login.setAuthentification(authService.getUserInfo());
                    if (login.getAuthentification() == null) {
                        throw new LoginException(username);
                    }

                    authService.loginCheck(loc.getWorkstationName(), login.getUser());
                    
                    User u = userQueryRemote.queryByIdentity(username);
                    login.processUser(u);
                    login.setState(LoginState.AUTENTICATED);
                    if (!login.validate()) {
                        throw new LoginException(username);
                    }
                }
            }
        } catch (LoginException ex) {
            lex = ex;
        } catch (J2EEServiceNotAvailable ex) {
            sex = ex;
        } catch (FacadeException ex) {
            fex = ex;
        } catch (Throwable ex) {
            log.log(Level.SEVERE, ex.getMessage());
            ex.printStackTrace();
            lex = new LoginException(username);
        } finally {
            if (statusLineServer != null) {
                statusLineServer.loggedIn(login.validate());
            }
            login.fireLoginStateChange();
            CursorControl.showNormalCursor();
            if (fex != null) {
                throw fex;
            } else if (lex != null) {
                throw lex;
            } else if (sex != null) {
                throw sex;
            }
            if (login != null && login.getState() != null) {
                if (login.getState() != LoginState.AUTENTICATED) {
                    throw new LoginException(username);
                }
            }
        }
        
        try{
            Locale locale = Translator.parseLocale(login.getAuthentification().locale);
            Locale.setDefault(locale);
        } catch (Throwable t){
            fex = new FacadeException("Locale could not be set", "BusinessException.LocaleException", new String[]{login.getAuthentification().locale});
            fex.setBundleResolver(CommonBundleResolver.class);
            ExceptionAnnotator.annotate(fex);
        }
        
        //if not LoginException will be thrown the user and password is valid
        return true;
    }
}

