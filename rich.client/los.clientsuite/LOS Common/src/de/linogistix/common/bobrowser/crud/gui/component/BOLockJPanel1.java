/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.crud.gui.component;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.crud.gui.gui_builder.AbstractBOLockPanel;
import de.linogistix.los.entityservice.BusinessObjectLock;
import de.linogistix.los.entityservice.BusinessObjectLockState;

/**
 *
 * @author trautm
 */
public class BOLockJPanel1 extends AbstractBOLockPanel{

    public BOLockJPanel1(BO bo){
        super(bo);
    }
   
    @Override
    public void initLockComboBox() {
        lockComboBox.removeAllItems();
        LockComboBoxItem defaultL = null;
        for (BusinessObjectLock l : bo.getLockStates()){
            LockComboBoxItem item = new LockComboBoxItem(l);
            lockComboBox.addItem(item);
            if (l == BusinessObjectLockState.NOT_LOCKED){
                defaultL = item;
            }
        }
        
        if (defaultL != null) lockComboBox.setSelectedItem(defaultL);
    }
    
    
    public BusinessObjectLock getSelectedLock(){
        LockComboBoxItem item = (LockComboBoxItem) lockComboBox.getSelectedItem();
        
        return item.lock;
    }
    
    public String getLockCause(){
        return causeTextArea.getText();
    }
    
    public void clear(){    
        initLockComboBox();
        causeTextArea.setText("");
    }
    
    final class LockComboBoxItem{
        public BusinessObjectLock lock;
        
        LockComboBoxItem(BusinessObjectLock lock){
            this.lock = lock;
        }

        @Override
        public String toString() {
            return lock.getMessage();
        }  
    }
    
     boolean implIsValid() {
    return true;
  }

  void implReadSettings(Object settings) {
   BOLockWizard w = (BOLockWizard)settings;
  }

  void implStoreSettings(Object settings) {
   BOLockWizard w = (BOLockWizard)settings;

    w.setLock(getSelectedLock().getLock());
    w.setLockCause(getLockCause());
  }
   
    

}
