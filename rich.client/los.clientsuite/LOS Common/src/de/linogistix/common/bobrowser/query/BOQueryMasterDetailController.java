/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.bobrowser.query;

import de.linogistix.common.bobrowser.bo.BOBeanNode;
import de.linogistix.common.bobrowser.bo.BOEntityNodeReadOnly;
import de.linogistix.common.bobrowser.bo.BOEntityQueryNode;
import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.bobrowser.bo.BONode;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mywms.model.BasicEntity;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author trautm
 */
public class BOQueryMasterDetailController implements PropertyChangeListener, VetoableChangeListener, ExplorerManager.Provider {

  private static Logger log = Logger.getLogger(BOQueryMasterDetailController.class.getName());

    public static Logger getLog() {
        return log;
    }
  
  private ExplorerManager detailManager;

  private ExplorerManager manager;
  
  private Class bundleResolver;
  
  private BONode boNode;

  /** Creates a new instance of BOQueryMasterDetailView */
  public BOQueryMasterDetailController(ExplorerManager manager, BONode boNode, Class bundleResolver) {
    this.bundleResolver = bundleResolver;
    this.boNode = boNode;
    if (manager != null){
      this.manager = manager;
    } else{
      this.manager = new ExplorerManager();
    }
    manager.addVetoableChangeListener(this);
  }

 
  public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
    if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
      refreshDetail((Node[]) evt.getNewValue());
    }
  }

  protected void refreshDetail(Node[] nodes) {
//    synchronized(semaphore){
      

      try{
          BasicEntity e;
        BOEntityNodeReadOnly entityNode;

        if (nodes != null && nodes.length > 0) {
          try {
            // if more than one node is selected, take the last
            Node n = nodes[nodes.length -1];
            BOQueryNode parent = null;

            if (n.getParentNode() instanceof BOQueryNode){
                parent = (BOQueryNode)n.getParentNode();
            }
            if (n == null){
                        getLog().warning("Null Node cannot be updated");
              return;
            }
            else if (n instanceof BOMasterNode) {
              if (parent == null){
                e = null;  
              } else{
                  e = parent.update(((BOMasterNode) n).getId());
              }
              if (e == null){
                entityNode = null;
                            this.getDetailManager().setSelectedNodes(new Node[]{});
              } else{
                entityNode = new BOEntityQueryNode(e);     
                            this.getDetailManager().setRootContext(entityNode);
                            this.getDetailManager().setSelectedNodes(new Node[]{entityNode});
              }
            } else if (n instanceof BOEntityQueryNode) {
              entityNode = (BOEntityQueryNode) n;
                        this.getDetailManager().setRootContext(entityNode);
                        this.getDetailManager().setSelectedNodes(new Node[]{entityNode});
            } else if (n instanceof BOBeanNode){
              BOEntityNodeReadOnly boBeanNode = (BOEntityNodeReadOnly)n;
              entityNode = new BOEntityQueryNode(boBeanNode.getBo());
                        this.getDetailManager().setRootContext(entityNode);
                        this.getDetailManager().setSelectedNodes(new Node[]{entityNode});
            }
          } catch(BusinessObjectRemovedException ore){
                    ExceptionAnnotator.annotate(ore);
          } catch (Throwable t) {
                    getLog().log(Level.SEVERE, t.getMessage(), t);
          }
        } else {
                getLog().warning("no nodes retrieved");
                this.getDetailManager().setRootContext(new AbstractNode(Children.LEAF));
        }
      }finally{
      }
//    }
  }

  public void refresh() {
//    synchronized(semaphore){
        Node[] nodes;
        nodes = getExplorerManager().getSelectedNodes();
        refreshDetail(nodes);        
//        updateCounter();
//    }
  }

 

  public void propertyChange(PropertyChangeEvent evt) {
//    log.info("property change:" + evt.getSource());
//    log.info("property change:" + evt.getNewValue());
  }

    public ExplorerManager getDetailManager() {
        return detailManager;
    }

    public ExplorerManager getManager() {
        return manager;
    }

    public Class getBundleResolver() {
        return bundleResolver;
    }

    public BONode getBoNode() {
        return boNode;
    }

    public ExplorerManager getExplorerManager() {
        return manager;
    }
}
