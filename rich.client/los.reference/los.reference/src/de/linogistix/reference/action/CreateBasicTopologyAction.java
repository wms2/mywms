/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.reference.action;

import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.los.reference.facade.RefTopologyFacade;
import de.linogistix.los.reference.model.ProjectPropertyKey;
import de.linogistix.los.util.entityservice.LOSSystemPropertyServiceRemote;
import de.linogistix.reference.res.ReferenceBundleResolver;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.mywms.globals.Role;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author krane
 */
public class CreateBasicTopologyAction extends AbstractAction {
     
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR};
    }
    
    public CreateBasicTopologyAction() {
        super(NbBundle.getMessage(ReferenceBundleResolver.class, "createBasicTopology"));

        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
        boolean result = login.checkRolesAllowed(getAllowedRoles());

        if( result ) {
            try {
                J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
                LOSSystemPropertyServiceRemote prop = loc.getStateless(LOSSystemPropertyServiceRemote.class);
                result = prop.getBooleanDefault(ProjectPropertyKey.CREATE_DEMO_TOPOLOGY, true);
            } catch (Exception ex) {
                result = false;
                System.out.println("Error: "+ex.getMessage());
            }
        }
        setEnabled(result);
    }
    
    
    
    public void actionPerformed(ActionEvent evt) {

        CursorControl.showWaitCursor();
        try {

            NotifyDescriptor d = new NotifyDescriptor.Confirmation(
                    NbBundle.getMessage(ReferenceBundleResolver.class, "createBasicTopology.notify"),
                    NbBundle.getMessage(ReferenceBundleResolver.class, "createBasicTopology"),
                    NotifyDescriptor.OK_CANCEL_OPTION);

            if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION) {
                J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);

                RefTopologyFacade facade = loc.getStateless(RefTopologyFacade.class);
                facade.createBasicTopology();

                d = new NotifyDescriptor.Message(
                    NbBundle.getMessage(ReferenceBundleResolver.class, "createBasicTopology.finish"),
                    NotifyDescriptor.INFORMATION_MESSAGE);

                DialogDisplayer.getDefault().notify(d);

            }

        } catch  (Throwable t) {
        ErrorManager em = ErrorManager.getDefault();
        //em.annotate(t, t.getMessage());
        em.notify(t);
        } finally {
            CursorControl.showNormalCursor();
        }

    }
}


