    /*
 *
 * Created on 1. Dezember 2006, 00:50
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.bobrowser.browse;

import de.linogistix.common.bobrowser.bo.BONode;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.api.BOLookup;

import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.system.BundleHelper;
import de.linogistix.common.userlogin.LoginService;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.DataShadow;
import org.openide.loaders.MultiDataObject;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

/**
 * One node represents one type (class) of BasicEntity, eg. StorageLocation.
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas
 * Trautmann</a>
 */
public class BOBrowserNode extends FilterNode {

    protected static Logger log = Logger.getLogger(BOBrowserNode.class.getName());
    public static final String BOBROWSER_FOLDER = "BOBrowser";
    public static final String ACTION_FOLDER = "Actions/BOBrowser";

    public static List<BO> getBOs() {
        return BOFolderChildren.getBOs();
    }

    public BOBrowserNode(Node folderNode) throws DataObjectNotFoundException {
        super(folderNode, new BOFolderChildren(folderNode));
    }

    public String initIconBaseWithExtension() {
        return "de/linogistix/bobrowser/res/icon/Document.png";
    }

    public String toString() {
        return getName() + ":BONode";
    }

//--------------------------------------------------------------------------
    /**
     * Getting the root node
     */
    public static class RootBONode extends BOBrowserNode {

        public RootBONode() throws DataObjectNotFoundException {

          super(DataObject.find(
          FileUtil.getConfigRoot().getFileObject(BOBROWSER_FOLDER)).getNodeDelegate());
    
          Thread t = new Thread(new Runnable() {
                public void run() {
                    log.log(Level.INFO, "--- Starting initialization of BOs ---");
                    getBOs();
                    log.log(Level.INFO, "--- Finished initialization of BOs ---");
                }
            });

            t.start();
        }

        public String getDisplayName() {
            return NbBundle.getMessage(CommonBundleResolver.class, "Data");
        }
    }

    /**
     * Getting the children of the root node
     */
    public static class BOFolderChildren extends FilterNode.Children {

        BOFolderChildren(Node folderNode) {
            super(folderNode);
        }

        @Override
        protected Node[] createNodes(Node key) {
            
            List<Node> children = new ArrayList<Node>();
            //DataFolder folder = (DataFolder) key.getLookup().lookup(DataFolder.class);
            //DataObject dao = (DataObject) key.getLookup().lookup(DataObject.class);
            
            //dgrys -workaround- for warnings: Attempt to obtain DataObject, java.lang.Exception: Find for MultiFileObject
            FileObject folderFO = (FileObject) key.getLookup().lookup(FileObject.class);
            DataFolder folder = null;
            if(folderFO.isFolder()) {
                folder=DataFolder.findFolder(folderFO);
            }
            
            FileObject daoFO = (FileObject) key.getLookup().lookup(FileObject.class);
            DataObject dao=null;
            try {
                dao = DataObject.find(daoFO);
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }

            if (folder != null) {

//                FileObject fo = folder.getPrimaryFile();
//                String resBundle = (String) fo.getAttribute("SystemFileSystem.localizingBundle");
                try {

//                    fo.get
//                    
//                    Class bundleResolverClass = Class.forName(bundleResolver);
//               
//                    if (resBundle != null) {
//                        BundleHelper h = new BundleHelper(resBundle, bundleResolverClass);
                    String name = folder.getName();
                    if (name != null && name.length() > 0) {
//                            name = h.resolve2(name, new Object[0]);
                        key.setDisplayName(name);
                    }
//                    }
                    BOBrowserNode child = new BOBrowserNode(key);
                    children.add(child);
                    return children.toArray(new Node[0]);
                } catch (Throwable t) {
                    log.severe(t.getMessage());
                    log.log(Level.INFO, t.getMessage(), t);
                }
            }

            try {
                if (dao instanceof DataObject) {
                    BO bo = getBO(dao.getNodeDelegate());
                    if (bo != null) {
                        //                    initActions(bo);
                        //                    initMasterActions(bo);
                        log.info("init BONodes for: " + bo.getName());
                        BONode boNode = new BONode(bo);
                        if (bo.isShowInExplorer() && checkRoles(bo.getAllowedRoles())) {
                            children.add(boNode);
                        }
                    }
                }
            } catch (Throwable t) {
                log.log(Level.SEVERE, t.getMessage(), t);
            }

            return children.toArray(new Node[0]);

        }

        /**
         * init every node to be sure that registration for BOLookup is done for
         * any subclass of BO and returns a list of those.
         */
        public static List<BO> getBOs() {
            try {
                DAORecurser rec = new DAORecurser();
                return rec.bos;
            } catch (Throwable ex) {
                log.severe(ex.getMessage());
                return null;
            }
        }

        /**
         * Looking up a BONode
         */
        static BO getBO(Node node) {
            BO bo;
            try {
                BOLookup l = (BOLookup) Lookup.getDefault().lookup(BOLookup.class);
                DataObject dao = (DataObject) node.getLookup().lookup(DataObject.class);
                if (dao != null) {
                    if (dao instanceof DataShadow) {
                        DataShadow sh = (DataShadow) dao;
                        dao = sh.getOriginal();
                    }
                    InstanceCookie ck = (InstanceCookie) dao.getCookie(InstanceCookie.class);
                    if (ck != null) {
                        bo = (BO) ck.instanceCreate();

                        BO exist;
                        if ((exist = (BO) l.lookup(bo.getClass())) != null) {
                            //                            && exist.getClass().equals(bo.getClass())){
                            //In case there is more than one BO per Entity class the last one will be stored in lookup
                            return exist;
                        }

                        FileObject fo = dao.getPrimaryFile();
                        Boolean show = (Boolean) fo.getAttribute("showInExplorer");
                        if (show == null) {
                            show = new Boolean(true);
                        }
                        bo.setShowInExplorer(show);
                        initActions(bo);
                        initMasterActions(bo);

                        l.addBO(bo.getClass(), bo);

                        Boolean defaultEntityLookUp = (Boolean) fo.getAttribute("defaultEntityLookUp");
                        if (defaultEntityLookUp == null) {
                            defaultEntityLookUp = new Boolean(true);
                        }
                        if (defaultEntityLookUp) {
                            l.addBO(bo.getBusinessObjectTemplate().getClass(), bo);
                        }
                        return bo;
                    }
                }
            } catch (Throwable t) {
                log.log(Level.SEVERE, t.getMessage(), t);
            }
            return null;
        }

        public static void initMasterActions(BO bo) {

            try {

                String masterActionsName;
                DataObject masterActions;
                DataFolder masterActionFolder;

                // global entries
                masterActionsName = ACTION_FOLDER + "/MasterActions";
                FileObject fob = FileUtil.getConfigRoot().getFileObject(masterActionsName);
                if (fob != null) {
                    masterActions = DataObject.find(fob);
                    masterActionFolder = masterActions.getLookup().lookup(DataFolder.class);
                    for (DataObject o : masterActionFolder.getChildren()) {
                        InstanceCookie ck = o.getCookie(InstanceCookie.class);
                        if (ck != null) {
                            Object instance = ck.instanceCreate();
                            if (instance instanceof SystemAction) {
                                bo.addMasterAction((SystemAction) instance, -1);
                            }
                        }
                    }
                }

                masterActionsName = ACTION_FOLDER + "/" + bo.getName() + "/MasterActions";
                fob = FileUtil.getConfigRoot().getFileObject(masterActionsName);
                if (fob != null) {
                    masterActions = DataObject.find(fob);
                    masterActionFolder = masterActions.getLookup().lookup(DataFolder.class);
                    for (DataObject o : masterActionFolder.getChildren()) {
                        InstanceCookie ck = o.getCookie(InstanceCookie.class);
                        if (ck != null) {
                            Object instance = ck.instanceCreate();
                            if (instance instanceof SystemAction) {
                                bo.addMasterAction((SystemAction) instance, -1);
                            }
                        }
                    }
                }
            } catch (Throwable ex) {
                log.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }

        public static void initActions(BO bo) {

            try {

                String actionsName;
                DataObject actions;
                DataFolder actionFolder;

                // global entries
                actionsName = ACTION_FOLDER + "/Actions";
                //FileObject fob = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject(actionsName);
                FileObject fob =  FileUtil.getConfigRoot().getFileObject(actionsName);

                if (fob != null) {
                    actions = DataObject.find(fob);
                    actionFolder = actions.getLookup().lookup(DataFolder.class);
                    for (DataObject o : actionFolder.getChildren()) {
                        InstanceCookie ck = o.getCookie(InstanceCookie.class);
                        Object instance = ck.instanceCreate();
                        if (instance instanceof SystemAction) {
                            bo.addAction((SystemAction) instance, -1);
                        }
                    }
                }

                actionsName = ACTION_FOLDER + "/" + bo.getName() + "/Actions";
                fob = FileUtil.getConfigRoot().getFileObject(actionsName);
                if (fob != null) {
                    actions = DataObject.find(fob);
                    actionFolder = actions.getLookup().lookup(DataFolder.class);
                    for (DataObject o : actionFolder.getChildren()) {
                        InstanceCookie ck = o.getCookie(InstanceCookie.class);
                        Object instance = ck.instanceCreate();
                        if (instance instanceof SystemAction) {
                            bo.addAction((SystemAction) instance, -1);
                        }
                    }
                }

            } catch (Throwable ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    /**
     * Checks whether the logged in user is allowed to see this node.
     */
    public static boolean checkRoles(String[] roles) {
        log.info("checkRoles");
        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
        return login.checkRolesAllowed(roles);
    }

    static class DAORecurser {

        List<BO> bos;
        final int MAX_RUNS = 1000;
        int runNo = 0;

        DAORecurser() {
            try {
                this.bos = new ArrayList<BO>();
                DataObject dao = DataObject.find(FileUtil.getConfigRoot().getFileObject(BOBROWSER_FOLDER));
                recurse(dao);
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        void recurse(DataObject dao) {
            if ((runNo++) >= MAX_RUNS) {
                log.severe("Max_RUNS reached");
                return;
            }
            try {
                DataFolder folder = dao.getLookup().lookup(DataFolder.class);
                if (folder != null) {
                    for (DataObject o : folder.getChildren()) {
                        recurse(o);
                    }
                } else {
                    BO bo = BOFolderChildren.getBO(dao.getNodeDelegate());
                    this.bos.add(bo);
                }
            } catch (Throwable t) {
                log.severe(t.getMessage());
                log.log(Level.SEVERE, t.getMessage(), t);
            }
        }
    }

}
