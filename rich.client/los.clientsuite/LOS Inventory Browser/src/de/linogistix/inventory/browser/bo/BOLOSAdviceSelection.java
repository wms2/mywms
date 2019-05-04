/*
 * BONodeClient.java
 *
 * Created on 1. Dezember 2006, 01:17
 *
 * Copyright (c) 2006-2012 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.inventory.browser.bo;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.query.gui.component.AutoCompletionQueryClientFilterProvider;
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
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.query.LOSAdviceQueryRemote;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.mywms.model.Client;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOLOSAdviceSelection extends BO {
    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }

    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR,Role.INVENTORY_STR};
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
        LOSAdvice c;

        c = new LOSAdvice();

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
    protected Property[] initBoMasterNodeProperties() {
        return BOLOSAdviceMasterNode.boMasterNodeProperties();
    }

    @Override
    protected String[] initIdentifiableProperties() {
        return new String[]{"adviceNumber"};
    }

    private AutoCompletionQueryFilterProvider quickSearch;

    @Override
    public List<BOQueryComponentProvider> initQueryComponentProviders() {
        try {
            List<BOQueryComponentProvider> retDesired = new ArrayList();
            LOSAdviceQueryRemote queryRemote = (LOSAdviceQueryRemote)getQueryService();
            if( queryRemote.hasSingleClient() ) {
                quickSearch = new AutoCompletionQueryFilterProvider(getQueryService());
                quickSearch.addFilter( 1, NbBundle.getMessage(InventoryBundleResolver.class, "filter")+":" );
                quickSearch.addFilterValue(1, NbBundle.getMessage(InventoryBundleResolver.class, "filter.all"), "ALL");
                quickSearch.addFilterValue(1, NbBundle.getMessage(InventoryBundleResolver.class, "filter.open"), "OPEN");
                quickSearch.setFilterSelected(1, 1);
                retDesired.add( quickSearch );
            }
            else {
                quickSearch = new AutoCompletionQueryClientFilterProvider(getQueryService());
                quickSearch.addFilter( 1, NbBundle.getMessage(InventoryBundleResolver.class, "filter")+":" );
                quickSearch.addFilterValue(1, NbBundle.getMessage(InventoryBundleResolver.class, "filter.all"), "ALL");
                quickSearch.addFilterValue(1, NbBundle.getMessage(InventoryBundleResolver.class, "filter.open"), "OPEN");
                quickSearch.setFilterSelected(1, 1);
                retDesired.add( quickSearch );
            }

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


    public void setClient( Client client ) {
        for( BOQueryComponentProvider query : getQueryComponentProviders() ) {
            if( query instanceof AutoCompletionQueryClientFilterProvider ) {
                ((AutoCompletionQueryClientFilterProvider)query).setClient(client);
            }
        }
    }
}