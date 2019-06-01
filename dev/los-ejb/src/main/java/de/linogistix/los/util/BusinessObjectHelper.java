/*
 * Copyright (c) 2006-2013 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyDescriptor;
//import java.lang.reflect.Method;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.EJBContext;
import javax.ejb.SessionContext;
import javax.xml.bind.JAXBElement;

import org.apache.log4j.Logger;
import org.mywms.model.BasicClientAssignedEntity;
import org.mywms.model.BasicEntity;
import org.mywms.model.Client;
import org.mywms.model.User;
import org.mywms.service.UserService;

/**
 *
 * @author trautm
 */
public class BusinessObjectHelper {
  
  private static final Logger log = Logger.getLogger(BusinessObjectHelper.class.getName());
  
  SessionContext ctx;
 
  UserService userService;
  
  EJBContext context;
  static List<String> collectionVetoList = new ArrayList<String>();

  
  /** Creates a new instance of BusinessObjectHelper */
  public BusinessObjectHelper(
          SessionContext ctx, 
          UserService userService,
          EJBContext context) {
    
    this.ctx = ctx;
    this.userService = userService;
    this.context = context;
    
    // 05.02..2013, krane, Client application crashes server, when selecting a large collection.
    // So for entities in this List, the collections are not initialized to show in rich-client
    // TODO: make this customizable or make a client application, which not automatically requests everything
    collectionVetoList.add("nirwana");
    collectionVetoList.add("shipped");
    collectionVetoList.add("shipping");
    collectionVetoList.add("versand");
    collectionVetoList.add("papierkorb");
    collectionVetoList.add("trash");
    collectionVetoList.add("goods-in");
    collectionVetoList.add("wareneingang");
    collectionVetoList.add("goods-out");
    collectionVetoList.add("warenausgang");
    
  }
  
    /**
   * Returns the client, the current caller belongs to. If the user does not
   * belong to a single client (for example if the user is an administrator)
   * this method will return null.
   *
   * @return the client of the user the calling user belongs to.
   *         <code>null</code> if user has Role.ADMIN
   */
  public final User getCallersUser() {
    Principal principal = context.getCallerPrincipal();
    
    if (principal.getName() == null) {
      return null;
    }
    
    try {
      User user = userService.getByUsername(principal.getName());
      return user;
    } catch (org.mywms.service.EntityNotFoundException ex) {
      return null;
    }
  }
  
  /**
   *Checks whether <code>T</code> of Type {@link BasicEntity} might be changed by
   * callers user.
   * @return true if caller has role {@link org.mywms.model.Role.ADMIN} or belongs to the same client than <code>T</code>
   */
  public boolean checkClient(BasicEntity bo) {
    
    if (bo == null) {
      throw new NullPointerException("bo must not be null");
    }
    
    User callersUser = getCallersUser();
    
    if (callersUser == null) {
      log.error("Cannot identify callers User");
      return false;
    }
    
    if (callersUser.hasRole(org.mywms.globals.Role.ADMIN)) {
      // anything goes
      return true;
    }
    
    if (bo instanceof BasicClientAssignedEntity) {
      BasicClientAssignedEntity bcae = (BasicClientAssignedEntity) bo;
      if (bcae.getClient() == null) {
        // no client assigned
        return true;
      }
      if (callersUser.getClient() == null) {
        return false;
      }
      if (bcae.getClient().equals(callersUser.getClient())) {
    	  return true;
      } else if (callersUser.getClient().isSystemClient()){
    	  return true;
      } else{
    	  return false;
      }
    } else if (bo instanceof Client){
    	if (callersUser.getClient() == null) {
            return false;
          }
    	if (callersUser.getClient().equals(bo)){
    		return true;
    	} else{
    		return false;
    	}
    }
    else{
    	return true;
    }
  }
  
  /**
   * Reads every property to initialize values from LAZY loading.
   *
   */
  @SuppressWarnings({ "unused", "rawtypes" })
  public static BasicEntity eagerRead(BasicEntity e){
    
    try{
      BeanInfo infoTo = Introspector.getBeanInfo(e.getClass());
      PropertyDescriptor[] d = infoTo.getPropertyDescriptors();
      
      for (int i=0; i<d.length; i++) {
        try{
          if( d[i].getReadMethod() == null ) {
        	  continue;
          }
          
          Object value = d[i].getReadMethod().invoke(e, new Object[0]);
//          log.debug("Read from " + d[i].getReadMethod().getName() +": " + value.toString());
          
          if (value == null){
        	  continue;
          } else if (TypeResolver.isPrimitiveType(d[i].getPropertyType())){
        	  // should be loaded now
          } else if (TypeResolver.isEnumType(d[i].getPropertyType())){
        	  // should be loaded now
          } else if (TypeResolver.isDateType(d[i].getPropertyType())){
        	  // should be loaded now
          } else if (value instanceof BasicEntity){
				((BasicEntity)value).toDescriptiveString();
		  } else if (value instanceof Collection){
			String name = e.toUniqueString().toLowerCase();
			if( collectionVetoList.contains(name) ) {
				log.info("Do not eager read veto collection. id=" + e.getId() + ", name="+e.toUniqueString()+", property="+d[i].getName() );
		  	}
			else {
	            Collection c = (Collection)value;
				if (c == null){
	              log.warn("Collection is null " + d[i].getName());
	            } else if (c.size() == 0 ){
	              try{
	//                log.debug("Try init Collection of length 0: " + d[i].getName());
	                // just to perform any operation on collection to read it eagerly - hopefully
	                c.contains(d[i].getName());
	                d[i].getWriteMethod().invoke(e,new Object[] {value});
	              } catch (Throwable t){
	                log.error(t.getMessage(),t);
	              }
	            } else{
	              for ( Object elem : c) {
	            	  // Read the element
	            	  ((BasicEntity) elem).toDescriptiveString();
	//                log.debug("-- read element: " + elem.toString());
	              }
	            }
			}
			//TODO dgrys comment as workaround portierung auf wildfly
//          } else if (value instanceof org.hibernate.proxy.HibernateProxy){
////        	  log.debug("Try init HibernateProxy: " + value.toString());
//        	  Method setter = d[i].getWriteMethod();
//        	  if (value != null){     	  
//	        	  org.hibernate.proxy.HibernateProxy proxy = (org.hibernate.proxy.HibernateProxy)value;
//	        	  Object o = proxy.writeReplace();
////	        	  setter.invoke(e, new Object[]{o});
//        	  } else{
////        		  setter.invoke(e, new Object[]{value});
//        	  }
          } else{
//        	  log.warn("Unsupported value type for eager reading: " + value.getClass().getName());
          }
        } catch (Throwable ex){
        	log.error(ex.getMessage(), ex);
          continue;
        }
      }
      
      return e;
      
    } catch (Throwable ex){
      log.error(ex.getMessage(), ex);
    } 
    
    return e;
    
  }
  
  @SuppressWarnings({ "rawtypes" })
public static String bean2String(Object bean){
	  StringBuffer b = new StringBuffer();
      try {
          BeanInfo info = Introspector.getBeanInfo(bean.getClass());
          java.beans.PropertyDescriptor[] d = info.getPropertyDescriptors();

          b.append(bean.getClass().getSimpleName());
          b.append(": ");

          for (int i = 0; i < d.length; i++) {
              try {
              	
              	if (d[i].getName().equals("class")){
              		continue;
              	}
                  b.append("[");
                  b.append(d[i].getName());
                  b.append("=");
                  try {
                      if (JAXBElement.class.isAssignableFrom(d[i].getPropertyType())){
                    	  JAXBElement el = (JAXBElement) d[i].getReadMethod().invoke(bean,
                                  new Object[0]);
                    	  b.append(el.getValue().toString());
                      } else{
                	  b.append(d[i].getReadMethod().invoke(
                          bean,
                          new Object[0]).toString());
                      }
                  }
                  catch (Throwable t) {
                      b.append("?");
                  }
                  b.append("]");
              }
              catch (Throwable ex) {
                  continue;
              }
          }
          return new String(b);
	  }  
	  catch (Throwable t) {
		  log.error(t.getMessage(),t);
	      return bean.toString();
	  }
  }
  /**
   * 
   * @param e1
   * @param e2
   * @return those properties that differ
   */
  
  public static PropertyChangeEvent[] diff(BasicEntity e1, BasicEntity e2){
    
	  List<PropertyChangeEvent> ret = new ArrayList<PropertyChangeEvent>();
	  
	  if (e1.getClass() != e2.getClass()){
		  throw new IllegalArgumentException("diff: e1 and e2 must be of same type");
	  }
	  
    try{
      BeanInfo infoTo = Introspector.getBeanInfo(e1.getClass());
      PropertyDescriptor[] d = infoTo.getPropertyDescriptors();
      
      for (int i=0; i<d.length; i++) {
        try{
          if( d[i].getReadMethod() == null ) {
        	  continue;
          }
          
          Object value1 = d[i].getReadMethod().invoke(e1, new Object[0]);
          Object value2 = d[i].getReadMethod().invoke(e2, new Object[0]);
//          log.debug("Read from " + d[i].getReadMethod().getName() +": " + value.toString());
          
          if (value1 == value2){
        	  continue;
          } else if (value1 != null && value1.equals(value2)){
        	  continue;
          } else{
        	  PropertyChangeEvent e = new PropertyChangeEvent(d[i], d[i].getName(), value2, value1);
        	  ret.add(e);
          }
        } catch (Throwable ex){
        	log.error(ex.getMessage(), ex);
          continue;
        }
      }
      
      return ret.toArray(new PropertyChangeEvent[0]);
      
    } catch (Throwable ex){
      log.error(ex.getMessage(), ex);
      return null;
    } 

    
  }
 
  
}