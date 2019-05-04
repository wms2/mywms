/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.order.gui.gui_builder;

import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.openide.ErrorManager;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.TreeTableView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author artur
 */
public class AbstractTreeTable_sample extends TreeTableView {
    private RevisionsRootNode rootNode;
    private List results;
    private final JPanel master;
    
public AbstractTreeTable_sample(JPanel master) {
        this.master = master;
        treeTable.setShowHorizontalLines(true);
        treeTable.setShowVerticalLines(false);
        setRootVisible(false);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setupColumns();
 
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setOpenIcon(null);
        renderer.setClosedIcon(null);
        renderer.setLeafIcon(null);
        tree.setCellRenderer(renderer);
    }
    
    @SuppressWarnings("unchecked")
    private void setupColumns() {
        Node.Property [] columns = new Node.Property[4];
//        ResourceBundle loc = NbBundle.getBundle(DiffTreeTable.class);
/*        columns[0] = new ColumnDescriptor(LogItemNode.COLUMN_NAME_NAME, String.class, "", ""); // NOI18N
        columns[0].setValue("TreeColumnTTV", Boolean.TRUE); // NOI18N
        columns[1] = new ColumnDescriptor(LogItemNode.COLUMN_NAME_DATE, String.class, loc.getString("LBL_DiffTree_Column_Time"), loc.getString("LBL_DiffTree_Column_Time_Desc"));
        columns[2] = new ColumnDescriptor(LogItemNode.COLUMN_NAME_USERNAME, String.class, loc.getString("LBL_DiffTree_Column_Username"), loc.getString("LBL_DiffTree_Column_Username_Desc"));
        columns[3] = new ColumnDescriptor(LogItemNode.COLUMN_NAME_MESSAGE, String.class, loc.getString("LBL_DiffTree_Column_Message"), loc.getString("LBL_DiffTree_Column_Message_Desc"));*/

        columns[0] = new ColumnDescriptor("1", String.class, "", ""); // NOI18N
        columns[0].setValue("TreeColumnTTV", Boolean.TRUE); // NOI18N
        columns[1] = new ColumnDescriptor("2", String.class, "LBL_DiffTree_Column_Time", "LBL_DiffTree_Column_Time_Desc");
        columns[2] = new ColumnDescriptor("3", String.class, "LBL_DiffTree_Column_Username", "LBL_DiffTree_Column_Username_Desc");
        columns[3] = new ColumnDescriptor("4", String.class, "LBL_DiffTree_Column_Message", "LBL_DiffTree_Column_Message_Desc");
        
        setProperties(columns);
    }
    
    private void setDefaultColumnSizes() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int width = getWidth();
                treeTable.getColumnModel().getColumn(0).setPreferredWidth(width * 25 / 100);
                treeTable.getColumnModel().getColumn(1).setPreferredWidth(width * 15 / 100);
                treeTable.getColumnModel().getColumn(2).setPreferredWidth(width * 10 / 100);
                treeTable.getColumnModel().getColumn(3).setPreferredWidth(width * 50 / 100);
            }
        });
    }
 
    void setSelection(int idx) {
        treeTable.getSelectionModel().setValueIsAdjusting(false);
        treeTable.scrollRectToVisible(treeTable.getCellRect(idx, 1, true));
        treeTable.getSelectionModel().setSelectionInterval(idx, idx);
    }
 
/*    void setSelection(RepositoryRevision container) {
        LogItemNode node = (LogItemNode) getNode(rootNode, container);
        if (node == null) return;
        ExplorerManager em = ExplorerManager.find(this);
        try {
            em.setSelectedNodes(new Node [] { node });
        } catch (PropertyVetoException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
 
    void setSelection(RepositoryRevision.Event revision) {
        LogItemNode node = (LogItemNode) getNode(rootNode, revision);
        if (node == null) return;
        ExplorerManager em = ExplorerManager.find(this);
        try {
            em.setSelectedNodes(new Node [] { node });
        } catch (PropertyVetoException e) {
            ErrorManager.getDefault().notify(e);
        }
    }*/
 
    private Node getNode(Node node, Object obj) {
        Object object = node.getLookup().lookup(obj.getClass());
        if (obj.equals(object)) return node;
        Enumeration children = node.getChildren().nodes();
        while (children.hasMoreElements()) {
            Node child = (Node) children.nextElement();
            Node result = getNode(child, obj);
            if (result != null) return result;
        }
        return null;
    }
 
    public int [] getSelection() {
        return treeTable.getSelectedRows();
    }
 
    public int getRowCount() {
        return treeTable.getRowCount();
    }
 
    private static class ColumnDescriptor<T> extends PropertySupport.ReadOnly<T> {
        
        public ColumnDescriptor(String name, Class<T> type, String displayName, String shortDescription) {
            super(name, type, displayName, shortDescription);
        }
 
        public T getValue() throws IllegalAccessException, InvocationTargetException {
            return null;
        }
    }
 
    public void addNotify() {
        super.addNotify();
        ExplorerManager em = ExplorerManager.find(this);
        em.setRootContext(rootNode);
        setDefaultColumnSizes();
    }
 
    public void setResults(List results) {
        this.results = results;
        rootNode = new RevisionsRootNode();
        ExplorerManager em = ExplorerManager.find(this);
        if (em != null) {
            em.setRootContext(rootNode);
        }
    }
    
    private class RevisionsRootNode extends AbstractNode {
    
        public RevisionsRootNode() {
            super(new RevisionsRootNodeChildren(), Lookups.singleton(results));
        }
 
        public String getName() {
            return ""; // NOI18N
        }
 
        public String getDisplayName() {
//            return NbBundle.getMessage(DiffTreeTable.class, "LBL_DiffTree_Column_Name"); // NOI18N
            return "";
        }
 
        public String getShortDescription() {
//            return NbBundle.getMessage(DiffTreeTable.class, "LBL_DiffTree_Column_Name_Desc"); // NOI18N
            return "";
        }
    }
 
    private class RevisionsRootNodeChildren extends Children.Keys {
    
        public RevisionsRootNodeChildren() {
        }
 
        protected void addNotify() {
            refreshKeys();
        }
        
        @SuppressWarnings("unchecked")
        @Override
        protected void removeNotify() {
            setKeys(Collections.EMPTY_SET);
        }
    
        @SuppressWarnings("unchecked")
        private void refreshKeys() {
            setKeys(results);
        }
    
        protected Node[] createNodes(Object arg0) {
//                if (arg0 instanceof LogItem) {
  //                  return new Node[]{new LogItemNode((LogItem) arg0)};
//            return new Node[] { node };
            return new Node[]{};
        }
    }    
}
