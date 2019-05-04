/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.bo.editor;

import de.linogistix.common.bobrowser.bo.BOEntityNodeReadOnly;
import de.linogistix.common.bobrowser.bo.BONode;
import de.linogistix.common.bobrowser.query.BOQueryMasterDetailView;
import de.linogistix.common.bobrowser.query.BOQueryModel;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.util.ExceptionAnnotator;
import java.awt.BorderLayout;
import java.beans.IntrospectionException;
import java.beans.PropertyVetoException;
import java.util.List;
import java.util.logging.Logger;
import org.mywms.model.BasicEntity;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author  trautm
 */
public class BOEditorChooseFromServicePanel extends BOEditorChoosePanel implements ExplorerManager.Provider {

    private static final Logger log = Logger.getLogger(BOEditorChooseFromServicePanel.class.getName());

    
    private List presetList;
    
    /** Creates new form BOEditorPanel */
    public BOEditorChooseFromServicePanel(BOEditorChooseFromService editor) {
        super(editor);
    }

    /** Creates new form BOEditorPanel */
    public BOEditorChooseFromServicePanel(Class boClass) {
        super(boClass);
    }
    
    /** Creates new form BOEditorPanel */
    public BOEditorChooseFromServicePanel(List list) {
        super(list.get(0).getClass());
        try {
            this.presetList = list;
            Node root;
            root = getExplorerManager().getRootContext();
            getExplorerManager().setRootContext(new AbstractNode(Children.LEAF));
            getExplorerManager().setSelectedNodes(new Node[]{});
            root = getRoot();
            getExplorerManager().setRootContext(root);
            getExplorerManager().setSelectedNodes(new Node[]{root});     
        } catch (PropertyVetoException ex) {
            ExceptionAnnotator.annotate(ex);
        }
    }

    @Override
    protected void initEditorComponent(BONode boNode, BOQueryModel model) {
        try {
//            BOMasterDetailView view;
//            view = new BOMasterDetailView(getExplorerManager(), boNode, BundleResolver.class);
//            view.getExplorerManager().setRootContext(getRoot());
//            view.getExplorerManager().setSelectedNodes(getSelectedNodes());
//            editorPanel.add(view, BorderLayout.CENTER);
//            getExplorerManager().addPropertyChangeListener(this);
            
            BOQueryMasterDetailView view;
            view = new BOQueryMasterDetailView(getExplorerManager(), boNode, CommonBundleResolver.class);
            view.getExplorerManager().setRootContext(getRoot());
            view.getExplorerManager().setSelectedNodes(getSelectedNodes());
            view.updateCounter();
            manager = view.getExplorerManager();
            editorPanel.add(view, BorderLayout.CENTER);
            getExplorerManager().addPropertyChangeListener(this);           
            
        } catch (PropertyVetoException ex) {
            ExceptionAnnotator.annotate(ex);
        }
    }

    private Node[] getSelectedNodes(){
        if (editor != null && editor.getValue() != null){
            if (editor.getValue() instanceof BasicEntity){
                try {
                    return new Node[]{new BOEntityNodeReadOnly((BasicEntity) editor.getValue())};
                } catch (IntrospectionException ex) {
                   ExceptionAnnotator.annotate(ex);
                }
            } 
        } 
            
        return new Node[0];
       
        
    }
    
    protected Node getRoot() {
        Node ret;

        Children.Keys keys = new Children.Keys() {

            @Override
            protected Node[] createNodes(Object arg0) {
                if (arg0 != null && arg0 instanceof BasicEntity) {
                    try {
                        return new Node[]{new BOEntityNodeReadOnly((BasicEntity) arg0)};
                    } catch (IntrospectionException ex) {
                        ExceptionAnnotator.annotate(ex);
                        return new Node[0];
                    }
                } else {
                    throw new IllegalArgumentException();
                }
            }

            @Override
            protected void addNotify() {
                if (editor != null){
                    setKeys(((BOEditorChooseFromService) editor).invokeService());
                } else if (presetList != null){
                    setKeys(presetList);
                } else{
                    return;
                }
            }
        };

        ret = new AbstractNode(keys);

        return ret;
    }
}
