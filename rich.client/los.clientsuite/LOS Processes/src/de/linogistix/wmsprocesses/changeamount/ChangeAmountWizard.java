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
package de.linogistix.wmsprocesses.changeamount;

import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.los.query.BODTO;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.mywms.model.StockUnit;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 * A Wizard for creating new BusinessObjects.
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class ChangeAmountWizard extends WizardDescriptor implements ActionListener, ChangeListener {

    private static final Logger log = Logger.getLogger(ChangeAmountWizard.class.getName());
    private BODTO<StockUnit> su;
    private StockUnit stockUnit;
    private BigDecimal amount = new BigDecimal(0);
    private BigDecimal reserveAmount = new BigDecimal(0);
    private boolean releaseReservation = false;
    private String info = null;

    /**
     * Creates a new instance of OrderByWizard
     */
    @SuppressWarnings("unchecked")
    public ChangeAmountWizard(BODTO suTO) throws InstantiationException {
        super(createPanels());


        putProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
        putProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
        putProperty("WizardPanel_contentData", getContentData());
        putProperty("WizardPanel_contentNumbered", Boolean.TRUE);
        putProperty("WizardPanel_image", ImageUtilities.loadImage("de/linogistix/wmsprocesses/res/img/ChangeAmountStockUnit.png"));
        setTitle(NbBundle.getMessage(WMSProcessesBundleResolver.class, "changeAmount"));

        setTitleFormat(new MessageFormat(NbBundle.getMessage(CommonBundleResolver.class, "Wizard.titleFormat")));

        setHelpCtx(new HelpCtx("de.linogistix.wmsprocesses.changeamount"));

        if (suTO != null) {
            setSu(suTO);
        }

        setButtonListener(this);

    }

    //-------------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public final static Panel[] createPanels() throws InstantiationException {
        List<Panel> panels = new ArrayList();

        ValidatingPanel p1 = new ChangeAmountDataPage();
        panels.add(p1);

        FinishablePanel p3 = new ChangeAmountInfoPage();
        panels.add(p3);

        return (Panel[]) panels.toArray(new Panel[0]);
    }

    public BODTO<StockUnit> getSu() {
        return su;
    }

    public void setSu(BODTO<StockUnit> su) {
        this.su = su;
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

    public StockUnit getStockUnit() {
        return stockUnit;
    }

    public void setStockUnit(StockUnit stockUnit) {
        this.stockUnit = stockUnit;
    }

    public void actionPerformed(ActionEvent e) {
        //
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getReserveAmount() {
        return reserveAmount;
    }

    public void setReserveAmount(BigDecimal reserveAmount) {
        this.reserveAmount = reserveAmount;
    }

    public boolean isReleaseReservation() {
        return releaseReservation;
    }

    public void setReleaseReservation(boolean releaseReservation) {
        this.releaseReservation = releaseReservation;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
    
    private String[] getContentData() {
       return new String[]{
        NbBundle.getMessage(WMSProcessesBundleResolver.class, "BOStockUnitChangeAmountPanelData.contentData"),
        NbBundle.getMessage(WMSProcessesBundleResolver.class, "ChangeAmountInfoPanel.contentData"),
       
       };
    }
    
}


