/*
 * BONodeUser.java
 *
 * Created on 1. Dezember 2006, 01:17
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.inventory.browser.bo;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.query.gui.component.AutoCompletionQueryFilterProvider;
import de.linogistix.inventory.browser.action.BOStockUnitNirwanaAction;
import de.linogistix.inventory.browser.masternode.BOStockUnitMasterNode;
import de.linogistix.common.bobrowser.query.gui.component.BOQueryComponentProvider;
import de.linogistix.common.bobrowser.query.gui.component.DefaultBOQueryComponentProvider;
import de.linogistix.common.bobrowser.query.gui.component.TemplateQueryWizardProvider;
import de.linogistix.inventory.browser.query.gui.component.StockUnitDefaultQueryProvider;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.entityservice.BusinessObjectLock;
import de.linogistix.los.inventory.crud.StockUnitCRUDRemote;
import de.linogistix.los.inventory.query.StockUnitQueryRemote;
import de.linogistix.los.inventory.service.StockUnitLockState;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.wms2.mywms.inventory.StockState;
import de.wms2.mywms.inventory.StockUnit;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
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
public class BOStockUnit extends BO {
    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR};
    }
 
    Vector<Action> actions;

    protected String initName() {
        return "StockUnits";
    }

    protected String initIconBaseWithExtension() {

        return "de/linogistix/bobrowser/res/icon/StockUnit.png";
    }

    protected BusinessObjectQueryRemote initQueryService() {

        BusinessObjectQueryRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (BusinessObjectQueryRemote) loc.getStateless(StockUnitQueryRemote.class);

        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
        }
        return ret;
    }

    protected BasicEntity initEntityTemplate() {
        StockUnit o;

        o = new StockUnit();
        //dgrys portierung wildfly 8.2
        //o.setLabelId("");

        return o;

    }

    protected BusinessObjectCRUDRemote initCRUDService() {
        BusinessObjectCRUDRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (BusinessObjectCRUDRemote) loc.getStateless(StockUnitCRUDRemote.class);

        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
        }
        return ret;
    }

    protected Class initBundleResolver() {
        return CommonBundleResolver.class;
    }

    protected String[] initIdentifiableProperties() {
        return new String[]{"labelId"};
    }

    protected List<SystemAction> doNot_initMasterActions() {
        List<SystemAction> actions = super.initMasterActions();
        
        SystemAction action = SystemAction.get(BOStockUnitNirwanaAction.class);
        action.setEnabled(true);
        actions.add(action);
        return actions;
    }

    @Override
    protected Property[] initBoMasterNodeProperties() {
        return BOStockUnitMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOStockUnitMasterNode.class;
    }

    @Override
    public List<BusinessObjectLock> getLockStates() {
        List<BusinessObjectLock> ret =  super.getLockStates();
        ret.addAll(Arrays.asList(StockUnitLockState.values()));
        
        return ret;
    }

    @Override
    public List<BOQueryComponentProvider> initQueryComponentProviders() {
        try {
            List<BOQueryComponentProvider> retDesired = new ArrayList();

            AutoCompletionQueryFilterProvider quickSearch = new AutoCompletionQueryFilterProvider(getQueryService());
            quickSearch.addFilter( 1, NbBundle.getMessage(InventoryBundleResolver.class, "filter")+":" );
            quickSearch.addFilterValue(1, NbBundle.getMessage(InventoryBundleResolver.class, "filter.all"), "ALL");
            quickSearch.addFilterValue(1, NbBundle.getMessage(InventoryBundleResolver.class, "filter.available"), "AVAILABLE");
            quickSearch.addFilterValue(1, NbBundle.getMessage(InventoryBundleResolver.class, "filter.qs"), "QS");
            quickSearch.addFilterValue(1, NbBundle.getMessage(InventoryBundleResolver.class, "filter.out"), "OUT");
            quickSearch.setFilterSelected(1, 1);
            retDesired.add( quickSearch );

            StockUnitDefaultQueryProvider d = new StockUnitDefaultQueryProvider((StockUnitQueryRemote) getQueryService());
            retDesired.add(d);

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


//    @Override
//    public BOQueryComponentProvider getDefaultBOQueryProvider() {
//
//        for (BOQueryComponentProvider p : getQueryComponentProviders()){
//            if (p.getClass().equals(StockUnitDefaultQueryProvider.class)){
//                return p;
//            }
//        }
//
//        return super.getDefaultBOQueryProvider();
//    }
    
    @Override
    public List<Object> getValueList(String fieldName) {
        if( "state".equals(fieldName) ) {
            List<Object> entryList = new ArrayList<Object>();
            entryList.add(StockState.INCOMING);
            entryList.add(StockState.ON_STOCK);
            entryList.add(StockState.PICKED);
            entryList.add(StockState.SHIPPED);
            entryList.add(StockState.DELETABLE);
            return entryList;
        }
        return super.getValueList(fieldName);
    }

    @Override
    public String getBundlePrefix() {
        return this.getClass().getSimpleName();
    }

}
