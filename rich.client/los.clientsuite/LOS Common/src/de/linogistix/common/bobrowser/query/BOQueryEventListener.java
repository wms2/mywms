/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.bobrowser.query;

import org.openide.nodes.NodeListener;

/**
 *
 * @author trautm
 */
public interface BOQueryEventListener extends NodeListener {
  /**
   * Informs Implementations of this interface which have been registeres as
   *  listeners about changes, i.e. when a BO has been outdated.
   * 
   * @see BOQueryEvent
   * @param ev
   */
  void outdated(BOQueryEvent ev);
  
}
