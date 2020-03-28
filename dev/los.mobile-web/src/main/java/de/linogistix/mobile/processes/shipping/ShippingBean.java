/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 * www.linogistix.com
 * 
 * Project: myWMS-LOS
*/
package de.linogistix.mobile.processes.shipping;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.facade.LOSGoodsOutFacade;
import de.linogistix.los.inventory.facade.LOSGoodsOutTO;
import de.linogistix.los.inventory.query.dto.LOSGoodsOutRequestTO;
import de.linogistix.los.util.entityservice.LOSSystemPropertyServiceRemote;
import de.linogistix.mobile.common.gui.bean.BasicDialogBean;
import de.linogistix.mobile.common.system.JSFHelper;
import de.linogistix.mobileserver.util.MobileProperties;

/**
 *
 * @author krane
 */
public class ShippingBean extends BasicDialogBean {
	private static final Logger log = Logger.getLogger(ShippingBean.class);

	protected boolean scanDestination = false;

	protected LOSGoodsOutFacade goFacade;
	protected LOSGoodsOutTO currentOrderTO = null;
	protected String inputUnitLoadLabel = "";
	protected String inputDestination = "";
	protected List<SelectItem> orderList = null; 
	protected LOSSystemPropertyServiceRemote propertyService;

	public ShippingBean() {
		super();
		goFacade = super.getStateless(LOSGoodsOutFacade.class);
		propertyService = super.getStateless(LOSSystemPropertyServiceRemote.class);

		scanDestination = propertyService.getBooleanDefault(getWorkstationName(), MobileProperties.KEY_SHIPPING_SCAN_DESTINATION, scanDestination);
		log.info(MobileProperties.KEY_SHIPPING_SCAN_DESTINATION+"="+scanDestination);

	}
	
	public String getNavigationKey() {
		return ShippingNavigation.SHIPPING_SELECT_ORDER.name();
	}
	
	public String getTitle() {
		return resolve("Shipping");
	}

	/** 
	 * reset all variables to default values
	 */
	protected void init() {
		inputUnitLoadLabel = "";
		inputDestination = "";
		currentOrderTO = null;
		orderList = null;
	}
	
	

	public String processSelectOrder() {
		orderList = null;
		if( currentOrderTO == null ) {
			JSFHelper.getInstance().message(resolve("MsgOrderNotFound"));
			return "";
		}
		
		try {
			goFacade.start(currentOrderTO.getOrderId());
		} 
		catch (FacadeException e) {
			JSFHelper.getInstance().message(e.getLocalizedMessage(getLocale()));
			return "";
		}
		
		return ShippingNavigation.SHIPPING_SCAN_UNITLOAD.toString();
	}
	

	public String processCancelSelectOrder() {
		init();
		return ShippingNavigation.SHIPPING_BACK_TO_MENU.toString();
	}

	
	public String processShowInfo() {
		return ShippingNavigation.SHIPPING_SHOW_INFO.toString();
	}


	public String processEnterUnitLoad() {
		if( currentOrderTO == null ) {
			JSFHelper.getInstance().message(resolve("MsgOrderNotLoaded"));
			return ShippingNavigation.SHIPPING_SELECT_ORDER.toString();
		}
		
		if( inputUnitLoadLabel == null ) {
			JSFHelper.getInstance().message(resolve("MsgEnterUnitLoad"));
			return ShippingNavigation.SHIPPING_SCAN_UNITLOAD.toString();
		}
		inputUnitLoadLabel = inputUnitLoadLabel.trim();
		if( inputUnitLoadLabel.length() == 0 ) {
			JSFHelper.getInstance().message(resolve("MsgEnterUnitLoad"));
			return ShippingNavigation.SHIPPING_SCAN_UNITLOAD.toString();
		}
		
		if (scanDestination) {
			return ShippingNavigation.SHIPPING_SCAN_DESTINATION.toString();
		}

		try {
			goFacade.finishPosition(inputUnitLoadLabel, currentOrderTO.getOrderId(), null);
		} catch (FacadeException e) {
			JSFHelper.getInstance().message(e.getLocalizedMessage(getLocale()));
			return "";
		}
		
		inputUnitLoadLabel = "";
		
		try {
			currentOrderTO = goFacade.getOrderInfo(currentOrderTO.getOrderId());
			if( currentOrderTO.isFinished() ) {
				goFacade.finish(currentOrderTO.getOrderId());
				return ShippingNavigation.SHIPPING_SHOW_SUMMARY.toString();
			}
		} catch (FacadeException e) {
			JSFHelper.getInstance().message(e.getLocalizedMessage(getLocale()));
			return "";
		}
		
		return ShippingNavigation.SHIPPING_SCAN_UNITLOAD.toString();
	}
	
	public String processEnterDestination() {
		if (currentOrderTO == null) {
			JSFHelper.getInstance().message(resolve("MsgOrderNotLoaded"));
			return ShippingNavigation.SHIPPING_SELECT_ORDER.toString();
		}

		if (inputDestination == null) {
			JSFHelper.getInstance().message(resolve("MsgEnterDestination"));
			return ShippingNavigation.SHIPPING_SCAN_DESTINATION.toString();
		}
		inputDestination = inputDestination.trim();
		if (inputDestination.length() == 0) {
			JSFHelper.getInstance().message(resolve("MsgEnterDestination"));
			return ShippingNavigation.SHIPPING_SCAN_DESTINATION.toString();
		}

		try {
			goFacade.finishPosition(inputUnitLoadLabel, currentOrderTO.getOrderId(), inputDestination);
		} catch (FacadeException e) {
			JSFHelper.getInstance().message(e.getLocalizedMessage(getLocale()));
			return "";
		}

		inputUnitLoadLabel = "";

		try {
			currentOrderTO = goFacade.getOrderInfo(currentOrderTO.getOrderId());
			if (currentOrderTO.isFinished()) {
				goFacade.finish(currentOrderTO.getOrderId());
				return ShippingNavigation.SHIPPING_SHOW_SUMMARY.toString();
			}
		} catch (FacadeException e) {
			JSFHelper.getInstance().message(e.getLocalizedMessage(getLocale()));
			return "";
		}

		return ShippingNavigation.SHIPPING_SCAN_UNITLOAD.toString();
	}

	public String processCancelUnitLoad() {
		if( currentOrderTO == null ) {
			JSFHelper.getInstance().message(resolve("MsgOrderNotLoaded"));
			return ShippingNavigation.SHIPPING_SELECT_ORDER.toString();
		}
		
		try {
			goFacade.cancel(currentOrderTO.getOrderId());
			return ShippingNavigation.SHIPPING_SHOW_SUMMARY.toString();
		} catch (FacadeException e) {
			JSFHelper.getInstance().message(e.getLocalizedMessage(getLocale()));
			return ShippingNavigation.SHIPPING_SELECT_ORDER.toString();
		}

	}
	
	public String processShowUnitLoad() {
		return ShippingNavigation.SHIPPING_SCAN_UNITLOAD.toString();
	}
	
	
    public List<SelectItem> getOrderList() {
    	
		if( orderList == null ) {
			orderList = new ArrayList<SelectItem>();

			List<LOSGoodsOutRequestTO> outRequestList = goFacade.getRaw();
			if (outRequestList != null && (outRequestList.isEmpty() == false)) {
				for (LOSGoodsOutRequestTO order : outRequestList) {
					String displayname = order.getNumber();
					String customerOrderNumber = order.getCustomerOrderNumber();
					if( !StringUtils.isBlank(customerOrderNumber) ) {
						displayname = customerOrderNumber + " ("+order.getNumber()+")";
					}
					orderList.add(new SelectItem(order.getNumber(), displayname));
				}
			}
		}

    	
		return orderList;
    }


    public void selectedOrderChanged(ValueChangeEvent vce) {
		currentOrderTO = null;
    	try {
        	String orderNumber = null;
    		orderNumber = (String)vce.getNewValue();
    		log.info("Selected order: " + orderNumber);
    		currentOrderTO = goFacade.load(orderNumber);
    	}
    	catch( Exception e) {
    		log.error(e.getMessage(), e);
    	}
    }
    
	public String processSummaryFinish() {
		init();
		return ShippingNavigation.SHIPPING_SELECT_ORDER.toString();
	}
	
	public String processInfoFinish() {
		return ShippingNavigation.SHIPPING_SCAN_UNITLOAD.toString();
	}
	
	public String getOrderNumber() {
		return currentOrderTO == null ? "" : currentOrderTO.getOrderNumber();
	}
	
	public void setOrderNumber(String orderNumber) {
	}
	
	public String getComment() {
		return currentOrderTO == null ? "" : currentOrderTO.getComment();
	}

	public String getNextUnitLoadLabel() {
		return currentOrderTO == null ? "" : currentOrderTO.getNextUnitLoadLabelId();
	}
	
	public String getNextLocation() {
		return currentOrderTO == null ? "" : currentOrderTO.getNextLocationName();
	}
	
	public String getNumPosDone() {
		return currentOrderTO == null ? "" : Long.valueOf(currentOrderTO.getNumPosDone()).toString();
	}

	public String getNumPosOpen() {
		return currentOrderTO == null ? "" : Long.valueOf(currentOrderTO.getNumPosOpen()).toString();
	}
	
	public String getNumPos() {
		if( currentOrderTO == null ) {
			return "";
		}
		long numPos = currentOrderTO.getNumPosDone() + currentOrderTO.getNumPosOpen(); 
		return Long.valueOf(numPos).toString();
	}

	public String getInputUnitLoadLabel() {
		return inputUnitLoadLabel;
	}

	public void setInputUnitLoadLabel(String inputUnitLoadLabel) {
		this.inputUnitLoadLabel = inputUnitLoadLabel;
	}

	public String getInputDestination() {
		return inputDestination;
	}

	public void setInputDestination(String inputDestination) {
		this.inputDestination = inputDestination;
	}

	public String getDestination() {
		if( currentOrderTO == null ) {
			return "";
		}
		return currentOrderTO.getDestination();
	}

	protected ResourceBundle getResourceBundle() {
		ResourceBundle bundle;
		Locale loc;
		loc = getUIViewRoot().getLocale();
		bundle = ResourceBundle.getBundle("de.linogistix.mobile.processes.shipping.ShippingBundle", loc);
		return bundle;
	}

}

