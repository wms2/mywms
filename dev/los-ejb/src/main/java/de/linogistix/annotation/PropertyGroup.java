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

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * Annotation for defining a group of Bean properties. Annotates classes.
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */

@Retention(RUNTIME)
@Target(TYPE)
public @interface PropertyGroup {
  
 
  boolean hidden() default false;
  /**
   *Name of the group this property belongs to
   */
  String name() default PropertyGroups.GROUP_OTHER;

  /**
   * Position of group within all groups. Can be used to order groups. If there is more than one group
   * with the same  position, display order is not defined.
   */
  int groupPosition() default PropertyGroups.GROUP_POSITION_DEFAULT;
  
}
