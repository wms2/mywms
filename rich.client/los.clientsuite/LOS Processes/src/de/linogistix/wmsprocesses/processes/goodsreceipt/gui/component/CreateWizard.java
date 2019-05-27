/*
 * OrderByWizard.java
 *
 * Created on 27. Juli 2006, 00:27
 *
 * Copyright (c) 2006-2012 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.wmsprocesses.processes.goodsreceipt.gui.component;

import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.query.BODTO;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import de.wms2.mywms.location.StorageLocation;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.mywms.model.Client;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * A Wizard for creating new BusinessObjects.
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class CreateWizard extends WizardDescriptor implements ActionListener, ChangeListener {

    private static final Logger log = Logger.getLogger(CreateWizard.class.getName());

    GoodsReceiptController controller;
    
    BODTO<Client> client;
        
    String deliverer;
    
    String externNumber;
    
    String info;
    
    BODTO<StorageLocation> gate;
    
    Date date = new Date();
    
    boolean allowChangeOfClient = true;
    
    /**
     * Creates a new instance of OrderByWizard
     */
    @SuppressWarnings("unchecked")
    public CreateWizard(GoodsReceiptController controller, boolean change) throws InstantiationException {
        super(createPanels());
        this.controller = controller;
        putProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
        setTitle(NbBundle.getMessage(WMSProcessesBundleResolver.class, change ? "CreateWizard.titleChange" : "CreateWizard.title"));
        putProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
        putProperty("WizardPanel_contentData", getContentData());
        putProperty("WizardPanel_contentNumbered", Boolean.TRUE);
        setTitleFormat(new MessageFormat(NbBundle.getMessage(CommonBundleResolver.class, "Wizard.titleFormat")));

        CursorControl.showWaitCursor();
        try {
            J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
        } finally {
            CursorControl.showNormalCursor();
        }

        setButtonListener(this);

    }

    //-------------------------------------------------------------------------------
    public final static Panel[] createPanels() throws InstantiationException {
        List<Panel> panels = new ArrayList<Panel>();

        FinishablePanel p1 = new CreateWizardDetailPanel();
        panels.add(p1);
        
        FinishablePanel p2 = new CreateWizardInfoPanel();
        panels.add(p2);
        
        return (Panel[]) panels.toArray(new Panel[0]);
    }

    private String[] getContentData() {
        String[] ret;
        ret = new String[]{
            NbBundle.getMessage(WMSProcessesBundleResolver.class, "CreateWizard.contentData"),
            NbBundle.getMessage(WMSProcessesBundleResolver.class, "CreateWizard.contentComment"),
        };
        return ret;
    }

    public void stateChanged(ChangeEvent e) {
        putProperty("WizardPanel_errorMessage", null);
        updateState();
    }

    public JButton getFinishOption() {
        for (Object o : getClosingOptions()) {
            if (o instanceof JButton) {
                JButton b = (JButton) o;
                return b;
            }
        }

        return null;
    }
    //-----------------------------------------------------------

   
    public void actionPerformed(ActionEvent e) {
        //
    }
}


