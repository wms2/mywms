/*
 * BOQueryNode.java
 *
 * Created on 25. Juli 2006, 18:52
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.bobrowser.query;

import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.bobrowser.bo.binding.BOBeanNodeDescriptor;

import de.linogistix.common.bobrowser.bo.BOEntityNodeReadOnly;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.BasicEntity;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 * 
 * BOQueryNode: A node that can be viewed in an explorer view. One child
 * represents one instance (object) of BasicEntity, eg. StorageLocation via
 * BOEntityNodeReadOnly.
 * 
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOQueryNode extends AbstractNode {

    //public static final Class syncSem = BOQueryNode.class;
    
    private static final Logger log = Logger.getLogger(BOQueryNode.class.getName());
    public final static String PROP_QUERY_UPDATE = "PROP_QUERY_UPDATE";
    public final static String PROP_BO_UPDATE = "PROP_BO_UPDATE";
    private BOQueryModel model;
    private Long lastUpdated;
//  private List<SystemAction> childActions;
    private boolean exceptionNoResult;
    private BOQueryByTemplateNode templateNode;
    protected Object semaphore = new Object();

    public BOQueryNode(BOQueryModel model, BOBeanNodeDescriptor desc, Class bundleResolver) {
        super(new BOQueryNodeChildren(model, desc, bundleResolver));
        try {
            this.setModel(model);            
            setDisplayName(model.getBoNode().getDisplayName());            
            this.templateNode = new BOQueryByTemplateNode((BasicEntity) getModel().getBoNode().getBusinessObjectTemplate());
        } catch (Throwable ex) {
            ExceptionAnnotator.annotate(ex);
        }
    }

    /**
     *@param exceptionNoResult if true, a BOQueryNoResultException is thrown when the result set of query is empty
     */
    public BOQueryNode(BOQueryModel model, BOBeanNodeDescriptor desc, Class bundleResolver, boolean exceptionNoResult) {
        this(model, desc, bundleResolver);
        this.exceptionNoResult = exceptionNoResult;
    }

    /**
     * Updates only the given entity selected in the ExplorerView
     */
    public BasicEntity update(Long id) throws BusinessObjectRemovedException {

            BasicEntity updated = null;
            
            BusinessObjectQueryRemote service = getModel().getBoNode().getQueryService();

            try {
                updated = service.queryById(id);
                
                setLastUpdated(id);
                
                return updated;    
                
            } catch(BusinessObjectNotFoundException nfe){
                
                throw new BusinessObjectRemovedException(id.toString());
    
            } catch (FacadeException ex) {
                ExceptionAnnotator.annotate(ex);
                return null;
            } catch (Throwable t) {
                ExceptionAnnotator.annotate(t);
                return null;
            }
    }

    /**
     *@return Array of {@link BasicEntity} currently represented in ExplorerView
     */
        public Object[] getEntities() {

        BasicEntity ret[];
        BOQueryNodeChildren keys;
        keys = (BOQueryNodeChildren) getChildren();

        return (Object[]) keys.getKeys().toArray(new Object[0]);

    }
        
    /**
     * Rebuilds list shown in ExplorerView to represent given entities.
     *
     * Use this method to determine a result list explicitly instead of fetching
     * entities from the database.
     *
     *@throws BOQueryNoResultException when List entities is empty or null and exceptionNoResult is set to true during construction
     *
     */
    public void update(List<BODTO> entities) throws BOQueryNoResultException {

//        synchronized (semaphore) {
            BOQueryNodeChildren keys;

            try {
                keys = (BOQueryNodeChildren) getChildren();
                if (entities == null || entities.size() == 0) {
                    keys.update(new ArrayList());
                    //log.info("updated empty");
                    if (exceptionNoResult) {
                        throw new BOQueryNoResultException();
                    }
                }

                keys.update(entities);
                firePropertyChange("UPDATED_LIST", null, null);
            } catch (FacadeException ex) {
                ExceptionAnnotator.annotate(ex);
            } catch (Throwable t){
                ExceptionAnnotator.annotate(t);
            }
//        }
    }

    /**
     * Rebuilds list shown in ExplorerView by fetching Entities from the database
     * according to the query defined in this objects {@link BOQueryModel}.
     *
     *@throws BOQueryNoResultException if the result set of query is empty and exceptionNoResult is set to true during construction
     */
    public void update() throws BOQueryNoResultException {
//        synchronized (semaphore) {
            List list;
            BOQueryNodeChildren keys;
            Node[] selected = null;
            log.info("*** update all");

            try {
                selected = getModel().getManager().getSelectedNodes();
                list = getModel().getResults();
                keys = (BOQueryNodeChildren) getChildren();
                if (list == null || list.size() == 0) {
                    keys.update(new ArrayList());
                    //log.info("updated empty");
                    if (exceptionNoResult) {
                        throw new BOQueryNoResultException();
                    }
                } else {
                    keys.update(list);
                }
            } catch (BOQueryNoResultException ex) {
                log.severe(ex.getMessage());
                log.log(Level.INFO, ex.getMessage(), ex);
                throw ex;
            } catch (Throwable ex) {
                ExceptionAnnotator.annotate(ex);
            } finally {
                getModel().setSelected(selected);
            }
//        }
    }

    public BOQueryModel getModel() {
        return model;
    }

    public void setModel(BOQueryModel model) {
        this.model = model;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

//  public List<SystemAction> getChildActions() {
//    return childActions;
//  }
//
//  public void setChildActions(List<SystemAction> childActions) {
//    this.childActions = childActions;
//  }
    public BOQueryByTemplateNode getTemplateNode() {
        return templateNode;
    }

    
    
    /**
     * Represent Entities as Children (i.e. {@link BOEntityNodeReadOnly}) of BOQueryNode
     * 
     * Based on code of Chris Palmer to speed up explorer View,
     * see {@link http://openide.netbeans.org/servlets/ReadMsg?listName=dev&msgNo=20306}
     */
    static class BOQueryNodeChildren extends Children.Keys {

        private List<BODTO> keys;
        Class bundleResolver;
        BOBeanNodeDescriptor descriptor;
        BOQueryModel model;
        
        protected Object semaphore = new Object();

        BOQueryNodeChildren(BOQueryModel model, BOBeanNodeDescriptor descriptor, Class bundleResolver) {
            super();
            this.model = model;
            this.descriptor = descriptor;
            this.bundleResolver = bundleResolver;
        }

        protected void addNotify() {
            
            
            log.info("addNotify # nodes : " + getKeys().size() + "@" + Thread.currentThread().getName());
           
            if (this.model.isInitialized()) {
                update();
            } else {
                log.warning("model is not initialized yet");
            }
            
        }

        protected void removeNotify() {
            log.warning("remove Notify ?????");
            setKeys(Collections.EMPTY_SET);
        }

        @Override
        public Node[] getNodes(boolean arg0) {
            return super.getNodes(arg0);
        }

        
        protected Node[] createNodes(Object object) {

            AbstractNode node;
            if (object == null) {
                return new Node[0];
            }

            try {
                if (object == null) {
                    throw new NullPointerException();
                }
                
                BODTO to;
                         
                if (object instanceof BODTO) {
                    to = (BODTO) object;
                } else{
                    log.severe("Wrong type of argument " + object.toString() + "@" + Thread.currentThread().getName());
                    return new Node[0];
                }
                /*
                 * Creates "dummy" nodes with basic information for showing in a MasterView
                 */
                if (this.model.getBoNode().getBo().getBoMasterNodeType().equals(BOMasterNode.class)) {
                    BOMasterNode m = new BOMasterNode(to, this.model.getBoNode().getBo());
                    m.setBo(model.getBoNode().getBo());
                    node = m;
                } else {
                    Constructor n;
                    Class c = this.model.getBoNode().getBo().getBoMasterNodeType();
                    try {
                        n = c.getConstructor(new Class[]{BODTO.class, BO.class});
                        BOMasterNode m = (BOMasterNode) n.newInstance(new Object[]{to, this.model.getBoNode().getBo()});
                        m.setBo(model.getBoNode().getBo());
                        node = m;
                    } catch (NoSuchMethodException ex) {
                        n = c.getConstructor(new Class[]{BODTO.class});
                        BOMasterNode m = (BOMasterNode) n.newInstance(new Object[]{to});
                        m.setBo(model.getBoNode().getBo());
                        node = m;
                    }
                }
                return new Node[]{node};
            } catch (Throwable ex) {
                log.log(Level.SEVERE, ex.getMessage(), ex);
                ExceptionAnnotator.annotate(ex);
                return new Node[0];
            }            

        }

        void update() {
//            synchronized (semaphore) {
                try {
                    keys = model.getResults();
                    update(keys);
                } catch (Throwable t) {
                    log.log(Level.SEVERE, t.getMessage(), t);
                    ExceptionAnnotator.annotate(t);
                }
//            }
        }

        void update(List entities) {
//            synchronized (semaphore) {
                try {
                    
                    List<BODTO> tmp = new ArrayList<BODTO>();
                    
                    if (entities != null && entities.size() > 0){ 
                        BusinessObjectQueryRemote query = this.model.getBoNode().getBo().getQueryService();
                        Constructor n;
                        Class c = query.getBODTOClass();
                        boolean nativCons = false; 
                        try {
                            n = c.getConstructor(new Class[]{query.getBoClass()});
                        } catch (NoSuchMethodException ex) {
                            nativCons = true;
                            n = c.getConstructor(new Class[]{Long.class, int.class, String.class});
                        }
                        for (Object o : entities) {
                            
                            if (o == null){
                                log.warning("null reference");
                                continue;
                            }
                                    
                            BODTO to;
                            if (o instanceof BODTO) {
                                to = (BODTO) o;
                            } else if (o instanceof BasicEntity) {
                                BasicEntity e = (BasicEntity) o;
                                if (nativCons )
                                     to = (BODTO) n.newInstance(new Object[]{e.getId(), e.getVersion(), e.toUniqueString()});
                                else
                                    to = (BODTO) n.newInstance(new Object[]{e});
                            } else {
                                log.warning("Unexpected type: " + o.getClass());
                                continue;
                            }
                            tmp.add(to);
                        }
                    }
                    
                    //log.info("updated keys: " + keys.size() ); 
                    keys = tmp;
                    setKeys(keys);
                    
                } catch (Throwable t) {
                    log.log(Level.SEVERE, t.getMessage(), t);
                    ExceptionAnnotator.annotate(t);
                }
//            }
        }

        public List getKeys() {
            if (keys == null) {
                return new ArrayList();
            }
            return keys;
        }
    }

//  static class BasicEntityWrapper {
//
//    BODTO entity;
//
//    BasicEntityWrapper(BODTO e) {
//      this.entity = e;
//    }
//
//    public BODTO getEntity() {
//      return entity;
//    }
//
//    public boolean equals(Object obj) {
//      if (obj == null) {
//        return false;
//      } else if (this == obj) {
//        return true;
//      } else if (obj instanceof BasicEntityWrapper) {
//        BasicEntityWrapper w = (BasicEntityWrapper) obj;
//        if (w.getEntity().getId().equals(getEntity().getId()) && (w.getEntity().getVersion() == getEntity().getVersion())) {
//          return true;
//        } else {
//          return false;
//        }
//      } else {
//        return false;
//      }
//
//
//
//    }
//  }
}

