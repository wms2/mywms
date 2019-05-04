/*
 * LoginInfo.java
 *
 * Created on 21. November 2006, 14:56
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.userlogin;

import de.linogistix.common.bobrowser.bo.BO;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;
import org.mywms.facade.AuthenticationInfoTO;
import org.mywms.model.BasicClientAssignedEntity;
import org.mywms.model.Client;
import org.mywms.model.User;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 * Holds information about login status of user
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class LoginServiceImpl extends AbstractNode implements LoginService {

  private static Logger log = Logger.getLogger(LoginServiceImpl.class.getName());
  
  private LoginState state = LoginState.NOT_AUTENTICATED;
  
  private String user = "" ; //System.getProperty("user.name");
  
  private String password;
  
  private String usersClientNumber = "";
  private Client usersClient = null;
  
  private AuthenticationInfoTO authentification;

  public LoginServiceImpl(){
    super(Children.LEAF);
  }
  
  /**
   * Shows whether the user is logged in correctly.
   *
   * @return true if state if LoginState-AUTHENTICATED and #getUser() equals userName of AuthenticationInfoTO
   */
  public boolean validate(){
    if (getState() != LoginState.AUTENTICATED){
      //log.warning("state is not AUTENTICATED");
      return false;
    }
    
    if (getAuthentification() == null){
      log.warning("no authentification info");
      return false;
    }
    if (! getUser().equals(getAuthentification().userName)){
      log.warning("user mismatch");
      return false;
    }
    return true;
  }
  
  /**
   * Resets the login information. After calling reset() the client thinks that 
   * the user is not logged in any longer. 
   */
  public void reset(){
    setState(LoginState.NOT_AUTENTICATED);
    setAuthentification(null);
    fireLoginStateChange();
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public AuthenticationInfoTO getAuthentification() {
    return authentification;
  }

  /**
   * Returns the authentification information returned by the application server.
   */
  public void setAuthentification(AuthenticationInfoTO authentification) {
    this.authentification = authentification;
  }

  public LoginState getState() {
    return state;
  }

  public void setState(LoginState state) {
    this.state = state;
  }

  public void fireLoginStateChange() {
    firePropertyChange(PROP_LOGIN_STATE,state, null);
  }

  public void addLoginStateChangeListener(PropertyChangeListener listener) {
    addPropertyChangeListener(listener);
  }
  
    /**
   * Checks whether the logged in user is allowed to see this node.
   */
  public boolean checkRolesAllowed(String[] rolesToCheck){

    if (!validate()){
      return false;
    }
    
    if (rolesToCheck == null || rolesToCheck.length == 0){
      return true;
    }
    
    String[] loginRoles = getAuthentification().roles;
    for (String loginRole : loginRoles ){
      for (String roleToCheck : rolesToCheck){
        if (loginRole.equals(roleToCheck)){
          return true;
        }
      }
    }
    return false;
  }

      /**
   * Checks whether the logged in user is allowed to see this node.
   */
  public boolean checkWritePermission(BO bo){

    if (!validate()){
      return false;
    }

    if( ! (bo.getBusinessObjectTemplate() instanceof BasicClientAssignedEntity) ) {
        if( !usersClient.isSystemClient() ) {
            return false;
        }
    }

    if (bo.getAllowedRolesCRUD() == null || bo.getAllowedRolesCRUD().length == 0){
      return true;
    }

    String[] loginRoles = getAuthentification().roles;
    for (String loginRole : loginRoles ){
      for (String roleToCheck : bo.getAllowedRolesCRUD()){
        if (loginRole.equals(roleToCheck)){
          return true;
        }
      }
    }
    return false;
  }

    public String getUsersClientNumber() {
       return this.usersClientNumber;
    }

    void setUsersClientNumber(String usersClientNumber) {
        this.usersClientNumber = usersClientNumber;
    }

    public Client getUsersClient() {
       return this.usersClient;
    }

    void setUsersClient(Client usersClient) {
        this.usersClient = usersClient;
    }
    
    public void processUser(User user) {
        setUsersClientNumber(user.getClient().getNumber());
        setUsersClient(user.getClient());
        setUser(user.getName());
    }
  

}
