/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobile.processes.storage.scan_destination.gui.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import de.linogistix.mobile.common.gui.bean.BasicBackingBean;
import de.linogistix.mobile.common.gui.bean.NotifyDescriptorExt;
import de.linogistix.mobile.common.gui.bean.NotifyDescriptorExtBean;
import de.linogistix.mobile.common.listener.ButtonListener;
import de.linogistix.mobile.common.system.JSFHelper;
import de.linogistix.mobile.processes.storage.NavigationEnum;
import de.linogistix.mobile.processes.storage.StorageBackingBean;
import de.wms2.mywms.transport.TransportOrder;

public class CenterBean extends BasicBackingBean{

	boolean consolidateCheckBox = false; 
	
	int currentPosition = 1;

	boolean unitLoadTextFieldEnabled = true;

	String storageLocationTextField;
	String unitLoadTextField;
	String storageLocationLabelMessage;
	String unitLoadLabelMessage;
	
	public CenterBean() {	
		resetBean();
	}

	public boolean isConsolidateCheckBox() {
		return consolidateCheckBox;
	}

	public void setConsolidateCheckBox(boolean additionalCheckBox) {
		this.consolidateCheckBox = additionalCheckBox;
	}

	public String forwardActionPerformedListener() {
		currentPosition++;
		return "";
	}

	public String backwardActionPerformedListener() {
		currentPosition--;
		return "";
	}

	protected StorageBackingBean getProcessBean(){
		StorageBackingBean b = JSFHelper.getInstance().getSessionBean(StorageBackingBean.class);
		if( b == null ) {
			b = JSFHelper.getInstance().getSessionBeanForce(StorageBackingBean.class);
		}
		return b;
	}
	
	public String finishActionPerformedListener() {
		
		String s = getProcessBean().processFinish(unitLoadTextField, storageLocationTextField, consolidateCheckBox, false);
		resetBean();
		return s;
	}


	public String cancelActionPerformedListener() {
		List<String> buttonTextList = new ArrayList<String>();
		buttonTextList.add(resolve("Yes", new Object[] {}));
		buttonTextList.add(resolve("No", new Object[] {}));
		NotifyDescriptorExt n = new NotifyDescriptorExt(
				NotifyDescriptorExt.NotifyEnum.INFORMATION, resolve(
						"CancelStorage", new Object[] {}), buttonTextList);
		return n.setCallbackListener(new ButtonListener() {

			public String buttonClicked(final int buttonId,
					NotifyDescriptorExtBean notifyDescriptorBean) {
				switch (buttonId) {
				case 1: {
					getProcessBean().processCancel(unitLoadTextField);
					resetBean();
					return NavigationEnum.storage_scanUnitLoad_CenterPanel.toString();
				}
				default:
					return NavigationEnum.storage_scanDestination_CenterPanel.toString();
				}

			}
		});

	}
	
	public void resetBean() {
		storageLocationTextField = "";
		unitLoadTextField = "";
		consolidateCheckBox = false;
	}

	private TransportOrder getCurrentStorageRequest() {
		return getProcessBean().getStorageRequests()
				.get(currentPosition - 1);
	}

	public boolean isFinishButtonEnable() {
		return true;
	}

	public boolean isBackwardButtonEnable() {
		if (currentPosition == 1) {
			return false;
		}
		return true;
	}

	public boolean isForwardButtonEnable() {
		if (currentPosition == getTotalPosition()) {
			return false;
		}
		return true;
	}

	public String getPosition() {
		return currentPosition + "/" + getTotalPosition();
	}

	public String getUnitLoadTextField() {
		if (unitLoadTextField == null || unitLoadTextField.length() == 0){
			if (getTotalPosition() == 1){
				unitLoadTextField = getCurrentStorageRequest().getUnitLoad().getLabelId();
				unitLoadTextFieldEnabled = false;
			}
		}
		
		
		return unitLoadTextField;
	}

	public void setUnitLoadTextField(String unitLoadTextField) {
		this.unitLoadTextField = unitLoadTextField;
	}

	public String getStorageLocationTextField() {
		return storageLocationTextField;
	}

	public void setStorageLocationTextField(String storageLocationTextField) {
		this.storageLocationTextField = storageLocationTextField;
	}

	public String getUnitLoadLabelMessage() {
		TransportOrder storageRequest = getCurrentStorageRequest();
		if( storageRequest == null ) {
			return "";
		}
		return storageRequest.getUnitLoad().getLabelId();
	}

	public void setUnitLoadLabelMessage(String unitLoadLabelMessage) {
		this.unitLoadLabelMessage = unitLoadLabelMessage;
	}

	public String getStorageLocationLabelMessage() {
		TransportOrder storageRequest = getCurrentStorageRequest();
		if( storageRequest == null ) {
			return "";
		}
		return storageRequest.getDestinationLocation().getName();
	}

	public void setStorageLocationLabelMessage(
			String storageLocationLabelMessage) {
		this.storageLocationLabelMessage = storageLocationLabelMessage;
	}

	public void unitLoadTextFieldValidator(FacesContext context,
			UIComponent toValidate, Object value) {
		TransportOrder storageRequest = getCurrentStorageRequest();
		if( storageRequest == null ) {
			return;
		}
		if (value.equals("") == false) {
			if (value.equals(storageRequest.getUnitLoad().getLabelId()) == false) {
				message("UnitLoadWrong");
			}
		}
	}

	public boolean isUnitLoadTextFieldEnable() {
		getUnitLoadTextField();
		return isFinishButtonEnable() && unitLoadTextFieldEnabled;
	}

	public boolean isStorageLocationTextFieldEnable() {
		return isFinishButtonEnable();
	}

	public boolean isConsolidateCheckBoxEnable() {
		return isFinishButtonEnable();
	}

	public int getTotalPosition() {
		StorageBackingBean p = getProcessBean();
		return p.getTotalPosition();
	}
	
}
