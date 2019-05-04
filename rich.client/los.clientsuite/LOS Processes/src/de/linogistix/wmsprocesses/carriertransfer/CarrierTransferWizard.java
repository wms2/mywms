/*
 * Copyright (c) 2012 LinogistiX GmbH
 *
 *  www.linogistix.com
 *
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.carriertransfer;

import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.query.BODTO;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author krane
 */
public class CarrierTransferWizard extends WizardDescriptor implements ChangeListener {

    private BODTO<LOSUnitLoad> source;
    private BODTO<LOSUnitLoad> destination;
    private String hint;

    /**
     * Creates a new instance of OrderByWizard
     */
    @SuppressWarnings("unchecked")
    public CarrierTransferWizard(BODTO unitLoadTo) throws InstantiationException {
        super(createPanels());

        putProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
        putProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
        putProperty("WizardPanel_contentData", getContentData());
        putProperty("WizardPanel_contentNumbered", Boolean.TRUE);
        putProperty("WizardPanel_image", ImageUtilities.loadImage("de/linogistix/wmsprocesses/res/img/TransferCarrier.png"));
        setTitle(NbBundle.getMessage(WMSProcessesBundleResolver.class, "Carrier.DialogTitle"));

        setTitleFormat(new MessageFormat(NbBundle.getMessage(CommonBundleResolver.class, "Wizard.titleFormat")));
        

//        setHelpCtx(new HelpCtx("de.linogistix.wmsprocesses.unitloadtransfer"));

        if (unitLoadTo != null) {
            setSource(unitLoadTo);
        }

    }

    //-------------------------------------------------------------------------------
    public final static Panel[] createPanels() throws InstantiationException {
        List<Panel> panels = new ArrayList<Panel>();

        ValidatingPanel p1 = new CarrierTransferDataPage();
        panels.add(p1);

        FinishablePanel p2 = new CarrierTransferHintPage();
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


    String getHint() {
        return this.hint;
    }

    void setHint(String hint) {
        this.hint = hint;
    }

    private String[] getContentData() {
       return new String[]{
        NbBundle.getMessage(WMSProcessesBundleResolver.class, "BOUnitLoadChooseDestinationPanel.contentData"),
        NbBundle.getMessage(WMSProcessesBundleResolver.class, "BOUnitLoadHintPanel.contentData"),
       
       };
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx("de.linogistix.wmsprocesses.unitloadtransfer");
    }

    public BODTO<LOSUnitLoad> getDestination() {
        return destination;
    }

    public void setDestination(BODTO<LOSUnitLoad> destination) {
        this.destination = destination;
    }

    public BODTO<LOSUnitLoad> getSource() {
        return source;
    }

    public void setSource(BODTO<LOSUnitLoad> source) {
        this.source = source;
    }

    
    

}


