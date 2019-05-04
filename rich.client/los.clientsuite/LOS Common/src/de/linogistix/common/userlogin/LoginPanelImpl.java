/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.userlogin;

import de.linogistix.common.bobrowser.browse.BOBrowserNode;
import de.linogistix.common.gui.component.other.TopComponentExt;
import de.linogistix.common.services.J2EEServiceNotAvailable;
import de.linogistix.common.system.ModulInstallExtSingleton;
import de.linogistix.common.util.ExceptionAnnotator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Set;
import javax.swing.SwingUtilities;
import org.mywms.facade.FacadeException;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author artur
 */
public class LoginPanelImpl implements ActionListener {

    private LoginPanel panel = new LoginPanel();
    private DialogDescriptor d = null;

    public void doLogin() {

        panel.nameTextField.selectAll();
        
        d = new DialogDescriptor(panel, "Login", true, this);
        d.setClosingOptions(new Object[]{});
        //Listen if someone clicked on the X for closing the dialog.
        d.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName().equals(DialogDescriptor.PROP_VALUE) && event.getNewValue() == DialogDescriptor.CLOSED_OPTION) {
                    System.exit(0);
                }
            }
        });
        
        DialogDisplayer.getDefault().notify(d);
       
    }
    
    

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() != null && event.getSource() == DialogDescriptor.CANCEL_OPTION) {
            System.exit(0);
        } else {
            try {
                
                boolean loggedin = SecurityManager.login(panel.getUsername(), panel.getPassword());

                ModulInstallExtSingleton.getInstance().informClasses();

                if (loggedin)
                    initializeBOs();

                informOpenComponents();
                printOpenComponents();
                d.setClosingOptions(null);

                    
            } catch (LoginException ex) {                
                panel.setInfo(ExceptionAnnotator.resolveMessage(ex));
            } catch (J2EEServiceNotAvailable ex) {                            
                panel.setInfo(ExceptionAnnotator.resolveMessage(ex));
            } catch (FacadeException ex) {
                panel.setInfo(ex.getLocalizedMessage());
            }
        }
    }
    
    private void initializeBOs(){
        
         BOBrowserNode.getBOs();
    }


    /**
     * Send an componendOpen event again, so TopComponent which need authorisation
     * can react on it.
     */
    private void informOpenComponents() {
                
        //must be run in AWT Thread. Else exception can occured by open an TopComponent
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                Set modes = WindowManager.getDefault().getModes();
                if (modes != null) {
                    Iterator it = modes.iterator();
                    if (it != null) {
                        while (it.hasNext()) {
                            Mode mode = (Mode) it.next();
                            //Ignore ! because this comp return by readResolve null and through an exception 
//                            if ((mode.getName().equals("properties") == false) && (mode.getClass().toString().equals("org.netbeans.core.windows.ModeImpl") == false)) {
                                TopComponent[] comp = mode.getTopComponents();
                                for (TopComponent tc : comp) {
                                    if (tc.isOpened()) {
                                        if (tc instanceof TopComponentExt) {
                                            //If some TopComponent extended TopComponentExt the
                                            //override method will be used.
                                            ((TopComponentExt) tc).componentOpened();
                                        } else {
                                            tc.close();
                                            tc.open();
                                        }
                                    }
                                }

//                            }
                        }
                    }
                }
            }
        });
    }

    private void printOpenComponents() {
        //must be run in AWT Thread. Else exception can occured by open an TopComponent
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {

                Set modes = WindowManager.getDefault().getModes();
                if (modes != null) {
                    Iterator it = modes.iterator();
                    for (int i = 0; i < modes.size(); i++) {
                        Mode mode = (Mode) it.next();
                        System.out.println("Mode name = " + mode.getName());
                        System.out.println("Mode toString = " + mode.toString());
                        System.out.println("Mode class = " + mode.getClass());
                        System.out.println("Mode hashcode = " + mode.hashCode());
                        //Ignore ! becouse this comp return by readResolve null and through an exception 
//                        if ((mode.getName().equals("properties") == false) && (mode.getClass().toString().equals("org.netbeans.core.windows.ModeImpl") == false)) {
                            for (TopComponent tc : mode.getTopComponents()) {
                                if (tc.isOpened()) {
                                    System.out.println("Open = " + mode.getName() + "  " + tc.toString());
                                }
                            }
//                        }
                    }
                }
            }
        });
    }
}
