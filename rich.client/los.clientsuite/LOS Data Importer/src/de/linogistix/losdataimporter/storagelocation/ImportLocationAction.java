/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.losdataimporter.storagelocation;

import de.linogistix.common.action.OpenDocumentTask;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.services.J2EEServiceLocatorException;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.location.facade.StorageLocationLabelReportFacade;
import de.linogistix.los.location.query.dto.StorageLocationTO;
import de.linogistix.los.location.report.StorageLocationLabelTO;
import de.linogistix.losdataimporter.res.BundleResolver;
import java.awt.Dialog;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.mywms.facade.FacadeException;
import org.mywms.globals.Role;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class ImportLocationAction extends CallableSystemAction {

    private J2EEServiceLocator loc;
    
    private LoginService login;
    
    private static final String[] allowedRoles = new String[]{
        Role.ADMIN_STR,
        Role.CLIENT_ADMIN_STR
    };
    
    public ImportLocationAction(){
  
        login = Lookup.getDefault().lookup(LoginService.class);

    }
    
    @Override
    public boolean isEnabled() {
 
        if (login == null) return false;
        
        return login.checkRolesAllowed(allowedRoles);
        
    }

    
    
    public void performAction() {

        ImportLocationWizard w;
        boolean redo = true;
        try {
            w = new ImportLocationWizard(null);
        } catch (InstantiationException ex) {
            ExceptionAnnotator.annotate(ex);
            return;
        }

        while (redo) {
            try {

                Dialog d = DialogDisplayer.getDefault().createDialog(w);
                d.setVisible(true);

                if (w.getValue().equals(NotifyDescriptor.OK_OPTION)) {
                    if (w.getLabelFile() != null) {
                        CursorControl.showWaitCursor();
                        J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
                        StorageLocationLabelReportFacade rep = loc.getStateless(StorageLocationLabelReportFacade.class);

                        List<StorageLocationLabelTO> labels = new ArrayList<StorageLocationLabelTO>();
                        for (StorageLocationTO s : w.getImportedLocations()) {
                            if (s == null) {
                                continue;
                            }
                            StorageLocationLabelTO to = new StorageLocationLabelTO(s.getName(), 1);
                            labels.add(to);
                        }

                        byte[] bytes = rep.generateStorageLocationLabels(labels);
                        FileOutputStream os = new FileOutputStream(w.getLabelFile());
                        os.write(bytes);
                        os.close();

                        if (w.isOpenLabels()) {
                            OpenDocumentTask.openDocument(w.getLabelFile().getAbsolutePath());
                        }

                    }
                }

                redo = false;

            } catch (FacadeException ex) {
                ExceptionAnnotator.annotate(ex);
                ImportLocationWizard tmp;
//                try {
//                    tmp = new ImportLocationWizard(null);
//                    tmp.setFile(w.getFile());
//                    w = tmp;
//                } catch (InstantiationException ex1) {
//                    ExceptionAnnotator.annotate(ex);
//                    return;
//                }
//                redo = true;
                redo = false;
            } catch (Throwable ex) {
                ExceptionAnnotator.annotate(ex);
                redo = false;
            } finally {
                CursorControl.showNormalCursor();
            }
        }
    }

    public String getName() {
        return NbBundle.getMessage(BundleResolver.class, "CTL_DataImportRackLocations");
    }

    @Override
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() Javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
