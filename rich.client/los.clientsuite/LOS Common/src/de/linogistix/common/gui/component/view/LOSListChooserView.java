/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.view;

import de.linogistix.common.bobrowser.api.BOLookup;
import de.linogistix.common.bobrowser.bo.BODTONode;
import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.bo.BONode;
import de.linogistix.common.gui.component.gui_builder.AbstractListChooserView;
import de.linogistix.common.bobrowser.query.BOQueryTopComponent;
import de.linogistix.common.gui.component.controls.LosLabel;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.query.BODTO;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Jordan
 */
public class LOSListChooserView extends AbstractListChooserView
                                implements LOSListViewModelListener
{
    
    private BONode boNode;
    
    private LOSListChooserViewModel myModel;
    
    private BOQueryTopComponent queryComponent;
    
    private ExplorerManager queryPanelManager;
    
    public LOSListChooserView(){
        
        chooseStockButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                chooseButtonActionPerformed();
            }
        });
        
        removeChosenStockButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                removeButtonActionPerformed();
            }
        });
    }
    
    public void init(Class entityClass, LOSListChooserViewModel model) throws Exception{
        
        BOLookup l = (BOLookup) Lookup.getDefault().lookup(BOLookup.class);
        BO bo = (BO) l.lookup(entityClass);
        
        init(bo, model);
    }
    
    public void init(BO entityBO, LOSListChooserViewModel model) throws Exception {
        
        if (queryComponent != null){
            queryPanel.removeAll();
            queryComponent = null;
            queryPanel.invalidate();
        }

        try {
            boNode = new BONode(entityBO);
        } catch (Exception ex) {
            ExceptionAnnotator.annotate(ex);
            return;
        } 
        
        if(model == null){
            myModel = new LOSListChooserViewModel(entityBO.getBusinessObjectTemplate().getClass());
            getChosenListView().setModel(myModel);
            queryComponent = new BOQueryTopComponent(boNode);
        }
        else{
            myModel = model;
            getChosenListView().setModel(model);
            queryComponent = new BOQueryTopComponent(boNode, model, false);
        }
        
        myModel.addModelListener(this);
        
        queryComponent.componentOpened();
//        queryComponent.getExplorerManager().addPropertyChangeListener(this);
        
        queryPanelManager = queryComponent.getExplorerManager();        
        queryPanel.add(queryComponent, BorderLayout.CENTER);    
        
        invalidate();
      
    }

    public void reload(){
        if(queryComponent != null){
            SwingUtilities.invokeLater(new Runnable() {
                        
                public void run() {           
                    try {
                        queryComponent.getBOQueryPanel().getExplorerManager().setSelectedNodes(new Node[]{});
                    } catch (PropertyVetoException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    queryComponent.getBOQueryPanel().reload();
                }
            });
            
        }
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        
        chooseStockButton.setEnabled(enabled);
        removeChosenStockButton.setEnabled(enabled);
    }
    
    public LosLabel getChosenListLabel(){
        return getChosenListView().getTextFieldLabel();
    }
    
    public LosLabel getQueryListLabel(){
        return (LosLabel) queryListLabel;
    }
    
    public LOSListChooserViewModel getModel(){
        return myModel;
    }
    
    private void chooseButtonActionPerformed(){
        
        Node[] selNodes = queryPanelManager.getSelectedNodes();
        
        if (selNodes == null || selNodes.length == 0) {
          return;
        }

        List<BODTO> selectedEntities = new ArrayList<BODTO>(selNodes.length);        
        for (Node n : selNodes) {
            BOMasterNode masterNode = (BOMasterNode) n;
            if(!myModel.isSelected(masterNode.getEntity())){
                selectedEntities.add(masterNode.getEntity());
            }
        }
        
        myModel.addToSelectionList(selectedEntities);
    }
    
    private void removeButtonActionPerformed() {
        
        Node[] selNodes = getChosenListView().getExplorerManager().getSelectedNodes();
        
        if (selNodes == null || selNodes.length == 0) {
          return;
        }
        
        List<BODTO> selectedEntities = new ArrayList<BODTO>(selNodes.length);        
        for (Node n : selNodes) {
            BODTONode dtoNode = (BODTONode) n;
            selectedEntities.add(dtoNode.getBODTO());
        }
        
        myModel.removeFromSelectionList(selectedEntities);
    }

    public void modelChanged() {
        reload();
    }

    public void modelRowsInserted(List<BODTO> insertedList) {  }

    public void modelRowsDeleted(List<BODTO> deletedList) {  }
}
