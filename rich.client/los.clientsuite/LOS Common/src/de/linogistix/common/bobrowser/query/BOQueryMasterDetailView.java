/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.bobrowser.query;

import de.linogistix.common.bobrowser.bo.BOBeanNode;
import de.linogistix.common.bobrowser.bo.BOEntityNodeReadOnly;
import de.linogistix.common.bobrowser.bo.BOEntityQueryNode;
import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.bobrowser.bo.editor.BOMasterDetailView;
import de.linogistix.common.bobrowser.bo.BONode;
import de.linogistix.common.bobrowser.bo.detailview.AbstractDetailViewPanel;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.util.ExceptionAnnotator;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.mywms.model.BasicEntity;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author trautm
 */
public class BOQueryMasterDetailView extends BOMasterDetailView {

    private static Logger log = Logger.getLogger(BOQueryMasterDetailView.class.getName());
    private ExplorerManager detailManager;
    
    

    /** Creates a new instance of BOQueryMasterDetailView */
    public BOQueryMasterDetailView(ExplorerManager masterManager, BONode boNode, Class bundleResolver) {
        super(masterManager, boNode, bundleResolver);
    }

    @Override
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
        if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
            refreshDetail((Node[]) evt.getNewValue());
        }
    }

    protected void refreshMaster(){
        updateCounter();
    }

    @Override
    public void updateCounter(){
        if (getExplorerManager().getRootContext() instanceof BOQueryNode) {
            BOQueryNode myNode = (BOQueryNode) getExplorerManager().getRootContext();
            countLabel.setText(NbBundle.getMessage(CommonBundleResolver.class, "RESULT_TOTAL_SIZE", myNode.getModel().getResultSetSize()));
            String sums = "";
            int i = 0;
            if (myNode.getModel().getColumnSums() != null){
                for (Object o : myNode.getModel().getColumnSums().keySet()){
                    String s = (String) o;
                    if (i++ > 0) sums += ", ";
                    Object sum = myNode.getModel().getColumnSums().get(s);
                    sums += s + " " + (sum != null ? sum : "?");
                }
                sumLabel.setText(NbBundle.getMessage(CommonBundleResolver.class, "RESULT_TOTAL_SUMS", sums));
            } else{
               sumLabel.setText("");
            }

        } else {
            countLabel.setText("?");
            sumLabel.setText("");
        }
    }
    
    protected void refreshDetail(final Node[] nodes) {

        BasicEntity e;
        BOEntityNodeReadOnly entityNode = null;

        if (nodes != null && nodes.length > 0) {
            try {
                // if more than one node is selected, take the last
                Node n = nodes[nodes.length - 1];
                BOQueryNode parent = null;

                if (n.getParentNode() instanceof BOQueryNode) {
                    parent = (BOQueryNode) n.getParentNode();
                }
                if (n == null) {
                    log.warning("Null Node cannot be updated");
                    return;
                } else if (n instanceof BOMasterNode) {
                    if (parent == null) {
                        e = null;
                    } else {
                        e = parent.update(((BOMasterNode) n).getId());
                    }
                    if (e == null) {
                        entityNode = null;
                        detailManager.setSelectedNodes(new Node[]{});
                    } else {
                        entityNode = new BOEntityQueryNode(e);
                        detailManager.setRootContext(entityNode);
                        detailManager.setSelectedNodes(new Node[]{entityNode});
                    }
                } else if (n instanceof BOEntityQueryNode) {
                    entityNode = (BOEntityQueryNode) n;
                    detailManager.setRootContext(entityNode);
                    detailManager.setSelectedNodes(new Node[]{entityNode});
                } else if (n instanceof BOBeanNode) {
                    BOEntityNodeReadOnly boBeanNode = (BOEntityNodeReadOnly) n;
                    entityNode = new BOEntityQueryNode(boBeanNode.getBo());
                    detailManager.setRootContext(entityNode);
                    detailManager.setSelectedNodes(new Node[]{entityNode});
                }

                for (AbstractDetailViewPanel pnl : getDetailViews()) {
                    pnl.onRefreshDetail();
                }


            } catch (BusinessObjectRemovedException ore) {
                ExceptionAnnotator.annotate(ore);
            } catch (Throwable t) {
                log.log(Level.SEVERE, t.getMessage(), t);
            }
        } else {
            detailManager.setRootContext(new AbstractNode(Children.LEAF));
        }
    }

    public void refresh() {
        refreshMaster();
        Node[] nodes;
        nodes = getExplorerManager().getSelectedNodes();
        refreshDetail(nodes);
    }

    @Override
    protected JPanel createDetailPanel() {
        ProviderPanel p = new ProviderPanel();
        this.detailManager = p.getExplorerManager();
        return p;
    }

    @Override
    public Lookup getDetailLookup() {
        return ((ProviderPanel)getDetailPanel()).getLookup();
    }



    public static class ProviderPanel extends JPanel implements ExplorerManager.Provider, Lookup.Provider {

        ExplorerManager mgr;
        Lookup lookup;

        ProviderPanel() {
            ActionMap map = getActionMap();
            InputMap keys = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            lookup = ExplorerUtils.createLookup(getExplorerManager(), map);
        }

        public ExplorerManager getExplorerManager() {
            if (mgr == null) {
                mgr = new ExplorerManager();
            }
            return mgr;
        }

        public Lookup getLookup() {
            return lookup;
        }
    }

    public ExplorerManager getDetailManager(){
        return detailManager;
    }
    public ExplorerManager getMasterManager(){
        return getExplorerManager();
    }
    
}
