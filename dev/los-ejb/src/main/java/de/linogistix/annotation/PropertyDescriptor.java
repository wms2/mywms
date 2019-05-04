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

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * Annotation for defining Bean properties. Instead of using BeanInfo classes
 * you can stick the information via this annotation directly to the bean.
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */

    @Retention(RUNTIME)
@Target(METHOD)
public @interface PropertyDescriptor {
  
  /**
   * if yes: don't show this property
   */
  boolean hidden() default false;
  /**
   *Name of the group this property belongs to
   *@see PropertyGroup
   *@see PropertyGroups
   */
  String group() default PropertyGroups.GROUP_OTHER;
  /**
   *Position of property within group. Can be used to order properties. If there is more than one property
   * with the same  position, display order is not defined.
   * 
   *@see PropertyGroup
   *@see PropertyGroups
   */
  int position() default PropertyGroups.GROUP_POSITION_DEFAULT ;
  
  /**
   * If Yes: This setter of this property can be used directly by a user.
   * In many cases, this has to be done by a service call to a facade instead. 
   * 
   */
  boolean setByUser() default false; 
 
  /**
   * 
   * 
   */
  boolean i18n() default false;
  
  /**
   * this property is a complex bean
   * 
   @return true if proeprties of an inline object shoud be displayed as properties of container.
   */
  boolean inline() default true;
}
