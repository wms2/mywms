/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.goodsreceipt.gui.component;

import de.linogistix.common.bobrowser.bo.editor.BOLockEntry;
import de.linogistix.los.entityservice.BusinessObjectLock;
import de.linogistix.los.entityservice.BusinessObjectLockState;
import de.linogistix.los.inventory.service.StockUnitLockState;
import de.linogistix.wmsprocesses.processes.goodsreceipt.gui.gui_builder.AbstractPositionWizardQMPanelUI;
import javax.swing.JComboBox;
import javax.swing.JTextArea;

/**
 *
 * @author trautm
 */
public class PositionWizardQMPanelUI extends AbstractPositionWizardQMPanelUI{

    public PositionWizardQMPanelUI(){
        getLockComboBox().removeAllItems();
        
        BusinessObjectLock noLock = new BusinessObjectLock() {

            public int getLock() {
                return BusinessObjectLockState.NOT_LOCKED.getLock();
            }

            public String getMessage() {
                return BusinessObjectLockState.NOT_LOCKED.getMessage();
            }

            public Class getBundleResolver() {
                return BusinessObjectLockState.NOT_LOCKED.getBundleResolver();
            }

            public String getMessageKey() {
                return BusinessObjectLockState.NOT_LOCKED.getMessageKey();
            }
        };
                
        BusinessObjectLock qmLock = new BusinessObjectLock() {

            public int getLock() {
                return StockUnitLockState.QUALITY_FAULT.getLock();
            }

            public String getMessage() {
                return StockUnitLockState.QUALITY_FAULT.getMessage();
            }

            public Class getBundleResolver() {
                return StockUnitLockState.QUALITY_FAULT.getBundleResolver();
            }

            public String getMessageKey() {
                return StockUnitLockState.QUALITY_FAULT.getMessageKey();
            }
        };
        
       getLockComboBox().addItem(new BOLockEntry(noLock));
       
       getLockComboBox().addItem(new BOLockEntry(qmLock));
        
       
    }

    void implReadSettings(Object settings) {
       //
    }

    void implStoreSettings(Object settings) {
        //
    }
    
    JTextArea getTextArea(){
        return jTextArea1;
    }
    
    JComboBox getLockComboBox(){
        return lockComboBox;
    }

    void initValues(final int lock, String info) {
        
        if (info != null) getTextArea().setText(info);

        boolean found = false;
        for (int i = 0; i< getLockComboBox().getItemCount();i++){
            BOLockEntry l = (BOLockEntry) getLockComboBox().getItemAt(i);
            if (l.getBOLock().getLock() == lock){
                getLockComboBox().setSelectedItem(l);
                found=true;
                break;
            }
        }

        if( !found ) {
            BusinessObjectLock newLock = new BusinessObjectLock() {
                public int getLock() {
                    return lock;
                }
                public String getMessage() {
                    return ""+lock;
                }
                public Class getBundleResolver() {
                    return StockUnitLockState.QUALITY_FAULT.getBundleResolver();
                }
                public String getMessageKey() {
                    return "lock_"+lock;
                }
            };
            BOLockEntry newLockX = new BOLockEntry(newLock);
            getLockComboBox().addItem(newLockX);
            getLockComboBox().setSelectedItem(newLockX);
        }
    
    }

}
