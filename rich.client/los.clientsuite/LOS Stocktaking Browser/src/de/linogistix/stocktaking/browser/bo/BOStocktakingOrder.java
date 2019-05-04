/*
 * BONodeUser.java
 *
 * Created on 1. Dezember 2006, 01:17
 *
 * Copyright (c) 2006-2011 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.stocktaking.browser.bo;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.query.gui.component.AutoCompletionQueryFilterProvider;
import de.linogistix.common.bobrowser.query.gui.component.BOQueryComponentProvider;
import de.linogistix.common.bobrowser.query.gui.component.DefaultBOQueryComponentProvider;
import de.linogistix.common.bobrowser.query.gui.component.TemplateQueryWizardProvider;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.stocktaking.crud.LOSStocktakingOrderCRUDRemote;
import de.linogistix.los.stocktaking.model.LOSStocktakingOrder;
import de.linogistix.los.stocktaking.query.LOSStocktakingOrderQueryRemote;
import de.linogistix.stocktaking.browser.action.StocktakingConfirmAction;
import de.linogistix.stocktaking.browser.action.StocktakingRecountAction;
import de.linogistix.stocktaking.browser.action.StocktakingRemoveAction;
import de.linogistix.stocktaking.browser.masternode.BOLOSStockTakingOrderMasterNode;
import de.linogistix.stocktaking.process.OpenStockTakingAction;
import de.linogistix.stocktaking.res.StocktakingBundleResolver;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;



/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOStocktakingOrder extends BO {
    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR};
    }
   
  protected String initName() {
    return "CountOrders";
  }
  
    @Override
  protected String initIconBaseWithExtension() {
    return "de/linogistix/bobrowser/res/icon/ItemUnit.png";
  }

  protected BusinessObjectQueryRemote initQueryService() {
    
    BusinessObjectQueryRemote ret = null;
    
    try{
      J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
      ret = (BusinessObjectQueryRemote)loc.getStateless(LOSStocktakingOrderQueryRemote.class);
      
    } catch (Throwable t){
      ExceptionAnnotator.annotate(t);
    }
    return ret;
  }
  
  
  protected BasicEntity initEntityTemplate() {
    LOSStocktakingOrder o;
    
    o = new LOSStocktakingOrder();
    
    return o;
    
  }
  
  protected BusinessObjectCRUDRemote initCRUDService(){
    BusinessObjectCRUDRemote ret = null;
    
    try{
      J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
      ret = (BusinessObjectCRUDRemote) loc.getStateless(LOSStocktakingOrderCRUDRemote.class);
      
    } catch (Throwable t){
      ExceptionAnnotator.annotate(t);
    }
    return ret;
  }
  
    @Override
   protected String[] initIdentifiableProperties() {
    return new String[]{"id"};
  }
   
    @Override
    protected Class initBundleResolver() {
        return StocktakingBundleResolver.class;
    }
    
    @Override
    protected Property[] initBoMasterNodeProperties() {
        return BOLOSStockTakingOrderMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOLOSStockTakingOrderMasterNode.class;
    }
    
    @Override
    public Action getPreferredAction() {
        return new OpenStockTakingAction();
    }

    @Override
    public List<BOQueryComponentProvider> initQueryComponentProviders() {
        try {
            List<BOQueryComponentProvider> retDesired = new ArrayList();

            AutoCompletionQueryFilterProvider quickSearch = new AutoCompletionQueryFilterProvider(getQueryService());
            quickSearch.addFilter( 1, NbBundle.getMessage(StocktakingBundleResolver.class, "filter")+":" );
            quickSearch.addFilterValue(1, NbBundle.getMessage(StocktakingBundleResolver.class, "filter.all"), "ALL");
            quickSearch.addFilterValue(1, NbBundle.getMessage(StocktakingBundleResolver.class, "filter.open"), "OPEN");
            quickSearch.addFilterValue(1, NbBundle.getMessage(StocktakingBundleResolver.class, "filter.difference"), "DIFF");
            quickSearch.setFilterSelected(1, 1);
            retDesired.add( quickSearch );

            Method m;
            m = getQueryService().getClass().getDeclaredMethod("queryAllHandles", new Class[]{QueryDetail.class});
            retDesired.add(new DefaultBOQueryComponentProvider(getQueryService(), m));

            m = getQueryService().getClass().getDeclaredMethod("queryByTemplateHandles", new Class[]{QueryDetail.class, TemplateQuery.class});
            retDesired.add(new TemplateQueryWizardProvider(getQueryService(), m));

            return retDesired;
        } catch (Throwable ex) {
           ExceptionAnnotator.annotate(ex);
           return new ArrayList();
        }
    }

    @Override
    public List<SystemAction> initMasterActions() {
        List<SystemAction> actions = new ArrayList<SystemAction>();
        SystemAction action;

        action = SystemAction.get(StocktakingRecountAction.class);
        action.setEnabled(true);
        actions.add(action);

        action = SystemAction.get(StocktakingConfirmAction.class);
        action.setEnabled(true);
        actions.add(action);

        action = SystemAction.get(StocktakingRemoveAction.class);
        action.setEnabled(true);
        actions.add(action);

        return actions;
    }

}
