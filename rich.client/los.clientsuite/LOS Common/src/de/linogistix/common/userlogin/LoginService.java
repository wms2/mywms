/*
 * LoginService.java
 *
 * Created on 27. Dezember 2006, 15:06
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.userlogin;

import de.linogistix.common.bobrowser.bo.BO;
import java.beans.PropertyChangeListener;
import org.mywms.facade.AuthenticationInfoTO;
import org.mywms.model.Client;
import org.mywms.model.User;

/**
 * A service for managing loging information such as username, LoginState, etc.
 * 
 * Allows for registering PropertyChangeListeners to listen on changes in login information.
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public interface LoginService {
  
  static final String PROP_LOGIN_STATE = "PROPERTY_STATE";
  
  /**
   * Returns the authentification information returned by the application server
   * @see AuthenticationInfoTO
   * @return {@link AuthenticationInfoTO}
   */
  AuthenticationInfoTO getAuthentification();

  /**
   * @return the poasword for login
   */
  String getPassword();

  /**
   * @return the username used for login
   */
  String getUser();

  /**
   * 
   * @return the client number associated with the logged in user
   */
  String getUsersClientNumber();
  
  /**
   * 
   * @return the client associated with the logged in user
   */
  public Client getUsersClient();

  /**
 *  
 * Returns one of three possible states concerning the login process:
 * 
 * <ul>
 * <li> <code>NOT_AUTENTICATED</code> The user is NOT authentificated against the system
 * <li> <code>AUTENTICATED</code> The user is authentificated against the system
 * <li> <code>LOG_IN</code>Authentification is in progress. This state is important for the internal login state machine 
 * </ul>
 * @return {@link LoginState} as described
 * @author trautm
 */
  LoginState getState();
  
  /**
   * Resets the login information. After calling reset() the client thinks that 
   * the user is not logged in any longer.
   */
  void reset();

  /**
   * Returns the authentification information returned by the application server.
   */
  void setAuthentification(AuthenticationInfoTO authentification);

  void setPassword(String password);

  /**
   * Used by a login dialog to set a {@link LoginState}
   * @param state
   */
  void setState(LoginState state);
  
  /**
   * fires an event to signal that a change of login information has occured
   * @see addLoginStateChangeListener
   */
  void fireLoginStateChange();
  
  void setUser(String user);

  /**
   * Shows whether the user is logged in correctly.
   * 
   * 
   * @return true if state if State-AUTHENTICATED and #getUser() equals userName of AuthenticationInfoTO
   */
  boolean validate();
  
  /**
   *Adds a listener that listens for changes of login state.
   */
  void addLoginStateChangeListener(PropertyChangeListener listener);
  
  
  /**
   * Checks whether the logged in user is allowed to see this node.
   */
  boolean checkRolesAllowed(String[] rolesToCheck);
  
  public void processUser(User user) ;

    /**
     * Checks whether the user is allowed to write this BO
     * @param bo
     * @return
     */
    public boolean checkWritePermission(BO bo);
}
