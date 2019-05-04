/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.bobrowser.api;

import de.linogistix.common.bobrowser.bo.BONode;


/**
 *Api for adding Nodes to the Explorer View of Data Manager, i.e. BOBrowserTopComponent
 *
 * @author trautm
 */
public interface BOBrowserAPI{
  /**
   * API method for adding a BONode to the explorer view
   */
  void addBONode(BONode boNode);
  
  /**
   * API method for removing a BONode from the explorer view
   */
  void removeBONode(BONode boNode);
  
  
  
}
