/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobile.processes.storage;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.facade.StorageFacade;
import de.linogistix.los.inventory.model.LOSStorageRequest;
import de.linogistix.los.inventory.model.LOSStorageRequestState;
import de.linogistix.los.inventory.query.LOSStorageRequestQueryRemote;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.exception.LOSLocationExceptionKey;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote;
import de.linogistix.mobile.common.gui.bean.BasicDialogBean;
import de.linogistix.mobile.common.gui.bean.NotifyDescriptorExt;
import de.linogistix.mobile.common.gui.bean.NotifyDescriptorExtBean;
import de.linogistix.mobile.common.listener.ButtonListener;

public class StorageBackingBean extends BasicDialogBean{
    private static final Logger log = Logger.getLogger(StorageBackingBean.class);

	
    protected List<LOSStorageRequest> storageRequests;
    protected String specialNavigation = null;
 	
	
	LOSStorageRequestQueryRemote storageRequestQueryRemote;
	LOSStorageLocationQueryRemote queryLoc;
	StorageFacade pof;
	
	public StorageBackingBean() {
		resetBean();
	}
	
	public String getNavigationKey() {
		return NavigationEnum.storage_scanUnitLoad_CenterPanel.name();
	}
	
	public String getTitle() {
		return resolve("Storage");
	}

	protected String notifyFacadeException(FacadeException ex) {
		List<String> buttonTextList = new ArrayList<String>();
		buttonTextList.add(resolve("Ok", new Object[] {}));
		NotifyDescriptorExt n = new NotifyDescriptorExt(
				NotifyDescriptorExt.NotifyEnum.ERROR, ex.getLocalizedMessage(getLocale()),
				buttonTextList);

		return n.setCallbackListener(new ButtonListener() {

			public String buttonClicked(final int buttonId,
					NotifyDescriptorExtBean notifyDescriptorBean) {
				return NavigationEnum.storage_scanDestination_CenterPanel
						.toString();
			}
		});
	}

	protected String notifyFinish( boolean isDiffLocation, boolean isTransfer ) {
		resetBean();
		List<String> buttonTextList = new ArrayList<String>();
		buttonTextList.add(resolve("Ok", new Object[] {}));

		NotifyDescriptorExt n;
		if( isDiffLocation ) {
			if( isTransfer ) {
				n = new NotifyDescriptorExt(
						NotifyDescriptorExt.NotifyEnum.INFORMATION, resolve(
								"FinishUnitLoadTransferDestination", new Object[] {}),
						buttonTextList);
			}
			else {
				n = new NotifyDescriptorExt(
						NotifyDescriptorExt.NotifyEnum.INFORMATION, resolve(
								"FinishUnitLoadDiffDestination", new Object[] {}),
						buttonTextList);
			}
		}
		else {
			n = new NotifyDescriptorExt(
					NotifyDescriptorExt.NotifyEnum.INFORMATION, resolve(
							"FinishUnitLoadDestination", new Object[] {}),
					buttonTextList);
		}
		return n.setCallbackListener(new ButtonListener() {

			public String buttonClicked(final int buttonId,
					NotifyDescriptorExtBean notifyDescriptorBean) {
				return NavigationEnum.storage_scanUnitLoad_CenterPanel.toString();
			}
		});

	}

	protected String notifyDestinationWrong() {
		List<String> buttonTextList = new ArrayList<String>();
		buttonTextList.add(resolve("Ok", new Object[] {}));
		NotifyDescriptorExt n = new NotifyDescriptorExt(
				NotifyDescriptorExt.NotifyEnum.ERROR, resolve(
						"DestinationWrong", new Object[] {}), buttonTextList);
		return n.setCallbackListener(new ButtonListener() {

			public String buttonClicked(final int buttonId,
					NotifyDescriptorExtBean notifyDescriptorBean) {
				return NavigationEnum.storage_scanDestination_CenterPanel
						.toString();
			}
		});
	}

	protected String notifyDestinationWrongButAllowed(final String ul, final String sl, final boolean consolidateCheckBox) {
		List<String> buttonTextList = new ArrayList<String>();
		buttonTextList.add(resolve("Yes", new Object[] {}));
		buttonTextList.add(resolve("No", new Object[] {}));
		NotifyDescriptorExt n = new NotifyDescriptorExt(
				NotifyDescriptorExt.NotifyEnum.WARNING, resolve(
						"DestinationWrongButAllowConfirm", new Object[] {}),
				buttonTextList);
		return n.setCallbackListener(new ButtonListener() {

			public String buttonClicked(final int buttonId,
					NotifyDescriptorExtBean notifyDescriptorBean) {
				switch (buttonId) {
				case 1: {
					return processFinish(ul, sl,consolidateCheckBox, true);
				}
				default:
					return NavigationEnum.storage_scanDestination_CenterPanel
							.toString();
				}

			}
		});

	}

	protected String notifyDestinationIsUnitload(final String ul, final String sl) {
		List<String> buttonTextList = new ArrayList<String>();
		buttonTextList.add(resolve("Yes", new Object[] {}));
		buttonTextList.add(resolve("No", new Object[] {}));
		NotifyDescriptorExt n = new NotifyDescriptorExt(
				NotifyDescriptorExt.NotifyEnum.WARNING, resolve(
						"DestinationIsUnitload", new Object[] {}),
				buttonTextList);
		return n.setCallbackListener(new ButtonListener() {

			public String buttonClicked(final int buttonId,
					NotifyDescriptorExtBean notifyDescriptorBean) {
				switch (buttonId) {
				case 1: {
					return processFinish(ul, sl,true, true);
				}
				}
				return NavigationEnum.storage_scanDestination_CenterPanel
						.toString();
			}
		});
	}

	protected boolean isComplete() {
		
		for (LOSStorageRequest r : getStorageRequests()){
			switch(r.getRequestState()){
			case RAW: return false;
			default:continue;
			}
		}
		
		return true;
	}

//	private LOSStorageRequest updateStorageRequest(String unitLoadTextField)
//			throws FacadeException {
//
//		return pof.getStorageRequest(unitLoadTextField);
//		
//	}

	public String processFinish(String unitLoadTextField, 
			String storageLocationTextField, boolean additional, boolean force) {
        try {
                        
            pof.finishStorageRequest(unitLoadTextField, storageLocationTextField, additional, force);
            
            LOSStorageRequest req = null;
            
            for (LOSStorageRequest r : storageRequests){
            	if (r.getUnitLoad().getLabelId().equals(unitLoadTextField)){
            		req = r;
            		break;
            	}
            }
            
            
            
            if (req != null && storageRequests.contains(req)){
            	req = storageRequestQueryRemote.queryById(req.getId());
            	storageRequests.set(storageRequests.indexOf(req), req);
            } else{
            	return notifyFacadeException(new LOSLocationException(LOSLocationExceptionKey.WRONG_UNITLOAD, new String[]{unitLoadTextField}));
            }
            
            if( specialNavigation != null ) {
            	log.info("Send back to calling process: " + specialNavigation);
            	String rc = specialNavigation;
            	specialNavigation = null;
            	return rc;
            }
            else if (isComplete()) {
        		boolean isDiffLocation = false;
        		boolean isTransfer = false;
                LOSStorageLocation reqLoc = req.getDestination();
                if( req.getRequestState() == LOSStorageRequestState.PROCESSING || req.getRequestState() == LOSStorageRequestState.RAW ) {
                	isTransfer = true;
                }
                if( reqLoc != null ) {
                	reqLoc = queryLoc.queryById( reqLoc.getId() );
                	if( ! reqLoc.getName().equals(storageLocationTextField) && ! reqLoc.getScanCode().equals(storageLocationTextField)) {
                		isDiffLocation = true;
                	}
                }
                
            	return notifyFinish( isDiffLocation, isTransfer );
            }
            return NavigationEnum.storage_scanDestination_CenterPanel.toString();

        } catch (InventoryException ex) {
            log.info("Exception: "+ex.getMessage());
            switch (ex.getInventoryExceptionKey()) {
                case STORAGE_WRONG_LOCATION_BUT_ALLOWED:
                    return notifyDestinationWrongButAllowed(unitLoadTextField, storageLocationTextField, additional);

                case STORAGE_WRONG_LOCATION_NOT_ALLOWED:
                    return notifyDestinationWrong();

                case STORAGE_ADD_TO_EXISTING:
                    return notifyDestinationIsUnitload(unitLoadTextField, storageLocationTextField);

                default: {
                    return notifyFacadeException(ex);
                }
            }
        } catch (FacadeException ex) {
            log.info("Exception: "+ex.getMessage());
            return notifyFacadeException(ex);
        }
    }

	public void processCancel(String unitLoadLabel) {
		try {
			pof.cancelStorageRequest(unitLoadLabel);
	    } catch (FacadeException ex) {
	        log.error(ex.getMessage(), ex);
	        notifyFacadeException(ex);
	    }
	}
	
	public String cancelActionPerformedListener() {
		List<String> buttonTextList = new ArrayList<String>();
		buttonTextList.add(resolve("Yes", new Object[] {}));
		buttonTextList.add(resolve("Cancel", new Object[] {}));
		NotifyDescriptorExt n = new NotifyDescriptorExt(
				NotifyDescriptorExt.NotifyEnum.INFORMATION, resolve(
						"CancelStorage", new Object[] {}), buttonTextList);
		return n.setCallbackListener(new ButtonListener() {

			public String buttonClicked(final int buttonId,
					NotifyDescriptorExtBean notifyDescriptorBean) {
				switch (buttonId) {
				case 1: {
					return NavigationEnum.controller_CenterPanel.toString();
				}
				default:
					return NavigationEnum.storage_scanDestination_CenterPanel
							.toString();
				}

			}
		});

	}
	
	public void resetBean() {
		storageRequests = null;
		storageRequestQueryRemote = getStateless(LOSStorageRequestQueryRemote.class);
		pof = getStateless(StorageFacade.class);
		queryLoc = getStateless(LOSStorageLocationQueryRemote.class); 

		specialNavigation = null;
	}

	public int getTotalPosition() {
		return this.storageRequests!=null?storageRequests.size():0;
	}

	public void setStorageRequests(List<LOSStorageRequest> storageRequests) {
		this.storageRequests = storageRequests;
	}

	public List<LOSStorageRequest> getStorageRequests() {
		return storageRequests;
	}
	
	public String getProcessName(){
		return resolve("Storage");
	}

	public void setSpecialNavigation( String viewId ) {
		this.specialNavigation = viewId;
	}

	public String getSpecialNavigation() {
		return specialNavigation;
	}
	
	
}
