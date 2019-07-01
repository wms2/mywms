/*
 * Copyright (c) 2006-2012 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.bobrowser.bo;

import de.linogistix.common.bobrowser.query.gui.component.BOQueryComponentProvider;
import de.linogistix.common.bobrowser.query.gui.component.DefaultBOQueryComponentProvider;
import de.linogistix.common.bobrowser.bo.binding.BOBeanNodeDescriptor;
import de.linogistix.common.bobrowser.bo.binding.DescriptorBinder;
import de.linogistix.common.bobrowser.query.gui.component.AutoCompletionQueryProvider;
import de.linogistix.common.bobrowser.query.BOQueryEvent;
import de.linogistix.common.bobrowser.query.BOQueryEventListener;
import de.linogistix.common.bobrowser.query.OpenBOQueryTopComponentAction;
import de.linogistix.common.bobrowser.action.RefreshBOBeanNodeAction;
import de.linogistix.common.bobrowser.bo.detailview.BOAdminDetailViewPanel;
import de.linogistix.common.bobrowser.bo.detailview.BOCommentDetailViewPanel;
import de.linogistix.common.bobrowser.query.gui.component.TemplateQueryWizardProvider;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.BundleResolve;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.entityservice.BusinessObjectLock;
import de.linogistix.los.entityservice.BusinessObjectLockState;
import de.linogistix.los.model.LOSCommonPropertyKey;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.Action;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileRenameEvent;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;

/**
 * Superclass for browser entries representing BasicEntities. For any BasicEntity
 * shown in the explorer a subclass of BO must exist.
 *
 * @author trautm
 */
public abstract class BO implements Serializable, FileChangeListener {

    protected final static Logger logger = Logger.getLogger(BO.class.getName());
    
    public final static String ICON_PATH_DEFAULT = "de/linogistix/common/res/icon/Document.png";
    
    protected static final Logger log = Logger.getLogger(BO.class.getName());
    
    public static final String ACTION_FOLDER = "Actions/BOBrowser";

    private String name;
    private Object businessObjectTemplate;
    private BusinessObjectQueryRemote queryService;
    private BusinessObjectCRUDRemote crudService;
    private Class bundleResolver;
    private BOBeanNode boBeanNodeTemplate;
    private BOBeanNodeDescriptor descriptor;
    private boolean withDescriptor = true;
    
    private String IconPath = ICON_PATH_DEFAULT;
    private Class iconResolver = de.linogistix.common.res.icon.IconResolver.class;
  
    private static String[] allowedRoles = new String[0];
    private static String[] allowedRolesCRUD = new String[0];
    private Class boBeanNodeType = BOEntityNodeReadOnly.class;
    private Class boMasterNodeType = BOMasterNode.class;
    private List<BOQueryEventListener> listeners = new ArrayList();
    private String singularDisplayName = "";
//    private SystemAction action;
    private SystemAction masterAction;
    private List<SystemAction> actions;
    private List<SystemAction> masterActions;
    private Property[] boMasterNodeProperties = new Property[0];
    private List<BOQueryComponentProvider> queryComponentProviders = new ArrayList();
    private boolean initialized = false;
    private boolean showInExplorer = true;
    private Class[] detailViewClasses = new Class[0];
    
    
    public BO() {
        init();
        log.info("Created new BO: " + getName());
    }

    public SystemAction getDefaultMasterAction() {
        return SystemAction.findObject(RefreshBOBeanNodeAction.class);
    }

    public void init(){
        try{
            setBundleResolver(initBundleResolver());
            setName(initName());
            setIconPathWithExtension(initIconBaseWithExtension());
            setQueryService(initQueryService());
            setBusinessObjectTemplate(initEntityTemplate());
            setCrudService(initCRUDService());
            setDescriptor(initDescriptor());

            setBoMasterNodeType(initBoMasterNodeType());
            setBoMasterNodeProperties(initBoMasterNodeProperties());

            setMasterActions(initMasterActions());
            setActions(initActions());

            setQueryComponentProviders(initQueryComponentProviders());
            setDetailViewClasses(initDetailViewClasses());
            allowedRolesCRUD = new String[]{Role.ADMIN_STR};
            
            initialized = true;
        } catch (Throwable t){
            log.severe(t.getMessage());
            initialized = false;
        }
    }
    
    public List<BusinessObjectLock> getLockStates() {
        List<BusinessObjectLock> ret = new ArrayList<BusinessObjectLock>();
        
        ret.addAll(Arrays.asList(BusinessObjectLockState.values()));
        
        return ret;
    }
    
    public List<Object> getValueList(String fieldName) {
        if( "lock".equals(fieldName) ) {
            List<Object> entryList = new ArrayList<Object>();
            for( BusinessObjectLock lock : BusinessObjectLockState.values() ) {
                entryList.add(lock.getLock());
            }
            return entryList;
        }
        return null;
    }

    public String getBundlePrefix() {
        return null;
    }

    //-------------------------------------------------------------

    protected abstract BusinessObjectQueryRemote initQueryService();

    protected abstract BusinessObjectCRUDRemote initCRUDService();

    protected abstract String initName();

    protected abstract BasicEntity initEntityTemplate();
    //--------------------------------------------------------------

    protected String initIconBaseWithExtension() {
        return "de/linogistix/common/res/icon/Document.png";
    }

    protected Class initBundleResolver() {
        return CommonBundleResolver.class;
    }

    protected String[] initIdentifiableProperties() {
        return new String[]{"name"};
    }

    protected Class<? extends Node> initBoMasterNodeType() {
        return BOMasterNode.class;
    }

    protected List<SystemAction> initActions() {

        return new ArrayList<SystemAction>();
        
    }

    protected List<SystemAction> initMasterActions() {
        return new ArrayList<SystemAction>();
    }

    protected Class[] initDetailViewClasses(){

        J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
        if( loc.getPropertyBool(LOSCommonPropertyKey.NBCLIENT_SHOW_DETAIL_PROPERTIES, true) ) {
            return new Class[]{
                BOAdminDetailViewPanel.class,
                BOCommentDetailViewPanel.class
            };
        }
        else {
            return new Class[]{
                BOCommentDetailViewPanel.class,
                BOAdminDetailViewPanel.class
            };
        }

    }
    //----------------------------------------------------------------

    public String toString() {
        return BundleResolve.resolve(new Class[]{de.linogistix.common.res.CommonBundleResolver.class, getBundleResolver()},
                getName(), null);
    }

    /**
     * 
     * 
     * @return name of represented type in plural
     */
    public String getDisplayName() {
        return toString();
    }

    /**
     * 
     * @return name of represented type in singular
     */
    public String getSingularDisplayName() {
        if (singularDisplayName != null) {
            return singularDisplayName;
        } else {
            return BundleResolve.resolve(new Class[]{de.linogistix.common.res.CommonBundleResolver.class, getBundleResolver()},
                    getBusinessObjectTemplate().getClass().getSimpleName(), null);
        }
    }

    /**
     * Overwrite to limit access only to allowed roles
     *
     *@return the Roles that are allowed to view this BO Node
     */
    public String[] getAllowedRoles() {
        return allowedRoles;
    }

    /**
     * Overwrite to limit access to the buttons only to allowed roles
     *
     *@return the Roles that are allowed to view this BO Node
     */
    public String[] getAllowedRolesCRUD() {
        return allowedRolesCRUD;
    }
    
    /**
     * Initialises the {@link BOBeanNodeDescriptor}
     */
    protected BOBeanNodeDescriptor initDescriptor() {
        Object o = getBusinessObjectTemplate();
        BOBeanNodeDescriptor ret = null;

        if (o == null) {
            throw new NullPointerException("BusinessObjectTemplate is null. Call initDescriptor after init of BusinessObjectTemplate.");
        }

        Class clazz = o.getClass();

        while (clazz != null && !clazz.equals(Object.class)) {
            BOBeanNodeDescriptor desc;
            desc = DescriptorBinder.getDescriptor(clazz);
            if (desc != null) {
                if (ret == null) {
                    ret = desc;
                } else {
                    ret.pack(desc);
                }
            }
            clazz = clazz.getSuperclass();
        }

        if (ret == null) {
            withDescriptor = false;
        }

        return ret;
    }

    public BOBeanNode getBoBeanNodeTemplate() {
        if (boBeanNodeTemplate == null && getBusinessObjectTemplate() != null) {
            try {
                if (getBusinessObjectTemplate() instanceof BasicEntity) {
                    boBeanNodeTemplate = new BOEntityNodeReadOnly((BasicEntity) getBusinessObjectTemplate());
                } else {
                    boBeanNodeTemplate = new BOBeanNode(getBusinessObjectTemplate());
                }
            } catch (Throwable t) {
                ExceptionAnnotator.annotate(t);
            }
        }
        return boBeanNodeTemplate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getBusinessObjectTemplate() {
        return businessObjectTemplate;
    }

    public void setBusinessObjectTemplate(Object businessObjectTemplate) {
        this.businessObjectTemplate = businessObjectTemplate;
    }

    public BusinessObjectQueryRemote getQueryService() {
        if (queryService == null) {
            initQueryService();
        }
        return queryService;
    }

    public void setQueryService(BusinessObjectQueryRemote queryService) {
        this.queryService = queryService;
    }

    public BusinessObjectCRUDRemote getCrudService() {
        if (crudService == null) {
            initCRUDService();
        }
        return crudService;
    }

    public void setCrudService(BusinessObjectCRUDRemote crudService) {
        this.crudService = crudService;
    }

    public Class getBundleResolver() {
        return bundleResolver;
    }

    public void setBundleResolver(Class bundleResolver) {
        this.bundleResolver = bundleResolver;
    }

    public String getIconPathWithExtension() {
        return IconPath;
    }

    public void setIconPathWithExtension(String iconPathWithExtension) {
        this.IconPath = iconPathWithExtension;
    }

    /**
     *Returns a {@link BOBeanNodeDescriptor} for this BO. Might be null if none can be found.
     */
    public BOBeanNodeDescriptor getDescriptor() {
        if (descriptor == null && withDescriptor) {
            setDescriptor(initDescriptor());
        }
        return descriptor;
    }

    public void setDescriptor(BOBeanNodeDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public boolean equals(Object obj) {
        boolean retValue;

        if (this == obj) {
            return true;
        }

        if (obj != null && obj instanceof BO) {
            BO bo = (BO) obj;
            if (bo.getName().equals(this.getName())) {
                return true;
            }
        }

        return false;
    }

    public Class getBoBeanNodeType() {
        return boBeanNodeType;
    }

    public void setBoBeanNodeType(Class boBeanNodeType) {
        this.boBeanNodeType = boBeanNodeType;
    }

    public Class getBoMasterNodeType() {
        return boMasterNodeType;
    }

    public void setBoMasterNodeType(Class boMasterNodeType) {
        this.boMasterNodeType = boMasterNodeType;
    }

    protected Property[] initBoMasterNodeProperties() {
        return BOMasterNode.boMasterNodeProperties();
    }
    //---------------------------------------------------------------------------

    public void addBOQueryListener(BOQueryEventListener listener) {
        listeners.add(listener);
    }

    public void removeBOQueryListener(BOQueryEventListener listener) {
        listeners.remove(listener);
    }

    public synchronized void fireOutdatedEvent(Node n) {
        for (BOQueryEventListener l : listeners) {
            l.outdated(new BOQueryEvent(n));
        }
    }
        
    public BOQueryComponentProvider getDefaultBOQueryProvider() {
        return getQueryComponentProviders().get(0);
    }

    public List<BOQueryComponentProvider> initQueryComponentProviders() {
        try {
            List<BOQueryComponentProvider> retDesired = new ArrayList();
            Method m;

            m = getQueryService().getClass().getDeclaredMethod("autoCompletion", new Class[]{String.class, QueryDetail.class});
            retDesired.add(new AutoCompletionQueryProvider(getQueryService()));
           
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

    public void fileFolderCreated(FileEvent arg0) {
//        this.descriptor = initBeanNodeDescriptor(businessObjectTemplate);
    }

    public void fileDataCreated(FileEvent arg0) {
//        this.descriptor = initBeanNodeDescriptor(businessObjectTemplate);
    }

    public void fileChanged(FileEvent arg0) {
        this.descriptor = initBeanNodeDescriptor(businessObjectTemplate);
    }

    public void fileDeleted(FileEvent arg0) {
        this.descriptor = null;
    }

    public void fileRenamed(FileRenameEvent arg0) {
        this.descriptor = null;
    }

    public void fileAttributeChanged(FileAttributeEvent arg0) {
    }

    //-----------------------------------------------------------------------
    // call to init BOBeanNodeDescriptor from config file
    public static BOBeanNodeDescriptor initBeanNodeDescriptor(Object bean) {
        BOBeanNodeDescriptor ret = null;
        Class clazz = bean.getClass();

        while (clazz != null && !clazz.equals(Object.class)) {
            BOBeanNodeDescriptor desc;
            desc = DescriptorBinder.getDescriptor(clazz);
            if (desc != null) {
                if (ret == null) {
                    ret = desc;
                } else {
                    ret.pack(desc);
                }
            }
            clazz = clazz.getSuperclass();
        }

        return ret;
    }

    //--------------------------------------------------------------------------
    public Action getPreferredAction() {
  
        SystemAction action = SystemAction.get(OpenBOQueryTopComponentAction.class);
        action.setEnabled(true);
        return action;
    }

    /**
     * API setter for adding Actions
     * @param action
     * @param  position if < 0: append action to the end
     */
    public void addAction(SystemAction add, int position) {
        if (actions == null) {
            actions = new ArrayList<SystemAction>();
        }
        
        if (actions.contains(add)) {
            log.info("Action has already been added. Skip: " + add.getName());
            return;
        }
        
        if ( position > actions.size()){
            position = actions.size();
        }
        
        if (position < 0) {
            actions.add(add);
            log.info("Action has been added: " + add.getName());
        } else {
            actions.add(position, add);
            log.info("Action has been added at position " + position + ": " + add.getName());
        }
    }
    
     /**
     * API setter for adding Actions
     * @param action
     * @param  position if < 0: append action to the end
     */
    public synchronized  void addMasterAction(SystemAction add, int position) {
        if (masterActions == null) {
            masterActions = new ArrayList<SystemAction>();
        }
        
        if (masterActions.contains(add)) {
            log.info("Action has already been added. Skip: " + add.getName());
            return;
        }
        
        if ( position > masterActions.size()){
            position = masterActions.size();
        }
        
        if (position < 0) {
            masterActions.add(add);
            log.info("Action has been added: " + add.getName());
        } else {
            masterActions.add(position, add);
            log.info("Action has been added at position " + position + ": " + add.getName());
        }
    }

    public Action[] getActions(boolean b) {
        return actions.toArray(new SystemAction[0]);
    }

     public void setActions(List<SystemAction> initActions) {
        this.actions = initActions;
    }
     
    public Action[] getMasterActions(boolean b) {
        if (this.masterActions != null){
            return this.masterActions.toArray(new Action[0]);
        } else{
            return new Action[0];
        }
    }
    
    public void setMasterActions(List<SystemAction> initMasterActions) {
        this.masterActions = initMasterActions;
    }

//    public Action getPreferredMasterAction() {
//        if (masterAction == null) {
//            masterAction = SystemAction.get(RefreshBOBeanNodeAction.class);
//            masterAction.setEnabled(true);
//        }
//        return masterAction;
//    }

    public void setPreferredMasterAction(SystemAction preferredMasterAction) {
        this.masterAction = preferredMasterAction;
    }

    public Property[] getBoMasterNodeProperties() {
        return boMasterNodeProperties;
    }

    public void setBoMasterNodeProperties(Property[] boMasterNodeProperties) {
        this.boMasterNodeProperties = boMasterNodeProperties;
    }

    public List<BOQueryComponentProvider> getQueryComponentProviders() {
        return queryComponentProviders;
    }

    public void setQueryComponentProviders(List<BOQueryComponentProvider> queryComponentProviders) {
        this.queryComponentProviders = queryComponentProviders;
    }
    
    public void removeMasterAction(SystemAction systemAction){
        this.masterActions.remove(systemAction);
    }

    public boolean isShowInExplorer() {
        return showInExplorer;
    }

    public void setShowInExplorer(boolean showInExplorer) {
        this.showInExplorer = showInExplorer;
    }
    
    public Class getIconResolver() {
        return iconResolver;
    }

    public void setIconResolver(Class iconResolver) {
        this.iconResolver = iconResolver;
    }

    /**
     * @return the detailViewClasses
     */
    public Class[] getDetailViewClasses() {
        return detailViewClasses;
    }

    /**
     * @param detailViewClasses the detailViewClasses to set
     */
    public void setDetailViewClasses(Class[] detailViewClasses) {
        this.detailViewClasses = detailViewClasses;
    }


}
