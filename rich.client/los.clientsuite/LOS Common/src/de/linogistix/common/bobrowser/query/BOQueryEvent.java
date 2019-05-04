/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.bobrowser.query;

import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;

/**
 * A {@link NodeEvent} for BOQueryNode.
 *
 * @author trautm
 */
public class BOQueryEvent extends NodeEvent{
  
  /** Creates a new instance of BOQueryEvent */
  public BOQueryEvent(Node node) {
    super(node);
  }
  
}
