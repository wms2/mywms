/*
 * J2EEServiceLocator.java
 *
 * Created on 14. September 2006, 08:05
 *
 * Copyright (c) 2006-2012 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.services;

import de.linogistix.los.query.BODTO;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.Topic;
import javax.transaction.UserTransaction;
import org.mywms.model.Client;

/**
 * Class for obtaining J2EE services via jndi.
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public interface J2EEServiceLocator{

    public String getApplicationName();
    
    public String getHostName();

    public String getWorkstationName();
    
    public BODTO<Client> getDefaultClient();
    public BODTO<Client> getUsersClient();
    public BODTO<Client> getSystemClient();
    
  void updateLoginInfo(String user, String password);
  
  <T> T getStateful(Class<T> interfaceClazz) throws J2EEServiceLocatorException;

  @SuppressWarnings("unchecked")
  <T> T getStateful(Class<T> interfaceClazz, String jndiName) throws J2EEServiceLocatorException;

  /**
   * @returns an implementation for the given interface
   * @param interfaceClazz Class of the interface whose implementations should be returned.
   */
  <T> T getStateless(Class<T> interfaceClazz) throws J2EEServiceLocatorException;

   /**
   * @returns an implementation for the given interface by jndi name
   * @param interfaceClazz Class of the interface whose implementations should be returned.
    *@param jndiName the jndi name for the service. If null, a jndi name will be guessed 
    */
  @SuppressWarnings("unchecked")
  <T> T getStateless(Class<T> interfaceClazz, String jndiName) throws J2EEServiceLocatorException;
  
  Topic getTopic(String topicname) throws J2EEServiceLocatorException;
  
  Queue getQueue(String queueName) throws J2EEServiceLocatorException;

  UserTransaction getUserTransaction() throws J2EEServiceLocatorException;

  public boolean getPropertyBool(String key, boolean defaultValue);

}
