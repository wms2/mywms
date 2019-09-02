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
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.inventory.query.LOSAdviceQueryRemote;
import de.linogistix.los.inventory.query.dto.LOSAdviceTO;
import de.linogistix.los.query.BODTO;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import de.wms2.mywms.goodsreceipt.GoodsReceipt;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.product.ItemData;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.mywms.facade.FacadeException;
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
public class PositionWizard extends WizardDescriptor implements ActionListener, ChangeListener {

    private static final Logger log = Logger.getLogger(PositionWizard.class.getName());

    public PositionWizardModel model;
    
    public static int goodsInDefaultLock = 0;
    
    /**
     * Creates a new instance of OrderByWizard
     */
    @SuppressWarnings("unchecked")
    public PositionWizard(BODTO<Client> clientTO, GoodsReceipt gr, LOSAdviceTO selectedAdvice, boolean isSingleUnitLoad, int defaultLock) throws InstantiationException {
        super(createPanels());
        goodsInDefaultLock = defaultLock;

        this.model = new PositionWizardModel();
        this.model.gr = gr;
        this.model.client = clientTO;
        this.model.lock = goodsInDefaultLock;
        this.model.isSingleUnitLoad = isSingleUnitLoad;
        
        if(selectedAdvice != null){
            
            J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
            LOSAdviceQueryRemote advQuery;
            
            try {
                advQuery = loc.getStateless(LOSAdviceQueryRemote.class);

                this.model.selectedAdvice = advQuery.queryById(selectedAdvice.getId());
//                this.model.selectedAdviceTO = selectedAdvice;
                
                UnitLoadType ult = model.selectedAdvice.getItemData().getDefaultUnitLoadType();
                if(ult != null){
                    model.ulType = new BODTO<UnitLoadType>(ult.getId(), ult.getVersion(), ult.getName());
                }

                this.model.item = new BODTO<ItemData>(this.model.selectedAdvice.getItemData().getId(), this.model.selectedAdvice.getItemData().getVersion(), this.model.selectedAdvice.getItemData().getNumber());

            } catch(FacadeException fe){
                ExceptionAnnotator.annotate(fe);
            }

        }
        
        putProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
        putProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
        putProperty("WizardPanel_contentData", getContentData());
        putProperty("WizardPanel_contentNumbered", Boolean.TRUE);
        setTitle(NbBundle.getMessage(WMSProcessesBundleResolver.class, "PositionWizard.createPosition"));      
        setTitleFormat(new MessageFormat(NbBundle.getMessage(CommonBundleResolver.class, "Wizard.titleFormat")));

        setButtonListener(this);

    }

    //-------------------------------------------------------------------------------
    public final static Panel[] createPanels() throws InstantiationException {
        
        List<Panel> panels = new ArrayList<Panel>();
        
        ValidatingPanel p1 = new PositionWizardULPanel();
        panels.add(p1);
        
        ValidatingPanel p2 = new PositionWizardSUPanel();
        panels.add(p2);
        
        ValidatingPanel p2a = new PositionWizardLotPanel();
        panels.add(p2a);
        
//        if(goodsInDefaultLock == 0){
            ValidatingPanel p3 = new PositionWizardQMPanel();
            panels.add(p3);
//        }
        
        return (Panel[]) panels.toArray(new Panel[0]);
    }

    private String[] getContentData() {
        String[] ret;
        if(goodsInDefaultLock == 0){
            ret = new String[]{
                NbBundle.getMessage(WMSProcessesBundleResolver.class, "PositionWizard.contentUnitLoad"),
                NbBundle.getMessage(WMSProcessesBundleResolver.class, "PositionWizard.contentStock"),
                NbBundle.getMessage(WMSProcessesBundleResolver.class, "PositionWizard.contentLot"),
                NbBundle.getMessage(WMSProcessesBundleResolver.class, "PositionWizard.contentQuality"),
            };
        }
        else {
            ret = new String[]{
                NbBundle.getMessage(WMSProcessesBundleResolver.class, "PositionWizard.contentUnitLoad"),
                NbBundle.getMessage(WMSProcessesBundleResolver.class, "PositionWizard.contentStock"),
                NbBundle.getMessage(WMSProcessesBundleResolver.class, "PositionWizard.contentLot"),
            };

        }
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


