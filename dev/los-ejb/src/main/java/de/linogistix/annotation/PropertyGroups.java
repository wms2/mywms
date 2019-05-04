/*
 * ServiceDescriptor.java
 *
 * Created on 13. Oktober 2006, 23:25
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Holds array of PropertyGroup.
 *
 * @see PropertyGroup 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PropertyGroups {
  
  static final int GROUP_POSITION_DEFAULT = 99;
  
  static final int GROUP_POSITION_BASIC = 1;
  
  static final int GROUP_POSITION_MAIN = 2;
  
  static final int GROUP_POSITION_OTHER = GROUP_POSITION_DEFAULT;
  
  static final String GROUP_BASIC = "basic";
  
  static final String GROUP_OTHER = "other";
  
  static final String GROUP_MAIN = "main";
  
 
  /**
   * Array of PropertyGroup
   */
  PropertyGroup[] value ();
  
}
