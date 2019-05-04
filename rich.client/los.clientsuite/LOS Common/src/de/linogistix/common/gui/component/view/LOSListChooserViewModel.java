/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.view;

import de.linogistix.common.bobrowser.api.BOLookup;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.bo.BONode;
import de.linogistix.common.bobrowser.query.BOQueryModel;
import de.linogistix.common.bobrowser.query.gui.component.BOQueryComponentProvider;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import java.util.ArrayList;
import java.util.List;
import org.mywms.facade.FacadeException;
import org.mywms.model.BasicEntity;
import org.openide.util.Lookup;

/**
 *
 * @author Jordan
 */
public class LOSListChooserViewModel<E extends BasicEntity> extends BOQueryModel implements LOSListViewModel {
    
    private List<LOSListViewModelListener> myListeners;
    
    private List<BODTO> selectedEntityList;
    
    public LOSListChooserViewModel(Class entityClass) throws Exception{
        
        this((BO) Lookup.getDefault().lookup(BOLookup.class).lookup(entityClass));
       
    }
    
    public LOSListChooserViewModel(BO entityBO) throws Exception{ 
        
        super(new BONode(entityBO));
        
        myListeners = new ArrayList<LOSListViewModelListener>();
        selectedEntityList = new ArrayList<BODTO>();
    }
    
    /**
     * Elements of selection list on the left
     */
    public List<BODTO> getResultList(){
        
        return selectedEntityList;
    }

    @Override
    public LOSResultList<BODTO> getResults() throws FacadeException {
        LOSResultList resList = getResults(getProvider(), getQueryDetail());
        setResultSetSize(resList.getResultSetSize());
        return resList;
    }   
    
    public LOSResultList<BODTO<E>> getResults(BOQueryComponentProvider provider, QueryDetail detail){
        
        try {

            return super.getResults();
            
        } catch (FacadeException ex) {
            ExceptionAnnotator.annotate(ex);
            return new LOSResultList<BODTO<E>>();
        }
        
    }

    public boolean isSelected(BODTO entityTO){
        
        for(BODTO to:selectedEntityList){
            if(to.getId() == entityTO.getId()){
                return true;
            }
        }
        
        return false;
    }
    
    public void clear(){
        clearSelectionList();
    }
    
    public void clearSelectionList(){
        
        selectedEntityList.clear();
        
        fireModelChangedEvent();
    }
    
    public void addToSelectionList(List<BODTO> selectedEntities){
        selectedEntityList.addAll(selectedEntities);
        
        for(LOSListViewModelListener l:myListeners){
            l.modelRowsInserted(selectedEntityList);
        }
        
        fireModelChangedEvent();
    }
    
    public void removeFromSelectionList(List<BODTO> selectedEntities){
        selectedEntityList.removeAll(selectedEntities);
        
        for(LOSListViewModelListener l:myListeners){
            l.modelRowsDeleted(selectedEntityList);
        }
        
        fireModelChangedEvent();
    }
    
    public void setSelectionList(List<BODTO> selectedEntities){
        
        this.selectedEntityList = selectedEntities;
        
        fireModelChangedEvent();
    }
    
    public void addModelListener(LOSListViewModelListener listener) {
        myListeners.add(listener);
    }

    public void fireModelChangedEvent() {
        
        for(LOSListViewModelListener l:myListeners){
            l.modelChanged();
        }
    }

}
