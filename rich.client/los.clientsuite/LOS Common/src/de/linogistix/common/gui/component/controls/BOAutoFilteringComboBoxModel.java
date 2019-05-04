/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls;

import de.linogistix.common.bobrowser.api.BOLookup;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.bo.BONode;
import de.linogistix.common.bobrowser.query.BOQueryModel;
import de.linogistix.common.bobrowser.query.gui.component.AutoCompletionQueryProvider;
import de.linogistix.common.bobrowser.query.gui.component.BOQueryComponentProvider;
import de.linogistix.common.bobrowser.query.gui.component.SingleResultQueryProvider;
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
public abstract class BOAutoFilteringComboBoxModel<E extends BasicEntity> 
        extends BOQueryModel
{
    private SingleResultQueryProvider<E> singleProvider = null;
    
    private List<BOQueryComponentProvider> providerList = null;
    
    public BOAutoFilteringComboBoxModel(Class entityClass) throws Exception{
        
        super(new BONode((BO) Lookup.getDefault().lookup(BOLookup.class).lookup(entityClass)));
        
    }

    @Override
    public List<BOQueryComponentProvider> getQueryComponentProviders() {
        
        providerList = new ArrayList<BOQueryComponentProvider>();   
                
        if(singleProvider == null){
            providerList.add(new AutoCompletionQueryProvider());
        }
        else{
            providerList.add(singleProvider);
        }
        
        return providerList;
    }

    @Override
    public BOQueryComponentProvider getDefaultBOQueryProvider() {
        
        if(singleProvider == null){
            return getQueryComponentProviders().get(0);
        }
        else{
            return singleProvider;
        }
    }
    
    @Override
    public LOSResultList<BODTO<E>> getResults() throws FacadeException {
        
        LOSResultList<BODTO<E>> resList;
        
        if(singleProvider != null){
             List<BODTO<E>> toList = new ArrayList<BODTO<E>>();
             toList.add(singleProvider.getEntityTO());
             resList = new LOSResultList<BODTO<E>>(toList);
             resList.setResultSetSize(1);
             resList.setStartResultIndex(0);
             
             setResultSetSize(resList.getResultSetSize());

             return resList;
        }

        BOQueryComponentProvider selectedProvider = getProvider();
        if(selectedProvider != null){
            if(selectedProvider instanceof AutoCompletionQueryProvider){
                AutoCompletionQueryProvider auto;
                auto = (AutoCompletionQueryProvider) selectedProvider;
                
                resList = getResults(auto.getSearchString(), getQueryDetail());
            }
            else{
                resList = getResults("", getQueryDetail());
            }
        }
        else{
            resList = getResults("", getQueryDetail());
        }
        setResultSetSize(resList.getResultSetSize());
        
        return resList;
    }
    
    public void setSingleResult(BODTO<E> enityTO){
        
        if(enityTO == null){
            singleProvider = null;
        }
        else{
            singleProvider = new SingleResultQueryProvider<E>(enityTO);
        }
    }

    public boolean isSingleResult() {
        return singleProvider != null;
    }
    
    public abstract LOSResultList<BODTO<E>> getResults(String searchString, QueryDetail detail);
    
    public void clear(){
        
        singleProvider = null;
    }
    
}