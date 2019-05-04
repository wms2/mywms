/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobile.processes.replenish;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;

import de.linogistix.los.model.State;
import de.linogistix.mobile.common.gui.bean.BasicDialogBean;
import de.linogistix.mobile.common.system.JSFHelper;
import de.linogistix.mobileserver.processes.replenish.ReplenishMobileFacade;
import de.linogistix.mobileserver.processes.replenish.ReplenishMobileOrder;



// TODO krane: Wiederaufsetzen
//TODO krane: Mengen Nachkommastellen
/**
 * @author krane
 *
 */
public class ReplenishMobileBean extends BasicDialogBean {
	Logger log = Logger.getLogger(ReplenishMobileBean.class);

	public static final String MODE_PROCESS = "MODE_PROCESS";
	public static final String MODE_REQUEST = "MODE_REQUEST";
	protected String currentMode;

	// ***********************************************************************
	// Switches. Configured with system properties
	// ***********************************************************************

	
	protected Long selectedOrderId;
	protected List<SelectItem> orderSelectList;
	
	protected ReplenishMobileFacade facade;
	protected ReplenishMobileOrder order;
	
	protected String inputCode = "";
	protected String searchCode = "";
	
	protected String inputLocationName = "";
	protected String inputAmount = "";
	
	
	private boolean showClient = true;
	
	public ReplenishMobileBean() {
		super();
		facade = super.getStateless(ReplenishMobileFacade.class);
		Client client = facade.getDefaultClient();
		showClient = (client == null);
	}

	@Override
	public String getNavigationKey() {
		return ReplenishNavigation.REPLENISH_MENU.name();
	}

	@Override
	public String getTitle() {
		return resolve("TitleReplenish");
	}

	public void init() {
		log.debug("init");
		
		inputCode = "";
		searchCode = "";
		orderSelectList = null;
		selectedOrderId = null;
		order = new ReplenishMobileOrder();
		
		inputLocationName = "";
		inputAmount = "";
	}
	

	// ***********************************************************************
	// ReplenishMenu.jsp
	// ***********************************************************************
	protected ReplenishMobileOrder getReservedOrder() throws FacadeException {
		return facade.getReservedOrder();
	}
	public String processStartRequest() {
		currentMode = MODE_REQUEST;
		return ReplenishNavigation.REPLENISH_REQ_LOCATION.name();
	}
	public String processStartProcess() {
		currentMode = MODE_PROCESS;
		
		try {
			order = getReservedOrder();
		}
		catch( FacadeException e ) {
			order = new ReplenishMobileOrder();
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
			return ReplenishNavigation.REPLENISH_ORDER_SELECT.name();
		}

		if( order == null ) {
			order = new ReplenishMobileOrder();
			return ReplenishNavigation.REPLENISH_ORDER_SELECT.name();
		}
		
		if( order.getState() == State.RESERVED ) {
			try {
				facade.startOrder(order);
			} catch (FacadeException e) {
				JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
				return ReplenishNavigation.REPLENISH_SOURCE_LOCATION.name();
			}
			JSFHelper.getInstance().message( resolve("MsgStartReservedOrder") );
		}
		else {
			JSFHelper.getInstance().message( resolve("MsgRestarted") );
		}
		return ReplenishNavigation.REPLENISH_SOURCE_LOCATION.name();
	}
	public String processStartCancel() {
		return ReplenishNavigation.REPLENISH_MAIN_MENU.name();
	}
	
	// ***********************************************************************
	// OrderSelect.jsp
	// ***********************************************************************
	protected List<SelectItem> getCalculatedOrders(String code) {
		return facade.getCalculatedOrders( code );
	}
	public String processOrderSelect(){
		String code = inputCode == null ? "" : inputCode.trim();
		inputCode ="";
		
		if( code.length() > 0 ) {
			selectedOrderId = null;
			orderSelectList = getCalculatedOrders( code );
			if( orderSelectList == null || orderSelectList .size() == 0 ) {
				loadSelectedOrder();
				JSFHelper.getInstance().message( resolve("MsgNoMatchingOrder") );
				return "";
			}
//			if( orderSelectList .size() > 1 ) {
//				JSFHelper.getInstance().message( resolve("MsgNotUnique") );
//				return "";
//			}
			try{
				selectedOrderId = (Long)orderSelectList.get(0).getValue();
			} catch( Throwable t ) {
		    	log.error("Exception: "+t.getClass().getSimpleName()+", "+t.getMessage(), t);
			}
			if( orderSelectList.size() > 1 ) {
				loadSelectedOrder();
				return "";
			}
			orderSelectList = null;
		}

		loadSelectedOrder();
		if( selectedOrderId == null ) {
			JSFHelper.getInstance().message( resolve("MsgSelectOrder") );
			orderSelectList = null;
			return "";
		}
		
		try {
			facade.startOrder(order);
		}
		catch( FacadeException e ) {
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
			return "";
		}
		
		return ReplenishNavigation.REPLENISH_SOURCE_LOCATION.name();
	}
	
	public String processOrderSelectCancel(){
		init();
		return ReplenishNavigation.REPLENISH_MAIN_MENU.name();
	}
	
	public List<SelectItem> getOrderList(){
		String logStr = "getOrderList ";

		if( orderSelectList == null ) {
		
			orderSelectList = getCalculatedOrders( null );
			if( orderSelectList == null ) {
				orderSelectList = new ArrayList<SelectItem>();
			}
			if( orderSelectList.size() > 0 ) {
				try{
					selectedOrderId = (Long)orderSelectList.get(0).getValue();
				} catch( Throwable t ) {
			    	log.error("Exception: "+t.getClass().getSimpleName()+", "+t.getMessage(), t);
				}
			}
			loadSelectedOrder();
		}
		
		log.info(logStr+"Found "+orderSelectList.size()+" orders");
		return orderSelectList;
	}

	public void setSelectedOrder(String sel){
		selectedOrderId = null;
		try{
			selectedOrderId = Long.valueOf(sel);
		} catch( Throwable t ) {}
		loadSelectedOrder();
	}
	
	public String getSelectedOrder(){
		if( selectedOrderId == null ) {
			return null;
		}
		return selectedOrderId.toString();
	}
	
    public void valueChanged(ValueChangeEvent vce) {
    	String sel = (String)vce.getNewValue();
    	setSelectedOrder(sel);
    	loadSelectedOrder();
    }

    protected void loadSelectedOrder() {
    	if( selectedOrderId != null ) {
    		order = facade.loadOrderById(selectedOrderId);
    	}
    	else {
    		order = new ReplenishMobileOrder();
    	}
    }
    
	
	
	// ***********************************************************************
	// SourceLocation.jsp
	// ***********************************************************************
	public String processSourceLocation(){
		String code = inputCode == null ? "" : inputCode.trim();
		inputCode = "";
		
		if( code.length() == 0 ) {
			JSFHelper.getInstance().message( resolve("MsgEnterSomething") );
			return "";
		}
		
		try {
			order = facade.checkSource(order, code);
		}
		catch( FacadeException e ) {
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
			return "";
		}
		
		return ReplenishNavigation.REPLENISH_SOURCE_CONFIRM.name();
	}
	
	public String processSourceLocationCancel(){
		inputCode = "";
		try {
			facade.resetOrder(order);
		}
		catch( FacadeException e ) {
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
		}
		init();

		if( currentMode == MODE_REQUEST ) {
			return ReplenishNavigation.REPLENISH_REQ_LOCATION.name();
		}
		return ReplenishNavigation.REPLENISH_START_PROCESS.name();
	}

	// ***********************************************************************
	// SourceConfirm.jsp
	// ***********************************************************************
	public String processSourceConfirm(){
		return ReplenishNavigation.REPLENISH_DESTINATION_LOCATION.name();
	}
	
	public String processSourceConfirmChange(){
		return ReplenishNavigation.REPLENISH_SOURCE_AMOUNT.name();
	}

	// ***********************************************************************
	// SourceAmount.jsp
	// ***********************************************************************
	public String processSourceAmount(){
		String code = inputCode == null ? "" : inputCode.trim();
		inputCode = "";

		if( code.length() == 0 ) {
			JSFHelper.getInstance().message( resolve("MsgEnterSomething") );
			return "";
		}
		
		BigDecimal amount = null;
		try {
			amount = new BigDecimal(code);
		}
		catch( Throwable t ) {
			JSFHelper.getInstance().message( resolve("MsgEnterValidAmount") );
			return "";
		}
		
		
		try {
			facade.checkAmountPicked(order, amount);
		}
		catch( FacadeException e ) {
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
			return "";
		}
		
		order.setAmountPicked(amount);
		
		return ReplenishNavigation.REPLENISH_DESTINATION_LOCATION.name();
	}
	
	// ***********************************************************************
	// DestinationLocation.jsp
	// ***********************************************************************
	public String processDestinationLocation() {
		String code = inputCode == null ? "" : inputCode.trim();
		inputCode = "";
		
		if( code.length() == 0 ) {
			JSFHelper.getInstance().message( resolve("MsgEnterSomething") );
			return "";
		}
		
		try {
			facade.checkDestination(order, code);
		}
		catch( FacadeException e ) {
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
			return "";
		}


		
		return ReplenishNavigation.REPLENISH_DESTINATION_CONFIRM.name();
	}
	
	public String processDestinationLocationCancel() {
		try {
			facade.resetOrder(order);
		}
		catch( FacadeException e ) {
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
			return "";
		}


		
		return ReplenishNavigation.REPLENISH_ORDER_CANCEL.name();
	}
	
	// ***********************************************************************
	// DestinationConfirm.jsp
	// ***********************************************************************
	public String processDestinationConfirm() {
		try {
			facade.confirmOrder(order);
		}
		catch( FacadeException e ) {
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
			return "";
		}
		return ReplenishNavigation.REPLENISH_ORDER_DONE.name();
	}
	
	public String processDestinationConfirmCancel(){
		try {
			facade.resetOrder(order);
		}
		catch( FacadeException e ) {
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
			return "";
		}

		return ReplenishNavigation.REPLENISH_ORDER_CANCEL.name();
	}


	// ***********************************************************************
	// OrderDone.jsp
	// ***********************************************************************
	public String processOrderDone(){
		init();
		
		order = null;
		try {
			order = facade.getReservedOrder();
		}
		catch( FacadeException e ) {
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
		}
		
		if( order == null ) {
			if( currentMode == MODE_REQUEST ) {
				return ReplenishNavigation.REPLENISH_REQ_LOCATION.name();
			}
			return ReplenishNavigation.REPLENISH_ORDER_SELECT.name();
		}
		
		if( order.getState() == State.RESERVED ) {
			try {
				facade.startOrder(order);
			} catch (FacadeException e) {
				JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
				return ReplenishNavigation.REPLENISH_SOURCE_LOCATION.name();
			}
			JSFHelper.getInstance().message( resolve("MsgStartReservedOrder") );
		}
		else {
			JSFHelper.getInstance().message( resolve("MsgRestarted") );
		}
		return ReplenishNavigation.REPLENISH_SOURCE_LOCATION.name();
	}


	
	
	
	
	// ***********************************************************************
	// RequestLocation.jsp
	// ***********************************************************************
	public String processRequestLocation() {

		if( inputLocationName == null ) 
			inputLocationName = "";
		inputLocationName = inputLocationName.trim();
		
		if( inputLocationName.length() == 0 ) {
			JSFHelper.getInstance().message(resolve("MsgEnterLocation"));
			return "";
		}
		
		try {
			order = facade.loadOrderByDestination(inputLocationName);
		}
		catch( FacadeException e ) {
			String msg = e.getLocalizedMessage(getLocale());
			if( msg == null || msg.length() == 0 ) {
				msg = resolve("MsgErrorReplenish");
			}
			JSFHelper.getInstance().message(msg);
			return "";
		}
		if( order.getSourceStockId()>0 ) {
			return ReplenishNavigation.REPLENISH_REQ_INFO.name();
		}
		inputLocationName = "";
		
		return ReplenishNavigation.REPLENISH_REQ_AMOUNT.name();
	}
	
	public String processRequestLocationCancel() {
		return ReplenishNavigation.REPLENISH_MAIN_MENU.name();
	}

	// ***********************************************************************
	// RequestAmount.jsp
	// ***********************************************************************
	public String processRequestAmount() {
		BigDecimal checkedAmount = null;
		
		if( inputAmount == null ) 
			inputAmount = "";
		inputAmount = inputAmount.trim();
		
		if( inputAmount.length() > 0 ) {
			try {
				checkedAmount = new BigDecimal(inputAmount);
			}
			catch( Exception e ) {
				JSFHelper.getInstance().message(resolve("MsgEnterValidAmount"));
				return "";
			}
		}
		if( order == null ) {
			order = new ReplenishMobileOrder();
		}
		
		if( checkedAmount != null ) {
			checkedAmount = checkedAmount.setScale(order.getItemScale());
		}
		
		try {
			order.setAmountRequested(checkedAmount);
			ReplenishMobileOrder orderNew = facade.requestReplenish(order);
			if( orderNew == null ) {
				JSFHelper.getInstance().message( resolve("MsgNoReplenishOrder") );
				return "";
			}
			order = orderNew;
		} catch (FacadeException e) {
			String msg = e.getLocalizedMessage(getLocale());
			if( msg == null || msg.length() == 0 ) {
				msg = resolve("MsgNoReplenishOrder");
			}
			JSFHelper.getInstance().message(msg);
			return "";
		}

		inputAmount = "";
		
		JSFHelper.getInstance().message(resolve("MsgSuccessReplenish"));
		return ReplenishNavigation.REPLENISH_REQ_INFO.name();
	}
	public String processRequestAmountCancel() {
		init();
		return ReplenishNavigation.REPLENISH_REQ_LOCATION.name();
	}
	
	// ***********************************************************************
	// RequestInfo.jsp
	// ***********************************************************************
	public String processRequestInfoYes() {
		
		try {
			facade.startOrder(order);
		}
		catch( FacadeException e ) {
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
			return "";
		}
		
		return ReplenishNavigation.REPLENISH_SOURCE_LOCATION.name();
	}
	
	public String processRequestInfoNo() {
		init();
		return ReplenishNavigation.REPLENISH_REQ_LOCATION.name();
	}
	
	
	
	// ***********************************************************************
	// Attributes
	// ***********************************************************************
	public String getInputCode() {
		return inputCode;
	}
	public void setInputCode(String inputCode) {
		this.inputCode = inputCode;
	}
	public String getSearchCode() {
		return searchCode;
	}
	public void setSearchCode(String code) {
		this.searchCode = code;
	}
    public String getInputLocationName() {
		return inputLocationName;
	}
	public void setInputLocationName(String inputLocationName) {
		this.inputLocationName = inputLocationName;
	}
	public String getInputAmount() {
		return inputAmount;
	}
	public void setInputAmount(String inputAmount) {
		this.inputAmount = inputAmount;
	}
	
	// ***********************************************************************
	public boolean isShowClient() {
		return showClient;
	}

	// ***********************************************************************
	public String getClientNumber() {
		return order == null ? "" : order.getClientNumber();
	}
	public String getItemNumber() {
		return order == null ? "" : order.getItemNumber();
	}	
	public String getItemName() {
		return order == null ? "" : order.getItemName();
	}	
	public String getItemUnit() {
		return order == null ? "" : order.getItemUnit();
	}	
	public String getSourceName() {
		return order == null ? "" : order.getSourceLocationName();
	}	
	public String getSourceUnitLoad() {
		return order == null ? "" : order.getSourceUnitLoadLabel();
	}	
	public String getDestinationName() {
		return order == null ? "" : order.getDestinationLocationName();
	}	
	public String getAmountSource() {
		return order == null ? "" : order.getAmountSource() == null ? "" : order.getAmountSource().toString();
	}
	public String getAmountRequested() {
		return order == null ? "" : order.getAmountRequested() == null ? "--" : order.getAmountRequested().toString();
	}	
	public String getAmountPicked() {
		if( order == null ) {
			return "";
		}
		if( order.getAmountPicked() != null ) {
			return order.getAmountPicked().toString();
		}
		if( order.getAmountSource() != null ) {
			return order.getAmountSource().toString();
		}
		return "--";
	}	
	public String getAmountDestination() {
		return order == null ? "" : order.getAmountDestination() == null ? "--" : order.getAmountDestination().toString();
	}
	public String getAmountDestinationAvailable() {
		if( order == null || order.getAmountDestination()==null || order.getAmountDestinationMax()==null ) {
			return "--";
		}
		BigDecimal amountAvailable = order.getAmountDestinationMax().subtract(order.getAmountDestination());
		if( BigDecimal.ZERO.compareTo(amountAvailable)>0 ) {
			return "0";
		}
		return amountAvailable.toString();
	}

	
	
	// ***********************************************************************
	@Override
	protected ResourceBundle getResourceBundle() {
		ResourceBundle bundle;
		Locale loc;
		loc = getUIViewRoot().getLocale();
		bundle = ResourceBundle.getBundle("de.linogistix.mobile.processes.replenish.ReplenishBundle", loc);
		return bundle;
	}




}
