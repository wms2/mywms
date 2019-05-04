/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.bo.editor;

import de.linogistix.common.bobrowser.api.BOLookup;
import de.linogistix.common.bobrowser.bo.BOEntityNode;
import de.linogistix.common.bobrowser.bo.BOEntityNodeReadOnly;
import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.bo.BONode;
import de.linogistix.common.bobrowser.query.BOQueryMasterDetailView;
import de.linogistix.common.bobrowser.query.BOQueryModel;
import de.linogistix.common.bobrowser.query.BOQueryNoResultException;
import de.linogistix.common.bobrowser.query.BOQueryNode;
import de.linogistix.common.bobrowser.query.BOQueryPanel;
import de.linogistix.common.exception.VetoException;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.res.icon.IconResolver;
import de.linogistix.common.util.BundleResolve;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.query.BODTO;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import org.mywms.model.BasicEntity;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.ListView;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 * An Editor for a Collection of BasicEntities
 *
 * @author  trautm
 */
public class BOCollectionEditorPanel
        extends javax.swing.JPanel
        implements ExplorerManager.Provider {

    private final static Logger log = Logger.getLogger(BOCollectionEditorPanel.class.getName());
    BOCollectionEditor editor;
    ExplorerManager em;
    BOQueryPanel boQueryPanel;
    BOLookup l;
    BONode boNode;
    BO bo;
    BOQueryNode queryNode;
    BOQueryModel queryModel;
    JScrollPane jScrollPaneList;
    Class typehint;
    String displayName;

    BOCollectionEditorSelectionListener mySelectionListener;
    
    private boolean singleSelection = false;
    
    protected Icon addIcon = new javax.swing.ImageIcon(
                                    IconResolver.class.getResource(
                                    "/de/linogistix/common/res/icon/Add.png"));
    
    protected Icon removeIcon = new javax.swing.ImageIcon(
                                    IconResolver.class.getResource(
                                    "/de/linogistix/common/res/icon/Remove.png"));
    
    protected Icon upIcon = new javax.swing.ImageIcon(
                                    IconResolver.class.getResource(
                                    "/de/linogistix/common/res/icon/up.png"));
    
    protected Icon downIcon = new javax.swing.ImageIcon(
                                    IconResolver.class.getResource(
                                    "/de/linogistix/common/res/icon/down.png"));
    
    public BOCollectionEditorPanel(Class typeHint, String displayName) {
        this.typehint = typeHint;
        this.displayName = displayName;

        this.em = new ExplorerManager();

        initComponents();
        initModel();
        initQuery();
        initDisplayName();
        initList();
        initHeader();

        this.em.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                log.info("received " + evt.toString());
                onPropertyChangeList(evt);
            }
        });

        if (this.queryNode.getEntities().length > 0) {
            jScrollPaneList.requestFocusInWindow();
            enableQueryButtons(false);
        } else {
            boQueryPanel.getMasterDetailView().getMasterView().requestFocusInWindow();
            enableQueryButtons(true);
        }

    }

    /**
     * Creates new form BOCollectionEditorPanel
     */
    public BOCollectionEditorPanel(BOCollectionEditor editor) {
        this.editor = editor;
        this.typehint = editor.getTypeHint();
        this.em = new ExplorerManager();

        initComponents();
        
        
        jSplitPaneEditor.setToolTipText(null);
        buttonAdd.setToolTipText(null);
        buttonDelete.setToolTipText(null);

        
        initModel();
        initQuery();
        initDisplayName();
        initList();
        initHeader();

        this.em.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                log.info("received " + evt.toString());
                onPropertyChangeList(evt);
            }
        });

        if (this.queryNode.getEntities().length > 0) {
            jScrollPaneList.requestFocusInWindow();
            enableQueryButtons(false);
        } else {
            boQueryPanel.getMasterDetailView().getMasterView().requestFocusInWindow();
            enableQueryButtons(true);
        }

    }

    public void setEntities(List<BODTO> sus) {
        try {

            queryNode.update(sus);
        } catch (BOQueryNoResultException ex) {
            // if we want to set an empty list, we will do it
        }
       
    }

    protected void initDisplayName() {

        String s;

        if (displayName == null) {
            s = editor.getEnv().getFeatureDescriptor().getDisplayName();
            if (s != null && s.length() != 0) {
                s = BundleResolve.resolve(new Class[]{bo.getBundleResolver()}, s, new Object[0]);
            //      labelProperty.setText(s);
            }
        }
    }

    protected void onPropertyChangeList(PropertyChangeEvent evt) {

        synchronized (this) {

            if (this.em.getSelectedNodes() == null || this.em.getSelectedNodes().length == 0) {
                return;
            }

            if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {

                enableQueryButtons(false);

                try {
                    boQueryPanel.getExplorerManager().setSelectedNodes(new Node[0]);
                } catch (PropertyVetoException ex) {
                    log.log(Level.SEVERE, ex.getMessage(), ex);
                }
                if (boQueryPanel != null) {
                    final BOQueryMasterDetailView mdv;

                    mdv = (BOQueryMasterDetailView) boQueryPanel.getMasterDetailView();
                    if (this.em.getSelectedNodes().length > 0) {
                        if (this.em.getSelectedNodes()[0] instanceof BOMasterNode) {
                            BOMasterNode n = (BOMasterNode) this.em.getSelectedNodes()[0];
                            final BOEntityNodeReadOnly en;
                            try {
                                en = new BOEntityNodeReadOnly(queryNode.update(n.getId()));
                                SwingUtilities.invokeLater(new Runnable() {

                                    public void run() {
                                        mdv.getDetailView().setNode(en);
                                    }
                                });

                            } catch (Throwable ex) {
                                log.log(Level.SEVERE, ex.getMessage(), ex);
                                ExceptionAnnotator.annotate(ex);
                            }
                        } else if (this.em.getSelectedNodes()[0] instanceof BOEntityNodeReadOnly) {
                            SwingUtilities.invokeLater(new Runnable() {

                                public void run() {
                                    mdv.getDetailView().setNode(em.getSelectedNodes()[0]);
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    protected void onPropertyChangeQuery(PropertyChangeEvent evt) {

        enableQueryButtons(true);

        try {
            this.em.setSelectedNodes(new Node[0]);
        } catch (PropertyVetoException ex) {
            log.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private void initModel() {

        Node tmp;

        try {

            l = (BOLookup) Lookup.getDefault().lookup(BOLookup.class);

            bo = (BO) l.lookup(this.typehint);

            boNode = new BONode(bo);
            queryModel = new BOQueryModel(boNode);
            queryNode = new BOQueryNode(queryModel, bo.getDescriptor(), CommonBundleResolver.class, false);
            if (editor != null && editor.getEntities() != null) {
                queryNode.update(new ArrayList(editor.getEntities()));
            } else {
                queryNode.update(new ArrayList());
            }

            getExplorerManager().setRootContext(queryNode);

            if (queryNode.getChildren() != null && queryNode.getChildren().getNodesCount() > 0) {
                tmp = queryNode.getChildren().getNodes()[0];
                getExplorerManager().setSelectedNodes(new Node[]{tmp});
            }
        } catch (Throwable ex) {
            log.log(Level.SEVERE, ex.getMessage(), ex);
            ExceptionAnnotator.annotate(ex);
        }
    }

    private void initList() {

        ListView listView;
        listView = new ListView();
        jScrollPaneList = listView;
        listPanel.add(jScrollPaneList);

    }

    protected void initHeader() {

        if (editor != null && editor.getBOBeanNode() != null){
            BOEditorHeader h = new BOEditorHeader(editor.getBOBeanNode());
            headerPanel.add(h);
        } else{
            BOEditorHeader h = new BOEditorHeader((BOEntityNode) bo.getBoBeanNodeTemplate());
            headerPanel.add(h);
        }
        
    }

    private void initQuery() {

        try {
            boQueryPanel = new BOQueryPanel(boNode, false);
        
            queryPanel.add(boQueryPanel);
            boQueryPanel.getExplorerManager().addPropertyChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    log.info("received " + evt.toString());
                    onPropertyChangeQuery(evt);
                }
            });
            
            
        } catch (Throwable ex) {
            log.log(Level.SEVERE, ex.getMessage(), ex);
            ExceptionAnnotator.annotate(ex);
        }
        
        SwingUtilities.invokeLater(new Runnable() {

                public void run() {

        boQueryPanel.reload();

//                    boQueryPanel.invalidate();//TODO: flicker BUG
    }
            });
    }

    public List<BODTO> getEntities() {
        ArrayList<BODTO> list;
        list = new ArrayList<BODTO>();

        if (queryNode.getEntities() == null) {
            return list;
        }

        for (Object e : queryNode.getEntities()) {
            
            if(e instanceof BasicEntity){
                BasicEntity be = (BasicEntity) e;
                list.add(new BODTO(be.getId(), be.getVersion(), be.toUniqueString()));
            }
            else{
                list.add((BODTO) e);
            }
        }

        return list;
    }

    public ExplorerManager getExplorerManager() {
        return this.em;
    }
    
    public boolean isSingleSelection() {
        return singleSelection;
    }

    public void setSingleSelection(boolean singleSelection) {
        this.singleSelection = singleSelection;
    }
    
    public BOCollectionEditorSelectionListener getSelectionListener() {
        return mySelectionListener;
    }

    public void setSelectionListener(BOCollectionEditorSelectionListener mySelectionListener) {
        this.mySelectionListener = mySelectionListener;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        headerPanel = new javax.swing.JPanel();
        editorPanel = new javax.swing.JPanel();
        jSplitPaneEditor = new javax.swing.JSplitPane();
        queryPanel = new javax.swing.JPanel();
        listPanel = new javax.swing.JPanel();
        listPanePanel = new javax.swing.JPanel();
        buttonPanel = new javax.swing.JPanel();
        buttonAdd = new javax.swing.JButton();
        buttonDelete = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(800, 400));
        setLayout(new java.awt.BorderLayout());

        headerPanel.setPreferredSize(new java.awt.Dimension(600, 50));
        headerPanel.setLayout(new java.awt.BorderLayout());
        add(headerPanel, java.awt.BorderLayout.NORTH);

        editorPanel.setLayout(new java.awt.BorderLayout());

        jSplitPaneEditor.setToolTipText("null");

        queryPanel.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                queryPanelFocusGained(evt);
            }
        });
        queryPanel.setLayout(new javax.swing.BoxLayout(queryPanel, javax.swing.BoxLayout.LINE_AXIS));
        jSplitPaneEditor.setRightComponent(queryPanel);

        listPanel.setMaximumSize(new java.awt.Dimension(150, 2147483647));
        listPanel.setMinimumSize(new java.awt.Dimension(150, 39));
        listPanel.setPreferredSize(new java.awt.Dimension(150, 322));
        listPanel.setLayout(new java.awt.BorderLayout());

        listPanePanel.setMaximumSize(new java.awt.Dimension(100, 2147483647));
        listPanePanel.setMinimumSize(new java.awt.Dimension(100, 0));
        listPanePanel.setPreferredSize(new java.awt.Dimension(100, 0));
        listPanePanel.setLayout(new java.awt.BorderLayout());
        listPanel.add(listPanePanel, java.awt.BorderLayout.CENTER);

        buttonPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        buttonPanel.setMinimumSize(new java.awt.Dimension(244, 39));
        buttonPanel.setLayout(new java.awt.GridBagLayout());

        buttonAdd.setIcon(addIcon);
        buttonAdd.setToolTipText("null");
        buttonAdd.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonAdd.setMaximumSize(new java.awt.Dimension(22, 150));
        buttonAdd.setMinimumSize(new java.awt.Dimension(22, 150));
        buttonAdd.setPreferredSize(new java.awt.Dimension(22, 150));
        buttonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        buttonPanel.add(buttonAdd, gridBagConstraints);

        buttonDelete.setIcon(removeIcon);
        buttonDelete.setToolTipText("null");
        buttonDelete.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonDelete.setMaximumSize(new java.awt.Dimension(22, 150));
        buttonDelete.setMinimumSize(new java.awt.Dimension(22, 150));
        buttonDelete.setPreferredSize(new java.awt.Dimension(22, 150));
        buttonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        buttonPanel.add(buttonDelete, gridBagConstraints);

        listPanel.add(buttonPanel, java.awt.BorderLayout.EAST);

        jSplitPaneEditor.setLeftComponent(listPanel);

        editorPanel.add(jSplitPaneEditor, java.awt.BorderLayout.CENTER);

        add(editorPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private synchronized void enableQueryButtons(boolean enable) {
        buttonAdd.setEnabled(enable);
        buttonDelete.setEnabled(!enable);
    }

  private void queryPanelFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_queryPanelFocusGained
      enableQueryButtons(true);
      
  }//GEN-LAST:event_queryPanelFocusGained

  private void buttonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteActionPerformed
      Node[] nodes = getExplorerManager().getSelectedNodes();
      List<BODTO> tmp = getEntities();

      for (Node n : nodes) {
          if (n instanceof BOMasterNode) {
              BOMasterNode en = (BOMasterNode) n;
              if(mySelectionListener != null){
                    try {
                        mySelectionListener.removeSelection(en.getEntity());
                        
                        tmp.remove(en.getEntity());
                        log.log(Level.INFO, " added to collection " + en.getEntity());
                        
                    } catch (VetoException ex) {  }
              }
              else{
                tmp.remove(en.getEntity());
              }
          } 
      }
      try {

          queryNode.update(tmp);
      } catch (BOQueryNoResultException ex) {
          // ok - the list might be empty
      }
      if (editor != null) {
          editor.setValue(getEntities());
      }

  }//GEN-LAST:event_buttonDeleteActionPerformed

  private void buttonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddActionPerformed
      Node[] nodes = boQueryPanel.getExplorerManager().getSelectedNodes();
      List<BODTO> tmp = getEntities();

      if(singleSelection && tmp.size()>0){
          return;
      }
      
      if (nodes == null || nodes.length == 0) {
          return;
      }

      for (Node n : nodes) {
          BOMasterNode en = (BOMasterNode) n;
          
          if (!tmp.contains(en.getEntity())) {
              
              if(mySelectionListener != null){
                    try {
                        mySelectionListener.addSelection(en.getEntity());
                        
                        tmp.add(en.getEntity());
                        log.log(Level.INFO, " added to collection " + en.getEntity());
                        
                    } catch (VetoException ex) {  }
              }
              else{
                  tmp.add(en.getEntity());
                  log.log(Level.INFO, " added to collection " + en.getEntity());
              }
              
          }
      }
      try {
          queryNode.update(tmp);
      } catch (BOQueryNoResultException ex) {
          // ok - the list might be empty
      }
      if (editor != null) {
          editor.setValue(getEntities());
      }
  }//GEN-LAST:event_buttonAddActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAdd;
    private javax.swing.JButton buttonDelete;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JPanel editorPanel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JSplitPane jSplitPaneEditor;
    private javax.swing.JPanel listPanePanel;
    private javax.swing.JPanel listPanel;
    private javax.swing.JPanel queryPanel;
    // End of variables declaration//GEN-END:variables
}
