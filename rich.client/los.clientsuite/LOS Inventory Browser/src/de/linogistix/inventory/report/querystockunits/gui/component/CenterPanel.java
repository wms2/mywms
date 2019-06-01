/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.inventory.report.querystockunits.gui.component;

import de.linogistix.common.gui.listener.TopComponentListener;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.query.BODTO;
import de.linogistix.inventory.report.querystockunits.gui.gui_builder.AbstractCenterPanel;
import de.linogistix.inventory.report.querystockunits.gui.gui_builder.TopComponentPanel;
import de.linogistix.reports.res.ReportsBundleResolver;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.product.ItemData;
import org.mywms.facade.FacadeException;

/**
 *
 * @author trautm
 */
public class CenterPanel extends AbstractCenterPanel implements TopComponentListener{

    TopComponentPanel topComponentPanel;
            
     public CenterPanel(TopComponentPanel topComponentPanel) {
        this.topComponentPanel = topComponentPanel;
     }
     
    @Override
    public void process() {
        
        ItemData itemData = getItemDataComboBox().getSelectedAsEntity();
        BODTO<ItemData> idat = null;
        if (itemData != null){
            idat= new BODTO<ItemData>(
                itemData.getId(),
                itemData.getVersion(),
                itemData.getNumber());
        }
        
        Lot lot = getLotComboBox().getSelectedAsEntity();
        BODTO<Lot> l = null;
        if (lot != null){
            l = new BODTO<Lot>(
                l.getId(),
                l.getVersion(),
                l.getName());
        }
        
        StorageLocation storloc = getStorageLocationComboBox().getSelectedAsEntity();
        BODTO<StorageLocation> sl = null;
        if (storloc != null){
            sl = new BODTO<StorageLocation>(
                sl.getId(),
                sl.getVersion(),
                sl.getName());
        }
        
        if (sl == null && idat == null && idat == null){
            FacadeException ex  = new FacadeException("Missing Query Parameter", "BusinessException.ProvideQueryParameter", null);
            ex.setBundleResolver(ReportsBundleResolver.class);
            ExceptionAnnotator.annotate(ex);
            return;
        }
        
        stockUnitTreeTablePanel.setNodes(
                idat,
                l,
                sl);
    }

     public void clear(){
        getItemDataComboBox().clear();
        getLotComboBox().clear();
        getStorageLocationComboBox().clear();
        
    }

    public void componentOpened() {
        clear();
    }

    public void componentClosed() {
        clear();
    }

    public void componentActivated() {
    }

    public void componentDeactivated() {
    }

    public void componentHidden() {
    }

    public void componentShowing() {
    }
}
