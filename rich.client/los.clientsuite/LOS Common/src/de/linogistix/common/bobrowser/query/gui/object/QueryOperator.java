/*
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 */

package de.linogistix.common.bobrowser.query.gui.object;

/**
 * 
 * 
 * @author trautm
 */
public interface QueryOperator {
  
  QueryOperator getDefault();
  
  String getOperator();
  
  boolean isNOP();

}
