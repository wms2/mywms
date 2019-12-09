/*
 * BOLOSAdvice.java
 *
 * Copyright (c) 2006-2013 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.inventory.browser.bo;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.query.gui.component.AutoCompletionQueryFilterProvider;
import de.linogistix.common.bobrowser.query.gui.component.BOQueryComponentProvider;
import de.linogistix.common.bobrowser.query.gui.component.DefaultBOQueryComponentProvider;
import de.linogistix.common.bobrowser.query.gui.component.TemplateQueryWizardProvider;
import de.linogistix.inventory.browser.masternode.BOLOSAdviceMasterNode;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.inventory.crud.LOSAdviceCRUDRemote;
import de.linogistix.los.inventory.query.LOSAdviceQueryRemote;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.wms2.mywms.advice.AdviceLine;
import de.wms2.mywms.strategy.OrderState;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOLOSAdvice extends BO {
    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR};
    }


    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOLOSAdviceMasterNode.class;
    }

    public String initName() {
        return "LOSAdvices";
    }

    @Override
    public String initIconBaseWithExtension() {
        return "de/linogistix/bobrowser/res/icon/LOSAdvice.png";
    }

    public BusinessObjectQueryRemote initQueryService() {

        BusinessObjectQueryRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (LOSAdviceQueryRemote) loc.getStateless(LOSAdviceQueryRemote.class);

        } catch (Throwable t) {
            //
        }
        return ret;
    }

    public BasicEntity initEntityTemplate() {
        AdviceLine c;

        c = new AdviceLine();

        return c;

    }

    public BusinessObjectCRUDRemote initCRUDService() {
        LOSAdviceCRUDRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (LOSAdviceCRUDRemote) loc.getStateless(LOSAdviceCRUDRemote.class);

        } catch (Throwable t) {
            //ExceptionAnnotator.annotate(t);
        }
        return ret;
    }

    @Override
    public Class<InventoryBundleResolver> initBundleResolver() {
        return InventoryBundleResolver.class;
    }

    @Override
    public List<BOQueryComponentProvider> initQueryComponentProviders() {
        try {
            List<BOQueryComponentProvider> retDesired = new ArrayList();

            AutoCompletionQueryFilterProvider quickSearch = new AutoCompletionQueryFilterProvider(getQueryService());
            quickSearch.addFilter( 1, NbBundle.getMessage(InventoryBundleResolver.class, "filter")+":" );
            quickSearch.addFilterValue(1, NbBundle.getMessage(InventoryBundleResolver.class, "filter.all"), "ALL");
            quickSearch.addFilterValue(1, NbBundle.getMessage(InventoryBundleResolver.class, "filter.open"), "OPEN");
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
    protected Property[] initBoMasterNodeProperties() {
        return BOLOSAdviceMasterNode.boMasterNodeProperties();
    }

    @Override
    protected String[] initIdentifiableProperties() {
        return new String[]{"lineNumber"};
    }

    @Override
    public List<Object> getValueList(String fieldName) {
        if( "state".equals(fieldName) ) {
            List<Object> entryList = new ArrayList<Object>();
            entryList.add(OrderState.CREATED);
            entryList.add(OrderState.PROCESSABLE);
            entryList.add(OrderState.STARTED);
            entryList.add(OrderState.FINISHED);
            return entryList;
        }

        return super.getValueList(fieldName);
    }

}
