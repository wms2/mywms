/*
 * Copyright (c) 2012 LinogistiX GmbH
 *
 *  www.linogistix.com
 *
 *  Project myWMS-LOS
 */
package de.linogistix.location.dialog;

import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.location.res.LocationBundleResolver;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.location.model.LOSRack;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.util.NbBundle;

/**
 *
 * @author krane
 */
public class LocationOrderWizard extends WizardDescriptor implements ChangeListener {


    public int valueStart = 0;
    public int valueDiff = 1;
    public BODTO<LOSRack> rack = null;
    
    /**
     * Creates a new instance of OrderByWizard
     */
    @SuppressWarnings("unchecked")
    public LocationOrderWizard(BODTO<LOSRack> rack) throws InstantiationException {
        super(createPanels());

        putProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
        putProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
        putProperty("WizardPanel_contentData", getContentData());
        putProperty("WizardPanel_contentNumbered", Boolean.TRUE);
//        putProperty("WizardPanel_image", ImageUtilities.loadImage("de/linogistix/wmsprocesses/res/img/TransferCarrier.png"));
        setTitle(NbBundle.getMessage(LocationBundleResolver.class, "LocationOrderWizard.title"));

        setTitleFormat(new MessageFormat(NbBundle.getMessage(CommonBundleResolver.class, "Wizard.titleFormat")));
        

        this.rack = rack;


    }

    //-------------------------------------------------------------------------------
    public final static Panel[] createPanels() throws InstantiationException {
        List<Panel> panels = new ArrayList<Panel>();

        FinishablePanel p1 = new LocationOrderDataPage();
        panels.add(p1);
        
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
            NbBundle.getMessage(LocationBundleResolver.class, "LocationOrderWizard.contentData"),
       };
    }

    
    

}


