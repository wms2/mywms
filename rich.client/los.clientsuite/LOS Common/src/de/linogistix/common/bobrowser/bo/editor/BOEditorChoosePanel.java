/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.bo.editor;

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
import java.awt.Graphics;
import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import org.mywms.model.BasicEntity;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author  trautm
 */
public class BOEditorChoosePanel extends javax.swing.JPanel implements PropertyChangeListener, ExplorerManager.Provider {

    private static final Logger log = Logger.getLogger(BOEditorChoosePanel.class.getName());
    protected BOEntityNodeReadOnly node;
//  protected BOQueryTopComponent queryComponent;
    protected BOEditorChoose editor;
    BONode boNode;
    BODTO selected;
    protected ExplorerManager manager;
    
    protected Icon eraseIcon = new javax.swing.ImageIcon(
                                    IconResolver.class.getResource(
                                    "/de/linogistix/common/res/icon/Erase.png"));

    /** Creates new form BOEditorPanel */
    public BOEditorChoosePanel(BOEditorChoose editor) {
        Class c;

        try {
            this.editor = editor;
            this.node = editor.getBoBeanNode();
            initComponents();

            Object o = editor.getTypeHint();
            if (o == null || (!(o instanceof Class))) {
                throw new RuntimeException("No class found");
            }
            c = (Class) o;
            BOLookup l = (BOLookup) Lookup.getDefault().lookup(BOLookup.class);
            BO bo = (BO) l.lookup(c);
            boNode = new BONode(bo);
            initEditorComponent(boNode, null);
            initHeader();
            initChoosenField();
        } catch (Throwable t) {
            log.log(Level.SEVERE, t.getMessage(), t);
            throw new RuntimeException(t);
        }
    }

    /** Creates new form BOEditorPanel */
    public BOEditorChoosePanel(Class boClass) {

        try {

            initComponents();
            
            previewPanel.setToolTipText(null);
            jButtonUnlink.setToolTipText(null);

            BOLookup l = (BOLookup) Lookup.getDefault().lookup(BOLookup.class);
            BO bo = (BO) l.lookup(boClass);
            boNode = new BONode(bo);
            initEditorComponent(boNode, null);
            node = new BOEntityNodeReadOnly((BasicEntity) bo.getBoBeanNodeTemplate().getBean());

            initHeader();
            initChoosenField();
        } catch (Throwable t) {
            log.log(Level.SEVERE, t.getMessage(), t);
            throw new RuntimeException(t);
        }
    }

    public BOEditorChoosePanel(Class entityClass, BOQueryModel model) {

        try {

            initComponents();
            
            previewPanel.setToolTipText(null);
            jButtonUnlink.setToolTipText(null);

            BOLookup l = (BOLookup) Lookup.getDefault().lookup(BOLookup.class);
            BO bo = (BO) l.lookup(entityClass);
            boNode = new BONode(bo);
            
            initEditorComponent(boNode, model);
            
            node = new BOEntityNodeReadOnly((BasicEntity) bo.getBoBeanNodeTemplate().getBean());

            initHeader();
            initChoosenField();
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
        
        final BOQueryPanel queryPanel;
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
                queryPanel.showCorrect();
                queryPanel.getMasterDetailView().refresh();
            }
        });
      
    }

    public void setSelection(String name){
        
        Node root = manager.getRootContext();
        
        Node[] childs = root.getChildren().getNodes();
        
        Node selectNode = null;
        
        for(Node n:childs){
            if(n.getName().equalsIgnoreCase(name)){
                selectNode = n;
            }
        }
        
        if(selectNode != null){
            try {
                manager.setSelectedNodes(new Node[]{selectNode});
            } catch (PropertyVetoException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
    
    public ExplorerManager getExplorerManager() {
        if (this.manager == null) {
            this.manager = new ExplorerManager();
        }
        return this.manager;
    }

    private void initChoosenField() {

        BODTO tmp = null;
        if (this.node != null) {
            tmp = new BODTO(this.node.getBo().getId(),
                    this.node.getBo().getVersion(),
                    this.node.getDisplayName());
        }
        selected = (tmp);
    }

    public BODTO getBoDTO() {
        return selected;
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
        jLabelSelected = new javax.swing.JLabel();
        jTextFieldSelected = new javax.swing.JTextField();
        jButtonUnlink = new javax.swing.JButton();

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

        jLabelSelected.setText("null");
        previewPanel.add(jLabelSelected);

        jTextFieldSelected.setEditable(false);
        jTextFieldSelected.setText("null");
        jTextFieldSelected.setMinimumSize(new java.awt.Dimension(120, 19));
        jTextFieldSelected.setPreferredSize(new java.awt.Dimension(120, 19));
        previewPanel.add(jTextFieldSelected);

        jButtonUnlink.setIcon(eraseIcon);
        jButtonUnlink.setToolTipText("null");
        jButtonUnlink.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButtonUnlink.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUnlinkActionPerformed(evt);
            }
        });
        previewPanel.add(jButtonUnlink);

        add(previewPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void link() {
        try {
            Node[] nodes;

            nodes = manager.getSelectedNodes();
//              queryComponent.getExplorerManager().getSelectedNodes();

            if (nodes == null || nodes.length == 0) {
//                unlink();
                return;
            }
            BODTO dto;

            if (nodes[0] instanceof BOMasterNode) {
                BOMasterNode m = (BOMasterNode) (nodes[0]);
                dto = new BODTO(m.getEntity().getId(), m.getEntity().getVersion(), m.getDisplayName());
            } else if (nodes[0] instanceof BOEntityNode) {
                BOEntityNode b = (BOEntityNode) nodes[0];
                dto = new BODTO(b.getBo().getId(), b.getBo().getVersion(), b.getBo().toUniqueString());
            } else {
//          throw new IllegalArgumentException();
                return;
            }
            if (editor != null) {
                editor.setValue(dto);
            }
            selected = dto;
            jTextFieldSelected.setText(selected.getName());
        } catch (Throwable ex) {
            ExceptionAnnotator.annotate(ex);
        }
    }

    private void unlink() {
        try {
            if (editor != null) {
                editor.setValue(null);
            }
            selected = null;
            jTextFieldSelected.setText(NbBundle.getMessage(CommonBundleResolver.class, "notLinked"));
        } catch (Throwable ex) {
            ExceptionAnnotator.annotate(ex);
        }
    }

  private void editorPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editorPanelMouseClicked
  }//GEN-LAST:event_editorPanelMouseClicked

  private void jButtonUnlinkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUnlinkActionPerformed
      unlink();
}//GEN-LAST:event_jButtonUnlinkActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JPanel editorPanel;
    protected javax.swing.JPanel headerPanel;
    private javax.swing.JButton jButtonUnlink;
    private javax.swing.JLabel jLabelSelected;
    private javax.swing.JTextField jTextFieldSelected;
    private javax.swing.JPanel previewPanel;
    // End of variables declaration//GEN-END:variables

    public void propertyChange(PropertyChangeEvent evt) {
        if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
            if (manager.getSelectedNodes() != null && manager.getSelectedNodes().length == 1){
                link();
            }
        }
    }

    private BODTO getPropertyValue() {
        Node[] nodes = manager.getSelectedNodes();
        if (nodes.length > 0) {
            BOMasterNode n = (BOMasterNode) nodes[0];
            return n.getEntity();
        }
        return null;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }
//  class PropertySelect extends PropertySupport.ReadWrite{
//
//    BODTO dto;
//    
//    PropertySelect(BODTO dto){
//      super("choose", BODTO.class, "choose","");
//      if (dto == null){
//        dto = new BODTO(1L,1,"...");
//      }
//      this.dto = dto;
//      setValue("suppressCustomEditor", Boolean.TRUE);
////      setValue("valueIcon", new javax.swing.ImageIcon(getClass().getResource("/de/linogistix/bobrowser/res/icon/Erase.png")));
//    }
//    
//    @Override
//    public Object getValue() throws IllegalAccessException, InvocationTargetException {
//      return dto;
//    }
//
//    @Override
//    public void setValue(Object arg0) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//      if (arg0 != null){
//        this.dto = (BODTO)arg0;
//      } else{
//        this.dto = null;
//      }
//    }
//    
//    @Override
//    public PropertyEditor getPropertyEditor() {
//      return new BODTOEditor();
//    }
//  }


}
