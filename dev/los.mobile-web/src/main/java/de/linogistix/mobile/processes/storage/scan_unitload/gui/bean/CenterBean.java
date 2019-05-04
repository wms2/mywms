/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobile.processes.storage.scan_unitload.gui.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mywms.facade.FacadeException;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.inventory.facade.StorageFacade;
import de.linogistix.los.inventory.model.LOSStorageRequest;
import de.linogistix.los.inventory.query.LOSStorageRequestQueryRemote;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.service.QueryUnitLoadServiceRemote;
import de.linogistix.mobile.common.gui.bean.BasicBackingBean;
import de.linogistix.mobile.common.gui.bean.NotifyDescriptorExt;
import de.linogistix.mobile.common.gui.bean.NotifyDescriptorExtBean;
import de.linogistix.mobile.common.listener.ButtonListener;
import de.linogistix.mobile.common.system.JSFHelper;
import de.linogistix.mobile.processes.storage.StorageBackingBean;
import de.linogistix.mobile.processes.storage.scan_unitload.NavigationEnum;

/**
 * 
 * @author trautm
 */
public class CenterBean extends BasicBackingBean {


	private String ulTextField;

	
	LOSStorageRequestQueryRemote storageRequestQueryRemote;
	QueryUnitLoadServiceRemote ulQuery;
	StorageFacade pof;

	public CenterBean() {
		resetBean();
		ulQuery = getStateless(QueryUnitLoadServiceRemote.class);

	}

	protected StorageBackingBean getProcessBean(){
		StorageBackingBean b = JSFHelper.getInstance().getSessionBean(StorageBackingBean.class);
		if( b == null ) {
			b = JSFHelper.getInstance().getSessionBeanForce(StorageBackingBean.class);
		}
		return b;
	}
	
	public String forwardActionHandler() throws FacadeException {
		LOSStorageRequest r = pof.getStorageRequest(ulTextField, true);
		if (r == null){
			return null;
		}
		
		// needs eagerRead of query service
		r = storageRequestQueryRemote.queryById(r.getId());
		
		List<LOSStorageRequest> list = new ArrayList<LOSStorageRequest>();
		list.add(r);
		
		StorageBackingBean sb = getProcessBean();
		if( sb.getStorageRequests() != null ) {
			sb.resetBean();
		}
    	sb.setStorageRequests(list);
    	
    	resetBean();
    	return NavigationEnum.storage_scanDestination_CenterPanel.toString();
	}
	
	public String forwardActionPerformedListener() {
		ulTextField = ulTextField == null ? "" : ulTextField.trim();
		
		if( ulTextField == null  || ulTextField.trim().length() == 0 ) {
			return notifyMessage(resolve("Storage_ENTER_UNITLOAD"));
		}

		LOSUnitLoad ul = null;
		try {
			ul = ulQuery.getByLabelId(ulTextField);
		} catch (UnAuthorizedException e) {
			// useless
		}
		if( ul == null ) {
			return notifyMessage(resolve("StorageMsgUnitloadUnknown"));
		}
		
    	String val = null;
		try{
			val = forwardActionHandler();
			
			if (val == null){
				return notifyNotCreated();
			}
			
	    	return val;
	    	
		} catch (FacadeException ex){
			Logger.getLogger(CenterBean.class.getName()).log(Level.SEVERE, ex.getMessage());
			return notifyMessage(ex.getLocalizedMessage(getLocale()));
		}
    	
    }

	
	private String notifyMessage(String message) {
		resetBean();
		List<String> buttonTextList = new ArrayList<String>();
		buttonTextList.add(resolve("Ok", new Object[] {}));
		NotifyDescriptorExt n = new NotifyDescriptorExt(
				NotifyDescriptorExt.NotifyEnum.ERROR, message,
				buttonTextList);

		return n.setCallbackListener(new ButtonListener() {

			public String buttonClicked(final int buttonId,
					NotifyDescriptorExtBean notifyDescriptorBean) {
				return NavigationEnum.storage_scanUnitLoad_CenterPanel
						.toString();
			}
		});
	}
	
	private String notifyNotCreated() {
		resetBean();
		List<String> buttonTextList = new ArrayList<String>();
		buttonTextList.add(resolve("Ok", new Object[] {}));
		
		NotifyDescriptorExt n = new NotifyDescriptorExt(
				NotifyDescriptorExt.NotifyEnum.WARNING, resolve(
						"NoStorageRequestCreated", new Object[] {ulTextField}),
				buttonTextList);
		return n.setCallbackListener(new ButtonListener() {

			public String buttonClicked(final int buttonId,
					NotifyDescriptorExtBean notifyDescriptorBean) {
				return NavigationEnum.storage_scanUnitLoad_CenterPanel.toString();
			}
		});

	}
	
	public String cancelActionPerformedListener() {
		resetBean();
		return NavigationEnum.controller_CenterPanel.toString();
	}

	public boolean isForwardButtonEnabled(){
		return true;
	}
	
	public void resetBean() {
		this.ulTextField = "";
		storageRequestQueryRemote = getStateless(LOSStorageRequestQueryRemote.class);
		pof = getStateless(StorageFacade.class);
	}

	public void setUlTextField(String ulTextField) {
		this.ulTextField = ulTextField;
	}

	public String getUlTextField() {
		return ulTextField;
	}
	
}
