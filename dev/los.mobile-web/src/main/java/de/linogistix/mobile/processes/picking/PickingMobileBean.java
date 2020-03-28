/*
 * Copyright (c) 2011-2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobile.processes.picking;

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
import de.linogistix.los.util.StringTools;
import de.linogistix.mobile.common.gui.bean.BasicDialogBean;
import de.linogistix.mobile.common.system.JSFHelper;
import de.linogistix.mobileserver.processes.picking.PickingMobileOrder;
import de.linogistix.mobileserver.processes.picking.PickingMobilePos;

/**
 * @author krane
 *
 */
public class PickingMobileBean extends BasicDialogBean {
	Logger log = Logger.getLogger(PickingMobileBean.class);


	// ***********************************************************************
	// Switches. Configured with system properties
	// ***********************************************************************
	
	protected Long selectedOrderId;
	protected List<SelectItem> orderSelectList;
	
	protected PickingMobileData data;
	protected List<String> serialList = null;
	
	protected String inputCode = "";
	protected String inputPrinter = "";
	protected BigDecimal amountTaken;
	protected BigDecimal amountRemaining;
	protected boolean locationCounted = false;
	protected String currentPrinter = null;
	protected Long currentId = null;

	protected boolean showClient = true;

	
	public PickingMobileBean() {
		super();
		
		data = new PickingMobileData();
		
		Client client = data.getDefaultClient();
		showClient = (client == null);
	}

	@Override
	public String getNavigationKey() {
		return getNavigationKey(PickingMobileNavigation.PICK_START);
	}

	@Override
	public String getTitle() {
		return resolve("TitlePickSingle");
	}

	String selClient;
	String selCustomerOrderNumber;
	String selCustomerOrderExternalNumber;
	String selNumPos;
	String selDate;
	String selStrategy;
	
	public void init() {
		log.debug("init");
		amountTaken = null;
		amountRemaining = null;
		locationCounted = false;
		inputCode = "";
		currentPrinter = null;
		orderSelectList = null;
		currentId = null;

		selClient = "";
		selCustomerOrderNumber = "";
		selCustomerOrderExternalNumber = "";
		selNumPos = "";
		selDate = "";
		selStrategy  ="";
		
		data.reset();
	}
	
	@Override
	public void init(String[] args) {
		super.init(args);
		init();
	}

	
	protected void initPos() {
		log.debug("Initialize position");
		inputCode = null;
		amountTaken = null;
		amountRemaining = null;
		locationCounted = false;
		currentId = null;
	}

	// ***********************************************************************
	// StartPicking.jsp
	// ***********************************************************************
	public String processStartPicking() {
    	log.debug("processStartPicking");
		init();
		try {
			data.resume(true);
		} catch (FacadeException e) {
			JSFHelper.getInstance().message( e.getLocalizedMessage() );
			return "";
		}

		if( data.getNumOrders()>0 ) {
			JSFHelper.getInstance().message( resolve("MsgRestore") );
			return getNavigationKey(PickingMobileNavigation.PICK_ORDER_SUMMARY);
		}
		return getNavigationKey(PickingMobileNavigation.PICK_ORDER_SELECT);
	}
	
	
	
	// ***********************************************************************
	// OrderSelect.jsp
	// ***********************************************************************
	protected List<SelectItem> getCalculatedPickingOrders(String code) {
		return data.getCalculatedPickingOrders( code, true, true );
	}
	public String processOrderSelect(){
		String code = inputCode == null ? "" : inputCode.trim();
		inputCode = "";
		
		if( code.length() > 0 ) {
			orderSelectList = getCalculatedPickingOrders( code );
			if( orderSelectList == null || orderSelectList .size() == 0 ) {
				orderSelectList = new ArrayList<SelectItem>();
				JSFHelper.getInstance().message( resolve("MsgNothingFound") );
				selectedOrderId=null;
				loadSelectedOrder();
				return "";
			}
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

		if( selectedOrderId == null ) {
			JSFHelper.getInstance().message( resolve("MsgSelectEmpty") );
			orderSelectList = null;
			return "";
		}
		
		try {
			data.loadOrder(selectedOrderId);
			orderSelectList = null;
			if( isShowOrderInfo() ) {
				return getNavigationKey(PickingMobileNavigation.PICK_ORDER_INFO);
			}
			else {
				data.startPicking();
				if( !data.setFirstPick() ) {
					JSFHelper.getInstance().message( resolve("MsgNoPos") );
					return "";
				}
				return getNavigationKey(PickingMobileNavigation.PICK_PICKFROM);
			}
		} catch (FacadeException e) {
			orderSelectList = null;
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
			return "";
		}

		
	}
	
	public String processOrderSelectCancel() {
		try {
			data.removeAllOrders();
		} catch (FacadeException e) {
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
			return "";
		}
			
		init();
		return getNavigationKey(PickingMobileNavigation.PICK_MENU);
	}

	public List<SelectItem> getOrderList(){
		String logStr = "getOrderList ";

		if( orderSelectList == null ) {
			selectedOrderId = null;

			orderSelectList = getCalculatedPickingOrders( null );
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
	}
	
	public String getSelectedOrder(){
		if( selectedOrderId == null ) {
			return null;
		}
		
		return selectedOrderId.toString();
	}
	
    public void orderSelectionChanged(ValueChangeEvent vce) {
    	String sel = (String)vce.getNewValue();
    	setSelectedOrder(sel);
    	loadSelectedOrder();
    }

    protected void loadSelectedOrder() {
    	if( selectedOrderId != null ) {
	    	PickingMobileOrder order = data.readOrder(selectedOrderId);
	    	selClient = order.clientNumber;
	    	selCustomerOrderNumber = order.customerOrderNumber;
	    	selCustomerOrderExternalNumber = order.customerOrderExternalNumber;
	    	selNumPos = String.valueOf(order.numPos);
	    	selDate = order.created;
	    	selStrategy = order.strategy;
    	}
    	else {
	    	selClient = "";
	    	selCustomerOrderNumber = "";
	    	selCustomerOrderExternalNumber = "";
	    	selNumPos = "";
	    	selDate = "";
	    	selStrategy = "";
    	}
    }
    
	// ***********************************************************************
	// PickToPrint.jsp
	// ***********************************************************************
	public String processPickToPrintSelect(){
		String logStr = "processPickToPrintSelect ";
		String code = inputPrinter == null ? "" : inputPrinter.trim();
		inputPrinter = "";

		log.debug(logStr+getTrace()+", input="+code);
		
		currentPrinter = (code.length()==0 ? null : code);

		try {
			data.printLabel( currentPrinter );
		} catch (FacadeException e) {
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
			return "";
		}

		return getNavigationKey(PickingMobileNavigation.PICK_PICKTO_TARGET);
	}
	
	public String processPickToPrintSkip(){
		String logStr = " processPickToPrintSkip";
		log.debug(logStr+getTrace());

		inputPrinter = "";
		currentPrinter = null;
		return getNavigationKey(PickingMobileNavigation.PICK_PICKTO_TARGET);
	}

	// ***********************************************************************
	// OrderSummary.jsp
	// ***********************************************************************
	public String processOrderSummary(){
		return processOrderInfo();
	}
	
	public String processOrderSummaryCancel(){
		return processOrderInfoCancel();
	}
	
	// ***********************************************************************
	// OrderInfo.jsp
	// ***********************************************************************
	public String processOrderInfo(){
		String logStr="processOrderInfo ";
		log.debug(logStr+getTrace());
		
		String code = inputCode == null ? "" : inputCode.trim();

		try {
			if (!StringTools.isEmpty(code)) {
				data.registerPickToLabel(code);
			}
			inputCode = "";

			data.startPicking();
			
			if( data.setFirstPick() ) {
				return getNavigationKey(PickingMobileNavigation.PICK_PICKFROM);
			}
		} catch (FacadeException e) {
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
			return "";
		}
		
		log.error(logStr+"No pick found to process");
		JSFHelper.getInstance().message( resolve("MsgNoPos") );
		return processOrderInfoCancel();
	}
	
	public String processOrderInfoCancel(){
		String logStr = "processOrderInfoCancel ";
		log.debug(logStr+getTrace());

		inputCode = "";

		try {
			data.startPutAway();
			if( data.getCurrentPickTo() != null ) {
				return getNavigationKey(PickingMobileNavigation.PICK_PICKTO_TARGET);
			}
			data.removeAllOrders();
		} catch (FacadeException e) {
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
			return "";
		}
		
		init();
		
		return getNavigationKey(PickingMobileNavigation.PICK_ORDER_SELECT);
	}
	
	// ***********************************************************************
	// PickFrom.jsp
	// ***********************************************************************
	public String processPickFrom(){
		String logStr = "processPickFrom ";
		String code = inputCode == null ? "" : inputCode.trim();
		inputCode = "";
		
		log.debug(logStr+getTrace()+", input="+code);

		if( code.length() == 0 ) {
			JSFHelper.getInstance().message( resolve("MsgEnterSomething") );
			return "";
		}
		
		initPos();

		try {
			data.checkPickSource(code);
		} catch (FacadeException e) {
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
			return "";
		}
		
		return getNavigationKey(PickingMobileNavigation.PICK_PICK);
	}
	
	public String processPickFromFunction(){
		return getNavigationKey(PickingMobileNavigation.PICK_PICKFROM_FUNCTION);
	}

	// ***********************************************************************
	// Pick.jsp
	// ***********************************************************************
	public String processPick(){
		String logStr = "processPick ";
		log.debug(logStr+getTrace());

		
		amountTaken = null;
		amountRemaining = null;
		locationCounted = false;
		
		try {
			// 06.08.2013, krane, Check correct pick. With browser back key, it is possible to select the wrong pick
			if( currentId == null || (data.getCurrentPick()!=null && !currentId.equals(data.getCurrentPick().id)) ) {
				log.warn(logStr+"Got wrong pick-id. Abort");
				JSFHelper.getInstance().message( resolve("MsgWrongPick") );
				data.setCurrentPick();
				return getNavigationKey(PickingMobileNavigation.PICK_PICKFROM);
			}

			if(isCompletePick()) {
				return getNavigationKey(PickingMobileNavigation.PICK_COMPLETE_TARGET);
			}
			
			if( getNumSerial() > (serialList == null ? 0 : serialList.size()) ) {
				return getNavigationKey(PickingMobileNavigation.PICK_PICK_SERIAL);
			}
			
			if( !data.isCompletePick() && data.isLocationEmpty() ) {
				return getNavigationKey(PickingMobileNavigation.PICK_PICK_EMPTY);
			}

			return confirmSelectSerial();

		} catch (FacadeException e) {
			JSFHelper.getInstance().message( e.getLocalizedMessage() );
			return "";
		}
	}
	
	public String processPickFunction(){
		String logStr = "processPickFunction ";
		log.debug(logStr+getTrace());

		amountTaken = null;
		
		// 06.08.2013, krane, Check correct pick. With browser back key, it is possible to select the wrong pick
		if( currentId == null || (data.getCurrentPick()!=null && !currentId.equals(data.getCurrentPick().id)) ) {
			log.warn(logStr+"Got wrong pick-id. Abort");
			JSFHelper.getInstance().message( resolve("MsgWrongPick") );
			return getNavigationKey(PickingMobileNavigation.PICK_PICKFROM);
		}

		return getNavigationKey(PickingMobileNavigation.PICK_PICK_FUNCTION);
	}
	
	// ***********************************************************************
	// PickSerial.jsp
	// ***********************************************************************
	public String processSerial(){
		String logStr = "processSerial ";
		String code = inputCode == null ? "" : inputCode.trim();
		inputCode = "";
		
		log.debug(logStr+getTrace()+", input="+code);

		if( code.length() == 0 ) {
			JSFHelper.getInstance().message( resolve("MsgEnterSomething") );
			return "";
		}
		
		try {
			data.checkSerial(serialList, code);
		}
		catch( FacadeException e ) {
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
			return "";
		}
		if( serialList == null ) {
			serialList = new ArrayList<String>();
		}
		serialList.add(code);

		try {
			return confirmSelectSerial();
		} catch (FacadeException e) {
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
			return "";
		}
	}
	
	public String processSerialCancel(){
		String logStr = "processSerialCancel ";
		log.debug(logStr+getTrace());
		amountTaken = null;
		amountRemaining = null;
		locationCounted = false;
		serialList = null;
		return getNavigationKey(PickingMobileNavigation.PICK_PICK);
	}
	
	public String confirmSelectSerial() throws FacadeException {
		String logStr = "confirmSelectSerial ";
		log.info(logStr+getTrace()+", SERIAL: required="+getNumSerial()+"selected="+(serialList==null?0:serialList.size()));

		if( (serialList==null?0:serialList.size()) < getNumSerial() ) {
			return getNavigationKey(PickingMobileNavigation.PICK_PICK_SERIAL);
		}

		data.confirmPick( amountTaken, amountRemaining, serialList, locationCounted );
		
		amountTaken = null;
		amountRemaining = null;
		locationCounted = false;
		serialList = null;
		
		if( data.setNextPick() ) {
			return getNavigationKey(PickingMobileNavigation.PICK_PICKFROM);
		}
		return getNavigationKey(PickingMobileNavigation.PICK_PICK_DONE);
	}
	
	// ***********************************************************************
	// PickFunction.jsp
	// ***********************************************************************
	public String processPickFunctionNext(){
		String logStr = "processPickFunctionNext ";
		log.debug(logStr+getTrace());
		try {
			if( data.setNextPick() ) {
				return getNavigationKey(PickingMobileNavigation.PICK_PICKFROM);
			}
		} catch (FacadeException e) {
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
			return "";
		}
		return getNavigationKey(PickingMobileNavigation.PICK_PICK_DONE);
	}
	
	public String processPickFunctionCancel(){
		String logStr = "processPickFunctionCancel ";
		log.debug(logStr+getTrace());
		try {
			data.startPutAway();
			if( data.getCurrentPickTo()==null ) {
				data.removeAllOrders();
				init();
				return getNavigationKey(PickingMobileNavigation.PICK_ORDER_DONE);
			}
		} catch (FacadeException e) {
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
			return "";
		}
		
		return getNavigationKey(PickingMobileNavigation.PICK_PICK_DONE);
	}

	public String processPickFunctionPrev(){
		String logStr = "processPickFunctionPrev ";
		log.debug(logStr+getTrace());
		try {
			if( data.setPrevPick() ) {
				return getNavigationKey(PickingMobileNavigation.PICK_PICKFROM);
			}
		} catch (FacadeException e) {
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
			return "";
		}
		return getNavigationKey(PickingMobileNavigation.PICK_PICK_DONE);
	}
	public String processPickFunctionFinish(){
		return getNavigationKey(PickingMobileNavigation.PICK_PICKTO_COMPLETED);
	}
	public String processPickFunctionMissing(){
		return getNavigationKey(PickingMobileNavigation.PICK_PICK_MISSING);
	}


	// ***********************************************************************
	// PickEmpty.jsp
	// ***********************************************************************
	public String processPickEmptyYes(){
		String logStr = "processPickEmptyYes ";
		log.debug(logStr+getTrace());
		try {
			// 23.08.2013, krane, Check correct pick. With browser back key, it is possible to select the wrong pick
			if( currentId == null || (data.getCurrentPick()!=null && !currentId.equals(data.getCurrentPick().id)) ) {
				log.warn(logStr+"Got wrong pick-id. Abort");
				JSFHelper.getInstance().message( resolve("MsgWrongPick") );
				data.setCurrentPick();
				return getNavigationKey(PickingMobileNavigation.PICK_PICKFROM);
			}
			
			amountRemaining = BigDecimal.ZERO;
			locationCounted = true;
			return confirmSelectSerial();
		} catch (FacadeException e) {
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
			return "";
		}
	}
	
	public String processPickEmptyNo(){
		String logStr = "processPickEmptyNo ";
		log.debug(logStr+getTrace());
		amountRemaining = null;
		locationCounted = false;
		
		// Only if the location is empty, a partial pick is allowed  
		if( amountTaken != null ) {
			amountTaken = null;
			JSFHelper.getInstance().message( resolve("MsgPickRequestedAmount") );
			return getNavigationKey(PickingMobileNavigation.PICK_PICK);
		}
		
		return getNavigationKey(PickingMobileNavigation.PICK_PICK_REMAINING);
	}
	
	// ***********************************************************************
	// PickRemaining.jsp
	// ***********************************************************************
	public String processPickRemaining(){
		String logStr = "processPickRemaining ";
		String code = inputCode == null ? "" : inputCode.trim();
		inputCode = "";
		
		log.debug(logStr+getTrace()+", input="+code);

		try {
			// 23.08.2013, krane, Check correct pick. With browser back key, it is possible to select the wrong pick
			if( currentId == null || (data.getCurrentPick()!=null && !currentId.equals(data.getCurrentPick().id)) ) {
				log.warn(logStr+"Got wrong pick-id. Abort");
				JSFHelper.getInstance().message( resolve("MsgWrongPick") );
				data.setCurrentPick();
				return getNavigationKey(PickingMobileNavigation.PICK_PICKFROM);
			}
		} catch (FacadeException e) {
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
			return getNavigationKey(PickingMobileNavigation.PICK_PICK);
		}
		
		if( code.length() == 0 ) {
			JSFHelper.getInstance().message( resolve("MsgEnterSomething") );
			return "";
		}
		BigDecimal amountRemain = null;
		try {
			amountRemain = new BigDecimal(code);
		}
		catch( Throwable t ) {
			JSFHelper.getInstance().message( "Please enter valid amount" );
			return "";
		}
		if( BigDecimal.ZERO.compareTo(amountRemain)>0 ) {
			JSFHelper.getInstance().message( "Please enter valid amount" );
			return "";
		}

		this.amountRemaining = amountRemain;
		locationCounted = true;
		try {
			return confirmSelectSerial();
		} catch (FacadeException e) {
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
			return "";
		}
	}
	
	public String processPickRemainingCancel(){
		String logStr = "processPickRemainingCancel ";
		log.debug(logStr+getTrace());
		inputCode = "";
		amountTaken = null;
		amountRemaining = null;
		locationCounted = false;
		return getNavigationKey(PickingMobileNavigation.PICK_PICK);
	}
	
	// ***********************************************************************
	// PickMissing.jsp
	// ***********************************************************************
	public String processPickMissing(){
		String logStr = "processPickMissing ";
		String code = inputCode == null ? "" : inputCode.trim();
		inputCode = "";
		
		log.debug(logStr+getTrace()+", input="+code);

		try {
			// 23.08.2013, krane, Check correct pick. With browser back key, it is possible to select the wrong pick
			if( currentId == null || (data.getCurrentPick()!=null && !currentId.equals(data.getCurrentPick().id)) ) {
				log.warn(logStr+"Got wrong pick-id. Abort");
				JSFHelper.getInstance().message( resolve("MsgWrongPick") );
				data.setCurrentPick();
				return getNavigationKey(PickingMobileNavigation.PICK_PICKFROM);
			}
		} catch (FacadeException e) {
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
			return getNavigationKey(PickingMobileNavigation.PICK_PICK);
		}
		
		BigDecimal amount = null;
		if( code.length() == 0 ) {
			JSFHelper.getInstance().message( resolve("MsgEnterSomething") );
			return "";
		}
		try {
			amount = new BigDecimal(code);
		}
		catch( Throwable t ) {
			JSFHelper.getInstance().message( resolve("MsgEnterValidAmount") );
			return "";
		}
		if( BigDecimal.ZERO.compareTo(amount)>0 ) {
			JSFHelper.getInstance().message( resolve("MsgEnterValidAmount") );
			return "";
		}
		
		// Entered amount must be < requested amount
		if( data.getAmount().compareTo(amount)<=0 ) {
			JSFHelper.getInstance().message( resolve("MsgAmountLessRequested") );
			return "";
		}
		
		amountTaken = amount;
		amountRemaining = BigDecimal.ZERO;
		locationCounted = false;
		
		try {
			return confirmSelectSerial();
		} catch (FacadeException e) {
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
			return getNavigationKey(PickingMobileNavigation.PICK_PICK);
		}
	}
	
	public String processPickMissingCancel(){
		String logStr = "processPickMissingCancel ";
		log.debug(logStr+getTrace());

		amountTaken = null;
		amountRemaining = null;
		locationCounted = false;
		return getNavigationKey(PickingMobileNavigation.PICK_PICK);
	}

	// ***********************************************************************
	// PickCompleteTarget.jsp
	// ***********************************************************************
	public String processCompleteTarget(){
		String logStr = "processCompleteTarget ";
		String code = inputCode == null ? "" : inputCode.trim();
		inputCode = "";

		log.debug(logStr+getTrace()+", input="+code);

		if( code.length() == 0 ) {
			JSFHelper.getInstance().message( resolve("MsgEnterSomething") );
			return "";
		}

		try {
			data.confirmCompletePick(code);
			if( data.setNextPick() ) {
				return getNavigationKey(PickingMobileNavigation.PICK_PICKFROM);
			}
			return getNavigationKey(PickingMobileNavigation.PICK_PICK_DONE);
		} catch (FacadeException e) {
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
			return "";
		}

	}

	public String processCompleteCancel(){
		String logStr = "processCompleteCancel ";
		log.debug(logStr+getTrace());
		inputCode = "";
		amountTaken = null;
		amountRemaining = null;
		locationCounted = false;
		return getNavigationKey(PickingMobileNavigation.PICK_PICK);
	}
	
	// ***********************************************************************
	// LocationEmpty.jsp
	// ***********************************************************************
	public String processLocationEmptyYes(){
		String logStr = "processLocationEmptyYes ";
		log.debug(logStr+getTrace());
		try {
			amountRemaining = BigDecimal.ZERO;
			locationCounted = true;
			return confirmSelectSerial();
		} catch (FacadeException e) {
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
			return "";
		}
	}
	
	public String processLocationEmptyNo(){
		String logStr = "processLocationEmptyNo ";
		log.debug(logStr+getTrace());
		return getNavigationKey(PickingMobileNavigation.PICK_PICK_REMAINING);
	}
	
	// ***********************************************************************
	// PickToCompleted.jsp
	// ***********************************************************************
	public String processPickToCompletedYes(){
		String logStr = "processPickToCompletedYes ";
		log.debug(logStr+getTrace());
		return getNavigationKey(PickingMobileNavigation.PICK_PICK_DONE);
	}
	
	public String processPickToCompletedNo(){
		String logStr = "processPickToCompletedNo ";
		log.debug(logStr+getTrace());
		return getNavigationKey(PickingMobileNavigation.PICK_PICKFROM);
	}

	
	// ***********************************************************************
	// PickToLabel.jsp
	// ***********************************************************************
	public String processPickToLabelPrint(){
		String logStr = "processPickToLabelPrint ";
		String code = inputCode == null ? "" : inputCode.trim();
		inputCode = "";
		
		log.debug(logStr+getTrace()+", input="+code);

		if( code.length() > 0 ) {
			try {
				data.changePickToLabel(code);
			} catch (FacadeException e) {
				JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
				return "";
			}
		}
		return getNavigationKey(PickingMobileNavigation.PICK_PICKTO_PRINT);
	}
	public String processPickToLabelGenerate(){
		String logStr = "processPickToLabelGenerate ";
		log.debug(logStr+getTrace());

		inputCode = "";
		try {
			data.changePickToLabel(null);
		} catch (FacadeException e) {
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
			return "";
		}
		return getNavigationKey(PickingMobileNavigation.PICK_PICKTO_PRINT);
	}
	
	// ***********************************************************************
	// PickToTarget.jsp
	// ***********************************************************************
	public String processPickToTarget(){
		String logStr = "processPickToTarget ";
		String code = inputCode == null ? "" : inputCode.trim();
		inputCode = "";
		
		log.debug(logStr+getTrace()+", input="+code);

		if( code.length() == 0 ) {
			JSFHelper.getInstance().message( resolve("MsgEnterSomething") );
			return "";
		}

		try {
			data.transferPickTo(code, State.PICKED);
			data.finishProcess();
		} catch (FacadeException e) {
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
			return "";
		}

		return getNavigationKey(PickingMobileNavigation.PICK_ORDER_DONE);
	}
	
	public String processPickToTargetLabel(){
		return getNavigationKey(PickingMobileNavigation.PICK_PICKTO_LABEL_POSTPICK);
	}
	
	// ***********************************************************************
	// PickDone.jsp
	// ***********************************************************************
	public String processPickDone(){
		String logStr = "processPickDone ";
		log.debug(logStr+getTrace());

		try {
			data.startPutAway();
			if( data.getCurrentPickTo() != null ) {
				return getNavigationKey(PickingMobileNavigation.PICK_PICKTO_TARGET);
			}
			data.finishProcess();
		} catch (FacadeException e) {
			JSFHelper.getInstance().message( e.getLocalizedMessage(getUIViewRoot().getLocale()) );
			return "";
		}
		
		return getNavigationKey(PickingMobileNavigation.PICK_ORDER_DONE);
	}

	// ***********************************************************************
	// OrderDone.jsp
	// ***********************************************************************
	public String processOrderDone(){
		String logStr = "processOrderDone ";
		log.debug(logStr+getTrace());
		init();
		return getNavigationKey(PickingMobileNavigation.PICK_ORDER_SELECT);
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
	public String getInputPrinter() {
		return inputPrinter;
	}
	public void setInputPrinter(String inputCode) {
		this.inputPrinter = inputCode;
	}
	
	// ***********************************************************************
	public boolean isShowClient() {
		return showClient;
	}

	// ***********************************************************************
	public boolean isShowOrderInfo() {
		return true;
	}

	// ***********************************************************************
	public String getClientNumber() {
		return data.getCurrentOrder().clientNumber;
	}
	public String getCustomerOrderNumber() {
		return data.getCurrentOrder().customerOrderNumber;
	}
	public String getCustomerOrderExternalNumber() {
		return data.getCurrentOrder().customerOrderExternalNumber;
	}
	public String getOrderInfo() {
		String ret = data.getCurrentOrder().customerOrderNumber;
		if( ret == null ) {
			ret = "";
		}
		if( showClient ) {
			ret = getClientNumber() + " / " + ret;
		}
		return ret;
	}
	public String getOrderStrategy() {
		return data.getCurrentOrder().strategy;
	}
	public String getOrderCreated() {
		return data.getCurrentOrder().created;
	}
	public String getCustomerNumber() {
		return data.getCurrentOrder().customerNumber;
	}
	public String getCustomerName() {
		return data.getCurrentOrder().customerName;
	}
	
	public String getNumPos() {
		return ""+data.getCurrentOrder().numPos;
	}
	public String getPickFromName() {
		return data.getCurrentPick() == null ? "" : data.getCurrentPick().locationName;
	}	
	public String getPickFromLabel() {
		return data.getCurrentPick() == null ? "" : data.getCurrentPick().unitLoadLabel;
	}	
	public String getPickAmount() {
		return data.getCurrentPick() == null ? "" : data.getCurrentPick().amount.toString();
	}	
	public String getPickUnit() {
		return data.getCurrentPick() == null ? "" : data.getCurrentPick().unitName;
	}	
	public String getPickItemName() {
		return data.getCurrentPick() == null ? "" : data.getCurrentPick().itemName;
	}	
	public String getPickItemNumber() {
		return data.getCurrentPick() == null ? "" : data.getCurrentPick().itemNo;
	}
	public String getPickToLabel() {
		return data.getCurrentPickTo() == null ? "" : data.getCurrentPickTo().label;
	}
	public String getPickToTargetName() {
		return data.getCurrentOrder().targetName;
	}
	
	public String getSelCustomerOrderNumber() {
		return selCustomerOrderNumber;
	}
	public String getSelCustomerOrderExternalNumber() {
		return selCustomerOrderExternalNumber;
	}
	public String getSelOrderClient() {
		return selClient;
	}
	public String getSelOrderCreated() {
		return selDate;
	}
	public String getSelOrderNumPos() {
		return selNumPos;
	}
	public String getSelStrategy() {
		return selStrategy;
	}
	
	public String getOrderSummary() {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		if( data.getCurrentOrder()==null ) {
			sb.append("No Picking Order");
		}
		else {
			sb.append("Picking Order: "+data.getCurrentOrder().pickingOrderNumber+"<br/>");
			int numPick = data.getCurrentOrder().numPos;
			sb.append(numPick==1?"1 Pick":(""+numPick+" Picks"));
		}
		sb.append("</html>");
		
		return sb.toString();
	}
	
	public String getSerialInfo() {
		return "" + (serialList == null ? 1 : (serialList.size()+1)) + " / " + getNumSerial();
	}

	public boolean isShowCancel() {
		if( data.getCurrentPickTo() == null ) {
			return true;
		}
		if( data.getCurrentPickTo().state < State.STARTED ) {
			return true;
		}
		return false;
	}

	public boolean isCompletePick() {
		return data.isCompletePick();
	}


	
	protected int getNumSerial() {
		if( data.getCurrentPick() == null ) {
			return 0;
		}
		if( !data.getCurrentPick().serialRequired ) {
			return 0;
		}
		if( amountTaken != null ) {
			return amountTaken.intValue();
		}
		return data.getCurrentPick().amount.intValue();
	}
	
	public Long getCurrentId() {
		PickingMobilePos pick = data.getCurrentPick();
		currentId = (pick == null ? null : pick.id); 
		return currentId;
	}
	public void setCurrentId(Long currentId) {
		this.currentId = currentId;
	}

	public boolean isIdentifyPacket() {
		return data.isIdentifyPacket() && data.hasPartialPicks();
	}

	// ***********************************************************************
	@Override
	protected ResourceBundle getResourceBundle() {
		ResourceBundle bundle;
		Locale loc;
		loc = getUIViewRoot().getLocale();
		bundle = ResourceBundle.getBundle("de.linogistix.mobile.processes.picking.PickingMobileBundle", loc);
		return bundle;
	}

	private String getTrace() {
		String value = "PickingOrder=";
		
		PickingMobileOrder order = data.getCurrentOrder();
		if( order != null ) {
			value += order.pickingOrderNumber;
		}
		
		PickingMobilePos pick = data.getCurrentPick();
		if( pick!=null ) {
			value += ", PickId="+pick.id;
		}
		
		return value;
	}

	protected String getNavigationKey(PickingMobileNavigation key) {
		log.debug("Navigate: "+key+", "+getTrace());
		return key.name();
	}
}
