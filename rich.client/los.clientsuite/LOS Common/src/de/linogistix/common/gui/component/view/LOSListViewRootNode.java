/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.view;

import de.linogistix.common.bobrowser.bo.BODTONode;
import de.linogistix.los.query.BODTO;
import java.util.List;
import org.mywms.model.BasicEntity;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author Jordan
 */
public class LOSListViewRootNode<E extends BasicEntity> extends AbstractNode{

    
    
    public LOSListViewRootNode(LOSListViewModel<E> model){
        
        super(new LOSListViewChildren(model));
    }
    
    private static class LOSListViewChildren extends Children.Keys 
                                             implements LOSListViewModelListener
    {

        private LOSListViewModel myModel;
        
        public LOSListViewChildren(LOSListViewModel model){
            myModel = model;
            myModel.addModelListener(this);
        }
        
        @Override
        protected void addNotify() {
            
            setKeys(myModel.getResultList());
        }
        
        @Override
        protected Node[] createNodes(Object arg0) {
            
            BODTO to = (BODTO) arg0;
            
            return new Node[]{new BODTONode(to)};
            
        }

        public void modelChanged() {
            addNotify();
        }

        public void modelRowsInserted(List<BODTO> insertedList) { }

        public void modelRowsDeleted(List<BODTO> deletedList) { }
               
    }
}
