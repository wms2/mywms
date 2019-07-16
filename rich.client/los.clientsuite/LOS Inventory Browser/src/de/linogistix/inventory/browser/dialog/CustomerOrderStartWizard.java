/*
 * Copyright (c) 2012 LinogistiX GmbH
 *
 *  www.linogistix.com
 *
 *  Project myWMS-LOS
 */
package de.linogistix.inventory.browser.dialog;

import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.query.dto.LOSCustomerOrderTO;
import de.linogistix.los.util.StringTools;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author krane
 */
public class CustomerOrderStartWizard extends WizardDescriptor implements ChangeListener {

    private boolean changed = false;
    private List<LOSCustomerOrderTO> orders;
    private J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);

    public Integer prio = null;
    public String userName = null;
    public String destinationName = null;
    public int numOrder = 0;
    public boolean combine = false;
    public boolean release = true;

    public String hint;

    /**
     * Creates a new instance of OrderByWizard
     */
    @SuppressWarnings("unchecked")
    public CustomerOrderStartWizard(List<LOSCustomerOrderTO> orders) throws InstantiationException {
        super(createPanels());

        putProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
        putProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
        putProperty("WizardPanel_contentData", getContentData());
        putProperty("WizardPanel_contentNumbered", Boolean.TRUE);
//        putProperty("WizardPanel_image", ImageUtilities.loadImage("de/linogistix/wmsprocesses/res/img/TransferCarrier.png"));
        setTitle(NbBundle.getMessage(InventoryBundleResolver.class, "CustomerOrderStartWizard.title"));

        setTitleFormat(new MessageFormat(NbBundle.getMessage(CommonBundleResolver.class, "Wizard.titleFormat")));
        

        this.orders = orders;

        boolean destinationDiff = false;
        boolean prioDiff = false;

        for( LOSCustomerOrderTO o : orders ) {
            if( numOrder == 0 ) {
                destinationName = o.getDestinationName();
                prio = o.getPrio();
            }
            else {
                if( StringTools.compare(destinationName, o.getDestinationName())!=0 ) {
                    destinationDiff = true;
                }
                if( prio != o.getPrio() ) {
                    prioDiff = true;
                }
            }

            numOrder++;
        }

        if( prioDiff ) {
            prio = null;
        }
        if( destinationDiff ) {
            destinationName = null;
        }

    }

    //-------------------------------------------------------------------------------
    public final static Panel[] createPanels() throws InstantiationException {
        List<Panel> panels = new ArrayList<Panel>();

        ValidatingPanel p1 = new CustomerOrderStartDataPage();
        panels.add(p1);

        FinishablePanel p2 = new CustomerOrderStartHintPage();
        panels.add(p2);
        
        return (Panel[]) panels.toArray(new Panel[0]);
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



    private String[] getContentData() {
       return new String[]{
            NbBundle.getMessage(InventoryBundleResolver.class, "CustomerOrderStartWizard.contentData"),
            NbBundle.getMessage(InventoryBundleResolver.class, "CustomerOrderStartWizard.contentHint"),
       };
    }

    
    

}


