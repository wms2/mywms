/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls;

import de.linogistix.common.bobrowser.bo.editor.*;
import de.linogistix.common.bobrowser.api.BOLookup;
import de.linogistix.common.bobrowser.bo.*;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.bo.BONode;
import de.linogistix.common.bobrowser.query.BOQueryModel;
import de.linogistix.common.bobrowser.query.BOQueryPanel;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.res.icon.IconResolver;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.query.BODTO;
import java.awt.BorderLayout;
import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import org.mywms.model.BasicEntity;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author  trautm
 */
public class BOMultiSelectionChoosePanel extends javax.swing.JPanel implements PropertyChangeListener, ExplorerManager.Provider {

    private static final Logger log = Logger.getLogger(BOMultiSelectionChoosePanel.class.getName());
    protected BOEntityNodeReadOnly node;
    protected BOQueryPanel queryPanel;

    BONode boNode;
    
    private List<BODTO> selectedValues = new ArrayList<BODTO>();
    
    protected ExplorerManager manager;
    
    protected Icon eraseIcon = new javax.swing.ImageIcon(
                                    IconResolver.class.getResource(
                                    "/de/linogistix/common/res/icon/Erase.png"));

    /** Creates new form BOEditorPanel */
    public BOMultiSelectionChoosePanel(Class boClass) {

        try {

            initComponents();
            
            previewPanel.setToolTipText(null);

            BOLookup l = (BOLookup) Lookup.getDefault().lookup(BOLookup.class);
            BO bo = (BO) l.lookup(boClass);
            boNode = new BONode(bo);
            initEditorComponent(boNode, null);
            node = new BOEntityNodeReadOnly((BasicEntity) bo.getBoBeanNodeTemplate().getBean());

            initHeader();
            
        } catch (Throwable t) {
            log.log(Level.SEVERE, t.getMessage(), t);
            throw new RuntimeException(t);
        }
    }

    public BOMultiSelectionChoosePanel(Class entityClass, BOQueryModel model) {

        try {

            initComponents();
            BOLookup l = (BOLookup) Lookup.getDefault().lookup(BOLookup.class);
            BO bo = (BO) l.lookup(entityClass);
            boNode = new BONode(bo);
            
            initEditorComponent(boNode, model);
            
            node = new BOEntityNodeReadOnly((BasicEntity) bo.getBoBeanNodeTemplate().getBean());

            initHeader();
            
        } catch (Throwable t) {
            log.log(Level.SEVERE, t.getMessage(), t);
            throw new RuntimeException(t);
        }
    }

    private void initHeader() {
        ImageIcon icon;

        if (node != null) {
            icon = new ImageIcon(node.getIcon(BeanInfo.ICON_COLOR_32x32));
        } else {
            try {
                icon = new ImageIcon(getClass().getResource("/" + boNode.getBo().getIconPathWithExtension()));
            } catch (NullPointerException ex) {
                try {
                    icon = new ImageIcon(getClass().getResource(boNode.getBo().getIconPathWithExtension()));
                } catch (NullPointerException nex) {
                    icon = new ImageIcon(getClass().getResource("/de/linogistix/common/res/icon/Document.png"));
                }
            }
        }
        String title = NbBundle.getMessage(CommonBundleResolver.class, "boChoose", new String[]{boNode.getBo().getSingularDisplayName()});
        this.boNode.getBo().getIconPathWithExtension();
        BOEditorHeader h = new BOEditorHeader(
                icon,
                title, "");
        headerPanel.add(h);
    }

    /**
     * 
     * Derived classes need to set ExplorerManager in manager.
     * @param boNode
     */
    protected void initEditorComponent(BONode boNode, BOQueryModel model) {
        
        if(model == null){
            queryPanel = new BOQueryPanel(boNode,false);
        }
        else{
            queryPanel = new BOQueryPanel(boNode, model, false);
        }
        queryPanel.reload();
        queryPanel.getExplorerManager().addPropertyChangeListener(this);
        
                
        manager = queryPanel.getExplorerManager();        
        editorPanel.add(queryPanel, BorderLayout.CENTER);    
        
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                
                queryPanel.getMasterDetailView().refresh();
            }
        });
      
    }

    public List<BODTO> getSelectedValues(){
        return selectedValues;
    }
    
    public ExplorerManager getExplorerManager() {
        if (this.manager == null) {
            this.manager = new ExplorerManager();
        }
        return this.manager;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        headerPanel = new javax.swing.JPanel();
        editorPanel = new javax.swing.JPanel();
        previewPanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        headerPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        add(headerPanel, java.awt.BorderLayout.NORTH);

        editorPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                editorPanelMouseClicked(evt);
            }
        });
        editorPanel.setLayout(new java.awt.BorderLayout());
        add(editorPanel, java.awt.BorderLayout.CENTER);

        previewPanel.setToolTipText("null");
        previewPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        add(previewPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void link() {
        try {
            Node[] nodes;

            nodes = manager.getSelectedNodes();
            selectedValues = new ArrayList<BODTO>(nodes.length);

            if (nodes == null || nodes.length == 0) {
                unlink();
                return;
            }
            
            for(Node n:nodes){
                BODTO dto = null;

                if (n instanceof BOMasterNode) {
                    BOMasterNode m = (BOMasterNode) (n);
                    dto = new BODTO(m.getEntity().getId(), m.getEntity().getVersion(), m.getDisplayName());
                } else if (n instanceof BOEntityNode) {
                    BOEntityNode b = (BOEntityNode) n;
                    dto = new BODTO(b.getBo().getId(), b.getBo().getVersion(), b.getBo().toUniqueString());
                } 
                
                if(dto != null)
                    selectedValues.add(dto);
            }
                       
        } catch (Throwable ex) {
            ExceptionAnnotator.annotate(ex);
        }
    }

    private void unlink() {

       selectedValues = new ArrayList<BODTO>();
       
    }

  private void editorPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editorPanelMouseClicked
  }//GEN-LAST:event_editorPanelMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JPanel editorPanel;
    protected javax.swing.JPanel headerPanel;
    private javax.swing.JPanel previewPanel;
    // End of variables declaration//GEN-END:variables

    public void propertyChange(final PropertyChangeEvent evt) {
        // This is really ugly! But closing the dialog calls the propertyChange with deselection of all.
        // So give the caller the chance to read the selection, before it is killed
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                    link();
                }
            }
        });
    }

}
