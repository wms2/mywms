/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.view;

import de.linogistix.common.bobrowser.bo.BODTONode;
import de.linogistix.common.gui.component.controls.*;
import de.linogistix.los.query.BODTO;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.ListView;
import org.openide.nodes.Node;

/**
 *
 * @author Jordan
 */
public class LOSListView extends JPanel implements ExplorerManager.Provider, PropertyChangeListener {
    
    private LosLabel textFieldLabel;
    
    private ListView myListView;
    
    private LOSListViewModel myModel;
    
    private ExplorerManager explorerManager = new ExplorerManager();
    
    private List<LOSListViewSelectionListener> selectionListenerList;
    
    public LOSListView(){
        
        explorerManager.addPropertyChangeListener(this);
        
        selectionListenerList = new ArrayList<LOSListViewSelectionListener>();
        
        setLayout(new GridBagLayout());
        
        textFieldLabel = new LosLabel();
        textFieldLabel.setText("");
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        add(textFieldLabel, gbc);
        
        myListView = new ListView();
        
        JScrollPane srollPane = new JScrollPane(myListView){
            
            @Override
            public Dimension getMinimumSize() {
                return new Dimension(150, 300);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(150, 300);
            }
        };
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(5, 0, 0, 0);
        gbc.fill = GridBagConstraints.VERTICAL;
        add(srollPane, gbc);
    }
    
    public void clear(){
        myModel.clear();
    }
    
    public void setModel(LOSListViewModel model){
        
        myModel = model;
        
        explorerManager.setRootContext(new LOSListViewRootNode(model));
        
    }
    
    public void setSingleSelection(boolean selectSingle){
        
        if(selectSingle){
            myListView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
        else{
            myListView.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        }
        
    }
    
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    public LosLabel getTextFieldLabel() {
        return textFieldLabel;
    }

    public void modelChanged() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addSelectionListener(LOSListViewSelectionListener listener){
        selectionListenerList.add(listener);
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        
        if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
            
            List<BODTO> selectedEntities = getSelectedEntities();
            
            for(LOSListViewSelectionListener l:selectionListenerList){
                l.selectionChanged(selectedEntities);
            }
        }
    }
    
    public List<BODTO> getSelectedEntities(){
        
        Node[] selNodes = explorerManager.getSelectedNodes();
            
        List<BODTO> selectedEntities = new ArrayList<BODTO>(selNodes.length);

        for(Node n:selNodes){
             BODTONode toNode = (BODTONode) n;
             selectedEntities.add(toNode.getBODTO());
        }
        
        return selectedEntities;
    }

}
