/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.services;

import de.linogistix.common.preferences.AppPreferencesController;

import de.linogistix.common.userlogin.LoginService;
import de.linogistix.los.common.facade.LOSClientFacade;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.util.entityservice.LOSSystemPropertyServiceRemote;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.Topic;
import javax.transaction.UserTransaction;

import org.mywms.ejb.BeanLocator;
import org.mywms.model.Client;
import org.openide.util.Lookup;

public class J2EEServiceLocatorImpl implements J2EEServiceLocator {

  private static final Logger log = Logger.getLogger(J2EEServiceLocatorImpl.class.getName());

  private static LoginService login;

  public static String hostName;

  public static String applicationName;

  private static String workstationName;

  private static BODTO<Client> defaultClient;
  private static BODTO<Client> usersClient;
  private static BODTO<Client> systemClient;

  private Boolean showDetailPropertyPanel = null;

  static {
    login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
  }

  BeanLocator beanLocator;

  public static BeanLocator getBeanLocator() {
    BeanLocator b;
    AppPreferencesController jndi;
    try {
//           if (login.getUser() == null || login.getUser().equals("")){
//               log.warning("login without authentification");
//               jndi = new AppPreferencesController("jndi-noauth");
//           } else{
      jndi = new AppPreferencesController("context");

      // }
      AppPreferencesController appServer = new AppPreferencesController("appserver");

      jndi.update();
      appServer.update();
      log.info("login " + login.getUser() + "/****************");
//         if (login.getUser() == null || login.getUser().equals("")){
//             b = new BeanLocator(jndi.getPrefs().getProperties());
//         } else{
      b = new BeanLocator(login.getUser(), login.getPassword(), jndi.getPrefs().getProperties(), appServer.getPrefs().getProperties());
      //}
//      for (Map.Entry entry : jndi.getPrefs().getProperties().entrySet()) {
//        log.info("Property: " + entry.getKey() + "=" + entry.getValue());
//      }

      hostName = jndi.getPrefs().getValue("remote.connection.default.host");
      if (hostName != null) {
        hostName = hostName.replace("\\:.*$", "");
      }
      log.info("hostName=" + hostName);

      applicationName = appServer.getPrefs().getValue("org.mywms.env.applicationName");
      log.info("applicationName=" + applicationName);

      workstationName = System.getenv("WORKSTATION_NAME");
      if (workstationName == null) {
        workstationName = System.getenv("COMPUTERNAME");
      }
      if (workstationName == null) {
        workstationName = System.getenv("HOSTNAME");
      }
      if (workstationName == null) {
        workstationName = System.getenv("HOST");
      }
      log.info("workstationName=" + workstationName);

      return b;
    } catch (Throwable ex) {
      log.log(Level.SEVERE, "Error getting beanlocator: " + ex.getMessage(), ex);
      throw new RuntimeException(ex);
    }
  }

  public J2EEServiceLocatorImpl() {
    try {

      beanLocator = J2EEServiceLocatorImpl.getBeanLocator();
    } catch (Throwable ex) {
      //log.log(Level.SEVERE, ex.getMessage(), ex);
      //ExceptionAnnotator.annotate(new J2EEServiceNotAvailable());
      throw new RuntimeException(ex);
    }
  }

  public void updateLoginInfo(String user, String password) {
    beanLocator = J2EEServiceLocatorImpl.getBeanLocator();
  }

  public <T> T getStateful(Class<T> interfaceClazz) throws J2EEServiceLocatorException {
    try {
      return beanLocator.getStateful(interfaceClazz);
    } catch (Throwable ex) {
      log.log(Level.SEVERE, ex.getMessage(), ex);
      J2EEServiceNotAvailable jex = new J2EEServiceNotAvailable();
      //ExceptionAnnotator.annotate(jex);
      throw jex;
    }
  }

  @SuppressWarnings("unchecked")
  public <T> T getStateful(Class<T> interfaceClazz, String jndiName) throws J2EEServiceLocatorException {
    try {
      return beanLocator.getStateful(interfaceClazz, jndiName);
    } catch (Throwable ex) {
      log.log(Level.SEVERE, ex.getMessage(), ex);
      J2EEServiceNotAvailable jex = new J2EEServiceNotAvailable();
      //ExceptionAnnotator.annotate(jex);
      throw jex;
    }
  }

  public <T> T getStateless(Class<T> interfaceClazz) throws J2EEServiceLocatorException {
    try {
      return beanLocator.getStateless(interfaceClazz);
    } catch (Throwable ex) {
      //Use this if netbeans should show the user an graphical error dialog ((ex) param)        
      //      log.log(Level.SEVERE, ex.getMessage(),ex);
      log.log(Level.SEVERE, ex.getMessage(), ex);
//      ex.printStackTrace();
      J2EEServiceNotAvailable jex = new J2EEServiceNotAvailable();
      //ExceptionAnnotator.annotate(jex);
      throw jex;
    }
  }

  @SuppressWarnings("unchecked")
  public <T> T getStateless(Class<T> interfaceClazz, String jndiName) throws J2EEServiceLocatorException {
    try {
      return beanLocator.getStateful(interfaceClazz, jndiName);
    } catch (Throwable ex) {
      log.log(Level.SEVERE, ex.getMessage(), ex);
      J2EEServiceNotAvailable jex = new J2EEServiceNotAvailable();
      //ExceptionAnnotator.annotate(jex);
      throw jex;
    }
  }

  public Queue getQueue(String queueName) throws J2EEServiceLocatorException {

    return beanLocator.getQueue(queueName);
  }

  public Topic getTopic(String topicname) throws J2EEServiceLocatorException {
    return beanLocator.getTopic(topicname);
  }

  public UserTransaction getUserTransaction() throws J2EEServiceLocatorException {
    return beanLocator.getUserTransaction();
  }

  public String getApplicationName() {
    return this.applicationName;
  }

  public String getHostName() {
    return this.hostName;
  }

  public String getWorkstationName() {
    return this.workstationName;
  }

  @Override
  public BODTO<Client> getDefaultClient() {
    readClients();
    return defaultClient;
  }

  @Override
  public BODTO<Client> getUsersClient() {
    readClients();
    return usersClient;
  }

  @Override
  public BODTO<Client> getSystemClient() {
    readClients();
    return systemClient;
  }

  private void readClients() {
    if (systemClient == null) {
      try {
        LOSClientFacade clientFacade = getStateless(LOSClientFacade.class);
        defaultClient = clientFacade.getDefaultClient();
        usersClient = clientFacade.getUsersClient();
        systemClient = clientFacade.getSystemClient();
      } catch (J2EEServiceLocatorException ex) {
        ex.printStackTrace();
      }
    }
  }

  Map<String, Boolean> propertyMapBool = new HashMap<String, Boolean>();

  public boolean getPropertyBool(String key, boolean defaultValue) {
    Boolean value = propertyMapBool.get(key);
    if (value != null) {
      return value;
    }
    if (beanLocator != null) {
      try {
        LOSSystemPropertyServiceRemote propertyService = getStateless(LOSSystemPropertyServiceRemote.class);
        value = propertyService.getBooleanDefault(workstationName, key, defaultValue);
        propertyMapBool.put(key, value);
      } catch (J2EEServiceLocatorException ex) {
        ex.printStackTrace();
      }
    }
    return value == null ? defaultValue : value;
  }
}
