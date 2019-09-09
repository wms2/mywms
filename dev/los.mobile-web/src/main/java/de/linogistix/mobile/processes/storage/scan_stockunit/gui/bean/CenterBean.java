/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobile.processes.storage.scan_stockunit.gui.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.facade.StorageFacade;
import de.linogistix.mobile.common.gui.bean.BasicBackingBean;
import de.linogistix.mobile.common.gui.bean.NotifyDescriptorExt;
import de.linogistix.mobile.common.gui.bean.NotifyDescriptorExtBean;
import de.linogistix.mobile.common.listener.ButtonListener;
import de.linogistix.mobile.common.system.JSFHelper;
import de.linogistix.mobile.processes.storage.scan_stockunit.NavigationEnum;
import de.wms2.mywms.transport.TransportOrder;


/**
 *
 * @author artur
 */
public class CenterBean extends BasicBackingBean {

    String destinationTextField;
    String pageResult = "";
    boolean finishedButtonEnabled = false;
    public List<TransportOrder> storageRequestList = new ArrayList<>();
    private TransportOrder storageRequest;

    public String addStockunitActionPerformedListener() {
        try {
            setTableData();
            setDestinationTextField("");
            return "";
        } catch (FacadeException ex) {
            Logger.getLogger(CenterBean.class.getName()).log(Level.SEVERE, null, ex);
            List<String> buttonTextList = new ArrayList<String>();
            buttonTextList.add(resolve("Ok", new Object[]{}));
            NotifyDescriptorExt n = new NotifyDescriptorExt(NotifyDescriptorExt.NotifyEnum.INFORMATION, ex.getLocalizedMessage(getLocale()), buttonTextList);
            return n.setCallbackListener(new ButtonListener() {

                public String buttonClicked(final int buttonId, NotifyDescriptorExtBean notifyDescriptorBean) {
                    setDestinationTextField("");
                    return NavigationEnum.storage_scanStockunit_CenterPanel.toString();
                }
            });
        } catch (Throwable ex) {
            Logger.getLogger(CenterBean.class.getName()).log(Level.SEVERE, null, ex);
            InventoryException iex = new InventoryException(InventoryExceptionKey.STORAGE_FAILED, new Object[0]);
            List<String> buttonTextList = new ArrayList<String>();
            buttonTextList.add(resolve("Ok", new Object[]{}));
            NotifyDescriptorExt n = new NotifyDescriptorExt(NotifyDescriptorExt.NotifyEnum.INFORMATION, iex.getLocalizedMessage(getLocale()), buttonTextList);
            return n.setCallbackListener(new ButtonListener() {

                public String buttonClicked(final int buttonId, NotifyDescriptorExtBean notifyDescriptorBean) {
                    setDestinationTextField("");
                    return NavigationEnum.storage_scanStockunit_CenterPanel.toString();
                }
            });
        }
    }

    private void setTableData() throws FacadeException {
//         TableBean managedBean = (TableBean) JSFHelper.getInstance().getBean("storage_scanStockunit_TableBean");        
//         managedBean.getRowList().add(managedBean.getRowObject(getDestinationTextField() , "test","test2"));    
//         Logger.getLogger(CenterBean.class.getName()).log(Level.SEVERE, "Nur ein test", new RuntimeException());
        TableBean managedBean = (TableBean) JSFHelper.getInstance().getSessionBeanForce(TableBean.class);
        StorageFacade pof = getStateless(StorageFacade.class);
        storageRequest = pof.getStorageRequest(destinationTextField, true);
        if (storageRequest != null) {
            
            Logger.getLogger(CenterBean.class.getName()).log(Level.INFO, "found: " + storageRequest.toUniqueString());        
            managedBean.getRowList().add(managedBean.getRowObject(storageRequest.getUnitLoad().toUniqueString(),
                    storageRequest.toUniqueString(),
                    storageRequest.getDestinationLocation().toUniqueString()));
            storageRequestList.add(storageRequest);
            finishedButtonEnabled = true;
        } else {
            Logger.getLogger(CenterBean.class.getName()).log(Level.INFO, "LOSStorageRequest is null");
            throw new NullPointerException();

        }
    }

    public String forwardActionPerformedListener() {
        return NavigationEnum.storage_scanDestination_CenterPanel.toString();
    }

    public String cancelActionPerformedListener() {
        List<String> buttonTextList = new ArrayList<String>();
        buttonTextList.add(resolve("Yes", new Object[]{}));
        buttonTextList.add(resolve("Cancel", new Object[]{}));
        NotifyDescriptorExt n = new NotifyDescriptorExt(NotifyDescriptorExt.NotifyEnum.INFORMATION, resolve("CancelStorage", new Object[]{}), buttonTextList);
        return n.setCallbackListener(new ButtonListener() {

            public String buttonClicked(final int buttonId, NotifyDescriptorExtBean notifyDescriptorBean) {
                switch (buttonId) {
                    case 1: {
                        resetBean();
                        return NavigationEnum.controller_CenterPanel.toString();
                    }
                    default:
                        return NavigationEnum.storage_scanStockunit_CenterPanel.toString();
                }


            }
            });
    }

    public String getDestinationTextField() {
        return destinationTextField;
    }

    public void setDestinationTextField(String destinationTextField) {
        this.destinationTextField = destinationTextField;
    }

    public boolean isFinishedButtonEnabled() {
        return finishedButtonEnabled;
    }

    public void resetBean() {
        JSFHelper.getInstance().resetBean(de.linogistix.mobile.processes.storage.scan_stockunit.gui.bean.TableBean.class);
        JSFHelper.getInstance().resetBean(de.linogistix.mobile.processes.storage.scan_stockunit.gui.bean.CenterBean.class);
    /*        JSFHelper.getInstance().resetBean(BeanEnum.storage_scanStockunit_TableBean.toString());
    JSFHelper.getInstance().resetBean(BeanEnum.storage_scanStockunit_CenterBean.toString()); 
    JSFHelper.getInstance().resetBean(BeanEnum.storage_scanDestination_CenterBean.toString());         */
    }
}

