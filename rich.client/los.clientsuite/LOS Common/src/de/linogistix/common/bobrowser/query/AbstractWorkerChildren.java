/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.bobrowser.query;

import java.util.Collection;
import java.util.Collections;
import javax.swing.SwingUtilities;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.RequestProcessor;

/**
 * Based on code of Chris Palmer to speed up explorer View,
 * see {@link http://openide.netbeans.org/servlets/ReadMsg?listName=dev&msgNo=20306}
 *
 * @author trautm
 */
public abstract class AbstractWorkerChildren extends Children.Keys {
  protected static final Object KEY_LOADING = new Object();
  
  public AbstractWorkerChildren() {
  }
  
  protected void addNotify() {
    if (isSlow()) {
      setKeys(Collections.singleton(KEY_LOADING));
      RequestProcessor.getDefault().post(new Runner());
    } else {
      setKeys(createChildList());
    }
    super.addNotify();
  }
  
  protected void removeNotify() {
    setKeys(Collections.EMPTY_LIST);
    super.removeNotify();
  }
  
  protected abstract Collection createChildList();
  
  /**
   * override when getting children is show -> contacts server, default
   * is false;
   */
  protected boolean isSlow() {
    return false;
  }
  private Node createLoadingNode() {
    return new WaitNode();
  }
  
  protected Node[] createNodes(Object obj) {
    if (obj == KEY_LOADING) {
      return new Node[] { createLoadingNode() };
    }
    return new Node[0];
  }
  private class Runner implements Runnable {
    public void run() {
      final Collection col = createChildList();
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          setKeys(col);
        }
      });
    }
    
  }
  
  class WaitNode extends AbstractNode{
    
    WaitNode(){
      super(Children.LEAF);
    }

    public String getName() {
      return "Loading...";
    }
    
    
    
    
  }
  
}
