/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 *
 *  www.linogistix.com
 *
 *  Project myWMS-LOS
 */
package de.linogistix.reference;

import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.system.ModuleInstallExt;
import de.linogistix.common.system.SystemUtil;
import de.linogistix.los.reference.facade.RefTopologyFacade;
import de.linogistix.los.user.LoginServiceRemote;
import de.linogistix.reference.res.ReferenceBundleResolver;
import org.mywms.facade.FacadeException;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
//public class Installer extends ModuleInstall {
public class Installer extends ModuleInstallExt {




    @Override
    public void restored() {
        checkBasicData();
        checkLogin();
    }


    @Override
    public void postRestored() {

//        MutableMultiFileSystem mf = (MutableMultiFileSystem) Lookup.getDefault().lookup(FileSystem.class);

//        URL url = getClass().getClassLoader().getResource("de/linogistix/common/layer_default.xml");

//        mf.addLayer(url);


    }


    public void checkBasicData() {
        try {

            J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
            RefTopologyFacade facade = loc.getStateless(RefTopologyFacade.class);

            if( !facade.checkDemoData() ) {
                NotifyDescriptor d = new NotifyDescriptor.Confirmation(
                        NbBundle.getMessage(ReferenceBundleResolver.class, "createDemoTopology.notify"),
                        NbBundle.getMessage(ReferenceBundleResolver.class, "createDemoTopology"),
                        NotifyDescriptor.OK_CANCEL_OPTION);

                if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION) {

                    facade.createDemoTopology();
                }
            }

        } catch  (Throwable t) {
            ErrorManager em = ErrorManager.getDefault();
            //em.annotate(t, t.getMessage());
            em.notify(t);
        }
    }

    public void checkLogin() {
        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            LoginServiceRemote authService;
            authService = (LoginServiceRemote) loc.getStateless(LoginServiceRemote.class);
            String clientVersion = SystemUtil.getSpecVersion("de.linogistix.reference");
            authService.loginCheck(loc.getWorkstationName(), null, clientVersion);
        } catch (FacadeException ex) {
            NotifyDescriptor d = new NotifyDescriptor.Message(ex.getLocalizedMessage());
            DialogDisplayer.getDefault().notify(d);
            System.exit(0);
        }
    }
}
